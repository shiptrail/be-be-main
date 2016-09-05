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
  val apacheConfig-http = baseDirectory.value / "dist" / "apache" / "swpdv-80"
  val apacheConfig-https = baseDirectory.value / "dist" / "apache" / "swpdv-443"
  packageMapping((apacheConfig-http, "/etc/apache/sites-available/swpdv-80"))
  packageMapping((apacheConfig-https, "/etc/apache/sites-available/swpdv-443"))
}

val linuxPostInstallScript =
  s"""
     |echo "Configure Apache"
     |a2enmod rewrite ssl proxy proxy_http
     |a2dissite 000-default default-ssl
     |a2ensite swpdv-80 swpdv-443
     |
     |echo "Enable and (re-)start nginx"
     |apachectl -t
     |apachectl -S
     |
     |update-rc.d apache2 start
     |service apache2 restart
     """.stripMargin


// ====== Debian specific ======

// dpkg-deb in java
enablePlugins(JDebPackaging)
// dependencies for debian based distributions
debianPackageDependencies in Debian ++= Seq("openjdk-8-jre", "apache2", "apache2-utils", "ssl-cert")
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
rpmRequirements := List("java-1.8.0-openjdk", "httpd24")
// append to install / remove scriptlets for RPM packages
// see: http://www.scala-sbt.org/sbt-native-packager/formats/rpm.html#scriptlet-changes
import RpmConstants._
maintainerScripts in Rpm := maintainerScriptsAppend((maintainerScripts in Rpm).value)(
  Post -> linuxPostInstallScript
)
