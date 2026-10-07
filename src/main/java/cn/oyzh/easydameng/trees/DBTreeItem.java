package cn.oyzh.easydameng.trees;


import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.gui.tree.view.RichTreeView;

/**
 * 基础的树节点
 *
 * @author oyzh
 * @since 2023/06/27
 */
public abstract class DBTreeItem<V extends RichTreeItemValue> extends RichTreeItem<V> {

    /**
     * 构造基础树节点
     *
     * @param treeView 所属树视图
     */
    public DBTreeItem(RichTreeView treeView) {
        super(treeView);
    }

    @Override
    public DBTreeView getTreeView() {
        return (DBTreeView) super.getTreeView();
    }
}
