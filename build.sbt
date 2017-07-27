name := "scrapeline"

version := "1.0"

scalaVersion := "2.11.8"
val akkaVersion = "2.5.3"

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" % "akka-actor_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-stream_2.11" % akkaVersion,
    "org.jsoup" % "jsoup" % "1.10.3",
    "net.ruippeixotog" %% "scala-scraper" % "2.0.0"
  )
}