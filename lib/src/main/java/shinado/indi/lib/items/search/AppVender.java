package shinado.indi.lib.items.search;

import android.content.Context;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.launcher.SearchHelper;
import shinado.indi.lib.util.AppManager;


public class AppVender extends SearchVender {

	private AppManager appManager;

	@Override
	public void init(Context context, SearchHelper s, int type) {
		super.init(context, s, type);
		//TODO takes a lot of time
		refreshAppMessage();
	}

	@Override
	public int function(VenderItem rs) {
		String body = rs.getValue();
		if(body.equals("android")){
    		return -1;
    	}else if(body.equals("search")){
    		return 1;
    	}
		try {
			appManager.launch( body);
			addFrequency(rs);
		} catch (Exception e) {
			//if there is any trouble, remove it
			removeFrequency(rs);
			removeItemInMap(rs);
		}
	    return 2;
	}

	private void refreshAppMessage(){
		appManager = AppManager.getAppManager(context);
		appManager.addOnAppChangeListener(new AppManager.OnAppChangeListener() {
			@Override
			public void onAppChange(int flag, VenderItem vo) {
				System.out.println("appChange:search:"+flag+","+vo.getValue());
				if(flag == AppManager.OnAppChangeListener.FLAG_ADD){
					putItemInMap(vo);
					addFrequency(vo);
				}else if(flag == AppManager.OnAppChangeListener.FLAG_REMOVE){
					removeItemInMap(vo);
					removeFrequency(vo);
				}
			}
		});
		for(int i=0 ;i<appManager.getAppCount(); i++){
			VenderItem vo = appManager.getResult(i);
			fulfillResult(vo);
			putItemInMap(vo);
		}
		resultStack.push(frequentItems);
	}

}
