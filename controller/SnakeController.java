package controller;

import model.Node;
import model.Snake;

import java.util.List;

public class SnakeController {
    private final Snake snake;        // snake entity
    private Node tail;     // use to grow the snake
    private final static int[][] DRCT = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0}
    };                          // direction array

    public SnakeController(Snake snake) {
        this.snake = snake;
    }

    /**
     * Move the body of the snake.
     * @return new head of snake and lost tail
     */
    public Node[] move() {
        int len = snake.size();
        int drct = snake.getCurDrct();
        Node head =  snake.getBody().get(0);
        this.tail = snake.getBody().get(len - 1);

        int dx = DRCT[drct][0], dy = DRCT[drct][1];
        Node newHead = new Node(head.getX() + dx, head.getY() + dy);
        snake.getBody().remove(len - 1);
        snake.getBody().add(0, newHead);

        return new Node[]{newHead, tail};
    }

    /**
     * increase the size of the snake.
     */
    public Node incr() {
        snake.getBody().add(tail);
        return tail;
    }

    public void changeDrct(int newDrct) {
        this.snake.setCurDrct(newDrct);
    }

    public Snake getSnake() {
        return snake;
    }
}
