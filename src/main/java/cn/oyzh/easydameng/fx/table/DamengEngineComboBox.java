package cn.oyzh.easydameng.fx.table;

import cn.oyzh.easydameng.dameng.DamengClient;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * 引擎下拉选择框
 *
 * @author oyzh
 * @since 2024/01/26
 */
public class DamengEngineComboBox extends FXComboBox<String> {

    public void init(DamengClient client) {
        this.clearItems();
        for (String engine : client.engines()) {
            this.addItem(engine.toUpperCase());
        }
    }

    @Override
    public void select(String engine) {
        if (engine != null) {
            super.select(engine.toUpperCase());
        }
    }
}
