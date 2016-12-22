package indi.shinado.piping.storage.implement;

import com.google.firebase.database.DatabaseError;

import indi.shinado.piping.storage.IDatabaseError;

public class FireBaseDatabaseError implements IDatabaseError{

    private DatabaseError error;

    public FireBaseDatabaseError(DatabaseError error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return error.toString();
    }
}
