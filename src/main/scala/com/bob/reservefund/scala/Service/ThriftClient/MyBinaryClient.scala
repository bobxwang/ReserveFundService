//package com.bob.reservefund.scala.Service.ThriftClient
//
//import java.nio.ByteBuffer
//import java.nio.charset.Charset
//
//import com.twitter.finagle.service.TimeoutFilter
//import com.twitter.finagle.util.DefaultTimer
//import com.twitter.finagle.{IndividualRequestTimeoutException, Service, SimpleFilter, Thrift}
//import com.twitter.tweetservice.thriftscala.BinaryService
//import com.twitter.tweetservice.thriftscala.BinaryService.FetchBlob
//import com.twitter.util.{Await, Duration, Future}
//
//object MyBinaryClient {
//
//  def main(args: Array[String]) {
//
//    val client = Thrift.newIface[BinaryService[Future]]("127.0.0.1:8080")
//
//    (1 to 10).map(x => {
//      val future = client.fetchBlob(x)
//      Await.result(future)
//      future.map(x => println(Buffer2String(x)))
//    })
//
//    println("\n----------------------")
//    val aclient: BinaryService.ServiceIface = Thrift.newServiceIface[BinaryService.ServiceIface]("127.0.0.1:8080", "aclient")
//    val result: Future[FetchBlob.Result] = aclient.fetchBlob(FetchBlob.Args(12l))
//    Await.result(result)
//    result.map(x => x._1.map(y => println(Buffer2String(y))))
//
//
//    println("\n---------- we will add filter to to client -----------")
//    val add200Filter = new SimpleFilter[FetchBlob.Args, FetchBlob.Result] {
//      def apply(req: FetchBlob.Args, service: Service[FetchBlob.Args, FetchBlob.Result]): Future[FetchBlob.Result] = {
//        val uppercaseRequest = req.copy(id = req._1 + 200)
//        service(uppercaseRequest)
//      }
//    }
//
//    def timeoutFilter[Req, Rep](duration: Duration) = {
//      val exc = new IndividualRequestTimeoutException(duration)
//      val timer = DefaultTimer.twitter
//      new TimeoutFilter[Req, Rep](duration, exc, timer)
//    }
//
//    import com.twitter.conversions.time._
//    val filteredLog = timeoutFilter(2.seconds) andThen add200Filter andThen aclient.fetchBlob
//    Await.result(filteredLog(FetchBlob.Args(100l)))._1.map(x => println(Buffer2String(x)))
//
//    println("invoke done")
//  }
//
//  def Buffer2String(buffer: ByteBuffer): String = {
//    val charset = Charset.forName("UTF-8")
//    val decoder = charset.newDecoder()
//    val charBuffer = decoder.decode(buffer.asReadOnlyBuffer())
//    charBuffer.toString
//  }
//}
