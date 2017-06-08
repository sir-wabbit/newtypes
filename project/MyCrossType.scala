import org.scalajs.sbtplugin.cross.CrossType
import sbt._

//from http://xuwei-k.github.io/slides/scala-js-matsuri/#21
// so we don't want to move files to shared/*
object MyCrossType extends CrossType {
  def projectDir(crossBase: File, projectType: String) =
    crossBase / projectType
  def sharedSrcDir(projectBase: File, conf: String) =
    Some(projectBase.getParentFile / "src" / conf / "scala")
}
