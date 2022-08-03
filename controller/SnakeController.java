package controller;

import model.Node;
import model.Snake;

public class SnakeController {
    private final Snake snake;        // snake entity
    private Node oldTail;     // use to grow the snake

    public Node getOldTail() {
        return oldTail;
    }

    public void setOldTail(Node oldTail) {
        this.oldTail = oldTail;
    }

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
        this.oldTail = snake.getBody().get(len - 1);

        int dx = DRCT[drct][0], dy = DRCT[drct][1];
        Node newHead = new Node(head.getX() + dx, head.getY() + dy);
        snake.getBody().remove(len - 1);
        snake.getBody().add(0, newHead);

        return new Node[]{newHead, oldTail};
    }

    /**
     * increase the size of the snake.
     */
    public Node grow() {
        snake.getBody().add(oldTail);
        return oldTail;
    }

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
