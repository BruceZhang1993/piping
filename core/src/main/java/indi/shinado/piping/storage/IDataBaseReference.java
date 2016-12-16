package indi.shinado.piping.storage;


public interface IDataBaseReference {

    public IDataBaseReference push();

    public void setValue(Object obj);

    public IDataBaseReference child(String key);

    public void addChildEventListener(OnChildEventListener listener);


    public interface OnChildEventListener {
        public void onChildAdded(IDataSnapshot dataSnapshot, String s);

        public void onChildChanged(IDataSnapshot dataSnapshot, String s);

        public void onChildRemoved(IDataSnapshot dataSnapshot);

        public void onChildMoved(IDataSnapshot dataSnapshot, String s);

        public void onCancelled(IDatabaseError databaseError);
    }
}
