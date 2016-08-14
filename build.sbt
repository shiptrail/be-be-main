name := """backend-server"""

version := "1.0-SNAPSHOT"

maintainer := "SWP DV Team <no@where.com>"

// Packaging start
enablePlugins(JavaServerAppPackaging, SystemdPlugin)

packageSummary := "backend-server for SWP DV"

packageDescription := """Contains the backend for both gps logging clients and the frontend"""

// dpkg-deb in java
enablePlugins(JDebPackaging)

// dependencies for debian based distributions
debianPackageDependencies in Debian ++= Seq("openjdk-8-jre")

// only required field specific to rpm
rpmVendor := "SWP DV"

// timeout before systemd restarts this service
retryTimeout := 10

// java options for deployment
javaOptions in Universal ++= Seq(
  // JVM memory tuning
  "-J-Xmx1024m",
  "-J-Xms512m",

  // Since play uses separate pidfile we have to provide it with a proper path
  // name of the pid file must be play.pid
  // s"-Dpidfile.path=/var/run/${packageName.value}/play.pid",

  // alternative, you can remove the PID file
  s"-Dpidfile.path=/dev/null",

  // You may also want to include this setting if you use play evolutions
  "-DapplyEvolutions.default=true"
)

// Packaging end

enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

coverageExcludedFiles := target.value.absolutePath + ".*"
coverageMinimum := 80.0
coverageFailOnMinimum := true

