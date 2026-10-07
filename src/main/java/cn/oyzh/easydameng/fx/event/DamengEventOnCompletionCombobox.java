package cn.oyzh.easydameng.fx.event;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * 达梦事件完成后处理方式下拉框
 *
 * @author oyzh
 * @since 2024-09-09
 */
public class DamengEventOnCompletionCombobox extends FXComboBox<String> {

    {
        this.addItem("PRESERVE");
        this.addItem("NOT PRESERVE");
    }

    @Override
    public void select(String val) {
        if (val != null) {
            super.select(val.toUpperCase());
        } else {
            super.clearSelection();
        }
    }
}
