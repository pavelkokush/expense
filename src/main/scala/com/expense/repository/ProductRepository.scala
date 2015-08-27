package com.expense.repository

import com.expense.model
import com.expense.model.Label
import com.mongodb.casbah.commons.conversions.scala.{RegisterConversionHelpers, RegisterJodaTimeConversionHelpers}
import com.mongodb.casbah.{MongoClient, MongoCollection}
import org.joda.time.DateTime

import scala.collection.mutable


class ProductRepository {
  RegisterConversionHelpers() // Problem: was missing ()
  RegisterJodaTimeConversionHelpers()

  // Problem: was missing ()

  def getAllProducts(): mutable.Set[model.Product] = {
    import com.mongodb.casbah.Imports._
    val productCol: MongoCollection = getProductCollection

    val products1 = scala.collection.mutable.Set[model.Product]()

    for (product <- productCol.find()) {
      val toList: List[Any] = product.get("labels").asInstanceOf[BasicDBList].toList
      products1.add(new model.Product(
        product.get("name").asInstanceOf[String],
        product.get("price").asInstanceOf[Int],
        product.get("date").asInstanceOf[DateTime],
        product.get("labels").asInstanceOf[BasicDBList].toList.map {
          case g2: BasicDBObject => Label(g2.get("name").asInstanceOf[String])
          case _ => throw new ClassCastException
        }
      )
      )
    }
    products1
  }

  def getAllProducts(from: DateTime, to: DateTime, labels: Set[Label]): mutable.Set[model.Product] = {
    import com.mongodb.casbah.Imports._
    val productCol: MongoCollection = getProductCollection

    val products1 = scala.collection.mutable.Set[model.Product]()
    var q: DBObject = ("date" $gt from $lt to)
//    if (labels != null) {
//      q = q ++ ("labels" $eq  labels)
//    }
    for (product <- productCol.find(q)) {
      val toList: List[Any] = product.get("labels").asInstanceOf[BasicDBList].toList
      products1.add(new model.Product(
        product.get("name").asInstanceOf[String],
        product.get("price").asInstanceOf[Int],
        product.get("date").asInstanceOf[DateTime],
        product.get("labels").asInstanceOf[BasicDBList].toList.map {
          case g2: BasicDBObject => Label(g2.get("name").asInstanceOf[String])
          case _ => throw new ClassCastException
        }
      )
      )
    }
      products1
  }

  def getProductCollection: MongoCollection = {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("expense")
    val collection = db("product")
    collection
  }

  def create(product: model.Product): Unit = {
    import com.mongodb.casbah.Imports._
    val productCol: MongoCollection = getProductCollection

    val labelsBuilder = MongoDBList.newBuilder
    product.labels.foreach(label => labelsBuilder += MongoDBObject("name" -> label.name))
    val labels = labelsBuilder.result

    productCol.insert(MongoDBObject("name" -> product.name,
      "price" -> product.price,
      "labels" -> labels,
      "date" -> product.date))
  }
}
