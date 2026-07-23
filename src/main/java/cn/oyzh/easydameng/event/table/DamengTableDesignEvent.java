package cn.oyzh.easydameng.event.table;

import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/08/07
 */
public class DamengTableDesignEvent extends Event<DamengTable> {

    private DamengDatabaseTreeItem dbItem;

    public String dbName() {
        return this.dbItem.dbName();
    }

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }

    public String tableName() {
        return this.data().getName();
    }
}
