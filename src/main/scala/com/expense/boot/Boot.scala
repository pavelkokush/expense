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

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[LabelController], "demo-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8083)



  val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("students")
  val collection = db("grades")

}