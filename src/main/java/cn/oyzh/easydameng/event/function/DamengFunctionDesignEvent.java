package cn.oyzh.easydameng.event.function;

import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;

/**
 * 达梦函数设计事件
 *
 * @author oyzh
 * @since 2024/06/29
 */
public class DamengFunctionDesignEvent extends Event<DamengFunction> {

    /**
     * 数据库树节点
     */
    private DamengSchemaTreeItem dbItem;

    /**
     * 获取函数名称
     *
     * @return 函数名称
     */
    public String functionName() {
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
