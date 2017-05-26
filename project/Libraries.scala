import sbt._

object Libraries {
  val paradise   = "org.scalameta"        %  "paradise"   % "3.0.0-M8" cross CrossVersion.full

  val scalacheck = List("org.scalacheck"       %% "scalacheck" % "1.13.5" % "test")
  val discipline = List("org.typelevel"        %% "discipline" % "0.7.3"  % "test")
  val scalatest  = List("org.scalatest"        %% "scalatest"  % "3.0.3"  % "test")

  val testing = scalacheck ++ scalatest ++ discipline

  val commonsLang = List("org.apache.commons"  % "commons-lang3" % "3.5")

  val cats       = List("org.typelevel"        %% "cats"       % "0.9.0")
  val simulacrum = List("com.github.mpilquist" %% "simulacrum" % "0.10.0")
  val shapeless  = List("com.chuusai"          %% "shapeless"  % "2.3.2")
  val leibniz    = List("com.alexknvl"         %% "leibniz"    % "0.3.1")
  val sourcecode = List("com.lihaoyi"          %% "sourcecode" % "0.1.3")
  val eff        = List("org.atnos"            %% "eff"        % "4.3.1")
  val refined    = List("eu.timepit"           %% "refined"    % "0.8.0")

  val junicode   = List("gcardone"             %  "junidecode" % "0.2")

  val scalameta  = List("org.scalameta"        %% "scalameta"  % "1.8.0")

  val iteratee = List(
    "io.iteratee"  %%  "iteratee-core",
    "io.iteratee"  %%  "iteratee-files")
    .map(_ % "0.10.0")

  val atto = List(
    "org.tpolecat" %% "atto-core",
    "org.tpolecat" %% "atto-compat-cats")
    .map(_ % "0.5.2")

  val circe = List(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser")
    .map(_ % "0.7.1")

  val fs2 = List(
    "co.fs2" %% "fs2-core" % "0.9.5",
    "co.fs2" %% "fs2-io" % "0.9.5",
    "com.spinoco" %% "fs2-http" % "0.1.7")
}