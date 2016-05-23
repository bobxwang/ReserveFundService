package com.bob.reservefund.scala

import com.bob.reservefund.scala.Service.UserService
import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule

object Modules extends TwitterModule {

  @Singleton
  @Provides
  def providesUserService: UserService = {
    new UserService
  }

}