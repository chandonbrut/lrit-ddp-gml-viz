name := "LRIT DDP XML/GML PolygonViz"

version := "1.0"

scalaVersion := "2.13.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

herokuAppName in Compile := "lrit-ddp-viz"

herokuConfigVars in Compile := Map(
	"JAVA_OPTS" -> "-Xmx512m -Xss2m -XX:+UseCompressedOops"
)

libraryDependencies ++= Seq(
	"com.vividsolutions" % "jts" % "1.13",
	"com.typesafe.play" %% "play-ws" % "2.7.3",
	"org.scala-lang.modules" %% "scala-xml" % "1.0.2"
)

libraryDependencies += guice

enablePlugins(PlayScala)