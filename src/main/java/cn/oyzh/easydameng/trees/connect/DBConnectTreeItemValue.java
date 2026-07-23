package cn.oyzh.easydameng.trees.connect;

import cn.oyzh.fx.gui.svg.glyph.database.DamengSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;


/**
 * db树连接值
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DBConnectTreeItemValue extends RichTreeItemValue {

    public DBConnectTreeItemValue(DBConnectTreeItem item) {
        super(item);
    }

    @Override
    public DBConnectTreeItem item() {
        return (DBConnectTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().value().getName();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new DamengSVGGlyph());
            super.graphic().disableTheme();
        }
        return super.graphic();
    }

    @Override
    public Color graphicColor() {
        if (!this.item().isChildEmpty()) {
            return Color.GREEN;
        }
        return super.graphicColor();
    }
}
