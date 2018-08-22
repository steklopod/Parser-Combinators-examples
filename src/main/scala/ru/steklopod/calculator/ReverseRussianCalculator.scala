package ru.steklopod.calculator

import scala.util.parsing.combinator.JavaTokenParsers

class ReverseRussianCalculator extends JavaTokenParsers {
  def num: Parser[Float] = floatingPointNumber ^^ (_.toFloat)

}