package indi.shinado.piping.pipes.impl.action;

import android.app.WallpaperManager;
import android.content.Intent;
import com.google.gson.Gson;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.ShareIntent;

public class WallpaperPipe extends DefaultInputActionPipe{

    public WallpaperPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return "Wallpaper";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName("wall", "paper");
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        if (input.startsWith("http://") || input.startsWith("https://")){

        }else {
            ShareIntent shareIntent = new Gson().fromJson(input, ShareIntent.class);
            if (shareIntent != null){
                if(shareIntent.type.contains("image")){
                    String stream = shareIntent.extras.get(Intent.EXTRA_STREAM);

                }
            }
        }
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getLauncher());
    }
}
