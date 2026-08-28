package cn.oyzh.easydameng.event.sql;

import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * @author oyzh
 * @since 2024/06/29
 */
public class ShellPrintSqlEvent extends Event<String> implements EventFormatter {

    private DamengConnect connect;

    public void setConnect(DamengConnect connect) {
        this.connect = connect;
    }

    @Override
    public String eventFormat() {
        return " " + this.connect.getName() + " > " + this.data();
    }
}
