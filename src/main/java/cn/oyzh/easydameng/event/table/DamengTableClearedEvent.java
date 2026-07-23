package cn.oyzh.easydameng.event.table;

import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.easydameng.trees.table.DamengTableTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/01/23
 */
public class DamengTableClearedEvent extends Event<DamengTableTreeItem> {

    private DamengDatabaseTreeItem dbItem;

    public String tableName() {
        return this.data().tableName();
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
