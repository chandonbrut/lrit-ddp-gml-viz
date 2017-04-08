name := "LRIT DDP XML/GML PolygonViz"

version := "1.0"

scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
	"com.vividsolutions" % "jts" % "1.13",
	"com.typesafe.play" %% "play-ws" % "2.5.10",
	"org.scala-lang.modules" %% "scala-xml" % "1.0.2"
)
