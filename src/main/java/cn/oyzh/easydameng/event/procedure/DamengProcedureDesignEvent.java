package cn.oyzh.easydameng.event.procedure;

import cn.oyzh.easydameng.procedure.DamengProcedure;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;

/**
 * 达梦存储过程设计事件
 *
 * @author oyzh
 * @since 2024/06/29
 */
public class DamengProcedureDesignEvent extends Event<DamengProcedure> {

    /**
     * 数据库树节点
     */
    private DamengSchemaTreeItem dbItem;

    /**
     * 获取存储过程名称
     *
     * @return 存储过程名称
     */
    public String procedureName() {
        return this.data().getName();
    }

    /**
     * 获取数据库树节点
     *
     * @return 数据库树节点
     */
    public DamengSchemaTreeItem getDbItem() {
        return dbItem;
    }

    /**
     * 设置数据库树节点
     *
     * @param dbItem 数据库树节点
     */
    public void setDbItem(DamengSchemaTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
