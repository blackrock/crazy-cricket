import java.io.File
import sbt._
import sbt.Keys._
import sbt.complete.DefaultParsers._
import sbtassembly.AssemblyPlugin.autoImport._

object BuildTask {
  val buildTask = InputKey[Unit]("build", "Builds assembly jar")

  lazy val buildtaskSettings = Seq(
    buildTask := {
      val args = spaceDelimited("<output>").parsed
      if(args.isEmpty) {
        IO.copyFile(assembly.value, new File(name.value + "-assembly-SNAPSHOT.jar"))
      } else {
        args.foreach(path => IO.copyFile(assembly.value, new File(path + Path.sep + name.value + ".jar")))
      }
    }
  )
}