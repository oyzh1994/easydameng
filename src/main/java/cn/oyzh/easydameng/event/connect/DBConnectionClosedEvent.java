package cn.oyzh.easydameng.event.connect;

import cn.oyzh.easydameng.dameng.DamengClient;
import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * @author oyzh
 * @since 2023/11/28
 */
public class DBConnectionClosedEvent extends Event<DamengClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已断开", this.data().connectName());
    }

    public DamengConnect dbConnect() {
        return this.data().getDbConnect();
    }

    public boolean isDamengType() {
        return this.data().dialect() == DBDialect.MYSQL;
    }
}
