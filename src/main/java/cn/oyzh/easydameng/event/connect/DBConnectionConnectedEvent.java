package cn.oyzh.easydameng.event.connect;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * @author oyzh
 * @since 2023/11/28
 */
public class DBConnectionConnectedEvent extends Event<ShellDamengClient> implements  EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已连接", this.data().connectName());
    }

    public DamengConnect dbConnect() {
        return this.data().getDbConnect();
    }
}
