package cn.oyzh.easydameng.event.view;

import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/06/29
 */
public class DamengViewAlertedEvent extends Event<String> {

    private DamengDatabaseTreeItem dbItem;

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
