package cn.oyzh.easydameng.event.connect;

import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * @author oyzh
 * @since 2024/7/26
 */
public class DamengConnectDeletedEvent extends Event<DamengConnect> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("连接[%s] 已删除", this.data().getName());
    }
}
