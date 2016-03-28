package indi.shinado.piping.account;

import android.content.Context;

import com.shinado.annotation.TargetVersion;

import java.util.HashMap;

import csu.org.dependency.volley.Listener;
import csu.org.dependency.volley.VolleyProvider;
import indi.shinado.piping.settings.Preferences;

@TargetVersion(4)
public class UserHelper {

    private Context context;
    private Preferences preferences;

    public UserHelper(Context context){
        this.context = context;
        preferences = new Preferences(context);
    }

    public String getUserName(){
        return preferences.getUserName();
    }

    public void signIn(final User user, final OnResultListener listener){
        HashMap<String, String> params = new HashMap<>();
        params.put("name", user.name);
        params.put("pwd", user.pwd);
        new VolleyProvider().handleData("http://1.yilaunch.sinaapp.com/user/sign_in_up.php", params, Result.class,
                new Listener.Response<Result>() {
                    @Override
                    public void onResponse(Result obj) {
                        if (obj.resultCode > 0){
                            preferences.setUserName(user.name);
                        }
                        listener.onResult(obj);
                    }
                },
                new Listener.Error() {
                    @Override
                    public void onError(String msg) {
                        listener.onResult(new Result(-1, msg));
                    }
                });
    }

    public class Result{
        public int resultCode = -1;
        public String msg;

        public Result(int resultCode, String msg) {
            this.resultCode = resultCode;
            this.msg = msg;
        }
    }

    public interface OnResultListener{
        void onResult(Result rs);
    }
}
