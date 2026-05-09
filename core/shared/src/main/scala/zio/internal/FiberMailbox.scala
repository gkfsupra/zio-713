package zio.internal

/**
 * [JAGUAR-256] :: Optimized High-Throughput Fiber Mailbox
 * Protocolo: SHA-713 | Nodo: Celaya_L1
 */
final class FiberMailbox {
  private[this] var p0, p1, p2, p3, p4, p5, p6, p7: Long = 0L
  @volatile private[this] var head: AnyRef = _
  private[this] var p8, p9, p10, p11, p12, p13, p14, p15: Long = 0L

  def offer(f: AnyRef): Unit = { head = f }
  def poll(): AnyRef = { val res = head; head = null; res }
}
