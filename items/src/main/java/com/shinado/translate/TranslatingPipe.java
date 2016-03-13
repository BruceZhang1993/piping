package com.shinado.translate;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import csu.org.dependency.volley.Listener;
import csu.org.dependency.volley.VolleyProvider;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class TranslatingPipe extends DefaultInputActionPipe {

    private static final String NAME = "$translate";

    private static final String KEY = "Gl7XVMw7FzV8dBPJZzxz";
    private static final String APP_ID = "20160304000014560";

    private static final String URL = "http://api.fanyi.baidu.com/api/trans/vip/translate?appid=" +
            APP_ID +
            "&q=%s&from=auto&to=auto&salt=%s&sign=%s";

    private static final String HELP = "Usage of " + NAME + ":\n" +
            "[key]"+ Keys.PIPE +"trans";

    public TranslatingPipe(int id) {
        super(id);
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        requestTranslation(input, callback);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"tran", "s,", "la", "te"});
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        callback.onOutput("You got to translate something, dude");
        callback.onOutput(HELP);
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        callback.onOutput("You got to translate something, dude");
        callback.onOutput(HELP);
    }

    private void requestTranslation(final String q, final OutputCallback callback) {
        getConsole().blockInput();
        int salt = new Random().nextInt();
        String key = APP_ID + q + salt + KEY;
        String sign = getMD5(key);

        new VolleyProvider().handleData(String.format(URL, q, ""+salt, sign), null, Result.class,
                new Listener.Response<Result>() {

                    @Override
                    public void onResponse(Result obj) {
                        if (obj != null) {
                            StringBuilder result = new StringBuilder();
                            for (Result.TransResult r : obj.trans_result) {
                                try {
                                    result.append(new String(r.dst.getBytes(), "UTF-8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            getConsole().releaseInput();
                            callback.onOutput(result.toString());
                        } else {
                            getConsole().releaseInput();
                            callback.onOutput("error");
                        }
                    }
                },
                new Listener.Error() {
                    @Override
                    public void onError(String msg) {
                        getConsole().releaseInput();
                        callback.onOutput(msg);
                    }
                });
    }

    private static String getMD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuilder buf = new StringBuilder();
            for (byte aB : b) {
                i = aB;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

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
