package com.stacktrace.yo.dagen.engine.http

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.routing.RoundRobinPool
import akka.stream.Materializer
import akka.util.ByteString
import com.stacktrace.yo.dagen.engine.core.protocol.SupervisorProtocol.SendNextRequests
import com.stacktrace.yo.dagen.engine.http.HttpRequestProtocol.{JSONContentCallBack, RequestUrl, RequestUrlAndCall, Requested}
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

  val requestCallbacks: TrieMap[String, JSONContentCallBack] = TrieMap[String, JSONContentCallBack]()

  val pendingRequest: mutable.Queue[(String, JSONContentCallBack)] = scala.collection.mutable.Queue[(String, JSONContentCallBack)]()
  //  val pendingHandle: mutable.Queue[(String, JSONContentCallBack)] = scala.collection.mutable.Queue[(String, JSONContentCallBack)]()

  val tick: Cancellable = context.system.scheduler.schedule(0 millis, 5000 millis, self, SendNextRequests())


  override def receive: PartialFunction[Any, Unit] = {

    case msg@RequestUrlAndCall(url: String, callback: JSONContentCallBack) =>
      pendingRequest.enqueue((url, callback))
    case SendNextRequests() =>
      for (i <- 1 to Math.min(pendingRequest.size, 100 - requestCallbacks.size)) {
        val process = pendingRequest.dequeue
        self ! ProcessHttpRequest(process._1, process._2)
      }
    case msg@ProcessHttpRequest(url: String, callback: JSONContentCallBack) =>
      requestCallbacks.put(url, callback)
      requesters ! RequestUrl(url)
    case msg@Requested(url: String, doc: HttpResponse) =>
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

  case class ProcessHttpRequest(url: String, callBack: JSONContentCallBack)

}







