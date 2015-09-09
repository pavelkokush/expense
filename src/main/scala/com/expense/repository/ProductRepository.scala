package com.expense.repository

import com.expense.model
import com.expense.model.Label
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala.{RegisterConversionHelpers, RegisterJodaTimeConversionHelpers}
import com.mongodb.casbah.{Imports, MongoClient, MongoCollection}
import org.joda.time.DateTime

class ProductRepository {
  val productCol: MongoCollection = getProductCollection

  RegisterConversionHelpers()
  RegisterJodaTimeConversionHelpers()

  def getAllProducts(): Set[model.Product] = {
    toProducts(productCol.find())
  }

  def getAllProducts(from: DateTime, to: DateTime, labels: Option[Set[Label]]): Set[model.Product] = {
    var query: DBObject = "date" $gt from $lt to
    labels match {
      case Some(labels) => query = query ++ ("labels" $all labels.map(l => MongoDBObject("name" -> l.name)))
      case None =>
    }

    toProducts(productCol.find(query))
  }

  def create(product: model.Product): Unit = {
    val labelsBuilder = MongoDBList.newBuilder
    product.labels.foreach(label => labelsBuilder += MongoDBObject("name" -> label.name))
    val labels = labelsBuilder.result

    productCol.insert(MongoDBObject("name" -> product.name,
      "price" -> product.price,
      "labels" -> labels,
      "date" -> product.date))
  }

  private def toProducts(cursor: Imports.MongoCollection#CursorType): Set[model.Product] = {
    cursor.map(productRow => new model.Product(
      productRow.get("name").asInstanceOf[String],
      productRow.get("price").asInstanceOf[Int],
      productRow.get("date").asInstanceOf[DateTime],
      productRow.get("labels").asInstanceOf[BasicDBList].toList.map {
        case g2: BasicDBObject => Label(g2.get("name").asInstanceOf[String])
        case _ => throw new scala.ClassCastException
      }
    )).toSet
  }

  private def getProductCollection: MongoCollection = {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("expense")
    db("product")
  }
}
