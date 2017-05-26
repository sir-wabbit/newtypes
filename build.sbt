val commonSettings = List(
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3"),
  organization := "edu.osu.nlp",
  scalaVersion := "2.12.1",
  scalaOrganization := "org.typelevel",
  scalacOptions ++= List(
    "-deprecation", "-unchecked", "-feature",
    "-encoding", "UTF-8",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Ypartial-unification",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Xfuture"),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.mavenLocal))

lazy val root = (project in file("."))
  .settings(name := "newts")
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Libraries.scalameta,
    addCompilerPlugin(Libraries.paradise),
    scalacOptions += "-Xplugin-require:macroparadise")
