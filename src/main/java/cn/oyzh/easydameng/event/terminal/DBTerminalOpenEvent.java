package cn.oyzh.easydameng.event.terminal;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/11/20
 */
public class DBTerminalOpenEvent extends Event<ShellDamengClient> {

    private String dbName;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
