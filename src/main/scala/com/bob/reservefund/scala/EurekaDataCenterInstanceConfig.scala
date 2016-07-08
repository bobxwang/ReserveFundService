package com.bob.reservefund.scala

import java.net.InetAddress

import com.netflix.appinfo.MyDataCenterInstanceConfig

/**
 * 服务注册到Eureka的时候以IP形式而不是域名形式进行注册
 */
class EurekaDataCenterInstanceConfig extends MyDataCenterInstanceConfig {

  /**
   * 由域名改成IP
   * @param refresh
   * @return
   */
  override def getHostName(refresh: Boolean): String = InetAddress.getLocalHost.getHostAddress

  override def getStatusPageUrlPath: String = "/info"

  override def getHealthCheckUrlPath: String = "/health"
}