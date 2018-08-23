package ru.steklopod.calculator

import scala.util.parsing.combinator.JavaTokenParsers

class ReversePolishCalculator extends JavaTokenParsers {
  def num: Parser[Float] = floatingPointNumber ^^ (_.toFloat)

  def operator: Parser[(Float, Float) => Float] = ("*" | "/" | "+" | "-") ^^ {
    case "+" => (x, y) => x + y
    case "-" => (x, y) => x - y
    case "*" => (x, y) => x * y
    case "/" => (x, y) => if (y > 0) x / y else 0F
  }

}

object Calculator extends ReversePolishCalculator with App {
    val result = parseAll(num, "123.09")
    println(s"Parsed $result")
}