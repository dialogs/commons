package im.dlg.concurrent

import akka.actor.{ActorLogging, Stash, ActorRef, Actor}

trait StashingActor extends Actor with Stash with ActorLogging {
  protected def becomeStashing(f: ActorRef ⇒ Receive, discardOld: Boolean = false): Unit =
    context.become(receiveStashing(f), discardOld = discardOld)

  protected def receiveStashing(f: ActorRef ⇒ Receive): Receive =
    f(sender()) orElse stashing

  private def stashing: Receive = {
    case msg ⇒
      log.debug("Stashing message: {}", msg)
      stash()
  }
}
