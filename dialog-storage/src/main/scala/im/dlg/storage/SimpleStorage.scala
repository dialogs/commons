package im.dlg.storage

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.FiniteDuration

trait Connector {
  def run[R](action: api.Action[R]): Future[R]

  def createTableIfNotExists(schema: Option[String] = None, name: String, createReverseIndex: Boolean): Unit

  final def runSync[R](action: api.Action[R])(implicit timeout: FiniteDuration): R =
    Await.result(run(action), timeout)
}

class SimpleStorage(schema: Option[String] = None, val name: String) {
  import api._

  final def get(key: String) = GetAction(schema, name, key)

  final def getByPrefix(keyPrefix: String) = GetByPrefixAction(schema, name, keyPrefix)

  final def upsert(key: String, value: Array[Byte]) = UpsertAction(schema, name, key, value)

  final def delete(key: String) = DeleteAction(schema, name, key)

  final def getKeys = GetKeysAction(schema, name)

  final def getKeysForValue(value: Array[Byte]) = GetKeysForValue(schema, name, value)
}

object api {
  sealed trait Action[R] {
    def name: String
  }

  final case class GetAction(schema: Option[String], name: String, key: String) extends Action[Option[Array[Byte]]]

  final case class GetByPrefixAction(schema: Option[String], name: String, keyPrefix: String) extends Action[Vector[(String, Array[Byte])]]

  final case class UpsertAction(schema: Option[String], name: String, key: String, value: Array[Byte]) extends Action[Int]

  final case class DeleteAction(schema: Option[String], name: String, key: String) extends Action[Int]

  final case class GetKeysAction(schema: Option[String], name: String) extends Action[Seq[String]]

  final case class GetKeysForValue(schema: Option[String], name: String, value: Array[Byte]) extends Action[Vector[String]]

}
