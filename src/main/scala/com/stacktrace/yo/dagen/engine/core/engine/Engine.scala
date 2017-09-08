package com.stacktrace.yo.dagen.engine.core.engine

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.stacktrace.yo.dagen.engine.core.definitions.{HttpDefinition, LineDefinition, ScrapeDefinition}
import com.stacktrace.yo.dagen.engine.core.protocol.EngineProtocol.{Begin, Read, Request, Scrape}
import com.stacktrace.yo.dagen.engine.http.HttpRequestProtocol.{JSONContentCallBack, RequestUrlAndCall}
import com.stacktrace.yo.dagen.engine.http.{HttpRequestSupervisor, HttpRequester}
import com.stacktrace.yo.dagen.engine.scrape.ScrapeProtocol.{ScrapeUrlAndCall, ScrapedContentCallBack}
import com.stacktrace.yo.dagen.engine.scrape.{ScrapeSupervisor, Scraper}

import scala.concurrent.ExecutionContext

/**
  * Created by Stacktraceyo on 9/6/17.
  */
class Engine(scrapeline: LineDefinition)(implicit as: ActorSystem) extends Actor with ActorLogging with Scraper with HttpRequester {

  implicit val ec: ExecutionContext = as.dispatcher
  implicit val am = ActorMaterializer()
  //  override val fileSourceSupervisor: ActorRef = context.actorOf(Props(new FileSourceSupervisor(this)))
  override lazy val scrapeSupervisor: ActorRef = context.actorOf(Props(new ScrapeSupervisor()))
  override lazy val requestSupervisor: ActorRef = context.actorOf(Props(new HttpRequestSupervisor()))

  override def receive: PartialFunction[Any, Unit] = {

    case Begin() =>
      scrapeline.start.foreach {
        case Scrape(url) =>
          log.info("Scraping Url: {}", url)
          self ! ScrapeUrlAndCall(url, scrapeline.asInstanceOf[ScrapeDefinition].beginScrape)
        case Request(url) =>
          log.info("Requesting Url: {}", url)
          self ! RequestUrlAndCall(url, scrapeline.asInstanceOf[HttpDefinition].beginRead)
        case Read(url) =>
          log.info("Reading Url: {}", url)
      }
    case msg@ScrapeUrlAndCall(url: String, callback: ScrapedContentCallBack) =>
      scrapeSupervisor ! msg
    case msg@RequestUrlAndCall(url: String, callback: JSONContentCallBack) =>
      requestSupervisor ! msg

  }


}
