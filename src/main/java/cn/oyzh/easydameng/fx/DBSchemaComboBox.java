package cn.oyzh.easydameng.fx;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.dameng.schema.DamengSchema;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

import java.util.List;

/**
 * db数据库选择框
 *
 * @author oyzh
 * @since 2024/01/25
 */
public class DBSchemaComboBox extends FXComboBox<String> {

    /**
     * 初始化
     *
     * @param client 达梦客户端
     */
    public void init(ShellDamengClient client) {
        this.init(client, null);
    }

    /**
     * 初始化
     *
     * @param client 达梦客户端
     * @param schema 模式名称
     */
    public void init(ShellDamengClient client, String schema) {
        this.clearItems();
        List<DamengSchema> schemas = client.schemas();
        if (CollectionUtil.isNotEmpty(schemas)) {
            this.setItem(schemas.stream().map(DamengSchema::getName).toList());
        }
        if (schema != null) {
            this.select(schema);
        }
    }
}
