package com.shinado.snake;

import com.shinado.annotation.TargetVersion;

import indi.shinado.piping.launcher.InputCallback;
import indi.shinado.piping.launcher.UserInputCallback;
import indi.shinado.piping.launcher.impl.DeviceConsole;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

@TargetVersion(4)
public class SnakePipe extends DefaultInputActionPipe implements Console {

    private final String UP = "8";
    private final String DOWN = "2";
    private final String LEFT = "4";
    private final String RIGHT = "6";

    private Game game;

    public SnakePipe(int id) {
        super(id);
        game = new Game();
        game.create(new Maze(1, 1)/**TODO**/, new Snake(), this);
        addInputCallback(mInputCallback);
    }

    @Override
    public void draw(byte[][] matrix) {

    }

    @Override
    public String getDisplayName() {
        return "$snake";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"snake"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {

    }

    private void ready(){
        getConsole().input("Use 8 for up, 2 for down, 4 for left and 6 for right. Enter anything to start.");
        getConsole().waitForUserInput(new UserInputCallback() {
            @Override
            public void onUserInput(String userInput) {
                int width = getConsole().getConsoleInfo().width;
                Maze maze = new Maze(width, width);
                game.create(maze, new Snake(), SnakePipe.this);
                game.start();
            }
        });
    }

    private InputCallback mInputCallback = new InputCallback() {
        @Override
        public void onInput(String character) {
            switch (character) {
                case UP:
                    game.getSnake().up();
                    break;
                case DOWN:
                    game.getSnake().down();
                    break;
                case LEFT:
                    game.getSnake().left();
                    break;
                case RIGHT:
                    game.getSnake().right();
                    break;
            }
        }
    };
}
