logLevel := Level.Warn

resolvers += Resolver.url("https://github.com/sbt/sbt-assembly.git")
resolvers += Resolver.url("https://github.com/jrudolph/sbt-dependency-graph.git")
// 解决下载路径问题(swagger-finatra)
resolvers += Resolver.url("https://oss.sonatype.org/content/repositories/releases/")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.1")

// 可以像mvn一样显示依赖关系图
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

resolvers += "twitter-repo" at "https://maven.twttr.com"

addSbtPlugin("com.twitter" % "scrooge-sbt-plugin" % "4.5.0")