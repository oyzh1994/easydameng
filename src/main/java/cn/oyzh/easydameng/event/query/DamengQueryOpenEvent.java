package cn.oyzh.easydameng.event.query;

import cn.oyzh.easydameng.domain.DamengQuery;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/12/22
 */
public class DamengQueryOpenEvent extends Event<DamengQuery> {

    private DamengSchemaTreeItem dbItem;

    public String queryId() {
        return this.data().getUid();
    }

    public DamengSchemaTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengSchemaTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
