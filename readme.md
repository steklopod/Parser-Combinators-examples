## Примеры работы с библиотекой [scala parser combinators](https://github.com/scala/scala-parser-combinators/blob/1.1.x/docs/Getting_Started.md)

> Уровень 1 

_Иногда программисты сталкиваются с необходимостью анализа произвольного текста для данных. Как правило, 
вы можете использовать `регулярные выражения` или `кодировать исходя из предположения о формате данных` о том, 
как вы анализируете текст (обрезать строку по определенным индексам, разделить по запятой и т.д.). 
Оба способа хрупкие и требуют большого количества подробного кода для правильной обработки всех 
возможных исключений. Это может привести к написанию собственного анализатора, если вы достаточно уверены в себе,
 но это непосильная задача для большинства разработчиков. Вам нужно научиться писать парсер или изучать 
 генераторы парсеров, чтобы даже начать программирование решения в вашем конкретном случае. 
 Однако Scala имеет фантастическое решение этой проблемы, и это решение представляет собой 
 **`комбинаторы парсеров`**._
 
  >Что такое комбинаторы парсеров?

**Парсер** - часть программы, которая `строит более сложные структуры данных` из линейной последовательности простых 
данных (символов, лексем, байтов) с учетом некоторой грамматики, неявно содержащиеся в исходной последовательности. 
Это может быть разбор конфигурационных файлов, разбор исходного кода на каком-либо языке программирования, 
разбор проблемно-ориентированных языков (DSL), чтение сложных и не очень форматов данных (XML, PostScript), 
разбор запросов и ответов сетевых протоколов, вроде HTTP, и т. п. Здесь и далее слова «парсинг» и «разбор» 
считаются синонимами.

**Комбинаторы** — это `функции высшего порядка`, которые из одних функций строят другие. 
Возможность принимать функции в качестве аргумента и возвращать их в качестве результата — 
отличительная черта функциональных языков программирования, в которых комбинаторы являются обычными функциями.

**Парсер-комбинаторы** — это широко известная в узких кругах `техника создания парсеров`, 
использующая возможности функциональных языков программирования. 

Функциональные языки позволяют `построить парсер динамически`, используя простейшие функции и комбинаторы для синтеза по 
правилам грамматики сложных парсеров из простых. 
При этом и сама грамматика, и семантические действия (выполняющиеся при успешном разборе того или иного элемента 
грамматики) формулируется на одном языке, а парсер-комбинаторы выступают в качестве `DSEL` (встроенного предметно-ориентированного языка).


**Scala parser combinators** - это мощный способ создания парсеров, которые могут использоваться в повседневных программах. 
Но сразу трудно понять как начать работу и это может быть сложным, так же стандартная документация не очень помогает. 
Итак, _каковы компоненты парсера? Как эти компоненты подходят друг к другу? Какие методы я вызываю? Какие шаблоны можно сопоставить?_ 
Пока эти части не будут поняты, вы не сможете начать работать над своей грамматикой или создавать и обрабатывать 
`абстрактные деревья синтаксиса`. Поэтому, чтобы свести к минимуму сложность, я хотел начать здесь с самого простого языка: 
**_слова в нижнем регистре_**. Давайте построим парсер для этого языка. Мы можем описать грамматику в одном правиле:


<!-- code -->
```regexp
    word -> [а-я]+
```

Вот как выглядит синтаксический анализатор:

<!-- code -->
```scala
    import scala.util.parsing.combinator._
    class SimpleParser extends RegexParsers {
      def word: Parser[String]    = """[а-я]+""".r ^^ { _.toString }
    }
```

* Пакет [scala.util.parsing.combinator](https://www.scala-lang.org/files/archive/api/current/scala-parser-combinators/scala/util/parsing/combinator/) содержит много интересного. 

* Наш парсер расширяет [RegexParsers](https://www.scala-lang.org/files/archive/api/current/scala-parser-combinators/scala/util/parsing/combinator/RegexParsers.html), потому что мы делаем `лексический анализ`. 

* **`"""[а-я]+""".r`** является регулярным выражением. 

* **`^^`** [документируется](https://www.scala-lang.org/files/archive/api/current/scala-parser-combinators/scala/util/parsing/combinator/Parsers$Parser.html#%5E%5E%5BU%5D(f:T=>U):Parsers.this.Parser%5BU%5D) как _«парсер комбинатор для применения функции»_. 
Если парсинг слева от **`^^`** успешен, выполняется функция справа. 
Поскольку метод **`word`** возвращает `Parser` типа `String`, функция справа от **`^^`** должна возвращать `String`.

Итак, как нам использовать этот парсер? Если мы хотим извлечь слово из строки, мы можем сделать след. вызов:

<!-- code -->
```scala
    SimpleParser.parse(SimpleParser.word(myString))
```

Код чтобы сделать это:
<!-- code -->
```scala
    object TestSimpleParser extends SimpleParser {
      def main(args: Array[String]) = println(parse(word, "дима придет поздно"))
    }
```

> Когда мы запускаем это, мы не возвращаем слово `word`, которое мы анализировали, мы получаем `ParserResult[String]`. 
Параметр типа `String` необходим, потому что метод с именем `word` возвращает результат типа `Parser[String]`, 
а параметр типа переносится в `ParseResult`.

Когда мы запускаем программу, мы получаем следующее в консоли:

<!-- code -->
```sbtshell
   [1.7] parsed: дима 
```

Это говорит о том, что первым символом ввода, который соответствует парсеру, является `позиция 1`, а первый символ,
 который должен быть сопоставлен, находится в `позиции 7`. Это хорошее начало, но все мы что-то упустили, 
 потому что у нас есть `ParseResult`, а не `слово`. 
 Нам нужно обработать `ParserResult` еще лучше. Мы могли бы вызвать метод `get` на `ParseResult`. Это дало бы нам результат, 
 но это сделало бы оптимистическое предположение, что все работает, и что синтаксический анализ был успешным. 
 Мы не можем рассчитывать на это, потому что мы, вероятно, не можем контролировать ввод достаточно хорошо, чтобы знать, 
 что все ОК. 
 Входные данные предоставляются нам, и мы должны сделать все возможное. Это означает обнаружение и обработку ошибок, 
 которые звучат как задание для сопоставления шаблонов, правильно? В Scala мы используем сопоставление шаблонов (Options)
  с ветвями для успеха и неудачи. И на самом деле вы можете сопоставить шаблон  в `ParseResult` для различных состояний.
   _Вот переписывание небольшой программы, которая делает работу лучше:_
   
<!-- code -->
```scala
  object TestSimpleParser extends SimpleParser {
    def main(args: Array[String]) = {    
      parse(word, "дима придет поздно") match {
        case Success(matched,_) => println(matched)
        case Failure(msg,_) => println("FAILURE: " + msg)
        case Error(msg,_) => println("ERROR: " + msg)
      }
    }
  }
```
 
 По сравнению с `Option`, который имеет два первичных случая `Some` и `None`, **`ParseResult`** в основном имеет три случая: 
 
 1. `Success (Успех)` - в случае успеха **первый элемент - это объект**, созданный синтаксическим анализатором 
 (строка для нас, поскольку `word` возвращает `Parser[String]`);
 
 2. `Failure (Сбой)`;
 
 3. `Error (Ошибка)`.
 
 Каждый случай соответствует шаблону **из двух элементов**. В случаях `Сбой и Ошибка` **первым элементом является сообщение об ошибке**.
  **Во всех случаях второй элемент** в матче - это оставшийся несматченный ввод, который нам здесь неинтересен. 
  Но если бы мы занимались обработкой ошибок или последующим анализом, мы бы обратили пристальное внимание. 
  Разница между `Failure` и `Error` заключается в том, что **при `Failure` синтаксический анализ будет отступать**, 
  когда синтаксический анализ продолжается (это правило не сработало, но, возможно, есть другое другое правило грамматики), 
  тогда как **случай `Error` является фатальным и не будет возврата** ( у вас есть синтаксическая ошибка, нет способа 
  сопоставления выражения, которое вы предоставили с грамматикой для этого языка, отредактируйте выражение и повторите попытку).
  
Этот крошечный пример на самом деле показывает много необходимой техники для парсера. Теперь давайте посмотрим на несколько 
более сложный, хотя и надуманный пример, чтобы привести разобраться более глубоко. 
Что если за словом следует число? Конечно, есть способы сделать это простым сопоставлением регулярных выражений, но давайте возьмем несколько более абстрактный подход,
 чтобы показать еще несколько приемов. В дополнение к словам, нам также придется сопоставлять числа, и нам придется 
 сопоставлять слова и числа вместе. Итак, сначала добавим новый тип для сбора слов и подсчетов. Вот простой класс для этого:

<!-- code -->
```scala
    case class WordFreq(word: String, count: Int) {
       override def toString = "Слово <" + word + "> " + "встречается с частотой " + count
    }
```

Теперь мы хотим, чтобы наш синтаксический анализатор возвращал экземпляры этого `case-класса` , а не экземпляры `String`. 
В контексте традиционного синтаксического анализа производные, возвращающие примитивные объекты, такие как `строки и числа`,
 выполняют лексический анализ (например, токенизацию, как правило, с использованием регулярных выражений), тогда как произведения,
  возвращающие составные объекты, соответствуют созданию `абстрактных синтаксических деревьев (АСД)`. 
  Действительно, в обновленном классе парсера ниже слова и числа распознаются регулярными выражениями, 
  а частоты слов используют шаблон более высокого порядка. 
  Итак, два наших правила грамматики предназначены для токенизации, а третья строит `АСД`:
  
<!-- code -->
```scala
    class SimpleParser extends RegexParsers {
        def word: Parser[String]   = """[a-z]+""".r       ^^ { _.toString }
        def number: Parser[Int]    = """(0|[1-9]\d*)""".r ^^ { _.toInt }
        def freq: Parser[WordFreq] = word ~ number        ^^ { case wd ~ fr => WordFreq(wd,fr) }
    }
```

Итак, что можно заметить здесь, в этой новой программе? Ну, парсер для `number` выглядит примерно так же, как парсер `word`,
 за исключением того, что он возвращает `Parser[Int]`, а не `Parser[String]`, а функция преобразования вызывает `toInt`, а не `toString`.
  Но здесь есть третье правило, правило `freq`. Это:
  
  * Не имеет **`.r`**, потому что это не регулярное выражение (это комбинатор).
  
  * Возвращает экземпляры `Parser[WordFreq]`, поэтому функция с правой стороны оператора **`^^`** имела лучшие экземпляры 
  возврата составного типа `WordFreq`.
  
  * Объединяет правило **`word`** с правилом **`number`**. Он использует комбинатор **`~`** (тильда), чтобы сказать 
  _«вам нужно сначала совместить слово, а затем число»_. Комбинатор тильды является наиболее распространенным комбинатором правил, 
  которые не включают регулярные выражения.
  
  * Использует паттерн-матчинг в правой части правила. Иногда эти выражения соответствия сложны, но во многих случаях они 
  являются лишь отголоском правила в левой части. В этом случае все, что он действительно делает - это имена для разных 
  элементов правила (в данном случае `wd` и `fr`), чтобы мы могли работать с этими элементами. 
  В этом случае мы используем эти именованные элементы для построения интересующего нас объекта. 
  Но также есть случаи, когда совпадение шаблона не является отражением левой стороны. 
  Эти случаи могут возникать, когда части правила являются необязательными или когда встречаются очень конкретные случаи. 
  _Например, если бы мы хотели выполнить специальную обработку в случае, когда **`fr`** было ровно 0. Для этого мы могли бы добавить случай:_ 
  
<!--code-->
```scala
    case wd ~ 0
```

>Вот немного модифицированная программа для использования этого синтаксического анализатора:

<!--code-->
```scala
    object TestSimpleParser extends SimpleParser {
      def main(args: Array[String]) = {
        parse(freq, "дима 121") match {
          case Success(matched,_) => println(matched)
          case Failure(msg,_) => println("FAILURE: " + msg)
          case Error(msg,_) => println("ERROR: " + msg)
        }
      }
    }
```

Есть только две отличия между этой маленькой программой и предыдущей. Оба эти различия находятся на третьей строке:

* Вместо использования парсера `word` мы используем парсер `freq`, потому что это те объекты, которые мы пытаемся получить 
от ввода, и

* Мы изменили строку ввода в соответствии с новым языком.

>Теперь, когда мы запускаем программу, получаем:

<!--code-->
```sbtshell
    Слово <дима> встречается с частотой 121
```

На данный момент мы показали достаточное количество техник для парсера, чтобы начать работу и сделать что-то полезное.
Теперь можно приступить к другим примерам. 

* => [Далее](https://github.com/steklopod/Parser-Combinators-examples/blob/master/src/main/resources/docs/p01_FirstExample.md)

[справка по регулярным выражениям](https://ru.wikibooks.org/wiki/Регулярные_выражения)

[переведено мной отсюда](https://github.com/scala/scala-parser-combinators/blob/1.1.x/docs/Getting_Started.md)

