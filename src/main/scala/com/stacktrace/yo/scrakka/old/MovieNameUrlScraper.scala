package com.stacktrace.yo.scrakka.old

import akka.actor.{Actor, ActorLogging}
import com.stacktrace.yo.scrakka.old.ScrapeActor.ScrapeContent
import com.stacktrace.yo.scrakka.scrape.Domain.MovieNameAndDetailUrl
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

class MovieNameUrlScraper extends Actor with ActorLogging {

  override def receive: Receive = {

    case msg@ScrapeContent(document: Document) =>
      val oSender = sender
      val table = document >> elementList("table tr")
      val movieLinkTuples = table.flatMap(tr => {
        val name = tr >> elementList("tr b a")
        name.map(
          link => {
            MovieNameAndDetailUrl(link.text, "http://www.the-numbers.com/" + link.attr("href"))
          }
        )
      })
      oSender ! movieLinkTuples
  }
}
