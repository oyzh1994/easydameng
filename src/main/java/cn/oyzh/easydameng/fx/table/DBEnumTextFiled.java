package cn.oyzh.easydameng.fx.table;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.popups.DamengColumnEnumPopupController;
import cn.oyzh.fx.gui.text.field.ChooseTextField;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import cn.oyzh.i18n.I18nHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 枚举值文本框
 *
 * @author oyzh
 * @since 2024/7/10
 */
public class DBEnumTextFiled extends ChooseTextField {

    {
        super.setAction(this::initPopup);
        this.setPromptText(I18nHelper.pleaseSelectContent());
    }

    /**
     * 枚举值列表
     */
    private List<String> values;

    /**
     * 构造枚举值文本框
     */
    public DBEnumTextFiled() {
    }

    /**
     * 构造枚举值文本框
     *
     * @param values 枚举值列表
     */
    public DBEnumTextFiled(List<String> values) {
        this.values = values;
    }

    /**
     * 弹窗
     */
    private PopupAdapter popup;

    /**
     * 初始化弹窗
     */
    protected void initPopup() {
        this.popup = PopupManager.parsePopup(DamengColumnEnumPopupController.class);
        this.popup.setProp("values", this.values);
        this.popup.setProp("onSubmit", (Runnable) () -> {
            FXListView<ClearableTextField> listView = this.listView();
            if (listView != null) {
                this.values = new ArrayList<>();
                for (ClearableTextField item : listView.getItems()) {
                    this.values.add(item.getTextTrim());
                }
            }
            this.initText();
        });
        this.popup.showPopup(this);
    }

    /**
     * 初始化文本
     */
    public void initText() {
        if (CollectionUtil.isEmpty(this.values)) {
            this.setText("");
        } else {
            StringBuilder builder = new StringBuilder();
            for (String value : this.values) {
                builder.append(",").append("'").append(value).append("'");
            }
            this.setText(builder.substring(1));
        }
    }

    /**
     * 设置枚举值
     *
     * @param values 枚举值列表
     */
    public void setValues(List<String> values) {
        this.values = values;
        FXListView listView = this.listView();
        if (listView != null) {
            listView.setItem(values);
        }
        this.initText();
    }

    /**
     * 获取列表视图
     *
     * @return 列表视图
     */
    protected FXListView<ClearableTextField> listView() {
        if (this.popup != null) {
            return (FXListView<ClearableTextField>) this.popup.content().lookup("#listView");
        }
        return null;
    }
}
