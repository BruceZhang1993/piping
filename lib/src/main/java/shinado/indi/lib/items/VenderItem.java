package shinado.indi.lib.items;

import android.content.Context;

import shinado.indi.lib.items.search.translator.AbsTranslator;
import shinado.indi.lib.items.search.translator.TranslatorFactory;

public class VenderItem implements Cloneable, Comparable<VenderItem>{

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
	private String value;

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

	public VenderItem(){
		
	}

	public VenderItem(String value){
		this.value = value;
	}

	public VenderItem(String value, int id){
		this.value = value;
		this.id = id;
	}

	public VenderItem(Context context, String value, String displayName, int id){
		AbsTranslator translator = TranslatorFactory.getTranslator(context);

		this.value = value;
		this.id = id;
		setDisplayName(displayName);
		String[] name = translator.getName(displayName);
		setName(name);
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
	
	@Override
	public VenderItem clone(){
		VenderItem vo = new VenderItem(name, value, id, displayName);
		vo.setFreq(freq);
		return vo;
	}
	
	public VenderItem(String[] name, String value,
					  int id, String displayName) {
		this.name = name;
		this.value = value;
		this.id = id;
		this.displayName = displayName;
	}

	public String[] getName() {
		return name;
	}
	public void setName(String[] name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
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

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	@Override
	public int compareTo(VenderItem another) {
		int compare = another.getDisplayName().compareTo(displayName);
		//if they are different
		if(compare != 0){
			int c = another.getFreq() - freq;
			/*
			 * when two items have different frequency
			 */
			if(c != 0){
				compare = c;
			}
		}
		return compare;
	}
	
	public void addFrequency(){
		++freq;
	}
	
	public void copy(VenderItem vo){
		this.name = vo.name;
		this.value = vo.value;
		this.displayName = vo.displayName;
		this.freq = vo.freq;
	}

}
