package cn.oyzh.easydameng.trees.terminal;

import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class DamengTerminalTreeItem extends RichTreeItem<DamengTerminalTreeItemValue> {

    public DamengTerminalTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new DamengTerminalTreeItemValue());
    }

    public DamengSchemaTreeItem parent() {
        return (DamengSchemaTreeItem) super.parent();
    }

    public DamengConnect shellConnect() {
        return this.parent().info();
    }

    public ShellDamengClient client() {
        return this.parent().client();
    }

    @Override
    public void onPrimaryDoubleClick() {
        DamengEventUtil.terminalOpen(this.client(), this.parent().schema());
    }

}
