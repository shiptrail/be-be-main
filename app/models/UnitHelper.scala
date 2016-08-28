package models

import io.github.karols.units.{MUnit, _}
import play.api.libs.functional.syntax._
import play.api.libs.json.Format

object UnitHelper {

  type _2[T <: MUnit] = square[T]
  type ^[B <: MUnit, E[_ <: MUnit]] = E[B]

  implicit def IntUFormat[T <: MUnit]: Format[IntU[T]] =
    implicitly[Format[Long]].inmap(_.of[T], _.value)
  implicit def DoubleUFormat[T <: MUnit]: Format[DoubleU[T]] =
    implicitly[Format[Double]].inmap(_.of[T], _.value)
}
