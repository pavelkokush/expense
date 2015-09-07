package com.expense.controllers

import akka.actor.Actor
import com.expense.model
import com.expense.model.{Label, Product}
import com.expense.service.{LabelService, ProductService}
import org.joda.time.format.ISODateTimeFormat
import org.json4s.{DefaultFormats, Formats}
import spray.http.MediaType
import spray.httpx.{Json4sSupport, SprayJsonSupport}
import spray.json._
import spray.routing._

import scala.collection.mutable

object PersonJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val PortofolioFormats = jsonFormat1(model.Label)
}

object Person2JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  import PersonJsonSupport._

  implicit val PortofolioFormats1 = jsonFormat4(ProductDTO)
}

object Json4sProtocol extends Json4sSupport {
  implicit def json4sFormats: Formats = DefaultFormats
}

class LabelController extends Actor with MyService {

  def actorRefFactory = context

  def receive = runRoute(route)
}


trait MyService extends HttpService {

  import Json4sProtocol._

  val route = {
    path("labels") {
      post {
        entity(as[Label]) { label =>
          complete {
            val labelService = new LabelService
            labelService.createLabel(label)
          }
        }
      }
    } ~
      path("labels") {
        get {

          val labelService = new LabelService
          val allLabels: mutable.Set[Label] = labelService.findAllLabels()
          val string: String = allLabels.map(l=>l.name).mkString("<br/>")

          respondWithMediaType(MediaType.custom("text/html")) {
            complete {
              string
            }
          }
        }
      } ~
      path("products") {
        post {
          entity(as[ProductDTO]) { productDTO =>
            complete {
              val productService = new ProductService
              val format = ISODateTimeFormat.date()
              val time1 = format.parseDateTime(productDTO.date)

              val product = Product(productDTO.name, productDTO.price.replace(" ", "").toInt, time1, productDTO.labels)
              productService.createProduct(product)
              productService.getAllProducts().map(a => ProductDTO(a.name, a.price.toString, format.print(a.date), a.labels));
            }
          }
        }
      } ~
      path("products") {
        get {
          parameters("from", "to", "labels" ? "") { (from, to, labelsStr) =>
            val productService = new ProductService
            val format = ISODateTimeFormat.date()
            var toSet:Option[Set[Label]] = None
            if (labelsStr != ""){
              toSet = Some(labelsStr.split(",").map(l => Label(l)).toSet)
            }
            val allProducts: Set[model.Product] = productService.getAllProducts(
              format.parseDateTime(from), format.parseDateTime(to), toSet)
            val price: Int = productService.getAllProductsPrice(
              format.parseDateTime(from), format.parseDateTime(to), toSet)
            val map: Set[ProductDTO] = allProducts.map(a => ProductDTO(a.name, a.price.toString(), format.print(a.date), a.labels))

            var s = ""
            map.foreach(p => {
              val string: String = p.labels.map(p => p.name).mkString(", ")
              s += "<br/>" + p.name + "    " + p.price + "     " + p.date + "     " + string + "<br/>"
            })
            s += "</br></br>Total:" + price

            respondWithMediaType(MediaType.custom("text/html")) {
              complete {
                s
              }
            }
          }
        }
      }
  }
}