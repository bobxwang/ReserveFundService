package com.bob.reservefund.scala.Filter

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Response, Request}
import com.twitter.util.Future

case class WhichUserLogin(id: Long)

object WhichUserLoginContext {
  private val UserField = Request.Schema.newField[WhichUserLogin]()

  /**
   * 相当于扩展request类，让它有个user的方法
   * @param request
   */
  implicit class UserContextSyntax(val request: Request) extends AnyVal {
    def user: WhichUserLogin = request.ctx(UserField)
  }

  def setUser(request: Request): Unit = {
    //Parse user from request headers/cookies/etc.
    val user = WhichUserLogin(1)
    request.ctx.update(UserField, user)
  }
}

/**
 * 需要验证登陆用户的的Filter
 */
class WhichUserLoginFilter extends SimpleFilter[Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {

    WhichUserLoginContext.setUser(request)
    service(request)
  }
}
