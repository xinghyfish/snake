package src.model;

public class Game {
    private int height;
    private int width;
    private Snake snake;
    private Node food;

    public Node getFood() {
        return food;
    }

    public void setFood(Node food) {
        this.food = food;
    }

    public Game(int height, int width) {
        this.snake = new Snake();
        this.height = height;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Snake getSnake() {
        return snake;
    }

    public void setSnake(Snake snake) {
        this.snake = snake;
    }
}
