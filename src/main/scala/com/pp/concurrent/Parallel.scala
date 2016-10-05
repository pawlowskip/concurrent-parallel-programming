package com.pp.concurrent

import scala.collection.GenIterable

/**
  * Created by przemek on 28/09/16.
  */
object Parallel extends App {
  def sumOfPowers(collection: GenIterable[Double]): Double = {
    collection.aggregate(0.0)(_ + Math.pow(_, 2), _ + _)
  }
}
