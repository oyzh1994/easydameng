package cn.oyzh.easydameng.event.terminal;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/11/20
 */
public class DBTerminalOpenEvent extends Event<ShellDamengClient> {

    private String schema;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
