package indi.shinado.piping.pipes.entity;

import java.util.TreeSet;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.action.BasePipeNotSetException;

public class Pipe implements Comparable<Pipe> {

    public static final int TYPE_SEARCHABLE = 100;

    public static final int TYPE_ACTION = 10;

    /**
     * three basic type
     */
    public static final int BUILD_IN_ID_APP = 1;
    public static final int BUILD_IN_ID_CONTACT = 2;
    public static final int BUILD_IN_ID_INSTALL = 3;
    public static final int BUILD_IN_ID_COPY = 4;
    public static final int BUILD_IN_ID_SEARCH = 5;
    public static final int BUILD_IN_ID_MIGHTY = 6;
    public static final String INDICATOR = "-";

    private String displayName;

    private int id;

    private SearchableName searchableName;

    private PreviousPipes previous;

    private Instruction instruction;

    private String executable;

    private BasePipe basePipe;

    private int keyIndex;

    private int frequency;

    private int typeIndex = TYPE_ACTION;

    public Pipe() {

    }

    public Pipe(int id) {
        this();
        this.id = id;
    }

    public Pipe(int id, String displayName) {
        this(id);
        this.displayName = displayName;
    }

    public Pipe(int id, Instruction instruction) {
        this(id);
        this.instruction = instruction;
    }

    public Pipe(int id, String displayName, SearchableName searchableName) {
        this(id, displayName);
        this.searchableName = searchableName;
    }

    public Pipe(int id, String displayName, SearchableName searchableName, String executable) {
        this(id, displayName);
        this.searchableName = searchableName;
        this.executable = executable;
    }

    public SearchableName getSearchableName() {
        return searchableName;
    }

    public void setSearchableName(SearchableName searchableName) {
        this.searchableName = searchableName;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction value) {
        this.instruction = value;
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

    public PreviousPipes getPrevious() {
        return previous;
    }

    public void setPrevious(PreviousPipes previous) {
        if (previous == null){
            this.previous = new PreviousPipes();
        }else {
            this.previous = previous;
        }
    }

    public void execute() {
        getBasePipe().startExecution(this);
    }

    public BasePipe getBasePipe() {
        if (basePipe == null) {
            throw new BasePipeNotSetException("Must set BasePipe in getResult() in BasePipe");
        }
        return basePipe;
    }

    public void setBasePipe(BasePipe basePipe) {
        this.basePipe = basePipe;
    }

    public void input() {
        getBasePipe().getConsole().input(displayName);
    }

    public void setKeyIndex(int keyIndex) {
        this.keyIndex = keyIndex;
    }

    public int getKeyIndex() {
        return keyIndex;
    }

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public void setTypeIndex(int typeIndex) {
        this.typeIndex = typeIndex;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public int compareTo(Pipe another) {
        //search results always ahead of action
        int compare = another.typeIndex - typeIndex;
        //same type
        if (compare == 0) {
            compare = keyIndex - another.keyIndex;

            //same key index
            if (compare == 0) {
                compare = another.frequency - frequency;

                //same frequency
                if (compare == 0) {
                    compare = another.getDisplayName().compareTo(displayName);
                }
            }
        }
        return compare;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pipe){
            return displayName.equals(((Pipe)o).displayName);
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return displayName.hashCode();
    }

    public static class PreviousPipes{
        private TreeSet<Pipe> previous;
        private int pointer;

        public PreviousPipes(){

        }

        public PreviousPipes(TreeSet<Pipe> previous, int pointer){
            this.previous = new TreeSet<>();
            this.previous.addAll(previous);
            this.pointer = pointer;
        }

        public TreeSet<Pipe> getPrevious() {
            return previous;
        }

        public void setPrevious(TreeSet<Pipe> previous) {
            this.previous = previous;
        }

        public int getPointer() {
            return pointer;
        }

        public void setPointer(int pointer) {
            this.pointer = pointer;
        }

        public void clear(){
            if (previous != null){
                previous.clear();
            }
            pointer = 0;
        }

        public Pipe get(){
            return previous == null || previous.size() == 0 ? null :
                    (Pipe) previous.toArray()[pointer];
        }

        public TreeSet<Pipe> getAll(){
            return previous;
        }

        public boolean isEmpty() {
            return previous == null || previous.isEmpty();
        }
    }
}
