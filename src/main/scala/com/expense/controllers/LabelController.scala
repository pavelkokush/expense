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

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class LabelController extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)
}


// this trait defines our service behavior independently from the service actor
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
      path("products") {
        post {
          entity(as[ProductDTO]) { productDTO =>
            complete {
              val productService = new ProductService
              val format = ISODateTimeFormat.date()
              //    def write(datetime: DateTime): JsValue = JsString(format.print(datetime.withZone(DateTimeZone.UTC)))
              //    def read(json: JsValue): DateTime = json match {
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
          parameters("from", "to", "labels" ? "all") { (from, to, labels) =>
            val productService = new ProductService
            val format = ISODateTimeFormat.date()
            val allProducts: mutable.Set[model.Product] = productService.getAllProducts1(
              format.parseDateTime(from), format.parseDateTime(to), Set(Label(labels)))
            val price: Int = productService.getAllProductsPrice(
              format.parseDateTime(from), format.parseDateTime(to), Set(Label(labels)))
            val map: mutable.Set[ProductDTO] = allProducts.map(a => ProductDTO(a.name, a.price.toString(), format.print(a.date), a.labels))

            var s  =  ""
            map.foreach(p => s+="<br/>"+p.name+"  "+p.price+"  "+p.date+"  "+p.labels+"<br/>")
            s+="</br></br>Total:"+ price

            respondWithMediaType(MediaType.custom("text/html")) {
              complete {
                s
              }
            }
          }
        }
      } ~
      path("products1") {
        get {
          parameters("callback", "page", "row_count", "col_name", "direction") { (callback, page, row_count, col_name, direction) =>
            val productService = new ProductService
            val format = ISODateTimeFormat.date()
//            val allProducts: mutable.Set[model.Product] = productService.getAllProducts1(
//              format.parseDateTime(from), format.parseDateTime(to), Set(Label(labels)))
//            val price: Int = productService.getAllProductsPrice(
//              format.parseDateTime(from), format.parseDateTime(to), Set(Label(labels)))
//            val map: mutable.Set[ProductDTO] = allProducts.map(a => ProductDTO(a.name, a.price.toString(), format.print(a.date), a.labels))

            var s  =  ""
//            map.foreach(p => s+="<br/>"+p.name+"  "+p.price+"  "+p.date+"  "+p.labels+"<br/>")
//            s+="</br></br>Total:"+ price

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