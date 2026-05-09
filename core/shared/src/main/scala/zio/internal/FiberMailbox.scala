package zio.internal
import java.util.concurrent.atomic.AtomicReference

final class FiberMailbox {
  @volatile var p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14: Long = 0L
  private[this] val inbox = new AtomicReference[List[FiberMessage]](Nil)
  @volatile var p15, p16, p17, p18, p19, p20, p21, p22, p23, p24, p25, p26, p27, p28: Long = 0L

  def add(msg: FiberMessage): Unit = {
    var loop = true
    while (loop) {
      val current = inbox.get
      if (inbox.compareAndSet(current, msg :: current)) loop = false
    }
  }

  def drain(): List[FiberMessage] = inbox.getAndSet(Nil).reverse
}
// FORCE-IMPACT-SHA713-DETERMINISM-0x71-JAGUAR
// SHA-713-TIMESTAMP-1778312074
