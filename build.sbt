import sbt._
import sbt.Keys._
import sbtavro.SbtAvro._

lazy val commonSettings = Seq(
  organization := "pl.example.spark",
  name := "spark-avro-issue",
  version := "0.0.1-SNAPSHOT",
  scalacOptions in Compile ++= Seq(
    "-encoding", "UTF-8",
    "-target:jvm-1.7",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlog-reflective-calls",
    "-Xlint"),
  assemblyMergeStrategy in assembly := {
    case PathList(xs@_*) if xs.last == "pom.xml" || xs.last == "pom.properties" =>
      MergeStrategy.rename
    case PathList("META-INF", xs@_*) =>
      (xs map {
        _.toLowerCase
      }) match {
        case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
          MergeStrategy.discard
        case ps@(x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
          MergeStrategy.discard
        case "plexus" :: xs =>
          MergeStrategy.discard
        case "services" :: xs =>
          MergeStrategy.filterDistinctLines
        case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
          MergeStrategy.filterDistinctLines
        case _ => MergeStrategy.discard
      }
    case x => MergeStrategy.first
  }
)

scalaVersion := "2.10.5"

lazy val root = (project in file("."))
  .settings(sbtavro.SbtAvro.avroSettings : _*)
  .settings(commonSettings : _*)
  .settings(
    version in avroConfig := "1.7.7",
    stringType in avroConfig := "String",
    javaSource in avroConfig := baseDirectory.value / "src" / "main" / "java",
    unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "java"
  )

libraryDependencies ++= Seq(
  "com.twitter" % "parquet-avro" % "1.4.3",
  "net.sf.supercsv" % "super-csv" % "2.2.0",
  "org.apache.spark" %% "spark-streaming" % "1.2.0" % "provided",
  "org.apache.spark" %% "spark-core" % "1.2.0" % "provided",
  "org.apache.spark" %% "spark-sql" % "1.2.0" % "provided",
  "org.apache.avro" % "avro" % "1.7.7",
  "com.twitter" %% "chill-avro" % "0.7.1",
  "org.apache.avro" % "avro-mapred" % "1.7.7" classifier "hadoop2"
)
