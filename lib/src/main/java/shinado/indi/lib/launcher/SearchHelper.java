package shinado.indi.lib.launcher;

import android.content.Context;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeSet;

import shinado.indi.lib.items.BaseVender;
import shinado.indi.lib.items.Vender;
import shinado.indi.lib.items.VenderFactory;
import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.action.CopyVender;
import shinado.indi.lib.items.action.InstallVender;
import shinado.indi.lib.items.search.AppVender;
import shinado.indi.lib.items.search.ContactVender;
import shinado.indi.lib.items.search.translator.AbsTranslator;
import shinado.indi.lib.items.search.translator.TranslatorFactory;
import shinado.indi.vender.lib.BuildConfig;


public class SearchHelper {

	private boolean blockInput = false;
	private static final String TAG = "SearchHelper";
	private Searchable searchable;
	
	/**
	 * Input for searching components
	 */
	private EditText input_search;

	/**
	 * searching results
	 */
	private TreeSet<VenderItem> result_set = new TreeSet<>();

	private int mFunctionSize;

	protected Hashtable<Integer, BaseVender> functionMap;

	private VenderItem mPrevisouItem = null;

	public SearchHelper(Context context, Searchable searchable){
		Log.d(TAG, "start");
		this.searchable = searchable;

		functionMap = new Hashtable<>();
		loadLocalFunctions(context);
		loadExternalFunctions(context);

		Collection<BaseVender> functions = functionMap.values();
		mFunctionSize = functions.size();

		initSearchView();
		Log.d(TAG, "end");
	}

	public void addNewVender(Context context, Vender v){
		v.save();
		createVender(context, v);
	}

	private void loadExternalFunctions(Context context){
		List<Vender> functions = new ArrayList<>();//new Select().all().from(Vender.class).execute();
		functions.add(new Vender(10, Environment.getExternalStorageDirectory() + "/vender/test.dex",
				"test", "me", "", "csu.org.item1.TestFunction"));
		for (Vender f : functions){
			createVender(context, f);
		}
	}

	private void createVender(Context context, Vender f){
		BaseVender bf = VenderFactory.getFunction(context, f);
		if (bf != null){
			bf.init(context, this, f.sid);
			bf.setOnResultChangedListener(mOnResultChangedListener);
			functionMap.put(f.sid, bf);
		}else {
			if (BuildConfig.DEBUG){
				Log.d(TAG, "find null in " + f.pkg);
			}
		}
	}

	private void loadLocalFunctions(Context context){
		AbsTranslator translator = TranslatorFactory.getTranslator(context, 2);

		AppVender appFunction = new AppVender();
		appFunction.init(context, this, BaseVender.TYPE_APP);
		appFunction.setOnResultChangedListener(mOnResultChangedListener);
		appFunction.load(translator);
		functionMap.put(BaseVender.TYPE_APP, appFunction);

		ContactVender contactFunction = new ContactVender();
		contactFunction.init(context, this, BaseVender.TYPE_CONTACT);
		contactFunction.load(translator);
		functionMap.put(BaseVender.TYPE_CONTACT, contactFunction);
		contactFunction.setOnResultChangedListener(mOnResultChangedListener);

		InstallVender insFunction = new InstallVender();
		insFunction.init(context, this, VenderItem.BUILD_IN_ID_INSTALL);
		functionMap.put(VenderItem.BUILD_IN_ID_INSTALL, insFunction);
		insFunction.setOnResultChangedListener(mOnResultChangedListener);

		CopyVender cpFunction = new CopyVender();
		cpFunction.init(context, this, VenderItem.BUILD_IN_ID_COPY);
		functionMap.put(VenderItem.BUILD_IN_ID_COPY, cpFunction);
		cpFunction.setOnResultChangedListener(mOnResultChangedListener);
	}

	private BaseVender.OnResultChangedListener mOnResultChangedListener = new BaseVender.OnResultChangedListener(){

		private int resultCount = 0;

		@Override
		public void onResultChange(TreeSet<VenderItem> list, boolean flag) {
			if (list != null){
				result_set.addAll(list);
			}
			synchronized (this){
				resultCount++;
			}
			if (resultCount >= mFunctionSize){
				onNotify();
				resultCount = 0;
			}
		}
	};

	private void clearSearch(){
		input_search.setText("");
	}

	private void initSearchView(){
		input_search = searchable.shapSearchInput();

		setOnKeyboardListener(searchable.getKeyboard());
		setTextChangeListener();

		input_search.setText("");
		input_search.setEnabled(false);
		input_search.setFocusable(false);

	}

	private void setTextChangeListener(){
		input_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().endsWith(".")){
					if(result_set.size() > 0){
						mPrevisouItem = (VenderItem) result_set.toArray()[0];
					}
				}
				input_search.setSelection(count);
				result_set.clear();
				doSearch(mPrevisouItem, s.toString(), count - before);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	private void setOnKeyboardListener(ViewGroup root){
		if(root == null){
			return;
		}
		int count = root.getChildCount();
		for(int i=0; i<count; i++){
			View v = root.getChildAt(i);
			if(v instanceof ViewGroup){
				setOnKeyboardListener((ViewGroup) v);
			}else{
				v.setOnClickListener(onKeyboardListener);
			}
		}
	}
	
	private OnClickListener onKeyboardListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
	    	pressKey(v);
		}
	};

	public void launch(VenderItem result){
	    BaseVender func = functionMap.get(result.getId());
		func.function(result);
		clearSearch();
	}

	public void launch(int index){
		if(result_set != null && index < result_set.size()){
			VenderItem result = (VenderItem) result_set.toArray()[index];
			BaseVender func = functionMap.get(result.getId());
			func.function(result);
			clearSearch();
		}
	}

	private void onNotify(){
		searchable.onNotified(result_set);
	}

	private void doSearch(VenderItem prev, String key, int length){
		for(BaseVender fuc:functionMap.values()){
			fuc.search(prev, key, length);
		}
	}

	public void removeFrequency(VenderItem result){
		functionMap.get(result.getId()).removeFrequency(result);
	}
	
	public void addFrequency(VenderItem result){
		functionMap.get(result.getId()).addFrequency(result);
	}
	
	public Searchable getSearchable(){
		return searchable;
	}

	private void pressKey(View v){
		if (blockInput){
			return;
		}
		String key = (String) v.getTag();
    	if(key.length() == 1){
    		String text = input_search.getText().toString();
    		input_search.setText(text+key);
    	}else{
    		String text = input_search.getText().toString();
    		if(key.equals("backspace")){
        		//EditView must have something
        		if(text.length() > 0){
        			input_search.setText(text.subSequence(0, text.length()-1));
        		}
    		}else if(key.equals("shift")){
    			searchable.onShift();
    		}else if(key.equals("space")){
    			input_search.setText(text+" ");
    		}else if(key.equals("enter")){
    			searchable.onEnter();
    		}
    	}
	}

	public void blockInput(){
		this.blockInput = true;
	}

	public void releaseInput(){
		this.blockInput = false;
	}
}
