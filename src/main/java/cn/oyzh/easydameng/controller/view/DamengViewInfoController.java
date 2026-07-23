package cn.oyzh.easydameng.controller.view;

import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.trees.view.DamengViewTreeItem;
import cn.oyzh.fx.gui.text.area.ReadOnlyTextArea;
import cn.oyzh.fx.gui.text.field.ReadOnlyTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
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
        value = FXConst.FXML_PATH + "view/damengViewInfo.fxml"
)
public class DamengViewInfoController extends StageController {

    /**
     * 名称
     */
    @FXML
    private ReadOnlyTextField viewName;

    /**
     * 注释
     */
    @FXML
    private ReadOnlyTextArea viewComment;

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        DamengViewTreeItem item = this.getProp("item");
        DamengView view = item.value();
        this.viewName.setText(view.getName());
        this.viewComment.setText(view.getComment());
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.viewInfo();
    }
}
