package cn.oyzh.easydameng.dameng;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.condition.DamengConditionUtil;
import cn.oyzh.easydameng.db.DBConnConfig;
import cn.oyzh.easydameng.db.DBConnState;
import cn.oyzh.easydameng.db.DBConnectionManager;
import cn.oyzh.easydameng.db.DBDatabase;
import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.db.DBFeature;
import cn.oyzh.easydameng.generator.routine.DamengFunctionSqlGenerator;
import cn.oyzh.easydameng.dameng.check.DamengCheck;
import cn.oyzh.easydameng.dameng.check.DamengChecks;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.column.DamengSelectColumnParam;
import cn.oyzh.easydameng.dameng.event.DamengEvent;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKey;
import cn.oyzh.easydameng.dameng.foreignKey.DamengForeignKeys;
import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.easydameng.dameng.index.DamengIndex;
import cn.oyzh.easydameng.dameng.index.DamengIndexes;
import cn.oyzh.easydameng.dameng.procedure.DamengProcedure;
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
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.dameng.table.DamengSelectTableParam;
import cn.oyzh.easydameng.dameng.trigger.DamengTrigger;
import cn.oyzh.easydameng.dameng.trigger.DamengTriggers;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.domain.DamengConnect;
import cn.oyzh.easydameng.event.DamengEventUtil;
import cn.oyzh.easydameng.exception.DBException;
import cn.oyzh.easydameng.exception.ReadonlyOperationException;
import cn.oyzh.easydameng.generator.event.EventAlertSqlGenerator;
import cn.oyzh.easydameng.generator.event.EventCreateSqlGenerator;
import cn.oyzh.easydameng.generator.routine.DamengProcedureSqlGenerator;
import cn.oyzh.easydameng.generator.table.DamengTableAlertSqlGenerator;
import cn.oyzh.easydameng.generator.table.DamengTableCreateSqlGenerator;
import cn.oyzh.easydameng.sql.DBSqlParser;
import cn.oyzh.easydameng.util.DBUtil;
import com.alibaba.druid.DbType;
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
public class DamengClient {

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

    public Connection functionConnection(String dbName, String schema) throws SQLException, ClassNotFoundException {
        Connection connection = this.connectionManager.getFunctionConnection(dbName, schema);
        if (this.isInvalid(connection)) {
            connection = this.initConnection(this.connConfig, dbName, this.dbConnect.getUser(), this.dbConnect.getPassword());
            this.connectionManager.addFunctionConnection(dbName, schema, connection);
        }
        connection.setAutoCommit(true);
        return connection;
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
        String host = connConfig.getConnectionString(this.dialect());
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

    public DamengClient(DamengConnect dbInfo) {
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
            DBSqlParser parser = DBSqlParser.getParser(sql, this.dialect());
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
                            result.setFullColumn(DBUtil.isFullColumn(execSql, this.dbType()));
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

    public int procedureSize(String dbName, String schema) {
        // int size = 0;
        // try {
        //     Connection connection = this.procedureConnection(dbName, schema);
        //     DatabaseMetaData metaData = connection.getMetaData();
        //     ResultSet resultSet = metaData.getProcedures(dbName, schema, "%");
        //     DBUtil.printMetaData(resultSet);
        //     while (resultSet.next()) {
        //         if (DBUtil.checkProcedureType(resultSet, dbName)) {
        //             size++;
        //         }
        //     }
        //     DBUtil.close(resultSet);
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        //     throw new DBException(ex);
        // }
        // return size;
        int size = 0;
        try {
            Connection connection = this.procedureConnection(dbName, schema);
            String sql = """
                    SELECT
                        COUNT(*)
                    FROM
                        ALL_PROCEDURES
                    WHERE
                        OWNER = ?
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

    public int functionSize(String dbName, String schema) {
        int size = 0;
        try {
            Connection connection = this.functionConnection(dbName, schema);
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

    public void alertFunction(String dbName, DamengFunction function) {
        try {
            String sql = "DROP FUNCTION IF EXISTS " + DBUtil.wrap(dbName, function.getName(), this.dialect());
            DBUtil.printSql(sql);
            Connection connection = this.connection(dbName);
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            sql = DamengFunctionSqlGenerator.INSTANCE.generate(function);
            DBUtil.printSql(sql);
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public List<DamengTrigger> triggers(String dbName) {
        try {
            String sql = """
                        SELECT
                            TRIGGER_NAME, TRIGGERING_EVENT AS EVENT_MANIPULATION, TABLE_NAME AS EVENT_OBJECT_TABLE
                        FROM
                            ALL_TRIGGERS
                        WHERE
                            OWNER = ?
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            ResultSet resultSet = statement.executeQuery();
            List<DamengTrigger> list = new ArrayList<>();
            while (resultSet.next()) {
                DamengTrigger trigger = new DamengTrigger();
                String name = resultSet.getString("TRIGGER_NAME");
                String tableName = resultSet.getString("EVENT_OBJECT_TABLE");
                String manipulation = resultSet.getString("EVENT_MANIPULATION");
                trigger.setName(name);
                trigger.setTableName(tableName);
                trigger.setDefinition("-- 请通过 DBMS_METADATA.GET_DDL 查看触发器定义");
                trigger.setPolicy("BEFORE", manipulation);
                list.add(trigger);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return list;
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public DamengTriggers triggers(String dbName, String tableName) {
        try {
            String sql = """
                        SELECT
                            TRIGGER_NAME, TRIGGERING_EVENT AS EVENT_MANIPULATION, TABLE_NAME AS EVENT_OBJECT_TABLE
                        FROM
                            ALL_TRIGGERS
                        WHERE
                            OWNER = ?
                        AND
                            TABLE_NAME = ?
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            DamengTriggers list = new DamengTriggers();
            while (resultSet.next()) {
                DamengTrigger trigger = new DamengTrigger();
                String name = resultSet.getString("TRIGGER_NAME");
                String manipulation = resultSet.getString("EVENT_MANIPULATION");
                trigger.setName(name);
                trigger.setDefinition("-- 请通过 DBMS_METADATA.GET_DDL 查看触发器定义");
                trigger.setPolicy("BEFORE", manipulation);
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

    public String selectVersion() {
        if (this.hasProperty("version")) {
            return this.getProperty("version");
        }
        String version = "";
        try {
            Connection conn = this.connection();
            Statement stmt = conn.createStatement();
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

    public void dropEvent(String dbName, DamengEvent event) {
        try {
            String sql = "DROP EVENT " + DBUtil.wrap(event.getDbName(), event.getName(), this.dialect());
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void createEvent(String dbName, DamengEvent event) {
        try {
            String sql = EventCreateSqlGenerator.generate(this.dialect(), event);
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void alertEvent(String dbName, DamengEvent event) {
        try {
            String sql = EventAlertSqlGenerator.generate(this.dialect(), event);
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public DamengEvent selectEvent(String dbName, String eventName) {
        // Dameng does not support MySQL-style events; return empty event stub
        DamengEvent event = new DamengEvent();
        event.setName(eventName);
        event.setDbName(dbName);
        return event;
    }

    public Integer eventSize(String dbName) {
        // Dameng does not support MySQL-style events natively; return 0
        return 0;
    }

    public List<DamengEvent> events(String dbName) {
        // Dameng does not support MySQL-style events natively; return empty list
        return new ArrayList<>();
    }

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

    public static final String[] TABLE_TYPES = new String[]{"TABLE", "SYSTEM TABLE", "SYSTEM VIEW", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};

    public static final String[] VIEW_TYPES = new String[]{"VIEW"};

    public List<DamengTable> selectTables(String dbName) {
        DamengSelectTableParam param = new DamengSelectTableParam();
        param.setDbName(dbName);
        return this.selectTables(param);
    }

    public List<DamengTable> selectTables(DamengSelectTableParam param) {
        try {
            String dbName = param.getDbName();
            List<DamengTable> tables = new ArrayList<>();
            Connection connection = this.connection(dbName);
            if (param.isFull()) {
                String sql = "SELECT NULL AS \"AUTO_INCREMENT\", NULL AS \"ROW_FORMAT\", NULL AS \"TABLE_COLLATION\", T.TABLE_NAME, C.COMMENTS AS \"TABLE_COMMENT\", T.TABLESPACE_NAME AS \"ENGINE\" FROM ALL_TABLES T LEFT JOIN ALL_TAB_COMMENTS C ON T.OWNER = C.OWNER AND T.TABLE_NAME = C.TABLE_NAME WHERE T.OWNER = ?";
                DBUtil.printSql(sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, dbName);
                ResultSet resultSet = statement.executeQuery();
                DBUtil.printMetaData(resultSet);
                while (resultSet.next()) {
                    DamengTable table = new DamengTable();
                    String tableEngine = resultSet.getString("ENGINE");
                    String tableName = resultSet.getString("TABLE_NAME");
                    String rowFormat = resultSet.getString("ROW_FORMAT");
                    Long autoIncrement = resultSet.getLong("AUTO_INCREMENT");
                    String tableComment = resultSet.getString("TABLE_COMMENT");
                    String tableCollation = resultSet.getString("TABLE_COLLATION");
                    String showCreateTable = this.showCreateTable(dbName, tableName);
                    table.setDbName(dbName);
                    table.setName(tableName);
                    table.setEngine(tableEngine);
                    table.setRowFormat(rowFormat);
                    table.setComment(tableComment);
                    table.setAutoIncrement(autoIncrement);
                    table.setCreateDefinition(showCreateTable);
                    table.setCharsetAndCollation(tableCollation);
                    tables.add(table);
                }
                DBUtil.close(resultSet);
                DBUtil.close(statement);
            } else {
                String sql = "SELECT TABLE_NAME, COMMENTS AS REMARKS FROM ALL_TAB_COMMENTS WHERE OWNER = ?";
                DBUtil.printSql(sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, dbName);
                ResultSet resultSet = statement.executeQuery();
                DBUtil.printMetaData(resultSet);
                while (resultSet.next()) {
                    DamengTable table = new DamengTable();
                    table.setDbName(dbName);
                    String remarks = resultSet.getString("REMARKS");
                    String tableName = resultSet.getString("TABLE_NAME");
                    table.setName(tableName);
                    table.setComment(remarks);
                    tables.add(table);
                }
                DBUtil.close(resultSet);
                DBUtil.close(statement);
            }


            return tables;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public DamengColumns selectColumns(DamengSelectColumnParam param) {
        try {
            String dbName = param.getDbName();
            String tableName = param.getTableName();
            DamengColumns columns = new DamengColumns();
            String sql = """
                    SELECT C.COLUMN_NAME AS "Field",
                        CASE WHEN C.NULLABLE='Y' THEN 'YES' ELSE 'NO' END AS "Null",
                        C.DATA_TYPE AS "Type",
                        CC.COMMENTS AS "Comment",
                        C.DATA_DEFAULT AS "Default",
                        CASE WHEN CONS.COLUMN_NAME IS NOT NULL THEN 'PRI' ELSE '' END AS "Key",
                        '' AS "Extra",
                        NULL AS "COLLATION"
                    FROM ALL_TAB_COLUMNS C
                    LEFT JOIN ALL_COL_COMMENTS CC ON C.OWNER=CC.OWNER AND C.TABLE_NAME=CC.TABLE_NAME AND C.COLUMN_NAME=CC.COLUMN_NAME
                    LEFT JOIN ALL_CONS_COLUMNS CONS ON C.OWNER=CONS.OWNER AND C.TABLE_NAME=CONS.TABLE_NAME AND C.COLUMN_NAME=CONS.COLUMN_NAME
                        AND EXISTS (SELECT 1 FROM ALL_CONSTRAINTS AC WHERE AC.OWNER=CONS.OWNER AND AC.CONSTRAINT_NAME=CONS.CONSTRAINT_NAME AND AC.CONSTRAINT_TYPE='P')
                    WHERE C.OWNER=? AND C.TABLE_NAME=?
                    ORDER BY C.COLUMN_ID
                    """;
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            int position = 0;
            while (resultSet.next()) {
                String key = resultSet.getString("Key");
                String type = resultSet.getString("Type");
                String field = resultSet.getString("Field");
                Object def = resultSet.getObject("Default");
                String extra = resultSet.getString("Extra");
                String nullable = resultSet.getString("Null");
                String comment = resultSet.getString("Comment");
                String collation = resultSet.getString("COLLATION");

                DamengColumn column = new DamengColumn();
                column.parseKey(key);
                column.parseType(type);
                column.parseExtra(extra);
                column.parseCollation(collation);

                column.setName(field);
                column.setDbName(dbName);
                column.setComment(comment);
                column.setDefaultValue(def);
                column.setPosition(position++);
                column.setTableName(tableName);
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
            builder.append(DBUtil.wrap(param.getDbName(), param.getTableName(), this.dialect()));
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
                    // 获取几何值
                    if (column.supportGeometry()) {
                        data = DamengHelper.getGeometryString(connection, data);
                    }
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
            builder.append(DBUtil.wrap(param.getDbName(), param.getTableName(), this.dialect()));
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
                    .append(DBUtil.wrap(param.getDbName(), param.getTableName(), this.dialect()))
                    .append("(");
            for (String column : param.getRecord().columns()) {
                builder.append(DBUtil.wrap(column, this.dialect())).append(",");
            }
            builder.append(")");
            builder.append(" VALUES(");
            for (String column : param.getRecord().columns()) {
                if (param.getRecord().isTypeGeometry(column)) {
                    builder.append("ST_GeomFromText(?),");
                } else {
                    builder.append("?,");
                }
            }
            builder.append(")");
            String sql = builder.toString();
            sql = sql.replaceAll(",\\)", ")");
            DBUtil.printSql(sql);
            Connection connection = this.connection(param.getDbName(), param.getSchema());
            PreparedStatement statement = connection.prepareStatement(sql);
            int index = 1;
            for (String colName : param.getRecord().columns()) {
                DBUtil.setVal(statement, param.getRecord().value(colName), index++);
            }
            int count = statement.executeUpdate();
            DamengRecordPrimaryKey primaryKey = param.getPrimaryKey();
            // 处理自动递增值
            if (primaryKey != null && primaryKey.shouldReturnData()) {
                primaryKey.setReturnData(DamengHelper.lastInsertId(connection));
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
                    .append(DBUtil.wrap(dbName, tableName, this.dialect()))
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
                        builder.append(DBUtil.wrap(colName, this.dialect()))
                                .append(" = ?");
                    } else {
                        builder.append(DBUtil.wrap(colName, this.dialect()))
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
                builder.append(DBUtil.wrap(primaryKey.getColumnName(), this.dialect()))
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
                    .append(DBUtil.wrap(dbName, tableName, this.dialect()))
                    .append(" SET ");
            for (String column : recordData.columns()) {
                if (recordData.isTypeGeometry(column)) {
                    builder.append(DBUtil.wrap(column, this.dialect())).append(" = ST_GeomFromText(?),");
                } else {
                    builder.append(DBUtil.wrap(column, this.dialect())).append(" = ?,");
                }
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
                    builder.append(DBUtil.wrap(column, this.dialect())).append(" = ?");
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
                    DBUtil.setVal(statement, originalRecordData.value(colName), index++);
                }
                updateCount = DBUtil.executeUpdate(statement);
                DBUtil.close(statement);
            } else {
                DamengRecordPrimaryKey primaryKey = param.getPrimaryKey();
                builder.append(DBUtil.wrap(primaryKey.getColumnName(), this.dialect())).append(" = ?");
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
            String sql = "SELECT DBMS_METADATA.GET_DDL('VIEW', ?) FROM DUAL";
            DBUtil.printSql(sql);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, viewName);
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

    public String showCreateFunction(String dbName, String functionName) {
        try {
            Connection connection = this.connection(dbName);
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
            Connection connection = this.connection(dbName);
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

    public List<String> engines() {
        if (this.hasProperty("engines")) {
            return this.getProperty("engines");
        }
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
            this.putProperty("engines", engines);
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
            if (param.isFull()) {
                String sql = "SELECT NULL AS \"AUTO_INCREMENT\", NULL AS \"ROW_FORMAT\", NULL AS \"TABLE_COLLATION\", C.COMMENTS AS \"TABLE_COMMENT\", T.TABLESPACE_NAME AS \"ENGINE\" FROM ALL_TABLES T LEFT JOIN ALL_TAB_COMMENTS C ON T.OWNER = C.OWNER AND T.TABLE_NAME = C.TABLE_NAME WHERE T.OWNER = ? AND T.TABLE_NAME = ?";
                DBUtil.printSql(sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, dbName);
                statement.setString(2, tableName);
                ResultSet resultSet = statement.executeQuery();
                DBUtil.printMetaData(resultSet);
                String showCreateTable = this.showCreateTable(dbName, tableName);
                while (resultSet.next()) {
                    String tableEngine = resultSet.getString("ENGINE");
                    String rowFormat = resultSet.getString("ROW_FORMAT");
                    Long autoIncrement = resultSet.getLong("AUTO_INCREMENT");
                    String tableComment = resultSet.getString("TABLE_COMMENT");
                    String tableCollation = resultSet.getString("TABLE_COLLATION");
                    table.setEngine(tableEngine);
                    table.setRowFormat(rowFormat);
                    table.setComment(tableComment);
                    table.setAutoIncrement(autoIncrement);
                    table.setCreateDefinition(showCreateTable);
                    table.setCharsetAndCollation(tableCollation);
                }
                DBUtil.close(resultSet);
                DBUtil.close(statement);
            } else {
                String sql = "SELECT COMMENTS AS REMARKS FROM ALL_TAB_COMMENTS WHERE OWNER = ? AND TABLE_NAME = ?";
                DBUtil.printSql(sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, dbName);
                statement.setString(2, tableName);
                ResultSet resultSet = statement.executeQuery();
                DBUtil.printMetaData(resultSet);
                while (resultSet.next()) {
                    String remarks = resultSet.getString("REMARKS");
                    table.setComment(remarks);
                }
                DBUtil.close(resultSet);
                DBUtil.close(statement);
            }
            return table;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public DamengTable selectFullTable(DamengSelectTableParam param) {
        try {
            String dbName = param.getDbName();
            String tableName = param.getTableName();
            DamengTable table = new DamengTable();
            table.setDbName(dbName);
            table.setName(tableName);
            Connection connection = this.connection(dbName);
            String sql = "SELECT NULL AS \"AUTO_INCREMENT\", NULL AS \"ROW_FORMAT\", NULL AS \"TABLE_COLLATION\", C.COMMENTS AS \"TABLE_COMMENT\", T.TABLESPACE_NAME AS \"ENGINE\" FROM ALL_TABLES T LEFT JOIN ALL_TAB_COMMENTS C ON T.OWNER = C.OWNER AND T.TABLE_NAME = C.TABLE_NAME WHERE T.OWNER = ? AND T.TABLE_NAME = ?";
            DBUtil.printSql(sql);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, tableName);
            ResultSet resultSet = statement.executeQuery();
            DBUtil.printMetaData(resultSet);
            String showCreateTable = this.showCreateTable(dbName, tableName);
            while (resultSet.next()) {
                String tableEngine = resultSet.getString("ENGINE");
                String rowFormat = resultSet.getString("ROW_FORMAT");
                Long autoIncrement = resultSet.getLong("AUTO_INCREMENT");
                String tableComment = resultSet.getString("TABLE_COMMENT");
                String tableCollation = resultSet.getString("TABLE_COLLATION");
                table.setEngine(tableEngine);
                table.setRowFormat(rowFormat);
                table.setComment(tableComment);
                table.setAutoIncrement(autoIncrement);
                table.setCreateDefinition(showCreateTable);
                table.setCharsetAndCollation(tableCollation);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);
            return table;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public DamengView view(String dbName, String viewName) {
        try {
            String sql = "SELECT V.VIEW_NAME AS \"TABLE_NAME\", C.COMMENTS AS \"TABLE_COMMENT\" FROM ALL_VIEWS V LEFT JOIN ALL_TAB_COMMENTS C ON V.OWNER = C.OWNER AND V.VIEW_NAME = C.TABLE_NAME WHERE V.OWNER = ? AND V.VIEW_NAME = ?";
            DBUtil.printSql(sql);
            Connection connection = this.connection();
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            statement.setString(2, viewName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            // 遍历结果集
            DamengView view = new DamengView();
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                String tableComment = resultSet.getString("TABLE_COMMENT");
                Map<String, String> info = DamengHelper.getViewInfo(connection, dbName, tableName);
                view.setDbName(dbName);
                view.setName(tableName);
                view.setComment(tableComment);
                view.setDefiner(info.get("DEFINER"));
                view.setAlgorithm(info.get("ALGORITHM"));
                view.setDefinition(info.get("DEFINITION"));
                view.setCheckOption(info.get("CHECK_OPTION"));
                view.setSecurityType(info.get("SECURITY_TYPE"));
                view.setUpdatable(StringUtil.equalsIgnoreCase("YES", info.get("UPDATABLE")));
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

    public List<DamengView> views(String dbName) {
        try {
            List<DamengView> list = new ArrayList<>();
            String sql = "SELECT V.VIEW_NAME AS \"TABLE_NAME\", C.COMMENTS AS \"TABLE_COMMENT\" FROM ALL_VIEWS V LEFT JOIN ALL_TAB_COMMENTS C ON V.OWNER = C.OWNER AND V.VIEW_NAME = C.TABLE_NAME WHERE V.OWNER = ?";
            DBUtil.printSql(sql);
            Connection connection = this.connection();
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            // 遍历结果集
            while (resultSet.next()) {
                DamengView view = new DamengView();
                String tableName = resultSet.getString("TABLE_NAME");
                String tableComment = resultSet.getString("TABLE_COMMENT");
                Map<String, String> info = DamengHelper.getViewInfo(connection, dbName, tableName);
                view.setDbName(dbName);
                view.setName(tableName);
                view.setComment(tableComment);
                view.setDefiner(info.get("DEFINER"));
                view.setAlgorithm(info.get("ALGORITHM"));
                view.setDefinition(info.get("DEFINITION"));
                view.setCheckOption(info.get("CHECK_OPTION"));
                view.setSecurityType(info.get("SECURITY_TYPE"));
                view.setUpdatable(StringUtil.equalsIgnoreCase("YES", info.get("UPDATABLE")));
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
            String sql = "DROP VIEW IF EXISTS " + DBUtil.wrap(view.getDbName(), view.getName(), this.dialect());
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
            PreparedStatement statement = this.connection().prepareStatement(sql);
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

    public void createView(String dbName, DamengView view) {
        try {
            Statement statement = this.connection(dbName).createStatement();
            String sql = "CREATE ";
            if (StringUtil.isNotBlank(view.getAlgorithm())) {
                sql += " ALGORITHM = " + view.getAlgorithm();
            }
            if (StringUtil.isNotBlank(view.getDefiner())) {
                sql += " DEFINER = " + view.getDefiner();
            }
            if (StringUtil.isNotBlank(view.getSecurityType())) {
                sql += " SQL SECURITY " + view.getSecurityType();
            }
            sql = sql + " VIEW " + DBUtil.wrap(dbName, view.getName(), this.dialect()) + " AS " + view.getDefinition();
            if (view.hasCheckOption()) {
                sql += " WITH " + view.getCheckOption() + " CHECK OPTION";
            }
            DBUtil.printSql(sql);
            statement.execute(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public void alertView(String dbName, DamengView view) {
        try {
            Statement statement = this.connection(dbName).createStatement();
            String sql = "CREATE OR REPLACE ";
            if (StringUtil.isNotBlank(view.getAlgorithm())) {
                sql += " ALGORITHM = " + view.getAlgorithm();
            }
            if (StringUtil.isNotBlank(view.getDefiner())) {
                sql += " DEFINER = " + view.getDefiner();
            }
            if (StringUtil.isNotBlank(view.getSecurityType())) {
                sql += " SQL SECURITY " + view.getSecurityType();
            }
            sql = sql + " VIEW " + DBUtil.wrap(dbName, view.getName(), this.dialect()) + " AS " + view.getDefinition();
            if (view.hasCheckOption()) {
                sql += " WITH " + view.getCheckOption() + " CHECK OPTION";
            }
            DBUtil.printSql(sql);
            statement.execute(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    public DamengIndexes indexes(String dbName, String tableName) {
        try {
            Connection connection = this.connection();
            String sql = """
                    SELECT
                        i.INDEX_NAME AS "Key_name",
                        c.COLUMN_NAME AS "Column_name",
                        CASE WHEN i.UNIQUENESS='UNIQUE' THEN 0 ELSE 1 END AS "Non_unique",
                        c.COLUMN_POSITION AS "Seq_in_index",
                        CASE WHEN i.INDEX_TYPE='NORMAL' THEN 'BTREE' ELSE i.INDEX_TYPE END AS "Index_type",
                        NULL AS "Index_comment",
                        NULL AS "Sub_Part"
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
                    String indexComment = resultSet.getString("Index_comment");
                    tableIndex = new DamengIndex();
                    tableIndex.setName(keyName);
                    tableIndex.setSeqIndex(seqInIndex);
                    tableIndex.setComment(indexComment);
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
            // String sql = """
            //         SELECT
            //             CHECK_CLAUSE AS 'CLAUSE',
            //             CONSTRAINT_NAME AS 'NAME',
            //             TABLE_NAME AS 'TABLE_NAME',
            //             OWNER AS 'DB_NAME'
            //         FROM
            //             DBA_OBJECTS.CHECK_CONSTRAINTS
            //         WHERE
            //             OWNER = ?
            //         AND
            //             TABLE_NAME = ?;
            //         """;
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
            PreparedStatement statement = this.connection().prepareStatement(sql);
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
                    FROM ALL_CONSTRAINTS ac
                    JOIN ALL_CONS_COLUMNS acc ON ac.CONSTRAINT_NAME = acc.CONSTRAINT_NAME AND ac.OWNER = acc.OWNER
                    JOIN ALL_CONSTRAINTS ac_r ON ac.R_OWNER = ac_r.OWNER AND ac.R_CONSTRAINT_NAME = ac_r.CONSTRAINT_NAME
                    JOIN ALL_CONS_COLUMNS acc_r ON ac_r.CONSTRAINT_NAME = acc_r.CONSTRAINT_NAME AND ac_r.OWNER = acc_r.OWNER AND acc.POSITION = acc_r.POSITION
                    WHERE ac.CONSTRAINT_TYPE = 'R'
                    AND ac.OWNER = ?
                    AND ac.TABLE_NAME = ?
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
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
                        CASE WHEN C.IDENTITY_COLUMN='YES' THEN 'auto_increment' ELSE '' END AS COLUMN_EXTRA,
                        NULL AS COLUMN_KEY,
                        CC.COMMENTS AS REMARKS,
                        C.DATA_TYPE AS COLUMN_TYPE,
                        C.COLUMN_NAME AS COLUMN_NAME,
                        CASE WHEN C.NULLABLE='Y' THEN 'YES' ELSE 'NO' END AS IS_NULLABLE,
                        C.DATA_DEFAULT AS COLUMN_DEF,
                        NULL AS COLLATION_NAME,
                        NULL AS CHARSET_NAME,
                        C.COLUMN_ID AS ORDINAL_POSITION
                    FROM ALL_TAB_COLUMNS C
                    LEFT JOIN ALL_COL_COMMENTS CC ON C.OWNER=CC.OWNER AND C.TABLE_NAME=CC.TABLE_NAME AND C.COLUMN_NAME=CC.COLUMN_NAME
                    WHERE C.OWNER = ?
                    AND C.TABLE_NAME = ?
                    ORDER BY C.COLUMN_ID
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
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
                String charsetName = resultSet.getString("CHARSET_NAME");
                String columnExtra = resultSet.getString("COLUMN_EXTRA");
                String collationName = resultSet.getString("COLLATION_NAME");
                DamengColumn column = new DamengColumn();
                column.initColumn(columnType, columnExtra);
                column.setDbName(dbName);
                column.setName(columnName);
                column.setComment(remarks);
                column.setDefaultValue(def);
                column.setPosition(position);
                column.setCharset(charsetName);
                column.setTableName(viewName);
                column.setCollation(collationName);
                column.setNullable("yes".equalsIgnoreCase(nullable));
                // column.setPrimaryKey("pri".equalsIgnoreCase(columnKey));
                columns.put(columnName, column);
            }
            DBUtil.close(resultSet);
            DBUtil.close(statement);

            sql = "SELECT * FROM " + DBUtil.wrap(dbName, viewName, this.dialect()) + " FETCH FIRST 1 ROWS ONLY";
            DBUtil.printSql(sql);
            PreparedStatement statement1 = this.connection().prepareStatement(sql);
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
            builder.append(DBUtil.wrap(dbName, viewName, this.dialect()));
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
            while (resultSet.next()) {
                DamengRecord record = new DamengRecord(columns, !updatable);
                for (DamengColumn column : columns) {
                    Object data = resultSet.getObject(column.getName());
                    // 获取几何值
                    if (column.supportGeometry()) {
                        data = DamengHelper.getGeometryString(connection, data);
                    }
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
            String sql = DamengTableCreateSqlGenerator.generateSql(param);
            DBUtil.printSql(sql);
            List<String> sqlList = DBSqlParser.parseSql(sql, this.dialect());
            connection.setAutoCommit(false);
            for (String sqlStr : sqlList) {
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
            String sql = DamengTableAlertSqlGenerator.generateSql(param);
            // 无变化
            if (StringUtil.isBlank(sql)) {
                return;
            }
            String dbName = param.getTable().getDbName();
            connection = this.connection(dbName);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            DBUtil.printSql(sql);
            List<String> sqlList = DBSqlParser.parseSql(sql, this.dialect());
            for (String sqlStr : sqlList) {
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

    @Deprecated
    public boolean existTable(String dbName, String tableName) {
        boolean result;
        try {
            String sql = "SELECT COUNT(*) FROM ALL_TABLES WHERE OWNER = ? AND TABLE_NAME = ?";
            PreparedStatement statement = this.connection().prepareStatement(sql);
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
            String sql = "ALTER TABLE " + DBUtil.wrap(dbName, oldTableName, this.dialect())
                    + " RENAME TO " + DBUtil.wrap(newTableName, this.dialect());
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
            String sql = "DELETE FROM " + DBUtil.wrap(dbName, tableName, this.dialect());
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
            String sql = "TRUNCATE TABLE " + DBUtil.wrap(dbName, tableName, this.dialect());
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
            String sql = "DROP TABLE " + DBUtil.wrap(dbName, tableName, this.dialect());
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
            PreparedStatement statement = this.connection().prepareStatement(sql);
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
            builder.append(DBUtil.wrap(database.getName(), this.dialect()));
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
            String sql = "DROP SCHEMA " + DBUtil.wrap(dbName, this.dialect()) + " CASCADE";
            DBUtil.printSql(sql);
            Statement statement = this.connection().createStatement();
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
            DBSqlParser parser = DBSqlParser.getParser(sql, this.dialect());
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
            DBSqlParser parser = DBSqlParser.getParser(sql, this.dialect());
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
                        result.setFullColumn(DBUtil.isFullColumn(execSql, this.dbType()));
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

    public List<DamengFunction> functions(String dbName) {
        try {
            List<DamengFunction> list = new ArrayList<>();
            String sql = """
                    SELECT
                        O.OBJECT_NAME AS "ROUTINE_NAME",
                        P.AUTHID AS "SECURITY_TYPE",
                        NULL AS "SQL_DATA_ACCESS",
                        NULL AS "ROUTINE_DEFINITION"
                    FROM ALL_OBJECTS O
                    LEFT JOIN ALL_PROCEDURES P ON O.OWNER = P.OWNER AND O.OBJECT_NAME = P.OBJECT_NAME
                    WHERE O.OWNER = ?
                    AND O.OBJECT_TYPE = 'FUNCTION'
                    AND O.STATUS = 'VALID'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            // 遍历结果集
            while (resultSet.next()) {
                DamengFunction function = new DamengFunction();
                String name = resultSet.getString("ROUTINE_NAME");
                List<DamengRoutineParam> params = DamengHelper.listFunctionParam(this.connection(), dbName, name);
                String securityType = resultSet.getString("SECURITY_TYPE");
                String definition = resultSet.getString("ROUTINE_DEFINITION");
                String sqlDataAccess = resultSet.getString("SQL_DATA_ACCESS");
                String createDefinition = DamengHelper.getFunctionDefinition(this.connection(dbName), name);
                function.setName(name);
                function.setDbName(dbName);
                function.setParams(params);
                function.setDefinition(definition);
                function.setSecurityType(securityType);
                function.setCharacteristic(sqlDataAccess);
                function.setCreateDefinition(createDefinition);
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

    public List<DamengProcedure> procedures(String dbName) {
        try {
            List<DamengProcedure> list = new ArrayList<>();
            String sql = """
                    SELECT
                        O.OBJECT_NAME AS "ROUTINE_NAME",
                        P.AUTHID AS "SECURITY_TYPE",
                        NULL AS "SQL_DATA_ACCESS",
                        NULL AS "ROUTINE_DEFINITION"
                    FROM ALL_OBJECTS O
                    LEFT JOIN ALL_PROCEDURES P ON O.OWNER = P.OWNER AND O.OBJECT_NAME = P.OBJECT_NAME
                    WHERE O.OWNER = ?
                    AND O.OBJECT_TYPE = 'PROCEDURE'
                    AND O.STATUS = 'VALID'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
            statement.setString(1, dbName);
            // 执行SQL查询并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 打印元数据
            DBUtil.printMetaData(resultSet);
            // 遍历结果集
            while (resultSet.next()) {
                DamengProcedure procedure = new DamengProcedure();
                String name = resultSet.getString("ROUTINE_NAME");
                String createDefinition = this.showCreateProcedure(dbName, name);
                List<DamengRoutineParam> params = DamengHelper.listProcedureParam(this.connection(), dbName, name);
                String securityType = resultSet.getString("SECURITY_TYPE");
                String definition = resultSet.getString("ROUTINE_DEFINITION");
                String sqlDataAccess = resultSet.getString("SQL_DATA_ACCESS");
                procedure.setName(name);
                procedure.setDbName(dbName);
                procedure.setParams(params);
                procedure.setDefinition(definition);
                procedure.setSecurityType(securityType);
                procedure.setCharacteristic(sqlDataAccess);
                procedure.setCreateDefinition(createDefinition);
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

    public DamengProcedure selectProcedure(String dbName, String produceName) {
        try {
            String sql = """
                    SELECT
                        P.AUTHID AS "SECURITY_TYPE",
                        NULL AS "SQL_DATA_ACCESS",
                        NULL AS "ROUTINE_DEFINITION"
                    FROM ALL_OBJECTS O
                    LEFT JOIN ALL_PROCEDURES P ON O.OWNER = P.OWNER AND O.OBJECT_NAME = P.OBJECT_NAME
                    WHERE O.OWNER = ?
                    AND O.OBJECT_NAME = ?
                    AND O.OBJECT_TYPE = 'PROCEDURE'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
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
                String createDefinition = this.showCreateProcedure(dbName, produceName);
                List<DamengRoutineParam> params = DamengHelper.listProcedureParam(this.connection(), dbName, produceName);
                String securityType = resultSet.getString("SECURITY_TYPE");
                String definition = resultSet.getString("ROUTINE_DEFINITION");
                String sqlDataAccess = resultSet.getString("SQL_DATA_ACCESS");
                procedure.setDbName(dbName);
                procedure.setParams(params);
                procedure.setDefinition(definition);
                procedure.setSecurityType(securityType);
                procedure.setCharacteristic(sqlDataAccess);
                procedure.setCreateDefinition(createDefinition);
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
            String sql = "DROP PROCEDURE IF EXISTS " + DBUtil.wrap(dbName, routine.getName(), this.dialect());
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void createProcedure(String dbName, DamengProcedure procedure) {
        try {
            String sql = DamengProcedureSqlGenerator.INSTANCE.generate(procedure);
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void alertProcedure(String dbName, DamengProcedure procedure) {
        try {
            String sql = "DROP PROCEDURE IF EXISTS " + DBUtil.wrap(dbName, procedure.getName(), this.dialect());
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            sql = DamengProcedureSqlGenerator.INSTANCE.generate(procedure);
            DBUtil.printSql(sql);
            Statement statement1 = this.connection(dbName).createStatement();
            statement1.executeUpdate(sql);
            DBUtil.close(statement1);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public void dropFunction(String dbName, DamengFunction function) {
        try {
            String sql = "DROP function IF EXISTS " + DBUtil.wrap(dbName, function.getName(), this.dialect());
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
            statement.executeUpdate(sql);
            DBUtil.close(statement);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
    }

    public DamengFunction selectFunction(String dbName, String functionName) {
        try {
            String sql = """
                    SELECT
                        P.AUTHID AS "SECURITY_TYPE",
                        NULL AS "SQL_DATA_ACCESS",
                        NULL AS "ROUTINE_DEFINITION"
                    FROM ALL_OBJECTS O
                    LEFT JOIN ALL_PROCEDURES P ON O.OWNER = P.OWNER AND O.OBJECT_NAME = P.OBJECT_NAME
                    WHERE O.OWNER = ?
                    AND O.OBJECT_NAME = ?
                    AND O.OBJECT_TYPE = 'FUNCTION'
                    """;
            DBUtil.printSql(sql);
            PreparedStatement statement = this.connection().prepareStatement(sql);
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
                String securityType = resultSet.getString("SECURITY_TYPE");
                String definition = resultSet.getString("ROUTINE_DEFINITION");
                String sqlDataAccess = resultSet.getString("SQL_DATA_ACCESS");
                List<DamengRoutineParam> params = DamengHelper.listFunctionParam(this.connection(), dbName, functionName);
                String createDefinition = this.showCreateFunction(dbName, functionName);
                function.setDbName(dbName);
                function.setParams(params);
                function.setDefinition(definition);
                function.setSecurityType(securityType);
                function.setCharacteristic(sqlDataAccess);
                function.setCreateDefinition(createDefinition);
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

    public void createFunction(String dbName, DamengFunction function) {
        try {
            String sql = DamengFunctionSqlGenerator.INSTANCE.generate(function);
            DBUtil.printSql(sql);
            Statement statement = this.connection(dbName).createStatement();
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
            builder.append(DBUtil.wrap(dbName, tableName, this.dialect()))
                    .append(" WHERE ")
                    .append(DBUtil.wrap(primaryKey.getColumnName(), this.dialect()))
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
                    // 获取几何值
                    if (column.supportGeometry()) {
                        data = DamengHelper.getGeometryString(connection, data);
                    }
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

    public boolean existPrimaryKey(String dbName, String tableName) {
        try {
            Connection connection = this.connection(dbName);
            String sql = "SELECT COUNT(*) FROM ALL_CONSTRAINTS WHERE OWNER = ? AND TABLE_NAME = ? AND CONSTRAINT_TYPE = 'P'";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, dbName);
            stmt.setString(2, tableName);
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
        // // 查询表
        // DamengSelectTableParam selectTableParam = new DamengSelectTableParam();
        // selectTableParam.setFull(true);
        // selectTableParam.setDbName(dbName);
        // selectTableParam.setTableName(tableName);
        // DamengTable table = this.selectTable(selectTableParam);
        // 查询检查
        DamengChecks checks = this.checks(dbName, tableName);
        // // 查询索引
        // DamengIndexes indexes = this.indexes(dbName, tableName);
        // 查询触发器
        DamengTriggers triggers = this.triggers(dbName, tableName);
        // 查询外键
        DamengForeignKeys foreignKeys = this.foreignKeys(dbName, tableName);
        // // 查询字段
        // DamengSelectColumnParam selectColumnParam = new DamengSelectColumnParam();
        // selectColumnParam.setDbName(dbName);
        // selectColumnParam.setTableName(tableName);
        // DamengColumns columns = this.selectColumns(selectColumnParam);
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
        // if (indexes != null) {
        //     for (DamengIndex index : indexes) {
        //         index.setName(index.getName() + DBUtil.genCloneName());
        //     }
        // }
        if (foreignKeys != null) {
            for (DamengForeignKey foreignKey : foreignKeys) {
                foreignKey.setName(foreignKey.getName() + DBUtil.genCloneName());
                foreignKey.clearStatus();
                foreignKey.clearOriginalData();
                foreignKey.setCreated(true);
            }
        }
        // table.setName(table.getName() + DBUtil.genCloneName());
        // // 创建表
        // DamengCreateTableParam createTableParam = new DamengCreateTableParam();
        // createTableParam.setTable(table);
        // createTableParam.setChecks(checks);
        // createTableParam.setIndexes(indexes);
        // createTableParam.setColumns(columns);
        // createTableParam.setTriggers(triggers);
        // createTableParam.setForeignKeys(foreignKeys);
        // this.createTable(createTableParam);
        // // 复制记录
        // if (includeRecord) {
        //     // 开始位置
        //     long start = 0;
        //     // 限制行
        //     long limit = 1000;
        //     while (true) {
        //         // 查询记录
        //         DamengSelectRecordParam selectRecordParam = new DamengSelectRecordParam();
        //         selectRecordParam.setDbName(dbName);
        //         selectRecordParam.setTableName(tableName);
        //         selectRecordParam.setStart(start);
        //         selectRecordParam.setLimit(limit);
        //         List<DamengRecord> records = this.selectRecords(selectRecordParam);
        //         // 插入记录
        //         if (CollectionUtil.isNotEmpty(records)) {
        //             for (DamengRecord record : records) {
        //                 DamengInsertRecordParam insertRecordParam = DBUtil.toInsertRecord(columns, record);
        //                 this.insertRecord(insertRecordParam);
        //             }
        //         }
        //         // 查询结束
        //         if (CollectionUtil.size(records) != limit) {
        //             break;
        //         }
        //         start += limit;
        //     }
        // }

        String newTableName = tableName + DBUtil.genCloneName();
        try {
            Connection connection = this.connection(dbName);

            // 克隆基本的表结构
            String sql = "CREATE TABLE " + DBUtil.wrap(dbName, newTableName, this.dialect())
                    + " LIKE " + DBUtil.wrap(dbName, tableName, this.dialect());
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
            alertTableParam.setTriggers(triggers);
            alertTableParam.setForeignKeys(foreignKeys);
            this.alertTable(alertTableParam);

            // 克隆数据
            if (includeRecord) {
                sql = "INSERT INTO " + DBUtil.wrap(dbName, newTableName, this.dialect())
                        + " SELECT * FROM " + DBUtil.wrap(dbName, tableName, this.dialect());
                DBUtil.printSql(sql);
                stmt = connection.prepareStatement(sql);
                stmt.execute();
                DBUtil.close(stmt);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException(ex);
        }
        return newTableName;
    }
}
