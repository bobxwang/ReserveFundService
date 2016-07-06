package com.bob.reservefund.scala

import com.bob.reservefund.scala.Controller.{UserController, HelloController, HelloFilter}
import com.bob.reservefund.scala.Filter.WhichUserLoginFilter
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.JsonGenerator.Feature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy
import com.github.xiaodongw.swagger.finatra.SwaggerController
import com.netflix.appinfo.InstanceInfo.InstanceStatus
import com.netflix.appinfo.{ApplicationInfoManager, MyDataCenterInstanceConfig}
import com.netflix.discovery.shared.Applications
import com.netflix.discovery.{DefaultEurekaClientConfig, DiscoveryManager}
import com.twitter.finagle.http.filter.JsonpFilter
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.finatra.json.utils.CamelCasePropertyNamingStrategy
import io.swagger.models.auth.BasicAuthDefinition
import io.swagger.models.{Contact, Info, Swagger}
import io.swagger.util.Json

import scala.collection.JavaConversions._

object CustomerSwagger extends Swagger {
  Json.mapper().setPropertyNamingStrategy(new LowerCaseWithUnderscoresStrategy)
}

object CustomJacksonModule extends FinatraJacksonModule {

  override val serializationInclusion = Include.ALWAYS

  override val propertyNamingStrategy = CamelCasePropertyNamingStrategy

  override def additionalMapperConfiguration(mapper: ObjectMapper) {
    mapper.configure(Feature.WRITE_NUMBERS_AS_STRINGS, true)
  }
}

object FundApp extends HttpServer {

  lazy val info = new Info()
    .description("reserve fund server").version("1.0.0").title("reserve fund")
    .contact(new Contact().name("wangxiang").email("wangx.freesoft@gmail.com"))

  CustomerSwagger.info(info)
    .addSecurityDefinition("sampleBasic", {
    val d = new BasicAuthDefinition
    d.setType("basic")
    d
  })

  val swaggerController = new SwaggerController(swagger = CustomerSwagger)

  initEurekaClient

  stopEurekaClient

  /**
   * if we using kill -9 pid,the below code will not invoke,we should using kill pid instead
   */
  def stopEurekaClient: Unit = {
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      override def run(): Unit = {
        println("app want to close")
        DiscoveryManager.getInstance().shutdownComponent()
        println("app has been closed")
      }
    }))
  }

  def initEurekaClient: Unit = {

    val dataCenterInstanceConfig = new MyDataCenterInstanceConfig();
    val defaultEurekaClientConfig = new DefaultEurekaClientConfig();
    DiscoveryManager.getInstance().initComponent(
      dataCenterInstanceConfig, defaultEurekaClientConfig);
    ApplicationInfoManager.getInstance.setInstanceStatus(InstanceStatus.UP)
    val applications: Applications = DiscoveryManager.getInstance.getDiscoveryClient.getApplications
    applications.getRegisteredApplications().foreach(x => {
      println(x.getName)
    })

    val otherService = "RISK-OPENAPI"
    applications.getRegisteredApplications(otherService).getInstances().foreach(x => {
      println(s"${x.getAppName}, ${x.getPort}, ${x.getHostName}, ${x.getStatus}")
    })
    applications.getRegisteredApplications(otherService).getInstances.foreach(x => {
      println(s"${x.getAppName}, ${x.getPort}, ${x.getHostName}, ${x.getStatus}")
    })
    (1 to 10).foreach(x => {
      println(s"the ${x} times to invoke\n")
      val nextServerInfo = DiscoveryManager.getInstance().getDiscoveryClient.getNextServerFromEureka(otherService, false)
      println(s"Found an instance of example service to talk to from eureka: ${nextServerInfo.getIPAddr}, ${nextServerInfo.getVIPAddress()} : ${nextServerInfo.getPort()}")
    })
  }

  /**
   * 也可以在运行进通过 -http.port 进行指定
   */
  override val defaultFinatraHttpPort: String = ":8080"

  override def jacksonModule = CustomJacksonModule

  override protected def configureHttp(router: HttpRouter): Unit = {

    println("invoke configure http")

    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[JsonpFilter[Request]]
      .filter[CommonFilters] // global filter
      .add(swaggerController)
      .add[HelloFilter, HelloController] // per-controller filter
      .add[WhichUserLoginFilter, UserController]
      .exceptionMapper[MExceptionMapper]
  }

  override val modules = Seq(
    Modules
  )
}