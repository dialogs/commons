import sbt.Keys._

name := "dialog-commons"

scalaVersion := "2.12.10"

crossScalaVersions := List("2.11.11", "2.12.10", "2.13.1")

lazy val defaultSettings = Seq(
  licenses := Seq("Apache-2.0" -> url("https://github.com/dialogs/api-schema/blob/master/LICENSE")),
  resolvers += Resolver.sonatypeRepo("public"),
  publishMavenStyle := true,
  libraryDependencies ++= Dependencies.root,
  bintrayOrganization := Some("dialog")
)

lazy val dialogUtil = (project in file("dialog-util")).settings(defaultSettings ++ Seq(name := "dialog-util")) 
lazy val dialogConcurrent = (project in file("dialog-concurrent") dependsOn dialogUtil).settings(defaultSettings ++ Seq(name := "dialog-concurrent")) 
lazy val dialogCatsSlick = (project in file("dialog-cats-slick")).settings(defaultSettings ++ Seq(name := "dialog-cats-slick")) 
lazy val dialogStorage = (project in file("dialog-storage")).settings(defaultSettings ++ Seq(name := "dialog-storage")) 
lazy val dialogStorageSlick = (project in file("dialog-storage-slick") dependsOn dialogStorage).settings(defaultSettings ++ Seq(name := "dialog-storage-slick")) 

lazy val root = project.in(file("."))
  .settings(
    name := "commons",
    skip in publish := true
  ).aggregate(
  	dialogUtil, 
  	dialogConcurrent, 
  	dialogCatsSlick, 
  	dialogStorage, 
  	dialogStorageSlick
  )

bintrayOrganization := Some("dialog")

enablePlugins(GitVersioning)

// enablePlugins(Publishing)
