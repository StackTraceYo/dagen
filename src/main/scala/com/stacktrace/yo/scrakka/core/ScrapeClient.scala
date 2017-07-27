package com.stacktrace.yo.scrakka.core

import net.ruippeixotog.scalascraper.browser.JsoupBrowser

object ScrapeClient {

  lazy val jsoup = JsoupBrowser()

  def scrape(url: String): jsoup.DocumentType = {
    jsoup.get(url)
  }


}
