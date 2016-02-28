package shinado.indi.lib.items.action;

import java.util.TreeSet;

import shinado.indi.lib.items.VenderItem;

public abstract class PreActionVender extends ActionVender{
    public PreActionVender(int id) {
        super(id);
    }

    @Override
    protected VenderItem filter(VenderItem result) {
        if (result == null){
            return null;
        }
        TreeSet<VenderItem> successors = result.getSuccessors();
        if (successors == null || successors.size() == 0){
            return null;
        }
        return result;
    }

}
