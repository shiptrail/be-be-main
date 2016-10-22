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
  "io.github.karols" %% "units" % "0.2.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.8" % Test,
  "com.scalawilliam" %% "xs4s" % "0.2",
  "org.mockito" % "mockito-core" % "1.10.19" % Test,
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  "com.lambdaworks" %% "jacks" % "2.3.3"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

coverageExcludedFiles := target.value.absolutePath + ".*"
coverageMinimum := 80.0
coverageFailOnMinimum := true
