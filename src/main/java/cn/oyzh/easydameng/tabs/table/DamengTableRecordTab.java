package cn.oyzh.easydameng.tabs.table;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.dameng.record.DamengRecordFilter;
import cn.oyzh.easydameng.tabs.DamengTab;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.easydameng.trees.table.DamengTableTreeItem;
import cn.oyzh.fx.gui.svg.glyph.database.TableSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.Cursor;

import java.util.List;

/**
 * 达梦表数据标签
 *
 * @author oyzh
 * @since 2023/12/24
 */
public class DamengTableRecordTab extends DamengTab {

    // /**
    //  * 标签打开时间
    //  */
    // private final long openedTime = System.currentTimeMillis();

    // private DamengTableTreeItem item;

    @Override
    protected String url() {
        return super.getBasePath() + "table/damengTableRecordTab.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new TableSVGGlyph();
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    public void flushTitle() {
        // 设置提示文本
        this.setText(this.item().tableName() + "@" + this.item().schema() + "(" + this.item().infoName() + ")");
    }

    /**
     * 初始化
     *
     * @param item 树键
     */
    public boolean init(DamengTableTreeItem item) {
        // this.item = item;
        this.controller().init(item);
        // 刷新tab
        this.flush();
        // 加载耗时处理
        return true;
    }

    @Override
    public DamengTableRecordTabController controller() {
        return (DamengTableRecordTabController) super.controller();
    }

    @Override
    public void reload() {
        this.controller().reload();
    }

    /**
     * 获取达梦客户端
     *
     * @return 达梦客户端
     */
    public ShellDamengClient client() {
        return this.item().client();
    }

    /**
     * 设置过滤条件
     *
     * @param filters 过滤条件列表
     */
    public void setFilters(List<DamengRecordFilter> filters) {
        this.controller().setFilters(filters);
    }

    /**
     * 获取表树节点
     *
     * @return 表树节点
     */
    public DamengTableTreeItem item(){
        return this.controller().getItem();
    }

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String tableName() {
        return this.item().tableName();
    }

    @Override
    public DamengSchemaTreeItem dbItem() {
        return this.item().dbItem();
    }

    /**
     * 获取模式
     *
     * @return 模式
     */
    public String schema() {
        return this.item().schema();
    }
}
