package cn.oyzh.easydameng.fx.data;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.i18n.I18nHelper;

/**
 * 数据导出类型下拉框
 *
 * @author oyzh
 * @since 2024/08/22
 */
public class DBDumpDataTypeComboBox extends FXComboBox<String> {

    {
        this.addItem(I18nHelper.dataAndStructure());
        this.addItem(I18nHelper.structure());
    }

    /**
     * 是否为结构加数据
     *
     * @return 是否为结构加数据
     */
    public boolean isFull() {
        return this.getSelectedIndex() == 0;
    }

}
