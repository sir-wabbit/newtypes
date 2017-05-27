import sbt._

object Libraries {
  val paradise: ModuleID   = "org.scalameta"        %  "paradise"   % "3.0.0-M8" cross CrossVersion.full

  val scalacheck  : List[ModuleID] = List("org.scalacheck"       %% "scalacheck" % "1.13.5" % "test")
  val discipline  : List[ModuleID] = List("org.typelevel"        %% "discipline" % "0.7.3"  % "test")
  val scalatest   : List[ModuleID] = List("org.scalatest"        %% "scalatest"  % "3.0.3"  % "test")

  val testing     : List[ModuleID] = scalacheck ++ scalatest ++ discipline

  val commonsLang : List[ModuleID] = List("org.apache.commons"  % "commons-lang3" % "3.5")

  val cats        : List[ModuleID] = List("org.typelevel"        %% "cats"       % "0.9.0")
  val simulacrum  : List[ModuleID] = List("com.github.mpilquist" %% "simulacrum" % "0.10.0")
  val shapeless   : List[ModuleID] = List("com.chuusai"          %% "shapeless"  % "2.3.2")
  val leibniz     : List[ModuleID] = List("com.alexknvl"         %% "leibniz"    % "0.3.1")
  val sourcecode  : List[ModuleID] = List("com.lihaoyi"          %% "sourcecode" % "0.1.3")
  val eff         : List[ModuleID] = List("org.atnos"            %% "eff"        % "4.3.1")
  val refined     : List[ModuleID] = List("eu.timepit"           %% "refined"    % "0.8.0")
  val junicode    : List[ModuleID] = List("gcardone"             %  "junidecode" % "0.2")

  val scalameta   : List[ModuleID] = List("org.scalameta"        %% "scalameta"  % "1.8.0")

  val iteratee : List[ModuleID] = List(
    "io.iteratee"  %%  "iteratee-core",
    "io.iteratee"  %%  "iteratee-files")
    .map(_ % "0.10.0")

  val atto : List[ModuleID] = List(
    "org.tpolecat" %% "atto-core",
    "org.tpolecat" %% "atto-compat-cats")
    .map(_ % "0.5.2")

  val circe: List[ModuleID] = List(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser")
    .map(_ % "0.7.1")

  val fs2: List[ModuleID] = List(
    "co.fs2" %% "fs2-core" % "0.9.5",
    "co.fs2" %% "fs2-io" % "0.9.5",
    "com.spinoco" %% "fs2-http" % "0.1.7")
}