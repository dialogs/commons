import sbt.Keys._

name := "dialog-commons"

scalaVersion := "2.12.10"

crossScalaVersions := List("2.11.11", "2.12.10", "2.13.1")

lazy val defaultSettings = Seq(
  licenses := Seq("Apache-2.0" -> url("https://github.com/dialogs/api-schema/blob/master/LICENSE")),
  resolvers += Resolver.sonatypeRepo("public"),
  publishMavenStyle := true
)

lazy val dialogUtil = (project in file("dialog-util")).settings(Seq(
      libraryDependencies ++= Dependencies.root
    )) 
lazy val dialogConcurrent = (project in file("dialog-concurrent") dependsOn dialogUtil).settings(Seq(
      libraryDependencies ++= Dependencies.root
    )) 
lazy val dialogCatsSlick = (project in file("dialog-cats-slick")).settings(Seq(
      libraryDependencies ++= Dependencies.root
    )) 
lazy val dialogStorage = (project in file("dialog-storage")).settings(Seq(
      libraryDependencies ++= Dependencies.root
    )) 
lazy val dialogStorageSlick = (project in file("dialog-storage-slick") dependsOn dialogStorage).settings(Seq(
      libraryDependencies ++= Dependencies.root
    )) 

lazy val root = project.in(file("."))
  .settings(
    name := "commons"
  ).aggregate(
  	dialogUtil, 
  	dialogConcurrent, 
  	dialogCatsSlick, 
  	dialogStorage, 
  	dialogStorageSlick
  )

publishMavenStyle := true

bintrayOrganization := Some("dialog")

enablePlugins(GitVersioning)

// enablePlugins(Publishing)
