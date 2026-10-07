package cn.oyzh.easydameng.fx.table;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * 数据库连接符号下拉框
 *
 * @author oyzh
 * @since 2024/1/26
 */
public class DBJoinSymbolComboBox extends FXComboBox<String> {

    {
        this.addItem("AND");
        this.addItem("OR");
    }
}
