package com.expense.repository

import com.expense.model.Label
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.{MongoClient, MongoCollection}

class LabelRepository {
  val labelsCol: MongoCollection = getLabelCollection

  def findAllLabels(): Set[Label] = {
    labelsCol.find()
      .map(labelRow => Label(labelRow.get("name").asInstanceOf[String]))
      .toSet
  }

  def findLabel(name: String): Option[Label] = {
    val cursor = labelsCol.find("name" $eq name)
    require(cursor.size < 2, "more than one label")

    if (cursor.isEmpty) {
      return Option.empty[Label]
    }

    val labelRow: LabelRepository.this.labelsCol.CursorType#T = cursor.next()
    Option.apply(Label(labelRow.get("name").asInstanceOf[String]))
  }

  def createLabel(label: Label): Label = {
    import com.mongodb.casbah.Imports._

    labelsCol.insert(MongoDBObject("name" -> label.name))
    findLabel(label.name).get
  }

  private def getLabelCollection: MongoCollection = {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("expense")
    db("label")
  }
}
