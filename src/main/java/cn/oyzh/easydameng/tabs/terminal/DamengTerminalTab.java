package cn.oyzh.easydameng.tabs.terminal;

import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.dameng.DamengClient;
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

    public DamengTerminalTab(DamengClient client, String dbName) {
        this.init(client, dbName);
    }

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
        DamengConnect connect = this.dbConnect();
        if (connect != null) {
            return connect.getName();
        }
        return I18nHelper.unnamedConnection();
    }

    /**
     * 初始化
     *
     * @param client dameng客户端
     */
    private void init(DamengClient client, String dbName) {
        try {
            if (client == null) {
                DamengConnect connect = new DamengConnect();
                connect.setName(I18nHelper.unnamedConnection());
                this.flushGraphic();
                this.controller().init(new DamengClient(connect), dbName);
            } else {
                this.flushGraphic();
                this.controller().init(client, dbName);
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
    public DamengConnect dbConnect() {
        return this.controller().getDbConnect();
    }

    public DamengClient client() {
        return this.controller().client();
    }

    public String dbName() {
        return this.controller().getDbName();
    }
}
