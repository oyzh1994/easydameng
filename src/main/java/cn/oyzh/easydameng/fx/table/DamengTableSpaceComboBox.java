package cn.oyzh.easydameng.fx.table;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * 表空间下拉选择框
 *
 * @author oyzh
 * @since 2024/01/26
 */
public class DamengTableSpaceComboBox extends FXComboBox<String> {

    /**
     * 初始化表空间列表
     *
     * @param client 达梦客户端
     */
    public void init(ShellDamengClient client) {
        this.clearItems();
        for (String tableSpace : client.tableSpaces()) {
            this.addItem(tableSpace);
        }
    }
}
