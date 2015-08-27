package com.expense.model


import org.joda.time.DateTime

case class Product(name: String, price: Int, date: DateTime, labels: List[Label]);
