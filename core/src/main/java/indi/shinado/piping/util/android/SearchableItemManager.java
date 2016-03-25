package indi.shinado.piping.util.android;

import android.content.Context;

import java.lang.ref.WeakReference;

import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import indi.shinado.piping.pipes.search.translator.TranslatorFactory;

public abstract class SearchableItemManager {

    protected Context context;
    protected WeakReference<AbsTranslator> mTranslatorRef;

    SearchableItemManager(Context context, AbsTranslator translator) {
        mTranslatorRef = new WeakReference<>(translator);
        this.context = context;
    }

    /**
     *
     * @return
     */
    protected AbsTranslator getTranslator(){
        AbsTranslator translator = mTranslatorRef.get();
        if (translator == null){
            translator = TranslatorFactory.getTranslator(context);
            mTranslatorRef = new WeakReference<>(translator);
        }
        return translator;
    }

    abstract void register();

    abstract void unregister();

    public void start(){
        register();
    }

    public void destroy(){
        unregister();
        mTranslatorRef.clear();
    }
}
