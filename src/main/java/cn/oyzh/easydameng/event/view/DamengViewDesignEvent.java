package cn.oyzh.easydameng.event.view;

import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;

/**
 * 达梦视图设计事件
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DamengViewDesignEvent extends Event<DamengView> {

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
        return this.data().getName();
    }

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

}
