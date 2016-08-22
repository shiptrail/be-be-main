enablePlugins(JavaServerAppPackaging, SystemdPlugin)

packageSummary := "backend-server for SWP DV"

packageDescription := """Contains the backend for both gps logging clients and the frontend"""

// Where to install package data
defaultLinuxInstallLocation := "/opt"

// dpkg-deb in java
enablePlugins(JDebPackaging)

// dependencies for debian based distributions
debianPackageDependencies in Debian ++= Seq("openjdk-8-jre", "nginx")

// rpm specific fields
rpmVendor := "SWP DV Team"
rpmLicense := Some("Apache License 2.0")

// timeout before the service manager restarts this service (in case of debian systemd)
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
  "-DapplyEvolutions.default=true",

  // Use separate configuration file for production environment
  s"-Dconfig.file=/opt/${packageName.value}/conf/production.conf"
)

linuxPackageMappings += {
  val builtFrontend = baseDirectory.value / "fe-root" / "fe"
  packageDirectoryAndContentsMapping((builtFrontend, s"/opt/${packageName.value}/fe-root/fe"))
}

linuxPackageMappings += {
  val nginxConfig = baseDirectory.value / "dist" / "nginx" / "swpdv"
  packageMapping((nginxConfig, "/etc/nginx/sites-available/swpdv"))
}
