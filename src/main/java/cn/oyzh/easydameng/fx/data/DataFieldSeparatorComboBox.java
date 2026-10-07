package cn.oyzh.easydameng.fx.data;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.i18n.I18nHelper;

/**
 * 数据字段分隔符下拉框
 *
 * @author oyzh
 * @since 2024/09/04
 */
public class DataFieldSeparatorComboBox extends FXComboBox<String> {

    {
        this.addItem(I18nHelper.semicolon() + "(;)");
        this.addItem(I18nHelper.comma() + "(,)");
        this.addItem(I18nHelper.space() + "( )");
    }

    /**
     * 获取选中的分隔符
     *
     * @return 分隔符
     */
    public String value() {
        int itemIndex = this.getSelectedIndex();
        if (itemIndex == 0) {
            return ";";
        }
        if (itemIndex == 1) {
            return ",";
        }
        return " ";
    }
}
