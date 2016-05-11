package indi.shinado.piping.process;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.shinado.core.R;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.List;

import indi.shinado.piping.process.models.AndroidAppProcess;
import indi.shinado.piping.process.models.Status;

public class ProcessView{

    private int mMaxProceesses;
    private int mInterval = 30 * 1000;

    private float mTextSize = -1;
    private View view;
    private TextView memoryTv;
    private TextView taskTv;
    private TableLayout processTl;

    private ProcessThread mThread;
    private Context mContext;
    private TheHandler mHandler;
    private boolean mThreadRunning = false;
    private boolean mThreadPause = true;

    public ProcessView(Context context, int maxProcessCount){
        this.mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.process_layout, null);
        memoryTv = (TextView) view.findViewById(R.id.process_layout_memory);
        taskTv = (TextView) view.findViewById(R.id.process_layout_tasks);
        processTl = (TableLayout) view.findViewById(R.id.process_layout_procs_tl);

        mMaxProceesses = maxProcessCount;

        mHandler = new TheHandler(this);
    }

    public void setTextSize(float textSize){
        this.mTextSize = textSize;
    }

    public View getView(){
        return view;
    }

    public void onPause(){
        mThreadPause = true;
    }

    public void onResume(){
        mThreadPause = false;
    }

    public void onCreate(){
        mThreadRunning = true;
        mThreadPause = false;
        mThread = new ProcessThread();
        mThread.start();
    }

    public void onDestroy(){
        mThreadPause = false;
        mThreadRunning = false;
    }

    public void notifyUI(){
        mThread.interrupt();
    }

    private void updateUI(List<AndroidAppProcess> list){
        memoryTv.setText(mContext.getResources().getString(R.string.process_memory, ProcessManager.getMemoryInfo(mContext)));


        processTl.removeViews(1, processTl.getChildCount()-1);
        int count = 0;

        for (AndroidAppProcess item : list){
            TableRow row = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.process_item, processTl, false);
            TextView pidTv = (TextView) row.findViewById(R.id.process_item_pid);
            TextView nameTv = (TextView) row.findViewById(R.id.process_item_name);
            TextView stateTv = (TextView) row.findViewById(R.id.process_item_state);
            TextView threadsTv = (TextView) row.findViewById(R.id.process_item_threads);
            TextView vmrssTv = (TextView) row.findViewById(R.id.process_item_vmrss);

            if (mTextSize > 0){
                pidTv.setTextSize(mTextSize);
                nameTv.setTextSize(mTextSize);
                stateTv.setTextSize(mTextSize);
                threadsTv.setTextSize(mTextSize);
                vmrssTv.setTextSize(mTextSize);
            }


            Status status = null;
            try {
                status = item.status();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            pidTv.setText(item.pid+"");
            nameTv.setText(status.getValue("Name"));
            stateTv.setText(status.getValue("State"));
            threadsTv.setText(status.getValue("Threads"));
            vmrssTv.setText(status.getValue("VmRSS"));

            processTl.addView(row);

            if (++count >= mMaxProceesses){
                break;
            }
        }

    }

    class ProcessThread extends Thread{
        @Override
        public void run() {
            while (mThreadRunning){
                while (mThreadPause){
                    try {
                        sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                List<AndroidAppProcess> list = ProcessManager.getRunningAppProcesses(mContext);
                mHandler.obtainMessage(0, list).sendToTarget();
                try {
                    sleep(mInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    static class TheHandler extends Handler{

        private SoftReference<ProcessView> mRef;

        public TheHandler(ProcessView view){
            mRef = new SoftReference<ProcessView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            ProcessView processView = mRef.get();
            List<AndroidAppProcess> list = (List<AndroidAppProcess>) msg.obj;
            processView.updateUI(list);
        }
    }

}
