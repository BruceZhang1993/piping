package shinado.indi.vender.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.activeandroid.query.Select;
import java.util.ArrayList;
import java.util.List;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.FrequentItem;
import indi.shinado.piping.util.android.AppManager;

public class PanelFragment extends Fragment {

    private List<Pipe> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initData() {
        AppManager appManager = AppManager.getInstance();
        List<FrequentItem> frequentItems = new Select().from(FrequentItem.class).execute();
        if (frequentItems.size() == 0) {
            for (int i=0; i<appManager.getAppCount(); i++){
                list.add(appManager.getResult(i));
            }
        } else {
            for (FrequentItem item : frequentItems) {
                list.add(appManager.getResult(item.key, false));
            }
        }

    }

    private BaseAdapter mAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    };
}
