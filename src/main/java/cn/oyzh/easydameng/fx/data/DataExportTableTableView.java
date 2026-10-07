package cn.oyzh.easydameng.fx.data;

import cn.oyzh.easydameng.dameng.dto.ShellDamengDataExportTable;
import cn.oyzh.fx.plus.controls.table.FXTableView;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据导出表表格视图
 *
 * @author oyzh
 * @since 2024/08/27
 */
public class DataExportTableTableView extends FXTableView<ShellDamengDataExportTable> {

    /**
     * 获取选中的导出表
     *
     * @return 选中的导出表列表
     */
    public List<ShellDamengDataExportTable> getSelectedTables() {
        List<ShellDamengDataExportTable> exportTables = new ArrayList<>();
        for (ShellDamengDataExportTable item : this.getItems()) {
            if (item.isSelected()) {
                exportTables.add(item);
            }
        }
        return exportTables;
    }

    /**
     * 是否存在选中的表
     *
     * @return 是否存在选中的表
     */
    public boolean hasSelectedTable() {
        for (ShellDamengDataExportTable item : this.getItems()) {
            if (item.isSelected()) {
                return true;
            }
        }
        return false;
    }
}
