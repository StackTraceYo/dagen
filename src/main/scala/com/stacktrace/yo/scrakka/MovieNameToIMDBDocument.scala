package com.stacktrace.yo.scrakka

import java.net.URLEncoder
import java.nio.file.Paths

import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString
import akka.{Done, NotUsed}
import com.stacktrace.yo.scrakka.core.Pipeline.{MapPipeline, PipelineEnd, PipelineStart, RunnablePipeline}

import scala.concurrent.{ExecutionContext, Future}

class MovieNameToIMDBDocument(implicit val m: ActorMaterializer, implicit val ec: ExecutionContext) extends PipelineStart[String] with MapPipeline[String, String] with PipelineEnd[String, Future[Done]] with RunnablePipeline[Future[Done]] {

  override def getPipelineSource: Source[String, Future[IOResult]] = {
    FileIO.fromPath(Paths.get("movie.txt"))
      .via(Framing.delimiter(ByteString("\n"), 256)
        .map(_.utf8String)
      )
  }

  override def getMappingFlow: Flow[String, String, NotUsed] = {
    Flow[String]
      .mapAsyncUnordered(100)(mapPipe)
  }


  override def mapPipe(in: String): Future[String] = Future {
    val encodedString: String = URLEncoder.encode(in, "UTF-8")
    "http://www.imdb.com/find?ref_=nv_sr_fn&q=" + encodedString + "&s=tt"
  }


  override def getPipeOut: Sink[String, Future[Done]] = {
    Sink.foreach[String](println)
  }

  def buildAndRun: Future[Done] = {
    getPipelineSource
      .via(getMappingFlow)
      .runWith(getPipeOut)
  }

}
