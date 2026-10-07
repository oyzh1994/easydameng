package cn.oyzh.easydameng.event.connect;

import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * 达梦连接已修改事件
 *
 * @author oyzh
 * @since 2024/01/30
 */
public class DamengConnectUpdatedEvent extends Event<DamengConnect> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("连接[%s] 已修改", this.data().getName());
    }
}
