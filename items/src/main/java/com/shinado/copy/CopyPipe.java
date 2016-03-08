package com.shinado.copy;

import android.content.Context;

import indi.shinado.piping.pipes.action.ActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class CopyPipe extends ActionPipe {

    private Pipe mResult;

    public CopyPipe(int id) {
        super(id);
        mResult = new Pipe();
        mResult.setId(id);
        mResult.setDisplayName("$clipboard");
        mResult.setSearchableName(new SearchableName(new String[]{"clip", "board"}));
        mResult.setBasePipe(this);
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        copyToClipboard(context, input);
        callback.onOutput(input);
    }

    private void copyToClipboard(Context context, String text) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            @SuppressWarnings("deprecation")
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("text label", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        @SuppressWarnings("deprecation")
        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        callback.onOutput(clipboard.getText().toString());
    }

    @Override
    protected Pipe getResult() {
        return mResult;
    }

    @Override
    public void execute(Pipe result) {
        getConsole().input("$clipboard must take input or as output");
    }

}
