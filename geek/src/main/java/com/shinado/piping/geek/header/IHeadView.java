
package com.shinado.piping.geek.header;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by shinado on 16/5/11.
 */
public interface IHeadView {

     void onCreate();
     void onDestroy();
     void onPause();
     void onResume();
     View getView(Context context, ViewGroup parent);
     void notifyUI();

}
