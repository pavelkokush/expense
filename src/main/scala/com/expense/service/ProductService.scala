package com.expense.service

import com.expense.model
import com.expense.model.Label
import com.expense.repository.ProductRepository
import org.joda.time.DateTime

import scala.collection.mutable

class ProductService {

  val labelService = new LabelService
  val productRepository = new ProductRepository

  def createProduct(product: model.Product): Unit = {
    product.labels.foreach(l => assume(labelService.isLabelExists(l.name), "label " + l.name + " not exists"))
    productRepository.create(product)
  }

  def getAllProducts(): Set[model.Product] = {
    productRepository.getAllProducts()
  }

  def getAllProducts(from: DateTime, to: DateTime, labels: Option[Set[Label]]): Set[model.Product] = {
    productRepository.getAllProducts(from, to, labels)
  }

  def getAllProductsPrice(from: DateTime, to: DateTime, labels: Option[Set[Label]]): Int = {
    productRepository.getAllProducts(from, to, labels).toList.map(p => p.price).sum
  }
}
