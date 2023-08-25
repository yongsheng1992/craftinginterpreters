# crafting interpreters

这是学习[munificent/craftinginterpreters](https://github.com/munificent/craftinginterpreters)的代码。主要是分别使用Java和C实现名为Lox的语言的解释器。

## 前置知识

![Mountain](https://craftinginterpreters.com/image/a-map-of-the-territory/mountain.png)

上山的路只有一条，即通过词法和语法分析，生成语法树。但是下山的路有多条。当生成语法树后，可以有3条路径下山：
* High-levl language 生成其它高级语言
* Bytecode 生成字节码在VM上执行，比如Java和Python
* Machine code 生成目标机器对应的机器码，比如C

表达式
```
var average = (min + max) / 2;

```