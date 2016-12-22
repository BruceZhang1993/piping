package indi.shinado.piping.pipes.impl;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.search.SearchableActionPipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import indi.shinado.piping.storage.IDataBaseReference;
import indi.shinado.piping.storage.IDataSnapshot;
import indi.shinado.piping.storage.IDatabaseError;
import indi.shinado.piping.storage.StorageFactory;

public class StoragePipe extends SearchableActionPipe{

    private Pipe addPipe, pushPipe;
    private IDataBaseReference mStorage;

    public StoragePipe(int id) {
        super(id);

        addPipe = new Pipe(id, "$add", new SearchableName("add"), "$#add");
        pushPipe = new Pipe(id, "$push", new SearchableName("push"), "$#push");

        WifiManager manager = (WifiManager) getLauncher().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        mStorage = StorageFactory.getStorage(context).child("local").child(address).child("storage");
    }

    @Override
    public String getKeyword() {
        return "db";
    }

    @Override
    public void start() {
        super.start();
        mStorage.addChildEventListener(mOnChildEventListener);

    }

    private IDataBaseReference.OnChildEventListener mOnChildEventListener = new IDataBaseReference.OnChildEventListener() {
        @Override
        public void onChildAdded(IDataSnapshot dataSnapshot, String s) {
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

    @Override
    public void destroy() {

    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {

    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {

    }

    @Override
    protected void execute(Pipe rs) {

    }

    @Override
    public void load(AbsTranslator translator, OnItemsLoadedListener listener, int total) {

    }

    class SimplePipe{
        public String name;
        public String[] params;
    }

}
