## Примеры работы с библиотекой [scala parser combinators](https://github.com/scala/scala-parser-combinators/blob/1.1.x/docs/Getting_Started.md)

В данном проекте собраны простые примеры для начального уровня понимания.
Scala предоставляет очень простой способ разработки собственного языка программирования, 
используя его парсер-библиотеку. 
Это упрощает создание собственного языка (например, DSL) или интерпретируемого языка. 
В качестве примера, давайте напишем синтаксический анализатор, который анализирует простые математические выражения, 
такие как `«1 + 9 * 8»` и `«4 * 6 / 2-5»`.

Чтобы начать писать парсер - необходимо унаследоваться от RegexParsers.

<!-- code -->
```scala
    import scala.util.parsing.combinator.RegexParsers

    class ExprParser extends RegexParsers {
        val number = "[1-9][0-9]+".r
        def expr: Parser[Any] = number ~ opt(operator ~ expr )
        def operator: Parser[Any] = "+" | "-" | "*" | "/"
    }
```
* **`~`** - комбинатор парсера для последовательной композиции между каждым токеном

* **`opt`** - генератор парсера для необязательных подфраз. `opt (p)` - это парсер, который возвращает `Some (x)`, 
если `p` возвращает `x` и `None`, если `p` не работает.

[Справка по регулярным выражениям](https://ru.wikibooks.org/wiki/Регулярные_выражения)
