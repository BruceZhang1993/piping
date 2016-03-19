package com.shinado.snake;

import java.util.LinkedList;

public class Snake {

    LinkedList<Point> body = new LinkedList<>();
    private Direction direction;

    public Snake(){
        direction = Direction.RIGHT;
        body.add(new Point(0, 0));
        body.add(new Point(0, 1));
        body.add(new Point(0, 2));
        body.add(new Point(0, 3));
    }

    private Snake(Snake another){
        this.direction = another.direction;
        this.body = another.body;
    }

    public void up(){
        if (direction == Direction.UP || direction ==Direction.DOWN){
            return;
        }
        direction = Direction.UP;
    }

    public void down(){
        if (direction == Direction.DOWN || direction == Direction.UP){
            return;
        }
        direction = Direction.DOWN;
    }

    public void left(){
        if (direction == Direction.LEFT || direction == Direction.RIGHT){
            return;
        }
        direction = Direction.LEFT;
    }

    public void right(){
        if (direction == Direction.RIGHT || direction == Direction.LEFT){
            return;
        }
        direction = Direction.RIGHT;
    }

    /**
     *
     * @return the last step
     */
    public Point crawl(Point dot){
        Point next = getNextStep();
        if (!next.equals(dot)){
            body.poll();
        }

        body.add(next);
        return next;
    }

    public Point getNextStep(){
        Point last = body.getLast();
        Point next;
        if (direction == Direction.LEFT){
            next = new Point(last.x - 1, last.y);
        }else if (direction == Direction.RIGHT){
            next = new Point(last.x + 1, last.y);
        }else if (direction == Direction.UP){
            next = new Point(last.x, last.y - 1);
        }else{
            next = new Point(last.x, last.y + 1);
        }
        return next;
    }

    public Point getTail(){
        return body.peek();
    }

    public boolean isPointPartOfBody(Point point){
        for (Point p : body){
            if (p.equals(point)){
                return true;
            }
        }
        return false;
    }

    public LinkedList<Point> getBody(){
        return body;
    }

    enum Direction{
        UP, DOWN, LEFT, RIGHT
    }

    public Snake clone(){
        return new Snake(this);
    }

}
