package indi.shinado.piping.pipes.impl.action;

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
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        callback.onOutput(HELP);
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        String[] params = rs.getInstruction().params;
        if (params.length > 1) {
            callback.onOutput("Warning:" + NAME + " takes only one param, ignoring the rests.");
        }

        switch (params[0]) {
            case OPT_RM:
                callback.onOutput("-rm must take index of items");
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
                callback.onOutput(sb.toString());
                break;
            default:
                callback.onOutput(HELP);
                break;
        }
    }

    private void addNewNote(String note) {
        notes.add(note);
    }

    @Override
    public void acceptInput(Pipe rs, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        if (previous != null){
            Pipe prev = previous.get();
            callback.onOutput("Get input from " + prev.getDisplayName() + ".");
        }

        Instruction value = rs.getInstruction();
        if (!value.isParamsEmpty()) {
            String[] params = rs.getInstruction().params;
            if (params.length > 1) {
                callback.onOutput("Warning:" + NAME + " takes only one param, ignoring the rests.");
            }

            if (params[0].equals(OPT_RM)) {
                try {
                    int index = Integer.parseInt(params[0]);
                    if (index >= notes.size() || index < 0) {
                        callback.onOutput("Index out of range, size " + notes.size());
                    } else {
                        notes.remove(index);
                    }
                } catch (NumberFormatException e) {
                    callback.onOutput("Arguments take only number");
                }
            } else {
                callback.onOutput(HELP);
            }
        }else {
            addNewNote(input);
        }
    }
}
