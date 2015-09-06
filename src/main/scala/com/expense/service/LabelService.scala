package com.expense.service

import com.expense.model.Label
import com.expense.repository.LabelRepository

class LabelService {
  val labelRepository = new LabelRepository

  def createLabel(label: Label): Label = {
    require(!isLabelExists(label.name), "label " + label.name + " is exists")
    labelRepository.createLabel(label)
  }
  def findAllLabels(): scala.collection.mutable.Set[Label] = {
    labelRepository.findAllLabels()
  }

  def isLabelExists(name: String): Boolean = {
    labelRepository.findLabel(name).isDefined
  }

}
