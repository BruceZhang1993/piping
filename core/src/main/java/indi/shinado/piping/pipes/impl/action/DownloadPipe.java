package indi.shinado.piping.pipes.impl.action;

import android.content.Intent;

import indi.shinado.piping.GlobalDefs;
import indi.shinado.piping.download.DownloadImpl;
import indi.shinado.piping.download.Downloadable;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.ShareIntent;
import indi.shinado.piping.util.IntentUtil;

public class DownloadPipe extends DefaultInputActionPipe {

    private DownloadImpl mLoader;

    public DownloadPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return "download";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName("download");
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void acceptInput(Pipe result, final String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        if (input.startsWith("http://") || input.startsWith("https://")) {
            setupDownload(callback);
            mLoader.addToQueue(new Downloadable() {
                @Override
                public String getUrl() {
                    return input;
                }

                @Override
                public String getFileName() {
                    int index = input.lastIndexOf("/");
                    return input.substring(index, input.length());
                }
            });
            mLoader.start();
        } else {
        }
    }

    private void setupDownload(final OutputCallback callback) {
        mLoader = new DownloadImpl();
        mLoader.setOnDownloadListener(new DownloadImpl.OnDownloadListener() {
            @Override
            public void onStart(Downloadable v) {
                getConsole().blockInput();
                getConsole().input("Start task:" + v.getFileName());
            }

            @Override
            public void onFinish(Downloadable v) {
                getConsole().replaceCurrentLine("Download complete.");
                getConsole().input("\n");
                getConsole().releaseInput();

                ShareIntent shareIntent = new ShareIntent(IntentUtil.getMIMEType(v.getUrl()));
                shareIntent.extras.put(Intent.EXTRA_STREAM, GlobalDefs.PATH_HOME + v.getFileName());
                callback.onOutput(shareIntent.toString());
            }

            @Override
            public void onFail(Downloadable v) {
                getConsole().input("Failed");
                getConsole().releaseInput();
            }

            @Override
            public void onProgress(Downloadable v, int prgs, int current) {
                StringBuilder sb = new StringBuilder(" ");
                int progressIn10 = prgs / 10;
                for (int i = 0; i < progressIn10; i++) {
                    sb.append("▅");
                }
                for (int i = progressIn10; i < 10; i++) {
                    sb.append("　");
                }
                sb.append("　");
                sb.append(prgs);
                sb.append("%");
                getConsole().replaceCurrentLine(sb.toString());
            }
        });
    }

}
