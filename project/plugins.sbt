resolvers += Resolver.url(
  "bintray-dialog-sbt-plugins",
  url("http://dl.bintray.com/dialog/sbt-plugins")
)(Resolver.ivyStylePatterns)

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")

addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.4")