package im.dlg.concurrent

import akka.actor.{Actor, ActorLogging}

trait AlertingActor extends Actor with ActorLogging {

  override def preRestart(cause: Throwable, message: Option[Any]): Unit = {
    super.preRestart(cause, message)
    log.error(cause, "Failure while handling message: {}", message)
  }

}
