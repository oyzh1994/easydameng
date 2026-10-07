package cn.oyzh.easydameng.event.terminal;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.event.Event;

/**
 * 数据库终端打开事件
 *
 * @author oyzh
 * @since 2023/11/20
 */
public class DBTerminalOpenEvent extends Event<ShellDamengClient> {

    /**
     * 模式
     */
    private String schema;

    /**
     * 获取模式
     *
     * @return 模式
     */
    public String getSchema() {
        return schema;
    }

    /**
     * 设置模式
     *
     * @param schema 模式
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }
}
