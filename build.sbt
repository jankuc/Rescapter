
name := "Rescapter"
version := "1.0"
scalaVersion := "2.12.1"
mainClass in Compile := Some("Rescapter.Rescapter")

libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.45.0"
libraryDependencies += "com.typesafe" % "config" % "1.2.1"
libraryDependencies += "com.twitter.common" % "util" % "0.0.121"
libraryDependencies += "org.apache.commons" % "commons-email" % "1.4"

assemblyJarName := "Rescapter-assembly.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
