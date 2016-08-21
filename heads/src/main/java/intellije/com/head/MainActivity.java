package intellije.com.head;

import android.app.Activity;
import android.os.Bundle;

import com.shinado.piping.geek.header.IHeadView;

import shinado.com.process.ProcessView;

/**
 * Created by shinado on 16/5/13.
 */
public class MainActivity extends Activity{

    private IHeadView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mView = new ProcessView();
        mView.onCreate();
        setContentView(mView.getView(this, null));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.onResume();
    }
}
