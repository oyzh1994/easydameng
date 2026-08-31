package cn.oyzh.easydameng.tabs.table;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.dameng.record.DamengRecordFilter;
import cn.oyzh.easydameng.tabs.DamengTab;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.easydameng.trees.table.DamengTableTreeItem;
import cn.oyzh.fx.gui.svg.glyph.database.TableSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.Cursor;

import java.util.List;

/**
 * db表tab
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
        this.setText(this.item().tableName() + "@" + this.item().dbName() + "(" + this.item().infoName() + ")");
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

    public ShellDamengClient client() {
        return this.item().client();
    }

    public void setFilters(List<DamengRecordFilter> filters) {
        this.controller().setFilters(filters);
    }

    public DamengTableTreeItem item(){
        return this.controller().getItem();
    }
    
    public String tableName() {
        return this.item().tableName();
    }

    @Override
    public DamengDatabaseTreeItem dbItem() {
        return this.item().dbItem();
    }

    public String dbName() {
        return this.item().dbName();
    }

    // @Override
    // protected void onTabClosed(Event event) {
    //     super.onTabClosed(event);
    //     System.out.println("------1");
    // }
}
