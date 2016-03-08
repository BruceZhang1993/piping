package indi.shinado.piping.pipes;

import java.util.ArrayList;

import indi.shinado.piping.pipes.entity.PipeEntity;

public interface IPipeManager {

    public void addNewPipe(PipeEntity entity);

    public boolean removePipe(int id);

    public ArrayList<BasePipe> getAllPipes();
}
