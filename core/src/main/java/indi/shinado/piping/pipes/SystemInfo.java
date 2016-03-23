package indi.shinado.piping.pipes;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class SystemInfo {

    private Context context;

    public SystemInfo(Context context){
        this.context = context;
    }

    private PackageInfo getPackageInfo(){
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    public int getVersionCode(){
        PackageInfo pInfo = getPackageInfo();
        return pInfo == null ? 0 : pInfo.versionCode;
    }

    public String getVersionName(){
        PackageInfo pInfo = getPackageInfo();
        return pInfo == null ? "" : pInfo.versionName;
    }


}
