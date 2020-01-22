package im.dlg.storage.slick

import com.github.tminglei.slickpg._
import im.dlg.storage.api._
import im.dlg.storage.Connector
import im.dlg.storage.api.Action

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

private object Driver extends Driver

private trait Driver extends ExPostgresProfile with PgArraySupport {
  override val api = PgAPI

  object PgAPI extends API with ByteaPlainImplicits
}
import Driver.api._

class SlickConnector(db: Database)(implicit ec: ExecutionContext) extends Connector {
  override def run[R](action: Action[R]): Future[R] = for {
    result ← (action match {
      case GetAction(schema, name, key)           ⇒ get(schema, name, key)
      case GetByPrefixAction(schema, name, key)   ⇒ getByPrefix(schema, name, key)
      case UpsertAction(schema, name, key, value) ⇒ upsert(schema, name, key, value)
      case DeleteAction(schema, name, key)        ⇒ delete(schema, name, key)
      case GetKeysAction(schema, name)            ⇒ getKeys(schema, name)
      case GetKeysForValue(schema, name, value)   ⇒ getKeysForValue(schema, name, value)
    }).asInstanceOf[Future[R]]
  } yield result

  override def createTableIfNotExists(schema: Option[String], name: String, createReverseIndex: Boolean = false): Unit = {
    val tName = tableName(schema, name)
    Await.result(
      db.run(for {
        _ ← sqlu"CREATE TABLE IF NOT EXISTS #$tName (key TEXT, value BYTEA, PRIMARY KEY (key))"
        _ ← if (createReverseIndex)
          sqlu"CREATE INDEX IF NOT EXISTS #${name}_reverse_index ON #$tName (value)"
        else DBIO.successful(())
      } yield ()),
      10.seconds
    )
  }

  private def tableName(schema: Option[String], name: String) =
    schema.map(s => s"$s.kv_$name").getOrElse(s"kv_$name")

  private def get(schema: Option[String], name: String, key: String): Future[Option[Array[Byte]]] =
    db.run(sql"""SELECT value FROM #${tableName(schema, name)} WHERE key = $key""".as[Array[Byte]].headOption)

  private def getByPrefix(schema: Option[String], name: String, keyPrefix: String): Future[Vector[(String, Array[Byte])]] =
    db.run(sql"""SELECT key, value FROM #${tableName(schema, name)} WHERE key like '#$keyPrefix%'""".as[(String, Array[Byte])])

  private def upsert(schema: Option[String], name: String, key: String, value: Array[Byte]): Future[Int] = {
    val tName = tableName(schema, name)
    val action: DBIO[Int] = for {
      count ← sql"SELECT COUNT(*) FROM #$tName WHERE KEY = $key".as[Int]
      exists = count.headOption.exists(_ > 0)
      result ← if (exists)
        sqlu"UPDATE #$tName SET value = $value WHERE key = $key"
      else
        sqlu"INSERT INTO #$tName VALUES ($key, $value)"

    } yield result
    db.run(action.transactionally)
  }

  private def delete(schema: Option[String], name: String, key: String) =
    db.run(sqlu"""DELETE FROM #${tableName(schema, name)} WHERE key = $key""")

  private def getKeys(schema: Option[String], name: String) =
    db.run(sql"""SELECT key FROM #${tableName(schema, name)}""".as[String])

  private def getKeysForValue(schema: Option[String], name: String, value: Array[Byte]) =
    db.run(sql"""SELECT key FROM #${tableName(schema, name)} WHERE value = $value""".as[String])
}
