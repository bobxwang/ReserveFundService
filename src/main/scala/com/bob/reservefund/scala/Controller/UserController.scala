package com.bob.reservefund.scala.Controller

import com.bob.reservefund.scala.Service.UserService
import com.bob.reservefund.scala.Util.EnvironmentContext
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.annotations.Flag
import com.twitter.finatra.http.request.RequestUtils

import com.bob.reservefund.scala.Filter.WhichUserLoginContext._

case class User(name: String, id: Int, age: Int)

@Singleton
class UserController @Inject()(userService: UserService, @Flag("active.environment") env: String) extends FinatraController {

  get("/users", swagger(o => {
    o.summary("get all users")
      .description("return all users item")
      .tag("user")
      .produces("application/json")
      .responseWith[List[User]](200, "the response json", example = Some(List(User("bb", 1, 2))))
      .responseWith[Unit](404, "the address is not found")
  })) { request: Request =>
    println(request.user.id)
    info("users")
    List(User(env, -1, -1), User(RequestUtils.pathUrl(request), 1, 1), Hello(userService.dbusername, 2, 2), Hello(EnvironmentContext.get("mqconfig.maxTryTimes", "fuck"), 3, 1))
  }

  get("/properties", swagger(o => {
    o.summary("get all properties")
      .description("return all properties item")
      .tag("user")
      .produces("application/json")
      .responseWith[List[User]](200, "the response json", example = Some(List(User("bb", 1, 2))))
      .responseWith[Unit](404, "the address is not found")
  })) { request: Request =>
    EnvironmentContext.toMap
  }
}