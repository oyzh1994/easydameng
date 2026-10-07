package cn.oyzh.easydameng.tabs.view;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.dameng.record.DamengRecordFilter;
import cn.oyzh.easydameng.tabs.DamengTab;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.easydameng.trees.view.DamengViewTreeItem;
import cn.oyzh.fx.gui.svg.glyph.database.ViewSVGGlyph;
import javafx.scene.Cursor;

import java.util.List;

/**
 * 达梦视图数据标签
 *
 * @author oyzh
 * @since 2023/12/24
 */
public class DamengViewRecordTab extends DamengTab {

    /**
     * 标签打开时间
     */
    private final long openedTime = System.currentTimeMillis();

    @Override
    protected String url() {
        return super.getBasePath() + "view/damengViewRecordTab.fxml";
    }

    @Override
    public void flushGraphic() {
        ViewSVGGlyph graphic = (ViewSVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new ViewSVGGlyph();
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    public void flushTitle() {
        this.setText(this.item().viewName() + "@" + this.item().schema() + "(" + this.item().infoName() + ")");
    }

    /**
     * 初始化
     *
     * @param item 树键
     */
    public boolean init(DamengViewTreeItem item) {
        this.controller().init(item);
        this.flush();
        return true;
    }

    @Override
    public DamengViewRecordTabController controller() {
        return (DamengViewRecordTabController) super.controller();
    }

    @Override
    public void reload() {
        this.controller().reload();
    }

    /**
     * 获取视图树节点
     *
     * @return 视图树节点
     */
    public DamengViewTreeItem item() {
        return this.controller().getItem();
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
     * 获取视图名称
     *
     * @return 视图名称
     */
    public String viewName() {
        return this.item().viewName();
    }

    @Override
    public DamengSchemaTreeItem dbItem() {
        return this.item().dbItem();
    }

    /**
     * 设置过滤条件
     *
     * @param filters 过滤条件列表
     */
    public void setFilters(List<DamengRecordFilter> filters) {
        this.controller().setFilters(filters);
    }
}
