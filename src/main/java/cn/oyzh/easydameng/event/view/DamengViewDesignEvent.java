package cn.oyzh.easydameng.event.view;

import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/12/22
 */
public class DamengViewDesignEvent extends Event<DamengView> {

    private DamengDatabaseTreeItem dbItem;

    public String viewName() {
        return this.data().getName();
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
