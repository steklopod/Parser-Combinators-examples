package ru.steklopod

import scala.util.parsing.combinator.RegexParsers

class ExprParser extends RegexParsers {
  val number = """(0|[1-9]\d*)""".r

  def expr: Parser[Int] = (number ^^ { _.toInt }) ~ opt(operator ~ expr ) ^^ {
    case a ~ None => a
    case a ~ Some("*" ~ b) =>  {println("Умнож.: a: " + a + ", b: " + b + "; a * b = " + (a * b) ); a * b}
    case a ~ Some("/" ~ b) =>  {println("Делен.: a: " + a + ", b: " + b + "; a / b = " + (a / b) ); a / b}
    case a ~ Some("+" ~ b) =>  {println("Слож. : a: " + a + ", b: " + b + "; a + b = " + (a + b) ); a + b}
    case a ~ Some("-" ~ b) =>  {println("Вычит.: a: " + a + ", b: " + b + "; a - b = " + (a - b) ); a - b}
  }

  def operator: Parser[Any] = "+" | "-" | "*" | "/"
}

object ExprParserTest extends App {
  val parser = new ExprParser
  val res = parser.parseAll(parser.expr, "9*8+21/7")
  println(res.get)
}
