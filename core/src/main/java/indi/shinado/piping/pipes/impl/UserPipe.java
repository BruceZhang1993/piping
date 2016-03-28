package indi.shinado.piping.pipes.impl;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.action.UserRequireAction;

public class UserPipe extends UserRequireAction{

    private final String NAME = "$user";

    public UserPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"user"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        test();
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {

    }

    void test(){
        verifyUser(new OnUserCallback() {
            @Override
            public void onUser(String userName) {
                getConsole().input("userName: " + userName);
            }
        });
    }
}
