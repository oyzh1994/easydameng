package cn.oyzh.easydameng.dameng.dto;


import cn.oyzh.easydameng.dameng.column.DamengColumn;

/**
 * 达梦数据导出字段
 *
 * @author oyzh
 * @since 2024/8/27
 */
public class ShellDamengDataExportColumn extends DamengColumn {

    /**
     * 是否选中
     */
    private boolean selected = true;

    /**
     * 是否选中
     *
     * @return 结果
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 设置是否选中
     *
     * @param selected 是否选中
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
