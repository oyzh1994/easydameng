package cn.oyzh.easydameng.tabs.table;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.tabs.DamengTab;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.scene.Cursor;

/**
 * db表设计tab
 *
 * @author oyzh
 * @since 2024/08/07
 */
public class DamengTableDesignTab extends DamengTab {

    @Override
    protected String url() {
        return super.getBasePath() + "table/damengTableDesignTab.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/table.svg");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    public void flushTitle() {
        String name = this.tableName();
        if (StringUtil.isBlank(name)) {
            name = I18nHelper.unnamedTable();
        }
        // 设置提示文本
        if (this.isUnsaved()) {
            this.setText("* " + this.dbName() + "-" + name);
        } else {
            this.setText(this.dbName() + "-" + name);
        }
    }

    public String tableName() {
        return this.controller().tableName();
    }

    public String dbName() {
        return this.controller().dbName();
    }

    /**
     * 初始化
     *
     * @param table 表
     * @param dbItem    db数据库树节点
     */
    public void init(DamengTable table, DamengDatabaseTreeItem dbItem) throws Exception {
        StageManager.showMask(() -> {
            try {
                this.controller().init(table, dbItem);
                this.flush();
            } catch (Exception ex) {
                MessageBox.exception(ex);
            }
        });
    }

    @Override
    public DamengTableDesignTabController controller() {
        return (DamengTableDesignTabController) super.controller();
    }

    public boolean isUnsaved() {
        return this.controller().isUnsaved();
    }

    @Override
    protected void onTabCloseRequest(Event event) {
        if (this.isUnsaved() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
            event.consume();
        } else {
            this.closeTab();
        }
    }

    @Override
    public DamengDatabaseTreeItem dbItem() {
        return this.controller().getDbItem();
    }


    @Override
    public void initNode() {
        this.setClosable(false);
        super.initNode();
    }
}
