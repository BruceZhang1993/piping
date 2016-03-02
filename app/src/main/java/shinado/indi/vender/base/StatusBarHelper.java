package shinado.indi.vender.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import indi.shinado.piping.launcher.BaseLauncherView;
import indi.shinado.piping.statusbar.StatusBar;
import indi.shinado.piping.statusbar.Statusable;
import shinado.indi.vender.R;

public class StatusBarHelper implements Statusable{

    private HashMap<Integer, View> mStatusBarMap = new HashMap<>();
    private LinearLayout mStatusBarLayout;

    public void setupStatusBar(BaseLauncherView launcher) {
        ArrayList<StatusBar> mStatusBars = launcher.addStatusable(this);
        mStatusBarMap.put(Statusable.ID_TIME, launcher.findViewById(R.id.status_time_tv));

        mStatusBarLayout = (LinearLayout) launcher.findViewById(R.id.status_right_ll);
        mStatusBarLayout.removeAllViews();

        for (StatusBar sb : mStatusBars) {
            sb.register();
            if (sb.id == Statusable.ID_TIME) {
                continue;
            }

            //add view and put in map
            int[] flags = sb.getFlags();
            View view;
            if (sb.getFlags() != null) {
                view = addStatusBarView(launcher, mStatusBarLayout, flags);
            } else {
                view = addStatusBarView(launcher, mStatusBarLayout, new int[]{sb.id});
            }
            mStatusBarMap.put(sb.id, view);
        }

    }

    private View addStatusBarView(Context context, ViewGroup parent, int[] keys) {
        if (keys.length > 1) {
            LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_connection, parent, false);
            for (int key : keys) {
                addStatusBarView(context, layout, new int[]{key});
            }
            parent.addView(layout);
            return layout;
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.text_status_bar, parent, false);
            view.setTag(keys[0]);
            parent.addView(view);
            return view;
        }
    }


    @Override
    public void onStatusBarNotified(int id, int flag, String msg) {
        View view = mStatusBarMap.get(id);
        TextView textView = null;
        if (view instanceof TextView) {
            textView = (TextView) view;
        } else if (view instanceof ViewGroup) {
            textView = (TextView) view.findViewWithTag(flag);
        }
        textView.setText(msg);
    }

}
