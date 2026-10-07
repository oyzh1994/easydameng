package cn.oyzh.easydameng.trees.procedure;

import cn.oyzh.fx.gui.svg.glyph.database.ProcedureSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * 数据库树存储过程节点值
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DamengProcedureTreeItemValue extends RichTreeItemValue {

    /**
     * 构造存储过程节点值
     *
     * @param item 所属存储过程树节点
     */
    public DamengProcedureTreeItemValue(DamengProcedureTreeItem item) {
        super(item);
    }

    @Override
    public DamengProcedureTreeItem item() {
        return (DamengProcedureTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new ProcedureSVGGlyph());
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().procedureName();
    }
}
