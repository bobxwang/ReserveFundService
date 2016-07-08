package com.bob.reservefund.scala

import com.google.inject.Inject
import com.twitter.finagle.http.{Response, Request}
import com.twitter.finatra.http.exceptions.HttpException
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder

class MExceptionMapper @Inject()(response: ResponseBuilder) extends ExceptionMapper[Exception] {

  override def toResponse(request: Request, throwable: Exception): Response = {
    throwable match {
      case e: HttpException =>
        val builder = response.status(e.statusCode)
        builder.json(
          s"""
             |{
             |  "msg": ${e.getMessage()}
              |}
          """.stripMargin)
      case _: Exception =>
        response.status(500).json( """{
                                     |      "name": "Bob",
                                     |      "age": 19
                                     |    }""")
    }
  }
}