package com.stacktrace.yo.scrapeline.core

import org.stacktrace.yo.igdb.client.IGDBClient

/**
  * Created by Stacktraceyo on 8/18/17.
  */
object IGDBAPIClient {

  private lazy val client = IGDBClient.getBuilder.
    withKey("ea380a24711ad2e2ae60c63223371f03")
    .withUrl("https://api-2445582011268.apicast.io")
    .build

  def getClient: IGDBClient = {
    client
  }

}
