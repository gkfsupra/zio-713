package zio

import org.openjdk.jmh.annotations._
import java.util.concurrent.TimeUnit
import zio.internal.FiberMailbox

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class FiberMailboxBenchmark {

  private val mailbox = new FiberMailbox()
  private val msg = new Object()

  @Benchmark
  def supra713Benchmark(): Unit = {
    mailbox.offer(msg)
    mailbox.poll()
  }
}
