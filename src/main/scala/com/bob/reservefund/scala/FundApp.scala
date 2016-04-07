package com.bob.reservefund.scala

import com.bob.reservefund.scala.Controller.{HelloController, HelloFilter}
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.JsonGenerator.Feature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy
import com.github.xiaodongw.swagger.finatra.SwaggerController
import com.twitter.finagle.http.Request
import com.twitter.finagle.http.filter.JsonpFilter
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.finatra.json.utils.CamelCasePropertyNamingStrategy
import io.swagger.models.auth.BasicAuthDefinition
import io.swagger.models.{Contact, Info, Swagger}
import io.swagger.util.Json

object CustomerSwagger extends Swagger {
  Json.mapper().setPropertyNamingStrategy(new LowerCaseWithUnderscoresStrategy)
}

object CustomJacksonModule extends FinatraJacksonModule {

  override val serializationInclusion = Include.NON_EMPTY

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

  /**
   * 也可以在运行进通过 -http.port 进行指定
   */
  override val defaultFinatraHttpPort: String = ":8080"

  override def jacksonModule = CustomJacksonModule

  override protected def configureHttp(router: HttpRouter): Unit = {

    router
      //      .filter[LoggingMDCFilter[Request, Response]]
      //      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[JsonpFilter[Request]]
      .filter[CommonFilters] // global filter
      .add(swaggerController)
      .add[HelloFilter, HelloController] // per-controller filter
  }
}