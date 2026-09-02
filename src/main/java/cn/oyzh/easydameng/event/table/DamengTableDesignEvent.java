package cn.oyzh.easydameng.event.table;

import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/08/07
 */
public class DamengTableDesignEvent extends Event<DamengTable> {

    private DamengSchemaTreeItem dbItem;

    public String schema() {
        return this.dbItem.schema();
    }

    public DamengSchemaTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengSchemaTreeItem dbItem) {
        this.dbItem = dbItem;
    }

    public String tableName() {
        return this.data().getName();
    }
}
