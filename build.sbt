import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.packager.docker.DockerChmodType

import NativePackagerHelper._

name := "lila-fishnet"

version := "2.0"

maintainer := "lichess.org"

//ensure builds use java version 11
javacOptions ++= Seq("-source", "11", "-target", "11")

lazy val root = Project("lila-fishnet", file("."))
  .enablePlugins(PlayScala, PlayNettyServer)
  .disablePlugins(PlayAkkaHttpServer)

scalaVersion := "2.13.8"
Compile/resourceDirectory  := baseDirectory.value / "conf"

val kamonVersion = "2.5.3"

libraryDependencies += "io.lettuce"   % "lettuce-core"                 % "6.1.8.RELEASE"
libraryDependencies += "io.netty"     % "netty-transport-native-epoll" % "4.1.77.Final" classifier "linux-x86_64"
libraryDependencies += "joda-time"    % "joda-time"                    % "2.10.14"
libraryDependencies += "org.lichess" %% "scalachess"                   % "10.4.5"
libraryDependencies += "io.kamon"    %% "kamon-core"                   % kamonVersion
libraryDependencies += "io.kamon"    %% "kamon-influxdb"               % kamonVersion
libraryDependencies += "io.kamon"    %% "kamon-system-metrics"         % kamonVersion

resolvers += "lila-maven" at "https://raw.githubusercontent.com/ornicar/lila-maven/master"

scalacOptions ++= Seq(
  "-explaintypes",
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Ymacro-annotations",
  // Warnings as errors!
  // "-Xfatal-warnings",
  // Linting options
  "-unchecked",
  "-Xcheckinit",
  "-Xlint:adapted-args",
  "-Xlint:constant",
  "-Xlint:delayedinit-select",
  "-Xlint:deprecation",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:missing-interpolator",
  "-Xlint:nullary-unit",
  "-Xlint:option-implicit",
  "-Xlint:package-object-classes",
  "-Xlint:poly-implicit-overload",
  "-Xlint:private-shadow",
  "-Xlint:stars-align",
  "-Xlint:type-parameter-shadow",
  "-Wdead-code",
  "-Wextra-implicit",
  "-Wnumeric-widen",
  "-Wunused:imports",
  "-Wunused:locals",
  "-Wunused:patvars",
  "-Wunused:privates",
  "-Wunused:implicits",
  "-Wunused:params"
  /* "-Wvalue-discard" */
)

javaOptions ++= Seq("-Xms64m", "-Xmx128m")

sources in (Compile, doc) := Seq.empty

publishArtifact in (Compile, packageDoc) := false


// docker
dockerBaseImage := "openjdk:11-jre-slim-buster"
dockerExposedPorts += 9665
Docker / daemonUserUid := None
Docker / daemonUser := "root"

//for m1, instruct to build amd64 package
dockerBuildCommand := {
   if (sys.props("os.arch") != "amd64") {
     dockerExecCommand.value ++ Seq("buildx", "build", "--platform=linux/amd64", "--load") ++ dockerBuildOptions.value :+ "."
   } else dockerBuildCommand.value
 }


dockerCommands := dockerCommands.value.filterNot {
  case ExecCmd("ENTRYPOINT", args @ _*) => true
  case ExecCmd("CMD",args @ _*) => true
  case cmd  => false
}

dockerCommands += Cmd("RUN","mkdir /opt/docker/logs")
dockerCommands += Cmd("ENTRYPOINT", "/opt/docker/bin/lila-fishnet -Dconfig.file=conf/prod.conf")