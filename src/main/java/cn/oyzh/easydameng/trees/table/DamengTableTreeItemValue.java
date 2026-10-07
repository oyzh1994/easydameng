package cn.oyzh.easydameng.trees.table;

import cn.oyzh.fx.gui.svg.glyph.database.TableSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * 数据库树表节点值
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class DamengTableTreeItemValue extends RichTreeItemValue {

    /**
     * 构造表节点值
     *
     * @param item 所属表树节点
     */
    public DamengTableTreeItemValue(DamengTableTreeItem item) {
        super(item);
    }

    @Override
    public DamengTableTreeItem item() {
        return (DamengTableTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new TableSVGGlyph());
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return this.item().tableName();
    }
}
