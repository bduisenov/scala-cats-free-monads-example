import sbt._

object Dependencies {

  val finch: Seq[ModuleID] = Seq(
    "finch-core",
    "finch-circe"
  ).map("com.github.finagle" %% _ % "0.11.0-M3")

  val circe: Seq[ModuleID] = Seq(
    "circe-core",
    "circe-parser",
    "circe-generic"
  ).map("io.circe" %% _ % "0.5.0")

  val cats: Seq[ModuleID] = Seq(
    "cats-core",
    "cats-free"
  ).map("org.typelevel" %% _ % "0.7.2")
}
