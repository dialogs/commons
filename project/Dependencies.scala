import sbt._
import Keys._

object V {
  val Akka = "2.4.4"
  val Scalatest = "2.2.4"
  val Slick = "3.1.1"
  val SlickPg = "0.13.0"
  val Cats = "0.7.2"
}

object Dependencies {
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % V.Akka % "provided"

  val scalatest = "org.scalatest" %% "scalatest" % V.Scalatest

  val slick = "com.typesafe.slick" %% "slick" % V.Slick % "provided"

  val slickPg = "com.github.tminglei" %% "slick-pg" % V.SlickPg % "provided"

  val cats = "org.typelevel" %% "cats" % V.Cats % "provided"
}
