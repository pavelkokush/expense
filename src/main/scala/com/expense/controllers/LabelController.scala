package com.expense.controllers

import akka.actor.Actor
import com.expense.model
import com.expense.model.{Label, Product}
import com.expense.service.{LabelService, ProductService}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.json4s.{DefaultFormats, Formats}
import spray.http.MediaType
import spray.httpx.{Json4sSupport, SprayJsonSupport}
import spray.json._
import spray.routing._

import scala.collection.mutable

object LabelJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val LabelFormats = jsonFormat1(model.Label)
}

object ProductJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  import LabelJsonSupport._

  implicit val ProductFormats = jsonFormat4(ProductDTO)
}

object Json4sProtocol extends Json4sSupport {
  implicit def json4sFormats: Formats = DefaultFormats
}

class LabelController extends Actor with ExpenseService {

  def actorRefFactory = context

  def receive = runRoute(route)
}


trait ExpenseService extends HttpService {

  import Json4sProtocol._

  val labelService = new LabelService
  val productService = new ProductService
  val format = ISODateTimeFormat.date()

  val route = {
    path("labels") {
      post {
        entity(as[Label]) { label =>
          complete {
            labelService.createLabel(label)
          }
        }
      }
    } ~
      path("labels") {
        get {
          respondWithMediaType(MediaType.custom("text/html")) {
            complete {
              labelService.findAllLabels().map(l => l.name).mkString("<br/>")
            }
          }
        }
      } ~
      path("products") {
        post {
          entity(as[ProductDTO]) { productDTO =>
            val date = format.parseDateTime(productDTO.date)

            val product = Product(productDTO.name, productDTO.price.replace(" ", "").toInt, date, productDTO.labels)
            productService.createProduct(product)
            complete {
              productService.getAllProducts().map(p => ProductDTO(p.name, p.price.toString, format.print(p.date), p.labels));
            }
          }
        }
      } ~
      path("products") {
        get {
          parameters("from", "to", "labels" ? "") { (from, to, labelsStr) =>
            var labels: Option[Set[Label]] = None
            if (labelsStr != "") {
              labels = Some(labelsStr.split(",").map(l => Label(l)).toSet)
            }

            val fromDate = format.parseDateTime(from)
            val toDate = format.parseDateTime(to)

            val allProducts = productService.getAllProducts(fromDate, toDate, labels)
            val totalPrice = productService.getAllProductsPrice(fromDate, toDate, labels)

            val productDTOs = allProducts.map(a => ProductDTO(a.name, a.price.toString, format.print(a.date), a.labels))
            respondWithMediaType(MediaType.custom("text/html")) {
              complete {
                var result = ""
                productDTOs.foreach(p => {
                  val string: String = p.labels.map(p => p.name).mkString(", ")
                  result += "<br/>" + p.name + "    " + p.price + "     " + p.date + "     " + string + "<br/>"
                })
                result += "</br></br>Total:" + totalPrice
                result
              }
            }
          }
        }
      }
  }
}