package cn.oyzh.easydameng.event.query;

import cn.oyzh.easydameng.domain.DamengQuery;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/12/22
 */
public class DamengQueryOpenEvent extends Event<DamengQuery> {

    private DamengDatabaseTreeItem dbItem;

    public String queryId() {
        return this.data().getUid();
    }

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
