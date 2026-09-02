package cn.oyzh.easydameng.event.view;

import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.easydameng.trees.view.DamengViewTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/01/23
 */
public class DamengViewRenamedEvent extends Event<DamengViewTreeItem> {

    private DamengSchemaTreeItem dbItem;

    public String viewName() {
        return this.data().viewName();
    }

    public String schema() {
        return this.dbItem.schema();
    }

    public DamengSchemaTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengSchemaTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
