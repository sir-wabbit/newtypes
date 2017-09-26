val commonSettings = List(
  addCompilerPlugin(Versions.kindProjector),
  addCompilerPlugin(Versions.paradise),
  organization := "com.alexknvl",
  version      := "0.3.0",
  licenses     += ("MIT", url("http://opensource.org/licenses/MIT")),
  scalaVersion := "2.12.3",
  scalacOptions ++= List(
    "-deprecation", "-unchecked", "-feature",
    "-encoding", "UTF-8",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Xfuture",
    "-Xplugin-require:macroparadise"),

  libraryDependencies ++= Versions.scalameta,
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.mavenLocal))


lazy val commonJvmSettings = Seq(
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF"),
  libraryDependencies ++= Versions.testing)

lazy val commonJsSettings = Seq(
  scalaJSStage in Global := FastOptStage,
  parallelExecution := false,
  jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
  // batch mode decreases the amount of memory needed to compile scala.js code
  scalaJSOptimizerOptions := scalaJSOptimizerOptions.value.withBatchMode(scala.sys.env.get("TRAVIS").isDefined))

lazy val root = project.in(file("."))
  .settings(commonSettings: _*)
  .aggregate(newtypesJVM, newtypesJS)

lazy val newtypes = crossProject.crossType(MyCrossType)
  .in(file("."))
  .settings(name := "newtypes")
  .settings(moduleName := "newtypes")
  .settings(commonSettings: _*)
  .jsSettings(commonJsSettings:_*)
  .jvmSettings(commonJvmSettings:_*)

lazy val newtypesJVM = newtypes.jvm
lazy val newtypesJS = newtypes.js
