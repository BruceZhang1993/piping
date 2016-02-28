package shinado.indi.lib.items.action;

import java.util.TreeSet;

import shinado.indi.lib.items.BaseVender;
import shinado.indi.lib.items.CompareUtil;
import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.VenderItem.Value;

public abstract class ActionVender extends BaseVender {

    public ActionVender(int id) {
        super(id);
    }

    @Override
    public TreeSet<VenderItem> search(TreeSet<VenderItem> prev, String key, int length) {
        VenderItem result = doSearch(prev, key);

        result = filter(result);

        TreeSet<VenderItem> list = new TreeSet<>();
        if (result != null) {
            list.add(result);
        }
        return list;
    }

    public VenderItem doSearch(TreeSet<VenderItem> prev, String key) {
        VenderItem result = getResult();
        Value value= getValue(key, result.getName());

        if (value == null) {
            result = null;
        } else {
            result.setSuccessors(prev);
            result.setType(VenderItem.TYPE_ACTION);
            result.setValue(value);
        }
        return result;
    }

    public Value getValue(String key, String[] targetName) {
        Value value = getValue(key);

        if (CompareUtil.contains(targetName, value.body)) {
            return value;
        } else {
            return null;
        }
    }

    protected abstract VenderItem getResult();

    /**
     * @param result
     * @return
     */
    protected abstract VenderItem filter(VenderItem result);

    @Override
    public VenderItem getItem(String value) {
        return getResult();
    }

}
