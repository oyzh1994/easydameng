package cn.oyzh.easydameng.event.procedure;

import cn.oyzh.easydameng.procedure.DamengProcedure;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/06/29
 */
public class DamengProcedureDesignEvent extends Event<DamengProcedure> {

    private DamengSchemaTreeItem dbItem;

    public String procedureName() {
        return this.data().getName();
    }

    public DamengSchemaTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(DamengSchemaTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
