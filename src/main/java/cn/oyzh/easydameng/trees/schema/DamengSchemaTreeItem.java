package cn.oyzh.easydameng.trees.schema;

import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.easydameng.controller.data.ShellDamengDataDumpController;
import cn.oyzh.easydameng.controller.data.ShellDamengDataRunSqlFileController;
import cn.oyzh.easydameng.controller.database.ShellDamengSchemaUpdateController;
import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.dameng.check.DamengCheck;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.column.DamengSelectColumnParam;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKey;
import cn.oyzh.easydameng.dameng.function.DamengAlertFunctionParam;
import cn.oyzh.easydameng.dameng.function.DamengCreateFunctionParam;
import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.easydameng.dameng.index.DamengIndex;
import cn.oyzh.easydameng.dameng.trigger.DamengTrigger;
import cn.oyzh.easydameng.procedure.DamengAlertProcedureParam;
import cn.oyzh.easydameng.procedure.DamengCreateProcedureParam;
import cn.oyzh.easydameng.procedure.DamengProcedure;
import cn.oyzh.easydameng.dameng.query.DamengExecuteResult;
import cn.oyzh.easydameng.dameng.query.DamengExplainResult;
import cn.oyzh.easydameng.dameng.record.DamengDeleteRecordParam;
import cn.oyzh.easydameng.dameng.record.DamengRecord;
import cn.oyzh.easydameng.dameng.record.DamengSelectRecordParam;
import cn.oyzh.easydameng.dameng.table.DamengAlertTableParam;
import cn.oyzh.easydameng.dameng.table.DamengCreateTableParam;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.dameng.view.DamengAlertViewParam;
import cn.oyzh.easydameng.dameng.view.DamengCreateViewParam;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.dameng.schema.DamengSchema;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.trees.DBTreeItem;
import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;
import cn.oyzh.easydameng.trees.function.DamengFunctionTreeItem;
import cn.oyzh.easydameng.trees.function.DamengFunctionsTreeItem;
import cn.oyzh.easydameng.trees.procedure.DamengProcedureTreeItem;
import cn.oyzh.easydameng.trees.procedure.DamengProceduresTreeItem;
import cn.oyzh.easydameng.trees.query.DamengQueriesTreeItem;
import cn.oyzh.easydameng.trees.table.DamengTableTreeItem;
import cn.oyzh.easydameng.trees.table.DamengTablesTreeItem;
import cn.oyzh.easydameng.trees.terminal.DamengTerminalTreeItem;
import cn.oyzh.easydameng.trees.view.DamengViewTreeItem;
import cn.oyzh.easydameng.trees.view.DamengViewsTreeItem;
import cn.oyzh.fx.db.DBDialect;
import cn.oyzh.fx.db.DBObjects;
import cn.oyzh.fx.db.query.DBQueryResults;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库树模式节点
 *
 * @author oyzh
 * @since 2023/12/12
 */
public class DamengSchemaTreeItem extends DBTreeItem<DamengSchemaTreeItemValue> {

    /**
     * 当前值
     */
    private final DamengSchema value;

    /**
     * 获取当前模式对象
     *
     * @return 当前模式对象
     */
    public DamengSchema value() {
        return value;
    }

    /**
     * 构造模式树节点
     *
     * @param database 模式对象
     * @param treeView 所属树视图
     */
    public DamengSchemaTreeItem(DamengSchema database, RichTreeView treeView) {
        super(treeView);
        super.setSortable(false);
        super.setFilterable(true);
        this.value = database;
        this.setValue(new DamengSchemaTreeItemValue(this));
    }

    @Override
    public DBConnectTreeItem parent() {
        return (DBConnectTreeItem) super.parent();
    }

    /**
     * 获取模式名称
     *
     * @return 模式名称
     */
    public String schema() {
        return this.value.getName();
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String userName() {
        return this.info().getUser();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (!this.isChildEmpty()) {
            FXMenuItem closeDB = MenuItemHelper.closeDatabase(this::closeDB);
            items.add(closeDB);
        }
        FXMenuItem editDB = MenuItemHelper.editDatabase(this::editDB);
        items.add(editDB);
        FXMenuItem dropDB = MenuItemHelper.deleteDatabase(this::delete);
        items.add(dropDB);
        FXMenuItem dumpData = MenuItemHelper.dumpData(this::dump);
        items.add(dumpData);
        FXMenuItem runSqlFile = MenuItemHelper.runSqlFile(this::runSqlFile);
        items.add(runSqlFile);
        // FXMenuItem dbInfo = MenuItemHelper.databaseInfo( this::dbInfo);
        // items.add(dbInfo);
        return items;
    }

    /**
     * 运行sql文件
     */
    private void runSqlFile() {
        StageAdapter fxView = StageManager.parseStage(ShellDamengDataRunSqlFileController.class, this.getWindow());
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.schema());
        fxView.setProp("dbClient", this.client());
        fxView.display();
    }

    /**
     * 转储
     */
    private void dump() {
        StageAdapter fxView = StageManager.parseStage(ShellDamengDataDumpController.class, this.getWindow());
        fxView.setProp("dumpType", 1);
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.schema());
        fxView.setProp("dbClient", this.client());
        fxView.display();
    }

    // private void dbInfo() {
    //     StageAdapter fxView = StageManager.parseStage(DamengDatabaseInfoController.class, this.window());
    //     fxView.setProp("dbItem", this);
    //     fxView.display();
    // }

    @Override
    public void delete() {
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    if (MessageBox.confirm(I18nHelper.deleteSchema() + "[" + this.schema() + "]", this.getWindow())) {
                        if (this.parent().dropSchema(this.schema())) {
                            DamengEventUtil.schemaDropped(this);
                            super.remove();
                        } else {
                            MessageBox.warn(I18nHelper.operationFail(), this.getWindow());
                        }
                    }
                })
                .onSuccess(super::refresh)
                .build();
        super.startWaiting(task);
    }

    /**
     * 编辑数据库
     */
    public void editDB() {
        StageAdapter fxView = StageManager.parseStage(ShellDamengSchemaUpdateController.class, this.getWindow());
        fxView.setProp("database", this.value);
        fxView.setProp("connectItem", this.parent());
        fxView.display();
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        this.clearChild();
        this.collapse();
        this.setLoaded(false);
        DamengEventUtil.schemaClosed(this);
    }

    @Override
    public void loadChild() {
        if (!this.isLoading() && !this.isLoaded()) {
            this.setLoaded(true);
            this.setLoading(true);
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> {
                        List<TreeItem<?>> typeItems = new ArrayList<>();
                        typeItems.add(new DamengTablesTreeItem(this.getTreeView()));
                        typeItems.add(new DamengViewsTreeItem(this.getTreeView()));
                        typeItems.add(new DamengFunctionsTreeItem(this.getTreeView()));
                        typeItems.add(new DamengProceduresTreeItem(this.getTreeView()));
                        //typeItems.add(new DamengEventsTreeItem(this.getTreeView()));
                        typeItems.add(new DamengQueriesTreeItem(this.getTreeView()));
                        typeItems.add(new DamengTerminalTreeItem(this.getTreeView()));
                        super.setChild(typeItems);
                    })
                    .onSuccess(this::expend)
                    .onError(ex -> {
                        this.setLoaded(false);
                        MessageBox.error(ex.getMessage(), this.getWindow());
                    })
                    .onFinish(() -> this.setLoading(false))
                    .build();
            super.startWaiting(task);
        }

    }

    /**
     * 获取表类型子节点
     *
     * @return 表类型子节点
     */
    public DamengTablesTreeItem getTableTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof DamengTablesTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取表节点列表
     *
     * @return 表节点列表
     */
    public List<DamengTableTreeItem> getTableChild() {
        List<DamengTableTreeItem> list = new ArrayList<>();
        for (RichTreeItem<?> child : this.getTableTypeChild().richChildren()) {
            if (child instanceof DamengTableTreeItem treeItem) {
                list.add(treeItem);
            }
        }
        return list;
    }

    /**
     * 获取查询类型子节点
     *
     * @return 查询类型子节点
     */
    public DamengQueriesTreeItem getQueryTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof DamengQueriesTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取函数类型子节点
     *
     * @return 函数类型子节点
     */
    public DamengFunctionsTreeItem getFunctionTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof DamengFunctionsTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取函数节点列表
     *
     * @return 函数节点列表
     */
    public List<DamengFunctionTreeItem> getFunctionChild() {
        List<DamengFunctionTreeItem> list = new ArrayList<>();
        for (RichTreeItem<?> child : this.getFunctionTypeChild().richChildren()) {
            if (child instanceof DamengFunctionTreeItem treeItem) {
                list.add(treeItem);
            }
        }
        return list;
    }

    /**
     * 获取过程类型子节点
     *
     * @return 过程类型子节点
     */
    public DamengProceduresTreeItem getProcedureTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof DamengProceduresTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取过程节点列表
     *
     * @return 过程节点列表
     */
    public List<DamengProcedureTreeItem> getProcedureChild() {
        List<DamengProcedureTreeItem> list = new ArrayList<>();
        for (RichTreeItem<?> child : this.getProcedureTypeChild().richChildren()) {
            if (child instanceof DamengProcedureTreeItem treeItem) {
                list.add(treeItem);
            }
        }
        return list;
    }

    //    public DamengEventsTreeItem getEventTypeChild() {
    //        for (RichTreeItem<?> child : this.richChildren()) {
    //            if (child instanceof DamengEventsTreeItem treeItem) {
    //                return treeItem;
    //            }
    //        }
    //        return null;
    //    }
    //
    //    public List<DamengEventTreeItem> getEventChild() {
    //        List<DamengEventTreeItem> list = new ArrayList<>();
    //        for (RichTreeItem<?> child : this.getEventTypeChild().richChildren()) {
    //            if (child instanceof DamengEventTreeItem treeItem) {
    //                list.add(treeItem);
    //            }
    //        }
    //        return list;
    //    }

    /**
     * 获取视图类型子节点
     *
     * @return 视图类型子节点
     */
    public DamengViewsTreeItem getViewTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof DamengViewsTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取视图节点列表
     *
     * @return 视图节点列表
     */
    public List<DamengViewTreeItem> getViewChild() {
        List<DamengViewTreeItem> list = new ArrayList<>();
        for (RichTreeItem<?> child : this.getViewTypeChild().richChildren()) {
            if (child instanceof DamengViewTreeItem treeItem) {
                list.add(treeItem);
            }
        }
        return list;
    }

    /**
     * 获取数据库客户端
     *
     * @return 数据库客户端
     */
    public ShellDamengClient client() {
        return this.parent().getClient();
    }

    /**
     * 获取连接信息
     *
     * @return 连接信息
     */
    public DamengConnect info() {
        return this.parent().value();
    }

    /**
     * 获取表数量
     *
     * @return 表数量
     */
    public Integer tableSize() {
        try {
            return this.client().tableSize(this.schema());
        } catch (Exception ex) {
            MessageBox.exception(ex, this.getWindow());
        }
        return 0;
    }

    /**
     * 获取视图数量
     *
     * @return 视图数量
     */
    public Integer viewSize() {
        return this.client().viewSize(this.schema());
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String infoName() {
        return this.info().getName();
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String connectName() {
        return this.info().getName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        if (!this.isLoaded()) {
            this.loadChild();
        } else {
            super.onPrimaryDoubleClick();
        }
    }

    /**
     * 创建表
     *
     * @param table       表对象
     * @param columns     列集合
     * @param indexes     索引集合
     * @param foreignKeys 外键集合
     * @param triggers    触发器集合
     * @param checks      检查约束集合
     */
    public void createTable(DamengTable table, DamengColumns columns, DBObjects<DamengIndex> indexes, DBObjects<DamengForeignKey> foreignKeys, DBObjects<DamengTrigger> triggers, DBObjects<DamengCheck> checks) {
        DamengCreateTableParam param = new DamengCreateTableParam();
        param.setTable(table);
        param.setChecks(checks);
        param.setColumns(columns);
        param.setIndexes(indexes);
        param.setTriggers(triggers);
        param.setForeignKeys(foreignKeys);
        this.client().createTable(param);
    }

    /**
     * 创建表
     *
     * @param param 建表参数
     */
    public void createTable(DamengCreateTableParam param) {
        this.client().createTable(param);
    }

    /**
     * 构建建表参数
     *
     * @param table       表对象
     * @param columns     列集合
     * @param indexes     索引集合
     * @param foreignKeys 外键集合
     * @param triggers    触发器集合
     * @param checks      检查约束集合
     * @return 建表参数
     */
    public DamengCreateTableParam createTableParam(DamengTable table, DamengColumns columns, DBObjects<DamengIndex> indexes, DBObjects<DamengForeignKey> foreignKeys, DBObjects<DamengTrigger> triggers, DBObjects<DamengCheck> checks) {
        DamengCreateTableParam param = new DamengCreateTableParam();
        param.setTable(table);
        param.setChecks(checks);
        param.setColumns(columns);
        param.setIndexes(indexes);
        param.setTriggers(triggers);
        param.setForeignKeys(foreignKeys);
        return param;
    }

    /**
     * 修改表
     *
     * @param table       表对象
     * @param columns     列集合
     * @param indexes     索引集合
     * @param foreignKeys 外键集合
     * @param triggers    触发器集合
     * @param checks      检查约束集合
     */
    public void alterTable(DamengTable table, DamengColumns columns, DBObjects<DamengIndex> indexes, DBObjects<DamengForeignKey> foreignKeys, DBObjects<DamengTrigger> triggers, DBObjects<DamengCheck> checks) {
        DamengAlertTableParam param = new DamengAlertTableParam();
        param.setTable(table);
        param.setChecks(checks);
        param.setColumns(columns);
        param.setIndexes(indexes);
        param.setTriggers(triggers);
        param.setForeignKeys(foreignKeys);
        param.setPrimaryKeys(this.selectePrimaryKeys(table.getName()));
        param.setExistAutoIncrement(this.existAutoIncrement(table.getName()));
        this.client().alertTable(param);
    }

    /**
     * 修改表
     *
     * @param param 改表参数
     */
    public void alterTable(DamengAlertTableParam param) {
        this.client().alertTable(param);
    }

    /**
     * 构建改表参数
     *
     * @param table       表对象
     * @param columns     列集合
     * @param indexes     索引集合
     * @param foreignKeys 外键集合
     * @param triggers    触发器集合
     * @param checks      检查约束集合
     * @return 改表参数
     */
    public DamengAlertTableParam alterTableParam(DamengTable table, DamengColumns columns, DBObjects<DamengIndex> indexes, DBObjects<DamengForeignKey> foreignKeys, DBObjects<DamengTrigger> triggers, DBObjects<DamengCheck> checks) {
        DamengAlertTableParam param = new DamengAlertTableParam();
        param.setTable(table);
        param.setChecks(checks);
        param.setColumns(columns);
        param.setIndexes(indexes);
        param.setTriggers(triggers);
        param.setForeignKeys(foreignKeys);
        param.setPrimaryKeys(this.selectePrimaryKeys(table.getName()));
        param.setExistAutoIncrement(this.existAutoIncrement(table.getName()));
        return param;
    }

    /**
     * 查询主键列
     *
     * @param tableName 表名称
     * @return 主键列名称集合
     */
    public List<String> selectePrimaryKeys(String tableName) {
        return this.client().selectePrimaryKeys(this.schema(), tableName);
    }

    /**
     * 是否存在自增列
     *
     * @param tableName 表名称
     * @return 是否存在自增列
     */
    public boolean existAutoIncrement(String tableName) {
        return this.client().existAutoIncrement(this.schema(), tableName);
    }

    //public DamengTable selectFullTable(String tableName) {
    //    DamengSelectTableParam param = new DamengSelectTableParam();
    //    param.setDbName(this.schema());
    //    param.setTableName(tableName);
    //    return this.client().selectFullTable(param);
    //}

    /**
     * 是否存在表
     *
     * @param tableName 表名称
     * @return 是否存在表
     */
    @Deprecated
    public boolean existTable(String tableName) {
        return this.client().existTable(this.schema(), tableName);
    }

    /**
     * 重命名表
     *
     * @param oldTableName 旧表名称
     * @param newTableName 新表名称
     */
    public void renameTable(String oldTableName, String newTableName) {
        this.client().renameTable(this.schema(), oldTableName, newTableName);
    }

//    /**
//     * 重命名事件
//     *
//     * @param oldEventName 事件名称
//     * @param newEventName 新事件名称
//     */
//    public void renameEvent(String oldEventName, String newEventName) {
//        this.client().renameEvent(this.schema(), oldEventName, newEventName);
//    }

    /**
     * 清空表数据
     *
     * @param tableName 表名称
     */
    public void clearTable(String tableName) {
        this.client().clearTable(this.schema(), tableName);
    }

    /**
     * 截断表
     *
     * @param tableName 表名称
     */
    public void truncateTable(String tableName) {
        this.client().truncateTable(this.schema(), tableName);
    }

    /**
     * 删除表
     *
     * @param tableName 表名称
     */
    public void dropTable(String tableName) {
        this.client().dropTable(this.schema(), tableName);
    }

    /**
     * 执行sql
     *
     * @param sql sql语句
     * @return 执行结果集
     */
    public DBQueryResults<DamengExecuteResult> executeSql(String sql) {
        return this.client().executeSql(this.schema(), sql);
    }

    /**
     * 执行单条sql
     *
     * @param sql sql语句
     * @return 执行结果
     */
    public DamengExecuteResult executeSingleSql(String sql) {
        return this.client().executeSingleSql(this.schema(), sql);
    }

    /**
     * 解析sql执行计划
     *
     * @param sql sql语句
     * @return 解析结果集
     */
    public DBQueryResults<DamengExplainResult> explainSql(String sql) {
        return this.client().explainSql(this.schema(), sql);
    }

    /**
     * 创建函数
     *
     * @param function 函数对象
     */
    public void createFunction(DamengFunction function) {
        DamengCreateFunctionParam param = new DamengCreateFunctionParam();
        param.setFunction(function);
        param.setSchema(this.schema());
        this.client().createFunction(param);
    }

    /**
     * 修改函数
     *
     * @param function 函数对象
     */
    public void alertFunction(DamengFunction function) {
        DamengAlertFunctionParam param = new DamengAlertFunctionParam();
        param.setFunction(function);
        param.setSchema(this.schema());
        this.client().alertFunction(param);
    }

    /**
     * 删除函数
     *
     * @param function 函数对象
     */
    public void dropFunction(DamengFunction function) {
        function.setSchema(this.schema());
        this.client().dropFunction(function);
    }

    /**
     * 查询存储过程
     *
     * @param procedureName 存储过程名称
     * @return 存储过程对象
     */
    public DamengProcedure selectProcedure(String procedureName) {
        return this.client().selectProcedure(this.schema(), procedureName);
    }

    /**
     * 创建存储过程
     *
     * @param procedure 存储过程对象
     */
    public void createProcedure(DamengProcedure procedure) {
        DamengCreateProcedureParam param = new DamengCreateProcedureParam();
        param.setProcedure(procedure);
        param.setSchema(this.schema());
        this.client().createProcedure(param);
    }

    /**
     * 修改存储过程
     *
     * @param procedure 存储过程对象
     */
    public void alertProcedure(DamengProcedure procedure) {
        DamengAlertProcedureParam param = new DamengAlertProcedureParam();
        param.setProcedure(procedure);
        param.setSchema(this.schema());
        this.client().alertProcedure(param);
    }

    /**
     * 删除存储过程
     *
     * @param procedure 存储过程对象
     */
    public void dropProcedure(DamengProcedure procedure) {
        this.client().dropProcedure(this.schema(), procedure);
    }

    /**
     * 查询函数
     *
     * @param functionName 函数名称
     * @return 函数对象
     */
    public DamengFunction selectFunction(String functionName) {
        return this.client().selectFunction(this.schema(), functionName);
    }

    /**
     * 查询视图
     *
     * @param viewName 视图名称
     * @return 视图对象
     */
    public DamengView selectView(String viewName) {
        return this.client().selectView(this.schema(), viewName);
    }

    /**
     * 查询表
     *
     * @param tableName 表名称
     * @return 表对象
     */
    public DamengTable selectTable(String tableName) {
        return this.client().selectTable(this.schema(), tableName);
    }

    /**
     * 创建视图
     *
     * @param view 视图对象
     */
    public void createView(DamengView view) {
        DamengCreateViewParam param = new DamengCreateViewParam();
        param.setView(view);
        param.setSchema(this.schema());
        this.client().createView(param);
    }

    /**
     * 修改视图
     *
     * @param view 视图对象
     */
    public void alertView(DamengView view) {
        DamengAlertViewParam param = new DamengAlertViewParam();
        param.setView(view);
        param.setSchema(this.schema());
        this.client().alertView(param);
    }

    /**
     * 删除视图
     *
     * @param view 视图对象
     */
    public void dropView(DamengView view) {
        view.setSchema(this.schema());
        this.client().dropView(view);
    }

    /**
     * 是否存在视图
     *
     * @param viewName 视图名称
     * @return 是否存在视图
     */
    public boolean existView(String viewName) {
        return this.client().existView(this.schema(), viewName);
    }

    @Override
    public boolean itemVisible() {
        return this.isVisible();
    }

    //@Override
    //public synchronized void doFilter(RichTreeItemFilter itemFilter) {
    //    super.doFilter(itemFilter);
    //    this.refresh();
    //}

    //    public DamengEvent selectEvent(String eventName) {
    //        return this.client().selectEvent(this.schema(), eventName);
    //    }

    //    public void alertEvent(DamengEvent event) {
    //        this.client().alertEvent(this.schema(), event);
    //    }
    //
    //    public void createEvent(DamengEvent event) {
    //        this.client().createEvent(this.schema(), event);
    //    }

    //public void dropEvent(DamengEvent event) {
    //    this.client().dropEvent(this.schema(), event);
    //}

    /**
     * 是否支持检查约束特性
     *
     * @return 是否支持
     */
    public boolean isSupportCheckFeature() {
        return this.client().isSupportCheckFeature();
    }

    /**
     * 获取数据库方言
     *
     * @return 数据库方言
     */
    public DBDialect dialect() {
        return this.client().dialect();
    }

    /**
     * 删除记录
     *
     * @param param 删除记录参数
     * @return 影响行数
     */
    public int deleteRecord(DamengDeleteRecordParam param) {
        return this.client().deleteRecord(param);
    }

    /**
     * 获取表检查约束
     *
     * @param tableName 表名称
     * @return 检查约束集合
     */
    public DBObjects<DamengCheck> checks(String tableName) {
        return this.client().checks(this.schema(), tableName);
    }

    /**
     * 获取表触发器
     *
     * @param tableName 表名称
     * @return 触发器集合
     */
    public DBObjects<DamengTrigger> triggers(String tableName) {
        return this.client().selectTriggers(this.schema(), tableName);
    }

    /**
     * 获取表列信息
     *
     * @param tableName 表名称
     * @return 表列集合
     */
    public DamengColumns columns(String tableName) {
        DamengSelectColumnParam param = new DamengSelectColumnParam();
        param.setSchema(this.schema());
        param.setTableName(tableName);
        return this.client().selectColumns(param);
    }

    /**
     * 获取表索引
     *
     * @param tableName 表名称
     * @return 索引集合
     */
    public DBObjects<DamengIndex> indexes(String tableName) {
        return this.client().indexes(this.schema(), tableName);
    }

    /**
     * 获取表外键
     *
     * @param tableName 表名称
     * @return 外键集合
     */
    public DBObjects<DamengForeignKey> foreignKeys(String tableName) {
        return this.client().foreignKeys(this.schema(), tableName);
    }

    /**
     * 查询单条记录
     *
     * @param param 查询记录参数
     * @return 记录
     */
    public DamengRecord selectRecord(DamengSelectRecordParam param) {
        return this.client().selectRecord(param);
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     */
    public DamengConnect dbConnect() {
        return this.client().getDbConnect();
    }

    /**
     * 克隆表
     *
     * @param tableName     表名称
     * @param includeRecord 是否包含记录
     * @return 克隆后的表名称
     */
    public String cloneTable(String tableName, boolean includeRecord) {
        return this.client().cloneTable(this.schema(), tableName, includeRecord);
    }
}
