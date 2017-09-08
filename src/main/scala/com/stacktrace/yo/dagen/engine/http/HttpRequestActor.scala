package com.stacktrace.yo.dagen.engine.http

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.stacktrace.yo.dagen.engine.http.HttpRequestProtocol.{Request, ResponseFromRequest}

import scala.concurrent.ExecutionContext

class HttpRequestActor(implicit ec: ExecutionContext) extends Actor with ActorLogging {

  import akka.pattern.pipe

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val http = Http(context.system)


  override def receive: Receive = {
    case msg@Request(request: HttpRequest) =>
      val oSender = sender
      http.singleRequest(request)
        .map(response => {
          println(response.status)
          ResponseFromRequest(request, response)
        })
        .pipeTo(oSender)
  }
}


