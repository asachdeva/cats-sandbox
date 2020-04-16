name := "cats-sandbox"
version := "0.0.1-SNAPSHOT"

scalafixDependencies in ThisBuild +=
  "com.nequissimus" %% "sort-imports" % "0.3.2"

val format = taskKey[Unit]("Format files using scalafmt and scalafix")

val CatsEffectVersion = "2.1.2"
val CatsVersion = "2.1.1"
val LogbackVersion = "1.2.3"
val ScalaMockVersion = "4.4.0"
val ScalaTestVersion = "3.1.1"

lazy val root = (project in file("."))
  .settings(
    organization := "asachdeva",
    name := "cats-sandbox",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "org.scalamock" %% "scalamock" % ScalaMockVersion % Test,
      "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
      "org.typelevel" %% "cats-core" % CatsVersion,
      "org.typelevel" %% "cats-effect" % CatsEffectVersion
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin(scalafixSemanticdb),
    format := {
      Command.process("scalafmt", state.value)
      Command.process("scalafmtSbt", state.value)
      Command.process("scalafix", state.value)
      Command.process("scalafix RemoveUnused", state.value)
      Command.process("test:scalafix", state.value)
      Command.process("test:scalafix RemoveUnused", state.value)
    }
  )

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8", // source files are in UTF-8
  "-deprecation", // warn about use of deprecated APIs
  "-unchecked", // warn about unchecked type parameters
  "-feature", // warn about misused language features
  "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
  "-Xlint", // enable handy linter warnings
  "-Xfatal-warnings", // turn compiler warnings into errors
  "-Ywarn-unused"
)

resolvers += Resolver.sonatypeRepo("releases")

// CI build
addCommandAlias("buildCatsSandbox", ";clean;+test;")
