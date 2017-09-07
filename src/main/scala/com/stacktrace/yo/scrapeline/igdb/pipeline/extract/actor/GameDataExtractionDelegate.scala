package com.stacktrace.yo.scrapeline.igdb.pipeline.extract.actor

import java.io.File

import akka.actor.{ActorRef, Cancellable, Props}
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, ObjectReader}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.stacktrace.yo.scrapeline.igdb.pipeline.extract.actor.GameDataExtractionDelegate.{Process, ProcessObjects, Processed, ReadNextFile}
import com.stacktrace.yo.scrapeline.old.core.Protocol.{Report, StartDelegate, Working}
import com.stacktrace.yo.scrapeline.old.core.pipeline.Delegator
import org.stacktrace.yo.igdb.model.Game

import scala.collection.mutable
import scala.concurrent.duration._
import scala.language.postfixOps

class GameDataExtractionDelegate() extends Delegator {

  import context.dispatcher

  var pipeline: ActorRef = _

  //files to read
  lazy val filesToProcess: mutable.Queue[File] = scala.collection.mutable.Queue[File](init(): _*)
  lazy val objectsToProcess: mutable.Queue[Game] = scala.collection.mutable.Queue[Game]()
  // files finised
  var filesRead = 0
  //files in process
  var inProcess = 0
  //objects in process
  var objectsInProcess = 0
  var objectsProcessed = 0

  val reader: ObjectReader = new ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .registerModule(DefaultScalaModule)
    .readerFor(classOf[Array[Game]])

  lazy val reportTick: Cancellable = context.system.scheduler.schedule(10000 millis, 10000 millis, self, Report())
  lazy val readObjectTick: Cancellable = context.system.scheduler.schedule(0 millis, 10000 millis, self, ProcessObjects())

  override def receive: Receive = {
    case StartDelegate(pipeline: ActorRef) =>
      this.pipeline = pipeline
      init()
      reportTick
      self ! ReadNextFile()
    case ReadNextFile() =>
      val file = filesToProcess.dequeue()
      val gameList: Array[Game] = reader.readValue[Array[Game]](file)
      gameList.foreach(objectsToProcess.enqueue(_))
      filesRead += 1
      inProcess += 1
      readObjectTick
    case ProcessObjects() =>
      for (i <- 1 to Math.min(objectsToProcess.size, 100)) {
        val extractor = context.actorOf(Props(new GameDataExtractionActor()))
        extractor ! Process(objectsToProcess.dequeue())
        objectsInProcess += 1
      }
    case Processed() =>
      pipeline ! Working()
    case Report() =>
  }

  private def init(): Array[File] = {
    val file = new File("games/")
    recursiveListFiles(file)
  }

  private def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }
}

object GameDataExtractionDelegate {

  case class ReadNextFile()

  case class ProcessObjects()

  case class Process(game: Game)

  case class Processed()

}


