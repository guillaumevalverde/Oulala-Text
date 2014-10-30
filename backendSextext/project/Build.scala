import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "SexText"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "mysql" % "mysql-connector-java" % "5.1.18",
    "org.json" % "json"%   "20140107"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
        version := appVersion,
        organization := "com.feth",
        publishArtifact in packageDoc := false
  )
  
  val buildSettings = Defaults.defaultSettings ++ Seq(
   
   javaOptions += "-XX:UseSplitVerifier"
   
  )
}