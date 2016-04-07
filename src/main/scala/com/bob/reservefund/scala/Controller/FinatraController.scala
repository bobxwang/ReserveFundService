package com.bob.reservefund.scala.Controller

import com.bob.reservefund.scala.CustomerSwagger
import com.github.xiaodongw.swagger.finatra.SwaggerSupport
import com.twitter.finatra.http.Controller

trait FinatraController extends Controller with SwaggerSupport {

  override implicit protected val swagger = CustomerSwagger
}