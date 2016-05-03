package indi.shinado.piping.pipes.impl.action;

import android.support.annotation.NonNull;

import indi.shinado.piping.account.User;
import indi.shinado.piping.account.UserHelper;
import indi.shinado.piping.launcher.UserInputCallback;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;

public abstract class UserRequireAction extends DefaultInputActionPipe{

    public UserRequireAction(int id) {
        super(id);
    }
    
    protected void verifyUser(@NonNull final OnUserCallback callback){
        final UserHelper userHelper = new UserHelper(context);
        String userName = userHelper.getUserName();
        if (userName == null){
            final User user = new User();

            getConsole().display("You haven't logged in. Your name please?");
            getConsole().waitForUserInput(new UserInputCallback() {
                @Override
                public void onUserInput(String userInput) {
                    user.name = userInput;
                    getConsole().display("\nYour password please?");
                    getConsole().waitForUserInput(new UserInputCallback() {
                        @Override
                        public void onUserInput(String userInput) {
                            user.pwd = userInput;
                            getConsole().blockInput();
                            userHelper.signIn(user, new UserHelper.OnResultListener() {
                                @Override
                                public void onResult(UserHelper.Result rs) {
                                    getConsole().input(rs.msg);
                                    if (rs.resultCode > 0){
                                        callback.onUser(user.name);
                                    }
                                    getConsole().releaseInput();
                                }
                            });
                        }
                    });
                }
            });
        }else{
            callback.onUser(userName);
        }
    }

    protected interface OnUserCallback{
        void onUser(String userName);
    }
}
