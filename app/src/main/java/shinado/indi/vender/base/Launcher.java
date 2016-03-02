package shinado.indi.vender.base;

public class Launcher
//		extends BaseLauncherView implements Statusable
{
//
//	private final String TAG = "IFC Launcher";
//	private HashMap<Integer, View> mStatusBarMap = new HashMap<>();
//	private LinearLayout mStatusBarLayout;
//	private EditText input_search;
//
//	private boolean initializing = true;
//	private boolean isRun = true;
//	private TextView showView;
//	private ScrollView scrollView;
//	private View keyboard;
//	private String mCurrentLine = "";
//
//	private int resultIndex = 0;
//	private TreeSet<VenderItem> resultList;
//	private String mInputText = "";
//
//	private OutputHandler mHandler;
//	private boolean initiated = false;
//
//	private final String[] INIT_TEXT = {"wsp_stp_auth_build.bui",
//			"Account:******",
//			"Password:***************",
//			"Access granted",
//			"initializing"};
//
//	@Override
//	public void onCreate(Bundle savedInstanceState){
//		super.onCreate(savedInstanceState);
//
//		Log.d(TAG, "start");
//
//		this.setContentView(R.layout.layout_base_launcher);
//
//		mHandler = new OutputHandler(this);
//		initViews();
//
//		addSearchable(this);
//		addFeedable(this);
//		setStatusBar();
//		Log.d(TAG, "end");
//	}
//
//	@Override
//	public void resume(boolean animNotRequired){
//		super.resume(animNotRequired);
//		//if not initiated yet, initiate the text
//		if(!initiated){
//			initText();
//			initiated = true;
//		}
//		//re-initiate text if animation is required
////		else if(!animNotRequired){
////			initText();
////		}
//	}
//
//	private void setStatusBar(){
//		ArrayList<StatusBar> mStatusBars = addStatusable(this);
//		mStatusBarMap.put(Statusable.ID_TIME, findViewById(R.id.status_time_tv));
//
//		mStatusBarLayout = (LinearLayout) findViewById(R.id.status_right_ll);
//		mStatusBarLayout.removeAllViews();
//
//		for (StatusBar sb : mStatusBars){
//			sb.register();
//			if (sb.id == Statusable.ID_TIME){
//				continue;
//			}
//
//			//add view and put in map
//			int[] flags = sb.getFlags();
//			View view;
//			if (sb.getFlags() != null){
//				view = addStatusBarView(mStatusBarLayout, flags);
//			}else {
//				view = addStatusBarView(mStatusBarLayout, new int[]{sb.id});
//			}
//			mStatusBarMap.put(sb.id, view);
//		}
//
//	}
//
//	private View addStatusBarView(ViewGroup parent, int[] keys){
//		if (keys.length > 1){
//			LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_connection, parent, false);
//			for (int key : keys){
//				addStatusBarView(layout, new int[]{key});
//			}
//			parent.addView(layout);
//			return layout;
//		}else {
//			View view = LayoutInflater.from(this).inflate(R.layout.text_status_bar, parent, false);
//			view.setTag(keys[0]);
//			parent.addView(view);
//			return view;
//		}
//	}
//
//	//clean text and start initiate text animation
//	private void initText(){
//		mPreviousLines = new StringBuffer();
//		input_search.setText("");
//		new InitTextingThread().start();
//	}
//
//	private void initViews(){
//		scrollView = (ScrollView) this.findViewById(R.id.scrollView);
//		keyboard = this.findViewById(R.id.keyboard);
//		showView = (TextView) this.findViewById(R.id.displayText);
//		initSearchWidget();
//		startTicking();
//	}
//
//	//ticking_
//	//ticking
//	//ticking_
//	//ticking
//	private void startTicking(){
//		new TickThread().start();
//	}
//
//	@Override
//	public void destroy(){
//		super.destroy();
//
//		isRun = false;
//	}
//
//	@Override
//	public void onFeed(int flag, String msg, String pkg) {
//		//find item and add to resultList
//		VenderItem item = searchHelper.getItem(VenderItem.BUILD_IN_ID_APP, pkg + ",");
//		String display = item.getDisplayName();
//		resultList.clear();
//		resultList.add(item);
//		displayText(display + " - " + msg + "\n");
//		replaceLine(true);
//	}
//
//	@Override
//	public void onStatusBarNotified(int id, int flag, String msg) {
//		View view = mStatusBarMap.get(id);
//		TextView textView = null;
//		if (view instanceof TextView){
//			textView = (TextView) view;
//		} else if (view instanceof ViewGroup){
//			textView = (TextView) view.findViewWithTag(flag);
//		}
//		textView.setText(msg);
//	}
//
//	static class OutputHandler extends Handler{
//
//		private static final int WHAT_TIK = 1;
//		private static final int WHAT_TOK = 0;
//		private static final int WHAT_INPUT = 2;
//		private static final int WHAT_START = 3;
//		private static final int WHAT_HIDE = 4;
//		private static final int WHAT_LINE = 5;
//
//		private WeakReference<Launcher> deskViewWeakReference;
//
//		public OutputHandler(Launcher launcher){
//			deskViewWeakReference = new WeakReference<>(launcher);
//		}
//
//		@Override
//		public void handleMessage(Message msg){
//			Launcher launcher = deskViewWeakReference.get();
//			switch(msg.what){
//				case WHAT_TIK:
//					launcher.showView.setText(launcher.mPreviousLines.toString() + launcher.mCurrentLine + "_");
//					break;
//				case WHAT_TOK:
//					launcher.showView.setText(launcher.mPreviousLines.toString() + launcher.mCurrentLine);
//					break;
//				case WHAT_INPUT:
//					launcher.mPreviousLines.append(msg.obj);
//					launcher.showView.setText(launcher.mPreviousLines.toString());
//					launcher.scrollView.fullScroll(View.FOCUS_DOWN);
//					break;
//				case WHAT_START:
//					launcher.input_search.setText("");
//					launcher.showKeyboard();
//					break;
//				case WHAT_HIDE:
//					launcher.hideKeyboard();
//					break;
//				case WHAT_LINE:
//					launcher.replaceLine((String) msg.obj);
//					break;
//			}
//		}
//	}
//
//	private void initSearchWidget(){
//		input_search = new EditText(this, null);
//	}
//
//	private void hideKeyboard(){
//		keyboard.setVisibility(View.GONE);
//	}
//
//	private void showKeyboard(){
//		int TIME_SHOW = 150;
//		keyboard.setVisibility(View.VISIBLE);
//		TranslateAnimation anim = new TranslateAnimation(
//				Animation.RELATIVE_TO_SELF, 0,
//				Animation.RELATIVE_TO_SELF, 0,
//				Animation.RELATIVE_TO_SELF, 1,
//				Animation.RELATIVE_TO_SELF, 0);
//		anim.setDuration(TIME_SHOW);
//		anim.setInterpolator(new DecelerateInterpolator(3.0f));
//		keyboard.startAnimation(anim);
//	}
//
//	@Override
//	public EditText shapSearchInput() {
//		return input_search;
//	}
//
//	@Override
//	public ViewGroup getKeyboard() {
//		return (ViewGroup) keyboard;
//	}
//
//	@Override
//	public void onNotified(TreeSet<VenderItem> result_set) {
//		Log.d(TAG, "onNotified, size:" + result_set.size());
//		resultList = result_set;
//		resultIndex = 0;
//		replaceLine(false);
//	}
//
//	@Override
//	public void onShift(){
//		if(resultList == null || resultList.size() == 0){
//			return;
//		}
//		resultIndex = (resultIndex+1) % resultList.size();
//		replaceLine(true);
//	}
//
//	@Override
//	public void onDisplay(final String msg, final int flag) {
//		new Thread(){
//			@Override
//			public void run(){
//				switch (flag){
//					case FLAG_REPLACE:
//						mHandler.obtainMessage(OutputHandler.WHAT_LINE, msg).sendToTarget();
//						break;
//					case FLAG_INPUT:
//						typeText(msg);
//						break;
//				}
//			}
//		}.start();
//	}
//
//	@Override
//	public void onEnter() {
//		VenderItem result;
//		try {
//			result = (VenderItem) resultList.toArray()[resultIndex];
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//
//		//add new line
//		mPreviousLines.append(mCurrentLine);
//		mPreviousLines.append("\n");
//		mCurrentLine = "";
//		searchHelper.launch(result);
//	}
//
//	private class InitTextingThread extends Thread{
//		@Override
//		public void run(){
//			while(showView == null);
//			initializing = true;
//
//			mHandler.sendEmptyMessage(OutputHandler.WHAT_HIDE);
//
//			typeTexts();
//			typeDots();
//
//			displayText("\n");
//			try { sleep(30); } catch (InterruptedException e) { e.printStackTrace();}
//			displayText("\n");
//
//			initializing = false;
//			mHandler.sendEmptyMessage(OutputHandler.WHAT_START);
//		}
//
//		private void typeTexts(){
//			for (String str: INIT_TEXT) {
//				Message message = new Message();
//				message.what = OutputHandler.WHAT_INPUT;
//				message.obj = "\n";
//				mHandler.sendMessage(message);
//				typeText(str);
//				try {
//					sleep(150);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//		private void typeDots(){
//			for(int k=0; k<3; k++){
//				displayText(".");
//				try {
//					sleep(150);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	private class TickThread extends Thread{
//		@Override
//		public void run(){
//			int i = 0;
//			while(isRun){
//				try {
//					sleep(500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				if (initializing){
//					continue;
//				}
//				mHandler.sendEmptyMessage(++i%2);
//			}
//		}
//	}
//
//	private void replaceLine(String line){
//		mCurrentLine = line;
//		showView.setText(mPreviousLines.toString() + mCurrentLine);
//	}
//
//	private void replaceLine(boolean ignoreMatch){
//		if(initializing){
//			return;
//		}
//		String value = input_search.getText().toString();
//		Log.d(TAG, "value:"+value +"," + mInputText);
//		if(mInputText.equals(value)){
//			if(!ignoreMatch){
//				return;
//			}
//		}
//		mInputText = value;
//
//		VenderItem result = null;
//		try {
//			result = (VenderItem) resultList.toArray()[resultIndex];
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		String line = "";
//		if(result == null){
//			line = "null -"+value;
//		}else{
//			line += result.getDisplayName();
//			line += " -" + value;
//			Log.v(TAG, "input:" + result.getInstruction());
//			Log.v(TAG, "display:"+line);
//		}
//
//		mCurrentLine = line;
//		showView.setText(mPreviousLines.toString() + mCurrentLine);
//	}
//
//	/*
//	 * display on screen as if it's typed into
//	 */
//	private void typeText(String str){
//		searchHelper.blockInput();
//		for(int j=0; j<str.length();){
//			int length = CommonUtil.getRandom(2, 2);
//			String token;
//			if(j+length >= str.length()){
//				token = str.substring(j, str.length());
//			}else{
//				token = str.substring(j, j+length);
//			}
//			Message msg = new Message();
//			msg.what = OutputHandler.WHAT_INPUT;
//			msg.obj = token;
//			mHandler.sendMessage(msg);
//			try {
//				Thread.sleep(15);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			j += length;
//		}
//		searchHelper.releaseInput();
//	}
//
//	private void displayText(String sth){
//		Message msg = new Message();
//		msg.what = OutputHandler.WHAT_INPUT;
//		msg.obj = sth;
//		mHandler.sendMessage(msg);
//	}

}
