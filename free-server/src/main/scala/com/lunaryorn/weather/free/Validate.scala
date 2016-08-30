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

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import com.lunaryorn.weather.{TemperatureValidationError, TemperatureValidator}
import squants.Temperature

import scala.language.implicitConversions

trait Validate[E, T] {
  def validate(t: T): Validated[E, T]
}

object Validate {
  implicit def fromTemperatureValidator(v: TemperatureValidator)
    : Validate[TemperatureValidationError, Temperature] =
    new Validate[TemperatureValidationError, Temperature] {
      override def validate(
          t: Temperature
      ): Validated[TemperatureValidationError, Temperature] = v.validate(t)
    }

  def valid[E, T]: Validate[E, T] = new Validate[E, T] {
    override def validate(t: T): Validated[E, T] = Valid(t)
  }

  def invalid[E, T](error: E): Validate[E, T] = new Validate[E, T] {
    override def validate(t: T): Validated[E, T] = Invalid(error)
  }
}
