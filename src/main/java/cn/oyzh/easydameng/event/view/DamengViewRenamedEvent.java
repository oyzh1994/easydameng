package cn.oyzh.easydameng.event.view;

import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.easydameng.trees.view.DamengViewTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/01/23
 */
public class DamengViewRenamedEvent extends Event<DamengViewTreeItem> {

    private DamengDatabaseTreeItem dbItem;

    public String viewName() {
        return this.data().viewName();
    }

    public String dbName() {
        return this.dbItem.dbName();
    }

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
