package cn.oyzh.easydameng.fx.data;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.dto.ShellDamengDataExportColumn;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.util.ListViewUtil;

import java.util.List;

/**
 * 数据导出字段列表视图
 *
 * @author oyzh
 * @since 2024/08/27
 */
public class DataExportColumnListView extends FXListView<FXCheckBox> {

    /**
     * 初始化导出字段列表
     *
     * @param columns 导出字段列表
     */
    public void init(List<ShellDamengDataExportColumn> columns) {
        this.clearItems();
        if (CollectionUtil.isNotEmpty(columns)) {
            for (ShellDamengDataExportColumn column : columns) {
                FXCheckBox checkBox = new FXCheckBox();
                checkBox.setSelected(column.isSelected());
                checkBox.setText(column.getName());
                checkBox.selectedChanged((observable, oldValue, newValue) -> column.setSelected(newValue));
                ListViewUtil.selectRowOnMouseClicked(checkBox);
                this.addItem(checkBox);
            }
        }
    }
}
