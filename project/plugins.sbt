logLevel := Level.Warn

resolvers += Resolver.url("https://github.com/sbt/sbt-assembly.git")
resolvers += Resolver.url("https://github.com/jrudolph/sbt-dependency-graph.git")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.1")

// 可以像mvn一样显示依赖关系图
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")