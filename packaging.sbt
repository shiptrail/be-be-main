enablePlugins(JavaServerAppPackaging, SystemdPlugin)


// ====== General settings ======

packageSummary := "backend-server for SWP DV"

packageDescription := """Contains the backend for both gps logging clients and the frontend"""

// timeout before the service manager restarts this service (in case of debian systemd)
retryTimeout := 10

// java options for deployment
javaOptions in Universal ++= Seq(
  // JVM memory tuning
  "-J-Xmx1024m",
  "-J-Xms512m",

  // Use separate configuration file for production environment
  s"-Dconfig.resource=production.conf",

  // Since play uses separate pidfile we have to provide it with a proper path
  // name of the pid file must be play.pid
  // s"-Dpidfile.path=/var/run/${packageName.value}/play.pid",

  // alternative, you can remove the PID file
  s"-Dpidfile.path=/dev/null",

  // You may also want to include this setting if you use play evolutions
  "-DapplyEvolutions.default=true"
)


// ====== Linux specific settings ======

// Where to install package data
defaultLinuxInstallLocation := "/opt"

linuxPackageMappings += {
  val builtFrontend = baseDirectory.value / "fe-root" / "fe"
  packageDirectoryAndContentsMapping((builtFrontend, s"/opt/${packageName.value}/fe-root/fe"))
}

linuxPackageMappings += {
  val nginxConfig = baseDirectory.value / "dist" / "nginx" / "swpdv"
  packageMapping((nginxConfig, "/etc/nginx/sites-available/swpdv"))
}

val linuxPostInstallScript =
  s"""
     |echo "Configure Nginx"
     |ln -s /etc/nginx/sites-available/swpdv /etc/nginx/sites-enabled/swpdv || true
     |rm /etc/nginx/sites-enabled/default || true
     |
     |echo "Enable and (re-)start nginx"
     |nginx -t
     |systemctl restart nginx
     """.stripMargin


// ====== Debian specific ======

// dpkg-deb in java
enablePlugins(JDebPackaging)
// dependencies for debian based distributions
debianPackageDependencies in Debian ++= Seq("openjdk-8-jre", "nginx", "ssl-cert")
// append to install / remove scripts for RPM packages
// see: http://www.scala-sbt.org/sbt-native-packager/formats/debian.html?highlight=maintainerscript#customizing-debian-metadata
import DebianConstants._
maintainerScripts in Debian := maintainerScriptsAppend((maintainerScripts in Debian).value)(
  Postinst -> linuxPostInstallScript
)


// ====== RPM specific ======

// rpm specific fields
rpmVendor := "SWP DV Team"
rpmLicense := Some("Apache License 2.0")
rpmRequirements := List("java-1.8.0-openjdk", "nginx")
// append to install / remove scriptlets for RPM packages
// see: http://www.scala-sbt.org/sbt-native-packager/formats/rpm.html#scriptlet-changes
import RpmConstants._
maintainerScripts in Rpm := maintainerScriptsAppend((maintainerScripts in Rpm).value)(
  Post -> linuxPostInstallScript
)
