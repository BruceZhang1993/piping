package indi.shinado.piping.pipes.impl.search;

import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.search.SearchableActionPipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import indi.shinado.piping.storage.IDataBaseReference;
import indi.shinado.piping.storage.IDataSnapshot;
import indi.shinado.piping.storage.IDatabaseError;
import indi.shinado.piping.storage.StorageFactory;

public class PublicPipe extends SearchableActionPipe {

    private OnQuitSearchActionListener mListener;
    private IDataBaseReference mDatabase;

    public PublicPipe(int id) {
        super(id);


    }

    @Override
    public String getKeyword() {
        return "pb";
    }

    @Override
    public void start(OnQuitSearchActionListener listener) {
        this.mListener = listener;
        mDatabase = StorageFactory.getStorage().child("public").child("user");
//        mDatabase.addChildEventListener(eventListener);
    }

    @Override
    public void destroy() {

    }

    @Override
    public Pipe getByValue(String value) {
        return null;
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {

    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {

    }

    @Override
    protected void execute(Pipe rs) {
        cd(rs, getConsoleCallback());
    }

    @Override
    public void load(AbsTranslator translator, OnItemsLoadedListener listener, int total) {

    }

    private void cd(Pipe rs, OutputCallback callback){
        clear();
        getConsole().setIndicator(rs.getDisplayName());
        mDatabase.child(rs.getExecutable()).addChildEventListener(eventListener);
    }

    private void clear(){
        resultMap.clear();
    }

    private IDataBaseReference.OnChildEventListener eventListener = new IDataBaseReference.OnChildEventListener() {
        @Override
        public void onChildAdded(IDataSnapshot dataSnapshot, String s) {
            if (dataSnapshot != null) {
                String value = dataSnapshot.getValue(String.class);
                Pipe pipe = new Pipe(getId(), "#" + value, new SearchableName(value), value);
                putItemInMap(pipe);
            }
        }

        @Override
        public void onChildChanged(IDataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(IDataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(IDataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(IDatabaseError databaseError) {

        }
    };
}
