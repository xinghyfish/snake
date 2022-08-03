package src.controller;

import src.model.Node;
import src.model.Snake;

public class SnakeController {
    private final Snake snake;        // snake entity
    private Node oldTail;     // use to grow the snake
    // direction array
    private final static int[][] DRCT = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0}
    };

    public SnakeController(Snake snake) {
        this.snake = snake;
    }

    public void setOldTail(Node oldTail) {
        this.oldTail = oldTail;
    }

    /**
     * Move the body of the snake.
     * @return new head of snake and lost tail
     */
    public Node[] move() {
        int len = snake.size();
        int drct = snake.getCurDrct();
        Node head =  snake.getBody().get(0);
        this.oldTail = snake.getBody().get(len - 1);

        int dx = DRCT[drct][0], dy = DRCT[drct][1];
        Node newHead = new Node(head.getX() + dx, head.getY() + dy);
        snake.getBody().remove(len - 1);
        snake.getBody().add(0, newHead);

        return new Node[]{newHead, oldTail};
    }

    /**
     * Increase the size of the snake.
     */
    public Node grow() {
        snake.getBody().add(oldTail);
        return oldTail;
    }

    /**
     * Rollback to valid status.
     */
    public void rollback() {
        snake.getBody().remove(0);
        snake.getBody().add(oldTail);
    }

    public void changeDrct(int newDrct) {
        this.snake.setCurDrct(newDrct);
    }

    public Snake getSnake() {
        return snake;
    }
}
