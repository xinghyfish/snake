package view;

import javax.swing.*;

public class GameMain extends JFrame {
    GameWin gameWin;
    static final int width = 800, height = 600, locX = 200, locY = 80;

    public GameMain() {
        super("Snake Game Main Window");
        gameWin = new GameWin();
        // add other components in JFrame window
        add(gameWin);
        // set size of the component
        this.setSize(width, height);
        // set visibility of the component
        this.setVisible(true);
        // set the location of the component
        this.setLocation(locX, locY);
    }

    public static void main(String[] args) {
        new GameMain();
    }
}
