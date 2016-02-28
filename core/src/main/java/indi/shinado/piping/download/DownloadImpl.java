package indi.shinado.piping.download;

import android.os.AsyncTask;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import shinado.indi.lib.GlobalDefs;
import shinado.indi.lib.util.FileUtil;

public class DownloadImpl {

    private Queue<Downloadable> mQueue = new LinkedList<>();
    private OnDownloadListener mOnDownloadListener;
    private int mCurrent = 0;
    private DownloadTask mCurrentTast;

    public void addAllIntoQueue(List<Downloadable> list){
        for (Downloadable s:list){
            mQueue.add(s);
        }
    }

    public void addToQueue(Downloadable s){
        mQueue.add(s);
    }

    public void setOnDownloadListener(OnDownloadListener l){
        this.mOnDownloadListener = l;
    }

    public void start(){
        next();
    }

    public void stop(){
        mQueue.clear();
        if (mCurrentTast != null){
            mCurrentTast.cancel(true);
            mCurrentTast = null;
        }
    }

    private void next(){
        if (mCurrentTast != null){
            return;
        }

        if (mQueue.isEmpty()){
            //finish
            return;
        }
        mCurrent++;
        Downloadable s = mQueue.poll();
        new DownloadTask(s).execute();
    }

    class DownloadTask extends AsyncTask<Integer, Integer, String> {

        private float FILE_SIZE = 0;
        private int downloadFileSize = 0;

        private Downloadable Downloadable;

        public DownloadTask(Downloadable Downloadable){
            this.Downloadable = Downloadable;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mCurrentTast = this;
            if (mOnDownloadListener != null){
                mOnDownloadListener.onStart(Downloadable);
            }
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                URL myURL = new URL(Downloadable.getUrl());
                URLConnection conn = myURL.openConnection();
                conn.setReadTimeout(5000);
                conn.connect();
                InputStream is = conn.getInputStream();
                FILE_SIZE = conn.getContentLength();
                System.out.println("file size:"+FILE_SIZE);
                if (FILE_SIZE <= 0) throw new RuntimeException("");
                if (is == null) throw new RuntimeException("stream is null");

                FileUtil.checkDir(GlobalDefs.PATH_HOME);
                FileOutputStream fos = new FileOutputStream(GlobalDefs.PATH_HOME + Downloadable.getFileName());
                byte buf[] = new byte[1024];
                downloadFileSize = 0;
                while(true){
                    int numread = is.read(buf);
                    if (numread == -1){
                        break;
                    }
                    fos.write(buf, 0, numread);
                    downloadFileSize += numread;
                    publishProgress();
                    Thread.sleep(50);
                }
                fos.close();
                is.close();
            } catch (Exception e) {
                if (mOnDownloadListener != null){
                    mOnDownloadListener.onFail(Downloadable);
                }
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            int prgs = (int) ((downloadFileSize/FILE_SIZE) * 100); //(int) (FILE_SIZE - downloadFileSize);

            if (mOnDownloadListener != null){
                mOnDownloadListener.onProgress(Downloadable, prgs, mCurrent);
            }
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (mOnDownloadListener != null){
                mOnDownloadListener.onFinish(Downloadable);
            }
            mCurrentTast = null;
            next();
        }

    }

    public interface OnDownloadListener{
        public void onStart(Downloadable v);
        public void onFinish(Downloadable v);
        public void onFail(Downloadable v);
        public void onProgress(Downloadable v, int prgs, int current);
    }
}
