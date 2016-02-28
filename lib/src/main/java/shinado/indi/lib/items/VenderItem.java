package shinado.indi.lib.items;

import java.util.TreeSet;

public class VenderItem implements Comparable<VenderItem>{

	public static String INDICATOR = "-";

	/**
	 * the name array defined by display name
	 * for searching
	 * e.g. an item with display name as WeChat
	 * it will be stored as {we, chat}
	 * for the convenience of searching
	 * for a display name in other languages other than English
	 * it stores it English spell
	 */
	private String[] name;

	/**
	 * the display name, as will be displayed
	 */
	private String displayName;

	/**
	 * the value to be executed
	 */
	private Value value;

	/**
	 * as {@link #BUILD_IN_ID_APP}, {@link #BUILD_IN_ID_CONTACT}, etc
	 *
	 */
	private int id;

	/**
	 * the frequency for each item
	 * which determines the sequence of the result to display
	 */
	private int freq;

	/**
	 * three basic type
	 */
	public static final int BUILD_IN_ID_APP = 1;
	public static final int BUILD_IN_ID_CONTACT = 2;
	public static final int BUILD_IN_ID_INSTALL = 3;
	public static final int BUILD_IN_ID_COPY = 4;
	public static final int BUILD_IN_ID_SEARCH = 5;
	public static final int BUILD_IN_ID_MIGHTY = 6;

	/**
	 * the key to be searched
	 * e.g. input "k"
	 * ["kakao", "talk"] -> 0
	 * ["we", "kite"] -> 1
	 * so that "kakao talk" will come first
	 */
	private int keyIndex;

	private int type;
	public static final int TYPE_APPLICATION = 0x0001;
	public static final int TYPE_CONTACT = 0x0010;
	public static final int TYPE_ACTION = 0x0100;

	private TreeSet<VenderItem> successors;

	public VenderItem(){
	}

	public VenderItem(String value){
		this.value = new Value(value);
	}

	public VenderItem(String value, int id){
		this(value);
		this.id = id;
	}

	public VenderItem(String value, String displayName, String[] name, int id){
		this(value, id);
		setDisplayName(displayName);
		setName(name);
	}

	public VenderItem(String value, String displayName, String[] name, int id, int type){
		this(value, displayName, name, id);
		this.type = type;
	}

	public VenderItem(String[] name, String value,
					  int id, String displayName) {
		this.name = name;
		this.value = new Value(value);
		this.id = id;
		this.displayName = displayName;
	}

	public TreeSet<VenderItem> getSuccessors() {
		return successors;
	}
	public void setSuccessors(TreeSet<VenderItem> successors) {
		this.successors = successors;
	}
	public String[] getName() {
		return name;
	}
	public void setName(String[] name) {
		this.name = name;
	}
	public Value getValue() {
		return value;
	}
	public void setValue(Value value) {
		this.value = value;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public void setKeyIndex(int keyIndex){
		this.keyIndex = keyIndex;
	}
	public void setType(int type){
		if (type != TYPE_ACTION && type != TYPE_APPLICATION && type != TYPE_CONTACT){
			return;
		}
		this.type = type;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	@Override
	public int compareTo(VenderItem another) {
		//search results always ahead of action
		int compare = another.type - type;
		//same type
		if (compare == 0){
			compare = keyIndex - another.keyIndex;

			//same key index
			if (compare == 0){
				compare = another.getFreq() - freq;

				//same frequency
				if (compare == 0){
					compare = another.getDisplayName().compareTo(displayName);
				}
			}
		}

		return compare;
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof VenderItem){
			if(((VenderItem)obj).getDisplayName().equals(displayName)){
				return true;
			}
		}
		return false;
	}

	public void addFrequency(){
		++freq;
	}

	public void copy(VenderItem vo){
		this.name = vo.name;
		this.value = vo.value;
		this.displayName = vo.displayName;
		this.freq = vo.freq;
		this.type = vo.type;
	}

	/**
	 * the value of key
	 * e.g.
	 * "tran.ins -ls"  -> ["tran", "ins", ["ls"]]
	 * "maya.txt.play" -> ["maya.txt", "play", null]
	 */
	public static class Value {

		public String pre;

		public String body;

		public String[] params;

		public Value(){

		}

		public Value(String body){
			this.body = body;
		}

		public boolean isEmpty(){
			return isPreEmpty() && isParamsEmpty();
		}

		public boolean isPreEmpty(){
			return pre == null || pre.isEmpty();
		}

		public boolean isParamsEmpty(){
			return params == null || params.length == 0;
		}

		public boolean isBodyEmpty(){
			return body == null || body.isEmpty();
		}
	}
}
