resolvers += Resolver.url("dialog-sbt-plugins", url("https://dl.bintray.com/dialog/sbt-plugins"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.0")

addSbtPlugin("im.dlg" % "sbt-dialog-houserules" % "0.1.36-SNAPSHOT")