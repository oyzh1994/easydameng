package cn.oyzh.easydameng.dameng;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.IOUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.condition.DamengConditionUtil;
import cn.oyzh.easydameng.dameng.check.DamengCheck;
import cn.oyzh.easydameng.dameng.check.DamengChecks;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.column.DamengSelectColumnParam;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKey;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKeys;
import cn.oyzh.easydameng.dameng.function.DamengAlertFunctionParam;
import cn.oyzh.easydameng.dameng.function.DamengCreateFunctionParam;
import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.easydameng.dameng.function.DamengSelectFunctionParam;
import cn.oyzh.easydameng.dameng.generator.function.DamengFunctionAlertSqlGenerator;
import cn.oyzh.easydameng.dameng.generator.function.DamengFunctionCreateSqlGenerator;
import cn.oyzh.easydameng.dameng.generator.procedure.DamengProcedureAlertSqlGenerator;
import cn.oyzh.easydameng.dameng.generator.procedure.DamengProcedureCreateSqlGenerator;
import cn.oyzh.easydameng.dameng.generator.table.DamengTableAlertSqlGenerator;
import cn.oyzh.easydameng.dameng.generator.table.DamengTableCreateSqlGenerator;
import cn.oyzh.easydameng.dameng.generator.view.DamengViewAlertSqlGenerator;
import cn.oyzh.easydameng.dameng.generator.view.DamengViewCreateSqlGenerator;
import cn.oyzh.easydameng.dameng.index.DamengIndex;
import cn.oyzh.easydameng.dameng.index.DamengIndexes;
import cn.oyzh.easydameng.dameng.procedure.DamengAlertProcedureParam;
import cn.oyzh.easydameng.dameng.procedure.DamengCreateProcedureParam;
import cn.oyzh.easydameng.dameng.procedure.DamengProcedure;
import cn.oyzh.easydameng.dameng.procedure.DamengSelectProcedureParam;
import cn.oyzh.easydameng.dameng.query.DamengExecuteResult;
import cn.oyzh.easydameng.dameng.query.DamengExplainResult;
import cn.oyzh.easydameng.dameng.query.DamengQueryResults;
import cn.oyzh.easydameng.dameng.record.DamengDeleteRecordParam;
import cn.oyzh.easydameng.dameng.record.DamengInsertRecordParam;
import cn.oyzh.easydameng.dameng.record.DamengRecord;
import cn.oyzh.easydameng.dameng.record.DamengRecordData;
import cn.oyzh.easydameng.dameng.record.DamengRecordFilter;
import cn.oyzh.easydameng.dameng.record.DamengRecordPrimaryKey;
import cn.oyzh.easydameng.dameng.record.DamengSelectRecordParam;
import cn.oyzh.easydameng.dameng.record.DamengUpdateRecordParam;
import cn.oyzh.easydameng.dameng.routine.DamengRoutineParam;
import cn.oyzh.easydameng.dameng.table.DamengAlertTableParam;
import cn.oyzh.easydameng.dameng.table.DamengCreateTableParam;
import cn.oyzh.easydameng.dameng.table.DamengSelectTableParam;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.dameng.trigger.DamengTrigger;
import cn.oyzh.easydameng.dameng.trigger.DamengTriggers;
import cn.oyzh.easydameng.dameng.view.DamengAlertViewParam;
import cn.oyzh.easydameng.dameng.view.DamengCreateViewParam;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.data.db.DBConnConfig;
import cn.oyzh.easydameng.data.db.DBConnState;
import cn.oyzh.easydameng.data.db.DBConnectionManager;
import cn.oyzh.easydameng.data.db.DBDatabase;
import cn.oyzh.easydameng.data.db.DBDialect;
import cn.oyzh.easydameng.data.db.DBFeature;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.exception.DBException;
import cn.oyzh.easydameng.exception.ReadonlyOperationException;
import cn.oyzh.easydameng.sql.DBSqlParser;
import cn.oyzh.easydameng.util.DBUtil;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * db客户端封装
 *
 * @author oyzh
 * @since 2023/11/06
 */
public class ShellDamengClient {

    /**
     * db信息
     */
    protected final DamengConnect dbConnect;

    //    /**
    //     * ssh端口转发器
    //     */
    //    private SSHForwarder sshForwarder;

    /**
     * 数据库连接管理器
     */
    protected final DBConnectionManager connectionManager = new DBConnectionManager();

    protected final DBConnConfig connConfig = new DBConnConfig();

    private boolean isInvalid(Connection connection) throws SQLException {
        if (connection == null || connection.isClosed()) {
            return true;
        }
        return !connection.isValid(10);
    }

    public Connection connection() throws SQLException, ClassNotFoundException {
        Connection connection = this.connectionManager.getServerConnection();
        if (this.isInvalid(connection)) {
            connection = this.initConnection(this.connConfig, null, this.dbConnect.getUser(), this.dbConnect.getPassword());
            this.connectionManager.setServerConnection(connection);
        }
        return connection;
    }

    public Connection connection(String dbName) throws SQLException, ClassNotFoundException {
        Connection connection = this.connectionManager.getConnection(dbName);
        if (this.isInvalid(connection)) {
            connection = this.initConnection(this.connConfig, dbName, this.dbConnect.getUser(), this.dbConnect.getPassword());
            this.connectionManager.addConnection(dbName, connection);
        }
        connection.setAutoCommit(true);
        return connection;
    }

    public Connection connection(String dbName, String schema) throws SQLException, ClassNotFoundException {
        if (schema == null) {
            return this.connection(dbName);
        }
        Connection connection = this.connectionManager.getSchemaConnection(dbName, schema);
        if (this.isInvalid(connection)) {
            connection = this.initConnection(this.connConfig, dbName, this.dbConnect.getUser(), this.dbConnect.getPassword());
            this.connectionManager.addSchemaConnection(dbName, schema, connection);
        }
        connection.setAutoCommit(true);
        return connection;
    }

    public Connection functionConnection(String dbName) throws SQLException, ClassNotFoundException {
        return this.functionConnection(dbName, null);
    }

    public Connection functionConnection(String dbName, String schema) throws SQLException, ClassNotFoundException {
        Connection connection = this.connectionManager.getFunctionConnection(dbName, schema);
        if (this.isInvalid(connection)) {
            connection = this.initConnection(this.connConfig, dbName, this.dbConnect.getUser(), this.dbConnect.getPassword());
            this.connectionManager.addFunctionConnection(dbName, schema, connection);
        }
        connection.setAutoCommit(true);
        return connection;
    }

    public Connection procedureConnection(String dbName) throws SQLException, ClassNotFoundException {
        return this.procedureConnection(dbName, null);
    }

    public Connection procedureConnection(String dbName, String schema) throws SQLException, ClassNotFoundException {
        Connection connection = this.connectionManager.getProcedureConnection(dbName, schema);
        if (this.isInvalid(connection)) {
            connection = this.initConnection(this.connConfig, dbName, this.dbConnect.getUser(), this.dbConnect.getPassword());
            this.connectionManager.addProcedureConnection(dbName, schema, connection);
        }
        connection.setAutoCommit(true);
        return connection;
    }

    public Connection newConnection(String dbName) throws SQLException, ClassNotFoundException {
        Connection connection = this.initConnection(this.connConfig, dbName, this.dbConnect.getUser(), this.dbConnect.getPassword());
        connection.setAutoCommit(true);
        return connection;
    }

    protected Connection initConnection(DBConnConfig connConfig, String dbName, String user, String password) throws ClassNotFoundException, SQLException {
        // 加载JDBC驱动
        Class.forName("dm.jdbc.driver.DmDriver");
        String host = connConfig.getConnectionString();
        if (dbName != null) {
            host += dbName;
        }
        // 创建数据库连接
        return DriverManager.getConnection(host, user, password);
    }

    /**
     * 连接状态
     */
    private final ReadOnlyObjectWrapper<DBConnState> state = new ReadOnlyObjectWrapper<>();

    /**
     * 属性列表
     */
    private Map<String, Object> properties;

    /**
     * 获取属性
     *
     * @param key 键
     * @param <T> 属性类型
     * @return 属性
     */
    protected <T> T getProperty(String key) {
        return this.properties == null || key == null ? null : (T) this.properties.get(key);
    }

    /**
     * 是否有此属性
     *
     * @param key 键
     * @return 结果
     */
    protected boolean hasProperty(String key) {
        return this.properties != null && this.properties.containsKey(key);
    }

    /**
     * 添加属性
     *
     * @param key   键
     * @param value 值
     */
    protected void putProperty(String key, Object value) {
        if (key != null && value != null) {
            if (this.properties == null) {
                this.properties = new HashMap<>();
            }
            this.properties.put(key, value);
        }
    }

    /**
     * 获取连接状态
     *
     * @return 连接状态
     */
    public DBConnState state() {
        return this.stateProperty().get();
    }

    /**
     * 连接状态属性
     *
     * @return 连接状态属性
     */
    public ReadOnlyObjectProperty<DBConnState> stateProperty() {
        return this.state.getReadOnlyProperty();
    }

    public ShellDamengClient(DamengConnect dbInfo) {
        this.dbConnect = dbInfo;
        // if (dbInfo.isSSHForward()) {
        //     this.sshForwarder = new SSHForwarder(dbInfo.getSshConfig());
        // }
        this.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue) {
                    case CLOSED -> DamengEventUtil.connectionClosed(this);
                    case CONNECTED -> DamengEventUtil.connectionConnected(this);
                }
            }
        });
    }

    /**
     * 是否只读模式
     *
     * @return 结果
     */
    public boolean isReadonly() {
        return this.dbConnect.isReadonly();
    }

    /**
     * 如果只读模式不支持操作，则抛出异常
     */
    public void throwReadonlyException() {
        if (this.isReadonly()) {
            throw new ReadonlyOperationException();
        }
    }

    /**
     * 连接数据库
     */
    public void start() {
        if (this.isConnected() || this.isConnecting()) {
            return;
        }
        // 初始化客户端
        this.initClient();
        try {
            // 开始连接时间
            final AtomicLong starTime = new AtomicLong();
            // 开始连接时间
            starTime.set(System.currentTimeMillis());
            // 更新连接状态
            this.state.set(DBConnState.CONNECTING);
            // 连接成功前阻塞线程
            if (this.connection().isValid(this.dbConnect.getConnectTimeOut())) {
                // 更新连接状态
                this.state.set(DBConnState.CONNECTED);
            } else {// 连接未成功则关闭
                this.close();
                if (this.state.get() == DBConnState.FAILED) {
                    this.state.set(null);
                } else {
                    this.state.set(DBConnState.FAILED);
                }
            }
        } catch (Exception ex) {
            this.state.set(DBConnState.FAILED);
            JulLog.warn("dbClient start error", ex);
            throw new DBException(ex);
        }
    }

    /**
     * 初始化客户端
     */
    protected void initClient() {
        // 连接地址
        String ip = "";
        int port = 0;
        // ssh端口转发
        if (this.dbConnect.isSSHForward()) {
            // SSHForwardConfig forwardInfo = new SSHForwardConfig();
            // forwardInfo.setHost(this.dbConnect.hostIp());
            // forwardInfo.setPort(this.dbConnect.hostPort());
            // // 连接信息
            // ip = "127.0.0.1";
            // port = this.sshForwarder.forward(forwardInfo);
        } else {// 直连
            // 连接信息
            ip = this.dbConnect.hostIp();
            port = this.dbConnect.hostPort();
        }
        this.connConfig.setHost(ip);
        this.connConfig.setPort(port);
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            this.connectionManager.destroy();
            //            // 销毁端口转发
            //            if (this.dbConnect.isSSHForward()) {
            //                this.sshForwarder.destroy();
            //            }
            JulLog.info("dbClient closed.");
            this.state.set(DBConnState.CLOSED);
        } catch (Exception ex) {
            JulLog.warn("dbClient close error.", ex);
        }
    }

    /**
     * db是否已连接
     *
     * @return 结果
     */
    public boolean isClosed() {
        return this.state() == DBConnState.CLOSED;
    }

    /**
     * db是否已连接
     *
     * @return 结果
     */
    public boolean isConnected() {
        DBConnState state = this.state.get();
        return state != null && state.isConnected();
    }

    /**
     * db是否连接中
     *
     * @return 结果
     */
    public boolean isConnecting() {
        return this.state() == DBConnState.CONNECTING;
    }


    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String connectName() {
        return this.dbConnect.getName();
    }

    /**
     * 添加状态监听器
     *
     * @param stateListener 状态监听器
     */
    public void addStateListener(ChangeListener<DBConnState> stateListener) {
        if (stateListener != null) {
            this.state.addListener(stateListener);
        }
    }

    /**
     * 获取表数量
     *
     * @param dbName 库名称或者模式名称
     * @return 表数量
     */
    public int tableSize(String dbName) {
        int size = 0;
        try {
            Connection connection = this.connection(dbName);
            String sql = "SELECT COUNT(*) FROM ALL_TABLES WHERE OWNER = ?";
            DBUtil.printSql(sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            if (resultSet.next()) {
                size = resultSet.getInt(1);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
        return size;
    }

    /**
     * 获取视图数量
     *
     * @param dbName 库名称或者模式名称
     * @return 视图数量
     */
    public int viewSize(String dbName) {
        int size = 0;
        try {
            Connection connection = this.connection(dbName);
            String sql = "SELECT COUNT(*) FROM ALL_VIEWS WHERE OWNER = ?";
            DBUtil.printSql(sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            if (resultSet.next()) {
                size = resultSet.getInt(1);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
        return size;
    }

    public DamengQueryResults<DamengExecuteResult> executeSql(String dbName, String sql) {
        DamengQueryResults<DamengExecuteResult> results = new DamengQueryResults<>();
        Connection connection = null;
        try {
            DBUtil.printSql(sql);
            DBSqlParser parser = DBSqlParser.getParser(sql, DBDialect.DAMENG);
            List<String> list = parser.parseSql();
            connection = this.connection(dbName);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            for (String execSql : list) {
                DamengExecuteResult result = new DamengExecuteResult();
                result.setSql(execSql);
                try {
                    long startTime = System.nanoTime();
                    boolean isQuery = statement.execute(execSql);
                    if (isQuery) {
                        ResultSet resultSet = statement.getResultSet();
                        if (parser.isSingle()) {
                            result.setFullColumn(parser.isFullColumn());
                        } else {
                            result.setFullColumn(DBUtil.isFullColumn(execSql));
                        }
                        result.parseResult(resultSet, connection, !parser.isSelect());
                        DBUtil.close(resultSet);
                        result.setSuccess(true);
                    } else {
                        int updateCount = statement.getUpdateCount();
                        result.setUpdateCount(updateCount);
                        result.setSuccess(true);
                    }
                    long endTime = System.nanoTime();
                    result.setUsed(endTime - startTime);
                } catch (SQLException ex) {
                    result.setMsg(ex.toString());
                }
                results.addResult(result);
            }
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            DBUtil.rollback(connection);
            results.parseError(ex);
        }
        return results;
    }

    public int insertBatch(String dbName, List<String> sqlList) {
        return this.insertBatch(dbName, sqlList, false);
    }

    public int procedureSize(String dbName) {
        int size = 0;
        try {
            Connection connection = this.procedureConnection(dbName);
            String sql = """
                    SELECT
                        COUNT(*)
                    FROM
                        ALL_OBJECTS
                    WHERE
                        OWNER = ?
                    AND
                        OBJECT_TYPE = 'PROCEDURE'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            if (resultSet.next()) {
                size = resultSet.getInt(1);
            }
            DBUtil.close(resultSet);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
        return size;
    }

    public int functionSize(String dbName) {
        int size = 0;
        try {
            Connection connection = this.functionConnection(dbName);
            String sql = """
                    SELECT
                        COUNT(*)
                    FROM
                        ALL_OBJECTS
                    WHERE
                        OWNER = ?
                    AND
                        OBJECT_TYPE = 'FUNCTION'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            if (resultSet.next()) {
                size = resultSet.getInt(1);
            }
            DBUtil.close(resultSet);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
        return size;
    }

    /**
     * 修改函数
     *
     * @param param 参数
     */
    public void alertFunction(DamengAlertFunctionParam param) {
        try {
            Connection connection = this.functionConnection(param.getDbName());
            Statement statement = connection.createStatement();
            String sql = DamengFunctionAlertSqlGenerator.generateSqlSingle(param);
            DBUtil.printSql(sql);
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<DamengTrigger> selectTriggers(String dbName) {
        try {
            String sql = """
                        SELECT
                            a.TRIGGER_NAME, a.TRIGGERING_EVENT AS EVENT_MANIPULATION, a.TRIGGERING_TYPE AS TRIGGER_TYPE, a.TABLE_NAME AS EVENT_OBJECT_TABLE , dt.TRIGGER_BODY ACTION_STATEMENT    
                        FROM
                            ALL_TRIGGERS a
                        LEFT JOIN 
                            DBA_TRIGGERS dt
                        ON 
                            dt.OWNER = a.OWNER
                        AND 
                            dt.TABLE_NAME = a.TABLE_NAME
                        AND
                            dt.TRIGGER_NAME = a.TRIGGER_NAME
                        WHERE
                            a.OWNER = ?
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection(dbName).prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet resultSet = statement.executeQuery();
            List<DamengTrigger> list = new ArrayList<>();
            while (resultSet.next()) {
                DamengTrigger trigger = new DamengTrigger();
                String name = resultSet.getString("TRIGGER_NAME");
                String type = resultSet.getString("TRIGGER_TYPE");
                String definition = resultSet.getString("ACTION_STATEMENT");
                String tableName = resultSet.getString("EVENT_OBJECT_TABLE");
                String manipulation = resultSet.getString("EVENT_MANIPULATION");
                trigger.setName(name);
                trigger.setTableName(tableName);
                trigger.setDefinition(DamengHelper.fixTiggerDefinition(definition));
                trigger.setPolicy(type.contains("BEFORE") ? "BEFORE" : "AFTER", manipulation);
                list.add(trigger);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return list;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public DamengTriggers selectTriggers(String dbName, String tableName) {
        try {
            String sql = """
                        SELECT
                            a.TRIGGER_NAME, a.TRIGGERING_EVENT AS EVENT_MANIPULATION, a.TRIGGERING_TYPE AS TRIGGER_TYPE, a.TABLE_NAME AS EVENT_OBJECT_TABLE , dt.TRIGGER_BODY ACTION_STATEMENT    
                        FROM
                            ALL_TRIGGERS a
                        LEFT JOIN 
                            DBA_TRIGGERS dt
                        ON 
                            dt.OWNER = a.OWNER
                        AND 
                            dt.TABLE_NAME = a.TABLE_NAME
                        AND
                            dt.TRIGGER_NAME = a.TRIGGER_NAME
                        WHERE
                            a.OWNER = ?
                        AND
                            a.TABLE_NAME = ?
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection(dbName).prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            DamengTriggers list = new DamengTriggers();
            while (resultSet.next()) {
                DamengTrigger trigger = new DamengTrigger();
                String name = resultSet.getString("TRIGGER_NAME");
                String type = resultSet.getString("TRIGGER_TYPE");
                String definition = resultSet.getString("ACTION_STATEMENT");
                String manipulation = resultSet.getString("EVENT_MANIPULATION");
                trigger.setName(name);
                trigger.setTableName(tableName);
                trigger.setDefinition(DamengHelper.fixTiggerDefinition(definition));
                trigger.setPolicy(type.contains("BEFORE") ? "BEFORE" : "AFTER", manipulation);
                list.add(trigger);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    //    public String getTriggerDefinition(String dbName, String triggerName) {
    //        try {
    //            String sql = """
    //                        SELECT
    //                            TO_CHAR(DBMS_METADATA.GET_DDL('TRIGGER', ?, ?))
    //                        AS
    //                            TRIGGER_DDL
    //                    """;
    //            DBUtil.printSql(sql);
    //            PreparedStatement statement = this.connection().prepareStatement(sql);
    //            statement.setString(1, triggerName);
    //            statement.setString(2, dbName);
    //            ResultSet resultSet = statement.executeQuery();
    //            List<DamengTrigger> list = new ArrayList<>();
    //            String definition = null;
    //            if (resultSet.next()) {
    //                definition = resultSet.getString(1);
    //            }
    //            DBUtil.close(resultSet);
    //            DBUtil.close(statement);
    //            return definition;
    //        } catch (Exception ex) {
    //            throw new DBException(ex);
    //        }
    //    }

    public String selectVersion() {
        if (this.hasProperty("version")) {
            return this.getProperty("version");
        }
        String version = "";
        try {
            Statement stmt = this.connection().createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT BANNER FROM V$VERSION FETCH FIRST 1 ROWS ONLY");
            if (resultSet.next()) {
                version = resultSet.getString(1);
            }
            this.putProperty("version", version);
            DBUtil.close(resultSet);
            DBUtil.close(stmt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    //public void dropEvent(String dbName, DamengEvent event) {
    //    try {
    //        String sql = "DROP EVENT " + DBUtil.wrap(event.getDbName(), event.getName(), DBDialect.DAMENG);
    //        DBUtil.printSql(sql);
    //        Statement statement = this.connection(dbName).createStatement();
    //        statement.executeUpdate(sql);
    //        DBUtil.close(statement);
    //    } catch (Exception ex) {
    //        ex.printStackTrace();
    //        throw new DBException(ex);
    //    }
    //}

    //    public void createEvent(String dbName, DamengEvent event) {
    //        try {
    //            String sql = DamengEventCreateSqlGenerator.generateSql(event);
    //            DBUtil.printSql(sql);
    //            Statement statement = this.connection(dbName).createStatement();
    //            statement.executeUpdate(sql);
    //            DBUtil.close(statement);
    //        } catch (Exception ex) {
    //            ex.printStackTrace();
    //            throw new DBException(ex);
    //        }
    //    }
    //
    //    public void alertEvent(String dbName, DamengEvent event) {
    //        try {
    //            String sql = EventAlertSqlGenerator.generateSql(event);
    //            DBUtil.printSql(sql);
    //            Statement statement = this.connection(dbName).createStatement();
    //            statement.executeUpdate(sql);
    //            DBUtil.close(statement);
    //        } catch (Exception ex) {
    //            ex.printStackTrace();
    //            throw new DBException(ex);
    //        }
    //    }

    //    public DamengEvent selectEvent(String dbName, String eventName) {
    //        try {
    //            Connection connection = this.functionConnection(dbName, null);
    //            String sql = """
    //                    SELECT
    //                        t1.ENABLED AS STATUS,
    //                        t1.JOB_ACTION AS ACTION,
    //                        t1.SCHEDULE_EXPR AS EXPR,
    //                    FROM
    //                        USER_SCHEDULER_JOBS t1
    //                    WHERE
    //                        t1.JOB_NAME = ?
    //                    """;
    //            DBUtil.printSql(sql);
    //            PreparedStatement statement = connection.prepareStatement(sql);
    //            statement.setString(1, eventName);
    //            ResultSet resultSet = statement.executeQuery();
    //            DBUtil.printMetaData(resultSet);
    //            DamengEvent event = new DamengEvent();
    //            while (resultSet.next()) {
    //                String expr = resultSet.getString("EXPR");
    //                String status = resultSet.getString("STATUS");
    //                String action = resultSet.getString("ACTION");
    //                event.setName(eventName);
    //                event.setStatus("TRUE".equalsIgnoreCase(status) ? "ENABLE" : "DISABLE");
    //                DamengHelper.parseJobExpr(expr, event);
    //                DamengHelper.parseJobAction(action, event);
    //            }
    //            DBUtil.close(resultSet);
    //            return event;
    //        } catch (Exception ex) {
    //            ex.printStackTrace();
    //            throw new DBException(ex);
    //        }
    //    }

    //    public Integer eventSize(String dbName) {
    //        int size = 0;
    //        try {
    //            Connection connection = this.functionConnection(dbName, null);
    //            String sql = """
    //                    SELECT
    //                        COUNT(*)
    //                    FROM
    //                        USER_SCHEDULER_JOBS
    //                    """;
    //            DBUtil.printSql(sql);
    //            PreparedStatement statement = connection.prepareStatement(sql);
    //            ResultSet resultSet = statement.executeQuery();
    //            DBUtil.printMetaData(resultSet);
    //            if (resultSet.next()) {
    //                size = resultSet.getInt(1);
    //            }
    //            DBUtil.close(resultSet);
    //        } catch (Exception ex) {
    //            ex.printStackTrace();
    //            throw new DBException(ex);
    //        }
    //        return size;
    //    }

    //    public List<DamengEvent> events(String dbName) {
    //        try {
    //            Connection connection = this.functionConnection(dbName, null);
    //            String sql = """
    //                    SELECT
    //                        t1.JOB_NAME AS NAME,
    //                        t1.ENABLED AS STATUS,
    //                        t1.JOB_ACTION AS ACTION,
    //                        t1.REPEAT_INTERVAL AS EXPR
    //                    FROM
    //                        USER_SCHEDULER_JOBS t1
    //                    """;
    //            DBUtil.printSql(sql);
    //            PreparedStatement statement = connection.prepareStatement(sql);
    //            ResultSet resultSet = statement.executeQuery();
    //            DBUtil.printMetaData(resultSet);
    //            List<DamengEvent> list = new ArrayList<>();
    //            while (resultSet.next()) {
    //                String name = resultSet.getString("NAME");
    //                String expr = resultSet.getString("EXPR");
    //                String status = resultSet.getString("STATUS");
    //                String action = resultSet.getString("ACTION");
    //                DamengEvent event = new DamengEvent();
    //                event.setName(name);
    //                event.setStatus("TRUE".equalsIgnoreCase(status) ? "ENABLE" : "DISABLE");
    //                DamengHelper.parseJobExpr(expr, event);
    //                DamengHelper.parseJobAction(action, event);
    //                list.add(event);
    //            }
    //            DBUtil.close(resultSet);
    //            return list;
    //        } catch (Exception ex) {
    //            ex.printStackTrace();
    //            throw new DBException(ex);
    //        }
    //    }

    public boolean isSupportFeature(DBFeature feature) {
        try {
            if (feature == DBFeature.EVENT) {
                return true;
            }
            // 检查约束
            if (feature == DBFeature.CHECK) {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean isSupportCheckFeature() {
        return this.isSupportFeature(DBFeature.CHECK);
    }

    public boolean isSupportEventFeature() {
        return this.isSupportFeature(DBFeature.EVENT);
    }

    public List<DamengTable> selectTables(String dbName) {
        DamengSelectTableParam param = new DamengSelectTableParam();
        param.setFull(true);
        param.setDbName(dbName);
        return this.selectTables(param);
    }

    public List<DamengTable> selectTablesSimple(String dbName) {
        DamengSelectTableParam param = new DamengSelectTableParam();
        param.setFull(false);
        param.setDbName(dbName);
        return this.selectTables(param);
    }

    public List<DamengTable> selectTables(DamengSelectTableParam param) {
        try {
            String dbName = param.getDbName();
            List<DamengTable> tables = new ArrayList<>();
            Connection connection = this.connection(dbName);
            String sql = """
                        SELECT 
                            T.TABLE_NAME, C.COMMENTS AS "TABLE_COMMENT", T.TABLESPACE_NAME AS "TABLE_SPACE" 
                        FROM 
                            ALL_TABLES T 
                        LEFT JOIN 
                            ALL_TAB_COMMENTS C 
                        ON 
                            T.OWNER = C.OWNER 
                        AND 
                           T.TABLE_NAME = C.TABLE_NAME 
                        WHERE 
                            T.OWNER = ?
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            while (resultSet.next()) {
                DamengTable table = new DamengTable();
                String tableName = resultSet.getString("TABLE_NAME");
                String tableSpace = resultSet.getString("TABLE_SPACE");
                String tableComment = resultSet.getString("TABLE_COMMENT");
                if (param.isFull()) {
                    String showCreateTable = this.showCreateTable(dbName, tableName);
                    table.setCreateDefinition(showCreateTable);
                }
                table.setDbName(dbName);
                table.setName(tableName);
                table.setComment(tableComment);
                table.setTableSpace(tableSpace);
                tables.add(table);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return tables;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    //    public List<DamengTable> selectTablesSimple(String dbName) {
    //        try {
    //            List<DamengTable> tables = new ArrayList<>();
    //            Connection connection = this.connection(dbName);
    //            String sql = """
    //                        SELECT
    //                            T.TABLE_NAME, T.TABLESPACE_NAME AS "TABLE_SPACE"
    //                        FROM
    //                            ALL_TABLES T
    //                        WHERE
    //                            T.OWNER = ?
    //                    """;
    //            DBUtil.printSql(sql);
    //            PreparedStatement statement = connection.prepareStatement(sql);
    //            statement.setString(1, dbName);
    //            ResultSet resultSet = statement.executeQuery();
    //            DBUtil.printMetaData(resultSet);
    //            while (resultSet.next()) {
    //                DamengTable table = new DamengTable();
    //                String tableName = resultSet.getString("TABLE_NAME");
    //                String tableSpace = resultSet.getString("TABLE_SPACE");
    //                table.setDbName(dbName);
    //                table.setName(tableName);
    //                table.setTableSpace(tableSpace);
    //                tables.add(table);
    //            }
    //            DBUtil.close(resultSet);
    //            DBUtil.close(statement);
    //            return tables;
    //        } catch (Exception ex) {
    //            ex.printStackTrace();
    //            throw new DBException(ex);
    //        }
    //    }

    public DamengColumns selectColumns(DamengSelectColumnParam param) {
        try {
            String dbName = param.getDbName();
            String tableName = param.getTableName();
            DamengColumns columns = new DamengColumns();
            String sql = """
                    SELECT
                        C.COLUMN_ID AS "POSITION",
                        C.COLUMN_NAME AS "Field",
                        C.DATA_SCALE AS "DATA_SCALE",
                        C.DATA_LENGTH AS "DATA_LENGTH",
                        CASE WHEN C.NULLABLE='Y' THEN 'YES' ELSE 'NO' END AS "Null",
                        C.DATA_TYPE AS "Type",
                        CC.COMMENT$ AS "Comment",
                        C.DATA_DEFAULT AS "Default",
                        CASE WHEN CONS.COLUMN_NAME IS NOT NULL THEN 'PRI' ELSE '' END AS "Key"
                    FROM
                        ALL_TAB_COLUMNS C
                    LEFT JOIN
                        SYS.SYSCOLUMNCOMMENTS CC
                    ON
                        C.OWNER = CC.SCHNAME
                    AND
                        C.TABLE_NAME = CC.TVNAME
                    AND
                        C.COLUMN_NAME = CC.COLNAME
                    LEFT JOIN
                        ALL_CONS_COLUMNS CONS
                    ON
                        C.OWNER = CONS.OWNER
                    AND
                        C.TABLE_NAME = CONS.TABLE_NAME
                    AND
                        C.COLUMN_NAME = CONS.COLUMN_NAME
                    AND
                        EXISTS (SELECT 1 FROM ALL_CONSTRAINTS AC WHERE AC.OWNER=CONS.OWNER AND AC.CONSTRAINT_NAME=CONS.CONSTRAINT_NAME AND AC.CONSTRAINT_TYPE='P')
                    WHERE
                        C.OWNER = ?
                    AND
                        C.TABLE_NAME = ?
                    """;
            Connection connection = this.connection(dbName);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            DatabaseMetaData metaData = connection.getMetaData();
            DBUtil.printMetaData(resultSet);
            while (resultSet.next()) {
                String key = resultSet.getString("Key");
                String type = resultSet.getString("Type");
                String field = resultSet.getString("Field");
                Object def = resultSet.getObject("Default");
                int position = resultSet.getInt("POSITION");
                String nullable = resultSet.getString("Null");
                int dataScale = resultSet.getInt("DATA_SCALE");
                String comment = resultSet.getString("Comment");
                int dataLength = resultSet.getInt("DATA_LENGTH");
                // 自动递增
                Boolean autoIncrement = null;
                ResultSet rs = metaData.getColumns(null, null, tableName, field);
                if (rs.next()) {
                    autoIncrement = StringUtil.equalsIgnoreCase("YES", rs.getString("IS_AUTOINCREMENT"));
                }
                IOUtil.close(rs);
                DamengColumn column = new DamengColumn();
                column.parseKey(key);
                column.setName(field);
                column.setSize(dataLength);
                column.setDigits(dataScale);
                column.parseType(type);
                column.setDbName(dbName);
                column.setComment(comment);
                column.setDefaultValue(def);
                column.setPosition(position);
                column.setTableName(tableName);
                column.setAutoIncrement(autoIncrement);
                column.setNullable("yes".equalsIgnoreCase(nullable));
                columns.add(column);
            }
            DBUtil.close(resultSet);
            // 返回排序后的数据
            return new DamengColumns(columns.sortOfPosition());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<DamengRecord> selectRecords(DamengSelectRecordParam param) {
        try {
            Connection connection = this.connection(param.getDbName(), param.getSchema());
            StringBuilder builder = new StringBuilder("SELECT * FROM ");
            builder.append(DBUtil.wrap(param.getDbName(), param.getTableName(), DBDialect.DAMENG));
            String filterCondition = DamengConditionUtil.buildCondition(param.getFilters());
            if (StringUtil.isNotBlank(filterCondition)) {
                builder.append(" WHERE ").append(filterCondition);
            }
            if (param.hasPageControl()) {
                builder.append(" OFFSET ")
                        .append(param.getStart())
                        .append(" ROWS FETCH NEXT ")
                        .append(param.getLimit())
                        .append(" ROWS ONLY");
            }
            String sql = builder.toString();
            DBUtil.printSql(sql);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            DBUtil.printMetaData(resultSet);
            List<DamengRecord> records = new ArrayList<>();
            List<DamengColumn> columns;
            if (param.getColumns() != null) {
                columns = param.getColumns();
            } else {
                columns = DamengHelper.parseColumns(resultSet);
            }
            while (resultSet.next()) {
                DamengRecord record = new DamengRecord(columns, param.isReadonly());
                for (DamengColumn column : columns) {
                    Object data = resultSet.getObject(column.getName());
                    //                    // 获取几何值
                    //                    if (column.supportGeometry()) {
                    //                        data = DamengHelper.getGeometryString(connection, data);
                    //                    }
                    record.putValue(column, data);
                }
                records.add(record);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return records;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public long selectRecordCount(DamengSelectRecordParam param) {
        long count = 0;
        try {
            Connection connection = this.connection(param.getDbName(), param.getSchema());
            StringBuilder builder = new StringBuilder("SELECT COUNT(*) FROM");
            builder.append(DBUtil.wrap(param.getDbName(), param.getTableName(), DBDialect.DAMENG));
            String filterCondition = DamengConditionUtil.buildCondition(param.getFilters());
            if (StringUtil.isNotBlank(filterCondition)) {
                builder.append(" WHERE ").append(filterCondition);
            }
            String sql = builder.toString();
            DBUtil.printSql(sql);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                count = resultSet.getLong(1);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
        return count;
    }

    public int insertRecord(DamengInsertRecordParam param) {
        if (param == null || param.getRecord() == null) {
            return 0;
        }
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO ")
                    .append(DBUtil.wrap(param.getDbName(), param.getTableName(), DBDialect.DAMENG))
                    .append("(");
            for (String column : param.getRecord().columns()) {
                builder.append(DBUtil.wrap(column, DBDialect.DAMENG)).append(",");
            }
            builder.append(")");
            builder.append(" VALUES(");
            for (String column : param.getRecord().columns()) {
                //                if (param.getRecord().isTypeGeometry(column)) {
                //                    builder.append("ST_GeomFromText(?),");
                //                } else {
                builder.append("?,");
                //                }
            }
            builder.append(")");
            String sql = builder.toString();
            sql = sql.replaceAll(",\\)", ")");
            DBUtil.printSql(sql);
            Connection connection = this.connection(param.getDbName(), param.getSchema());
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            for (String colName : param.getRecord().columns()) {
                DBUtil.setVal(statement, param.getRecord().value(colName), index++);
            }
            int count = statement.executeUpdate();
            DamengRecordPrimaryKey primaryKey = param.getPrimaryKey();
            // 处理自动递增值
            if (primaryKey != null && primaryKey.shouldReturnData()) {
                ResultSet rs = statement.getGeneratedKeys();
                Long newId;
                if (rs.next()) {
                    newId = rs.getLong(1);
                } else {
                    newId = DamengHelper.lastInsertId(connection);
                }
                IOUtil.close(rs);
                primaryKey.setReturnData(newId);
            }
            DBUtil.close(statement);
            return count;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public int deleteRecord(DamengDeleteRecordParam param) {
        try {
            int updateCount;
            String dbName = param.getDbName();
            String schema = param.getSchema();
            String tableName = param.getTableName();
            Connection connection = this.connection(dbName, schema);
            StringBuilder builder = new StringBuilder();
            builder.append("DELETE FROM ")
                    .append(DBUtil.wrap(dbName, tableName, DBDialect.DAMENG))
                    .append(" WHERE ");
            if (param.getPrimaryKey() == null) {
                DamengRecordData recordData = param.getRecord();
                boolean first = true;
                for (String colName : recordData.columns()) {
                    if (first) {
                        first = false;
                    } else {
                        builder.append(" AND ");
                    }
                    if (recordData.hasValue(colName)) {
                        builder.append(DBUtil.wrap(colName, DBDialect.DAMENG))
                                .append(" = ?");
                    } else {
                        builder.append(DBUtil.wrap(colName, DBDialect.DAMENG))
                                .append(" IS NULL");
                    }
                }
                String sql = builder.toString();
                DBUtil.printSql(sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                int index = 1;
                // 设置参数
                for (String colName : recordData.notNullColumns()) {
                    DBUtil.setVal(statement, recordData.value(colName), index++);
                }
                updateCount = DBUtil.executeUpdate(statement);
            } else {
                DamengRecordPrimaryKey primaryKey = param.getPrimaryKey();
                builder.append(DBUtil.wrap(primaryKey.getColumnName(), DBDialect.DAMENG))
                        .append(" = ?");
                String sql = builder.toString();
                DBUtil.printSql(sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                DBUtil.setVal(statement, primaryKey.originalData(), 1);
                updateCount = DBUtil.executeUpdate(statement);
                DBUtil.close(statement);
            }
            return updateCount;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public int updateRecord(DamengUpdateRecordParam param) {
        try {
            int updateCount;
            String dbName = param.getDbName();
            String schema = param.getSchema();
            String tableName = param.getTableName();
            DamengRecordData recordData = param.getUpdateRecord();
            StringBuilder builder = new StringBuilder();
            builder.append("UPDATE ")
                    .append(DBUtil.wrap(dbName, tableName, DBDialect.DAMENG))
                    .append(" SET ");
            for (String column : recordData.columns()) {
                //                if (recordData.isTypeGeometry(column)) {
                //                    builder.append(DBUtil.wrap(column)).append(" = ST_GeomFromText(?),");
                //                } else {
                builder.append(DBUtil.wrap(column, DBDialect.DAMENG)).append(" = ?,");
                //                }
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(" WHERE ");
            Connection connection = this.connection(dbName, schema);
            if (param.getPrimaryKey() == null) {
                DamengRecordData originalRecordData = param.getRecord();
                // 参数
                boolean first = true;
                for (String column : originalRecordData.columns()) {
                    if (first) {
                        first = false;
                    } else {
                        builder.append(" AND ");
                    }
                    Object value = originalRecordData.value(column);
                    if (value == null) {
                        builder.append(DBUtil.wrap(column, DBDialect.DAMENG)).append(" IS NULL");
                    } else {
                        builder.append(DBUtil.wrap(column, DBDialect.DAMENG)).append(" = ?");
                    }
                }
                int index = 1;
                String sql = builder.toString();
                DBUtil.printSql(sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                // 设置值
                for (String colName : recordData.columns()) {
                    DBUtil.setVal(statement, recordData.value(colName), index++);
                }
                // 设置参数
                for (String colName : originalRecordData.columns()) {
                    Object value = originalRecordData.value(colName);
                    if (value != null) {
                        DBUtil.setVal(statement, value, index++);
                    }
                }
                updateCount = DBUtil.executeUpdate(statement);
                DBUtil.close(statement);
            } else {
                DamengRecordPrimaryKey primaryKey = param.getPrimaryKey();
                builder.append(DBUtil.wrap(primaryKey.getColumnName(), DBDialect.DAMENG)).append(" = ?");
                String sql = builder.toString();
                DBUtil.printInfo(sql, recordData);
                PreparedStatement statement = connection.prepareStatement(sql);
                int index = 1;
                // 设置值
                for (String colName : recordData.columns()) {
                    DBUtil.setVal(statement, recordData.value(colName), index++);
                }
                // 设置参数
                DBUtil.setVal(statement, primaryKey.originalData(), index);
                updateCount = DBUtil.executeUpdate(statement);
                DBUtil.close(statement);
            }
            return updateCount;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public String showCreateTable(String dbName, String tableName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = "SELECT DBMS_METADATA.GET_DDL('TABLE', ?) FROM DUAL";
            DBUtil.printSql(sql);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, tableName);
            ResultSet resultSet = stmt.executeQuery();
            String createDefinition = "";
            if (resultSet.next()) {
                createDefinition = resultSet.getString(1);
            }
            DBUtil.close(resultSet);
            DBUtil.close(stmt);
            return createDefinition;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public String showCreateView(String dbName, String viewName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = "SELECT TEXT FROM USER_VIEWS WHERE VIEW_NAME = ?";
            DBUtil.printSql(sql);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, viewName);
            ResultSet resultSet = stmt.executeQuery();
            String createDefinition = "";
            if (resultSet.next()) {
                createDefinition = "CREATE OR REPLACE VIEW " + viewName + " AS\n" + resultSet.getString(1);
            }
            DBUtil.close(resultSet);
            DBUtil.close(stmt);
            return createDefinition;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public String showCreateFunction(String dbName, String functionName) {
        try {
            Connection connection = this.functionConnection(dbName);
            String sql = "SELECT DBMS_METADATA.GET_DDL('FUNCTION', ?) FROM DUAL";
            DBUtil.printSql(sql);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, functionName);
            ResultSet resultSet = stmt.executeQuery();
            String createDefinition = "";
            if (resultSet.next()) {
                createDefinition = resultSet.getString(1);
            }
            DBUtil.close(resultSet);
            DBUtil.close(stmt);
            return createDefinition;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public String showCreateProcedure(String dbName, String procedureName) {
        try {
            Connection connection = this.procedureConnection(dbName);
            String sql = "SELECT DBMS_METADATA.GET_DDL('PROCEDURE', ?) FROM DUAL";
            DBUtil.printSql(sql);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, procedureName);
            ResultSet resultSet = stmt.executeQuery();
            String createDefinition = "";
            if (resultSet.next()) {
                createDefinition = resultSet.getString(1);
            }
            DBUtil.close(resultSet);
            DBUtil.close(stmt);
            return createDefinition;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public String showCreateTrigger(String dbName, String triggerName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = "SELECT DBMS_METADATA.GET_DDL('TRIGGER', ?) FROM DUAL";
            DBUtil.printSql(sql);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, triggerName);
            ResultSet resultSet = stmt.executeQuery();
            String createDefinition = "";
            if (resultSet.next()) {
                createDefinition = resultSet.getString(1);
            }
            DBUtil.close(resultSet);
            DBUtil.close(stmt);
            return createDefinition;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public String showCreateEvent(String dbName, String eventName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = "SELECT DBMS_METADATA.GET_DDL('EVENT', ?) FROM DUAL";
            DBUtil.printSql(sql);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, eventName);
            ResultSet resultSet = stmt.executeQuery();
            String createDefinition = "";
            if (resultSet.next()) {
                createDefinition = resultSet.getString(1);
            }
            DBUtil.close(resultSet);
            DBUtil.close(stmt);
            return createDefinition;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<String> tableSpaces() {
        try {
            List<String> engines = new ArrayList<>();
            String sql = "SELECT TABLESPACE_NAME FROM DBA_TABLESPACES WHERE STATUS = 0";
            DBUtil.printSql(sql);
            Statement statement = this.connection().createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            DBUtil.printMetaData(resultSet);
            while (resultSet.next()) {
                engines.add(resultSet.getString(1));
            }
            DBUtil.close(statement);
            return engines;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public List<DBDatabase> databases() {
        try {
            Statement statement = this.connection().createStatement();
            // 查询当前用户有权限访问的schema：优先显示有对象的schema，也包含当前用户自己的schema
            String sql = """
                    SELECT DISTINCT OWNER FROM ALL_OBJECTS
                    UNION
                    SELECT USERNAME FROM ALL_USERS
                    ORDER BY 1
                    """;
            ResultSet resultSet = statement.executeQuery(sql);
            List<DBDatabase> list = new ArrayList<>();
            while (resultSet.next()) {
                DBDatabase databases = new DBDatabase();
                String dbName = resultSet.getString(1);
                databases.setName(dbName);
                list.add(databases);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public DBDatabase database(String dbName) {
        try {
            DBDatabase database = new DBDatabase();
            database.setName(dbName);
            database.setCharsetAndCollation(this.databaseCollation(dbName));
            return database;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public DamengTable selectTable(String dbName, String tableName) {
        DamengSelectTableParam param = new DamengSelectTableParam();
        param.setFull(true);
        param.setDbName(dbName);
        param.setTableName(tableName);
        return this.selectTable(param);
    }

    public DamengTable selectTableSimple(String dbName, String tableName) {
        DamengSelectTableParam param = new DamengSelectTableParam();
        param.setFull(false);
        param.setDbName(dbName);
        param.setTableName(tableName);
        return this.selectTable(param);
    }

    public DamengTable selectTable(DamengSelectTableParam param) {
        try {
            String dbName = param.getDbName();
            String tableName = param.getTableName();
            DamengTable table = new DamengTable();
            table.setDbName(dbName);
            table.setName(tableName);
            Connection connection = this.connection(dbName);
            //if (param.isFull()) {
            String sql = """
                        SELECT 
                            C.COMMENTS AS "TABLE_COMMENT", T.TABLESPACE_NAME AS "TABLE_SPACE" 
                        FROM 
                            ALL_TABLES T 
                        LEFT JOIN 
                            ALL_TAB_COMMENTS C
                        ON 
                            T.OWNER = C.OWNER 
                        AND 
                            T.TABLE_NAME = C.TABLE_NAME 
                        WHERE 
                            T.OWNER = ? 
                        AND 
                            T.TABLE_NAME = ?
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            while (resultSet.next()) {
                String tableSpace = resultSet.getString("TABLE_SPACE");
                String tableComment = resultSet.getString("TABLE_COMMENT");
                table.setComment(tableComment);
                table.setTableSpace(tableSpace);
            }
            if (param.isFull()) {
                String showCreateTable = this.showCreateTable(dbName, tableName);
                table.setCreateDefinition(showCreateTable);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            //} else {
            //    String sql = "SELECT COMMENTS AS REMARKS FROM ALL_TAB_COMMENTS WHERE OWNER = ? AND TABLE_NAME = ?";
            //    DBUtil.printSql(sql);
            //    PreparedStatement statement = connection.prepareStatement(sql);
            //    statement.setString(1, dbName);
            //    statement.setString(2, tableName);
            //    ResultSet resultSet = statement.executeQuery();
            //    DBUtil.printMetaData(resultSet);
            //    while (resultSet.next()) {
            //        String remarks = resultSet.getString("REMARKS");
            //        table.setComment(remarks);
            //    }
            //    DBUtil.close(resultSet);
            //    DBUtil.close(statement);
            //}
            return table;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    //public DamengTable selectFullTable(DamengSelectTableParam param) {
    //    try {
    //        String dbName = param.getDbName();
    //        String tableName = param.getTableName();
    //        DamengTable table = new DamengTable();
    //        table.setDbName(dbName);
    //        table.setName(tableName);
    //        Connection connection = this.connection(dbName);
    //        String sql = """
    //                        SELECT
    //                            C.COMMENTS AS "TABLE_COMMENT", T.TABLESPACE_NAME AS "TABLE_SPACE"
    //                        FROM
    //                            ALL_TABLES T
    //                        LEFT JOIN
    //                            ALL_TAB_COMMENTS C
    //                        ON
    //                            T.OWNER = C.OWNER
    //                        AND
    //                            T.TABLE_NAME = C.TABLE_NAME
    //                        WHERE
    //                            T.OWNER = ?
    //                        AND
    //                            T.TABLE_NAME = ?
    //                """;
    //        DBUtil.printSql(sql);
    //        PreparedStatement statement = connection.prepareStatement(sql);
    //        statement.setString(1, dbName);
    //        statement.setString(2, tableName);
    //        ResultSet resultSet = statement.executeQuery();
    //        DBUtil.printMetaData(resultSet);
    //        String showCreateTable = this.showCreateTable(dbName, tableName);
    //        while (resultSet.next()) {
    //            String tableSpace = resultSet.getString("TABLE_SPACE");
    //            String tableComment = resultSet.getString("TABLE_COMMENT");
    //            table.setComment(tableComment);
    //            table.setTableSpace(tableSpace);
    //            table.setCreateDefinition(showCreateTable);
    //        }
    //        DBUtil.close(resultSet);
    //        DBUtil.close(statement);
    //        return table;
    //    } catch (Exception ex) {
    //        ex.printStackTrace();
    //        throw new DBException(ex);
    //    }
    //}

    public DamengView selectView(String dbName, String viewName) {
        try {
            String sql = """
                        SELECT 
                            v.TEXT AS DEFINITION, v.VIEW_NAME, v.READ_ONLY, c.COMMENTS AS TABLE_COMMENT 
                        FROM 
                            ALL_VIEWS v
                        LEFT JOIN 
                            ALL_TAB_COMMENTS C 
                        ON 
                            V.OWNER = C.OWNER
                        AND 
                            V.VIEW_NAME = C.TABLE_NAME 
                        WHERE 
                            v.OWNER = ?
                        AND
                            VIEW_NAME = ?
                    """;
            DBUtil.printSql(sql);
            Connection connection = this.connection(dbName);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, viewName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            // 遍历结果集
            DamengView view = new DamengView();
            while (resultSet.next()) {
                String name = resultSet.getString("VIEW_NAME");
                String readOnly = resultSet.getString("READ_ONLY");
                String definition = resultSet.getString("DEFINITION");
                String tableComment = resultSet.getString("TABLE_COMMENT");
                if (readOnly == null) {
                    view.setUpdatable(DamengHelper.isViewUpdatable(connection, dbName, viewName));
                } else {
                    view.setUpdatable(!StringUtil.equalsIgnoreCase("Y", readOnly));
                }
                view.setDbName(dbName);
                view.setName(name);
                view.setComment(tableComment);
                view.setDefinition(definition);
            }
            // 关闭连接和释放资源
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return view;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<DamengView> selectViews(String dbName) {
        try {
            List<DamengView> list = new ArrayList<>();
            String sql = """
                        SELECT 
                            v.TEXT AS DEFINITION, v.VIEW_NAME, v.READ_ONLY,  C.COMMENTS AS TABLE_COMMENT 
                        FROM 
                            ALL_VIEWS v
                        LEFT JOIN 
                            ALL_TAB_COMMENTS C 
                        ON 
                            V.OWNER = C.OWNER
                        AND 
                            V.VIEW_NAME = C.TABLE_NAME 
                        WHERE 
                            V.OWNER = ?
                    """;
            DBUtil.printSql(sql);
            Connection connection = this.connection(dbName);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            // 遍历结果集
            while (resultSet.next()) {
                DamengView view = new DamengView();
                String name = resultSet.getString("VIEW_NAME");
                String readOnly = resultSet.getString("READ_ONLY");
                String definition = resultSet.getString("DEFINITION");
                String tableComment = resultSet.getString("TABLE_COMMENT");
                if (readOnly == null) {
                    view.setUpdatable(DamengHelper.isViewUpdatable(connection, dbName, name));
                } else {
                    view.setUpdatable(!StringUtil.equalsIgnoreCase("Y", readOnly));
                }
                view.setDbName(dbName);
                view.setName(name);
                view.setComment(tableComment);
                view.setDefinition(definition);
                view.setUpdatable(!StringUtil.equalsIgnoreCase("Y", readOnly));
                list.add(view);
            }
            // 关闭连接和释放资源
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void dropView(String dbName, DamengView view) {
        try {
            String sql = "DROP VIEW IF EXISTS " + DBUtil.wrap(view.getDbName(), view.getName(), DBDialect.DAMENG);
            Statement statement = this.connection(dbName).createStatement();
            DBUtil.printSql(sql);
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public boolean existView(String dbName, String viewName) {
        boolean result;
        try {
            String sql = "SELECT COUNT(*) FROM ALL_VIEWS WHERE OWNER = ? AND VIEW_NAME = ?";
            PreparedStatement statement = this.connection(dbName).prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, viewName);
            ResultSet resultSet = statement.executeQuery();
            result = resultSet.next() && resultSet.getInt(1) > 0;
            DBUtil.close(resultSet);
            DBUtil.close(statement);
        } catch (Exception ex) {
            throw new DBException(ex);
        }
        return result;
    }

    /**
     * 创建视图
     *
     * @param param 参数
     */
    public void createView(DamengCreateViewParam param) {
        Connection connection = null;
        try {
            List<String> sqlList = DamengViewCreateSqlGenerator.generateSql(param);
            // 无变化
            if (CollectionUtil.isEmpty(sqlList)) {
                return;
            }
            String dbName = param.getDbName();
            connection = this.connection(dbName);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            for (String sql : sqlList) {
                if (StringUtil.isBlank(sql)) {
                    continue;
                }
                DBUtil.printSql(sql);
                statement.executeUpdate(sql);
            }
            connection.commit();
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            DBUtil.rollback(connection);
            throw new DBException(ex);
        }
    }

    /**
     * 修改视图
     *
     * @param param 参数
     */
    public void alertView(DamengAlertViewParam param) {
        Connection connection = null;
        try {
            List<String> sqlList = DamengViewAlertSqlGenerator.generateSql(param);
            // 无变化
            if (CollectionUtil.isEmpty(sqlList)) {
                return;
            }
            String dbName = param.getDbName();
            connection = this.connection(dbName);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            for (String sql : sqlList) {
                if (StringUtil.isBlank(sql)) {
                    continue;
                }
                DBUtil.printSql(sql);
                statement.executeUpdate(sql);
            }
            connection.commit();
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            DBUtil.rollback(connection);
            throw new DBException(ex);
        }
    }

    public DamengIndexes indexes(String dbName, String tableName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = """
                    SELECT
                        i.INDEX_NAME AS "Key_name",
                        c.COLUMN_NAME AS "Column_name",
                        CASE WHEN i.UNIQUENESS='UNIQUE' THEN 0 ELSE 1 END AS "Non_unique",
                        c.COLUMN_POSITION AS "Seq_in_index",
                        CASE WHEN i.INDEX_TYPE='NORMAL' THEN 'BTREE' ELSE i.INDEX_TYPE END AS "Index_type"
                    FROM ALL_INDEXES i
                    JOIN ALL_IND_COLUMNS c ON i.INDEX_NAME = c.INDEX_NAME AND i.OWNER = c.INDEX_OWNER
                    WHERE i.TABLE_OWNER = ? AND i.TABLE_NAME = ? AND i.INDEX_TYPE != 'PRIMARY'
                    ORDER BY i.INDEX_NAME, c.COLUMN_POSITION
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            Map<String, DamengIndex> indexMap = new HashMap<>();
            while (resultSet.next()) {
                String keyName = resultSet.getString("Key_name");
                // 主键类型的跳过
                if ("Primary".equalsIgnoreCase(keyName)) {
                    continue;
                }
                DamengIndex tableIndex = indexMap.get(keyName);
                String columnName = resultSet.getString("Column_name");
                if (tableIndex == null) {
                    int noneUnique = resultSet.getInt("Non_unique");
                    int seqInIndex = resultSet.getInt("Seq_in_index");
                    String indexType = resultSet.getString("Index_type");
                    tableIndex = new DamengIndex();
                    tableIndex.setName(keyName);
                    tableIndex.setSeqIndex(seqInIndex);
                    tableIndex.type(indexType, noneUnique);
                    indexMap.put(keyName, tableIndex);
                }
                tableIndex.addColumn(columnName);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return new DamengIndexes(indexMap.values());
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public DamengChecks checks(String dbName, String tableName) {
        if (!this.isSupportCheckFeature()) {
            return null;
        }
        try {
            String sql = """
                        SELECT
                            OWNER AS "DB_NAME",
                            CONSTRAINT_NAME AS "NAME",
                            TABLE_NAME AS "TABLE_NAME",
                            SEARCH_CONDITION AS "CLAUSE"
                        FROM ALL_CONSTRAINTS
                        WHERE CONSTRAINT_TYPE = 'C'
                        AND OWNER = ?
                        AND TABLE_NAME = ?
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection(dbName).prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            DamengChecks checks = new DamengChecks();
            while (resultSet.next()) {
                DamengCheck check = new DamengCheck();
                String name = resultSet.getString("NAME");
                String clause = resultSet.getString("CLAUSE");
                check.setName(name);
                check.setClause(clause);
                check.setDbName(dbName);
                check.setTableName(tableName);
                checks.add(check);
            }
            DBUtil.close(resultSet);
            return checks;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public DamengForeignKeys foreignKeys(String dbName, String tableName) {
        try {
            // 查询外键 - Dameng uses ALL_CONSTRAINTS + ALL_CONS_COLUMNS
            String sql = """
                    SELECT
                        acc.COLUMN_NAME AS "FKCOLUMN_NAME",
                        ac_r.OWNER AS "PKTABLE_CAT",
                        ac_r.TABLE_NAME AS "PKTABLE_NAME",
                        acc_r.COLUMN_NAME AS "PKCOLUMN_NAME",
                        ac.CONSTRAINT_NAME AS "FK_NAME",
                        NULL AS "UPDATE_RULE",
                        ac.DELETE_RULE
                    FROM 
                        ALL_CONSTRAINTS ac
                    JOIN 
                        ALL_CONS_COLUMNS acc ON ac.CONSTRAINT_NAME = acc.CONSTRAINT_NAME AND ac.OWNER = acc.OWNER
                    JOIN 
                        ALL_CONSTRAINTS ac_r ON ac.R_OWNER = ac_r.OWNER AND ac.R_CONSTRAINT_NAME = ac_r.CONSTRAINT_NAME
                    JOIN ALL_CONS_COLUMNS acc_r ON ac_r.CONSTRAINT_NAME = acc_r.CONSTRAINT_NAME AND ac_r.OWNER = acc_r.OWNER AND acc.POSITION = acc_r.POSITION
                    WHERE ac.CONSTRAINT_TYPE = 'R'
                    AND ac.OWNER = ?
                    AND ac.TABLE_NAME = ?
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection(dbName).prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            Map<String, DamengForeignKey> foreignKeyMap = new HashMap<>();
            while (resultSet.next()) {
                String fkName = resultSet.getString("FK_NAME");
                String fkColumnName = resultSet.getString("FKCOLUMN_NAME");
                String pkColumnName = resultSet.getString("PKCOLUMN_NAME");
                DamengForeignKey foreignKey = foreignKeyMap.get(fkName);
                if (foreignKey == null) {
                    String pkTableName = resultSet.getString("PKTABLE_NAME");
                    String pkTableCat = resultSet.getString("PKTABLE_CAT");
                    String updateRule = resultSet.getString("UPDATE_RULE");
                    String deleteRule = resultSet.getString("DELETE_RULE");
                    foreignKey = new DamengForeignKey();
                    foreignKey.setName(fkName);
                    foreignKey.setUpdatePolicy(updateRule == null ? null : updateRule.toUpperCase());
                    foreignKey.setDeletePolicy(deleteRule == null ? null : deleteRule.toUpperCase());
                    foreignKey.setPrimaryKeyTable(pkTableName);
                    foreignKey.setPrimaryKeyDatabase(pkTableCat);
                    foreignKeyMap.put(fkName, foreignKey);
                }
                foreignKey.addColumn(fkColumnName);
                foreignKey.addPrimaryKeyColumn(pkColumnName);
            }
            DBUtil.close(resultSet);
            return new DamengForeignKeys(foreignKeyMap.values());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<DamengColumn> viewColumns(String dbName, String viewName) {
        try {
            if (StringUtil.isBlank(viewName)) {
                return Collections.emptyList();
            }
            String sql = """
                    SELECT
                        C.COLUMN_NAME AS "COLUMN_NAME",
                        CASE WHEN C.NULLABLE='Y' THEN 'YES' ELSE 'NO' END AS "IS_NULLABLE",
                        C.DATA_TYPE AS "COLUMN_TYPE",
                        CC.COMMENT$ AS "REMARKS",
                        C.DATA_DEFAULT AS "COLUMN_DEF",
                        CASE WHEN CONS.COLUMN_NAME IS NOT NULL THEN 'PRI' ELSE '' END AS "COLUMN_KEY",
                        '' AS "COLUMN_EXTRA",
                        C.COLUMN_ID AS ORDINAL_POSITION
                    FROM
                        ALL_TAB_COLUMNS C
                    LEFT JOIN
                        SYS.SYSCOLUMNCOMMENTS CC
                    ON
                        C.OWNER=CC.SCHNAME
                    AND
                        C.TABLE_NAME=CC.TVNAME
                    AND
                        C.COLUMN_NAME=CC.COLNAME
                    LEFT JOIN
                        ALL_CONS_COLUMNS CONS
                    ON
                        C.OWNER=CONS.OWNER
                    AND
                        C.TABLE_NAME=CONS.TABLE_NAME
                    AND
                        C.COLUMN_NAME=CONS.COLUMN_NAME
                    AND
                        EXISTS (SELECT 1 FROM ALL_CONSTRAINTS AC WHERE AC.OWNER=CONS.OWNER AND AC.CONSTRAINT_NAME=CONS.CONSTRAINT_NAME AND AC.CONSTRAINT_TYPE='P')
                    WHERE
                        C.OWNER=?
                    AND
                        C.TABLE_NAME=?
                    ORDER BY
                        C.COLUMN_ID
                    """;
            //            String sql = """
            //                    SELECT
            //                        '' AS COLUMN_EXTRA,
            //                        NULL AS COLUMN_KEY,
            //                        CC.COMMENTS AS REMARKS,
            //                        C.DATA_TYPE AS COLUMN_TYPE,
            //                        C.COLUMN_NAME AS COLUMN_NAME,
            //                        CASE WHEN C.NULLABLE='Y' THEN 'YES' ELSE 'NO' END AS IS_NULLABLE,
            //                        C.DATA_DEFAULT AS COLUMN_DEF,
            //                        NULL AS COLLATION_NAME,
            //                        NULL AS CHARSET_NAME,
            //                        C.COLUMN_ID AS ORDINAL_POSITION
            //                    FROM
            //                        ALL_TAB_COLUMNS C
            //                    LEFT JOIN
            //                        ALL_COL_COMMENTS CC ON C.OWNER=CC.OWNER AND C.TABLE_NAME=CC.TABLE_NAME AND C.COLUMN_NAME=CC.COLUMN_NAME
            //                    WHERE
            //                        C.OWNER = ?
            //                    AND
            //                        C.TABLE_NAME = ?
            //                    ORDER BY
            //                        C.COLUMN_ID
            //                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection(dbName).prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, viewName);
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            Map<String, DamengColumn> columns = new HashMap<>();
            while (resultSet.next()) {
                Object def = resultSet.getObject("COLUMN_DEF");
                String remarks = resultSet.getString("REMARKS");
                int position = resultSet.getInt("ORDINAL_POSITION");
                String nullable = resultSet.getString("IS_NULLABLE");
                String columnKey = resultSet.getString("COLUMN_KEY");
                String columnType = resultSet.getString("COLUMN_TYPE");
                String columnName = resultSet.getString("COLUMN_NAME");
                String columnExtra = resultSet.getString("COLUMN_EXTRA");
                DamengColumn column = new DamengColumn();
                column.initColumn(columnType, columnExtra);
                column.setDbName(dbName);
                column.setName(columnName);
                column.setComment(remarks);
                column.setDefaultValue(def);
                column.setPosition(position);
                column.setTableName(viewName);
                column.setNullable("yes".equalsIgnoreCase(nullable));
                column.setPrimaryKey("pri".equalsIgnoreCase(columnKey));
                columns.put(columnName, column);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);

            sql = "SELECT * FROM " + DBUtil.wrap(dbName, viewName, DBDialect.DAMENG) + " FETCH FIRST 1 ROWS ONLY";
            DBUtil.printSql(sql);
            PreparedStatement statement1 = this.connection(dbName).prepareStatement(sql);
            ResultSet resultSet1 = statement1.executeQuery();
            DBUtil.printMetaData(resultSet1);
            DamengColumns dbColumns = DamengHelper.parseColumns(resultSet1);
            DBUtil.close(resultSet1);
            DBUtil.close(statement1);

            // 初始化状态
            for (DamengColumn value : columns.values()) {
                DamengColumn dbColumn = dbColumns.column(value.getName());
                if (dbColumn != null) {
                    value.setNullable(dbColumn.isNullable());
                    value.setAutoIncrement(dbColumn.isAutoIncrement());
                }
                value.setTableName(viewName);
                value.initStatus();
            }
            // 返回排序后的数据
            return CollectionUtil.sort(columns.values(), Comparator.comparingInt(DamengColumn::getPosition));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<DamengRecord> viewRecords(String dbName, String viewName, Long start, Long limit, List<DamengRecordFilter> filters) {
        try {
            Connection connection = this.connection(dbName);
            StringBuilder builder = new StringBuilder("SELECT * FROM ");
            builder.append(DBUtil.wrap(dbName, viewName, DBDialect.DAMENG));
            String filterCondition = DamengConditionUtil.buildCondition(filters);
            if (StringUtil.isNotBlank(filterCondition)) {
                builder.append(" WHERE ").append(filterCondition);
            }
            if (start != null && limit != null) {
                builder.append(" OFFSET ").append(start).append(" ROWS FETCH NEXT ").append(limit).append(" ROWS ONLY");
            }
            String sql = builder.toString();
            DBUtil.printSql(sql);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            DBUtil.printMetaData(resultSet);
            List<DamengRecord> records = new ArrayList<>();
            boolean updatable = DamengHelper.isViewUpdatable(connection, dbName, viewName);
            DamengColumns columns = DamengHelper.parseColumns(resultSet);
            for (DamengColumn column : columns) {
                column.setTableName(viewName);
            }
            while (resultSet.next()) {
                DamengRecord record = new DamengRecord(columns, !updatable);
                for (DamengColumn column : columns) {
                    Object data = resultSet.getObject(column.getName());
                    //                    // 获取几何值
                    //                    if (column.supportGeometry()) {
                    //                        data = DamengHelper.getGeometryString(connection, data);
                    //                    }
                    record.putValue(column, data);
                }
                records.add(record);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return records;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public void createTable(DamengCreateTableParam param) {
        Connection connection = null;
        try {
            String dbName = param.dbName();
            connection = this.connection(dbName);
            Statement statement = connection.createStatement();
            List<String> sqlList = DamengTableCreateSqlGenerator.generateSql(param);
            connection.setAutoCommit(false);
            for (String sqlStr : sqlList) {
                DBUtil.printSql(sqlStr);
                statement.executeUpdate(sqlStr);
            }
            connection.commit();
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            DBUtil.rollback(connection);
            throw new DBException(ex);
        }
    }

    public void alertTable(DamengAlertTableParam param) {
        Connection connection = null;
        try {
            List<String> sqlList = DamengTableAlertSqlGenerator.generateSql(param);
            // 无变化
            if (CollectionUtil.isEmpty(sqlList)) {
                return;
            }
            String dbName = param.getDbName();
            connection = this.connection(dbName);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            for (String sql : sqlList) {
                if (StringUtil.isBlank(sql)) {
                    continue;
                }
                DBUtil.printSql(sql);
                statement.executeUpdate(sql);
            }
            connection.commit();
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            DBUtil.rollback(connection);
            throw new DBException(ex);
        }
    }

    @Deprecated
    public boolean existTable(String dbName, String tableName) {
        boolean result;
        try {
            String sql = "SELECT COUNT(*) FROM ALL_TABLES WHERE OWNER = ? AND TABLE_NAME = ?";
            PreparedStatement statement = this.connection(dbName).prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            result = resultSet.next() && resultSet.getInt(1) > 0;
            DBUtil.close(resultSet);
            DBUtil.close(statement);
        } catch (Exception ex) {
            throw new DBException(ex);
        }
        return result;
    }

    public void renameTable(String dbName, String oldTableName, String newTableName) {
        try {
            // 达梦用 ALTER TABLE ... RENAME TO 代替MySQL的 RENAME TABLE
            String sql = "ALTER TABLE " + DBUtil.wrap(dbName, oldTableName, DBDialect.DAMENG)
                    + " RENAME TO " + DBUtil.wrap(newTableName, DBDialect.DAMENG);
            Connection connection = this.connection(dbName);
            Statement statement = connection.createStatement();
            DBUtil.printSql(sql);
            statement.execute(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    /**
     * 重命名事件
     *
     * @param dbName       库名称
     * @param oldEventName 事件名称
     * @param newEventName 新事件名称
     */
    public void renameEvent(String dbName, String oldEventName, String newEventName) {
        // 达梦使用调度器JOB代替MySQL EVENT，重命名操作需通过DBMS_SCHEDULER包实现，暂不支持
        throw new DBException("达梦调度器JOB重命名暂不支持");
    }

    public void clearTable(String dbName, String tableName) {
        try {
            Statement statement = this.connection(dbName).createStatement();
            String sql = "DELETE FROM " + DBUtil.wrap(dbName, tableName, DBDialect.DAMENG);
            DBUtil.printSql(sql);
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void truncateTable(String dbName, String tableName) {
        try {
            Statement statement = this.connection(dbName).createStatement();
            String sql = "TRUNCATE TABLE " + DBUtil.wrap(dbName, tableName, DBDialect.DAMENG);
            DBUtil.printSql(sql);
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void dropTable(String dbName, String tableName) {
        try {
            Statement statement = this.connection(dbName).createStatement();
            String sql = "DROP TABLE " + DBUtil.wrap(dbName, tableName, DBDialect.DAMENG);
            DBUtil.printSql(sql);
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<String> charsets() {
        if (this.hasProperty("charsets")) {
            return this.getProperty("charsets");
        }
        try {
            // 达梦常用字符集列表（系统视图查询可能因版本差异失败，直接返回静态列表）
            List<String> charsets = new ArrayList<>();
            charsets.add("UTF-8");
            charsets.add("GBK");
            charsets.add("GB18030");
            charsets.add("GB2312");
            charsets.add("ISO-8859-1");
            this.putProperty("charsets", charsets);
            return charsets;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public List<String> collation(String charset) {
        try {
            Map<String, List<String>> collations = this.getProperty("collation");
            if (collations == null) {
                collations = new HashMap<>();
                this.putProperty("collations", collations);
            }
            charset = charset.toUpperCase();
            if (collations.containsKey(charset)) {
                return collations.get(charset.toUpperCase());
            }
            // 达梦排序规则：返回默认列表
            List<String> list = new ArrayList<>();
            list.add("BINARY");
            list.add("BINARY_CI");
            collations.put(charset, list);
            return list;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public boolean existDatabase(String dbName) {
        boolean result = false;
        try {
            String sql = "SELECT COUNT(*) FROM ALL_USERS WHERE USERNAME = ?";
            PreparedStatement statement = this.connection(dbName).prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(1) > 0;
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
        } catch (Exception ex) {
            throw new DBException(ex);
        }
        return result;
    }

    public void createDatabase(DBDatabase database) {
        try {
            // 达梦用"CREATE SCHEMA"代替MySQL的"CREATE DATABASE"
            StringBuilder builder = new StringBuilder("CREATE SCHEMA ");
            builder.append(DBUtil.wrap(database.getName(), DBDialect.DAMENG));
            String sql = builder.toString();
            DBUtil.printSql(sql);
            Statement statement = this.connection().createStatement();
            statement.execute(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public boolean alterDatabase(DBDatabase database) {
        try {
            // 达梦不支持MySQL式的 ALTER DATABASE CHARACTER SET/COLLATE，跳过
            return true;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public String databaseCollation(String dbName) {
        // 达梦不使用MySQL式的排序规则，返回null
        return null;
    }

    public boolean dropDatabase(String dbName) {
        try {
            String sql = "DROP SCHEMA " + DBUtil.wrap(dbName, DBDialect.DAMENG) + " CASCADE";
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
            return true;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public DamengQueryResults<DamengExplainResult> explainSql(String dbName, String sql) {
        DamengQueryResults<DamengExplainResult> results = new DamengQueryResults<>();
        Connection connection = null;
        try {
            DBUtil.printSql(sql);
            DBSqlParser parser = DBSqlParser.getParser(sql, DBDialect.DAMENG);
            List<String> list = parser.parseSql();
            connection = this.connection(dbName);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            for (String execSql : list) {
                DamengExplainResult result = new DamengExplainResult();
                try {
                    execSql = "EXPLAIN PLAN FOR " + execSql.stripLeading();
                    result.setSql(execSql);
                    long startTime = System.nanoTime();
                    ResultSet resultSet = statement.executeQuery(execSql);
                    result.parseResult(resultSet, connection);
                    result.setUsed(System.nanoTime() - startTime);
                    DBUtil.close(resultSet);
                    result.setSuccess(true);
                } catch (SQLException ex) {
                    result.setMsg(ex.toString());
                }
                results.addResult(result);
            }
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            DBUtil.rollback(connection);
            results.parseError(ex);
        }
        return results;
    }

    public DamengExecuteResult executeSingleSql(String dbName, String sql) {
        Connection connection = null;
        DamengExecuteResult result = new DamengExecuteResult();
        result.setSql(sql);
        try {
            DBUtil.printSql(sql);
            DBSqlParser parser = DBSqlParser.getParser(sql, DBDialect.DAMENG);
            String execSql = parser.parseSingleSql();
            connection = this.connection(dbName);
            Statement statement = connection.createStatement();
            try {
                long startTime = System.nanoTime();
                boolean isQuery = statement.execute(execSql);
                if (isQuery) {
                    ResultSet resultSet = statement.getResultSet();
                    if (parser.isSingle()) {
                        result.setFullColumn(parser.isFullColumn());
                    } else {
                        result.setFullColumn(DBUtil.isFullColumn(execSql));
                    }
                    result.parseResult(resultSet, connection, !parser.isSelect());
                    DBUtil.close(resultSet);
                    result.setSuccess(true);
                } else {
                    connection.setAutoCommit(false);
                    int updateCount = statement.getUpdateCount();
                    connection.commit();
                    result.setUpdateCount(updateCount);
                    result.setSuccess(true);
                }
                long endTime = System.nanoTime();
                result.setUsed(endTime - startTime);
            } catch (SQLException ex) {
                result.setMsg(ex.getMessage());
            }
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            DBUtil.rollback(connection);
        }
        return result;
    }

    public void executeSqlSimple(String dbName, String sql) {
        Connection connection = null;
        try {
            DBUtil.printSql(sql);
            connection = this.connection(dbName);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute(sql);
            connection.commit();
            DBUtil.close(statement);
        } catch (Exception ex) {
            JulLog.warn("sql:\n{}", sql);
            ex.printStackTrace();
            DBUtil.rollback(connection);
            throw new DBException(ex);
        }
    }

    public int insertBatch(String dbName, List<String> sqlList, boolean parallel) {
        Connection connection = null;
        int result = 0;
        try {
            connection = parallel ? this.newConnection(dbName) : this.connection(dbName);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            for (String sql : sqlList) {
                DBUtil.printSql(sql);
                statement.addBatch(sql);
            }
            int[] results = statement.executeBatch();
            connection.commit();
            DBUtil.close(statement);
            if (parallel) {
                DBUtil.close(connection);
            }
            for (int i : results) {
                result += i;
            }
        } catch (Exception ex) {
            JulLog.warn("sqlList:{}", sqlList);
            ex.printStackTrace();
            DBUtil.rollback(connection);
            throw new DBException(ex);

        }
        return result;
    }

    public DbType dbType() {
        return DBDialect.DAMENG.dbType();
    }

    public DBDialect dialect() {
        return DBDialect.DAMENG;
    }

    public List<DamengFunction> selectFunctions(String dbName) {
        DamengSelectFunctionParam param = new DamengSelectFunctionParam();
        param.setFull(true);
        param.setDbName(dbName);
        return this.selectFunctions(param);
    }

    public List<DamengFunction> selectFunctionsSimple(String dbName) {
        DamengSelectFunctionParam param = new DamengSelectFunctionParam();
        param.setFull(false);
        param.setDbName(dbName);
        return this.selectFunctions(param);
    }

    public List<DamengFunction> selectFunctions(DamengSelectFunctionParam param) {
        try {
            String dbName = param.getDbName();
            List<DamengFunction> list = new ArrayList<>();
            String sql = """
                    SELECT
                        O.OBJECT_NAME AS "ROUTINE_NAME",
                        P.AUTHID AS "SECURITY_TYPE",
                        P.AGGREGATE,
                        P.PIPELINED,
                        P.DETERMINISTIC
                    FROM 
                        ALL_OBJECTS O
                    LEFT JOIN 
                        ALL_PROCEDURES P ON O.OWNER = P.OWNER AND O.OBJECT_NAME = P.OBJECT_NAME
                    WHERE 
                        O.OWNER = ?
                    AND 
                        O.OBJECT_TYPE = 'FUNCTION'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.functionConnection(dbName).prepareStatement(sql);
            statement.setString(1, dbName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            // 遍历结果集
            while (resultSet.next()) {
                DamengFunction function = new DamengFunction();
                String name = resultSet.getString("ROUTINE_NAME");
                List<DamengRoutineParam> params = this.listFunctionParam(dbName, name);
                String securityType = resultSet.getString("SECURITY_TYPE");
                String aggregate = resultSet.getString("AGGREGATE");
                String pipelined = resultSet.getString("PIPELINED");
                String deterministic = resultSet.getString("DETERMINISTIC");
                StringBuilder characteristic = new StringBuilder();
                if (param.isFull()) {
                    String createDefinition = this.showCreateFunction(dbName, name);
                    String definition = createDefinition.substring(createDefinition.indexOf("\nAS\n") + 4);
                    if (StringUtil.contains(createDefinition, "PARALLEL_ENABLE")) {
                        characteristic.append(",PARALLEL_ENABLE");
                    }
                    if (StringUtil.contains(createDefinition, "RESULT_CACHE")) {
                        characteristic.append(",RESULT_CACHE");
                    }
                    function.setDefinition(definition);
                    function.setCreateDefinition(createDefinition);
                }
                if (StringUtil.equalsIgnoreCase("YES", aggregate)) {
                    characteristic.append(",AGGREGATE");
                }
                if (StringUtil.equalsIgnoreCase("YES", pipelined)) {
                    characteristic.append(",PIPELINED");
                }
                if (StringUtil.equalsIgnoreCase("YES", deterministic)) {
                    characteristic.append(",DETERMINISTIC");
                }
                if (!characteristic.isEmpty()) {
                    function.setCharacteristic(characteristic.substring(1));
                }
                function.setName(name);
                function.setDbName(dbName);
                function.setParams(params);
                function.setSecurityType(securityType);
                list.add(function);
            }
            // 关闭连接和释放资源
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return list;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public List<DamengProcedure> selectProcedures(String dbName) {
        DamengSelectProcedureParam param = new DamengSelectProcedureParam();
        param.setFull(true);
        param.setDbName(dbName);
        return this.selectProcedures(param);
    }

    public List<DamengProcedure> selectProceduresSimple(String dbName) {
        DamengSelectProcedureParam param = new DamengSelectProcedureParam();
        param.setFull(false);
        param.setDbName(dbName);
        return this.selectProcedures(param);
    }

    public List<DamengProcedure> selectProcedures(DamengSelectProcedureParam param) {
        try {
            String dbName = param.getDbName();
            List<DamengProcedure> list = new ArrayList<>();
            String sql = """
                    SELECT
                        O.OBJECT_NAME AS "ROUTINE_NAME",
                        P.AUTHID AS "SECURITY_TYPE",
                        P.AGGREGATE,
                        P.PIPELINED,
                        P.DETERMINISTIC
                    FROM 
                        ALL_OBJECTS O
                    LEFT JOIN
                        ALL_PROCEDURES P ON O.OWNER = P.OWNER AND O.OBJECT_NAME = P.OBJECT_NAME
                    WHERE 
                        O.OWNER = ?
                    AND 
                        O.OBJECT_TYPE = 'PROCEDURE'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.procedureConnection(dbName).prepareStatement(sql);
            statement.setString(1, dbName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            // 遍历结果集
            while (resultSet.next()) {
                DamengProcedure procedure = new DamengProcedure();
                String name = resultSet.getString("ROUTINE_NAME");
                String aggregate = resultSet.getString("AGGREGATE");
                String pipelined = resultSet.getString("PIPELINED");
                String securityType = resultSet.getString("SECURITY_TYPE");
                String deterministic = resultSet.getString("DETERMINISTIC");
                StringBuilder characteristic = new StringBuilder();
                if (param.isFull()) {
                    List<DamengRoutineParam> params = this.listProcedureParam(dbName, name);
                    procedure.setParams(params);
                    String createDefinition = this.showCreateProcedure(dbName, name);
                    String definition = createDefinition.substring(createDefinition.indexOf("\nAS\n") + 4);
                    if (StringUtil.contains(createDefinition, "PARALLEL_ENABLE")) {
                        characteristic.append(",PARALLEL_ENABLE");
                    }
                    if (StringUtil.contains(createDefinition, "RESULT_CACHE")) {
                        characteristic.append(",RESULT_CACHE");
                    }
                    procedure.setDefinition(definition);
                    procedure.setCreateDefinition(createDefinition);
                }
                if (StringUtil.equalsIgnoreCase("YES", aggregate)) {
                    characteristic.append(",AGGREGATE");
                }
                if (StringUtil.equalsIgnoreCase("YES", pipelined)) {
                    characteristic.append(",PIPELINED");
                }
                if (StringUtil.equalsIgnoreCase("YES", deterministic)) {
                    characteristic.append(",DETERMINISTIC");
                }
                if (!characteristic.isEmpty()) {
                    procedure.setCharacteristic(characteristic.substring(1));
                }
                procedure.setName(name);
                procedure.setDbName(dbName);
                procedure.setSecurityType(securityType);
                list.add(procedure);
            }
            // 关闭连接和释放资源
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return list;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public DamengProcedure selectProcedure(String dbName, String procedureName) {
        DamengSelectProcedureParam param = new DamengSelectProcedureParam();
        param.setFull(true);
        param.setDbName(dbName);
        param.setProcedureName(procedureName);
        return this.selectProcedure(param);
    }

    public DamengProcedure selectProcedureSimple(String dbName, String procedureName) {
        DamengSelectProcedureParam param = new DamengSelectProcedureParam();
        param.setFull(false);
        param.setDbName(dbName);
        param.setProcedureName(procedureName);
        return this.selectProcedure(param);
    }

    public DamengProcedure selectProcedure(DamengSelectProcedureParam param) {
        try {
            String dbName = param.getDbName();
            String produceName = param.getProcedureName();
            String sql = """
                    SELECT
                        P.AUTHID AS "SECURITY_TYPE",
                        P.AGGREGATE,
                        P.PIPELINED,
                        P.DETERMINISTIC
                    FROM 
                        ALL_OBJECTS O
                    LEFT JOIN 
                        ALL_PROCEDURES P ON O.OWNER = P.OWNER AND O.OBJECT_NAME = P.OBJECT_NAME
                    WHERE 
                        O.OWNER = ?
                    AND 
                        O.OBJECT_NAME = ?
                    AND 
                        O.OBJECT_TYPE = 'PROCEDURE'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.procedureConnection(dbName).prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, produceName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            DamengProcedure procedure = new DamengProcedure();
            procedure.setDbName(dbName);
            procedure.setName(produceName);
            // 遍历结果集
            while (resultSet.next()) {
                String aggregate = resultSet.getString("AGGREGATE");
                String pipelined = resultSet.getString("PIPELINED");
                String securityType = resultSet.getString("SECURITY_TYPE");
                String deterministic = resultSet.getString("DETERMINISTIC");
                StringBuilder characteristic = new StringBuilder();
                if (param.isFull()) {
                    List<DamengRoutineParam> params = this.listProcedureParam(dbName, produceName);
                    procedure.setParams(params);
                    String createDefinition = this.showCreateProcedure(dbName, produceName);
                    String definition = createDefinition.substring(createDefinition.indexOf("\nAS\n") + 4);
                    if (StringUtil.contains(createDefinition, "PARALLEL_ENABLE")) {
                        characteristic.append(",PARALLEL_ENABLE");
                    }
                    if (StringUtil.contains(createDefinition, "RESULT_CACHE")) {
                        characteristic.append(",RESULT_CACHE");
                    }
                    procedure.setDefinition(definition);
                    procedure.setCreateDefinition(createDefinition);
                }
                if (StringUtil.equalsIgnoreCase("YES", aggregate)) {
                    characteristic.append(",AGGREGATE");
                }
                if (StringUtil.equalsIgnoreCase("YES", pipelined)) {
                    characteristic.append(",PIPELINED");
                }
                if (StringUtil.equalsIgnoreCase("YES", deterministic)) {
                    characteristic.append(",DETERMINISTIC");
                }
                if (!characteristic.isEmpty()) {
                    procedure.setCharacteristic(characteristic.substring(1));
                }
                procedure.setDbName(dbName);
                procedure.setSecurityType(securityType);
            }
            // 关闭连接和释放资源
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return procedure;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void dropProcedure(String dbName, DamengProcedure routine) {
        try {
            String sql = "DROP PROCEDURE IF EXISTS " + DBUtil.wrap(dbName, routine.getName(), DBDialect.DAMENG);
            DBUtil.printSql(sql);
            Statement statement = this.procedureConnection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    /**
     * 创建过程
     *
     * @param param 参数
     */
    public void createProcedure(DamengCreateProcedureParam param) {
        try {
            String sql = DamengProcedureCreateSqlGenerator.generateSqlSingle(param);
            DBUtil.printSql(sql);
            Statement statement = this.procedureConnection(param.getDbName()).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    /**
     * 修改过程
     *
     * @param param 参数
     */
    public void alertProcedure(DamengAlertProcedureParam param) {
        try {
            String sql = DamengProcedureAlertSqlGenerator.generateSqlSingle(param);
            DBUtil.printSql(sql);
            Statement statement = this.procedureConnection(param.getDbName(), null).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void dropFunction(String dbName, DamengFunction function) {
        try {
            String sql = "DROP function IF EXISTS " + DBUtil.wrap(dbName, function.getName(), DBDialect.DAMENG);
            DBUtil.printSql(sql);
            Statement statement = this.functionConnection(dbName, null).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public DamengFunction selectFunction(String dbName, String functionName) {
        DamengSelectFunctionParam param = new DamengSelectFunctionParam();
        param.setFull(true);
        param.setDbName(dbName);
        param.setFunctionName(functionName);
        return this.selectFunction(param);
    }

    public DamengFunction selectFunctionSimple(String dbName, String functionName) {
        DamengSelectFunctionParam param = new DamengSelectFunctionParam();
        param.setFull(false);
        param.setDbName(dbName);
        param.setFunctionName(functionName);
        return this.selectFunction(param);
    }

    /**
     * 查询函数
     *
     * @param param 参数
     * @return 结果
     */
    public DamengFunction selectFunction(DamengSelectFunctionParam param) {
        try {
            String dbName = param.getDbName();
            String functionName = param.getFunctionName();
            String sql = """
                    SELECT
                        P.AUTHID AS "SECURITY_TYPE",
                        P.AGGREGATE,
                        P.PIPELINED,
                        P.DETERMINISTIC
                    FROM 
                        ALL_OBJECTS O
                    LEFT JOIN 
                        ALL_PROCEDURES P ON O.OWNER = P.OWNER AND O.OBJECT_NAME = P.OBJECT_NAME
                    WHERE 
                        O.OWNER = ?
                    AND 
                        O.OBJECT_NAME = ?
                    AND 
                        O.OBJECT_TYPE = 'FUNCTION'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.functionConnection(dbName, null).prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, functionName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            DamengFunction function = new DamengFunction();
            function.setDbName(dbName);
            function.setName(functionName);
            // 遍历结果集
            while (resultSet.next()) {
                String aggregate = resultSet.getString("AGGREGATE");
                String pipelined = resultSet.getString("PIPELINED");
                String securityType = resultSet.getString("SECURITY_TYPE");
                String deterministic = resultSet.getString("DETERMINISTIC");
                StringBuilder characteristic = new StringBuilder();
                if (param.isFull()) {
                    List<DamengRoutineParam> params = this.listFunctionParam(dbName, functionName);
                    function.setParams(params);
                    String createDefinition = this.showCreateFunction(dbName, functionName);
                    String definition = createDefinition.substring(createDefinition.indexOf("\nAS\n") + 4);
                    if (StringUtil.contains(createDefinition, "PARALLEL_ENABLE")) {
                        characteristic.append(",PARALLEL_ENABLE");
                    }
                    if (StringUtil.contains(createDefinition, "RESULT_CACHE")) {
                        characteristic.append(",RESULT_CACHE");
                    }
                    function.setDefinition(definition);
                    function.setCreateDefinition(createDefinition);
                }
                if (StringUtil.equalsIgnoreCase("YES", aggregate)) {
                    characteristic.append(",AGGREGATE");
                }
                if (StringUtil.equalsIgnoreCase("YES", pipelined)) {
                    characteristic.append(",PIPELINED");
                }
                if (StringUtil.equalsIgnoreCase("YES", deterministic)) {
                    characteristic.append(",DETERMINISTIC");
                }
                if (!characteristic.isEmpty()) {
                    function.setCharacteristic(characteristic.substring(1));
                }
                function.setDbName(dbName);
                function.setSecurityType(securityType);
            }
            // 关闭连接和释放资源
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return function;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    /**
     * 创建函数
     *
     * @param param 参数
     */
    public void createFunction(DamengCreateFunctionParam param) {
        try {
            String sql = DamengFunctionCreateSqlGenerator.generateSqlSingle(param);
            DBUtil.printSql(sql);
            Statement statement = this.functionConnection(param.getDbName()).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public DamengRecord selectRecord(DamengSelectRecordParam param) {
        try {
            String dbName = param.getDbName();
            String tableName = param.getTableName();
            Connection connection = this.connection(dbName);
            DamengRecordPrimaryKey primaryKey = param.getPrimaryKey();
            StringBuilder builder = new StringBuilder("SELECT * FROM ");
            builder.append(DBUtil.wrap(dbName, tableName, DBDialect.DAMENG))
                    .append(" WHERE ")
                    .append(DBUtil.wrap(primaryKey.getColumnName(), DBDialect.DAMENG))
                    .append(" = ?");
            String sql = builder.toString();
            DBUtil.printSql(sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, primaryKey.data());
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            DamengColumns columns = DamengHelper.parseColumns(resultSet);
            DamengRecord record = new DamengRecord(columns);
            while (resultSet.next()) {
                for (DamengColumn column : columns) {
                    Object data = resultSet.getObject(column.getName());
                    //                    // 获取几何值
                    //                    if (column.supportGeometry()) {
                    //                        data = DamengHelper.getGeometryString(connection, data);
                    //                    }
                    record.putValue(column, data);
                }
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return record;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public String selectClientCharacter() {
        // 达梦客户端字符集，返回默认值
        return "UTF-8";
    }

    public boolean existAutoIncrement(String dbName, String tableName) {
        try {
            Connection connection = this.connection(dbName);
            // String sql = "SELECT COUNT(*) FROM ALL_CONSTRAINTS WHERE OWNER = ? AND TABLE_NAME = ? AND CONSTRAINT_TYPE = 'P'";
            String sql = """
                    SELECT 
                        COUNT(*)
                    FROM 
                        SYSCOLUMNS C, SYSOBJECTS O
                    WHERE 
                        C.ID = O.ID
                    AND 
                        O.NAME = ?
                    AND 
                        O.SCHID = (SELECT ID FROM SYSOBJECTS WHERE NAME = ? AND TYPE$ = 'SCH')
                    AND 
                        C.INFO2 = 1;
                    """;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, tableName);
            stmt.setString(2, dbName);
            ResultSet resultSet = stmt.executeQuery();
            DBUtil.printMetaData(resultSet);
            boolean exist = resultSet.next() && resultSet.getInt(1) > 0;
            DBUtil.close(resultSet);
            DBUtil.close(stmt);
            return exist;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<String> selectePrimaryKeys(String dbName, String tableName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = "SELECT *  FROM ALL_CONSTRAINTS WHERE OWNER = ? AND TABLE_NAME = ? AND CONSTRAINT_TYPE = 'P'";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, dbName);
            stmt.setString(2, tableName);
            ResultSet resultSet = stmt.executeQuery();
            DBUtil.printMetaData(resultSet);
            List<String> primaryKeys = new ArrayList<>();
            while (resultSet.next()) {
                primaryKeys.add(resultSet.getString("CONSTRAINT_NAME"));
            }
            DBUtil.close(resultSet);
            DBUtil.close(stmt);
            return primaryKeys;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public DamengConnect getDbConnect() {
        return dbConnect;
    }

    /**
     * 克隆表
     *
     * @param dbName        数据库
     * @param tableName     表名称
     * @param includeRecord 是否包含数据
     * @return 克隆表名称
     */
    public String cloneTable(String dbName, String tableName, boolean includeRecord) {
        // 查询检查
        DamengChecks checks = this.checks(dbName, tableName);
        //        // 查询索引
        //        DamengIndexes indexes = this.indexes(dbName, tableName);
        // 查询触发器
        DamengTriggers triggers = this.selectTriggers(dbName, tableName);
        // 查询外键
        DamengForeignKeys foreignKeys = this.foreignKeys(dbName, tableName);
        if (checks != null) {
            for (DamengCheck check : checks) {
                check.setName(check.getName() + DBUtil.genCloneName());
                check.clearStatus();
                check.clearOriginalData();
                check.setCreated(true);
            }
        }
        if (triggers != null) {
            for (DamengTrigger trigger : triggers) {
                trigger.setName(trigger.getName() + DBUtil.genCloneName());
                trigger.clearStatus();
                trigger.clearOriginalData();
                trigger.setCreated(true);
            }
        }
        //         if (indexes != null) {
        //             for (DamengIndex index : indexes) {
        //                 index.setName(index.getName() + DBUtil.genCloneName());
        //                 index.clearStatus();
        //                 index.clearOriginalData();
        //                 index.setCreated(true);
        //             }
        //         }
        if (foreignKeys != null) {
            for (DamengForeignKey foreignKey : foreignKeys) {
                foreignKey.setName(foreignKey.getName() + DBUtil.genCloneName());
                foreignKey.clearStatus();
                foreignKey.clearOriginalData();
                foreignKey.setCreated(true);
            }
        }

        String newTableName = tableName + DBUtil.genCloneName();
        try {
            Connection connection = this.connection(dbName);
            // 克隆基本的表结构
            //            String sql = "CREATE TABLE " + DBUtil.wrap(dbName, newTableName, DBDialect.DAMENG);
            //            if (!includeRecord) {
            //                sql = sql + " AS SELECT * FROM " + DBUtil.wrap(dbName, tableName, DBDialect.DAMENG);
            //                sql = sql + " WHERE 1 = 0";
            //            } else {
            //                sql = sql + " LIKE " + DBUtil.wrap(dbName, tableName, DBDialect.DAMENG);
            //            }
            String sql = this.showCreateTable(dbName, tableName);
            sql = sql.replace(DBUtil.wrap(dbName, tableName, DBDialect.DAMENG), DBUtil.wrap(dbName, newTableName, DBDialect.DAMENG));
            DBUtil.printSql(sql);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.execute();
            DBUtil.close(stmt);
            DamengTable table = new DamengTable();
            table.setDbName(dbName);
            table.setName(newTableName);

            // 克隆表结构的检查、外键、触发器
            DamengAlertTableParam alertTableParam = new DamengAlertTableParam();
            alertTableParam.setTable(table);
            alertTableParam.setChecks(checks);
            //            alertTableParam.setIndexes(indexes);
            alertTableParam.setTriggers(triggers);
            alertTableParam.setForeignKeys(foreignKeys);
            this.alertTable(alertTableParam);

            // 克隆数据
            if (includeRecord) {
                // 查询字段列表
                DamengSelectColumnParam param = new DamengSelectColumnParam();
                param.setDbName(dbName);
                param.setTableName(newTableName);
                DamengColumns columns = this.selectColumns(param);
                StringBuilder builder = new StringBuilder();
                for (DamengColumn column : columns) {
                    builder.append(DBUtil.wrap(column.getName(), DBDialect.DAMENG))
                            .append(" ,");
                }
                StringUtil.deleteLast(builder, ",");

                // 处理数据
                String sql1 = "SET IDENTITY_INSERT " + DBUtil.wrap(newTableName, DBDialect.DAMENG) + " ON;";
                String sql2 = "INSERT INTO " + DBUtil.wrap(dbName, newTableName, this.dialect())
                        + "(" + builder + ")"
                        + " SELECT " + builder + " FROM " + DBUtil.wrap(dbName, tableName, this.dialect());
                String sql3 = "SET IDENTITY_INSERT " + DBUtil.wrap(newTableName, DBDialect.DAMENG) + " OFF;";
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();
                this.printSql(sql1);
                statement.execute(sql1);
                this.printSql(sql2);
                statement.executeUpdate(sql2);
                this.printSql(sql3);
                statement.execute(sql3);
                connection.commit();
                DBUtil.close(statement);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
        return newTableName;
    }

    public String iid() {
        return this.dbConnect == null ? null : this.dbConnect.getId();
    }

    /// **
    // * 获取视图更新标志位
    // *
    // * @param viewName 定义
    // * @return 结果
    // */
    //public String isViewUpdateable(String viewName) {
    //    try {
    //        String sql = """
    //                    SELECT
    //                        READ_ONLY
    //                    FROM
    //                        USER_VIEWS
    //                    WHERE
    //                        VIEW_NAME = ?
    //                """;
    //        DBUtil.printSql(sql);
    //        PreparedStatement statement = this.connection().prepareStatement(sql);
    //        statement.setString(1, viewName);
    //        // 执行SQL查询并获取结果集
    //        ResultSet resultSet = statement.executeQuery();
    //        // 打印元数据
    //        DBUtil.printMetaData(resultSet);
    //        if (resultSet.next()) {
    //            return resultSet.getString(1);
    //        }
    //    } catch (Exception ex) {
    //        ex.printStackTrace();
    //    }
    //    return null;
    //}
    public List<DamengRoutineParam> listRoutineParam(String dbName, String routineName, String routineType) throws Exception {
        try {
            Connection connection = this.connection(dbName);
            String sql = """
                    
                        SELECT
                    	a.POSITION,
                    	a.DATA_TYPE,
                    	a.DATA_LENGTH AS SIZE,
                    	a.DATA_SCALE AS DIGITS,
                    	a.IN_OUT AS PARAMETER_MODE,
                    	a.ARGUMENT_NAME AS PARAMETER_NAME,
                    	a.CHARACTER_SET_NAME
                    FROM
                    	ALL_ARGUMENTS a
                    WHERE
                    	a.OWNER = ?
                    AND
                    	a.OBJECT_NAME = ?
                    AND
                    	a.PACKAGE_NAME IS NULL
                    
                    """;
            List<DamengRoutineParam> params = new
                    ArrayList<>();
            PreparedStatement statement = connection.
                    prepareStatement(sql);
            statement.setString(1, dbName);
            statement.
                    setString(2,
                            routineName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery
                    ();
            while (resultSet.next()) {
                DamengRoutineParam param = new DamengRoutineParam();
                param.setSize(resultSet.getInt("SIZE"));
                param.setDigits(resultSet.getInt("DIGITS"));
                param.setPosition(resultSet.getInt("POSITION"));
                param.setType(resultSet.getString("DATA_TYPE"));
                param.setName(resultSet.getString("PARAMETER_NAME"));
                param.setMode(resultSet.getString("PARAMETER_MODE"));
                param.
                        setCharset(
                                resultSet.getString("CHARACTER_SET_NAME"));
                params.add(param);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return params;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<DamengRoutineParam> listFunctionParam(String dbName, String functionName) throws Exception {
        return listRoutineParam(dbName, functionName, "FUNCTION");
    }

    public List<DamengRoutineParam> listProcedureParam(String dbName, String procedureName) throws Exception {
        return listRoutineParam(dbName, procedureName, "PROCEDURE");
    }

    /**
     * 打印sql
     *
     * @param sql sql
     */
    private void printSql(String sql) {
        DBUtil.printSql(sql);
        String compressedSql = sql;
        try {
            // 压缩sql
            SQLUtils.FormatOption formatOption = new SQLUtils.FormatOption();
            formatOption.setUppCase(true);
            formatOption.setPrettyFormat(false);
            compressedSql = SQLUtils.format(sql, this.dbType(), formatOption);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        DamengEventUtil.printSql(compressedSql, this.dbConnect);
    }
}
