import sbt._
import Keys._
import play.sbt.PlayImport._

object CommonSettingsPlugin extends AutoPlugin {
  override def trigger = allRequirements

  override def projectSettings = Seq(
    organization := "com.gu",
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq("-deprecation", "-feature", "-language:postfixOps"),
    version := "1.0",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
      "org.scala-lang.modules" %% "scala-async" % "0.9.5",
      "com.github.nscala-time" %% "nscala-time" % "2.10.0",
      "com.softwaremill.macwire" %% "macros" % "2.2.3" % "provided",
      "org.specs2" %% "specs2-core" % "3.6.4" % Test,
      "org.specs2" %% "specs2-matcher-extra" % "3.6.4" % Test,
      specs2 % Test
    )
  )
}
