package com.bob.reservefund.scala

import com.bob.reservefund.scala.Service.UserService
import com.bob.reservefund.scala.Util.EnvironmentContext
import com.google.gson.JsonParser
import com.google.inject.{Provides, Singleton}
import com.twitter.finatra.annotations.Flag
import com.twitter.inject.TwitterModule
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConversions._
import scala.io.Source

object Modules extends TwitterModule {

  val config = ConfigFactory.load()
  val test = config.getString("config.testurl")
  val default = config.getString("config.defaulturl")

  val env = System.getProperty("active.environment", "default")
  val url = env match {
    case "test" => test
    case _ => default
  }

  val jsons = Source.fromURL(url).mkString
  val parser = new JsonParser()
  val jsonobj = parser.parse(jsons).getAsJsonObject.getAsJsonArray("propertySources").get(0).getAsJsonObject.getAsJsonObject("source")
  jsonobj.entrySet().foreach(x => {
    flag(x.getKey, x.getValue.getAsString, "")
    EnvironmentContext.put(x.getKey, x.getValue.getAsString)
  })

  flag("dbusername", "root", "the username of the database")
  flag("active.environment", env, "which environment now is run")

  @Singleton
  @Provides
  def providesUserService(@Flag("dbusername") dbusername: String): UserService = {
    new UserService(dbusername)
  }

}