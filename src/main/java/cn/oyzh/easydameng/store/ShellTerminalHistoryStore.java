package cn.oyzh.easydameng.store;

import cn.oyzh.easydameng.domain.ShellTerminalHistory;
import cn.oyzh.store.jdbc.JdbcStandardStore;

/**
 * Shell 终端历史记录存储。
 *
 * @author oyzh
 * @since 2024-11-25
 */
public class ShellTerminalHistoryStore extends JdbcStandardStore<ShellTerminalHistory> {

    /**
     * 当前实例
     */
    public static final ShellTerminalHistoryStore INSTANCE = new ShellTerminalHistoryStore();

    /**
     * 保存终端历史记录
     *
     * @param model 终端历史记录对象
     * @return 结果
     */
    public boolean replace(ShellTerminalHistory model) {
        return this.insert(model);
    }

    @Override
    protected Class<ShellTerminalHistory> modelClass() {
        return ShellTerminalHistory.class;
    }
}
