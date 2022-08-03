# snake
Java+OOP to implement the classic game Snake

# 概述

> 不要招惹蛇，除非你打算并且有能力砍下蛇头。——萨达姆

本项目用Java语言实现了经典小游戏【贪吃蛇】。项目采用MVC的设计模式进行架构。

除了基本的交互之外，项目还实现了游戏的存档和读档，用户可以在暂停游戏的情况下进行存档。用户点击【Reload】按钮即可进行读档，游戏将加载用户最新一次存档时的所有状态。

本项目通过Intellij IDEA进行开发。用户可以直接在IDEA中运行项目。同时，用户也可以切换到目录`out/artifacts/snake_jar`，通过指令：
```shell
java -jar snake.jar
```
通过jar包运行本项目。

# 设计思想

本项目的类图保存在`diagrams`文件夹。在项目设计时，使用MVC架构。

- 模型类包含节点类`Node`，存储贪吃蛇相关状态的类`Snake`和存储游戏相关状态和数据的类`Game`。
- 控制类包含贪吃蛇控制器`SnakeController`和游戏控制器`GameController`，用于对相关模型类进行数据的读写操作。
- 视图类包含游戏窗口视图``