package cn.oyzh.easydameng.fx.data;

import cn.oyzh.easydameng.dameng.dto.ShellDamengDataExportTable;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.converter.SimpleStringConverter;

/**
 * 数据导出表下拉框
 *
 * @author oyzh
 * @since 2024/8/27
 */
public class DataExportTableComboBox extends FXComboBox<ShellDamengDataExportTable> {

    @Override
   public void initNode(){
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(ShellDamengDataExportTable object) {
                if (object != null) {
                    return object.getName();
                }
                return null;
            }
        });
        super.initNode();
    }
}
