package ru.steklopod

import scala.util.parsing.combinator._

class SimpleParser extends RegexParsers {
  def word: Parser[String] =
    """[а-я]+""".r ^^ {
      _.toString
    }
}

object SimpleParserTest extends SimpleParser with App {
  parse(word, "дима придет поздно") match {
    case Success(matched, _) => println(matched)
    case Failure(msg, _) => println("FAILURE: " + msg)
    case Error(msg, _) => println("ERROR: " + msg)
  }
}

case class WordFreq(word: String, count: Int) {
  override def toString = "Слово <" + word + "> " + "встречается с частотой " + count
}


