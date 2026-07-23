package cn.oyzh.easydameng.event.view;

import cn.oyzh.easydameng.dameng.record.DamengRecordFilter;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.easydameng.trees.view.DamengViewTreeItem;
import cn.oyzh.event.Event;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/06/26
 */
public class DamengViewFilteredEvent extends Event<DamengViewTreeItem> {

    private List<DamengRecordFilter> filters;

    private DamengDatabaseTreeItem dbItem;

    public String viewName() {
        return this.data().viewName();
    }

    public List<DamengRecordFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<DamengRecordFilter> filters) {
        this.filters = filters;
    }

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
