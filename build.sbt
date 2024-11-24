scalaVersion := "2.12.15" 


name := "scala-data-analysis"
organization := "ch.epfl.scala"
version := "1.0"

// Dependências do projeto
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.3.0",
  "org.apache.spark" %% "spark-sql" % "3.3.0",
  "org.knowm.xchart" % "xchart" % "3.8.0",  
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0",
  "org.apache.hadoop" % "hadoop-client" % "3.3.1",
  "org.jfree" % "jfreechart" % "1.5.3",
  "org.plotly-scala" %% "plotly-render" % "0.8.2",
  "org.seleniumhq.selenium" % "selenium-java" % "4.1.0"
)

// Configuração para o uso do plotly-scala, caso seja necessário
resolvers += "jitpack" at "https://jitpack.io"

evictionErrorLevel := Level.Warn

// Força o esquema de versionamento para `scala-parser-combinators` como EarlySemVer
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-parser-combinators" % VersionScheme.EarlySemVer
