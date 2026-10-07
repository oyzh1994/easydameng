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

    /**
     * 初始化
     *
     * @param schema 模式名称
     * @param client 达梦客户端
     */
    public void init(String schema, ShellDamengClient client) {
        this.init(schema, null, client);
    }

    /**
     * 初始化
     *
     * @param schema    模式名称
     * @param tableName 表名称
     * @param client    达梦客户端
     */
    public void init(String schema, String tableName, ShellDamengClient client) {
        List<DamengTable> list = client.selectTables(schema);
        this.setItem(list.parallelStream().map(DamengTable::getName).toList());
        if (tableName != null) {
            this.select(tableName);
        }
    }
}
