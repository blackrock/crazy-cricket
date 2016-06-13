import sbt.Keys._
import sbtassembly.AssemblyPlugin.autoImport._

object Common {

  val commonSettings = Seq(
    name := "crazy-cricket",
    version := "0.1",
    scalaVersion := "2.11.7",
    organization := "com.bfm"
  )

  val assemblySettings = Seq(
    assemblyMergeStrategy in assembly := {
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    test in assembly := {}
  )
}