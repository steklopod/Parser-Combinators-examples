package ru.steklopod

import scala.util.parsing.combinator._

class SimpleParser extends RegexParsers {
  def word:   Parser[String]   = """[а-я]+""".r       ^^ { _.toString }
  def number: Parser[Int]      = """(0|[1-9]\d*)""".r ^^ { _.toInt }
  def freq:   Parser[WordFreq] = word ~ number        ^^ { case wd ~ fr => WordFreq(wd,fr) }
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


object TestSimpleParser extends SimpleParser with App{
    parse(freq, "дима 121") match {
      case Success(matched,_) => println(matched)
      case Failure(msg,_) => println("FAILURE: " + msg)
      case Error(msg,_) => println("ERROR: " + msg)
    }

}
