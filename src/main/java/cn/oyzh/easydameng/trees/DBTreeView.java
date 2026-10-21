package cn.oyzh.easydameng.trees;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.easydameng.controller.connect.DamengConnectAddController;
import cn.oyzh.easydameng.event.connect.DBAddConnectEvent;
import cn.oyzh.easydameng.event.connect.DamengConnectAddedEvent;
import cn.oyzh.easydameng.event.connect.DamengConnectUpdatedEvent;
import cn.oyzh.easydameng.event.group.DBAddGroupEvent;
import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;
import cn.oyzh.easydameng.trees.group.DBGroupTreeItem;
import cn.oyzh.easydameng.trees.root.DBRootTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.tree.view.RichTreeCell;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.event.FXEventListener;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

/**
 * 数据库树视图组件
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class DBTreeView extends RichTreeView implements FXEventListener {

    /**
     * 搜索中标志位
     */
    private volatile boolean searching;

    @Override
    public DBTreeItemFilter getItemFilter() {
        // 初始化过滤器
        if (this.itemFilter == null) {
            this.itemFilter = new DBTreeItemFilter();
        }
        return (DBTreeItemFilter) this.itemFilter;
    }

    /**
     * 构造数据库树视图
     */
    public DBTreeView() {
        this.dragContent = "db_tree_drag";
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new RichTreeCell<>());
        super.setRoot(new DBRootTreeItem(this));
        this.root().expend();
    }

    @Override
    public DBRootTreeItem root() {
        return (DBRootTreeItem) super.root();
    }

    /**
     * 关闭连接
     */
    public void closeConnects() {
        for (DBConnectTreeItem treeItem : this.root().getConnectedItems()) {
            ThreadUtil.start(treeItem::closeConnect);
        }
    }

//    /**
//     * 搜索开始事件
//     *
//     * @param event 事件
//     */
//    @EventSubscribe
//    private void onSearchStart(DBSearchStartEvent event) {
//        this.searching = true;
//        this.filter();
//    }
//
//    /**
//     * 搜索结束事件
//     *
//     * @param event 事件
//     */
//    @EventSubscribe
//    private void onSearchFinish(DBSearchFinishEvent event) {
//        this.searching = false;
//        this.filter();
//    }

//    /**
//     * 树节点过滤事件
//     *
//     * @param event 事件
//     */
//    @EventSubscribe
//    private void onTreeChildFilter(TreeChildFilterEvent event) {
//        this.filter();
//    }

    /**
     * 连接修改事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void onInfoUpdate(DamengConnectUpdatedEvent event) {
        f1:
        for (TreeItem<?> item : this.root().unfilteredChildren()) {
            if (item instanceof DBConnectTreeItem connectTreeItem) {
                if (connectTreeItem.value() == event.data()) {
                    connectTreeItem.value(event.data());
                    break;
                }
            } else if (item instanceof DBGroupTreeItem groupTreeItem) {
                for (DBConnectTreeItem connectTreeItem : groupTreeItem.getConnectItems()) {
                    if (connectTreeItem.value() == event.data()) {
                        connectTreeItem.value(event.data());
                        break f1;
                    }
                }
            }
        }
    }

    /**
     * 添加连接事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void addConnect(DBAddConnectEvent event) {
        StageManager.showStage(DamengConnectAddController.class, this.getWindow());
    }

    /**
     * 添加分组事件
     *
     * @param event 事件
     */
    @EventSubscribe
    public void addGroup(DBAddGroupEvent event) {
        this.root().addGroup();
    }

    /**
     * 连接新增事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void infoAdded(DamengConnectAddedEvent event) {
        this.root().addConnect(event.data());
    }

    /**
     * 连接变更事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void infoUpdated(DamengConnectUpdatedEvent event) {
        this.root().infoUpdate(event.data());
    }
}
