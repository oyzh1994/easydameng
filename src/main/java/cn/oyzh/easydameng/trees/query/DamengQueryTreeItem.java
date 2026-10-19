package cn.oyzh.easydameng.trees.query;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.domain.DamengQuery;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.store.DamengQueryStore;
import cn.oyzh.easydameng.trees.DBTreeItem;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 数据库树查询节点
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class DamengQueryTreeItem extends DBTreeItem<DamengQueryTreeItemValue> {

    /**
     * 当前值
     */
    private final DamengQuery value;

    /**
     * 获取当前查询对象
     *
     * @return 当前查询对象
     */
    public DamengQuery value() {
        return value;
    }

    /**
     * 构造查询树节点
     *
     * @param query    查询对象
     * @param treeView 所属树视图
     */
    public DamengQueryTreeItem(DamengQuery query, RichTreeView treeView) {
        super(treeView);
        super.setFilterable(true);
        this.value = query;
        this.setValue(new DamengQueryTreeItemValue(this));
    }

    @Override
    public DamengQueriesTreeItem parent() {
        return (DamengQueriesTreeItem) super.parent();
    }

    /**
     * 获取数据库客户端
     *
     * @return 数据库客户端
     */
    public ShellDamengClient client() {
        return this.parent().client();
    }

    /**
     * 获取连接信息
     *
     * @return 连接信息
     */
    public DamengConnect info() {
        return this.parent().info();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem openQuery = MenuItemHelper.openQuery( this::onPrimaryDoubleClick);
        items.add(openQuery);
        FXMenuItem renameQuery = MenuItemHelper.renameQuery( this::rename);
        items.add(renameQuery);
        FXMenuItem deleteQuery = MenuItemHelper.deleteTable( this::delete);
        items.add(deleteQuery);
        return items;
    }

    @Override
    public void delete() {
        if (MessageBox.confirm(I18nHelper.delete() + " " + this.queryName() + "?", this.getWindow())) {
            if (DamengQueryStore.INSTANCE.delete(this.value)) {
                this.remove();
                DamengEventUtil.queryDeleted(this);
            } else {
                MessageBox.warn(I18nHelper.operationFail(), this.getWindow());
            }
        }
    }

    @Override
    public void rename() {
        String name = MessageBox.prompt(I18nHelper.pleaseInputName(), this.queryName(), this.getWindow());
        // 名称为null或者跟当前名称相同，则忽略
        if (name == null || Objects.equals(name, this.queryName())) {
            return;
        }
        // 检查名称
        if (StringUtil.isBlank(name)) {
            MessageBox.warn(I18nHelper.pleaseInputName(), this.getWindow());
            return;
        }
        String oldName = this.value.getName();
        this.value.setName(name);
        // 修改名称
        if (DamengQueryStore.INSTANCE.update(this.value)) {
            DamengEventUtil.queryRenamed(this.value, this.dbItem());
            this.refresh();
        } else {
            this.value.setName(oldName);
            MessageBox.warn(I18nHelper.operationFail(), this.getWindow());
        }
    }

    /**
     * 获取所属模式树节点
     *
     * @return 模式树节点
     */
    public DamengSchemaTreeItem dbItem() {
        return this.parent().parent();
    }

    /**
     * 获取所属模式名称
     *
     * @return 模式名称
     */
    public String schema() {
        return this.parent().schema();
    }

    /**
     * 获取查询名称
     *
     * @return 查询名称
     */
    public String queryName() {
        return this.value.getName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        DamengEventUtil.queryOpen(this.value, this.dbItem());
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     */
    public DamengConnect dbConnect() {
        return this.client().getDbConnect();
    }
}
