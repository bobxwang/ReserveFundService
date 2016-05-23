organization := "com.bob"

name := "ReserveFundService"

version := "1.0"

scalaVersion := "2.11.6"

//libraryDependencies ++= Seq(
//  "com.twitter" %% "finagle-http" % "6.34.0",
//  "com.twitter" %% "finagle-mysql" % "6.34.0"
//).map(_.exclude("com.google.code.findbugs", "jsr305"))

libraryDependencies ++= Seq(
  "com.twitter.finatra" % "finatra-http_2.11" % "2.1.6",
  "com.twitter.finatra" % "finatra-slf4j_2.11" % "2.1.6"
).map(_.exclude("com.google.code.findbugs", "jsr305")
  .exclude("com.google.code.findbugs", "annotations"))

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"

libraryDependencies += "com.github.xiaodongw" %% "swagger-finatra2" % "0.5.1"

libraryDependencies += ("com.netflix.eureka" % "eureka-client" % "1.1.147")
  .exclude("javax.ws.rs", "jsr311-api")
  .exclude("commons-logging", "commons-logging")
  .exclude("xmlpull", "xmlpull")

libraryDependencies ++= Seq(
  "com.twitter" % "scrooge-core_2.11" % "4.7.0",
  "com.twitter.finatra" % "finatra-thrift_2.11" % "2.1.6",
  "org.apache.thrift" % "libthrift" % "0.8.0"
)

baseAssemblySettings

mainClass in(Compile, run) := Some("com.bob.reservefund.scala.FundApp")

logLevel in assembly := Level.Debug

/* 包合并策略 */
assemblyMergeStrategy in assembly := {
  /* 将org,xmlpull这个下的所有类和文件都做合并 */
  case PathList("org", "xmlpull", xs@_ *) => MergeStrategy.first
  case PathList("javax", "servlet", xs@_ *) => MergeStrategy.first
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

/* where to find the thrift files, default is src/main/thrift */
scroogeThriftSourceFolder <<= baseDirectory