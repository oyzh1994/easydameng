//package cn.oyzh.easydameng.controller;
//
//import cn.oyzh.common.dto.Project;
//import cn.oyzh.easydameng.controller.data.DamengDataTransportController;
//import cn.oyzh.easydameng.event.DBEventUtil;
//import cn.oyzh.fx.plus.controller.SubStageController;
//import cn.oyzh.fx.plus.controls.svg.SVGLabel;
//import cn.oyzh.fx.plus.information.MessageBox;
//import cn.oyzh.fx.plus.node.NodeMutexes;
//import cn.oyzh.fx.plus.window.StageAdapter;
//import cn.oyzh.fx.plus.window.StageManager;
//import javafx.fxml.FXML;
//import javafx.stage.WindowEvent;
//
///**
// * 主页头部业务
// *
// * @author oyzh
// * @since 2023/06/16
// */
//public class HeaderController extends SubStageController {
//
//    /**
//     * 项目信息
//     */
//    private Project project = Project.load();
//
//    /**
//     * 展开db树
//     */
//    @FXML
//    private SVGLabel expandTree;
//
//    /**
//     * 收缩db树
//     */
//    @FXML
//    private SVGLabel collapseTree;
//
//    /**
//     * db树互斥器
//     */
//    private final NodeMutexes treeMutexes = new NodeMutexes();
//
//    /**
//     * 数据传输
//     */
//    @FXML
//    private void transport() {
//        StageAdapter wrapper = StageManager.getStage(DamengDataTransportController.class);
//        if (wrapper != null) {
//            wrapper.toFront();
//        } else {
//            StageManager.showStage(DamengDataTransportController.class);
//        }
//    }
//
//    /**
//     * 设置
//     */
//    @FXML
//    private void setting() {
//        StageAdapter fxView = StageManager.getStage(SettingController.class);
//        if (fxView != null) {
//            fxView.toFront();
//        } else {
//            StageManager.showStage(SettingController.class, this.stage);
//        }
//    }
//
//    /**
//     * 关于
//     */
//    @FXML
//    private void about() {
//        StageManager.showStage(AboutController.class, this.stage);
//    }
//
//    /**
//     * 退出
//     */
//    @FXML
//    private void quit() {
//        if (MessageBox.confirm("确定退出" + this.project.getName() + "？")) {
//            StageManager.exit();
//        }
//    }
//
//    /**
//     * 收缩左侧db树
//     */
//    @FXML
//    private void collapseTree() {
//        this.treeMutexes.visible(this.expandTree);
//        DBEventUtil.leftCollapse();
//    }
//
//    /**
//     * 展开左侧db树
//     */
//    @FXML
//    private void expandTree() {
//        this.treeMutexes.visible(this.collapseTree);
//        DBEventUtil.leftExtend();
//    }
//
////    /**
////     * 搜索
////     */
////    @FXML
////    private void search() {
////        DBEventUtil.searchFire();
////    }
//
//    @Override
//    public void onWindowShown(WindowEvent event) {
//        super.onWindowShown(event);
//        this.treeMutexes.addNodes(this.collapseTree, this.expandTree);
//        this.treeMutexes.manageBindVisible();
//    }
//}
