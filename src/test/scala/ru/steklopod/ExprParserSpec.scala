package ru.steklopod

import org.scalatest.{FunSuite, Matchers}

class ExprParserSpec extends FunSuite with Matchers {

  test("init") {
    val parser = new ExprParser
    val result = parser.parseAll(parser.expr, "9*8+21/7")
    println(result.get)
  }
}
