name := "free-monads-example"

import de.heikoseeberger.sbtheader.license.Apache2_0
import de.heikoseeberger.sbtheader.CommentStyleMapping._

val commonSettings = Seq(
  organization := "com.lunaryorn",
  description := "A toy weather service to demonstrate DI with free monads",
  homepage := Some(
    url(s"https://github.com/lunaryorn/scala-free-monads-example")),
  startYear := Some(2016),
  licenses += "Apache-2.0" -> url(
    "http://www.apache.org/licenses/LICENSE-2.0"),
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq(
    // Code encoding
    "-encoding",
    "UTF-8",
    // Deprecation warnings
    "-deprecation",
    // Warnings about features that should be imported explicitly
    "-feature",
    // Enable additional warnings about assumptions in the generated code
    "-unchecked",
    // Recommended additional warnings
    "-Xlint",
    // Warn when argument list is modified to match receiver
    "-Ywarn-adapted-args",
    // Warn about dead code
    "-Ywarn-dead-code",
    // Warn about inaccessible types in signatures
    "-Ywarn-inaccessible",
    // Warn when non-nullary overrides a nullary (def foo() over def foo)
    "-Ywarn-nullary-override",
    // Warn when numerics are unintentionally widened
    "-Ywarn-numeric-widen",
    // Fail compilation on warnings
    "-Xfatal-warnings"
  ),
  // Automatically update headers
  headers := createFrom(Apache2_0, "2016", "Sebastian Wiesner")
)

val common = (project in file("common"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    name := "weather-common",
    libraryDependencies ++= Seq(
      // Temperature types
      "com.squants" %% "squants" % "0.6.2",
      // Test framework
      "org.scalatest" %% "scalatest" % "3.0.0" % "test",
      // Property testing
      "org.scalacheck" %% "scalacheck" % "1.13.2" % "test"
    ) ++ Dependencies.circe ++ Dependencies.finch ++ Dependencies.cats
  )

val cakeServer = (project in file("cake-server"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    name := "cake-weather-server",
    libraryDependencies ++= Seq(
      "org.mockito" % "mockito-core" % "1.10.19" % "test")
  )
  .dependsOn(common % "compile->compile;test->test")

val freeServer = (project in file("free-server"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(name := "free-weather-server")
  .dependsOn(common % "compile->compile;test->test")

val root = (project in file("."))
  .settings(commonSettings)
  .settings(publishArtifact := false)
  .aggregate(common, cakeServer, freeServer)

// Validate the project
val validateCommands = List(
  "clean",
  "scalafmtTest",
  "test:scalafmtTest",
  "compile",
  "test:compile",
  "test"
)
addCommandAlias("validate", validateCommands.mkString(";", ";", ""))
