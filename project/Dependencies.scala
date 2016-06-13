import sbt._

object Dependencies {

  object Versions {
    val kafkaVer = "0.9.0.1"
    val scoptVer = "3.4.0"
    val scalaHttpVer = "2.3.0"
    val sprayJsonVer = "1.3.2"
    val sprayVer = "1.3.1"
    val jodaVer = "2.9.4"
    val akkaVer = "2.3.15"
  }

  import Versions._

  val kafkaDeps = Seq(
    "org.apache.kafka" % "kafka-clients" % kafkaVer
  )

  val sprayDeps = Seq(
    "io.spray" %% "spray-can" % sprayVer,
    "io.spray" %% "spray-routing" % sprayVer,
    "com.typesafe.akka" %% "akka-actor" % akkaVer
  )

  val coreDeps = Seq(
    "com.github.scopt" %% "scopt" % scoptVer,
    "org.scalaj" %% "scalaj-http" % scalaHttpVer,
    "io.spray" %%  "spray-json" % sprayJsonVer,
    "joda-time" % "joda-time" % jodaVer
  )

  val allDeps = coreDeps ++ kafkaDeps ++ sprayDeps
}