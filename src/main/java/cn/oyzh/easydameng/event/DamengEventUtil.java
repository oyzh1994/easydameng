package cn.oyzh.easydameng.event;

import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.easydameng.procedure.DamengProcedure;
import cn.oyzh.easydameng.dameng.record.DamengRecordFilter;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.dameng.schema.DamengSchema;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.domain.DamengQuery;
import cn.oyzh.easydameng.event.connect.DBAddConnectEvent;
import cn.oyzh.easydameng.event.connect.DBConnectionClosedEvent;
import cn.oyzh.easydameng.event.connect.DBConnectionConnectedEvent;
import cn.oyzh.easydameng.event.connect.DamengConnectAddedEvent;
import cn.oyzh.easydameng.event.connect.DamengConnectDeletedEvent;
import cn.oyzh.easydameng.event.connect.DamengConnectUpdatedEvent;
import cn.oyzh.easydameng.event.schema.DamengSchemaAddedEvent;
import cn.oyzh.easydameng.event.schema.DamengSchemaClosedEvent;
import cn.oyzh.easydameng.event.schema.DamengSchemaDroppedEvent;
import cn.oyzh.easydameng.event.schema.DamengSchemaUpdatedEvent;
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
import cn.oyzh.easydameng.event.sql.ShellPrintSqlEvent;
import cn.oyzh.easydameng.event.table.DamengTableAddedEvent;
import cn.oyzh.easydameng.event.table.DamengTableAlertedEvent;
import cn.oyzh.easydameng.event.table.DamengTableClearedEvent;
import cn.oyzh.easydameng.event.table.DamengTableDesignEvent;
import cn.oyzh.easydameng.event.table.DamengTableDroppedEvent;
import cn.oyzh.easydameng.event.table.DamengTableFilteredEvent;
import cn.oyzh.easydameng.event.table.DamengTableOpenEvent;
import cn.oyzh.easydameng.event.table.DamengTableRenamedEvent;
import cn.oyzh.easydameng.event.table.DamengTableTruncatedEvent;
import cn.oyzh.easydameng.event.terminal.DBTerminalCloseEvent;
import cn.oyzh.easydameng.event.terminal.DBTerminalOpenEvent;
import cn.oyzh.easydameng.event.tree.DamengTreeItemChangedEvent;
import cn.oyzh.easydameng.event.view.DamengViewAddedEvent;
import cn.oyzh.easydameng.event.view.DamengViewAlertedEvent;
import cn.oyzh.easydameng.event.view.DamengViewDesignEvent;
import cn.oyzh.easydameng.event.view.DamengViewFilteredEvent;
import cn.oyzh.easydameng.event.view.DamengViewOpenEvent;
import cn.oyzh.easydameng.event.view.DamengViewRenamedEvent;
import cn.oyzh.easydameng.event.window.ShellShowMessageEvent;
import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;
import cn.oyzh.easydameng.trees.query.DamengQueryTreeItem;
import cn.oyzh.easydameng.trees.schema.DamengSchemaTreeItem;
import cn.oyzh.easydameng.trees.table.DamengTableTreeItem;
import cn.oyzh.easydameng.trees.view.DamengViewTreeItem;
import cn.oyzh.event.EventUtil;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import javafx.scene.control.TreeItem;

import java.util.List;

/**
 * 达梦事件工具
 *
 * @author oyzh
 * @since 2023/11/20
 */
public class DamengEventUtil {

    /**
     * 打开表事件
     *
     * @param item   表树节点
     * @param dbItem 数据库树节点
     */
    public static void tableOpen(DamengTableTreeItem item, DamengSchemaTreeItem dbItem) {
        DamengTableOpenEvent event = new DamengTableOpenEvent();
        event.data(item);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    // public static void recordDelete() {
    //     EventUtil.post(new DamengRecordDeleteEvent());
    // }

    /**
     * 表已新增事件
     *
     * @param item 数据库树节点
     */
    public static void tableAdded(DamengSchemaTreeItem item) {
        DamengTableAddedEvent event = new DamengTableAddedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 表已刷新事件
     *
     * @param tableName 表名
     * @param dbItem    数据库树节点
     */
    public static void tableAlerted(String tableName, DamengSchemaTreeItem dbItem) {
        DamengTableAlertedEvent event = new DamengTableAlertedEvent();
        event.data(tableName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 存储过程已新增事件
     *
     * @param dbItem 数据库树节点
     */
    public static void procedureAdded(DamengSchemaTreeItem dbItem) {
        DamengProcedureAddedEvent event = new DamengProcedureAddedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    /**
     * 存储过程已刷新事件
     *
     * @param procedureName 存储过程名称
     * @param dbItem        数据库树节点
     */
    public static void procedureAlerted(String procedureName, DamengSchemaTreeItem dbItem) {
        DamengProcedureAlertedEvent event = new DamengProcedureAlertedEvent();
        event.data(procedureName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

//    public static void eventAdded(DamengSchemaTreeItem dbItem) {
//        DamengEventAddedEvent event = new DamengEventAddedEvent();
//        event.data(dbItem);
//        EventUtil.post(event);
//    }
//
//    public static void eventAlerted(String eventName, DamengSchemaTreeItem dbItem) {
//        DamengEventAlertedEvent event = new DamengEventAlertedEvent();
//        event.data(eventName);
//        event.setDbItem(dbItem);
//        EventUtil.post(event);
//    }

    /**
     * 函数已新增事件
     *
     * @param dbItem 数据库树节点
     */
    public static void functionAdded(DamengSchemaTreeItem dbItem) {
        DamengFunctionAddedEvent event = new DamengFunctionAddedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    /**
     * 函数已刷新事件
     *
     * @param functionName 函数名称
     * @param dbItem       数据库树节点
     */
    public static void functionAlerted(String functionName, DamengSchemaTreeItem dbItem) {
        DamengFunctionAlertedEvent event = new DamengFunctionAlertedEvent();
        event.data(functionName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 表已重命名事件
     *
     * @param tableItem 表树节点
     * @param dbItem    数据库树节点
     */
    public static void tableRenamed(DamengTableTreeItem tableItem, DamengSchemaTreeItem dbItem) {
        DamengTableRenamedEvent event = new DamengTableRenamedEvent();
        event.setDbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    /**
     * 视图已重命名事件
     *
     * @param viewItem 视图树节点
     * @param dbItem   数据库树节点
     */
    public static void viewRenamed(DamengViewTreeItem viewItem, DamengSchemaTreeItem dbItem) {
        DamengViewRenamedEvent event = new DamengViewRenamedEvent();
        event.setDbItem(dbItem);
        event.data(viewItem);
        EventUtil.post(event);
    }

    //    public static void eventRenamed(DamengEventTreeItem viewItem, DamengSchemaTreeItem dbItem) {
    //        DamengEventRenamedEvent event = new DamengEventRenamedEvent();
    //        event.setDbItem(dbItem);
    //        event.data(viewItem);
    //        EventUtil.post(event);
    //    }

    /**
     * 表已清空事件
     *
     * @param tableItem 表树节点
     * @param dbItem    数据库树节点
     */
    public static void tableCleared(DamengTableTreeItem tableItem, DamengSchemaTreeItem dbItem) {
        DamengTableClearedEvent event = new DamengTableClearedEvent();
        event.setDbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    /**
     * 表已过滤事件
     *
     * @param item    表树节点
     * @param filters 过滤条件列表
     */
    public static void tableFiltered(DamengTableTreeItem item, List<DamengRecordFilter> filters) {
        DamengTableFilteredEvent event = new DamengTableFilteredEvent();
        event.data(item);
        event.setFilters(filters);
        EventUtil.post(event);
    }

    /**
     * 视图已过滤事件
     *
     * @param item    视图树节点
     * @param filters 过滤条件列表
     */
    public static void viewFiltered(DamengViewTreeItem item, List<DamengRecordFilter> filters) {
        DamengViewFilteredEvent event = new DamengViewFilteredEvent();
        event.data(item);
        event.setFilters(filters);
        EventUtil.post(event);
    }

    /**
     * 表已截断事件
     *
     * @param tableItem 表树节点
     * @param dbItem    数据库树节点
     */
    public static void tableTruncated(DamengTableTreeItem tableItem, DamengSchemaTreeItem dbItem) {
        DamengTableTruncatedEvent event = new DamengTableTruncatedEvent();
        event.setDbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    /**
     * 表已删除事件
     *
     * @param tableItem 表树节点
     * @param dbItem    数据库树节点
     */
    public static void tableDropped(DamengTableTreeItem tableItem, DamengSchemaTreeItem dbItem) {
        DamengTableDroppedEvent event = new DamengTableDroppedEvent();
        event.setDbItem(dbItem);
        event.data(tableItem);
        EventUtil.post(event);
    }

    /**
     * 数据库已关闭事件
     *
     * @param dbItem 数据库树节点
     */
    public static void schemaClosed(DamengSchemaTreeItem dbItem) {
        DamengSchemaClosedEvent event = new DamengSchemaClosedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    /**
     * 数据库已新增事件
     *
     * @param connectItem 连接树节点
     * @param schema      数据库
     */
    public static void schemaAdded(DBConnectTreeItem connectItem, DamengSchema schema) {
        DamengSchemaAddedEvent event = new DamengSchemaAddedEvent();
        event.data(schema);
        event.setConnectItem(connectItem);
        EventUtil.post(event);
    }

    /**
     * 数据库已修改事件
     *
     * @param connectItem 连接树节点
     * @param schema      数据库
     */
    public static void schemaUpdated(DBConnectTreeItem connectItem, DamengSchema schema) {
        DamengSchemaUpdatedEvent event = new DamengSchemaUpdatedEvent();
        event.data(schema);
        event.setConnectItem(connectItem);
        EventUtil.post(event);
    }

    /**
     * 数据库已删除事件
     *
     * @param dbItem 数据库树节点
     */
    public static void schemaDropped(DamengSchemaTreeItem dbItem) {
        DamengSchemaDroppedEvent event = new DamengSchemaDroppedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    /**
     * 查询添加事件
     *
     * @param item 数据库树节点
     */
    public static void queryAdd(DamengSchemaTreeItem item) {
        DamengQueryAddEvent event = new DamengQueryAddEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 查询已新增事件
     *
     * @param query 查询
     * @param item  数据库树节点
     */
    public static void queryAdded(DamengQuery query, DamengSchemaTreeItem item) {
        DamengQueryAddedEvent event = new DamengQueryAddedEvent();
        event.data(query);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    /**
     * 查询已删除事件
     *
     * @param item 查询树节点
     */
    public static void queryDeleted(DamengQueryTreeItem item) {
        DamengQueryDeletedEvent event = new DamengQueryDeletedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 查询打开事件
     *
     * @param query 查询
     * @param item  数据库树节点
     */
    public static void queryOpen(DamengQuery query, DamengSchemaTreeItem item) {
        DamengQueryOpenEvent event = new DamengQueryOpenEvent();
        event.data(query);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    /**
     * 查询已重命名事件
     *
     * @param query 查询
     * @param item  数据库树节点
     */
    public static void queryRenamed(DamengQuery query, DamengSchemaTreeItem item) {
        DamengQueryRenamedEvent event = new DamengQueryRenamedEvent();
        event.data(query);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    /**
     * 视图打开事件
     *
     * @param item   视图树节点
     * @param dbItem 数据库树节点
     */
    public static void viewOpen(DamengViewTreeItem item, DamengSchemaTreeItem dbItem) {
        DamengViewOpenEvent event = new DamengViewOpenEvent();
        event.data(item);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 设计函数事件
     *
     * @param function 函数
     * @param dbItem   数据库树节点
     */
    public static void designFunction(DamengFunction function, DamengSchemaTreeItem dbItem) {
        DamengFunctionDesignEvent event = new DamengFunctionDesignEvent();
        event.data(function);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 设计存储过程事件
     *
     * @param procedure 存储过程
     * @param dbItem    数据库树节点
     */
    public static void designProcedure(DamengProcedure procedure, DamengSchemaTreeItem dbItem) {
        DamengProcedureDesignEvent event = new DamengProcedureDesignEvent();
        event.data(procedure);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

//    public static void designEvent(DamengEvent event, DamengSchemaTreeItem dbItem) {
//        DamengEventDesignEvent event1 = new DamengEventDesignEvent();
//        event1.data(event);
//        event1.setDbItem(dbItem);
//        EventUtil.post(event1);
//    }

    /**
     * 视图已刷新事件
     *
     * @param viewName 视图名称
     * @param dbItem   数据库树节点
     */
    public static void viewAlerted(String viewName, DamengSchemaTreeItem dbItem) {
        DamengViewAlertedEvent event = new DamengViewAlertedEvent();
        event.data(viewName);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 设计视图事件
     *
     * @param dbView 视图
     * @param dbItem 数据库树节点
     */
    public static void designView(DamengView dbView, DamengSchemaTreeItem dbItem) {
        DamengViewDesignEvent event = new DamengViewDesignEvent();
        event.data(dbView);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 视图已新增事件
     *
     * @param dbItem 数据库树节点
     */
    public static void viewAdded(DamengSchemaTreeItem dbItem) {
        DamengViewAddedEvent event = new DamengViewAddedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    /**
     * 连接已删除事件
     *
     * @param info 连接
     */
    public static void infoDeleted(DamengConnect info) {
        DamengConnectDeletedEvent event = new DamengConnectDeletedEvent();
        event.data(info);
        EventUtil.post(event);
    }

    /**
     * 设计表事件
     *
     * @param table  表
     * @param dbItem 数据库树节点
     */
    public static void designTable(DamengTable table, DamengSchemaTreeItem dbItem) {
        DamengTableDesignEvent event = new DamengTableDesignEvent();
        event.data(table);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 连接关闭事件
     *
     * @param client 达梦客户端
     */
    public static void connectionClosed(ShellDamengClient client) {
        DBConnectionClosedEvent event = new DBConnectionClosedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 连接成功事件
     *
     * @param client 达梦客户端
     */
    public static void connectionConnected(ShellDamengClient client) {
        DBConnectionConnectedEvent event = new DBConnectionConnectedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 终端打开事件
     *
     * @param client 达梦客户端
     * @param schema 数据库模式
     */
    public static void terminalOpen(ShellDamengClient client, String schema) {
        DBTerminalOpenEvent event = new DBTerminalOpenEvent();
        event.data(client);
        event.setSchema(schema);
        EventUtil.post(event);
    }

    /**
     * 终端关闭事件
     *
     * @param client dameng客户端
     */
    public static void terminalClose(ShellDamengClient client) {
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

    /**
     * 添加连接事件
     */
    public static void addConnect() {
        EventUtil.post(new DBAddConnectEvent());
    }

    /**
     * 添加分组事件
     */
    public static void addGroup() {
        EventUtil.post(new DBAddGroupEvent());
    }

    /**
     * 更新日志事件
     */
    public static void changelog() {
        EventUtil.post(new ChangelogEvent());
    }

    /**
     * 连接已新增事件
     *
     * @param connect 连接
     */
    public static void connectAdded(DamengConnect connect) {
        DamengConnectAddedEvent event = new DamengConnectAddedEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    /**
     * 连接已删除事件
     *
     * @param connect 连接
     */
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

    /**
     * 打印SQL事件
     *
     * @param sql     SQL语句
     * @param connect 连接
     */
    public static void printSql(String sql, DamengConnect connect) {
        ShellPrintSqlEvent event = new ShellPrintSqlEvent();
        event.data(sql);
        event.setConnect(connect);
        EventUtil.post(event);
    }
}
