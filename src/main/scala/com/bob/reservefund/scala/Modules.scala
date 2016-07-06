package com.bob.reservefund.scala

import com.bob.reservefund.scala.Service.UserService
import com.google.gson.JsonParser
import com.google.inject.{Provides, Singleton}
import com.twitter.finatra.annotations.Flag
import com.twitter.inject.TwitterModule

import scala.collection.JavaConversions._

import scala.io.Source

object Modules extends TwitterModule {

  flag("dbusername", "root", "the username of the database")

  val jsons = Source.fromURL("http://financeconfig.51.nb:8080/financeaccountservice/default").mkString
  val parser = new JsonParser()
  val jsonobj = parser.parse(jsons).getAsJsonObject.getAsJsonArray("propertySources").get(0).getAsJsonObject.getAsJsonObject("source")
  jsonobj.entrySet().foreach(x => {
    flag(x.getKey, x.getValue.getAsString, "")
  })

  @Singleton
  @Provides
  def providesUserService(@Flag("dbusername") dbusername: String): UserService = {
    new UserService(dbusername)
  }

}