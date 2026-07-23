package cn.oyzh.easydameng.event.function;

import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/06/29
 */
public class DamengFunctionDesignEvent extends Event<DamengFunction> {

    private DamengDatabaseTreeItem dbItem;

    public String functionName() {
        return this.data().getName();
    }

    public DamengDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }

}
