package com.stacktrace.yo.dagen.engine.http

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.routing.RoundRobinPool
import akka.stream.Materializer
import akka.util.ByteString
import com.stacktrace.yo.dagen.engine.core.protocol.SupervisorProtocol.SendNextRequests
import com.stacktrace.yo.dagen.engine.http.HttpRequestProtocol._
import com.stacktrace.yo.dagen.engine.http.HttpRequestSupervisor.ProcessHttpRequest

import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by Stacktraceyo on 9/6/17.
  */
class HttpRequestSupervisor()(implicit ec: ExecutionContext, am: Materializer) extends Actor with ActorLogging {

  val requesters: ActorRef = context.actorOf(RoundRobinPool(5).props(Props(new HttpRequestActor())))
  //  val handlers: ActorRef = context.actorOf(RoundRobinPool(5).props(Props(new HttpRequestActor())))

  val requestCallbacks: TrieMap[HttpRequest, JSONContentCallBack] = TrieMap[HttpRequest, JSONContentCallBack]()

  val pendingRequest: mutable.Queue[(HttpRequest, JSONContentCallBack)] = scala.collection.mutable.Queue[(HttpRequest, JSONContentCallBack)]()
  //  val pendingHandle: mutable.Queue[(String, JSONContentCallBack)] = scala.collection.mutable.Queue[(String, JSONContentCallBack)]()

  val tick: Cancellable = context.system.scheduler.schedule(0 millis, 5000 millis, self, SendNextRequests())


  override def receive: PartialFunction[Any, Unit] = {

    case msg@RequestUrlAndCall(url: String, callback: JSONContentCallBack) =>
      pendingRequest.enqueue((HttpRequest(uri = url), callback))
    case msg@RequestAndCall(httpRequest: HttpRequest, callback: JSONContentCallBack) =>
      pendingRequest.enqueue((httpRequest, callback))
    case SendNextRequests() =>
      for (i <- 1 to Math.min(pendingRequest.size, 100 - requestCallbacks.size)) {
        val process = pendingRequest.dequeue
        self ! ProcessHttpRequest(process._1, process._2)
      }
    case msg@ProcessHttpRequest(httpRequest: HttpRequest, callback: JSONContentCallBack) =>
      requestCallbacks.put(httpRequest, callback)
      requesters ! Request(httpRequest)
    case msg@ResponseFromRequest(request: HttpRequest, doc: HttpResponse) =>
      println(requestCallbacks.get(request).isDefined)
      doc match {
        case HttpResponse(StatusCodes.OK, headers, entity, _) =>
          entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
            log.info("Got response, body: " + body.utf8String)
          }
        case resp@HttpResponse(code, _, _, _) =>
          log.info("Request failed, response code: " + code)
          resp.discardEntityBytes()
      }
  }
}

object HttpRequestSupervisor {

  case class ProcessHttpRequest(httpRequest: HttpRequest, callBack: JSONContentCallBack)

}







