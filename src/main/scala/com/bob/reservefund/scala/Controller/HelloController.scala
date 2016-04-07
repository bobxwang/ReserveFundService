package com.bob.reservefund.scala.Controller

import javax.inject.Inject

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Method, Response, Request}
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.util.Future

case class hellojson(name: String, age: Int)

class HelloController extends Controller {

  get("/hello") { request: Request =>
    info("hello")
    "hello"
  }

  get("/hellojson") { request: Request =>
    List(hellojson("bb", 1), hellojson("aa", 2), hellojson("cc", 3))
  }
}

class HelloFilter @Inject()(responseBuilder: ResponseBuilder) extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val userid = request.params.getOrElse("userid", "")
    if (userid.isEmpty) {
      responseBuilder.badRequest(hellojson("请求非法", 1)).toFuture
    } else {
      service(request)
    }
  }
}