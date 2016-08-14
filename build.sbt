
name := """backend-server"""

version := "1.0-SNAPSHOT"

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
