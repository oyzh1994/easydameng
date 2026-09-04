package cn.oyzh.easydameng.fx.event;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * @author oyzh
 * @since 2024-09-10
 */
public class DamengEventIntervalTypeCombobox extends FXComboBox<String> {

    @Override
    public void select(String val) {
        if (val != null) {
            super.select(val.toUpperCase());
        } else {
            super.clearSelection();
        }
    }


    @Override
    public void initNode() {
        this.addItem("YEARLY");
        this.addItem("MONTHLY");
        this.addItem("WEEKLY");
        this.addItem("DAILY");
        this.addItem("HOURLY");
        this.addItem("MINUTELY");
        this.addItem("SECONDLY");
        super.initNode();
    }
}
