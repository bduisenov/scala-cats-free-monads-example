/*
 * Copyright 2016 Sebastian Wiesner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
