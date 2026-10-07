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

    /**
     * 获取当前选中的数据库类型名称
     *
     * @return 数据库类型名称
     */
    public String getType() {
        return this.getSelectedItem().name();
    }

    /**
     * 判断当前选中的是否为达梦数据库
     *
     * @return 是否为达梦数据库
     */
    public boolean isDameng() {
        return this.getSelectedItem() == DBDialect.DAMENG;
    }

    /**
     * 按类型名称选中对应项
     *
     * @param type 类型名称
     */
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
