package cn.oyzh.easydameng.event.schema;

import cn.oyzh.easydameng.data.db.DBSchema;
import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * @author oyzh
 * @since 2024/01/30
 */
public class DamengSchemaUpdatedEvent extends Event<DBSchema> implements EventFormatter {

    private DBConnectTreeItem connectItem;

    @Override
    public String eventFormat() {
        return String.format("[%s] 数据库已修改", this.data().getName());
    }

    public DBConnectTreeItem getConnectItem() {
        return connectItem;
    }

    public void setConnectItem(DBConnectTreeItem connectItem) {
        this.connectItem = connectItem;
    }
}
