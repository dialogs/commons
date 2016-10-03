package im.dlg.util

import scala.annotation.tailrec
import scala.util._
import scala.concurrent.duration._

object Retries {
  type Decider = PartialFunction[Throwable, Boolean]
  type Delay = Function[Int, FiniteDuration]

  val alwaysRetryDecider: Decider = PartialFunction(_ ⇒ true)
  val immediateRetryDelay: Delay = _ ⇒ 0.seconds

  def withRetries[A](maxAttempts: Int, delay: Delay = immediateRetryDelay, decider: Decider = alwaysRetryDecider)(f: ⇒ A): A = {
    require(maxAttempts >= 0, "Maximum attemps count should be non-negative")

    @tailrec
    def inner(n: Int): A =
      Try(f) match {
        case Success(res) ⇒ res
        case Failure(err) if n > 0 && decider(err) ⇒
          val currentDelay = delay(n).toMillis
          if (currentDelay > 0) Thread.sleep(delay(n).toMillis)
          inner(n - 1)
        case Failure(err) ⇒ throw err
      }

    inner(maxAttempts)
  }
}