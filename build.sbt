val commonSettings = List(
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3"),
  organization := "com.alexknvl",
  version      := "0.0.1",
  licenses     += ("MIT", url("http://opensource.org/licenses/MIT")),
  scalaVersion := "2.12.1",
  scalacOptions ++= List(
    "-deprecation", "-unchecked", "-feature",
    "-encoding", "UTF-8",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Xfuture"),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")))

lazy val root = (project in file("."))
  .settings(name := "newts")
  .settings(commonSettings: _*)
  .settings(
    addCompilerPlugin(Libraries.paradise),
    libraryDependencies ++= Libraries.scalameta,
    scalacOptions += "-Xplugin-require:macroparadise")
