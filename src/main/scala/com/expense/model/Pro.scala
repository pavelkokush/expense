package com.expense.model

import org.joda.time.DateTime


case class Pro(name: String, price: Int, date: DateTime, labels: List[Label]);
