name := """ApplicationPlay"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += evolutions

libraryDependencies += "com.h2database" % "h2" % "1.4.188"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % "test"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.0.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0"
libraryDependencies +=  "org.mockito" % "mockito-core" % "1.8.5" % "test"
libraryDependencies += "org.postgresql" % "postgresql" % "42.1.4"
libraryDependencies += "org.postgresql" % "postgresql" % "42.1.4"
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"
libraryDependencies += specs2 % Test
javaOptions in Test += "-Dconfig.file=conf/test.conf"
coverageExcludedPackages := "<empty>;Reverse.*;router\\.*"


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

javaOptions in Test += "-Dconfig.file=conf/test.conf"