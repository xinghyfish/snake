package src.controller;

import com.sun.tools.javac.Main;
import src.GameMain;
import src.model.Game;
import src.model.Node;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameController {
    private final Game game;
    private final SnakeController snakeController;
    private final static int[][] DRCT = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0}
    };
    private final static String archivePath = URLDecoder.decode(GameController.class.getProtectionDomain().getCodeSource().getLocation().getFile(), StandardCharsets.UTF_8)
                                                + "archive.sav";

    public GameController(int height, int width) {
        this.game = new Game(height, width);
        snakeController = new SnakeController(game.getSnake());
        startGame();
        System.out.println(archivePath);
    }

    public void startGame() {
        snakeController.getSnake().setCurDrct(3);
        snakeController.getSnake().getBody().clear();
        for (int i = 2; i >= 0; --i)
            snakeController.getSnake().getBody().add(new Node(i, 0));
        generateFood();
    }

    public int getLimitX() {
        return game.getWidth();
    }

    public int getLimitY() {
        return game.getHeight();
    }


    public boolean isDead() {
        boolean flag = isHitSelf() || isHitWall();
        if (flag) snakeController.rollback();
        return flag;
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
        return !(head.getX() >= 0 && head.getY() >= 0 && head.getX() < limitX && head.getY() < limitY);
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
            snakeController.setOldTail(head_tail[1]);
            Node tail = snakeController.grow();
            generateFood();
        }
        return isDead();
    }

    public SnakeController getSnakeController() {
        return this.snakeController;
    }

    public Node getFood() {
        return game.getFood();
    }

    public boolean hasArchive() {
        File arcFile = new File(archivePath);
        return arcFile.exists();
    }

    public boolean saveGame() throws IOException {
        try{
            File file = new File(archivePath);
            if(file.createNewFile())
                System.out.println("Successfully create file!");
            else
                file.delete();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(archivePath));
        bufferedWriter.write(getFood().getX() + " " + getFood().getY());
        bufferedWriter.newLine();
        bufferedWriter.flush();
        bufferedWriter.write(String.valueOf(snakeController.getSnake().getCurDrct()));
        bufferedWriter.newLine();
        bufferedWriter.flush();
        for (Node node : snakeController.getSnake().getBody()) {
            bufferedWriter.write(node.getX() + " " + node.getY());
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        return true;
    }

    public boolean reloadGame() throws IOException {
        if (!hasArchive()) return false;

        BufferedReader bufferedReader = new BufferedReader(new FileReader(archivePath));
        String lineStr = bufferedReader.readLine();
        String[] pos = lineStr.split(" ");
        game.setFood(new Node(Integer.parseInt(pos[0]), Integer.parseInt(pos[1])));
        String drct = bufferedReader.readLine();
        snakeController.getSnake().setCurDrct(Integer.parseInt(drct));
        List<Node> body = new ArrayList<>();
        while ((lineStr = bufferedReader.readLine()) != null) {
            pos = lineStr.split(" ");
            body.add(new Node(Integer.parseInt(pos[0]), Integer.parseInt(pos[1])));
        }
        game.getSnake().setBody(body);
        return true;
    }

    public boolean isWin() {
        return snakeController.getSnake().size() == getLimitX() * getLimitY();
    }
}
