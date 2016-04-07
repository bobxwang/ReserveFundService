package com.bob.reservefund.scala

import com.bob.reservefund.scala.Controller.{HelloFilter, HelloController}
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.JsonGenerator.Feature
import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.finagle.http.filter.JsonpFilter
import com.twitter.finagle.http.{Response, Request}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, TraceIdMDCFilter, LoggingMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.finatra.json.utils.CamelCasePropertyNamingStrategy

object FundApp extends HttpServer {

  /**
   * 也可以在运行进通过 -http.port 进行指定
   */
  override val defaultFinatraHttpPort: String = ":8080"

  override def jacksonModule = CustomJacksonModule

  override protected def configureHttp(router: HttpRouter): Unit = {

    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[JsonpFilter[Request]]
      .filter[CommonFilters] // global filter
      .add[HelloFilter, HelloController] // per-controller filter
  }
}

object CustomJacksonModule extends FinatraJacksonModule {

  override val serializationInclusion = Include.NON_EMPTY

  override val propertyNamingStrategy = CamelCasePropertyNamingStrategy

  override def additionalMapperConfiguration(mapper: ObjectMapper) {
    mapper.configure(Feature.WRITE_NUMBERS_AS_STRINGS, true)
  }
}