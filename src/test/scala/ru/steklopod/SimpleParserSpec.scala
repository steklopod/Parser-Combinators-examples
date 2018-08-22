package ru.steklopod

import org.scalatest.{FunSuite, Matchers}
import ru.steklopod.basics.SimpleParser

class SimpleParserSpec extends FunSuite with Matchers{

  test("init") {
    val p = new SimpleParser
    val parsedWord = p.parse(p.word, "дима придет поздно")
    println(parsedWord)
  }

}
