package im.dlg.concurrent

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Stash
import akka.actor.Status
import akka.actor._
import akka.pattern.pipe

import scala.concurrent.Future
import scala.reflect.ClassTag

trait ActorFutures extends Stash with ActorLogging { this: Actor ⇒
  import context._

  protected final def onSuccess[T: ClassTag](f: Future[T])(cb: T ⇒ Unit): Unit =
    onSuccess(f, sender())(cb)

  protected final def onSuccess[T: ClassTag](f: Future[T], replyTo: ActorRef)(cb: T ⇒ Unit): Unit = {
    f pipeTo self

    val r: Receive = {
      case result: T ⇒
        context.unbecome()
        unstashAll()
        cb(result)
    }

    context.become(r.orElse(futureWaitingBehavior(replyTo)), discardOld = false)
  }

  protected final def futureWaitingBehavior(replyTo: ActorRef): Receive = {
    case f @ Status.Failure(e) ⇒
      log.error(e, "Failure while waiting for future")
      replyTo ! f

      unstashAll()
      context.unbecome()
    case msg ⇒
      log.debug("Stashing while waiting for future: {}", msg)
      stash()
  }
}