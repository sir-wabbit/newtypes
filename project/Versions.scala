import sbt._

object Versions {
  val kindProjector : ModuleID       =      "org.spire-math"       %% "kind-projector" % "0.9.4"
  val paradise      : ModuleID       =      "org.scalameta"        %  "paradise"       % "3.0.0-M10" cross CrossVersion.full
  val scalameta     : List[ModuleID] = List("org.scalameta"        %% "scalameta"      % "1.8.0")
  val scalacheck    : List[ModuleID] = List("org.scalacheck"       %% "scalacheck"     % "1.13.5" % "test")
  val discipline    : List[ModuleID] = List("org.typelevel"        %% "discipline"     % "0.8"  % "test")
  val scalatest     : List[ModuleID] = List("org.scalatest"        %% "scalatest"      % "3.0.3"  % "test")
  val testing       : List[ModuleID] = scalacheck ++ scalatest ++ discipline
}
