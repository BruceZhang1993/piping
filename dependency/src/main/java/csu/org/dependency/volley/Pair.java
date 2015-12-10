package csu.org.dependency.volley;

import java.util.ArrayList;

public class Pair {

	private ArrayList<String> keyList;
	private ArrayList<String> valueList;
	
	public Pair(String key, String value){
		keyList = new ArrayList<String>();
		valueList = new ArrayList<String>();
		keyList.add(key);
		valueList.add(value);
	}
	public Pair(){
		keyList = new ArrayList<String>();
		valueList = new ArrayList<String>();
	}
	
	public void add(String key, String value){
		keyList.add(key);
		valueList.add(value);
	}
	public void remove(int index){
		keyList.remove(index);
		valueList.remove(index);
	}
	public int size(){
		return keyList.size();
	}
	public String parseURL(){
		String ret = "";
		for(int i=0; i<size(); i++){
			ret += keyList.get(i)+"="+valueList.get(i);
			if(i != size()-1){
				ret += "&";
			}
		}
		return ret;
	}
}
