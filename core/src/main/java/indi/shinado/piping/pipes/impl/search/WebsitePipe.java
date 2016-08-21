package indi.shinado.piping.pipes.impl.search;

import java.util.HashMap;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

/**
 * Created by shinado on 16/5/15.
 */
public class WebsitePipe extends SearchablePipe{

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

    }

    @Override
    protected void execute(Pipe rs) {

    }

    @Override
    public void load(AbsTranslator translator, OnItemsLoadedListener listener, int total) {
        mWebsites.put("Baidu", "http://www.baidu.com");
        mWebsites.put("Sina", "http://www.sina.com.cn");
        mWebsites.put("Zhihu", "http://www.zhihu.com");
        mWebsites.put("163", "http://www.163.com");

        for (String key : mWebsites.keySet()){
            Pipe pipe = new Pipe(WebsitePipe.this.getId(), key, translator.getName(key), mWebsites.get(key));
            pipe.setBasePipe(WebsitePipe.this);
            putItemInMap(pipe);
        }
    }
}
