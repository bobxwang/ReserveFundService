package com.bob.reservefund.scala.Util

import java.util.Properties

import scala.collection.JavaConversions._

/**
 * 应用环境信息
 */
object EnvironmentContext {

  private val properties = new Properties()

  def put(key: String, value: String) = {
    properties.put(key, value)
  }

  def get(key: String) = {
    properties.getProperty(key)
  }

  def get(key: String, defaultValue: String) = {
    properties.getProperty(key, defaultValue)
  }

  def toMap: Map[String, String] = {
    properties.toMap
  }

}