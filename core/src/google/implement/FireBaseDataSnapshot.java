package indi.shinado.piping.storage.implement;

import com.google.firebase.database.DataSnapshot;

import indi.shinado.piping.storage.IDataSnapshot;


public class FireBaseDataSnapshot implements IDataSnapshot{

    private DataSnapshot mSnapshot;

    public FireBaseDataSnapshot(DataSnapshot mSnapshot) {
        this.mSnapshot = mSnapshot;
    }

    @Override
    public IDataSnapshot child(String name) {
        return null;
    }

    @Override
    public <T> T getValue(Class<T> c) {
        return null;
    }
}
