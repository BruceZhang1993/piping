package indi.shinado.piping.pipes.impl.action.snake;

import java.util.Random;

public class Game {

    private Console console;
    public static final byte EMPTY = 0;
    public static final byte SOLID = 1;
    private Maze maze;
    private Snake snake;
    private Point dot;
    private byte[][] matrix;
    private int interval = 500;
    private boolean running = false;

    public void create(Maze maze, Snake snake, Console console){
        this.maze = maze;
        this.snake =snake;
        this.console = console;

        initMatrix(maze);
        for (Point point : snake.getBody()){
            add(point);
        }

        dot = getNewDot();
    }

    public void start(){
        running = true;
        new GameThread().start();
    }

    public void stop(){
        running = false;
    }

    public void end(){
        running = false;
    }

    public Maze getMaze() {
        return maze;
    }

    private class GameThread extends Thread{
        @Override
        public void run(){
            while (running){
                try {
                    sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Snake previous = snake.clone();
                Point next = snake.crawl(dot);

                if (next.equals(dot)){
                    dot = getNewDot();
                    onNewDot(dot);
                }
                if (maze.hitWall(next)){
                    console.die();
                    return;
                }
                onMove(previous, next);
            }
        }
    }

    private Point getNewDot(){
        Point point = getRandomPoint();
        while (snake.isPointPartOfBody(point)){
            point = getRandomPoint();
        }
        return point;
    }

    private Point getRandomPoint(){
        Point point = new Point(
                Math.abs(new Random(maze.width).nextInt()),
                Math.abs(new Random(maze.height).nextInt())
        );
        return point;
    }

    interface GameListener{
        void onMove(Snake snake, Point next);
        void onNewDot(Point point);
    }

    public Snake getSnake(){
        return snake;
    }


    private void initMatrix(Maze maze){
        matrix = new byte[maze.width][maze.height];
        for (byte[] row : matrix){
            for (int i=0; i<row.length; i++){
                row[i] += EMPTY;
            }
        }
    }

    private void onMove(Snake snake, Point next) {
        //previous
        Point previousTail = snake.getTail();
        remove(previousTail);
        add(next);
        this.snake = snake;
        console.draw(matrix);
    }

    private void remove(Point point){
        matrix[point.x][point.y] = EMPTY;
    }

    private void add(Point point){
        matrix[point.x][point.y] = SOLID;
    }

    private void onNewDot(Point point) {
        add(point);
        console.draw(matrix);
    }

}
