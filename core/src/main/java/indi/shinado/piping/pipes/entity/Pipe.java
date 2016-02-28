package indi.shinado.piping.pipes.entity;

import java.util.TreeSet;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.action.BasePipeNotSetException;

public class Pipe {

    /**
     * three basic type
     */
    public static final int BUILD_IN_ID_APP = 1;
    public static final int BUILD_IN_ID_CONTACT = 2;
    public static final int BUILD_IN_ID_INSTALL = 3;
    public static final int BUILD_IN_ID_COPY = 4;
    public static final int BUILD_IN_ID_SEARCH = 5;
    public static final int BUILD_IN_ID_MIGHTY = 6;

    private String displayName;

    private int id;

    private SearchableName searchableName;

    private TreeSet<Pipe> previous;

    private Value value;

    private BasePipe basePipe;

    private int keyIndex;

    public Pipe(){

    }

    public Pipe(int id){
        this();
        this.id = id;
    }

    public Pipe(int id, String displayName){
        this(id);
        this.displayName = displayName;
    }

    public Pipe(int id, Value value){
        this(id);
        this.value = value;
    }

    public Pipe(int id, String displayName, SearchableName searchableName){
        this(id, displayName);
        this.searchableName = searchableName;
    }

    public Pipe(int id, String displayName, SearchableName searchableName, Value value){
        this(id, displayName);
        this.searchableName = searchableName;
        this.value = value;
    }
    
    public SearchableName getSearchableName() {
        return searchableName;
    }

    public void setSearchableName(SearchableName searchableName) {
        this.searchableName = searchableName;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TreeSet<Pipe> getPrevious() {
        return previous;
    }

    public void setPrevious(TreeSet<Pipe> previous) {
        this.previous = previous;
    }

    public BasePipe getBasePipe() {
        if (basePipe == null){
            throw new BasePipeNotSetException("Must set BasePipe in getResult() in BasePipe");
        }
        return basePipe;
    }

    public void setBasePipe(BasePipe basePipe) {
        this.basePipe = basePipe;
    }

    public void input(){
        getBasePipe().getConsole().input(displayName);
    }

    public void setKeyIndex(int keyIndex) {
        this.keyIndex = keyIndex;
    }

    public int getKeyIndex() {
        return keyIndex;
    }
}
