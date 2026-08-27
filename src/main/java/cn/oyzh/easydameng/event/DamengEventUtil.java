package cn.oyzh.easydameng.event;

import cn.oyzh.easydameng.db.DBDatabase;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.domain.DamengQuery;
import cn.oyzh.easydameng.event.connect.DBAddConnectEvent;
import cn.oyzh.easydameng.event.connect.DBConnectionClosedEvent;
import cn.oyzh.easydameng.event.connect.DBConnectionConnectedEvent;
import cn.oyzh.easydameng.event.connect.DamengConnectAddedEvent;
import cn.oyzh.easydameng.event.connect.DamengConnectDeletedEvent;
import cn.oyzh.easydameng.event.connect.DamengConnectUpdatedEvent;
import cn.oyzh.easydameng.event.database.DamengDatabaseAddedEvent;
import cn.oyzh.easydameng.event.database.DamengDatabaseClosedEvent;
import cn.oyzh.easydameng.event.database.DamengDatabaseDroppedEvent;
import cn.oyzh.easydameng.event.database.DamengDatabaseUpdatedEvent;
import cn.oyzh.easydameng.event.event.DamengEventAddedEvent;
import cn.oyzh.easydameng.event.event.DamengEventAlertedEvent;
import cn.oyzh.easydameng.event.event.DamengEventDesignEvent;
import cn.oyzh.easydameng.event.event.DamengEventRenamedEvent;
import cn.oyzh.easydameng.event.function.DamengFunctionAddedEvent;
import cn.oyzh.easydameng.event.function.DamengFunctionAlertedEvent;
import cn.oyzh.easydameng.event.function.DamengFunctionDesignEvent;
import cn.oyzh.easydameng.event.group.DBAddGroupEvent;
import cn.oyzh.easydameng.event.procedure.DamengProcedureAddedEvent;
import cn.oyzh.easydameng.event.procedure.DamengProcedureAlertedEvent;
import cn.oyzh.easydameng.event.procedure.DamengProcedureDesignEvent;
import cn.oyzh.easydameng.event.query.DamengQueryAddEvent;
import cn.oyzh.easydameng.event.query.DamengQueryAddedEvent;
import cn.oyzh.easydameng.event.query.DamengQueryDeletedEvent;
import cn.oyzh.easydameng.event.query.DamengQueryOpenEvent;
import cn.oyzh.easydameng.event.query.DamengQueryRenamedEvent;
import cn.oyzh.easydameng.event.table.DamengTableAddedEvent;
import cn.oyzh.easydameng.event.table.DamengTableAlertedEvent;
import cn.oyzh.easydameng.event.table.DamengTableClearedEvent;
import cn.oyzh.easydameng.event.table.DamengTableDesignEvent;
import cn.oyzh.easydameng.event.table.DamengTableDroppedEvent;
import cn.oyzh.easydameng.event.table.DamengTableFilteredEvent;
import cn.oyzh.easydameng.event.terminal.DBTerminalCloseEvent;
import cn.oyzh.easydameng.event.terminal.DBTerminalOpenEvent;
import cn.oyzh.easydameng.event.table.DamengTableOpenEvent;
import cn.oyzh.easydameng.event.table.DamengTableRenamedEvent;
import cn.oyzh.easydameng.event.table.DamengTableTruncatedEvent;
import cn.oyzh.easydameng.event.tree.DamengTreeItemChangedEvent;
import cn.oyzh.easydameng.event.view.DamengViewAddedEvent;
import cn.oyzh.easydameng.event.view.DamengViewAlertedEvent;
import cn.oyzh.easydameng.event.view.DamengViewDesignEvent;
import cn.oyzh.easydameng.event.view.DamengViewFilteredEvent;
import cn.oyzh.easydameng.event.view.DamengViewOpenEvent;
import cn.oyzh.easydameng.event.view.DamengViewRenamedEvent;
import cn.oyzh.easydameng.dameng.DamengClient;
import cn.oyzh.easydameng.dameng.event.DamengEvent;
import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.easydameng.dameng.procedure.DamengProcedure;
import cn.oyzh.easydameng.dameng.record.DamengRecordFilter;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.event.window.ShellShowMessageEvent;
import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;
import cn.oyzh.easydameng.trees.database.DamengDatabaseTreeItem;
import cn.oyzh.easydameng.trees.event.DamengEventTreeItem;
import cn.oyzh.easydameng.trees.query.DamengQueryTreeItem;
import cn.oyzh.easydameng.trees.table.DamengTableTreeItem;
import cn.oyzh.easydameng.trees.view.DamengViewTreeItem;
import cn.oyzh.event.EventUtil;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import javafx.scene.control.TreeItem;

import java.util.List;

/**
 * redis事件工具
 *
 * @author oyzh
 * @since 2023/11/20
 */
public class DamengEventUtil {

    public static void tableOpen(DamengTableTreeItem item, DamengDatabaseTreeItem dbItem) {
        DamengTableOpenEvent event = new DamengTableOpenEvent();
        event.data(item);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    // public static void recordDelete() {
    //     EventUtil.post(new DamengRecordDeleteEvent());
    // }

    public static void tableAdded(DamengDatabaseTreeItem item) {
        DamengTableAddedEvent event = new DamengTableAddedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    public static void tableAlerted(String tableName, DamengDatabaseTreeItem dbItem) {
        DamengTableAlertedEvent event = new DamengTableAlertedEvent();
        event.data(tableName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void procedureAdded(DamengDatabaseTreeItem dbItem) {
        DamengProcedureAddedEvent event = new DamengProcedureAddedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void procedureAlerted(String procedureName, DamengDatabaseTreeItem dbItem) {
        DamengProcedureAlertedEvent event = new DamengProcedureAlertedEvent();
        event.data(procedureName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void eventAdded(DamengDatabaseTreeItem dbItem) {
        DamengEventAddedEvent event = new DamengEventAddedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void eventAlerted(String eventName, DamengDatabaseTreeItem dbItem) {
        DamengEventAlertedEvent event = new DamengEventAlertedEvent();
        event.data(eventName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void functionAdded(DamengDatabaseTreeItem dbItem) {
        DamengFunctionAddedEvent event = new DamengFunctionAddedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void functionAlerted(String functionName, DamengDatabaseTreeItem dbItem) {
        DamengFunctionAlertedEvent event = new DamengFunctionAlertedEvent();
        event.data(functionName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void tableRenamed(DamengTableTreeItem tableItem, DamengDatabaseTreeItem dbItem) {
        DamengTableRenamedEvent event = new DamengTableRenamedEvent();
        event.setDbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    public static void viewRenamed(DamengViewTreeItem viewItem, DamengDatabaseTreeItem dbItem) {
        DamengViewRenamedEvent event = new DamengViewRenamedEvent();
        event.setDbItem(dbItem);
        event.data(viewItem);
        EventUtil.post(event);
    }

    public static void eventRenamed(DamengEventTreeItem viewItem, DamengDatabaseTreeItem dbItem) {
        DamengEventRenamedEvent event = new DamengEventRenamedEvent();
        event.setDbItem(dbItem);
        event.data(viewItem);
        EventUtil.post(event);
    }

    public static void tableCleared(DamengTableTreeItem tableItem, DamengDatabaseTreeItem dbItem) {
        DamengTableClearedEvent event = new DamengTableClearedEvent();
        event.setDbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    public static void tableFiltered(DamengTableTreeItem item, List<DamengRecordFilter> filters) {
        DamengTableFilteredEvent event = new DamengTableFilteredEvent();
        event.data(item);
        event.setFilters(filters);
        EventUtil.post(event);
    }

    public static void viewFiltered(DamengViewTreeItem item, List<DamengRecordFilter> filters) {
        DamengViewFilteredEvent event = new DamengViewFilteredEvent();
        event.data(item);
        event.setFilters(filters);
        EventUtil.post(event);
    }

    public static void tableTruncated(DamengTableTreeItem tableItem, DamengDatabaseTreeItem dbItem) {
        DamengTableTruncatedEvent event = new DamengTableTruncatedEvent();
        event.setDbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    public static void tableDropped(DamengTableTreeItem tableItem, DamengDatabaseTreeItem dbItem) {
        DamengTableDroppedEvent event = new DamengTableDroppedEvent();
        event.setDbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    public static void databaseClosed(DamengDatabaseTreeItem dbItem) {
        DamengDatabaseClosedEvent event = new DamengDatabaseClosedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void databaseAdded(DBConnectTreeItem connectItem, DBDatabase database) {
        DamengDatabaseAddedEvent event = new DamengDatabaseAddedEvent();
        event.data(database);
        event.setConnectItem(connectItem);
        EventUtil.post(event);
    }

    public static void databaseUpdated(DBConnectTreeItem connectItem, DBDatabase database) {
        DamengDatabaseUpdatedEvent event = new DamengDatabaseUpdatedEvent();
        event.data(database);
        event.setConnectItem(connectItem);
        EventUtil.post(event);
    }

    public static void databaseDropped(DamengDatabaseTreeItem dbItem) {
        DamengDatabaseDroppedEvent event = new DamengDatabaseDroppedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void queryAdd(DamengDatabaseTreeItem item) {
        DamengQueryAddEvent event = new DamengQueryAddEvent();
        event.data(item);
        EventUtil.post(event);
    }

    public static void queryAdded(DamengQuery query, DamengDatabaseTreeItem item) {
        DamengQueryAddedEvent event = new DamengQueryAddedEvent();
        event.data(query);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    public static void queryDeleted(DamengQueryTreeItem item) {
        DamengQueryDeletedEvent event = new DamengQueryDeletedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    public static void queryOpen(DamengQuery query, DamengDatabaseTreeItem item) {
        DamengQueryOpenEvent event = new DamengQueryOpenEvent();
        event.data(query);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    public static void queryRenamed(DamengQuery query, DamengDatabaseTreeItem item) {
        DamengQueryRenamedEvent event = new DamengQueryRenamedEvent();
        event.data(query);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    public static void viewOpen(DamengViewTreeItem item, DamengDatabaseTreeItem dbItem) {
        DamengViewOpenEvent event = new DamengViewOpenEvent();
        event.data(item);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void designFunction(DamengFunction function, DamengDatabaseTreeItem dbItem) {
        DamengFunctionDesignEvent event = new DamengFunctionDesignEvent();
        event.data(function);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void designProcedure(DamengProcedure procedure, DamengDatabaseTreeItem dbItem) {
        DamengProcedureDesignEvent event = new DamengProcedureDesignEvent();
        event.data(procedure);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void designEvent(DamengEvent event, DamengDatabaseTreeItem dbItem) {
        DamengEventDesignEvent event1 = new DamengEventDesignEvent();
        event1.data(event);
        event1.setDbItem(dbItem);
        EventUtil.post(event1);
    }

    public static void viewAlerted(String viewName, DamengDatabaseTreeItem dbItem) {
        DamengViewAlertedEvent event = new DamengViewAlertedEvent();
        event.data(viewName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void designView(DamengView dbView, DamengDatabaseTreeItem dbItem) {
        DamengViewDesignEvent event = new DamengViewDesignEvent();
        event.data(dbView);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void viewAdded(DamengDatabaseTreeItem dbItem) {
        DamengViewAddedEvent event = new DamengViewAddedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void infoDeleted(DamengConnect info) {
        DamengConnectDeletedEvent event = new DamengConnectDeletedEvent();
        event.data(info);
        EventUtil.post(event);
    }

    public static void designTable(DamengTable table, DamengDatabaseTreeItem dbItem) {
        DamengTableDesignEvent event = new DamengTableDesignEvent();
        event.data(table);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 连接关闭事件
     *
     * @param client redis客户端
     */
    public static void connectionClosed(DamengClient client) {
        DBConnectionClosedEvent event = new DBConnectionClosedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 连接成功事件
     *
     * @param client redis客户端
     */
    public static void connectionConnected(DamengClient client) {
        DBConnectionConnectedEvent event = new DBConnectionConnectedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 终端打开事件
     */
    public static void terminalOpen(DamengClient client, String dbName) {
        DBTerminalOpenEvent event = new DBTerminalOpenEvent();
        event.data(client);
        event.setDbName(dbName);
        EventUtil.post(event);
    }

    /**
     * 终端关闭事件
     *
     * @param client dameng客户端
     */
    public static void terminalClose(DamengClient client) {
        DBTerminalCloseEvent event = new DBTerminalCloseEvent();
        event.data(client);
        EventUtil.post(event);
    }

//    /**
//     * 树节点变化事件
//     */
//    public static void treeChildChanged() {
//        EventUtil.postDelay(new TreeChildChangedEvent(), 100);
//    }

    // public static void recordDelete(DamengRecord record) {
    //     RecordDeleteEvent event = new RecordDeleteEvent();
    //     event.data(record);
    //     EventUtil.post(event);
    // }

    /**
     * 连接已修改事件
     *
     * @param connect DB信息
     */
    public static void connectUpdated(DamengConnect connect) {
        DamengConnectUpdatedEvent event = new DamengConnectUpdatedEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    public static void addConnect() {
        EventUtil.post(new DBAddConnectEvent());
    }

    public static void addGroup() {
        EventUtil.post(new DBAddGroupEvent());
    }

    public static void changelog() {
        EventUtil.post(new ChangelogEvent());
    }

    public static void connectAdded(DamengConnect connect) {
        DamengConnectAddedEvent event = new DamengConnectAddedEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    public static void connectDeleted(DamengConnect connect) {
        DamengConnectDeletedEvent event = new DamengConnectDeletedEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    /**
     * 布局1
     */
    public static void layout1() {
        EventUtil.post(new Layout1Event());
    }

    /**
     * 布局2
     */
    public static void layout2() {
        EventUtil.post(new Layout2Event());
    }

    /**
     * 节点选中事件
     *
     * @param item 节点
     */
    public static void treeItemChanged(TreeItem<?> item) {
        DamengTreeItemChangedEvent event = new DamengTreeItemChangedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 显示消息页面
     */
    public static void showMessage() {
        EventUtil.post(new ShellShowMessageEvent());
    }
}
