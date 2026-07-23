package cn.oyzh.easydameng.event.event;

import cn.oyzh.easydameng.dameng.event.DamengEvent;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public class DamengEventDesignEvent extends Event<DamengEvent> {

    private DamengDatabaseTreeItem dbItem;

    public String eventName() {
        return this.data().getName();
    }

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
