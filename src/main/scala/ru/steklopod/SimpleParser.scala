package ru.steklopod

import scala.util.parsing.combinator._

class SimpleParser extends RegexParsers {
  def word: Parser[String] = """[a-z]+""".r ^^ { _.toString }

}

object SimpleParserTest extends SimpleParser with App {
  println(parse(word, "johnny come lately"))
}