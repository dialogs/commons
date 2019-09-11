import sbt._
import Keys._

object V {
  val Akka = "2.5.25"
  val Scalatest = "3.0.8"
  val Slick = "3.3.2"
  val SlickPg = "0.18.0"
  val Cats = "2.0.0"
}

object Dependencies {
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % V.Akka % "provided"

  val scalatest = "org.scalatest" %% "scalatest" % V.Scalatest

  val slick = "com.typesafe.slick" %% "slick" % V.Slick % "provided"

  val slickPg = "com.github.tminglei" %% "slick-pg" % V.SlickPg % "provided"

  val cats = "org.typelevel" %% "cats-core" % V.Cats % "provided"
}
