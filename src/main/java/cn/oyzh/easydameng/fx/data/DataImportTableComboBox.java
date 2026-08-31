package cn.oyzh.easydameng.fx.data;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.converter.SimpleStringConverter;

/**
 * @author oyzh
 * @since 2024/8/27
 */
public class DataImportTableComboBox extends FXComboBox<DamengDataImportFile> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(DamengDataImportFile object) {
                if (object != null) {
                    return object.getTableName();
                }
                return null;
            }
        });
    }

    public String getSelectedTableName() {
        return this.getSelectedItem().getTableName();
    }
}
