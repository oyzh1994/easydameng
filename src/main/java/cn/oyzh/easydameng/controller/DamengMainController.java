package cn.oyzh.easydameng.controller;

import cn.oyzh.easydameng.controller.main.ConnectController;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.domain.DamengSetting;
import cn.oyzh.easydameng.event.connect.DamengConnectUpdatedEvent;
import cn.oyzh.easydameng.event.tree.DamengTreeItemChangedEvent;
import cn.oyzh.easydameng.store.DamengSettingStore;
import cn.oyzh.easydameng.tabs.DamengTabPane;
import cn.oyzh.easydameng.trees.DBTreeView;
import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.easydameng.trees.query.DamengQueryTreeItem;
import cn.oyzh.easydameng.trees.query.DamengQueriesTreeItem;
import cn.oyzh.easydameng.trees.table.DamengTableTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.plus.controller.ParentStageController;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.box.FXVBox;
import cn.oyzh.fx.plus.controls.pane.FXSplitPane;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.stage.WindowEvent;

import java.util.List;


/**
 * db主页
 *
 * @author oyzh
 * @since 2023/06/22
 */
public class DamengMainController extends ParentStageController {

    /**
     * 跟节点
     */
    @FXML
    private FXSplitPane root;

    /**
     * 左侧组件
     */
    @FXML
    private FXVBox connect;

    /**
     * db切换面板
     */
    @FXML
    public DamengTabPane tabPane;

    /**
     * redis连接
     */
    @FXML
    private ConnectController connectController;

    /**
     * 刷新窗口标题
     *
     * @param info db信息
     */
    private void flushViewTitle(DamengConnect info) {
        if (info != null) {
            this.stage.appendTitle(" (" + info.getName() + ")");
        } else {
            this.stage.restoreTitle();
        }
    }

    /**
     * 树节点变化事件
     *
     * @param item 节点
     */
    private void treeItemChanged(TreeItem<?> item) {
        if (item instanceof DamengTableTreeItem treeItem) {
            this.flushViewTitle(treeItem.info());
        } else if (item instanceof DamengQueryTreeItem treeItem) {
            this.flushViewTitle(treeItem.info());
        } else if (item instanceof DamengDatabaseTreeItem treeItem) {
            this.flushViewTitle(treeItem.info());
        } else if (item instanceof DBConnectTreeItem treeItem) {
            this.flushViewTitle(treeItem.value());
        } else {
            this.flushViewTitle(null);
        }
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        // 注册事件处理
//        EventUtil.register(this.tree);
//        EventUtil.register(this.tabPane);
//        EventUtil.register(this.msgArea);

        // 初始化过滤
        // this.tree.itemFilter(this.treeItemFilter);
        // this.treeItemFilter.initFilters();
//        this.filter();

//        // 设置上次保存的页面拉伸
//        if (this.setting.isRememberPageResize()) {
//            this.resizeMainLeft(this.pageInfo.getMainLeftWidth());
//        }
    }

//     @Override
//     public void onWindowHidden(WindowEvent event) {
//         super.onWindowHidden(event);
//         // 取消注册事件处理
// //        EventUtil.unregister(this.tree);
// //        EventUtil.unregister(this.tabPane);
// //        EventUtil.unregister(this.msgArea);
//         // 关闭连接
//         this.tree.closeConnects();
//         // 取消F5按键监听
//         KeyListener.unListenReleased(this.tree, KeyCode.F5);
//         KeyListener.unListenReleased(this.tabPane, KeyCode.F5);
//     }

    @Override
    protected void bindListeners() {
        // // 左侧栏业务
        // this.onlyCollect.selectedChanged((obs, o, n) -> {
        //     if (n) {
        //         this.showSet.disable();
        //         this.showZSet.disable();
        //         this.showHash.disable();
        //         this.showList.disable();
        //         this.showString.disable();
        //         this.showStream.disable();
        //     } else {
        //         this.showSet.enable();
        //         this.showZSet.enable();
        //         this.showHash.enable();
        //         this.showList.enable();
        //         this.showString.enable();
        //         this.showStream.enable();
        //     }
        //     this.filter();
        // });
        // this.showSet.selectedChanged((obs, o, n) -> this.filter());
        // this.showHash.selectedChanged((obs, o, n) -> this.filter());
        // this.showList.selectedChanged((obs, o, n) -> this.filter());
        // this.showZSet.selectedChanged((obs, o, n) -> this.filter());
        // this.showString.selectedChanged((obs, o, n) -> this.filter());
        // this.showStream.selectedChanged((obs, o, n) -> this.filter());
        // this.sortAsc.managedBindVisible();
        // this.sortDesc.managedBindVisible();
        // // redis树变化事件
        // this.tree.selectItemChanged(this::treeItemChanged);
        // // 文件拖拽初始化
        // this.stage.initDragFile(this.tree.getDragContent(), this.tree.getRoot()::dragFile);
        // 拖动改变redis树大小处理
        // NodeResizer resizeHelper = new NodeResizer(this.tabPaneLeft, Cursor.DEFAULT, this::resizeMainLeft);
        // resizeHelper.widthLimit(240f, 650f);
        // // // 初始化拉伸事件
        // // this.tree.setOnMouseMoved(resizeHelper.mouseMoved());
        // resizeHelper.initResizeEvent();

        // 搜索触发事件
        // KeyListener.listenReleased(this.stage, new KeyHandler().keyCode(KeyCode.F).controlDown(true).handler(t1 -> RedisEventUtil.searchFire()));
        // // 刷新触发事件
        // KeyListener.listenReleased(this.tree, KeyCode.F5, keyEvent -> this.tree.reload());
        // // 刷新触发事件
        // KeyListener.listenReleased(this.tabPane, KeyCode.F5, keyEvent -> this.tabPane.reload());
    }

    /**
     * 树节点变化事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void treeItemChanged(DamengTreeItemChangedEvent event) {
        if (event.data() instanceof DBConnectTreeItem treeItem) {
            this.flushViewTitle(treeItem.value());
        } else if (event.data() instanceof DamengDatabaseTreeItem treeItem) {
            this.flushViewTitle(treeItem.dbConnect());
        } else if (event.data() instanceof DamengQueryTreeItem treeItem) {
            this.flushViewTitle(treeItem.dbConnect());
        } else if (event.data() instanceof DamengQueriesTreeItem treeItem) {
            this.flushViewTitle(treeItem.dbConnect());
        } else {
            this.flushViewTitle(null);
        }
    }

    /**
     * 布局2
     */
    @EventSubscribe
    private void layout2(Layout2Event event) {
        this.connect.display();
        //        double w = this.connect.getRealWidth();
        //        this.tabPane.setLayoutX(w);
        //        this.tabPane.setFlexWidth("100% - " + w);
        //        this.connect.parentAutosize();
        if (!this.root.isShowDivider()) {
            this.root.setShowDivider(true);
            double positions_0 = this.root.getPosition0(0.25);
            this.root.setDividerPositions(positions_0, 1 - positions_0);
        }
    }

    /**
     * 布局1
     */
    @EventSubscribe
    private void layout1(Layout1Event event) {
        this.connect.disappear();
        //        this.tabPane.setLayoutX(0);
        //        this.tabPane.setFlexWidth("100%");
        //        this.connect.parentAutosize();
        if (this.root.isShowDivider()) {
            this.root.recordPosition0();
            this.root.setShowDivider(false);
            this.root.setDividerPositions(0, 1);
        }
    }

    @Override
    public List<SubStageController> getSubControllers() {
        return List.of(this.connectController);
    }
}
