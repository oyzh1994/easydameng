package cn.oyzh.easydameng.trees.query;

import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库树查询分类节点
 *
 * @author oyzh
 * @since 2024/01/31
 */
public class DamengQueriesTreeItem extends DBTreeItem<DamengQueriesTreeItemValue> {

    /**
     * 构造查询分类树节点
     *
     * @param treeView 所属树视图
     */
    public DamengQueriesTreeItem(RichTreeView treeView) {
        super(treeView);
        super.setFilterable(true);
        this.setValue(new DamengQueriesTreeItemValue(this));
    }

    @Override
    public DamengSchemaTreeItem parent() {
        return (DamengSchemaTreeItem) super.parent();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem addQuery = MenuItemHelper.addQuery( this::addQuery);
        items.add(addQuery);
        FXMenuItem reload = MenuItemHelper.refreshData( this::reloadChild);
        items.add(reload);
        return items;
    }

    /**
     * 新增查询
     */
    private void addQuery() {
        DamengEventUtil.queryAdd(this.parent());
    }

    @Override
    public boolean itemVisible() {
        return this.isVisible();
    }

    @Override
    public void loadChild() {
        if (!this.isLoading() && !this.isLoaded()) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> {
                        this.setLoaded(true);
                        this.setLoading(true);
                        List<DamengQuery> dbQueries = DamengQueryStore.INSTANCE.list(this.info().getId(), this.schema());
                        List<TreeItem<?>> list = new ArrayList<>();
                        for (DamengQuery query : dbQueries) {
                            list.add(new DamengQueryTreeItem(query, this.getTreeView()));
                        }
                        this.setChild(list);
                    })
                    .onFinish(() -> this.setLoading(false))
                    .onSuccess(this::expend)
                    .onError(ex -> {
                        this.setLoaded(false);
                        MessageBox.exception(ex);
                    })
                    .build();
            this.startWaiting(task);
        }
    }

    @Override
    public void reloadChild() {
        this.clearChild();
        this.setLoaded(false);
        this.loadChild();
    }

    /**
     * 添加查询子节点
     *
     * @param query 查询对象
     */
    public void addChild(DamengQuery query) {
        this.addChild(new DamengQueryTreeItem(query, this.getTreeView()));
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
    public void onPrimaryDoubleClick() {
        if (!this.isLoaded()) {
            this.loadChild();
        } else {
            super.onPrimaryDoubleClick();
        }
    }

    //@Override
    //public synchronized void doFilter(RichTreeItemFilter itemFilter) {
    //    super.doFilter(itemFilter);
    //    this.refresh();
    //}

    /**
     * 获取查询数量
     *
     * @return 查询数量
     */
    public Integer querySize() {
        List<DamengQuery> dbQueries = DamengQueryStore.INSTANCE.list(this.info().getId(), this.schema());
        return dbQueries == null ? 0 : dbQueries.size();
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     */
    public DamengConnect dbConnect() {
        return this.parent().dbConnect();
    }

    /**
     * 添加查询子节点
     *
     * @param query 查询对象
     */
    public void addQuery(DamengQuery query) {
        this.addChild(new DamengQueryTreeItem(query, this.getTreeView()));
        this.sortChild(this.isSortAsc());
    }
}
