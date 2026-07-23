package cn.oyzh.easydameng.fx;

import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.store.DamengConnectStore;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.converter.SimpleStringConverter;

/**
 * db连接库选择框
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class DBInfoComboBox extends FXComboBox<DamengConnect> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(DamengConnect object) {
                if (object == null) {
                    return null;
                }
                return object.getName();
            }
        });
        this.setItem(DamengConnectStore.INSTANCE.load());
    }
}
