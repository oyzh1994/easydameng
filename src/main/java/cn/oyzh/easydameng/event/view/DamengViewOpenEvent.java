package cn.oyzh.easydameng.event.view;

import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.easydameng.trees.view.DamengViewTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/12/22
 */
public class DamengViewOpenEvent extends Event<DamengViewTreeItem> {

    private DamengDatabaseTreeItem dbItem;

    public String viewName() {
        return this.data().viewName();
    }

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
