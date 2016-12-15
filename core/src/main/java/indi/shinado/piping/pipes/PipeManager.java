package indi.shinado.piping.pipes;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import indi.shinado.piping.pipes.entity.PipeEntity;

public class PipeManager extends IPipeManager{

    @Override
    protected PipeEntity search(int id) {
        return new Select().from(PipeEntity.class)
                .where("Id = ?", id).executeSingle();
    }

    @Override
    protected void delete(int id) {
        new Delete().from(PipeEntity.class).where("Id = ?", id).execute();
    }
}
