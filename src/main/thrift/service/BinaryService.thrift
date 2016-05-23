namespace java com.twitter.tweetservice.thriftjava
#@namespace scala com.twitter.tweetservice.thriftscala

service BinaryService {
  binary fetchBlob(1: i64 id)
}