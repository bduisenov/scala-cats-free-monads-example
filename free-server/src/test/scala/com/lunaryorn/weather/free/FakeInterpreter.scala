package com.lunaryorn.weather.free

import cats.{Id, ~>}
import squants.Temperature
import com.lunaryorn.weather.free.dsl.types.TemperatureAction
import com.lunaryorn.weather.free.dsl.types.TemperatureAction.{GetAll, Store}
import scala.collection.mutable

class FakeInterpreter(temperatures: Seq[Temperature]) extends (TemperatureAction ~> Id) {

  val storedTemperatures = new mutable.Stack[Temperature]
  val dispatchedActions = new mutable.Stack[TemperatureAction[_]]

  override def apply[A](actions: TemperatureAction[A]): Id[A] = {
    dispatchedActions.push(actions)
    actions match {
      case GetAll => temperatures
      case Store(temperature) =>
        storedTemperatures.push(temperature)
        temperature
    }
  }
}
