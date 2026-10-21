package cn.oyzh.easydameng.trees.table;

import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.easydameng.controller.data.ShellDamengDataExportController;
import cn.oyzh.easydameng.controller.data.ShellDamengDataImportController;
import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.trees.DBTreeItem;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库树表分类节点
 *
 * @author oyzh
 * @since 2023/12/08
 */
public class DamengTablesTreeItem extends DBTreeItem<DamengTablesTreeItemValue> {

    /**
     * 构造表分类树节点
     *
     * @param treeView 所属树视图
     */
    public DamengTablesTreeItem(RichTreeView treeView) {
        super(treeView);
        super.setFilterable(true);
        this.setValue(new DamengTablesTreeItemValue(this));
    }

    @Override
    public DamengSchemaTreeItem parent() {
        return (DamengSchemaTreeItem) super.parent();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem reload = MenuItemHelper.reloadData( this::reloadChild);
        FXMenuItem add = MenuItemHelper.addTable( this::addTable);
        FXMenuItem exportData = MenuItemHelper.exportData( this::exportData);
        FXMenuItem importData = MenuItemHelper.importData( this::importData);
        items.add(add);
        items.add(reload);
        items.add(exportData);
        items.add(importData);
        return items;
    }

    /**
     * 导出数据
     */
    private void exportData() {
        StageAdapter fxView = StageManager.parseStage(ShellDamengDataExportController.class, this.getWindow());
        fxView.setProp("dumpType", 2);
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.schema());
        fxView.setProp("dbClient", this.client());
        fxView.display();
    }

    /**
     * 导入数据
     */
    private void importData() {
        StageAdapter fxView = StageManager.parseStage(ShellDamengDataImportController.class, this.getWindow());
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.schema());
        fxView.setProp("dbClient", this.client());
        fxView.display();
    }

    /**
     * 新增表
     */
    private void addTable() {
        DamengTable table = new DamengTable();
        table.setSchema(this.schema());
        DamengEventUtil.designTable(table, this.parent());
    }

    @Override
    public boolean itemVisible() {
        return this.isVisible();
    }

    @Override
    public void loadChild() {
        if (!this.isLoading() && !this.isLoaded()) {
            this.setLoaded(true);
            this.setLoading(true);
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> {
                        List<DamengTable> tables = this.client().selectTablesSimple(this.schema());
                        // 无数据直接更新列表
                        if (this.isChildEmpty()) {
                            List<TreeItem<?>> list = new ArrayList<>();
                            for (DamengTable table : tables) {
                                list.add(new DamengTableTreeItem(table, this.getTreeView()));
                            }
                            this.setChild(list);
                        } else {// 有数据则执行删除、新增、更新操作
                            ObservableList children = this.richChildren();
                            ObservableList<DamengTableTreeItem> list = children;
                            List<DamengTableTreeItem> delList = new ArrayList<>();
                            List<DamengTableTreeItem> addList = new ArrayList<>();
                            // 删除
                            for (DamengTableTreeItem item : list) {
                                if (tables.parallelStream().noneMatch(f -> f.compare(item.value()))) {
                                    delList.add(item);
                                }
                            }
                            // 新增
                            for (DamengTable table : tables) {
                                if (list.parallelStream().noneMatch(item -> table.compare(item.value()))) {
                                    addList.add(new DamengTableTreeItem(table, this.getTreeView()));
                                }
                            }
                            // 更新
                            for (DamengTableTreeItem item : list) {
                                if (!addList.contains(item) && !delList.contains(item)) {
                                    tables.parallelStream().filter(f -> f.compare(item.value())).findFirst().ifPresent(f -> item.value().copy(f));
                                }
                            }
                            list.removeAll(delList);
                            list.addAll(addList);
                        }
                        this.expend();
                    })
                    .onError(ex -> {
                        this.setLoaded(false);
                        MessageBox.exception(ex, this.getWindow());
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
     * 获取表数量
     *
     * @return 表数量
     */
    public Integer tableSize() {
        return this.parent().tableSize();
    }

    /**
     * 获取连接信息
     *
     * @return 连接信息
     */
    public DamengConnect info() {
        return this.parent().info();
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
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

    /**
     * 添加表子节点
     *
     * @param table 表对象
     */
    public void addTable(DamengTable table) {
        this.addChild(new DamengTableTreeItem(table, this.getTreeView()));
        this.sortChild(this.isSortAsc());
    }
}
