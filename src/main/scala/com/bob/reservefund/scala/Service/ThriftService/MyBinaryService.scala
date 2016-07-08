//package com.bob.reservefund.scala.Service.ThriftService
//
//import java.nio.ByteBuffer
//
//import com.twitter.finagle.{ListeningServer, Thrift}
//import com.twitter.tweetservice.thriftscala.BinaryService
//import com.twitter.util.{Await, Future}
//
//object MyBinaryService extends BinaryService[Future] {
//
//  override def fetchBlob(id: Long): Future[ByteBuffer] = Future.value(ByteBuffer.wrap(s"fuck u ${id} times".getBytes))
//
//  def main(args: Array[String]) {
//
//    // this is we implementing the service interface directly
//    val port = 8080
//    val service: ListeningServer = Thrift.serveIface(s"127.0.0.1:${port}", MyBinaryService)
//    Await.result(service)
//  }
//}