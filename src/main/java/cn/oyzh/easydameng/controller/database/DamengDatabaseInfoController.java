// package cn.oyzh.easydameng.controller.database;
//
// import cn.oyzh.easydameng.db.DBDatabase;
// import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
// import cn.oyzh.fx.gui.text.field.ReadOnlyTextField;
// import cn.oyzh.fx.plus.FXConst;
// import cn.oyzh.fx.plus.controller.StageController;
// import cn.oyzh.fx.plus.window.FXStageStyle;
// import cn.oyzh.fx.plus.window.StageAttribute;
// import cn.oyzh.i18n.I18nHelper;
// import javafx.fxml.FXML;
// import javafx.stage.Modality;
// import javafx.stage.WindowEvent;
//
// /**
//  * db库信息业务
//  *
//  * @author oyzh
//  * @since 2024/01/30
//  */
// @StageAttribute(
//         stageStyle = FXStageStyle.UTILITY,
//         modality = Modality.APPLICATION_MODAL,
//         value = FXConst.FXML_PATH + "database/damengDatabaseInfo.fxml"
// )
// public class DamengDatabaseInfoController extends StageController {
//
//     /**
//      * 名称
//      */
//     @FXML
//     private ReadOnlyTextField dbName;
//
//     /**
//      * 字符集
//      */
//     @FXML
//     private ReadOnlyTextField dbCharset;
//
//     /**
//      * 排序方式
//      */
//     @FXML
//     private ReadOnlyTextField dbCollation;
//
//     @Override
//     public void onWindowShown(WindowEvent event) {
//         super.onWindowShown(event);
//         this.stage.hideOnEscape();
//         DamengDatabaseTreeItem dbItem = this.getProp("dbItem");
//         DBDatabase database = dbItem.value();
//         this.dbName.setText(database.getName());
//         this.dbCharset.setText(database.getCharset());
//         this.dbCollation.setText(database.getCollation());
//     }
//
//     @Override
//     public String getViewTitle() {
//         return I18nHelper.databaseInfo();
//     }
// }
