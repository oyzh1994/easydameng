package cn.oyzh.easydameng.event.connect;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.fx.db.DBDialect;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * 数据库连接已关闭事件
 *
 * @author oyzh
 * @since 2023/11/28
 */
public class DBConnectionClosedEvent extends Event<ShellDamengClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已断开", this.data().connectName());
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     */
    public DamengConnect dbConnect() {
        return this.data().getDbConnect();
    }

    /**
     * 是否为达梦类型
     *
     * @return 是否为达梦类型
     */
    public boolean isDamengType() {
        return this.data().dialect() == DBDialect.DAMENG;
    }
}
