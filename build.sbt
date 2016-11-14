scalaVersion := "2.11.8"


lazy val akkaV = "2.4.12"

lazy val app = project.in(file("."))

resolvers += "lightbend-contrail" at "https://dl.bintray.com/typesafe/commercial-maven-releases"
enablePlugins(Cinnamon)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  Cinnamon.library.cinnamonSandbox,
  Cinnamon.library.cinnamonAkka,
  Cinnamon.library.cinnamonCHMetrics
)
fork in run := true
connectInput in run := true

// Add the Monitoring Agent for run and test
cinnamon in run := true
cinnamon in test := true

// Set the Monitoring Agent log level
cinnamonLogLevel := "INFO"
