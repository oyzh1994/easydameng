package cn.oyzh.easydameng.event.sql;

import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * 打印SQL事件
 *
 * @author oyzh
 * @since 2024/06/29
 */
public class ShellPrintSqlEvent extends Event<String> implements EventFormatter {

    /**
     * 数据库连接
     */
    private DamengConnect connect;

    /**
     * 设置数据库连接
     *
     * @param connect 数据库连接
     */
    public void setConnect(DamengConnect connect) {
        this.connect = connect;
    }

    @Override
    public String eventFormat() {
        return " " + this.connect.getName() + " > " + this.data();
    }
}
