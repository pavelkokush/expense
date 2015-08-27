package com.expense.service

import com.expense.model.Label
import com.expense.repository.LabelRepository

class LabelService {
  val labelRepository = new LabelRepository
  def createLabel(label: Label): Label = {
    labelRepository.createLabel(label)
  }
  def findAllLabels(): Unit = {
    labelRepository.findAllLabels()
  }

  def isLabelExists(name: String): Boolean = {
    labelRepository.findLabel(name).isDefined
  }

}
