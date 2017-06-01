package im.dlg.storage.slick

import com.github.tminglei.slickpg.ExPostgresProfile.ByteaPlainImplicits
import com.github.tminglei.slickpg._
import im.dlg.storage.api._
import im.dlg.storage.Connector
import im.dlg.storage.api.Action
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

trait PgProfile extends ExPostgresProfile with PgArraySupport {
  trait API extends super.API with ArrayImplicits
  override val api = new API with ByteaPlainImplicits with SimpleArrayPlainImplicits
}
object PgProfile extends PgProfile

class SlickConnector(db: PgProfile.api.Database)(implicit ec: ExecutionContext) extends Connector {
  import PgProfile.api._
  private val log = LoggerFactory.getLogger(this.getClass)

  override def run[R](action: Action[R]): Future[R] = for {
    result ← (action match {
      case GetAction(name, key)           ⇒ get(name, key)
      case GetByPrefixAction(name, key)   ⇒ getByPrefix(name, key)
      case UpsertAction(name, key, value) ⇒ upsert(name, key, value)
      case DeleteAction(name, key)        ⇒ delete(name, key)
      case GetKeysAction(name)            ⇒ getKeys(name)
      case GetKeysForValue(name, value)   ⇒ getKeysForValue(name, value)
    }).asInstanceOf[Future[R]]
  } yield result

  override def createTableIfNotExists(name: String, createReverseIndex: Boolean = false): Unit = {
    val tName = tableName(name)
    Await.result(
      db.run(for {
        _ ← sqlu"CREATE TABLE IF NOT EXISTS #$tName (key TEXT, value BYTEA, PRIMARY KEY (key))"
        _ ← if (createReverseIndex)
          sqlu"CREATE INDEX IF NOT EXISTS #${tName}_reverse_index ON #$tName (value)"
        else DBIO.successful(())
      } yield ()),
      10.seconds
    )
  }

  private def tableName(name: String) = s"kv_$name"

  private def get(name: String, key: String): Future[Option[Array[Byte]]] =
    db.run(sql"""SELECT value FROM #${tableName(name)} WHERE key = $key""".as[Array[Byte]].headOption)

  private def getByPrefix(name: String, keyPrefix: String): Future[Vector[(String, Array[Byte])]] =
    db.run(sql"""SELECT key, value FROM #${tableName(name)} WHERE key like '#$keyPrefix%'""".as[(String, Array[Byte])])

  private def upsert(name: String, key: String, value: Array[Byte]): Future[Int] = {
    val tName = tableName(name)
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

  private def delete(name: String, key: String) =
    db.run(sqlu"""DELETE FROM #${tableName(name)} WHERE key = $key""")

  private def getKeys(name: String) =
    db.run(sql"""SELECT key FROM #${tableName(name)}""".as[String])

  private def getKeysForValue(name: String, value: Array[Byte]) =
    db.run(sql"""SELECT key FROM #${tableName(name)} WHERE value = $value""".as[String])
}
