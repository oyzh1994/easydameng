package cn.oyzh.easydameng.data.dameng.handler;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.DamengHelper;
import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.column.DamengSelectColumnParam;
import cn.oyzh.easydameng.dameng.record.DamengRecord;
import cn.oyzh.easydameng.dameng.record.DamengSelectRecordParam;
import cn.oyzh.easydameng.util.DamengDataUtil;
import cn.oyzh.fx.db.DBDialect;
import cn.oyzh.fx.db.data.dto.DBDataTransportObject;
import cn.oyzh.fx.db.data.handler.DBDataTransportHandler;
import cn.oyzh.fx.db.util.DBUtil;

import java.util.List;

/**
 * 达梦数据传输处理器
 *
 * @author oyzh
 * @since 2024/09/06
 */
public class DamengDataTransportHandler extends DBDataTransportHandler<String> {

    /**
     * 来源客户端
     */
    protected ShellDamengClient sourceClient;

    /**
     * 目标客户端
     */
    protected ShellDamengClient targetClient;

    /**
     * 视图
     */
    protected List<DBDataTransportObject> views;

    /**
     * 表
     */
    protected List<DBDataTransportObject> tables;

    /**
     * 触发器
     */
    protected List<DBDataTransportObject> triggers;

    /**
     * 函数
     */
    protected List<DBDataTransportObject> functions;

    /**
     * 过程
     */
    protected List<DBDataTransportObject> procedures;

    /**
     * 构造数据传输处理器
     */
    public DamengDataTransportHandler() {
        super(DBDialect.DAMENG);
    }

    @Override
    public void doTransport() throws Exception {
        this.message("Transport Starting");
        try {
            if (CollectionUtil.isNotEmpty(this.tables)) {
                for (DBDataTransportObject table : this.tables) {
                    this.transportTable(table.getName());
                }
            }
            if (CollectionUtil.isNotEmpty(this.views)) {
                for (DBDataTransportObject view : this.views) {
                    this.transportView(view.getName());
                }
            }
            if (CollectionUtil.isNotEmpty(this.functions)) {
                for (DBDataTransportObject function : this.functions) {
                    this.transportFunction(function.getName());
                }
            }
            if (CollectionUtil.isNotEmpty(this.procedures)) {
                for (DBDataTransportObject procedure : this.procedures) {
                    this.transportProcedure(procedure.getName());
                }
            }
            if (CollectionUtil.isNotEmpty(this.triggers)) {
                for (DBDataTransportObject trigger : this.triggers) {
                    this.transportTrigger(trigger.getName());
                }
            }
        } catch (Exception ex) {
            this.exception(ex);
        } finally {
            this.message("Transport Finished");
        }
    }

    /**
     * 传输表
     *
     * @param tableName 表名称
     * @throws Exception 异常
     */
    private void transportTable(String tableName) throws Exception {
        this.checkInterrupt();
        // 删除表
        String dropTable = "DROP TABLE IF EXISTS " + DBUtil.wrap(tableName, DBDialect.DAMENG) + ";";
        this.targetClient.executeSqlSimple(this.targetDatabase, dropTable);
        this.message("Drop Table " + tableName);
        this.processedIncr();

        // 创建表
        String createTable = this.sourceClient.showCreateTable(this.sourceDatabase, tableName);
        // TODO: 去除特定架构
        createTable = createTable.replaceAll("CREATE\\s+TABLE\\s+\"[^\"]+\"\\.", "CREATE TABLE ");
        this.targetClient.executeSqlSimple(this.targetDatabase, createTable);
        this.message("Create Table " + tableName);
        this.processedIncr();

        // 传输表
        this.message("Transport Table " + tableName + " Starting");
        List<DamengColumn> columns = this.sourceClient.selectColumns(new DamengSelectColumnParam(this.sourceDatabase, tableName));
        DamengColumns dbColumns = new DamengColumns(columns);
        if (dbColumns.hasAutoIncrement()) {
            try {
                String line0 = "SET IDENTITY_INSERT " + DBUtil.wrap(tableName, DBDialect.DAMENG) + " ON;";
                this.targetClient.executeSqlSimple(this.targetDatabase, line0);
            } catch (Exception ex) {
                if (!DamengHelper.isIdentityError(ex)) {
                    ex.printStackTrace();
                }
            }
        }
        long start = 0;
        while (true) {
            this.checkInterrupt();
            DamengSelectRecordParam param = new DamengSelectRecordParam();
            param.setStart(start);
            param.setReadonly(true);
            param.setTableName(tableName);
            param.setSchema(this.sourceDatabase);
            param.setLimit((long) this.selectLimit);
            List<DamengRecord> records = this.sourceClient.selectRecords(param);
            if (CollectionUtil.isEmpty(records)) {
                break;
            }
            List<String> list = DamengDataUtil.toInsertSql(dbColumns, records, true);
            this.addInsert(list);
            start += this.selectLimit;
        }
        // 收尾批量插入
        this.doBatchInsert();
        if (dbColumns.hasAutoIncrement()) {
            try {
                String line1 = "SET IDENTITY_INSERT " + DBUtil.wrap(tableName, DBDialect.DAMENG) + " OFF;";
                this.targetClient.executeSqlSimple(this.targetDatabase, line1);
            } catch (Exception ex) {
                if (!DamengHelper.isIdentityError(ex)) {
                    ex.printStackTrace();
                }
            }
        }
        this.message("Transport Table " + tableName + " Finished");
    }

    /**
     * 传输视图
     *
     * @param viewName 视图名称
     * @throws InterruptedException 异常
     */
    private void transportView(String viewName) throws InterruptedException {
        this.checkInterrupt();
        // 删除视图
        String dropTable = "DROP VIEW IF EXISTS " + DBUtil.wrap(viewName, DBDialect.DAMENG) + ";";
        this.targetClient.executeSqlSimple(this.targetDatabase, dropTable);
        this.message("Drop View " + viewName);
        this.processedIncr();

        // 创建视图
        String createView = this.sourceClient.showCreateView(this.sourceDatabase, viewName);
        this.targetClient.executeSqlSimple(this.targetDatabase, createView);
        this.message("Create View " + viewName);
        this.processedIncr();
    }

    /**
     * 传输函数
     *
     * @param functionName 函数名称
     * @throws InterruptedException 异常
     */
    private void transportFunction(String functionName) throws InterruptedException {
        this.checkInterrupt();
        // 删除函数
        String dropTable = "DROP FUNCTION IF EXISTS " + DBUtil.wrap(functionName, DBDialect.DAMENG) + ";";
        this.targetClient.executeSqlSimple(this.targetDatabase, dropTable);
        this.message("Drop Function " + functionName);
        this.processedIncr();

        // 创建函数
        String createView = this.sourceClient.showCreateFunction(this.sourceDatabase, functionName);
        this.targetClient.executeSqlSimple(this.targetDatabase, createView);
        this.message("Create Function " + functionName);
        this.processedIncr();
    }

    /**
     * 传输过程
     *
     * @param procedureName 过程名称
     * @throws InterruptedException 异常
     */
    private void transportProcedure(String procedureName) throws InterruptedException {
        this.checkInterrupt();
        // 删除过程
        String dropTable = "DROP PROCEDURE IF EXISTS " + DBUtil.wrap(procedureName, DBDialect.DAMENG) + ";";
        this.targetClient.executeSqlSimple(this.targetDatabase, dropTable);
        this.message("Drop Procedure " + procedureName);
        this.processedIncr();

        // 创建过程
        String createView = this.sourceClient.showCreateProcedure(this.sourceDatabase, procedureName);
        this.targetClient.executeSqlSimple(this.targetDatabase, createView);
        this.message("Create Procedure " + procedureName);
        this.processedIncr();
    }

    /**
     * 传输触发器
     *
     * @param triggerName 触发器名称
     * @throws InterruptedException 异常
     */
    private void transportTrigger(String triggerName) throws InterruptedException {
        this.checkInterrupt();
        // 删除触发器
        String dropTable = "DROP TRIGGER IF EXISTS " + DBUtil.wrap(triggerName, DBDialect.DAMENG) + ";";
        this.targetClient.executeSqlSimple(this.targetDatabase, dropTable);
        this.message("Drop Trigger " + triggerName);
        this.processedIncr();

        // 创建触发器
        String createView = this.sourceClient.showCreateTrigger(this.sourceDatabase, triggerName);
        this.targetClient.executeSqlSimple(this.targetDatabase, createView);
        this.message("Create Trigger " + triggerName);
        this.processedIncr();
    }

    @Override
    public void doBatchInsert(List<String> list, boolean parallel) throws Exception {
        try {
            int result = this.targetClient.insertBatch(this.targetDatabase, list, parallel);
            this.processedIncr(result);
        } catch (Exception ex) {
            this.processedDecr(list.size());
            throw ex;
        }
    }

    /**
     * 获取来源客户端
     *
     * @return 来源客户端
     */
    public ShellDamengClient getSourceClient() {
        return sourceClient;
    }

    /**
     * 设置来源客户端
     *
     * @param sourceClient 来源客户端
     */
    public void setSourceClient(ShellDamengClient sourceClient) {
        this.sourceClient = sourceClient;
    }

    /**
     * 获取目标客户端
     *
     * @return 目标客户端
     */
    public ShellDamengClient getTargetClient() {
        return targetClient;
    }

    /**
     * 设置目标客户端
     *
     * @param targetClient 目标客户端
     */
    public void setTargetClient(ShellDamengClient targetClient) {
        this.targetClient = targetClient;
    }

    /**
     * 获取视图集合
     *
     * @return 视图集合
     */
    public List<DBDataTransportObject> getViews() {
        return views;
    }

    /**
     * 设置视图集合
     *
     * @param views 视图集合
     */
    public void setViews(List<DBDataTransportObject> views) {
        this.views = views;
    }

    /**
     * 获取表集合
     *
     * @return 表集合
     */
    public List<DBDataTransportObject> getTables() {
        return tables;
    }

    /**
     * 设置表集合
     *
     * @param tables 表集合
     */
    public void setTables(List<DBDataTransportObject> tables) {
        this.tables = tables;
    }

    /**
     * 获取触发器集合
     *
     * @return 触发器集合
     */
    public List<DBDataTransportObject> getTriggers() {
        return triggers;
    }

    /**
     * 设置触发器集合
     *
     * @param triggers 触发器集合
     */
    public void setTriggers(List<DBDataTransportObject> triggers) {
        this.triggers = triggers;
    }

    /**
     * 获取函数集合
     *
     * @return 函数集合
     */
    public List<DBDataTransportObject> getFunctions() {
        return functions;
    }

    /**
     * 设置函数集合
     *
     * @param functions 函数集合
     */
    public void setFunctions(List<DBDataTransportObject> functions) {
        this.functions = functions;
    }

    /**
     * 获取过程集合
     *
     * @return 过程集合
     */
    public List<DBDataTransportObject> getProcedures() {
        return procedures;
    }

    /**
     * 设置过程集合
     *
     * @param procedures 过程集合
     */
    public void setProcedures(List<DBDataTransportObject> procedures) {
        this.procedures = procedures;
    }
}

