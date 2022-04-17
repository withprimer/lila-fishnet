name := "lila-fishnet"

version := "3.0"

maintainer := "lichess.org"

lazy val root = Project("lila-fishnet", file("."))
  .enablePlugins(PlayScala, PlayNettyServer)
  .disablePlugins(PlayAkkaHttpServer)

scalaVersion := "3.1.2"
resourceDirectory in Compile := baseDirectory.value / "conf"

val kamonVersion = "2.5.1"
val nettyVersion = "4.1.76.Final"

libraryDependencies += "io.lettuce"   % "lettuce-core"                 % "6.1.8.RELEASE"
libraryDependencies += "io.netty"     % "netty-transport-native-epoll" % nettyVersion classifier "linux-x86_64"
libraryDependencies += "joda-time"    % "joda-time"                    % "2.10.14"
libraryDependencies += "org.lichess" %% "scalachess"                   % "11.0.1"
libraryDependencies += "io.kamon"    %% "kamon-core"                   % kamonVersion
libraryDependencies += "io.kamon"    %% "kamon-influxdb"               % kamonVersion
libraryDependencies += "io.kamon"    %% "kamon-system-metrics"         % kamonVersion

resolvers += "lila-maven" at "https://raw.githubusercontent.com/ornicar/lila-maven/master"

scalacOptions := Seq(
  "-encoding",
  "utf-8",
  "-rewrite",
  "-source:future-migration",
  "-indent",
  "-explaintypes",
  "-feature",
  "-language:postfixOps"
  // Warnings as errors!
  // "-Xfatal-warnings",
)

javaOptions ++= Seq("-Xms64m", "-Xmx128m")

sources in (Compile, doc) := Seq.empty

publishArtifact in (Compile, packageDoc) := false
