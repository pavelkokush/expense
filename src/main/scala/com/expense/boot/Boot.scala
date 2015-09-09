package com.expense.boot


import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.expense.controllers.LabelController
import com.mongodb.casbah.MongoClient
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Boot extends App {
  implicit val system = ActorSystem("on-spray-can")
  val service = system.actorOf(Props[LabelController], "demo-service")
  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8083)
}