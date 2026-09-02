package cn.oyzh.easydameng.event.schema;

import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * @author oyzh
 * @since 2024/01/30
 */
public class DamengSchemaDroppedEvent extends Event<DamengSchemaTreeItem> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 数据库已删除", this.data().schema());
    }
}
