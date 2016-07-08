package com.bob.reservefund.scala.Filter

import javax.inject.Inject

import com.twitter.finagle.filter.LogFormatter
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.inject.Logging
import com.twitter.util.{Stopwatch, Future}

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

/**
 * 通过此可以记录请求入参出参
 * @param logFormatter
 * @tparam R
 */
class RequestAopFilter[R <: Request] @Inject()(
                                                logFormatter: LogFormatter[R, Response])
  extends SimpleFilter[R, Response] with Logging {

  override def apply(request: R, service: Service[R, Response]): Future[Response] = {
    if (!isInfoEnabled) {
      service(request)
    }
    else {
      val elapsed = Stopwatch.start()
      service(request) onSuccess { response =>
        info(response.contentString)
      } onFailure { e =>
        // should never get here since this filter is meant to be after the exception barrier
        info(logFormatter.formatException(request, e, elapsed()))
      }
    }
  }

}