package com.stacktrace.yo.scrakka.core

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Sink, Source}
import net.ruippeixotog.scalascraper.model.Document

import scala.concurrent.Future

object Pipeline {

  trait PipelineStart[T] {

    def getPipelineSource: Source[T, Any]


  }

  trait ReadPipeline[T] {

    def getReadFlow: Flow[T, Document, NotUsed]

  }

  trait MapPipeline[K, T] {

    def getMappingFlow: Flow[K, T, NotUsed]

    def mapPipe(in: K): Future[T]

  }

  trait ParsePipeline[T] {

    def getParseFlow: Flow[Document, T, NotUsed]

  }


  trait PipelineEnd[T, K] {

    def getPipeOut: Sink[T, K]
  }

  trait RunnablePipeline[K] {

    def buildAndRun: K

  }


}
