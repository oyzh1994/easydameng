package cn.oyzh.easydameng.event.event;

import cn.oyzh.easydameng.dameng.event.DamengEvent;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public class DamengEventDesignEvent extends Event<DamengEvent> {

    private DamengSchemaTreeItem dbItem;

    public String eventName() {
        return this.data().getName();
    }

    public DamengSchemaTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengSchemaTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
