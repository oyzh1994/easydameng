package cn.oyzh.easydameng.trees.terminal;

import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;

/**
 * 数据库终端树节点
 *
 * @author oyzh
 * @since 2023/1/30
 */
public class DamengTerminalTreeItem extends RichTreeItem<DamengTerminalTreeItemValue> {

    /**
     * 构造终端树节点
     *
     * @param treeView 所属树视图
     */
    public DamengTerminalTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new DamengTerminalTreeItemValue());
    }

    /**
     * 获取父节点
     *
     * @return 父节点
     */
    public DamengSchemaTreeItem parent() {
        return (DamengSchemaTreeItem) super.parent();
    }

    /**
     * 获取终端所属连接信息
     *
     * @return 连接信息
     */
    public DamengConnect shellConnect() {
        return this.parent().info();
    }

    /**
     * 获取数据库客户端
     *
     * @return 数据库客户端
     */
    public ShellDamengClient client() {
        return this.parent().client();
    }

    @Override
    public void onPrimaryDoubleClick() {
        DamengEventUtil.terminalOpen(this.client(), this.parent().schema());
    }

}
