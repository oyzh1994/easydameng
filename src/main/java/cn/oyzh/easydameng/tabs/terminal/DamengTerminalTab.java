package cn.oyzh.easydameng.tabs.terminal;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.fx.gui.svg.glyph.TerminalSVGGlyph;
import cn.oyzh.fx.gui.tabs.RichTab;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.Cursor;

/**
 * dameng终端tab
 *
 * @author oyzh
 * @since 2023/7/21
 */
public class DamengTerminalTab extends RichTab {

    //public DamengTerminalTab(ShellDamengClient client, String schema) {
    //    this.init(client, schema);
    //}

    @Override
    public DamengTerminalTabController controller() {
        return (DamengTerminalTabController) super.controller();
    }

    @Override
    protected String url() {
        return "/tabs/terminal/damengTerminalTab.fxml";
    }

    @Override
    public void flushGraphic() {
        TerminalSVGGlyph graphic = (TerminalSVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new TerminalSVGGlyph();
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    protected String getTabTitle() {
        DamengConnect connect = this.shellConnect();
        if (connect != null) {
            return connect.getName();
        }
        return I18nHelper.unnamedConnection();
    }

    /**
     * 初始化
     *
     * @param client dameng客户端
     * @param schema 模式
     */
    public void init(ShellDamengClient client, String schema) {
        try {
            if (client == null) {
                DamengConnect connect = new DamengConnect();
                connect.setName(I18nHelper.unnamedConnection());
                this.flushGraphic();
                this.controller().init(new ShellDamengClient(connect), schema);
            } else {
                this.flushGraphic();
                this.controller().init(client, schema);
            }
            this.flushTitle();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * db信息
     *
     * @return 当前db信息
     */
    public DamengConnect shellConnect() {
        return this.controller().shellConnect();
    }

    public ShellDamengClient client() {
        return this.controller().client();
    }

    public String schema() {
        return this.controller().getSchema();
    }
}
