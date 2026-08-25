package cn.oyzh.easydameng.trees.database;

import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.easydameng.controller.data.DamengDataDumpController;
import cn.oyzh.easydameng.controller.data.DamengRunSqlFileController;
import cn.oyzh.easydameng.controller.database.DamengDatabaseUpdateController;
import cn.oyzh.easydameng.db.DBDatabase;
import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.dameng.DamengClient;
import cn.oyzh.easydameng.dameng.check.DamengChecks;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.column.DamengSelectColumnParam;
import cn.oyzh.easydameng.dameng.event.DamengEvent;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKeys;
import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.easydameng.dameng.index.DamengIndexes;
import cn.oyzh.easydameng.dameng.procedure.DamengProcedure;
import cn.oyzh.easydameng.dameng.query.DamengExecuteResult;
import cn.oyzh.easydameng.dameng.query.DamengExplainResult;
import cn.oyzh.easydameng.dameng.query.DamengQueryResults;
import cn.oyzh.easydameng.dameng.record.DamengDeleteRecordParam;
import cn.oyzh.easydameng.dameng.record.DamengRecord;
import cn.oyzh.easydameng.dameng.record.DamengSelectRecordParam;
import cn.oyzh.easydameng.dameng.table.DamengAlertTableParam;
import cn.oyzh.easydameng.dameng.table.DamengCreateTableParam;
import cn.oyzh.easydameng.dameng.table.DamengSelectTableParam;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.dameng.trigger.DamengTriggers;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.trees.DBTreeItem;
import cn.oyzh.easydameng.trees.connect.DBConnectTreeItem;
import cn.oyzh.easydameng.trees.event.DamengEventTreeItem;
import cn.oyzh.easydameng.trees.event.DamengEventsTreeItem;
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
 * db树database节点
 *
 * @author oyzh
 * @since 2023/12/12
 */
public class DamengDatabaseTreeItem extends DBTreeItem<DamengDatabaseTreeItemValue> {

    /**
     * 当前值
     */
    private final DBDatabase value;

    public DBDatabase value() {
        return value;
    }

    public DamengDatabaseTreeItem(DBDatabase database, RichTreeView treeView) {
        super(treeView);
        super.setSortable(false);
        super.setFilterable(true);
        this.value = database;
        this.setValue(new DamengDatabaseTreeItemValue(this));
    }

    @Override
    public DBConnectTreeItem parent() {
        return (DBConnectTreeItem) super.parent();
    }

    public String dbName() {
        return this.value.getName();
    }

    public String userName() {
        return this.info().getUser();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (!this.isChildEmpty()) {
            FXMenuItem closeDB = MenuItemHelper.closeDatabase( this::closeDB);
            items.add(closeDB);
        }
        FXMenuItem editDB = MenuItemHelper.editDatabase( this::editDB);
        items.add(editDB);
        FXMenuItem dropDB = MenuItemHelper.deleteDatabase( this::delete);
        items.add(dropDB);
        FXMenuItem dumpData = MenuItemHelper.dumpData( this::dump);
        items.add(dumpData);
        FXMenuItem runSqlFile = MenuItemHelper.runSqlFile( this::runSqlFile);
        items.add(runSqlFile);
        // FXMenuItem dbInfo = MenuItemHelper.databaseInfo( this::dbInfo);
        // items.add(dbInfo);
        return items;
    }

    /**
     * 运行sql文件
     */
    private void runSqlFile() {
        StageAdapter fxView = StageManager.parseStage(DamengRunSqlFileController.class, this.window());
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.dbName());
        fxView.setProp("dbClient", this.client());
        fxView.display();
    }

    /**
     * 转储
     */
    private void dump() {
        StageAdapter fxView = StageManager.parseStage(DamengDataDumpController.class, this.window());
        fxView.setProp("dumpType", 1);
        fxView.setProp("dbInfo", this.info());
        fxView.setProp("dbName", this.dbName());
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
                    if (MessageBox.confirm(I18nHelper.deleteDatabase() + "[" + this.dbName() + "]")) {
                        if (this.parent().dropDatabase(this.dbName())) {
                            DamengEventUtil.databaseDropped(this);
                            super.remove();
                        } else {
                            MessageBox.warn(I18nHelper.operationFail());
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
        StageAdapter fxView = StageManager.parseStage(DamengDatabaseUpdateController.class, this.window());
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
        DamengEventUtil.databaseClosed(this);
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
                        MessageBox.error(ex.getMessage());
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
     * @return 过程节点列表
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

    public DamengEventsTreeItem getEventTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof DamengEventsTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    public List<DamengEventTreeItem> getEventChild() {
        List<DamengEventTreeItem> list = new ArrayList<>();
        for (RichTreeItem<?> child : this.getEventTypeChild().richChildren()) {
            if (child instanceof DamengEventTreeItem treeItem) {
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
     * 获取db客户端
     *
     * @return db客户端
     */
    public DamengClient client() {
        return this.parent().getClient();
    }

    /**
     * 获取db信息
     *
     * @return db信息
     */
    public DamengConnect info() {
        return this.parent().value();
    }

    public Integer tableSize() {
        try {
            return this.client().tableSize(this.dbName());
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
        return 0;
    }

    public Integer viewSize() {
        return this.client().viewSize(this.dbName());
    }

    public String infoName() {
        return this.info().getName();
    }

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

    public void createTable(DamengTable table, DamengColumns columns, DamengIndexes indexes, DamengForeignKeys foreignKeys, DamengTriggers triggers, DamengChecks checks) {
        DamengCreateTableParam param = new DamengCreateTableParam();
        param.setTable(table);
        param.setChecks(checks);
        param.setColumns(columns);
        param.setIndexes(indexes);
        param.setTriggers(triggers);
        param.setForeignKeys(foreignKeys);
        this.client().createTable(param);
    }

    public void createTable(DamengCreateTableParam param) {
        this.client().createTable(param);
    }

    public DamengCreateTableParam createTableParam(DamengTable table, DamengColumns columns, DamengIndexes indexes, DamengForeignKeys foreignKeys, DamengTriggers triggers, DamengChecks checks) {
        DamengCreateTableParam param = new DamengCreateTableParam();
        param.setTable(table);
        param.setChecks(checks);
        param.setColumns(columns);
        param.setIndexes(indexes);
        param.setTriggers(triggers);
        param.setForeignKeys(foreignKeys);
        return param;
    }

    public void alterTable(DamengTable table, DamengColumns columns, DamengIndexes indexes, DamengForeignKeys foreignKeys, DamengTriggers triggers, DamengChecks checks) {
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

    public void alterTable(DamengAlertTableParam param) {
        this.client().alertTable(param);
    }

    public DamengAlertTableParam alterTableParam(DamengTable table, DamengColumns columns, DamengIndexes indexes, DamengForeignKeys foreignKeys, DamengTriggers triggers, DamengChecks checks) {
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

    public List<String> selectePrimaryKeys(String tableName) {
        return this.client().selectePrimaryKeys(this.dbName(), tableName);
    }

    public boolean existAutoIncrement(String tableName) {
        return this.client().existAutoIncrement(this.dbName(), tableName);
    }

    public DamengTable selectFullTable(String tableName) {
        DamengSelectTableParam param = new DamengSelectTableParam();
        param.setDbName(this.dbName());
        param.setTableName(tableName);
        return this.client().selectFullTable(param);
    }

    @Deprecated
    public boolean existTable(String tableName) {
        return this.client().existTable(this.dbName(), tableName);
    }

    public void renameTable(String oldTableName, String newTableName) {
        this.client().renameTable(this.dbName(), oldTableName, newTableName);
    }

    /**
     * 重命名事件
     *
     * @param oldEventName 事件名称
     * @param newEventName 新事件名称
     */
    public void renameEvent(String oldEventName, String newEventName) {
        this.client().renameEvent(this.dbName(), oldEventName, newEventName);
    }

    public void clearTable(String tableName) {
        this.client().clearTable(this.dbName(), tableName);
    }

    public void truncateTable(String tableName) {
        this.client().truncateTable(this.dbName(), tableName);
    }

    public void dropTable(String tableName) {
        this.client().dropTable(this.dbName(), tableName);
    }

    public DamengQueryResults<DamengExecuteResult> executeSql(String sql) {
        return this.client().executeSql(this.dbName(), sql);
    }

    public DamengExecuteResult executeSingleSql(String sql) {
        return this.client().executeSingleSql(this.dbName(), sql);
    }

    public DamengQueryResults<DamengExplainResult> explainSql(String sql) {
        return this.client().explainSql(this.dbName(), sql);
    }

    public void createFunction(DamengFunction function) {
        this.client().createFunction(this.dbName(), function);
    }

    public void dropFunction(DamengFunction function) {
        this.client().dropFunction(this.dbName(), function);
    }

    public DamengProcedure selectProcedure(String procedureName) {
        return this.client().selectProcedure(this.dbName(), procedureName);
    }

    public void alertProcedure(DamengProcedure procedure) {
        this.client().alertProcedure(this.dbName(), procedure);
    }

    public void createProcedure(DamengProcedure procedure) {
        this.client().createProcedure(this.dbName(), procedure);
    }

    public void dropProcedure(DamengProcedure procedure) {
        this.client().dropProcedure(this.dbName(), procedure);
    }

    public DamengFunction selectFunction(String functionName) {
        return this.client().selectFunction(this.dbName(), functionName);
    }

    public void alertFunction(DamengFunction function) {
        this.client().alertFunction(this.dbName(), function);
    }

    public DamengView selectView(String viewName) {
        return this.client().view(this.dbName(), viewName);
    }

    public DamengTable selectTable(String tableName) {
        return this.client().selectTable(this.dbName(), tableName);
    }

    public void createView(DamengView view) {
        this.client().createView(this.dbName(), view);
    }

    public void alertView(DamengView view) {
        this.client().alertView(this.dbName(), view);
    }

    public void dropView(DamengView view) {
        this.client().dropView(this.dbName(), view);
    }

    public boolean existView(String viewName) {
        return this.client().existView(this.dbName(), viewName);
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

    public DamengEvent selectEvent(String eventName) {
        return this.client().selectEvent(this.dbName(), eventName);
    }

    public void alertEvent(DamengEvent event) {
        this.client().alertEvent(this.dbName(), event);
    }

    public void createEvent(DamengEvent event) {
        this.client().createEvent(this.dbName(), event);
    }

    public void dropEvent(DamengEvent event) {
        this.client().dropEvent(this.dbName(), event);
    }

    public boolean isSupportCheckFeature() {
        return this.client().isSupportCheckFeature();
    }

    public DBDialect dialect() {
        return this.client().dialect();
    }

    public int deleteRecord(DamengDeleteRecordParam param) {
        return this.client().deleteRecord(param);
    }

    public DamengChecks checks(String tableName) {
        return this.client().checks(this.dbName(), tableName);
    }

    public DamengTriggers triggers(String tableName) {
        return this.client().selectTriggers(this.dbName(), tableName);
    }

    public DamengColumns columns(String tableName) {
        DamengSelectColumnParam param = new DamengSelectColumnParam();
        param.setDbName(this.dbName());
        param.setTableName(tableName);
        return this.client().selectColumns(param);
    }

    public DamengIndexes indexes(String tableName) {
        return this.client().indexes(this.dbName(), tableName);
    }

    public DamengForeignKeys foreignKeys(String tableName) {
        return this.client().foreignKeys(this.dbName(), tableName);
    }

    public DamengRecord selectRecord(DamengSelectRecordParam param) {
        return this.client().selectRecord(param);
    }

    public DamengConnect dbConnect() {
        return this.client().getDbConnect();
    }

    public String cloneTable(String tableName, boolean includeRecord) {
        return this.client().cloneTable(this.dbName(), tableName, includeRecord);
    }
}
