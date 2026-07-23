package cn.oyzh.easydameng.trees.event;

import cn.oyzh.fx.gui.svg.glyph.database.EventSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.paint.Color;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public class DamengEventsTreeItemValue extends RichTreeItemValue {

    public DamengEventsTreeItemValue(DamengEventsTreeItem item) {
        super(item);
    }

    @Override
    public DamengEventsTreeItem item() {
        return (DamengEventsTreeItem) super.item();
    }

    @Override
    public String name() {
        return I18nHelper.event();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new EventSVGGlyph());
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

    @Override
    public String extra() {
        Integer size = this.item().eventSize();
        if (size != null) {
            return " (" + size + ")";
        }
        return super.extra();
    }

    @Override
    public Color extraColor() {
        return Color.valueOf("#228B22");
    }
}
