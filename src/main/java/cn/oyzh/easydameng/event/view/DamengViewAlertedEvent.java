package cn.oyzh.easydameng.event.view;

import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;

/**
 * 达梦视图修改事件
 *
 * @author oyzh
 * @since 2024/06/29
 */
public class DamengViewAlertedEvent extends Event<String> {

    /**
     * 模式树节点
     */
    private DamengSchemaTreeItem dbItem;

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
}
