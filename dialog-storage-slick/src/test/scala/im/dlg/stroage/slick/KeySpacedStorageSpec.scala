package im.dlg.stroage.slick

import java.nio.ByteBuffer

import cats.std.future._
import com.typesafe.config.ConfigFactory
import im.dlg.storage.Interpreter
import im.dlg.storage.slick.{Codec, KeySpacedStorage}
import org.scalatest.FlatSpec
import slick.driver.JdbcProfile
import slick.jdbc.{GetResult, PositionedParameters, PositionedResult, SetParameter}

import scala.concurrent.Future

class KeySpacedStorageSpec extends FlatSpec {
  import slick.driver.H2Driver.api._

  val db = Database.forConfig(path = "h2mem", ConfigFactory.parseString(
    """
      |h2mem {
      |url="jdbc:h2:mem:test1"
      |driver=org.h2.Driver
      |connectionPool=disabled
      |}
    """.stripMargin
  ))

  import scala.concurrent.ExecutionContext.Implicits.global

  "KeySpacedStorageSpec" should "encode get int values from storage" in {
    import im.dlg.storage.dsl._

    implicit val codec: Codec[Int] = new Codec[Int] {
      def encode(x: Int) = { val b = ByteBuffer.allocate(4); b.putInt(x); b.array }
      def decode(x: Array[Byte]) = ByteBuffer.wrap(x).getInt
    }

    implicit val sp: SetParameter[Array[Byte]] = new SetParameter[Array[Byte]] {
      override def apply(bs: Array[Byte], pp: PositionedParameters): Unit = pp.setBytes(bs)
    }

    implicit val gr: GetResult[Array[Byte]] = new GetResult[Array[Byte]] {
      override def apply(pr: PositionedResult): Array[Byte] = pr.nextBytes
    }

    implicit val interpreter: Interpreter[Future] = KeySpacedStorage(db, "test")

    val program = for {
      _ ← upsert("a", 1)
      r ← get[Int]("a")
    } yield r

    println(program)
    assert(false)
  }
}
