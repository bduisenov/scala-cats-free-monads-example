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

import cats.{Functor, Id, ~>}
import cats.data.{Validated, Xor}
import cats.data.Validated.Valid
import cats.free.Free
import com.lunaryorn.weather.{TemperatureError, TemperatureValidationError}
import com.lunaryorn.weather.free.dsl.types.TemperatureAction
import com.lunaryorn.weather.free.dsl.types.TemperatureAction.{GetAll, Store}
import com.lunaryorn.weather.prop._
import org.scalatest.prop.PropertyChecks
import com.lunaryorn.weather.free.dsl.actions.Temperature._
import org.scalatest.{MustMatchers, WordSpec}
import squants.{Temperature, _}
import squants.thermal.{Celsius, Temperature}

class TemperatureServiceSpec
    extends WordSpec
    with MustMatchers
    with PropertyChecks {

  val toList =
    "The WeatherService" must {

      "get temperatures from the repository" in {
        forAll { temperatures: Seq[Temperature] =>
          val service = new TemperatureService()(Validate.valid)
          val interpreter = new FakeInterpreter(temperatures)
          val result = service.getTemperatures.foldMap(interpreter)

          result mustBe temperatures
          interpreter.dispatchedActions must contain only GetAll
        }
      }

      "stores valid temperatures in the repository" in {
        forAll { temperature: Temperature =>
          val service = new TemperatureService()(Validate.valid)
          val interpreter = new FakeInterpreter(Seq.empty)
          val result = service.addTemperature(temperature).foldMap(interpreter)

          result mustBe Xor.right(temperature)
          interpreter.dispatchedActions must contain only Store(temperature)
          interpreter.storedTemperatures must contain only temperature
        }
      }

      "fails to store invalid temperatures in the repository" in {
        forAll {
          (temperature: Temperature, range: QuantityRange[Temperature]) =>
            val error = TemperatureValidationError.TemperatureOutOfBoundsError(range)
            val service = new TemperatureService()(Validate.invalid(error))
            val interpreter = new FakeInterpreter(Seq.empty)

            val result = service.addTemperature(temperature).foldMap(interpreter)

            result mustBe Xor.left(TemperatureError.InvalidTemperature(error))
            interpreter.dispatchedActions mustBe empty
            interpreter.storedTemperatures mustBe empty
        }
      }
    }
}
