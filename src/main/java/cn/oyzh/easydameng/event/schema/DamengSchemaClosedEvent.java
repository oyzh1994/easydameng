package cn.oyzh.easydameng.event.schema;

import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * 达梦数据库已关闭事件
 *
 * @author oyzh
 * @since 2024/01/26
 */
public class DamengSchemaClosedEvent extends Event<DamengSchemaTreeItem> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 数据库已关闭", this.data().value());
    }
}
