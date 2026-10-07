package cn.oyzh.easydameng.terminal.basic;

/**
 * 显示数据库列表终端命令处理器（show dbs 简写）
 *
 * @author oyzh
 * @since 2024-12-30
 */
public class DamengShowDbsTerminalCommandHandler extends DamengShowDatabasesTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "dbs;";
    }
}
