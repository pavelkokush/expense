package com.expense.repository

import com.expense.model
import com.expense.model.Label
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.{MongoCollection, MongoClient}

import scala.collection.immutable.Stream.Empty

class LabelRepository {

  def findAllLabels(): scala.collection.mutable.Set[Label] = {
    val labelsCol: MongoCollection = getLabelCollection
    val labels = scala.collection.mutable.Set[model.Label]()
    for (label <- labelsCol.find()) {
      labels.add(Label(label.get("name").asInstanceOf[String]))
    }
    labels
  }

  def findLabel(name: String): Option[Label] = {
    val labelsCol: MongoCollection = getLabelCollection
    val query: DBObject = "name" $eq name
    val find: MongoCollection#CursorType = labelsCol.find(query)
    require(find.size < 2, "more than one label")
    if (find.isEmpty){
      return Option.empty[Label]
    }
    Option.apply(Label(find.next().get("name").asInstanceOf[String]))
  }


  def createLabel(label: Label): Label = {
    import com.mongodb.casbah.Imports._
    val labelsCol: MongoCollection = getLabelCollection

    labelsCol.insert(MongoDBObject("name" -> label.name))
    val savedLabel = labelsCol.findOne(MongoDBObject("name" -> label.name))
    val sLabel = new Label(savedLabel.get.get("name").toString);
    sLabel
  }

  def getLabelCollection: MongoCollection = {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("expense")
    val collection = db("label")
    collection
  }


}
