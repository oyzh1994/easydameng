package cn.oyzh.easydameng.fx;

import cn.oyzh.fx.db.DBDialect;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * db类型选择框
 *
 * @author oyzh
 * @since 2023/12/15
 */
public class DBTypeComboBox extends FXComboBox<DBDialect> {

    {
        this.setItem(DBDialect.valueList());
    }

    public String getType() {
        return this.getSelectedItem().name();
    }

    public boolean isDameng() {
        return this.getSelectedItem() == DBDialect.DAMENG;
    }

    public void selectType(String type) {
        if (type != null) {
            for (DBDialect item : this.getItems()) {
                if (item.name().equals(type)) {
                    this.select(item);
                    break;
                }
            }
        }
    }
}
