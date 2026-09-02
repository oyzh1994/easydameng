package cn.oyzh.easydameng.event.database;

import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * @author oyzh
 * @since 2024/01/26
 */
public class DamengDatabaseClosedEvent extends Event<DamengSchemaTreeItem> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 数据库已关闭", this.data().value());
    }
}
