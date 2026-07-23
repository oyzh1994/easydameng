package cn.oyzh.easydameng.fx;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.DamengClient;
import cn.oyzh.easydameng.db.DBDatabase;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

import java.util.List;

/**
 * db数据库选择框
 *
 * @author oyzh
 * @since 2024/01/25
 */
public class DBDatabaseComboBox extends FXComboBox<String> {

    public void init(DamengClient client) {
        this.init(client, null);
    }

    public void init(DamengClient client, String dbName) {
        this.clearItems();
        List<DBDatabase> databases = client.databases();
        if (CollectionUtil.isNotEmpty(databases)) {
            this.setItem(databases.stream().map(DBDatabase::getName).toList());
        }
        if (dbName != null) {
            this.select(dbName);
        }
    }
}
