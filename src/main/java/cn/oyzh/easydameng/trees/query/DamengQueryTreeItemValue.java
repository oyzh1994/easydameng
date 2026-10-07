package cn.oyzh.easydameng.trees.query;

import cn.oyzh.fx.gui.svg.glyph.QuerySVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * 数据库树查询节点值
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DamengQueryTreeItemValue extends RichTreeItemValue {

    /**
     * 构造查询节点值
     *
     * @param item 所属查询树节点
     */
    public DamengQueryTreeItemValue(DamengQueryTreeItem item) {
        super(item);
    }

    @Override
    public DamengQueryTreeItem item() {
        return (DamengQueryTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new QuerySVGGlyph());
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().queryName();
    }
}
