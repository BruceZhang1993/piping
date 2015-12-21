package shinado.indi.lib.items.action;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.TreeSet;

import shinado.indi.lib.items.BaseVender;
import shinado.indi.lib.items.VenderItem;

public abstract class ActionVender extends BaseVender {

    protected FunctionHandler mHandler;

    public ActionVender() {
        mHandler = new FunctionHandler(this);
    }

    static class FunctionHandler extends Handler {

        private WeakReference<ActionVender> handlerWeakReference;

        public FunctionHandler(ActionVender ifc){
            handlerWeakReference = new WeakReference<>(ifc);
        }

        @Override
        public void handleMessage(Message msg){
            Log.d("IFC", "handleMessage");
            ActionVender ifc = handlerWeakReference.get();
            TreeSet<VenderItem> result = null;
            Object obj = msg.obj;
            if (obj != null){
                VenderItem rs = (VenderItem) obj;
                result = new TreeSet<>();
                result.add(rs);
            }

            if(ifc.mOnResultChangedListener != null){
                ifc.mOnResultChangedListener.onResultChange(result, false/*ifc.resultStack.size() == 1*/);
            }
        }
    }

    //rule:
    //[key].[instruction] [-option]...
    @Override
    public void search(VenderItem prev, String key, int length) {
        VenderItem result = getResult();
        result.setSuccessor(prev);
        result.setType(VenderItem.TYPE_ACTION);
        if (!key.contains(".")){
            result.setValue(key);
        }else{
            int indexOfDot = key.indexOf(".");
            String value = "";
            //e.g. dic.ins
            //     value = "dic"
            if (indexOfDot != 0){
                value = key.substring(0, indexOfDot);
            }

            String[] split = key.split(" ", 2);
            //the key to the action
            //e.g. dic.ins => "ins"
            //e.g. dic.    => ""
            String sKey = split[0].substring(indexOfDot+1);

            if (sKey.length() == 0){
                result.setValue(value);
            }else {
                if (contains(result.getName(), sKey)){
                    //e.g. dic.ins -ls => "dic -ls"
                    if (split.length > 1){
                        value += " " + split[1];
                    }
                    result.setValue(value);
                }else{
                    result = null;
                }
            }
        }
        notify(result);

    }

    protected void notify(VenderItem result){
        mHandler.obtainMessage(0, result).sendToTarget();
    }

    protected  abstract VenderItem getResult();
}
