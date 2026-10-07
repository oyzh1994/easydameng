package cn.oyzh.easydameng.trees.schema;

import cn.oyzh.fx.gui.svg.glyph.database.DatabaseSVGGlyph;
import cn.oyzh.fx.gui.svg.glyph.database.SchemaSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;

/**
 * 数据库模式节点值
 *
 * @author oyzh
 * @since 2023/12/20
 */
public class DamengSchemaTreeItemValue extends RichTreeItemValue {

    /**
     * 构造模式节点值
     *
     * @param item 所属模式树节点
     */
    public DamengSchemaTreeItemValue(DamengSchemaTreeItem item) {
        super(item);
    }

    @Override
    public DamengSchemaTreeItem item() {
        return (DamengSchemaTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().schema();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new SchemaSVGGlyph());
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
