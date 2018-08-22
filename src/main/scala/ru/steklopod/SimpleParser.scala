package ru.steklopod

import scala.util.parsing.combinator.RegexParsers

class SimpleParser extends RegexParsers {
  def word: Parser[String] = """[a-z]+""".r ^^ { _.toString }

}

object SimpleParserTest extends App {

}