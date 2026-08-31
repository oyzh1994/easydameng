package cn.oyzh.easydameng.fx.table;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

import java.util.List;

/**
 * db数据库选择框
 *
 * @author oyzh
 * @since 2024/01/25
 */
public class DamengTableComboBox extends FXComboBox<String> {

    public void init(String dbName, ShellDamengClient client) {
        this.init(dbName, null, client);
    }

    public void init(String dbName, String tableName, ShellDamengClient client) {
        List<DamengTable> list = client.selectTables(dbName);
        this.setItem(list.parallelStream().map(DamengTable::getName).toList());
        if (tableName != null) {
            this.select(tableName);
        }
    }
}
