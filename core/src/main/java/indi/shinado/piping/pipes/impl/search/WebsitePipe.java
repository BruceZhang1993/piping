package indi.shinado.piping.pipes.impl.search;

import android.content.Intent;
import android.net.Uri;
import java.util.HashMap;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.FrequentPipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class WebsitePipe extends FrequentPipe{

    private HashMap<String, String> mWebsites = new HashMap<>();

    public WebsitePipe(int id) {
        super(id);
    }

    @Override
    public void destroy() {

    }

    @Override
    public Pipe getByValue(String value) {
        return null;
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {

    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        callback.onOutput(result.getExecutable());
    }

    @Override
    protected void execute(Pipe rs) {
        String url = rs.getExecutable();
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        getLauncher().startActivity(it);
    }

    @Override
    public void load(final AbsTranslator translator, final OnItemsLoadedListener listener, final int total) {
        new Thread() {
            public void run() {
                while (!translator.ready()) {
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mWebsites.put("Baidu", "http://www.baidu.com");
                mWebsites.put("Sina", "http://www.sina.com.cn");
                mWebsites.put("Zhihu", "http://www.zhihu.com");
                mWebsites.put("163", "http://www.163.com");

                for (String key : mWebsites.keySet()) {
                    Pipe pipe = new Pipe(WebsitePipe.this.getId(), key, translator.getName(key), mWebsites.get(key));
                    pipe.setBasePipe(WebsitePipe.this);
                    putItemInMap(pipe);
                }

                listener.onItemsLoaded(WebsitePipe.this.getId(), total);
            }
        }.start();
    }
}
