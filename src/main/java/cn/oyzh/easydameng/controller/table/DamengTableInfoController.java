package cn.oyzh.easydameng.controller.table;

import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.trees.table.DamengTableTreeItem;
import cn.oyzh.fx.editor.incubator.Editor;
import cn.oyzh.fx.gui.text.area.ReadOnlyTextArea;
import cn.oyzh.fx.gui.text.field.ReadOnlyTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.box.FXVBox;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 * db表信息业务
 *
 * @author oyzh
 * @since 2024/01/30
 */
@StageAttribute(
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "table/damengTableInfo.fxml"
)
public class DamengTableInfoController extends StageController {

    /**
     * 名称
     */
    @FXML
    private ReadOnlyTextField tableName;

    /**
     * 引擎
     */
    @FXML
    private ReadOnlyTextField tableEngine;

    /**
     * 注释
     */
    @FXML
    private ReadOnlyTextArea tableComment;

    /**
     * 定义
     */
    @FXML
    private Editor tableDefinition;

    @Override
    public void onWindowShown(WindowEvent event) {
        DamengTableTreeItem tableItem = this.getProp("tableItem");
        DamengTable table = tableItem.value();
        this.tableName.setText(table.getName());
        this.tableEngine.setText(table.getEngine());
        this.tableComment.setText(table.getComment());
        this.tableDefinition.setText(table.getCreateDefinition());
        super.onWindowShown(event);
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.tableInfo();
    }
}
