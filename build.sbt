import sbt.Keys._

name := "dialog-commons"

scalaVersion := "2.12.10"

crossScalaVersions := List("2.11.11", "2.12.10", "2.13.1")

lazy val defaultSettings = Seq(
  licenses := Seq("Apache-2.0" -> url("https://github.com/dialogs/api-schema/blob/master/LICENSE")),
  resolvers += Resolver.sonatypeRepo("public"),
  publishMavenStyle := true
)

lazy val dialogUtil = project in file("dialog-util")
lazy val dialogConcurrent = project in file("dialog-concurrent") dependsOn dialogUtil
lazy val dialogCatsSlick = project in file("dialog-cats-slick")
lazy val dialogStorage = project in file("dialog-storage")
lazy val dialogStorageSlick = project in file("dialog-storage-slick") dependsOn dialogStorage

publishMavenStyle := true

bintrayOrganization := Some("dialog")

enablePlugins(GitVersioning)

// enablePlugins(Publishing)
