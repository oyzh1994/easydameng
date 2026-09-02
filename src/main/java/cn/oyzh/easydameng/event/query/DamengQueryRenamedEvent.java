package cn.oyzh.easydameng.event.query;

import cn.oyzh.easydameng.domain.DamengQuery;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/01/23
 */
public class DamengQueryRenamedEvent extends Event<DamengQuery> {

    private DamengDatabaseTreeItem dbItem;

    public String queryName() {
        return this.data().getName();
    }

    public String queryId() {
        return this.data().getUid();
    }

    public String schema() {
        return this.dbItem.schema();
    }

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
