package cn.oyzh.easydameng.trees.group;

import cn.oyzh.fx.gui.svg.glyph.GroupSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;


/**
 * 数据库树分组节点值
 *
 * @author oyzh
 * @since 2023/12/21
 */
public class DBGroupTreeItemValue extends RichTreeItemValue {

    /**
     * 构造分组节点值
     *
     * @param item 所属分组树节点
     */
    public DBGroupTreeItemValue(DBGroupTreeItem item) {
        super(item);
    }

    @Override
    public DBGroupTreeItem item() {
        return (DBGroupTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().value().getName();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new GroupSVGGlyph());
            super.graphic().disableTheme();
        }
        return super.graphic();
    }

    @Override
    public Color graphicColor() {
        if (!this.item().isChildEmpty()) {
           return Color.DEEPSKYBLUE;
        }
        return super.graphicColor();
    }
}
