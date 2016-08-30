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
