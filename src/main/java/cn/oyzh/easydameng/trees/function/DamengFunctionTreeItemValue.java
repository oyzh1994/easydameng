package cn.oyzh.easydameng.trees.function;

import cn.oyzh.fx.gui.svg.glyph.database.FunctionSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * 函数树节点值
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DamengFunctionTreeItemValue extends RichTreeItemValue {

    /**
     * 构造函数树节点值
     *
     * @param item 所属的函数树节点
     */
    public DamengFunctionTreeItemValue(DamengFunctionTreeItem item) {
        super(item);
    }

    @Override
    public DamengFunctionTreeItem item() {
        return (DamengFunctionTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new FunctionSVGGlyph());
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().functionName();
    }
}
