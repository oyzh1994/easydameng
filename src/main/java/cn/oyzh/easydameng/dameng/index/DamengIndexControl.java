package cn.oyzh.easydameng.dameng.index;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.fx.table.DamengIndexFieldTextFiled;
import cn.oyzh.easydameng.fx.table.DamengIndexMethodComboBox;
import cn.oyzh.easydameng.fx.table.DamengIndexTypeComboBox;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.fx.db.util.DBUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.i18n.I18nHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 达梦索引控件
 *
 * @author oyzh
 * @since 2024/09/14
 */
public class DamengIndexControl extends DamengIndex {

    /**
     * 字段列表
     */
    private List<DamengColumn> columnList;

    /**
     * 设置字段列表
     *
     * @param columnList 字段列表
     */
    public void setColumnList(List<DamengColumn> columnList) {
        this.columnList = columnList;
    }

    /**
     * 获取名称控件
     *
     * @return 名称控件
     */
    public ClearableTextField getNameControl() {
        ClearableTextField textField = new ClearableTextField();
        if (StringUtil.isEmpty(this.getName())) {
            this.setName(DBUtil.genIndexName());
        }
        textField.setText(this.getName());
        textField.setPromptText(I18nHelper.pleaseInputName());
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setName(newValue));
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取字段控件
     *
     * @return 字段控件
     */
    public DamengIndexFieldTextFiled getColumnControl() {
        //List<DamengColumn> columnList = CacheHelper.get("dameng:columnList");
        if (this.columnList == null) {
            this.columnList = null;
        }
        DamengIndexFieldTextFiled textField = new DamengIndexFieldTextFiled(this, this.columnList, this.getColumns());
        textField.setFlexWidth("100% - 12");
        textField.addTextChangeListener((observable, oldValue, newValue) -> this.setColumns(textField.getColumns()));
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 获取类型控件
     *
     * @return 类型控件
     */
    public DamengIndexTypeComboBox getTypeControl() {
        DamengIndexTypeComboBox comboBox = new DamengIndexTypeComboBox();
        comboBox.selectFirstIfNull(this.getType());
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setType(newValue));
        TableViewUtil.rowOnCtrlS(comboBox);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        // 初始化数据
        this.setType(comboBox.getValue());
        return comboBox;
    }

    /**
     * 获取方式控件
     *
     * @return 方式控件
     */
    public DamengIndexMethodComboBox getMethodControl() {
        DamengIndexMethodComboBox comboBox = new DamengIndexMethodComboBox();
        comboBox.selectFirstIfNull(this.getMethod());
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.setMethod(newValue));
        TableViewUtil.rowOnCtrlS(comboBox);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    /**
     * 转换索引为控件对象
     *
     * @param index 索引
     * @return 索引控件
     */
    public static DamengIndexControl of(DamengIndex index) {
        DamengIndexControl control = new DamengIndexControl();
        control.copy(index);
        return control;
    }

    /**
     * 批量转换索引为控件对象
     *
     * @param indices 索引列表
     * @return 索引控件列表
     */
    public static List<DamengIndexControl> of(List<DamengIndex> indices) {
        List<DamengIndexControl> controls = new ArrayList<>();
        for (DamengIndex index : indices) {
            controls.add(of(index));
        }
        return controls;
    }
}
