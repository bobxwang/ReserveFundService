organization := "com.bob"

name := "ReserveFundService"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-http" % "6.34.0",
  "com.twitter" %% "finagle-mysql" % "6.34.0"
).map(_.exclude("com.google.code.findbugs", "jsr305"))

libraryDependencies ++= Seq(
  "com.twitter.finatra" % "finatra-http_2.11" % "2.1.5",
  "com.twitter.finatra" % "finatra-slf4j_2.11" % "2.1.5"
).map(_.exclude("com.google.code.findbugs", "jsr305"))

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"

baseAssemblySettings

mainClass in(Compile, run) := Some("com.bob.reservefund.scala.FundApp")

/* 包合并策略 */
assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
  case "application.conf" => MergeStrategy.concat
  case "unwanted.txt" => MergeStrategy.discard
  case "BUILD" => MergeStrategy.discard
  case m if m.endsWith("io.netty.versions.properties") => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

/* 修改包名 */
assemblyShadeRules in assembly := Seq(
  /* 相同包不同版本时可以修改包名 */
  ShadeRule.rename("com.google.**" -> "scom.google.@1").inAll
)