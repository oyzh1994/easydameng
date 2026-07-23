package cn.oyzh.easydameng.terminal.basic;

/**
 * @author oyzh
 * @since 2024-12-30
 */
public class DamengShowDbsTerminalCommandHandler extends DamengShowDatabasesTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "dbs;";
    }
}
