package indi.shinado.piping.storage.implement;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import indi.shinado.piping.storage.IDataBaseReference;


public class FirebaseDatabaseReference implements IDataBaseReference{

    private DatabaseReference mDatabase;

    public FirebaseDatabaseReference(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public FirebaseDatabaseReference() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public IDataBaseReference push() {
        return new FirebaseDatabaseReference(mDatabase.push());
    }

    @Override
    public void setValue(Object obj) {
        mDatabase.setValue(obj);
    }

    @Override
    public IDataBaseReference child(String key) {
        return new FirebaseDatabaseReference(mDatabase.child(key));
    }

    @Override
    public void addChildEventListener(final OnChildEventListener listener) {
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                listener.onChildAdded(new FireBaseDataSnapshot(dataSnapshot), s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                listener.onChildChanged(new FireBaseDataSnapshot(dataSnapshot), s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                listener.onChildRemoved(new FireBaseDataSnapshot(dataSnapshot));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                listener.onChildMoved(new FireBaseDataSnapshot(dataSnapshot), s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onCancelled(new FireBaseDatabaseError(databaseError));
            }
        });
    }
}
