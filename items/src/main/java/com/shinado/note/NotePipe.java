package com.shinado.note;

import java.util.ArrayList;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class NotePipe extends DefaultInputActionPipe {

    private static final String NAME = "$note";

    private static final String OPT_RM = "rm";
    private static final String OPT_LS = "ls";
    private static final String OPT_CLEAR = "c";

    private static final String HELP = "Usage of " + NAME + "\n" +
            "[note]"+ Keys.PIPE +"note to add a new note\n" +
            "[index]"+ Keys.PIPE +"note " + Keys.PARAMS + OPT_RM + " to remove note by index\n" +
            "note " + Keys.PARAMS + OPT_LS + " to list all notes\n" +
            "note " + Keys.PARAMS + OPT_CLEAR + " to clear all notes";

    private ArrayList<String> notes = new ArrayList<>();

    public NotePipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"no", "te"});
    }

    @Override
    public void onEmpty(Pipe rs, IInput input) {
        input.input(HELP);
    }

    @Override
    public void onParamsEmpty(Pipe rs, IInput input) {
        addNewNote(rs.getInstruction(), input);
    }

    private void addNewNote(Instruction ins, IInput input) {
        notes.add(ins.pre);
    }

    @Override
    public void onPreEmpty(Pipe rs, IInput input) {
        String[] params = rs.getInstruction().params;
        if (params.length > 1) {
            input.input("Warning:" + NAME + " takes only one param, ignoring the rests.");
        }

        switch (params[0]) {
            case OPT_RM:
                input.input("-rm must take index of items");
                break;
            case OPT_CLEAR:
                notes.clear();
                break;
            case OPT_LS:
                StringBuilder sb = new StringBuilder();
                int i = 0;
                for (String note : notes) {
                    sb.append(i++);
                    sb.append(".");
                    sb.append(note);
                    sb.append("\n");
                }
                input.input(sb.toString());
                break;
        }
    }

    @Override
    public void onNoEmpty(Pipe rs, IInput input) {
        String[] params = rs.getInstruction().params;
        if (params.length > 1) {
            input.input("Warning:" + NAME + " takes only one param, ignoring the rests.");
        }

        if (params[0].equals(OPT_RM)){
            try {
                int index = Integer.parseInt(params[0]);
                if (index >= notes.size() || index < 0) {
                    input.input("Index out of range, size " + notes.size());
                } else {
                    notes.remove(index);
                }
            } catch (NumberFormatException e) {
                input.input("Arguments take only number");
            }
        }else {
            input.input(HELP);
        }
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous) {
        Pipe prev = previous.get();
        getConsole().input("Warning: this note is taken with the input from " + prev.getDisplayName() + ". Is that what you really want?");
        notes.add(input);
    }
}
