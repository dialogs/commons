import sbt.Keys._

name := "dialog-commons"

scalaVersion := "2.13.0"

crossScalaVersions := List("2.11.11", "2.12.8", "2.13.0")

lazy val defaultSettings = Seq(
  licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
  resolvers += Resolver.sonatypeRepo("public"),
  publishMavenStyle := true
)

lazy val dialogUtil = project in file("dialog-util")
lazy val dialogConcurrent = project in file("dialog-concurrent") dependsOn dialogUtil
lazy val dialogCatsSlick = project in file("dialog-cats-slick")
lazy val dialogStorage = project in file("dialog-storage")
lazy val dialogStorageSlick = project in file("dialog-storage-slick") dependsOn dialogStorage

enablePlugins(Publishing)
