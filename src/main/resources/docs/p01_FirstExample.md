## Калькулятор [обратной польской записи](https://ru.wikipedia.org/wiki/%D0%9E%D0%B1%D1%80%D0%B0%D1%82%D0%BD%D0%B0%D1%8F_%D0%BF%D0%BE%D0%BB%D1%8C%D1%81%D0%BA%D0%B0%D1%8F_%D0%B7%D0%B0%D0%BF%D0%B8%D1%81%D1%8C)  

**Обра́тная по́льская запись** (англ. Reverse Polish notation, RPN) — _форма записи математических и логических выражений, 
в которой операнды расположены перед знаками операций._
![alt text](https://upload.wikimedia.org/wikipedia/commons/thumb/a/a6/Postfix-dia.svg/125px-Postfix-dia.svg.png "Обра́тная по́льская запись")

`Стековой машиной` называется алгоритм, проводящий вычисления по обратной польской записи.

_Стоит еще раз повторить основные понятия:_

**Парсер** - это функция, которая принимает поток входных токенов 
и преобразует их в формат (как правило, структуру данных, такую как список или дерево), 
которая более легко потребляется вашим приложением. 

**Комбинатор** - это просто функция более высокого порядка, которая объединяет две функции в новую функцию. 

**Комбинатор парсеров** - это просто функция, которая объединяет два парсера в другой парсер.

Мы создадим калькулятор `Reverse Polish Notation` в качестве примера того, как применять комбинатор парсеров. 
Доступные комбинаторы:

* **`|`** - комбинатор чередования.  _Успешно, если либо левая, либо правая сторона соответствует_;

* **`~`** - последовательный комбинатор. _Успешно, если `левый операнд успешно проанализирован, а затем переходим к правому`, 
(например, `a ~ b` ожидает `a`, за которым следует `b`)_;

* **`^^`** - комбинатор преобразований. _Если левый операнд успешно проанализирован, преобразовать результат, `используя функцию справа`_;

* **`~>`** - _левый операнд существует, но не включать его в результат_;

* **`<~`** - если `левый операнд успешно проанализирован, а затем справа, но не включает в себя правый` контент в результате"_;

* **`rep(p)`** - _повторить n-раз парсер `p`, пока не возникнет ошибка_ 

Первый шаг - определить, как разбирать число:

<!-- code -->
```scala
    import scala.util.parsing.combinator._
    
    class ReversePolishCalculator extends JavaTokenParsers {
        def num: Parser[Float] = floatingPointNumber ^^ (_.toFloat)
    }
```
Итак, мы импортируем комбинаторы парсеров и создаем класс с парсером чисел. Мы расширяем **JavaTokenParsers**, чтобы 
иметь возможность анализировать текст и получать доступ к парсеру `floatingPointNumber`. Функция `num` будет соответствовать 
любому числу с плавающей запятой и преобразует ее в `Float`. Парсер `floPointNumber` просто матчит текст, он не выполняет 
никакого преобразования. Если вы посмотрите на его исходный код, вы увидите, что это просто парсер регулярного выражения:

<!-- code -->
```scala
    trait JavaTokenParsers extends RegexParsers {
        def floatingPointNumber: Parser[String] = {
            """-?(\d+(\.\d*)?|\d*\.\d+)([eE][+-]?\d+)?[fFdD]?""".r
        }
    }
```

Поэтому на данный момент наш парсер может соответствовать числу, вот и все. Если это все, что мы хотели, 
мы могли бы объект для синтаксического анализа чисел:

<!-- code -->
```scala
object Calculator extends ReversePolishCalculator with App {
    val result = parseAll(num, "123.09")
    println(s"Parsed $result")
}
```
И получили бы след. результат:

<!-- code -->
```bash
  Parsed [1.7] parsed: 123.09
```

Это, в основном, бесполезно, поэтому давайте продолжим и определим, как анализировать операторы, которые может 
использовать наш калькулятор:

<!-- code -->
```scala
    class ReversePolishCalculator extends JavaTokenParsers {
        def num: Parser[Float] = floatingPointNumber ^^ (_.toFloat)
        def operator: Parser[(Float, Float) => Float] = ("*" | "/" | "+" | "-") ^^ {
            case "+" => (x, y) => x + y
            case "-" => (x, y) => x - y
            case "*" => (x, y) => x * y
            case "/" => (x, y) => if (y > 0) (x / y) else 0f
        }
    }
```

### Конечный результат

Окончательная, исполняемая версия нашего калькулятора `Reverse Polish Notation` после рефакторинга выглядит следующим образом:

<!-- code -->
```scala
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
```

[переведено мной отсюда](http://bitwalker.org/posts/2013-08-10-learn-by-example-scala-parser-combinators)

[еще пример для самых умных](http://www.codecommit.com/blog/scala/the-magic-behind-parser-combinators)
