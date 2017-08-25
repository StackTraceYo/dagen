package com.stacktrace.yo.scrapeline.core

import com.fasterxml.jackson.databind.{ObjectMapper, ObjectWriter}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
  * Created by Ahmad on 8/23/2017.
  */
object FileWriting {

  private lazy val writer = new ObjectMapper().registerModule(DefaultScalaModule).writerWithDefaultPrettyPrinter()


  def getJsonWriter: ObjectWriter = writer


}
