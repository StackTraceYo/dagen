package com.stacktrace.yo.scrapeline.old

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.stacktrace.yo.scrapeline.engine.scrape.ScrapeActor
import com.stacktrace.yo.scrapeline.engine.scrape.ScrapeProtocol.{BeginScrape, ScrapedContent}
import com.stacktrace.yo.scrapeline.imdb.Domain.MovieNameAndDetailUrl

import scala.concurrent.duration._
import scala.language.postfixOps


object ImdbScraper extends App {

  implicit val timeout = Timeout(5 seconds)
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  val system = ActorSystem("scraper")

  getNameUrlTuples()

  def test = {
    val phase1 =
      system.actorOf(Props(
        new HttpRequestSupervisorOld(
          Set("http://www.the-numbers.com/movie/budgets/all"))
      ))
  }

  def getNameUrlTuples(): Unit = {
    val reader = system.actorOf(Props(new ScrapeActor()))
    //get the list movie names and detail urls
    val movieNameAndUrlList = ask(reader, BeginScrape("http://www.the-numbers.com/movie/budgets/all"))
      .mapTo[ScrapedContent]
      .flatMap(content => {
        val phase1 = system.actorOf(Props(new MovieNameUrlScraper()))
        val movieNameAndDetailUrlList = ask(phase1, content)
          .mapTo[List[MovieNameAndDetailUrl]]
        movieNameAndDetailUrlList
      })
    //phase 2
    movieNameAndUrlList.map(
      list => {
        val listOfUrl =
          list.map(obj => {
            println(obj.url)
            obj.url
          })
        val urlSet = listOfUrl.toSet
        system.actorOf(Props(new HttpRequestSupervisorOld(urlSet)))
      }
    )
  }
}

//.map(content => {
//val table = content.document >> elementList("table tr")
//val movieLinkTuples = table.flatMap(tr => {
//val name = tr >> elementList("tr b a")
//name.map(
//link => {
//MovieNameAndDetailUrl(link.text, "http://www.the-numbers.com/" + link.attr("href"))
//}
//)
//})
//movieLinkTuples
//})


