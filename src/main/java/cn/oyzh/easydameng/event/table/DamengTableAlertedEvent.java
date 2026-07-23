package cn.oyzh.easydameng.event.table;

import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/01/17
 */
public class DamengTableAlertedEvent extends Event<String> {

    private DamengDatabaseTreeItem dbItem;

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
