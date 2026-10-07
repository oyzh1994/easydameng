package cn.oyzh.easydameng.event.schema;

import cn.oyzh.easydameng.dameng.schema.DamengSchema;
import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * 达梦数据库已新增事件
 *
 * @author oyzh
 * @since 2024/01/30
 */
public class DamengSchemaAddedEvent extends Event<DamengSchema> implements EventFormatter {

    /**
     * 连接树节点
     */
    private DBConnectTreeItem connectItem;

    @Override
    public String eventFormat() {
        return String.format("[%s] 数据库已新增", this.data().getName());
    }

    /**
     * 获取连接树节点
     *
     * @return 连接树节点
     */
    public DBConnectTreeItem getConnectItem() {
        return connectItem;
    }

    /**
     * 设置连接树节点
     *
     * @param connectItem 连接树节点
     */
    public void setConnectItem(DBConnectTreeItem connectItem) {
        this.connectItem = connectItem;
    }
}
