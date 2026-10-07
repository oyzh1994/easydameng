package cn.oyzh.easydameng.fx.routine;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * 达梦参数模式下拉框
 *
 * @author oyzh
 * @since 2024/06/26
 */
public class DamengParamModeComboBox extends FXComboBox<String> {

    @Override
    public void initNode() {
        this.addItem("IN");
        this.addItem("OUT");
        this.addItem("IN/OUT");
    }
}
