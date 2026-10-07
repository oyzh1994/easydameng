package cn.oyzh.easydameng.event.query;

import cn.oyzh.easydameng.domain.DamengQuery;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;

/**
 * 达梦查询已重命名事件
 *
 * @author oyzh
 * @since 2024/01/23
 */
public class DamengQueryRenamedEvent extends Event<DamengQuery> {

    /**
     * 数据库树节点
     */
    private DamengSchemaTreeItem dbItem;

    /**
     * 获取查询名称
     *
     * @return 查询名称
     */
    public String queryName() {
        return this.data().getName();
    }

    /**
     * 获取查询唯一标识
     *
     * @return 查询唯一标识
     */
    public String queryId() {
        return this.data().getUid();
    }

    /**
     * 获取数据库模式
     *
     * @return 数据库模式
     */
    public String schema() {
        return this.dbItem.schema();
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
