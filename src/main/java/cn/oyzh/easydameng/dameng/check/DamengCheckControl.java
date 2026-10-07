package cn.oyzh.easydameng.dameng.check;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.db.util.DBUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.i18n.I18nHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 达梦检查约束控件
 *
 * @author oyzh
 * @since 2024/09/11
 */
public class DamengCheckControl extends DamengCheck {

    /**
     * 获取名称控件
     *
     * @return 名称控件
     */
    public ClearableTextField getNameControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputName());
        if (StringUtil.isEmpty(this.getName())) {
            this.setName(DBUtil.genCheckName());
        }
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setName(newValue));
        textField.setText(this.getName());
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取子语句控件
     *
     * @return 子语句控件
     */
    public ClearableTextField getClauseControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputName());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setClause(newValue));
        textField.setText(this.getClause());
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 转换检查约束为控件对象
     *
     * @param check 检查约束
     * @return 检查约束控件
     */
    public static DamengCheckControl of(DamengCheck check) {
        DamengCheckControl control = new DamengCheckControl();
        control.copy(check);
        return control;
    }

    /**
     * 批量转换检查约束为控件对象
     *
     * @param checks 检查约束列表
     * @return 检查约束控件列表
     */
    public static List<DamengCheckControl> of(List<DamengCheck> checks) {
        List<DamengCheckControl> controls = new ArrayList<>();
        for (DamengCheck check : checks) {
            controls.add(of(check));
        }
        return controls;
    }
}
