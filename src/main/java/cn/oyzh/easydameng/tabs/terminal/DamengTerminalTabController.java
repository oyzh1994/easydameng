package cn.oyzh.easydameng.tabs.terminal;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.domain.DamengConnect;
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

    /**
     * 模式
     */
    private String schema;

    /**
     * 初始化
     *
     * @param client dameng客户端
     * @param schema 模式
     */
    public void init(ShellDamengClient client, String schema) {
        this.terminal.init(client, schema);
        this.schema = schema;
    }

    /**
     * 获取模式
     *
     * @return 模式
     */
    public String getSchema() {
        return schema;
    }

    /**
     * db信息
     *
     * @return 当前db信息
     */
    protected DamengConnect shellConnect() {
        return this.terminal.shellConnect();
    }

    /**
     * 获取达梦客户端
     *
     * @return 达梦客户端
     */
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
