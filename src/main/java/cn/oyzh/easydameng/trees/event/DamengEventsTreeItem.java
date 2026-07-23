package cn.oyzh.easydameng.trees.event;

import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.easydameng.dameng.DamengClient;
import cn.oyzh.easydameng.dameng.event.DamengEvent;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.trees.DBTreeItem;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItemFilter;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public class DamengEventsTreeItem extends DBTreeItem<DamengEventsTreeItemValue> {

    public DamengEventsTreeItem(RichTreeView treeView) {
        super(treeView);
        super.setFilterable(true);
        this.setValue(new DamengEventsTreeItemValue(this));
    }

    @Override
    public DamengDatabaseTreeItem parent() {
        return (DamengDatabaseTreeItem) super.parent();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem add = MenuItemHelper.addEvent( this::add);
        items.add(add);
        FXMenuItem reload = MenuItemHelper.refreshData( this::reloadChild);
        items.add(reload);
        return items;
    }

    private void add() {
        DamengEvent event = new DamengEvent();
        event.setDbName(this.dbName());
        DamengEventUtil.designEvent(event, this.parent());
    }

    @Override
    public boolean itemVisible() {
        return this.isVisible();
    }

    @Override
    public void loadChild() {
        if (!this.isWaiting() && !this.isLoaded() && !this.isLoading()) {
            this.setLoaded(true);
            this.setLoading(true);
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> {
                        List<DamengEvent> events = this.client().events(this.dbName());
                        // 无数据直接更新列表
                        if (this.isChildEmpty()) {
                            List<TreeItem<?>> list = new ArrayList<>();
                            for (DamengEvent event : events) {
                                list.add(new DamengEventTreeItem(event, this.getTreeView()));
                            }
                            this.setChild(list);
                        } else {// 有数据则执行删除、新增、更新操作
                            ObservableList<DamengEventTreeItem> list = (ObservableList) this.richChildren();
                            List<DamengEventTreeItem> delList = new ArrayList<>();
                            List<DamengEventTreeItem> addList = new ArrayList<>();
                            // 删除
                            for (DamengEventTreeItem item : list) {
                                if (events.parallelStream().noneMatch(f -> f.compare(item.value()))) {
                                    delList.add(item);
                                }
                            }
                            // 新增
                            for (DamengEvent f : events) {
                                if (list.parallelStream().noneMatch(item -> f.compare(item.value()))) {
                                    addList.add(new DamengEventTreeItem(f, this.getTreeView()));
                                }
                            }
                            // 更新
                            for (DamengEventTreeItem item : list) {
                                if (!addList.contains(item) && !delList.contains(item)) {
                                    events.parallelStream().filter(f -> f.compare(item.value())).findFirst().ifPresent(f -> item.value().copy(f));
                                }
                            }
                            list.removeAll(delList);
                            list.addAll(addList);
                        }
                        this.expend();
                    })
                    .onError(ex -> {
                        this.setLoaded(false);
                        MessageBox.exception(ex);
                    })
                    .onSuccess(this::refresh)
                    .onFinish(() -> this.setLoading(false))
                    .build();
            // 执行业务
            this.startWaiting(task);
        }
    }

    @Override
    public void reloadChild() {
        this.clearChild();
        this.setLoaded(false);
        this.loadChild();
    }

    public String dbName() {
        return this.parent().dbName();
    }

    public DamengClient client() {
        return this.parent().client();
    }

    public DamengConnect info() {
        return this.parent().info();
    }

    public String infoName() {
        return this.parent().infoName();
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

    public Integer eventSize() {
        return this.client().eventSize(this.dbName());
    }

    public void addEvent(DamengEvent event) {
        this.addChild(new DamengEventTreeItem(event, this.getTreeView()));
        this.sortChild(this.isSortAsc());
    }
}
