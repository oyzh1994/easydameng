package cn.oyzh.easydameng.event.event;

import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public class DamengEventAlertedEvent extends Event<String> {

    private DamengDatabaseTreeItem dbItem;

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
