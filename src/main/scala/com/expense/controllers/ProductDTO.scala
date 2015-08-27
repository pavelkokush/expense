package com.expense.controllers

import com.expense.model.Label

case class ProductDTO(name: String, price: String, date: String, labels: List[Label]);
