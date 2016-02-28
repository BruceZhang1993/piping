package shinado.indi.vender_lib;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.action.ActionVender;
import shinado.indi.lib.items.action.PreActionVender;

/**
 * Created by shinado on 2016/2/23.
 */
public class TestAction extends PreActionVender{

    public TestAction(int id) {
        super(id);
    }

    @Override
    protected VenderItem getResult() {
        return null;
    }

    @Override
    public int execute(VenderItem result) {
        return 0;
    }
}
