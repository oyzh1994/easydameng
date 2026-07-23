package cn.oyzh.easydameng.event.database;

import cn.oyzh.easydameng.db.DBDatabase;
import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * @author oyzh
 * @since 2024/01/30
 */
public class DamengDatabaseUpdatedEvent extends Event<DBDatabase> implements EventFormatter {

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
