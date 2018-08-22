## Примеры работы с библиотекой [scala parser combinators](https://github.com/scala/scala-parser-combinators/blob/1.1.x/docs/Getting_Started.md)

В данном проекте собраны простые примеры для начального уровня понимания.

**Парсером** называется часть программы, которая из линейной последовательности простых данных 
(символов, лексем, байтов) с учетом некоторой грамматики `строит более сложные структуры данных`, 
неявно содержащиеся в исходной последовательности. 
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
Итак, _каковы компоненты парсера? Как эти компоненты подходят друг к другу? Какие методы я называю? Какие шаблоны можно сопоставить?_ 
Пока эти части не будут поняты, вы не сможете начать работать над своей грамматикой или создавать и обрабатывать 
`абстрактные деревья синтаксиса`. Поэтому, чтобы свести к минимуму сложность, я хотел начать здесь с самого простого языка: 
**_слова в нижнем регистре_**. Давайте построим парсер для этого языка. Мы можем описать грамматику в одном правиле производства:


<!-- code -->
```regexp
    word -> [a-z]+
```

Вот как выглядит синтаксический анализатор:

<!-- code -->
```scala
    import scala.util.parsing.combinator._
    class SimpleParser extends RegexParsers {
      def word: Parser[String]    = """[a-z]+""".r ^^ { _.toString }
    }
```

* Пакет [scala.util.parsing.combinator](https://www.scala-lang.org/files/archive/api/current/scala-parser-combinators/scala/util/parsing/combinator/) содержит много интересного. 

* Наш парсер расширяет [RegexParsers](https://www.scala-lang.org/files/archive/api/current/scala-parser-combinators/scala/util/parsing/combinator/RegexParsers.html), потому что мы делаем `лексический анализ`. 

* **`"" "[a-z] +" "".r`** является регулярным выражением. 

* **`^^`** [документируется](https://www.scala-lang.org/files/archive/api/current/scala-parser-combinators/scala/util/parsing/combinator/Parsers$Parser.html#%5E%5E%5BU%5D(f:T=>U):Parsers.this.Parser%5BU%5D) как _«парсер комбинатор для применения функции»_. 
В принципе, если спарсинг слева от **`^^`** успешен, выполняется функция справа. 
Если вы провели анализ `yacc`, левая часть `^^` соответствует правилу грамматики, а правая сторона соответствует коду, 
генерируемому правилом. Поскольку метод `word` возвращает `Parser` типа `String`, функция справа от `^^` должна возвращать String.

[Справка по регулярным выражениям](https://ru.wikibooks.org/wiki/Регулярные_выражения)
