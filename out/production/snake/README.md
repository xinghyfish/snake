# snake
Java+OOP to implement the classic game Snake



# 项目运行
1. 使用IDE，切换到`src/GameMain.py`，运行该类的`main`方法。
2. 在终端中打开`out/artifacts/snake_jar`，输入指令`java -jar snake.jar`，回车运行。



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

> 风格首先是一种本能、直觉。——Bill Blass.

## 概要设计
本项目的类图保存在`diagrams`文件夹。在项目设计时，使用MVC架构。

- 模型类包含节点类`Node`，存储贪吃蛇相关状态的类`Snake`和存储游戏相关状态和数据的类`Game`。
- 控制类包含贪吃蛇控制器`SnakeController`和游戏控制器`GameController`，用于对相关模型类进行数据的读写操作。
- 视图类包含游戏窗口视图`GameWin`，用户在该窗口和程序进行交互，交互产生的动作由控制器类处理，将结果数据在模型类中，并进行输出。

注意到，在实现时，并没有区分食物、蛇身和地砖的差异（初期有考虑，但实质上只有视图层面的差异），统一用`Node`表示，根据不同的类型进行不同颜色的绘制。



## 蛇的运动、成长和食物的生成

在最初的思考中，把蛇抽象为一个线性表。每一条贪吃蛇需要一个当前的方向和身体。为了考虑每一帧的变化，引入了"快照"的概念，即将上一帧的位置保存为一个快照，根据快照决定下一个位置。

但是写代码时忽然想到，这不就是个链表……于是痛改前非（大雾），使用链表实现（逻辑上是双向链表），我们只需要关注头的下一个位置，其余的部分全部移动到前一节上一帧的位置。

这样可以解决蛇动态运动的效果。但是，当蛇吃到食物，需要向上一帧的尾部添加一个新的节点。因此在控制器中，加入了`oldTail`属性，每当蛇成长时就把新的节点添加到相关位置。

至于食物的生成，如果使用随机数，当空余的位置很少时，随机数生成将会产生大量的碰撞。因此，使用时间换空间，每次生成食物时，将逻辑空间转化为一个一维数组，去掉蛇身体占有的部分，在空余的位置生成一个不会产生碰撞的随机数，再映射到二维作为食物的位置。



## 动画实现
和静态的绘图不同的是，贪吃蛇需要呈现给用户"动态"的效果。从生物学的角度，人的大脑可以将静态的帧补全为动画的效果，因此我们只需要不断地进行"刷新"，让蛇在很短的时间间隔内移动后，刷新图像，即可呈现出动画的效果。

考虑循环进行刷新（实际调用`repaint()`方法，调用`paint()`方法进行绘图，但本质上`repaint`可以理解为一个事件），那么用户就可以得到连续的动画视图。在具体实现时，每次点击`start`按钮，都会产生一个新的线程进行游戏的逻辑判断和视图的刷新。

同时，贪吃蛇的一大风格就是"像素化"，及将很多细微的像素点合并成一个视觉上的像素点。因此，在进行设计时，采用的是逻辑上的坐标点，即我们所看到的大像素块的坐标，而不是细微的像素坐标。

在实际进行绘图时，使用rectangle进行模拟大块像素点，用逻辑上的像素坐标作为rectangle左上角坐标，像素块大小根据实际需要自定义。

因此，我们使用多线程和抽象化的思想，实现了贪吃蛇的动态捕食效果。



# 踩坑记录

## 1. `repaint()`方法：不使用条件

在新的线程中，我们使用一个循环，不断处理蛇的运动。每次蛇运动完，我们都要重新绘制蛇的位置。但是，由于`repaint()`方法的机制，当我们使用：
```java
class SnakeThread extends Thread {
    @Override
    public void run() {
        while (startFlag) {
            deadFlag = gameController.processMove();
            if (deadFlag) {
                pauseButton.setEnabled(false);
                startFlag = false;
                startButton.setEnabled(true);
                JOptionPane.showMessageDialog(getParent(),"YOU DIED");
                reloadButton.setEnabled(true);
                break;
            }
            if (gameController.isWin()) {
                JOptionPane.showMessageDialog(getParent(), "HEIR OF FIRE DESTROYED");
            }
            try {
                Thread.sleep(MAX_LATENCY - gameController.getSnakeController().getSnake().size());
                repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```
如果在`repaint()`方法前使用条件语句（确保蛇在视图上不会越界），则蛇实际上死亡时头不会碰到左侧的墙壁，尽管逻辑上蛇已经死去。而如果将条件移去，则蛇在右侧和下侧墙壁撞死后头会穿墙。

相比前一种情况设计的画图的复杂机制，由于后一种情况相对较为简单。在`src/controller/GameController.java`类中，实现了`rollback()`方法，当蛇死亡时，调用该方法，会滚到前一帧，在视图上呈现出正常的逻辑。

实现了`rollback()`方法后，只需要用当前蛇头位置是否合法作为标志，而不需要使用下一个位置。如果使用下一个位置作为合法性判断的一句，则蛇头一碰到墙壁则立即死亡，不符合要求。



## 2. JAR包运行项目

由于涉及存档需要进行文件的IO，jar包运行项目时相对路径和IDEA运行的不同，无法进行存档。查看相关资料（链接：https://stackoverflow.com/questions/45765092/why-relative-path-doesnt-work-in-java-in-jar-file）后，发现需要调用
```java
class GameController {
    // ...
    String filepath = Main.class.getResource("folder/file");
    // ...
}
```
作为路径，这样在jar包中就可以根据路径进行调整，完成存档。



# 总结

> 做项目三天，JAVA治好了我的精神内耗。——无火的余灰

由于本科阶段教学的原因，JAVA只学习了粗浅的一小部分内容，在低年级阶段没有做过课程设计。因此，在高年级阶段完成低年级的课程设计也花费了不少心思。

老实说，由于学校教学方面的一些问题，我一度对软件工程这门专业产生了巨大的排斥。但是随着见识的增长和对恐惧的克服（一大部分来源于大三下的软件项目和黑暗之魂3的游戏经历），我对软件工程也有了新的思考。

虽然行业的年龄焦虑、同侪压力并未远去，许许多多的人还在追求更加深奥的AI算法，相比之下开发岗确实没有那么大的光环。但是，目前而言，我们仍然缺乏优秀的软件工程师，而不是卷上天的算法工程师。即使拥有性能极佳的算法，如果缺乏组织，也无法发挥其极限性能。

那么，就以此作为起点，慢慢变强吧。
