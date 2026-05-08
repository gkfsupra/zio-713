/*
 * Copyright 2017-2024 John A. De Goes and the ZIO Contributors
 */

package zio

import zio.internal.Sync
import scala.annotation.tailrec
import java.util.ArrayDeque

sealed abstract class Semaphore extends Serializable {
  def available(implicit trace: Trace): UIO[Long]
  def acquire(implicit trace: Trace): UIO[Unit] = acquireN(1)
  def acquireN(n: Long)(implicit trace: Trace): UIO[Unit]
  def release(implicit trace: Trace): UIO[Unit] = releaseN(1)
  def releaseN(n: Long)(implicit trace: Trace): UIO[Unit]
  def withPermit[R, E, A](zio: ZIO[R, E, A])(implicit trace: Trace): ZIO[R, E, A] = withPermits(1)(zio)
  def withPermits[R, E, A](n: Long)(zio: ZIO[R, E, A])(implicit trace: Trace): ZIO[R, E, A]
}

object Semaphore {
  def make(permits: Long)(implicit trace: Trace): UIO[Semaphore] =
    ZIO.succeed(unsafe.make(permits)(Unsafe.unsafe))

  object unsafe {
    def make(permits: Long)(implicit unsafe: Unsafe): Semaphore =
      new TSemaphore(permits)
  }

  private final class TSemaphore(private var permits: Long) extends Semaphore {
    private val waiters = new ArrayDeque[(Long, Promise[Nothing, Unit])]()

    def available(implicit trace: Trace): UIO[Long] =
      ZIO.succeed(Sync(waiters)(permits))

    def acquireN(n: Long)(implicit trace: Trace): UIO[Unit] =
      ZIO.fiberIdWith { fiberId =>
        ZIO.suspendSucceed {
          var promise: Promise[Nothing, Unit] = null
          Sync(waiters) {
            if (waiters.isEmpty && permits >= n) {
              permits -= n
            } else {
              promise = Promise.unsafe.make[Nothing, Unit](fiberId)(Unsafe.unsafe)
              waiters.addLast((n, promise))
            }
          }
          if (promise eq null) ZIO.unit else promise.await
        }
      }

    def releaseN(n: Long)(implicit trace: Trace): UIO[Unit] =
      ZIO.succeed {
        Sync(waiters) {
          permits += n
          @tailrec
          def wakeup(): Unit =
            if (!waiters.isEmpty) {
              val next = waiters.peekFirst()
              val needed = next._1
              val promise = next._2
              if (permits >= needed) {
                permits -= needed
                waiters.removeFirst()
                promise.unsafe.done(Exit.unit)(Unsafe.unsafe)
                wakeup()
              }
            }
          wakeup()
        }
      }

    def withPermits[R, E, A](n: Long)(zio: ZIO[R, E, A])(implicit trace: Trace): ZIO[R, E, A] =
      ZIO.uninterruptibleMask { restore =>
        acquireN(n) *> restore(zio).ensuring(releaseN(n))
      }
  }
}
