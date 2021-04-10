name := "cats-sandbox"
version := "0.0.1-SNAPSHOT"

val format = taskKey[Unit]("Format files using scalafmt and scalafix")

val CatsEffectVersion = "3.0.2"
val CatsVersion = "2.5.0"
val LogbackVersion = "1.2.3"
val MunitVersion = "0.7.23"

val MUnitFramework = new TestFramework("munit.Framework")

lazy val testSettings: Seq[Def.Setting[_]] = List(
  Test / parallelExecution := false,
  skip.in(publish) := true,
  fork := true,
  testFrameworks := List(MUnitFramework),
  testOptions.in(Test) ++= {
    List(Tests.Argument(MUnitFramework, "+l", "--verbose"))
  }
)

lazy val root = (project in file("."))
  .settings(
    testSettings,
    organization := "asachdeva",
    name := "cats-sandbox",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.5",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "org.typelevel" %% "cats-core" % CatsVersion,
      "org.typelevel" %% "cats-effect" % CatsEffectVersion
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    scalafmtOnCompile := true,
    testFrameworks := List(new TestFramework("munit.Framework")),
    format := {
      Command.process("scalafmtAll", state.value)
      Command.process("scalafmtSbt", state.value)
    }
  )

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8", // source files are in UTF-8
  "-deprecation", // warn about use of deprecated APIs
  "-unchecked", // warn about unchecked type parameters
  "-feature", // warn about misused language features
  "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
  "-language:postfixOps",
  "-Xlint", // enable handy linter warnings
  "-Xfatal-warnings", // turn compiler warnings into errors
  "-Ywarn-unused"
)

resolvers += Resolver.sonatypeRepo("releases")

// CI build
addCommandAlias("buildCatsSandbox", ";clean;+test;")
