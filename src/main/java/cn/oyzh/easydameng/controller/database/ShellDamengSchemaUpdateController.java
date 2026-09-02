package cn.oyzh.easydameng.controller.database;

import cn.oyzh.easydameng.data.db.DBSchema;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;
import cn.oyzh.fx.gui.text.field.ReadOnlyTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 * 编辑db库业务
 *
 * @author oyzh
 * @since 2024/01/30
 */
@StageAttribute(
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "database/damengDatabaseUpdate.fxml"
)
public class ShellDamengSchemaUpdateController extends StageController {

    /**
     * 名称
     */
    @FXML
    private ReadOnlyTextField name;

//    /**
//     * 字符集
//     */
//    @FXML
//    private DBCharsetComboBox charset;
//
//    /**
//     * 排序方式
//     */
//    @FXML
//    private DBCollationComboBox collation;

    /**
     * db库对象
     */
    private DBSchema schema;

    /**
     * db连接节点
     */
    private DBConnectTreeItem connectItem;

    /**
     * 编辑db库
     */
    @FXML
    private void save() {
        try {
            DBSchema schema = new DBSchema();
            schema.setName(this.name.getText());
//            // 字符集
//            String charset = this.charset.getSelectedItem();
//            if (!StringUtil.equalsIgnoreCase(charset, this.database.getCharset())) {
//                database.setCharset(charset);
//            }
//            // 排序规则
//            String collation = this.collation.getSelectedItem();
//            if (!StringUtil.equalsIgnoreCase(charset, this.database.getCollation())) {
//                database.setCollation(collation);
//            }
            // 修改数据库
            if (this.connectItem.alterSchema(schema)) {
                DamengEventUtil.databaseUpdated(this.connectItem, schema);
//                // 更新字符集和排序规则
//                this.database.setCharset(charset);
//                this.database.setCollation(collation);
                this.closeWindow();
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

//    @Override
//    protected void bindListeners() {
//        super.bindListeners();
//        // 字符集选中事件
//        this.charset.selectedItemChanged((observable, oldValue, newValue) -> {
//            this.collation.init(newValue, this.connectItem.getClient());
//            this.collation.select(0);
//        });
//    }

    @Override
    public void onWindowShown(WindowEvent event) {
        this.schema = this.getProp("database");
        this.connectItem = this.getProp("connectItem");
        // 数据库名
        this.name.setText(this.schema.getName());
        super.onWindowShown(event);
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.updateDatabase();
    }
}
