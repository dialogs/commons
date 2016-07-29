package im.dlg.concurrent

import scala.concurrent.{ExecutionContext, Future}

object FutureExt extends FutureExt

trait FutureExt {
  /**
   * Function to process sequence of futures in sequential order
   * http://stackoverflow.com/questions/28514621/is-there-a-build-in-slow-future-traverse-version
   */
  def ftraverse[A, B](xs: Seq[A])(f: A ⇒ Future[B])(implicit ec: ExecutionContext): Future[Seq[B]] = {
    if (xs.isEmpty) Future successful Seq.empty[B]
    else f(xs.head) flatMap { fh ⇒ ftraverse(xs.tail)(f) map (r ⇒ fh +: r) }
  }

  def ftraverse[A, B](xs: Seq[A], parallelism: Int)(f: A ⇒ Future[B])(implicit ec: ExecutionContext): Future[Seq[B]] = {
    val xss = xs.grouped(parallelism).toSeq
    ftraverse(xss) { xs ⇒
      Future.sequence(xs map f)
    } map (_.flatten)
  }
}