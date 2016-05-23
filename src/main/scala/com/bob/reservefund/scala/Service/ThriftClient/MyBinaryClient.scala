package com.bob.reservefund.scala.Service.ThriftClient

import java.nio.ByteBuffer
import java.nio.charset.Charset

import com.twitter.finagle.Thrift
import com.twitter.tweetservice.thriftscala.BinaryService
import com.twitter.util.{Await, Future}

object MyBinaryClient {

  def Buffer2String(buffer: ByteBuffer): String = {
    val charset = Charset.forName("UTF-8")
    val decoder = charset.newDecoder()
    val charBuffer = decoder.decode(buffer.asReadOnlyBuffer())
    charBuffer.toString
  }

  def main(args: Array[String]) {

    val client = Thrift.newIface[BinaryService[Future]]("127.0.0.1:8080")

    (1 to 10).map(x => {
      val future = client.fetchBlob(x)
      Await.result(future)
      future.map(x => println(Buffer2String(x)))
    })

    println("invoke done")
  }
}
