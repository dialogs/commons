import sbt._
import Keys._

object V {
  val Akka = "2.5.0"
  val Scalatest = "2.2.4"
  val Slick = "3.2.0"
  val SlickPg = "0.15.0-RC"
  val Cats = "0.9.0"
}

object Dependencies {
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % V.Akka % "provided"

  val scalatest = "org.scalatest" %% "scalatest" % V.Scalatest

  val slick = "com.typesafe.slick" %% "slick" % V.Slick % "provided"

  val slickPg = "com.github.tminglei" %% "slick-pg" % V.SlickPg % "provided"

  val cats = "org.typelevel" %% "cats" % V.Cats % "provided"
}
