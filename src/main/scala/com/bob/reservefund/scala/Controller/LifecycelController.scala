package com.bob.reservefund.scala.Controller

import com.netflix.appinfo.ApplicationInfoManager
import com.netflix.appinfo.InstanceInfo.InstanceStatus
import com.twitter.finagle.http.Request

/**
 * 控制服务的生命
 */
@Singleton
class LifecycleController extends FinatraController {

  post("/pause", swagger(o => {
    o.summary("暂停服务")
      .description("注册到Eureka,但不参与后续请求处理")
      .tag("服务治理")
  })) { request: Request =>
    ApplicationInfoManager.getInstance.setInstanceStatus(InstanceStatus.DOWN)
    val builder = response.status(200)
    val map = Map("msg" -> "the service is down")
    builder.json(map)
  }

  post("/restart", swagger(o => {
    o.summary("重启服务")
      .description("注册到Eureka,重新参与后续请求处理")
      .tag("服务治理")
  })) { request: Request =>
    ApplicationInfoManager.getInstance.setInstanceStatus(InstanceStatus.UP)
    val builder = response.status(200)
    val map = Map("msg" -> "the service is up")
    builder.json(map)
  }
}