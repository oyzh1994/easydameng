package cn.oyzh.easydameng.tabs.function;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.easydameng.tabs.DamengTab;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.fx.gui.svg.glyph.database.FunctionSVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.scene.Cursor;

/**
 * 达梦函数设计标签
 *
 * @author oyzh
 * @since 2024/02/18
 */
public class DamengFunctionDesignTab extends DamengTab {

    @Override
    protected String url() {
        return super.getBasePath() + "function/damengFunctionDesignTab.fxml";
    }

    @Override
    public void flushGraphic() {
        FunctionSVGGlyph graphic = (FunctionSVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new FunctionSVGGlyph();
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    public void flushTitle() {
        String name = this.functionName();
        if (StringUtil.isBlank(name)) {
            name = I18nHelper.unnamedFunction();
        }
        // 设置提示文本
        if (this.isUnsaved()) {
            this.setText("* " + name + "@" + this.schema() + "(" + this.connectName() + ")");
        } else {
            this.setText(name + "@" + this.schema() + "(" + this.connectName() + ")");
        }
    }

    /**
     * 获取函数名称
     *
     * @return 函数名称
     */
    public String functionName() {
        return this.controller().getFunction().getName();
    }

    @Override
    public DamengSchemaTreeItem dbItem() {
        return this.controller().getDbItem();
    }

    /**
     * 获取模式
     *
     * @return 模式
     */
    public String schema() {
        return this.dbItem().schema();
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String connectName() {
        return this.dbItem().connectName();
    }

    /**
     * 初始化
     *
     * @param function 查询对象
     * @param item     db库树节点
     */
    public void init(DamengFunction function, DamengSchemaTreeItem item) {
        this.controller().init(function, item);
        // 刷新tab
        this.flush();
    }

    @Override
    public DamengFunctionDesignTabController controller() {
        return (DamengFunctionDesignTabController) super.controller();
    }

    /**
     * 是否未保存
     *
     * @return 是否未保存
     */
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
}
