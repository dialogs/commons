package im.dlg

import sbt._
import sbt.Keys._

object Publishing extends AutoPlugin {
  override def trigger = allRequirements

  override lazy val projectSettings = Seq(
    publishMavenStyle := true,
    organization := "im.dlg"
  ) ++ settings()

  private def settings(): Seq[Def.Setting[_]] = {
    sys.env.get("PUBLISH_TO").map(_.toLowerCase()).map {
      case "nexus" =>
        (sys.env.get("NEXUS_REPOSITORY_NAME"), sys.env.get("NEXUS_REPOSITORY_URL")) match {
          case (Some(repoName), Some(repoUrl)) =>
            Seq(
              Some(publishTo := Some(repoName at repoUrl)),
              (sys.env.get("NEXUS_USERNAME"), sys.env.get("NEXUS_PASSWORD")) match {
                case (Some(username), Some(password)) =>
                  Some(credentials += Credentials(repoName, new URL(repoUrl).getHost, username, password))
                case _ =>
                  Some(credentials ++= Seq(Credentials(Path.userHome / ".m2" / ".credentials"),
                    Credentials(Path.userHome / ".sbt" / ".credentials")))
              }
            ).flatten
          case _ => Seq.empty
        }

      case _ => Seq.empty

    }.getOrElse(Seq.empty)
  }
}
