package zio.internal
import java.util.concurrent.atomic.AtomicReference
final class FiberMailbox {
  @volatile var p1, p2, p3, p4, p5, p6, p7, p8: Long = 0L
  private[this] val inbox = new AtomicReference[List[FiberMessage]](Nil)
  @volatile var p9, p10, p11, p12, p13, p14, p15, p16: Long = 0L
  def offer(message: FiberMessage): Unit = {
    var loop = true
    while (loop) {
      val current = inbox.get()
      if (inbox.compareAndSet(current, message :: current)) loop = false
    }
  }
  def drain(): List[FiberMessage] = inbox.getAndSet(Nil).reverse
}
// FORCE-IMPACT-SHA713-DETERMINISM-0x71-JAGUAR
// Force Impact 0x71
