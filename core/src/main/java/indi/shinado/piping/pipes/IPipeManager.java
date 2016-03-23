package indi.shinado.piping.pipes;

import java.util.ArrayList;

import indi.shinado.piping.launcher.InputCallback;
import indi.shinado.piping.pipes.entity.PipeEntity;

public interface IPipeManager {

    void addNewPipe(PipeEntity entity);

    boolean removePipe(int id);

    ArrayList<BasePipe> getAllPipes();

    void destroy();

}
