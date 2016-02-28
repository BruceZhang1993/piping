package shinado.indi.lib.items.action;

import java.util.ArrayList;

import shinado.indi.lib.items.VenderItem;

public class NoteVender extends ActionVender {

    private VenderItem mResult;
    private static final String OPT_LS = "ls";
    private static final String OPT_D = "d";
    private static final String OPT_C = "c";
    private ArrayList<String> notes = new ArrayList<>();

    private static final String HELP = "Usage of note:\n" +
            "[note].note [option], where option includes\n" +
            VenderItem.INDICATOR +  OPT_LS + " to list all notes\n" +
            VenderItem.INDICATOR +  OPT_D + " to delete a note\n" +
            VenderItem.INDICATOR +  OPT_C + " to clear all notes";

    public NoteVender(int id) {
        super(id);
        mResult = new VenderItem();
        mResult.setId(id);
        mResult.setDisplayName("$note");
        mResult.setName(new String[]{"no", "te"});
    }

    @Override
    public void acceptInput(VenderItem result, String input) {
         notes.add(input);
    }

    @Override
    public void getOutput(VenderItem result, OutputCallback callback) {

    }

    @Override
    protected VenderItem getResult() {
        return mResult;
    }

    @Override
    protected VenderItem filter(VenderItem result) {
        return result;
    }

    @Override
    public void execute(VenderItem result) {
        VenderItem.Value value = result.getValue();

        if (value.isEmpty()) {
            //.note
            input(HELP);
        } else {
            if (value.isParamsEmpty()) {
                //callplay.note
                notes.add(value.pre);
            } else {
                if (value.isPreEmpty()) {
                    String[] params = value.params;

                    if (params.length > 1) {
                        input(".note only takes one param, ignoring the rest");
                    }
                    //.note -c
                    switch (params[0]) {
                        case OPT_C:
                            notes.clear();
                            break;
                        case OPT_LS:
                            StringBuilder sb = new StringBuilder();
                            int i = 0;
                            for (String note : notes) {
                                sb.append(i++ + "." + note);
                                sb.append("\n");
                            }
                            input(sb.toString());
                            break;
                        default:
                            input(HELP);
                    }
                } else {
                    //1.note -d
                    String[] params = value.params;
                    if (params.length > 1) {
                        input(".note only takes one param, ignoring the rest");
                    }
                    if (params[0].equals(OPT_D)) {
                        try {
                            int index = Integer.parseInt(params[0]);
                            if (index >= notes.size() || index < 0) {
                                input("Index out of range, size " + notes.size());
                            } else {
                                notes.remove(index);
                            }
                        } catch (NumberFormatException e) {
                            input("Arguments take only number");
                        }
                    } else {
                        input(HELP);
                    }
                }

            }
        }
    }
}