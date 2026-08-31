package cn.oyzh.easydameng.tabs.terminal;

import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.terminal.DamengTerminalPane;
import cn.oyzh.fx.gui.tabs.RichTabController;
import javafx.event.Event;
import javafx.fxml.FXML;

/**
 * dameng命令行tab内容组件
 *
 * @author oyzh
 * @since 2023/07/21
 */
public class DamengTerminalTabController extends RichTabController {

    /**
     * dameng命令行文本域
     */
    @FXML
    private DamengTerminalPane terminal;

    private String dbName;

    /**
     * 初始化
     *
     * @param client dameng客户端
     */
    public void init(ShellDamengClient client, String dbName) {
        this.terminal.init(client, dbName);
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }

    /**
     * db信息
     *
     * @return 当前db信息
     */
    protected DamengConnect shellConnect() {
        return this.terminal.shellConnect();
    }

    public ShellDamengClient client() {
        return this.terminal.getClient();
    }

    @Override
    public void onTabClosed(Event event) {
        if (this.terminal.isTemporary()) {
            this.client().close();
        }
        super.onTabClosed(event);
    }
}
