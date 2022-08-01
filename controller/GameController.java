package controller;

import model.Game;
import model.Node;
import model.Snake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameController {
    private final Game game;
    private SnakeController snakeController;
    public GameController(Game game) {
        this.game = game;
        snakeController = new SnakeController(game.getSnake());
    }
    private final static int[][] DRCT = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0}
    };

    public GameController(int height, int width) {
        this.game = new Game(height, width);
        snakeController = new SnakeController(game.getSnake());
        startGame();
    }

    public void startGame() {
        snakeController.getSnake().setCurDrct(3);
        snakeController.getSnake().getBody().clear();
        for (int i = 2; i >= 0; --i)
            snakeController.getSnake().getBody().add(new Node(i, 0));
        generateFood();
    }

    public boolean judge() {
        return !isHitSelf() && !isHitWall();
    }

    /**
     * Judge whether snake hits itself
     * @return true if snake hits itself, else false
     */
    public boolean isHitSelf() {
        List<Node> nodes = snakeController.getSnake().getBody();
        Node head = snakeController.getSnake().getHead();
        for (int i = 1; i < nodes.size(); ++i)
            if (head.getX() == nodes.get(i).getX() && head.getY() == nodes.get(i).getY())
                return true;
        return false;
    }

    public boolean isHitWall() {
        Node head = game.getSnake().getHead();
        int limitX = game.getWidth(), limitY = game.getHeight();
        int[] curDrctArr = DRCT[snakeController.getSnake().getCurDrct()];
        int nextHeadX = head.getX() + curDrctArr[0], nextHeadY = head.getY() + curDrctArr[1];
        return !(nextHeadX >= 0 && nextHeadY >= 0 && nextHeadX <= limitX && nextHeadY <= limitY);
    }

    public void generateFood() {
        int limit = game.getWidth() * game.getHeight();
        boolean[] used = new boolean[limit];
        List<Node> body = snakeController.getSnake().getBody();
        Arrays.fill(used, false);
        for (Node node : body) {
            int x = node.getX(), y = node.getY();
            used[y * game.getWidth() + x] = true;
        }
        List<Integer> unused = new ArrayList<>();
        for (int i = 0; i < limit; ++i)
            if (!used[i])
                unused.add(i);
        int randInt = (new Random()).nextInt(unused.size());
        int foodIndex = unused.get(randInt);

        int foodX = foodIndex % game.getWidth(), foodY = foodIndex / game.getWidth();
        Node food = new Node(foodX, foodY);
        game.setFood(food);
    }

    public boolean processMove() {
        Node[] head_tail = snakeController.move();
        Node head = game.getSnake().getHead();
        if (game.getFood().getX() == head.getX() && game.getFood().getY() == head.getY()) {
            Node tail = snakeController.incr();
            generateFood();
        }
        return judge();
    }

    public SnakeController getSnakeController() {
        return this.snakeController;
    }

    public Node getFood() {
        return game.getFood();
    }

    public boolean hasArchive() {
        return false;
    }

    public boolean saveGame() {
        return true;
    }

    public boolean restoreGame() {
        return true;
    }
}
