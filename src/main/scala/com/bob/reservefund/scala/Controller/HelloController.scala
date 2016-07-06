package com.bob.reservefund.scala.Controller

import javax.inject.Inject

import com.google.inject.Singleton
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finatra.http.request.RequestUtils
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.finatra.validation.NotEmpty
import com.twitter.util.Future

case class Hello(@NotEmpty name: String, age: Int, sex: Int)

@Singleton
class HelloController @Inject()(finatraObjectMapper: FinatraObjectMapper) extends FinatraController {

  def toInt(s: String): Option[Int] = {
    try {
      Some(s.toInt)
    } catch {
      case e: Exception => None
    }
  }

  get("/hellos", swagger(o => {
    o.summary("get all hellos")
      .description("return all hellos item")
      .tag("hello")
      .produces("application/json")
      .responseWith[List[Hello]](200, "the response json", example = Some(List(Hello("bb", 1, 2))))
      .responseWith[Unit](404, "the address is not found")
  })) { request: Request =>
    info("hello")
    List(Hello(RequestUtils.pathUrl(request), 1, 1), Hello("aa", 2, 2), Hello("cc", 3, 1))
  }

  get("/hellos/:id", swagger(o => {
    o.summary("get the id hello")
      .description("return the appoint hello")
      .tag("hello")
      .queryParam[Int]("sex", "the hello sex")
      .routeParam[String]("id", "the hello id")
      .produces("application/json")
      .responseWith[Hello](200, "the response json", example = Some(Hello("bb", 1, 2)))
      .responseWith[Unit](404, "the address is not found")
  })) { request: Request =>
    val iid = request.params("id")
    val id = request.getParam("id")
    val sex = request.getIntParam("sex", 1)
    Hello(s"name id ${id},pm id is ${iid}", toInt(id).getOrElse(0), sex)
  }

  put("/hellos/:id", swagger(o => {
    o.summary("modify the id hello")
      .description("modify the hello and return the hello")
      .tag("hello")
      .formParam[String]("name", "the hello name")
      .formParam[Int]("sex", "the hello sex")
      .routeParam[String]("id", "the hello id")
      .produces("application/json")
      .responseWith[Hello](200, "the response json", example = Some(Hello("bb", 1, 2)))
      .responseWith[Unit](404, "the address is not found")
  })) { request: Request =>
    val id = request.getParam("id")
    val sex = request.getIntParam("sex", 1)
    val name = request.getParam("name")
    Hello(s"${name}-${id}", toInt(id).getOrElse(0), sex)
  }

  post("/hellos/paramisclass",
    swagger { o =>
      o.summary("Create a new hello")
        .tag("hello")
        .bodyParam[Hello]("hello", "the hello details")
        .responseWith[Unit](200, "the hello is created")
        .responseWith[Unit](500, "internal error")
    }) { hello: Hello =>
    response.ok.json(hello).toFuture
  }

  post("/hellos/paramisreq",
    swagger { o =>
      o.summary("Create a new hello")
        .tag("hello")
        .bodyParam[Hello]("hello", "the hello details")
        .responseWith[Unit](200, "the hello is created")
        .responseWith[Unit](500, "internal error")
    }) { request: Request =>
    val hello = request.contentString
    val helloObj = finatraObjectMapper.parse[Hello](hello)
    println(helloObj)
    response.ok.json(hello).toFuture
  }
}

class HelloFilter @Inject()(responseBuilder: ResponseBuilder) extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val userid = request.params.getOrElse("userid", "")
    if (userid.nonEmpty) {
      responseBuilder.badRequest(Hello("请求非法", 1, 1)).toFuture
    } else {
      service(request)
    }
  }
}