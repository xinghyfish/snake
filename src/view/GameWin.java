package src.view;

import src.controller.GameController;
import src.model.Node;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.*;


public class GameWin extends JPanel implements ActionListener, KeyListener {

    static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    static final int gameLocX = 50, gameLocY = 50;
    static final int gameWidth = 700, gameHeight = 500, squareSize = 20;  // step size

    static final int MAX_LATENCY = 130;
    boolean startFlag = false, deadFlag = false;

    JButton startButton, reloadButton, quitButton, pauseButton, saveButton;                 // initial window button

    GameController gameController;

    public GameWin() {
        gameController = new GameController(gameHeight/ squareSize, gameWidth/ squareSize);
        startButton = new JButton("Start");
        reloadButton = new JButton("Reload");
        quitButton = new JButton("Quit");
        pauseButton = new JButton("Pause");
        saveButton = new JButton("Save");

        setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(startButton);
        this.add(reloadButton);
        this.add(pauseButton);
        this.add(saveButton);
        this.add(quitButton);

        startButton.addActionListener(this);
        reloadButton.addActionListener(this);
        pauseButton.addActionListener(this);
        saveButton.addActionListener(this);
        quitButton.addActionListener(this);
        this.addKeyListener(this);

        quitButton.setEnabled(true);
        pauseButton.setEnabled(false);
        startButton.setEnabled(true);
        reloadButton.setEnabled(gameController.hasArchive());
        saveButton.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean res;
        if (e.getSource() == startButton) {
            if (deadFlag) {
                gameController.startGame();
                deadFlag = false;
            }
            startFlag = true;
            startButton.setEnabled(false);
            reloadButton.setEnabled(false);
            pauseButton.setEnabled(true);
            new Thread(new SnakeThread()).start();
        }
        else if (e.getSource() == reloadButton) {
            startButton.setEnabled(false);
            reloadButton.setEnabled(false);
            pauseButton.setEnabled(true);
            res = false;
            try {
                res = gameController.reloadGame();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            if (!res) System.err.println("Unable to reload game");
            else repaint();
            startButton.setEnabled(true);
            deadFlag = false;
        }
        else if (e.getSource() == pauseButton) {
            startFlag = false;
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            saveButton.setEnabled(true);
            reloadButton.setEnabled(true);
        }
        else if (e.getSource() == saveButton) {
            try {
                res = gameController.saveGame();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            if (!res) System.err.println("Unable to save game!");
        }
        else {
            System.exit(0);
        }
        this.requestFocus();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // do nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!startFlag) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (gameController.getSnakeController().getSnake().getCurDrct() != DOWN)
                    gameController.getSnakeController().changeDrct(UP);
                break;
            case KeyEvent.VK_DOWN:
                if (gameController.getSnakeController().getSnake().getCurDrct() != UP)
                    gameController.getSnakeController().changeDrct(DOWN);
                break;
            case KeyEvent.VK_LEFT:
                if (gameController.getSnakeController().getSnake().getCurDrct() != RIGHT)
                    gameController.getSnakeController().changeDrct(LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                if (gameController.getSnakeController().getSnake().getCurDrct() != LEFT)
                    gameController.getSnakeController().changeDrct(RIGHT);
                break;
            default:
                // do nothing.
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // do nothing
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);                 // don't refresh buttons
        g.setColor(Color.white);        // set the color of pen
        g.fillRect(gameLocX, gameLocY, gameWidth, gameHeight);
        g.setColor(Color.black);
        g.drawRect(gameLocX, gameLocY, gameWidth, gameHeight);
        g.drawString("Score: " + gameController.getSnakeController().getSnake().size(),
                gameWidth + gameLocX - 50, gameLocY - 5);
        this.paintFood(g);
        this.paintSnake(g);
    }

    /**
     * Paint and draw the food.
     * @param g graphic class
     */
    public void paintFood(Graphics g) {
        Node food = gameController.getFood();
        g.setColor(Color.green);
        g.fillRect(food.getX() * squareSize + gameLocX, food.getY() * squareSize + gameLocY, squareSize, squareSize);
        g.setColor(Color.black);
        g.drawRect(food.getX() * squareSize + gameLocX, food.getY() * squareSize + gameLocY, squareSize, squareSize);
    }

    /**
     * Paint and draw the body of the snake.
     * @param g graphic class
     */
    public void paintSnake(Graphics g) {
        List<Node> nodes = gameController.getSnakeController().getSnake().getBody();
        // Color[] rambo = {Color.red, Color.orange, Color.yellow, Color.green, Color.cyan, Color.blue, Color.magenta};
        Node head = nodes.get(0);
        g.setColor(Color.orange);
        g.fillRect(head.getX() * squareSize + gameLocX, head.getY() * squareSize + gameLocY, squareSize, squareSize);
        g.setColor(Color.black);
        g.drawRect(head.getX() * squareSize + gameLocX, head.getY() * squareSize + gameLocY, squareSize, squareSize);
        for (int i = 1; i < nodes.size(); ++i) {
            Node node = nodes.get(i);
            g.setColor(Color.yellow);
            g.fillRect(node.getX() * squareSize + gameLocX, node.getY() * squareSize + gameLocY, squareSize, squareSize);
            g.setColor(Color.black);
            g.drawRect(node.getX() * squareSize + gameLocX, node.getY() * squareSize + gameLocY, squareSize, squareSize);
        }
    }

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

}
