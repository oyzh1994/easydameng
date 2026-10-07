package cn.oyzh.easydameng.event.view;

import cn.oyzh.easydameng.dameng.record.DamengRecordFilter;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.easydameng.trees.view.DamengViewTreeItem;
import cn.oyzh.event.Event;

import java.util.List;

/**
 * 达梦视图数据过滤事件
 *
 * @author oyzh
 * @since 2024/06/26
 */
public class DamengViewFilteredEvent extends Event<DamengViewTreeItem> {

    /**
     * 过滤条件集合
     */
    private List<DamengRecordFilter> filters;

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
     * 获取过滤条件集合
     *
     * @return 过滤条件集合
     */
    public List<DamengRecordFilter> getFilters() {
        return filters;
    }

    /**
     * 设置过滤条件集合
     *
     * @param filters 过滤条件集合
     */
    public void setFilters(List<DamengRecordFilter> filters) {
        this.filters = filters;
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
