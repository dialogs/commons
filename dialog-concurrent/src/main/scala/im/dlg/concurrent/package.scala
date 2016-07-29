package im.dlg

import java.util.concurrent.TimeoutException

import akka.actor.ActorSystem
import akka.pattern.after

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

package object concurrent {
  implicit class ExtendedFuture[T](future: Future[T]) {
    def withTimeout(duration: FiniteDuration)(implicit system: ActorSystem, ec: ExecutionContext): Future[T] = {
      val timeoutFuture = after(duration, using = system.scheduler)(Future.failed(new TimeoutException("Future timed out")))
      Future.firstCompletedOf(future :: timeoutFuture :: Nil)
    }
  }
}