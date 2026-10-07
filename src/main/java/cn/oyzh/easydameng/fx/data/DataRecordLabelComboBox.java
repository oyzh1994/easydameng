package cn.oyzh.easydameng.fx.data;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * 数据记录标签下拉框
 *
 * @author oyzh
 * @since 2024/8/27
 */
public class DataRecordLabelComboBox extends FXComboBox<String> {

    {
        this.addItem("(Root)");
        this.addItem("RECORDS");
    }

    /**
     * 是否为根节点
     *
     * @return 是否为根节点
     */
    public boolean isRoot() {
        return this.getSelectedIndex() == 0;
    }
}
