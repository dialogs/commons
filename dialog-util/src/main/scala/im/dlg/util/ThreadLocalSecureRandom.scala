package im.dlg.util

import java.security.SecureRandom

object ThreadLocalSecureRandom {
  private val localRandom: ThreadLocal[ThreadLocalSecureRandom] = new ThreadLocal[ThreadLocalSecureRandom]() {
    override protected def initialValue(): ThreadLocalSecureRandom = {
      new ThreadLocalSecureRandom()
    }
  }

  def current(): ThreadLocalSecureRandom = localRandom.get()
}

final class ThreadLocalSecureRandom extends SecureRandom
