package com.bob.reservefund.scala

import com.google.inject.Inject
import com.twitter.finagle.http.{Response, Request}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder

class MExceptionMapper @Inject()(response: ResponseBuilder) extends ExceptionMapper[Exception] {

  override def toResponse(request: Request, throwable: Exception): Response = {

    response.badRequest(s"erors- ${throwable.getMessage}")
  }
}