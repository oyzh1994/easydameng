//package cn.oyzh.easydameng.event;
//
//import cn.oyzh.easydameng.dameng.DamengClient;
//import cn.oyzh.easydameng.domain.DamengConnect;
//import cn.oyzh.easydameng.event.connect.DBAddConnectEvent;
//import cn.oyzh.easydameng.event.connect.DBConnectionClosedEvent;
//import cn.oyzh.easydameng.event.connect.DBConnectionConnectedEvent;
//import cn.oyzh.easydameng.event.connect.DamengConnectAddedEvent;
//import cn.oyzh.easydameng.event.connect.DamengConnectDeletedEvent;
//import cn.oyzh.easydameng.event.connect.DamengConnectUpdatedEvent;
//import cn.oyzh.easydameng.event.group.DBAddGroupEvent;
//import cn.oyzh.easydameng.event.record.RecordDeleteEvent;
//import cn.oyzh.easydameng.event.terminal.DBTerminalCloseEvent;
//import cn.oyzh.easydameng.event.terminal.DBTerminalOpenEvent;
//import cn.oyzh.easydameng.event.tree.DamengTreeItemChangedEvent;
//import cn.oyzh.event.EventUtil;
//import cn.oyzh.fx.gui.event.Layout1Event;
//import cn.oyzh.fx.gui.event.Layout2Event;
//import cn.oyzh.fx.plus.changelog.ChangelogEvent;
//import javafx.scene.control.TreeItem;
//import lombok.experimental.UtilityClass;
//
///**
// * redis事件工具
// *
// * @author oyzh
// * @since 2023/11/20
// */
//@UtilityClass
//public class DBEventUtil {
//
//    /**
//     * 连接关闭事件
//     *
//     * @param client redis客户端
//     */
//    public static void connectionClosed(DamengClient client) {
//        DBConnectionClosedEvent event = new DBConnectionClosedEvent();
//        event.data(client);
//        EventUtil.post(event);
//    }
//
//    /**
//     * 连接成功事件
//     *
//     * @param client redis客户端
//     */
//    public static void connectionConnected(DamengClient client) {
//        DBConnectionConnectedEvent event = new DBConnectionConnectedEvent();
//        event.data(client);
//        EventUtil.post(event);
//    }
//
//    /**
//     * 终端打开事件
//     */
//    public static void terminalOpen() {
//        terminalOpen(null);
//    }
//
//    /**
//     * 终端打开事件
//     *
//     * @param info redis信息
//     */
//    public static void terminalOpen(DamengConnect info) {
//        DBTerminalOpenEvent event = new DBTerminalOpenEvent();
//        event.data(info);
//        EventUtil.post(event);
//    }
//
//    /**
//     * 终端关闭事件
//     *
//     * @param info redis信息
//     */
//    public static void terminalClose(DamengConnect info) {
//        DBTerminalCloseEvent event = new DBTerminalCloseEvent();
//        event.data(info);
//        EventUtil.post(event);
//    }
//
////    /**
////     * 树节点变化事件
////     */
////    public static void treeChildChanged() {
////        EventUtil.postDelay(new TreeChildChangedEvent(), 100);
////    }
//
//    public static void recordDelete() {
//        EventUtil.post(new RecordDeleteEvent());
//    }
//
//    /**
//     * 连接已修改事件
//     *
//     * @param connect DB信息
//     */
//    public static void connectUpdated(DamengConnect connect) {
//        DamengConnectUpdatedEvent event = new DamengConnectUpdatedEvent();
//        event.data(connect);
//        EventUtil.post(event);
//    }
//
//    public static void addConnect() {
//        EventUtil.post(new DBAddConnectEvent());
//    }
//
//    public static void addGroup() {
//        EventUtil.post(new DBAddGroupEvent());
//    }
//
//    public static void changelog() {
//        EventUtil.post(new ChangelogEvent());
//    }
//
//    public static void connectAdded(DamengConnect connect) {
//        DamengConnectAddedEvent event = new DamengConnectAddedEvent();
//        event.data(connect);
//        EventUtil.post(event);
//    }
//
//    public static void connectDeleted(DamengConnect connect) {
//        DamengConnectDeletedEvent event = new DamengConnectDeletedEvent();
//        event.data(connect);
//        EventUtil.post(event);
//    }
//
//    /**
//     * 布局1
//     */
//    public static void layout1() {
//        EventUtil.post(new Layout1Event());
//    }
//
//    /**
//     * 布局2
//     */
//    public static void layout2() {
//        EventUtil.post(new Layout2Event());
//    }
//
//    /**
//     * 节点选中事件
//     *
//     * @param item 节点
//     */
//    public static void treeItemChanged(TreeItem<?> item) {
//        DamengTreeItemChangedEvent event = new DamengTreeItemChangedEvent();
//        event.data(item);
//        EventUtil.post(event);
//    }
//
//}
