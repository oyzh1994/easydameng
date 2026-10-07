package cn.oyzh.easydameng.event.query;

import cn.oyzh.easydameng.domain.DamengQuery;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;

/**
 * 达梦查询已新增事件
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DamengQueryAddedEvent extends Event<DamengQuery> {

    /**
     * 数据库树节点
     */
    private DamengSchemaTreeItem dbItem;

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
