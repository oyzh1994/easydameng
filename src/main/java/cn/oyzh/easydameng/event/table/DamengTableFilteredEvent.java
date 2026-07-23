package cn.oyzh.easydameng.event.table;

import cn.oyzh.easydameng.dameng.record.DamengRecordFilter;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.easydameng.trees.table.DamengTableTreeItem;
import cn.oyzh.event.Event;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/06/26
 */
public class DamengTableFilteredEvent extends Event<DamengTableTreeItem> {

    private List<DamengRecordFilter> filters;

    private DamengDatabaseTreeItem dbItem;

    public String tableName() {
        return this.data().tableName();
    }

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }

    public List<DamengRecordFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<DamengRecordFilter> filters) {
        this.filters = filters;
    }
}
