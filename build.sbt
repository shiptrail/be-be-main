name := """backend-server"""

version := {
  val jenkinsBuild = for {
    minor <- sys.env.get("BUILD_NUMBER")
    changenum <- sys.env.get("GERRIT_CHANGE_NUMBER")
    patchset <- sys.env.get("GERRIT_PATCHSET_NUMBER")
  } yield s"1.$minor.$changenum.$patchset"

  val user = sys.env.getOrElse("USER", "nouser")

  jenkinsBuild.getOrElse(s"1.${git.gitHeadCommit.value.get}.$user")
}

maintainer := "SWP DV Team <dbslehre@inf.fu-berlin.de>"

enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.8" % Test,
  "org.mockito" % "mockito-core" % "1.10.19" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

coverageExcludedFiles := target.value.absolutePath + ".*"
coverageMinimum := 80.0
coverageFailOnMinimum := true
