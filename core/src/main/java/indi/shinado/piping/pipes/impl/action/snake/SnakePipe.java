package indi.shinado.piping.pipes.impl.action.snake;

import android.app.Activity;

import com.shinado.annotation.TargetVersion;

import indi.shinado.piping.launcher.InputCallback;
import indi.shinado.piping.launcher.SingleLineInputCallback;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

@TargetVersion(4)
public class SnakePipe{

//        extends DefaultInputActionPipe implements Console {
//
//    private final String UP = "2";
//    private final String DOWN = "8";
//    private final String LEFT = "4";
//    private final String RIGHT = "6";
//
//    private Game game;
//
//    public SnakePipe(int id) {
//        super(id);
//        game = new Game();
//    }
//
//    @Override
//    public void draw(byte[][] matrix) {
//        StringBuilder sb = new StringBuilder();
//        for (int y = 0; y < matrix.length; y++) {
//            for (int x = 0; x < matrix[y].length; x++) {
//                if (matrix[y][x] == Game.EMPTY) {
//                    sb.append("█");
//                } else {
//                    sb.append("　");
//                }
//            }
//            sb.append("\n");
//        }
//        getConsole().replaceCurrentLine(sb.toString());
//    }
//
//    @Override
//    public void die() {
//        ((Activity)getLauncher()).runOnUiThread(new Runnable() {
//            public void run() {
//                stop();
//            }
//        });
//    }
//
//    @Override
//    public String getDisplayName() {
//        return "$snake";
//    }
//
//    @Override
//    public SearchableName getSearchable() {
//        return new SearchableName(new String[]{"snake"});
//    }
//
//    @Override
//    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
//        ready();
//    }
//
//    @Override
//    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
//        ready();
//    }
//
//    @Override
//    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
//        ready();
//    }
//
//    private void ready() {
//        getConsole().display("Use 2 for up, 8 for down, 4 for left and 6 for right. Enter anything to start.");
//        getConsole().waitForSingleLineInput(new SingleLineInputCallback() {
//            @Override
//            public void onUserInput(String userInput) {
//                int width = getConsole().getConsoleInfo().width;
//                Maze maze = new Maze(width, (int) (width * 0.75f));
//                getConsole().blindMode();
//                getConsole().hideInitText();
//                game.create(maze, new Snake(), SnakePipe.this);
//                game.start();
//                getConsole().addInputCallback(mInputCallback);
//            }
//        });
//    }
//
//    @Override
//    public void intercept() {
//        stop();
//    }
//
//    private void stop() {
//        game.stop();
//        getConsole().quitBlind();
//        getConsole().showInitText();
//        getConsole().removeInputCallback(mInputCallback);
//    }
//
//    private InputCallback mInputCallback = new InputCallback() {
//        @Override
//        public void onInput(String character) {
//            switch (character) {
//                case UP:
//                    game.getSnake().up();
//                    break;
//                case DOWN:
//                    game.getSnake().down();
//                    break;
//                case LEFT:
//                    game.getSnake().left();
//                    break;
//                case RIGHT:
//                    game.getSnake().right();
//                    break;
//            }
//        }
//    };

}
