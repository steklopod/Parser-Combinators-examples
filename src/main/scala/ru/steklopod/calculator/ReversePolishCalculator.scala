package ru.steklopod.calculator

import scala.util.parsing.combinator.JavaTokenParsers

trait Maths {
  def add(x: Float, y: Float) = x + y
  def sub(x: Float, y: Float) = x - y
  def mul(x: Float, y: Float) = x * y
  def div(x: Float, y: Float) = if (y > 0) x / y else 0.0f
}

class ReversePolishCalculator extends JavaTokenParsers with Maths {

  // Каждый операнд будет помещен в стек, и пары будут удалены для каждой операции, заменяя пару результатом операции.
  // Расчет заканчивается, когда конечный оператор применяется ко всем оставшимся операндам
  def expr: Parser[Float] = rep(term ~ operator) ^^ { case terms =>
    var stack = List.empty[Float]
    var lastOp: (Float, Float) => Float = add // Запоминает последнюю выполненную операцию, по умолчанию добавление
    terms.foreach { case nums ~ op => lastOp = op;
      stack = reduce(stack ++ nums, op)
    }
    // Применяет последнюю операцию ко всем оставшимся операндам
    stack.reduceRight((x, y) => lastOp(y, x))
  }

  def term: Parser[List[Float]] = rep(factor)

  // Фактором является либо число, либо другое выражение (завернутое в parens), преобразованное в Float
  def factor: Parser[Float] = num | "(" ~> expr <~ ")" ^^ (_.toFloat)

  // Преобразует число с плавающей запятой из String в Float
  def num: Parser[Float] = floatingPointNumber ^^ (_.toFloat)

  // Разбирает оператор и преобразует его в базовую функцию, которую он логически отображает на
  def operator: Parser[(Float, Float) => Float] = ("*" | "/" | "+" | "-") ^^ {
    case "+" => add
    case "-" => sub
    case "*" => mul
    case "/" => div
  }

  // Уменьшает стек чисел, выбирая последнюю пару из стека, применяя `op` и добавляет в результат
  def reduce(nums: List[Float], op: (Float, Float) => Float): List[Float] = {
    // Смена направления списка позволяет нам использовать сопоставление с образцом для безопасного удаления списка
    val result = nums.reverse match {
      case x :: y :: xs => xs ++ List(op(y, x))
      case List(x) => List(x)
      case _ => List.empty[Float]
    }
    result
  }
}

object Calculator extends ReversePolishCalculator with App {
  val result = calculate("3 4 - 5 + 2 *") //3 - 4 + 5 * 2
  println(s"Результат:  $result")

  def calculate(expression: String) = parseAll(expr, expression)
}
