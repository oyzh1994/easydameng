package cn.oyzh.easydameng.trees.view;

import cn.oyzh.fx.gui.svg.glyph.database.ViewSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * 数据库树视图节点值
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DamengViewTreeItemValue extends RichTreeItemValue {

    /**
     * 构造视图节点值
     *
     * @param item 所属视图树节点
     */
    public DamengViewTreeItemValue(DamengViewTreeItem item) {
        super(item);
    }

    @Override
    public DamengViewTreeItem item() {
        return (DamengViewTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new ViewSVGGlyph());
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().viewName();
    }
}
