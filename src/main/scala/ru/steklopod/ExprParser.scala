package ru.steklopod

import scala.util.parsing.combinator._

//Чтобы начать писать парсер - необходимо унаследоваться от RegexParsers.

class ExprParser extends RegexParsers {
  val number = "[1-9][0-9]+".r //TODO - check this

  def expr: Parser[Int] = (number ^^ { _.toInt }) ~ opt(operator ~ expr ) ^^ {
    case a ~ None => a
    case a ~ Some("*" ~ b) => a * b
    case a ~ Some("/" ~ b) => a / b
    case a ~ Some("+" ~ b) => a + b
    case a ~ Some("-" ~ b) => a - b
  }

  def operator: Parser[Any] = "+" | "-" | "*" | "/"
}

object ExprParserTest extends App {
  val parser = new ExprParser
  val result = parser.parseAll(parser.expr, "9*8+21/7")
  println(result.get)
}