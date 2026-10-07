package cn.oyzh.easydameng.event.table;

import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;

/**
 * 达梦表设计事件
 *
 * @author oyzh
 * @since 2024/08/07
 */
public class DamengTableDesignEvent extends Event<DamengTable> {

    /**
     * 模式树节点
     */
    private DamengSchemaTreeItem dbItem;

    /**
     * 获取模式
     *
     * @return 模式
     */
    public String schema() {
        return this.dbItem.schema();
    }

    /**
     * 获取模式树节点
     *
     * @return 模式树节点
     */
    public DamengSchemaTreeItem getDbItem() {
        return dbItem;
    }

    /**
     * 设置模式树节点
     *
     * @param dbItem 模式树节点
     */
    public void setDbItem(DamengSchemaTreeItem dbItem) {
        this.dbItem = dbItem;
    }

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String tableName() {
        return this.data().getName();
    }
}
