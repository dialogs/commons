package im.dlg.concurrent

import akka.actor.ActorSystem

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._
import scala.math.pow
import scala.util.Random.nextInt
import im.dlg.util.Retries._

import scala.util.Failure

object FutureRetries {
  private def expBackoffInternal(n: Int, dev: Int) =
    (pow(2, n.toDouble) + (1 + nextInt(dev))).milliseconds

  def expBackoff(maxBackoff: FiniteDuration, dev: Int): Delay = {
    require(maxBackoff.length > 0, "Maximal backoff should be positive")
    require(dev > 0, "Deviation should be positive")
    n ⇒ Array(maxBackoff, expBackoffInternal(n, dev)).min
  }

  def withRetries[A](maxAttempts: Int, delay: Delay = immediateRetryDelay, decider: Decider = alwaysRetryDecider)(f: ⇒ A)(implicit system: ActorSystem, ec: ExecutionContext): Future[A] = {
    require(maxAttempts >= 0, "Maximum attemps count should be non-negative")
    val deciderLifted = decider.lift
    val p = Promise[A]()

    def inner(n: Int): Unit = Future(f) onComplete {
      case Failure(err) if n < maxAttempts && deciderLifted(err).exists(_) ⇒
        val nextN = n + 1
        system.scheduler.scheduleOnce(delay(nextN)) {
          inner(nextN)
        }
      case x ⇒ p complete x
    }

    inner(0)

    p.future
  }
}