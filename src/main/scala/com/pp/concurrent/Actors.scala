package com.pp.concurrent

import akka.actor.{Actor, ActorRef, ActorSystem, Kill, Props}
import akka.pattern.ask
import com.pp.concurrent.Actors.Protocol.{Result, StartCalculation, SumOfPowers}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by przemek on 05/10/16.
  */
object Actors {

  def initializeCalc(workers: Int): ActorRef = {
    require(workers > 1)
    val actorSystem = ActorSystem("CalculatorActorSystem")
    actorSystem.actorOf(Calculator.props(workers), "calc")
  }

  def sumOfPowers(actorRef: ActorRef, collection: Iterable[Double], workers: Int): Double = {
    val future = actorRef.ask(SumOfPowers(collection))(2 minutes).mapTo[Double]
    Await.result(future, 2 minutes)
  }

  def shutdown(actorRef: ActorRef) = {
    actorRef ! Kill
  }

  object Protocol {
    case class SumOfPowers(numbers: Iterable[Double])
    case object StartCalculation
    case class Result(res: Double)
  }

  object Calculator {
    def props(workerCount: Int): Props = Props(new Calculator(workerCount))
  }

  class Calculator(workerCount: Int) extends Actor {

    override def receive: Receive = {
      case SumOfPowers(numbers) =>
        numbers
          .sliding(numbers.size / workerCount)
          .map(nums => context.actorOf(Worker.props(nums)))
          .foreach(worker => worker ! StartCalculation)
        context.become(calculating(0.0, workerCount, sender()))
    }

    def calculating(acc: Double, remaining: Int, client: ActorRef): Receive = {
      case Result(value) =>
        if (remaining > 1)
          context.become(calculating(acc + value, remaining - 1, client))
        else {
          client ! Result(acc + value)
        }
    }

  }

  object Worker {
    def props(numbers: Iterable[Double]): Props = Props(new Worker(numbers))
  }

  class Worker(numbers: Iterable[Double]) extends Actor {
    def calculate(): Double = {
      var sum = 0.0
      for(d <- numbers.iterator) {
        sum += d
      }
      sum
    }

    override def receive: Receive = {
      case StartCalculation =>
        sender() ! Result(calculate())
    }
  }


}
