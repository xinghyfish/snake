package src.view;

import javax.swing.*;
import java.awt.*;

public class GameMain extends JFrame {
    GameWin gameWin;
    static final int width = 800, height = 600;

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
        Toolkit kit = Toolkit.getDefaultToolkit();    // define toolkit
        Dimension screenSize = kit.getScreenSize();   // get screen size
        int screenWidth = screenSize.width / 2;         // get screen height
        int screenHeight = screenSize.height / 2;       // get screen width
        int height = this.getHeight();
        int width = this.getWidth();
        setLocation(screenWidth-width/2, screenHeight-height/2);
    }

    public static void main(String[] args) {
        new GameMain();
    }
}
