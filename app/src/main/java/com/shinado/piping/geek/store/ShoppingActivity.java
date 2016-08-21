package com.shinado.piping.geek.store;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shinado.piping.geek.header.HeadEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csu.org.dependency.volley.Listener;
import csu.org.dependency.volley.VolleyProvider;
import indi.shinado.piping.download.DownloadImpl;
import indi.shinado.piping.download.Downloadable;
import indi.shinado.piping.pipes.SystemInfo;
import shinado.indi.vender.R;

public class ShoppingActivity extends Activity {

    private static final String URL_STORE = "http://1.yilaunch.sinaapp.com/head/list.php";
    private StoreAdapter mAdapter;
    private List<HeadEntity> mLocalItems;
    private DownloadImpl mDownloadImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_activity);

        setupDownloader();
        initViews();
        loadItems();
    }

    public void quit(View view) {
        finish();
    }

    private void setupDownloader() {
        mDownloadImpl = new DownloadImpl();
        mDownloadImpl.setOnDownloadListener(new DownloadImpl.OnDownloadListener() {
            @Override
            public void onStart(Downloadable v) {

            }

            @Override
            public void onFinish(Downloadable v) {
                HeadEntity headEntity = (HeadEntity) v;
                headEntity.isDownloading = false;
                headEntity.isLocal = true;
                headEntity.save();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Downloadable v) {
                HeadEntity headEntity = (HeadEntity) v;
                headEntity.isDownloading = false;
                mAdapter.notifyDataSetChanged();
                Toast.makeText(ShoppingActivity.this, "Downloading " + headEntity.name +
                        " failed, please try again", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProgress(Downloadable v, int prgs, int current) {
                HeadEntity headEntity = (HeadEntity) v;
                headEntity.downloadProgress = prgs;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadItems() {
        String localWidgets = "";
        mLocalItems = new Select().all().from(HeadEntity.class).execute();
        if (mLocalItems != null) {
            for (HeadEntity entity : mLocalItems) {
                entity.isLocal = true;
                localWidgets += entity.sid + "-";
            }
            mAdapter.addAll(mLocalItems);
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("local", localWidgets);
        params.put("version", "" + new SystemInfo(this).getVersionCode());
        new VolleyProvider().handleData(URL_STORE, params, Result.class,
                new Listener.Response<Result>() {
                    @Override
                    public void onResponse(Result obj) {
                        mAdapter.addAll(obj.list);
                    }
                }, null);
    }

    private void initViews() {
        mAdapter = new StoreAdapter(this);
        ListView listView = (ListView) findViewById(R.id.store_list);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HeadEntity entity = (HeadEntity) adapterView.getAdapter().getItem(i);
                if (entity.isLocal) {
                    if (entity.selected == 0) {
                        entity.selected = 1;
                        entity.save();
                        setResult(RESULT_OK);
                        finish();
                    }
                } else {
                    mAdapter.download(entity);
                }
            }
        });
    }

    class StoreAdapter extends BaseAdapter {

        private ArrayList<HeadEntity> mItems = new ArrayList<>();
        private Context context;

        public void addAll(List<HeadEntity> list) {
            this.mItems.addAll(list);
            notifyDataSetChanged();
        }

        public StoreAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return mItems == null ? 0 : mItems.size();
        }

        @Override
        public HeadEntity getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            HeadEntity item = getItem(i);
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.store_item, viewGroup, false);
            }
            ImageView imgView = (ImageView) view.findViewById(R.id.store_item_img);
            TextView nameTv = (TextView) view.findViewById(R.id.store_item_name);
            TextView statusTv = (TextView) view.findViewById(R.id.store_item_status);

            ImageLoader.getInstance().displayImage(item.imgUrl, imgView);
            nameTv.setText(item.name);

            if (item.isLocal) {
                statusTv.setVisibility(View.VISIBLE);
                if (item.selected != 0) {
                    statusTv.setText("Selected");
                } else {
                    statusTv.setText("Downloaded");
                }
            } else {

                if (item.isDownloading) {
                    statusTv.setText("Loading:..." + item.downloadProgress + "%");
                }else {
                    statusTv.setText(Math.abs(item.price) < 0.01f ? "Free" : "$" + item.price);
                }
            }


            return view;
        }

        public void download(HeadEntity entity) {
            entity.isDownloading = true;
            mDownloadImpl.addToQueue(entity);
            mDownloadImpl.start();
        }
    }

    public class Result {
        ArrayList<HeadEntity> list;
    }
}
