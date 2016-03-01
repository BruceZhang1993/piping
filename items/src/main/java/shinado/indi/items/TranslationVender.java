package shinado.indi.items;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import csu.org.dependency.volley.Listener;
import csu.org.dependency.volley.VolleyProvider;
import indi.shinado.piping.pipes.action.ActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.entity.Instruction;

public class TranslationVender extends ActionPipe {

    public TranslationVender(int id) {
        super(id);
        mResult = new Pipe();
        mResult.setId(id);
        mResult.setDisplayName("$trans");
        mResult.setSearchableName(new SearchableName(new String[]{"tran", "s", "la", "tion"}));
    }

    @Override
    public void acceptInput(Pipe result, String input) {

    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {

    }

    @Override
    protected void execute(Pipe rs) {
        Instruction value = rs.getInstruction();

        if (value.isEmpty()) {
            //.trans
            getConsole().input(HELP);
        } else {
            if (value.isParamsEmpty()) {
                //what.trans
                getConsole().blockInput();
                getConsole().input("loading...");
                requestTranslation(value.pre);
            } else {
                //.trans -w
                //what.trans -k
                getConsole().input(HELP);
            }

        }
    }

    private Pipe mResult;
    private static final String URL = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=yUZ32TKgbLjGeST3zvRSXxGN&" +
            "q=%s&from=auto&to=auto";

    private static final String HELP = "Usage of translation:\n" +
            "[key].trans";

    @Override
    protected Pipe getResult() {
        return mResult;
    }

    private void requestTranslation(final String q) {
        new VolleyProvider().handleData(String.format(URL, q), null, Result.class,
                new Listener.Response<Result>() {

                    @Override
                    public void onResponse(Result obj) {
                        getConsole().releaseInput();
                        if (obj != null) {
                            StringBuilder result = new StringBuilder("Translation for " + q + "\n");
                            for (Result.TransResult r : obj.trans_result) {
                                try {
                                    result.append(new String(r.dst.getBytes(), "UTF-8") + "\n");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            getConsole().input(result.toString());
                        } else {
                            getConsole().input("error");
                        }
                    }
                },
                mDefaultError);
    }

    private Listener.Error mDefaultError = new Listener.Error() {
        @Override
        public void onError(String msg) {
            getConsole().releaseInput();
            getConsole().input(msg);
        }
    };

    public class Result {
        public String from;
        public String to;
        public ArrayList<TransResult> trans_result;

        public class TransResult {
            public String src;
            public String dst;
        }
    }
}
