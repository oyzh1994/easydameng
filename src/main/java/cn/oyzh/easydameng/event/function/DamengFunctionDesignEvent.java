package cn.oyzh.easydameng.event.function;

import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/06/29
 */
public class DamengFunctionDesignEvent extends Event<DamengFunction> {

    private DamengSchemaTreeItem dbItem;

    public String functionName() {
        return this.data().getName();
    }

    public DamengSchemaTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengSchemaTreeItem dbItem) {
        this.dbItem = dbItem;
    }

}
