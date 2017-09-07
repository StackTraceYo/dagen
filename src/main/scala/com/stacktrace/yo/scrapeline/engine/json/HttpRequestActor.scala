package com.stacktrace.yo.scrapeline.engine.json

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.stacktrace.yo.scrapeline.engine.json.JSONProtocol.{RequestUrl, Requested}

import scala.concurrent.ExecutionContext

class HttpRequestActor(implicit ec: ExecutionContext) extends Actor with ActorLogging {

  import akka.pattern.pipe

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val http = Http(context.system)


  override def receive: Receive = {
    case msg@RequestUrl(url: String) =>
      val oSender = sender
      val callUrl = url
      http.singleRequest(HttpRequest(uri = callUrl))
        .map(response => {
          Requested(url, response)
        })
        .pipeTo(oSender)
  }
}


