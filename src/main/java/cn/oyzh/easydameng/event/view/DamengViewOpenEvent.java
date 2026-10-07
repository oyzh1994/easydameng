package cn.oyzh.easydameng.event.view;

import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.easydameng.trees.view.DamengViewTreeItem;
import cn.oyzh.event.Event;

/**
 * 达梦视图打开事件
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DamengViewOpenEvent extends Event<DamengViewTreeItem> {

    /**
     * 模式树节点
     */
    private DamengSchemaTreeItem dbItem;

    /**
     * 获取视图名称
     *
     * @return 视图名称
     */
    public String viewName() {
        return this.data().viewName();
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
}
