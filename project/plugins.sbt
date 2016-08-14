// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.4")

// deployment

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-M5")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.7.1")

// web plugins

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")

addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.2")

// Code quality tools

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "1.1.0")

addSbtPlugin("org.danielnixon" % "sbt-playwarts" % "0.27")

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "0.2.5")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5")

libraryDependencies += "org.vafer" % "jdeb" % "1.3" artifacts (Artifact("jdeb", "jar", "jar"))
