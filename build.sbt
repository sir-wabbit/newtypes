val commonSettings = List(
  addCompilerPlugin(Versions.kindProjector),
  organization := "com.alexknvl",
  version      := "0.0.2",
  licenses     += ("MIT", url("http://opensource.org/licenses/MIT")),
  scalaVersion := "2.12.2",
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
  .settings(name := "newtypes")
  .settings(commonSettings: _*)
  .settings(
    resolvers += Resolver.mavenLocal,
    addCompilerPlugin(Versions.paradise),
    libraryDependencies ++= Versions.scalameta ++ Versions.testing,
    scalacOptions += "-Xplugin-require:macroparadise")
