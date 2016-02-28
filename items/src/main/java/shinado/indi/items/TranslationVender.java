package shinado.indi.items;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import csu.org.dependency.volley.Listener;
import csu.org.dependency.volley.VolleyProvider;
import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.action.ActionVender;

public class TranslationVender extends ActionVender {

    public TranslationVender(int id) {
        super(id);
        mResult = new VenderItem();
        mResult.setId(id);
        mResult.setDisplayName(".trans");
        mResult.setName(new String[]{".", "trans"});
    }

    private VenderItem mResult;
    private static final String URL = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=yUZ32TKgbLjGeST3zvRSXxGN&" +
            "q=%s&from=auto&to=auto";

    private static final String HELP = "Usage of translation:\n" +
            "[key].trans";

    @Override
    protected VenderItem getResult() {
        return mResult;
    }

    @Override
    protected VenderItem filter(VenderItem result) {
        return result;
    }

    @Override
    public int execute(VenderItem result) {
        VenderItem.Value value = result.getValue();

        if (value.isEmpty()) {
            //.trans
            input(HELP);
        } else {
            if (value.isParamsEmpty()) {
                //what.trans
                mSearchHelper.blockInput();
                input("loading...");
                requestTranslation(value.pre);
            } else {
                //.trans -w
                //what.trans -k
                input(HELP);
            }
        }
        return 0;
    }

    private void handleWithOption(String value) {


    }

    private void requestTranslation(final String q) {
        new VolleyProvider().handleData(String.format(URL, q), null, Result.class,
                new Listener.Response<Result>() {

                    @Override
                    public void onResponse(Result obj) {
                        mSearchHelper.releaseInput();
                        if (obj != null) {
                            StringBuilder result = new StringBuilder("Translation for " + q + "\n");
                            for (Result.TransResult r : obj.trans_result) {
                                try {
                                    result.append(new String(r.dst.getBytes(), "UTF-8") + "\n");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            input(result.toString());
                        } else {
                            input("error");
                        }
                    }
                },
                mDefaultError);
    }

    private Listener.Error mDefaultError = new Listener.Error() {
        @Override
        public void onError(String msg) {
            mSearchHelper.releaseInput();
            input(msg);
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
