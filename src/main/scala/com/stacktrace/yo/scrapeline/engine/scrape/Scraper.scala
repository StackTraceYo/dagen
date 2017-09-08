package com.stacktrace.yo.scrapeline.engine.scrape

import akka.actor.{Actor, ActorRef}

/**
  * Created by Stacktraceyo on 9/6/17.
  */
trait Scraper {


  this: Actor =>

  var scrapeSupervisor: ActorRef

}
