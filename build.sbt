name := "expense"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.mongodb" %% "casbah" % "2.8.0"
libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.12"

//libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"





scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test"
  )
}
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.1"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.11"
libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.11"