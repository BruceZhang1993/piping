package shinado.com.process;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.shinado.piping.geek.header.IHeadView;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.List;

import indi.shinado.piping.process.ProcessManager;
import indi.shinado.piping.process.models.AndroidAppProcess;
import indi.shinado.piping.process.models.Status;

public class ProcessView implements IHeadView {

    private int mMaxProceesses = 5;
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

    private float density;
    private DisplayMetrics dm;

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
    }

    public View getView(Context context, ViewGroup parent) {
        this.mContext = context;
        dm = context.getResources().getDisplayMetrics();

        view = inflateView();

        mHandler = new TheHandler(this);
        return view;
    }

    private View inflateView() {
        LinearLayout root = new LinearLayout(mContext, null);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        memoryTv = new TextView(mContext, null);
        memoryTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        memoryTv.setTextColor(0xffff0000);
        root.addView(memoryTv);

        processTl = new TableLayout(mContext, null);
        processTl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(processTl);

        TableRow tableRow = new TableRow(mContext, null);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)));
        tableRow.setBackgroundColor(0xff00ff00);
        processTl.addView(tableRow);

        TextView pidTv = inflateTextViewHead();
        pidTv.setText("Pid");
        tableRow.addView(pidTv);

        TextView nameTv = inflateTextViewHead();
        nameTv.setText("Name");
        tableRow.addView(nameTv);

        TextView vmTv = inflateTextViewHead();
        vmTv.setText("VmRSS");
        tableRow.addView(vmTv);

        TextView threadsTv = inflateTextViewHead();
        threadsTv.setText("Threads");
        tableRow.addView(threadsTv);

        TextView stateTv = inflateTextViewHead();
        stateTv.setText("State");
        tableRow.addView(stateTv);

        return root;
    }

    private TextView inflateTextViewContent(){
        TextView textView = inflateTextViewHead();
        textView.setTextColor(0xffffffff);
        return textView;
    }

    private TextView inflateTextViewHead() {
        TextView textView = new TextView(mContext, null);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(0xff000000);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = (int) (4 * dm.density);
        params.rightMargin = (int) (4 * dm.density);
        textView.setLayoutParams(params);

        return textView;
    }



    public void onPause() {
        mThreadPause = true;
    }

    public void onResume() {
        mThreadPause = false;
    }

    public void onCreate() {
        mThreadRunning = true;
        mThreadPause = false;
        mThread = new ProcessThread();
        mThread.start();
    }

    public void onDestroy() {
        mThreadPause = false;
        mThreadRunning = false;
    }

    public void notifyUI() {
        mThread.interrupt();
    }

    private String getMemoryInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.availMem;
        String result = Formatter.formatFileSize(context, memSize);
        result += "/" + Formatter.formatFileSize(context, memoryInfo.totalMem);
        return result;
    }

    private void updateUI(List<AndroidAppProcess> list) {
        memoryTv.setText(String.format("[Memory: $s]", getMemoryInfo(mContext)));


        processTl.removeViews(1, processTl.getChildCount() - 1);
        int count = 0;

        for (AndroidAppProcess item : list) {
            TableRow row = inflateContentView();
//                    (TableRow) LayoutInflater.from(mContext).inflate(R.layout.process_item, processTl, false);
            TextView pidTv = (TextView) row.findViewWithTag("pid");//findViewById(R.id.process_item_pid);
            TextView nameTv = (TextView) row.findViewWithTag("name");//findViewById(R.id.process_item_name);
            TextView stateTv = (TextView) row.findViewWithTag("state");//findViewById(R.id.process_item_state);
            TextView threadsTv = (TextView) row.findViewWithTag("threads");//findViewById(R.id.process_item_threads);
            TextView vmrssTv = (TextView) row.findViewWithTag("vm");//findViewById(R.id.process_item_vmrss);

            if (mTextSize > 0) {
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
            pidTv.setText(item.pid + "");
            nameTv.setText(status.getValue("Name"));
            stateTv.setText(status.getValue("State"));
            threadsTv.setText(status.getValue("Threads"));
            vmrssTv.setText(status.getValue("VmRSS"));

            processTl.addView(row);

            if (++count >= mMaxProceesses) {
                break;
            }
        }

    }

    private TableRow inflateContentView(){
        TableRow tableRow = new TableRow(mContext, null);

        TextView pidTv = inflateTextViewContent();
        pidTv.setTag("pid");
        tableRow.addView(pidTv);

        TextView nameTv = inflateTextViewContent();
        nameTv.setTag("namt");
        tableRow.addView(nameTv);

        TextView stateTv = inflateTextViewContent();
        stateTv.setTag("state");
        tableRow.addView(stateTv);

        TextView threadsTv = inflateTextViewContent();
        threadsTv.setTag("threads");
        tableRow.addView(threadsTv);

        TextView vmTv = inflateTextViewContent();
        vmTv.setTag("vm");
        tableRow.addView(vmTv);

        return tableRow;
    }

    class ProcessThread extends Thread {
        @Override
        public void run() {
            while (mThreadRunning) {
                while (mThreadPause) {
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

    static class TheHandler extends Handler {

        private SoftReference<ProcessView> mRef;

        public TheHandler(ProcessView view) {
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
