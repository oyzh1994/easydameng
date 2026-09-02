package cn.oyzh.easydameng.terminal.basic;

import cn.oyzh.easydameng.data.db.DBSchema;
import cn.oyzh.easydameng.terminal.DamengTerminalCommandHandler;
import cn.oyzh.easydameng.terminal.DamengTerminalPane;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;

import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-30
 */
public class DamengUseTerminalCommandHandler extends DamengTerminalCommandHandler<TerminalCommand> {

    @Override
    protected TerminalCommand parseCommand(String line, String[] args) {
        TerminalCommand terminalCommand = new TerminalCommand();
        terminalCommand.setArgs(args);
        terminalCommand.setCommand(line);
        return terminalCommand;
    }

    @Override
    protected boolean checkArgs(String[] args) throws RuntimeException {
        return args != null && args.length == 2;
    }

    @Override
    public String commandName() {
        return "use";
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, DamengTerminalPane terminal) {
        terminal.setDbName(command.getArgs()[1]);
        TerminalExecuteResult result = TerminalExecuteResult.ok();
        try {
            List<DBSchema> databases = terminal.getClient().databases();
            List<String> dbs = databases.stream()
                    .map(DBSchema::getName)
                    .toList();
            if (dbs.contains(terminal.getDbName())) {
                result.setResult("Database invalid");
            } else {
                result.setResult("Database changed");
            }
        } catch (Exception ex) {
            result.setException(ex);
        }
        return result;
    }
}
