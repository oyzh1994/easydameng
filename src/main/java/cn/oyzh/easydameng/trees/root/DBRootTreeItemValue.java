package cn.oyzh.easydameng.trees.root;

import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;


/**
 * redis 根节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class DBRootTreeItemValue extends RichTreeItemValue {

//    public DBRootTreeItemValue() {
//        this.flushGraphic();
//        this.flushText();
//    }

    @Override
    public String name() {
        return I18nHelper.database();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new SVGGlyph("/font/database.svg"));
//            super.graphic().glyph);
        }
        return super.graphic();
    }
}
