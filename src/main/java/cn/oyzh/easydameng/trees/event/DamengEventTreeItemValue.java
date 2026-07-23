package cn.oyzh.easydameng.trees.event;

import cn.oyzh.fx.gui.svg.glyph.database.EventSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public class DamengEventTreeItemValue extends RichTreeItemValue {

    public DamengEventTreeItemValue(DamengEventTreeItem item) {
        super(item);
    }

    @Override
    public DamengEventTreeItem item() {
        return (DamengEventTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new EventSVGGlyph());
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().eventName();
    }
}
