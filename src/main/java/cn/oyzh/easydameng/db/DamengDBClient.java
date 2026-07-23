// package cn.oyzh.easydameng.db;
//
// import cn.hutool.core.collection.CollUtil;
// import cn.hutool.core.util.StrUtil;
// import cn.oyzh.easydameng.condition.DamengConditionUtil;
// import cn.oyzh.easydameng.db.column.DamengColumn;
// import cn.oyzh.easydameng.db.column.DamengColumns;
// import cn.oyzh.easydameng.db.event.DamengEvent;
// import cn.oyzh.easydameng.db.function.DamengFunction;
// import cn.oyzh.easydameng.db.procedure.DamengProcedure;
// import cn.oyzh.easydameng.db.query.DamengExecuteResult;
// import cn.oyzh.easydameng.db.query.DamengExplainResult;
// import cn.oyzh.easydameng.db.query.DamengQueryResults;
// import cn.oyzh.easydameng.db.record.DamengDeleteRecordParam;
// import cn.oyzh.easydameng.db.record.DamengInsertRecordParam;
// import cn.oyzh.easydameng.db.record.DamengRecord;
// import cn.oyzh.easydameng.db.record.DamengRecordData;
// import cn.oyzh.easydameng.db.record.DamengRecordFilter;
// import cn.oyzh.easydameng.db.record.DamengRecordPrimaryKey;
// import cn.oyzh.easydameng.db.record.DamengSelectRecordParam;
// import cn.oyzh.easydameng.db.record.DamengUpdateRecordParam;
// import cn.oyzh.easydameng.db.routine.DamengRoutineParam;
// import cn.oyzh.easydameng.db.check.DamengCheck;
// import cn.oyzh.easydameng.db.check.DamengChecks;
// import cn.oyzh.easydameng.db.foreignKey.DamengForeignKey;
// import cn.oyzh.easydameng.db.index.DamengIndex;
// import cn.oyzh.easydameng.db.table.DamengTable;
// import cn.oyzh.easydameng.db.table.DamengAlertTableParam;
// import cn.oyzh.easydameng.db.trigger.DamengTrigger;
// import cn.oyzh.easydameng.db.view.DamengView;
// import cn.oyzh.easydameng.domain.DamengInfo;
// import cn.oyzh.easydameng.exception.DBException;
// import cn.oyzh.easydameng.generator.event.EventAlertSqlGenerator;
// import cn.oyzh.easydameng.generator.event.EventCreateSqlGenerator;
// import cn.oyzh.easydameng.generator.routine.DamengFunctionSqlGenerator;
// import cn.oyzh.easydameng.generator.routine.DamengProcedureSqlGenerator;
// import cn.oyzh.easydameng.generator.table.TableAlertSqlGenerator;
// import cn.oyzh.easydameng.generator.table.TableCreateSqlGenerator;
// import cn.oyzh.easydameng.sql.DBSqlParser;
// import cn.oyzh.easydameng.util.DBUtil;
// import com.dameng.cj.Messages;
// import com.dameng.cj.conf.EnumPropertyDefinition;
// import com.dameng.cj.conf.PropertyDefinition;
// import com.dameng.cj.conf.PropertyDefinitions;
// import com.dameng.cj.conf.PropertyKey;
// import com.dameng.cj.jdbc.JdbcConnection;
// import lombok.NonNull;
//
// import java.sql.Connection;
// import java.sql.DatabaseMetaData;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.Statement;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.Comparator;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// /**
//  * dameng客户端封装
//  *
//  * @author oyzh
//  * @since 2023/11/06
//  */
// public class DamengDBClient extends DamengClient {
//
//     public DamengDBClient( DamengInfo dbInfo) {
//         super(dbInfo);
//     }
//
//     @Override
//     protected Connection initConnection(DBConnConfig connConfig, String dbName, String user, String password) throws ClassNotFoundException, SQLException {
//         // 加载JDBC驱动
//         Class.forName("com.dameng.cj.jdbc.Driver");
//         String host = connConfig.getConnectionString(this.dialect());
//         if (dbName != null) {
//             host += dbName;
//         }
//         // 创建数据库连接
//         return DriverManager.getConnection(host, user, password);
//     }
//
//     @Override
//     public List<String> engines() {
//         if (this.hasProperty("engines")) {
//             return this.getProperty("engines");
//         }
//         try {
//             List<String> engines = new ArrayList<>();
//             String sql = "SELECT ENGINE FROM DBA_TABLESPACES WHERE SUPPORT = 'YES' OR SUPPORT = 'DEFAULT'";
//             Statement statement = this.connection().createStatement();
//             ResultSet resultSet = statement.executeQuery(sql);
//             while (resultSet.next()) {
//                 engines.add(resultSet.getString(1));
//             }
//             DBUtil.close(statement);
//             this.putProperty("engines", engines);
//             return engines;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     /**
//      * 获取结构连接
//      *
//      * @param dbName 库名称
//      * @return 连接
//      * @throws SQLException
//      * @throws ClassNotFoundException
//      */
//     protected Connection schemaConnection(String dbName) throws SQLException, ClassNotFoundException {
//         Connection connection = this.connection(dbName);
//         if (connection instanceof JdbcConnection jdbcConnection) {
//             PropertyDefinition<PropertyDefinitions.DatabaseTerm> definition = new EnumPropertyDefinition<>(PropertyKey.databaseTerm, PropertyDefinitions.DatabaseTerm.SCHEMA, PropertyDefinitions.RUNTIME_MODIFIABLE, Messages.getString("ConnectionProperties.databaseTerm"), "8.0.17", PropertyDefinitions.CATEGORY_CONNECTION, Integer.MIN_VALUE);
//             jdbcConnection.getPropertySet().addProperty(definition.createRuntimeProperty());
//         }
//         return connection;
//     }
//
//     @Override
//     public List<DBDatabase> databases() {
//         try {
//             Statement statement = this.connection().createStatement();
//             ResultSet resultSet = statement.executeQuery("SHOW DATABASES");
//             List<DBDatabase> list = new ArrayList<>();
//             while (resultSet.next()) {
//                 DBDatabase databases = new DBDatabase();
//                 String dbName = resultSet.getString(1);
//                 databases.setName(dbName);
//                 databases.setCharsetAndCollation(this.databaseCollation(dbName));
//                 list.add(databases);
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return list;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public List<DamengTable> tables(String dbName, String schema, boolean full) {
//         try {
//             List<DamengTable> list = new ArrayList<>();
//             String sql = "SELECT `NULL AS AUTO_INCREMENT`, `NULL AS ROW_FORMAT`, `NULL AS TABLE_COLLATION`, `TABLE_NAME`, `TABLE_COMMENT`, `ENGINE` FROM ALL_TABLES WHERE `OWNER` = ? AND `TABLE_TYPE` != 'VIEW'";
//             DBUtil.printSql(sql);
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             // 执行SQL查询并获取结果集
//             ResultSet resultSet = statement.executeQuery();
//             // 打印元数据
//             DBUtil.printMetaData(resultSet);
//             // 遍历结果集
//             while (resultSet.next()) {
//                 DamengTable table = new DamengTable();
//                 String tableEngine = resultSet.getString("ENGINE");
//                 String tableName = resultSet.getString("TABLE_NAME");
//                 String rowFormat = resultSet.getString("NULL AS ROW_FORMAT");
//                 Long autoIncrement = resultSet.getLong("NULL AS AUTO_INCREMENT");
//                 String tableComment = resultSet.getString("TABLE_COMMENT");
//                 String tableCollation = resultSet.getString("NULL AS TABLE_COLLATION");
//                 String showCreateTable = this.showCreateTable(dbName, tableName);
//                 table.setDbName(dbName);
//                 table.setName(tableName);
//                 table.setEngine(tableEngine);
//                 table.setRowFormat(rowFormat);
//                 table.setComment(tableComment);
//                 table.setAutoIncrement(autoIncrement);
//                 table.setCreateDefinition(showCreateTable);
//                 table.setCharsetAndCollation(tableCollation);
//                 // if (full) {
//                 //     table.setChecks(this.checks(dbName, tableName));
//                 //     table.setColumns(this.tableColumns(dbName, null, tableName));
//                 //     table.setIndexes(new DamengIndexes(this.indexes(dbName, tableName)));
//                 //     table.setTriggers(new DamengTriggers(this.triggers(dbName, tableName)));
//                 //     table.setForeignKeys(new DamengForeignKeys(this.foreignKeys(dbName, tableName)));
//                 // }
//                 list.add(table);
//             }
//             // 关闭连接和释放资源
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return list;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public DamengTable table(String dbName, String tableName, boolean full) {
//         try {
//             String sql = "SELECT `NULL AS AUTO_INCREMENT`, `NULL AS ROW_FORMAT`, `NULL AS TABLE_COLLATION`, `TABLE_COMMENT`, `ENGINE` FROM ALL_TABLES WHERE `OWNER` = ? AND `TABLE_NAME` = ?  AND `TABLE_TYPE` = 'BASE TABLE'";
//             DBUtil.printSql(sql);
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             statement.setString(2, tableName);
//             // 执行SQL查询并获取结果集
//             ResultSet resultSet = statement.executeQuery();
//             // 打印元数据
//             DBUtil.printMetaData(resultSet);
//             DamengTable table = new DamengTable();
//             table.setName(tableName);
//             table.setDbName(dbName);
//             String showCreateTable = this.showCreateTable(dbName, tableName);
//             // 遍历结果集
//             while (resultSet.next()) {
//                 String tableEngine = resultSet.getString("ENGINE");
//                 String rowFormat = resultSet.getString("NULL AS ROW_FORMAT");
//                 Long autoIncrement = resultSet.getLong("NULL AS AUTO_INCREMENT");
//                 String tableComment = resultSet.getString("TABLE_COMMENT");
//                 String tableCollation = resultSet.getString("NULL AS TABLE_COLLATION");
//                 table.setEngine(tableEngine);
//                 table.setRowFormat(rowFormat);
//                 table.setComment(tableComment);
//                 table.setAutoIncrement(autoIncrement);
//                 table.setCreateDefinition(showCreateTable);
//                 table.setCharsetAndCollation(tableCollation);
//                 // if (full) {
//                 //     table.setChecks(this.checks(dbName, tableName));
//                 //     table.setColumns(this.tableColumns(dbName, null, tableName));
//                 //     table.setIndexes(new DamengIndexes(this.indexes(dbName, tableName)));
//                 //     table.setTriggers(new DamengTriggers(this.triggers(dbName, tableName)));
//                 //     table.setForeignKeys(new DamengForeignKeys(this.foreignKeys(dbName, tableName)));
//                 // }
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return table;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     // @Override
//     // public int viewSize(String dbName) {
//     //     try {
//     //         int size = 0;
//     //         // 创建Statement对象
//     //         DatabaseMetaData metaData = this.schemaConnection(dbName).getMetaData();
//     //         // 执行SQL查询并获取结果集
//     //         ResultSet resultSet = metaData.getTables(null, dbName, "%", new String[]{"VIEW"});
//     //         // 打印元数据
//     //         DBUtil.printMetaData(resultSet);
//     //         while (resultSet.next()) {
//     //             size++;
//     //         }
//     //         // 关闭连接和释放资源
//     //         DBUtil.close(resultSet);
//     //         return size;
//     //     } catch (Exception ex) {
//     //         ex.printStackTrace();
//     //         throw new DBException(ex);
//     //     }
//     // }
//
//     @Override
//     public DamengView view(String dbName, String viewName) {
//         try {
//             String sql = "SELECT `TABLE_NAME`, `TABLE_COMMENT` FROM DBA_OBJECTS.`TABLES` WHERE `OWNER` = ? AND `TABLE_NAME` = ? AND `TABLE_TYPE` = 'VIEW'";
//             DBUtil.printSql(sql);
//             Connection connection = this.connection();
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             statement.setString(2, viewName);
//             // 执行SQL查询并获取结果集
//             ResultSet resultSet = statement.executeQuery();
//             // 打印元数据
//             DBUtil.printMetaData(resultSet);
//             // 遍历结果集
//             DamengView view = new DamengView();
//             while (resultSet.next()) {
//                 String tableName = resultSet.getString("TABLE_NAME");
//                 String tableComment = resultSet.getString("TABLE_COMMENT");
//                 Map<String, String> info = DamengHelper.getViewInfo(connection, dbName, tableName);
//                 view.setDbName(dbName);
//                 view.setName(tableName);
//                 view.setComment(tableComment);
//                 view.setDefiner(info.get("DEFINER"));
//                 view.setAlgorithm(info.get("ALGORITHM"));
//                 view.setDefinition(info.get("DEFINITION"));
//                 view.setCheckOption(info.get("CHECK_OPTION"));
//                 view.setSecurityType(info.get("SECURITY_TYPE"));
//                 view.setUpdatable(StrUtil.equalsIgnoreCase("YES", info.get("UPDATABLE")));
//             }
//             // 关闭连接和释放资源
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return view;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public List<DamengView> views(String dbName) {
//         try {
//             List<DamengView> list = new ArrayList<>();
//             String sql = "SELECT `TABLE_NAME`, `TABLE_COMMENT` FROM DBA_OBJECTS.`TABLES` WHERE `OWNER` = ? AND `TABLE_TYPE` = 'VIEW'";
//             DBUtil.printSql(sql);
//             Connection connection = this.connection();
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             // 执行SQL查询并获取结果集
//             ResultSet resultSet = statement.executeQuery();
//             // 打印元数据
//             DBUtil.printMetaData(resultSet);
//             // 遍历结果集
//             while (resultSet.next()) {
//                 DamengView view = new DamengView();
//                 String tableName = resultSet.getString("TABLE_NAME");
//                 String tableComment = resultSet.getString("TABLE_COMMENT");
//                 Map<String, String> info = DamengHelper.getViewInfo(connection, dbName, tableName);
//                 view.setDbName(dbName);
//                 view.setName(tableName);
//                 view.setComment(tableComment);
//                 view.setDefiner(info.get("DEFINER"));
//                 view.setAlgorithm(info.get("ALGORITHM"));
//                 view.setDefinition(info.get("DEFINITION"));
//                 view.setCheckOption(info.get("CHECK_OPTION"));
//                 view.setSecurityType(info.get("SECURITY_TYPE"));
//                 view.setUpdatable(StrUtil.equalsIgnoreCase("YES", info.get("UPDATABLE")));
//                 list.add(view);
//             }
//             // 关闭连接和释放资源
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return list;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public void dropView(String dbName, DamengView view) {
//         try {
//             String sql = "DROP VIEW IF EXISTS " + DBUtil.wrap(view.getDbName(), view.getName());
//             Statement statement = this.connection(dbName).createStatement();
//             DBUtil.printSql(sql);
//             statement.executeUpdate(sql);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public boolean existView(String dbName, String viewName) {
//         boolean result;
//         try {
//             DatabaseMetaData metaData = this.schemaConnection(dbName).getMetaData();
//             ResultSet resultSet = metaData.getTables(null, dbName, viewName, new String[]{"VIEW"});
//             result = resultSet.next();
//             DBUtil.close(resultSet);
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//         return result;
//     }
//
//     @Override
//     public void createView(String dbName, DamengView view) {
//         try {
//             Statement statement = this.connection(dbName).createStatement();
//             String sql = "CREATE ";
//             if (StrUtil.isNotBlank(view.getAlgorithm())) {
//                 sql += " ALGORITHM = " + view.getAlgorithm();
//             }
//             if (StrUtil.isNotBlank(view.getDefiner())) {
//                 sql += " DEFINER = " + view.getDefiner();
//             }
//             if (StrUtil.isNotBlank(view.getSecurityType())) {
//                 sql += " SQL SECURITY " + view.getSecurityType();
//             }
//             sql = sql + " VIEW " + DBUtil.wrap(dbName, view.getName()) + " AS " + view.getDefinition();
//             if (view.hasCheckOption()) {
//                 sql += " WITH " + view.getCheckOption() + " CHECK OPTION";
//             }
//             DBUtil.printSql(sql);
//             statement.execute(sql);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public void alertView(String dbName, DamengView view) {
//         try {
//             Statement statement = this.connection(dbName).createStatement();
//             String sql = "CREATE OR REPLACE ";
//             if (StrUtil.isNotBlank(view.getAlgorithm())) {
//                 sql += " ALGORITHM = " + view.getAlgorithm();
//             }
//             if (StrUtil.isNotBlank(view.getDefiner())) {
//                 sql += " DEFINER = " + view.getDefiner();
//             }
//             if (StrUtil.isNotBlank(view.getSecurityType())) {
//                 sql += " SQL SECURITY " + view.getSecurityType();
//             }
//             sql = sql + " VIEW " + DBUtil.wrap(dbName, view.getName()) + " AS " + view.getDefinition();
//             if (view.hasCheckOption()) {
//                 sql += " WITH " + view.getCheckOption() + " CHECK OPTION";
//             }
//             DBUtil.printSql(sql);
//             statement.execute(sql);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     // @Override
//     // public Long tableAutoIncrement(String dbName, String tableName) {
//     //     Long val = null;
//     //     try {
//     //         String sql = "SELECT NULL AS AUTO_INCREMENT FROM ALL_TABLES WHERE OWNER = ? AND TABLE_NAME = ?";
//     //         PreparedStatement statement = this.connection(dbName).prepareStatement(sql);
//     //         statement.setString(1, dbName);
//     //         statement.setString(2, tableName);
//     //         DBUtil.printSql(sql);
//     //         ResultSet resultSet = statement.executeQuery();
//     //         if (resultSet.next()) {
//     //             val = resultSet.getLong(1);
//     //         }
//     //         DBUtil.close(resultSet);
//     //         DBUtil.close(statement);
//     //     } catch (Exception ex) {
//     //         throw new DBException(ex);
//     //     }
//     //     return val;
//     // }
//
//     // @Override
//     // public long tableCount(String dbName, String tableName, List<DamengRecordFilter> filters) {
//     //     long count = 0;
//     //     try {
//     //         Connection connection = this.connection(dbName);
//     //         StringBuilder builder = new StringBuilder("SELECT COUNT(*) FROM");
//     //         builder.append(DBUtil.wrap(dbName, tableName));
//     //         String filterCondition = DamengConditionUtil.buildCondition(filters);
//     //         if (StrUtil.isNotBlank(filterCondition)) {
//     //             builder.append(" WHERE ").append(filterCondition);
//     //         }
//     //         String sql = builder.toString();
//     //         DBUtil.printSql(sql);
//     //         Statement statement = connection.createStatement();
//     //         ResultSet resultSet = statement.executeQuery(sql);
//     //         if (resultSet.next()) {
//     //             count = resultSet.getLong(1);
//     //         }
//     //         DBUtil.close(resultSet);
//     //         DBUtil.close(statement);
//     //     } catch (Exception ex) {
//     //         throw new DBException(ex);
//     //     }
//     //     return count;
//     // }
//
//     @Override
//     public List<DamengIndex> indexes(String dbName, String tableName) {
//         try {
//             Connection connection = this.connection();
//             Statement statement = connection.createStatement();
//             String sql = "SHOW INDEX FROM " + DBUtil.wrap(dbName, tableName);
//             DBUtil.printSql(sql);
//             ResultSet resultSet = statement.executeQuery(sql);
//             // 打印元数据
//             DBUtil.printMetaData(resultSet);
//             Map<String, DamengIndex> indexMap = new HashMap<>();
//             while (resultSet.next()) {
//                 String keyName = resultSet.getString("Key_name");
//                 // 主键类型的跳过
//                 if ("Primary".equalsIgnoreCase(keyName)) {
//                     continue;
//                 }
//                 DamengIndex tableIndex = indexMap.get(keyName);
//                 String columnName = resultSet.getString("Column_name");
//                 if (tableIndex == null) {
//                     int noneUnique = resultSet.getInt("Non_unique");
//                     int seqInIndex = resultSet.getInt("Seq_in_index");
//                     String indexType = resultSet.getString("Index_type");
//                     String indexComment = resultSet.getString("Index_comment");
//                     tableIndex = new DamengIndex();
//                     tableIndex.setName(keyName);
//                     tableIndex.setSeqIndex(seqInIndex);
//                     tableIndex.setComment(indexComment);
//                     tableIndex.type(indexType, noneUnique);
//                     indexMap.put(keyName, tableIndex);
//                 }
//                 int subPart = resultSet.getInt("Sub_Part");
//                 tableIndex.addColumn(columnName, subPart);
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return new ArrayList<>(indexMap.values());
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public DamengChecks checks(String dbName, String tableName) {
//         if (!this.isSupportCheckFeature()) {
//             return null;
//         }
//         try {
//             String sql = """
//                     SELECT
//                         CHECK_CLAUSE AS 'CLAUSE',
//                         CONSTRAINT_NAME AS 'NAME',
//                         TABLE_NAME AS 'TABLE_NAME',
//                         OWNER AS 'DB_NAME'
//                     FROM
//                         DBA_OBJECTS.CHECK_CONSTRAINTS
//                     WHERE
//                         OWNER = ?
//                     AND
//                         TABLE_NAME = ?;
//                     """;
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             statement.setString(2, tableName);
//             ResultSet resultSet = statement.executeQuery();
//             DBUtil.printMetaData(resultSet);
//             DamengChecks checks = new DamengChecks();
//             while (resultSet.next()) {
//                 DamengCheck check = new DamengCheck();
//                 String name = resultSet.getString("NAME");
//                 String clause = resultSet.getString("CLAUSE");
//                 check.setName(name);
//                 check.setClause(clause);
//                 check.setDbName(dbName);
//                 check.setTableName(tableName);
//                 checks.add(check);
//             }
//             DBUtil.close(resultSet);
//             return checks;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public List<DamengForeignKey> foreignKeys(String dbName, String tableName) {
//         try {
//             // 查询外键
//             String sql = """
//                     SELECT
//                         a.COLUMN_NAME AS 'FKCOLUMN_NAME',
//                         a.REFERENCED_OWNER AS 'PKTABLE_CAT',
//                         a.REFERENCED_TABLE_NAME AS 'PKTABLE_NAME',
//                         a.REFERENCED_COLUMN_NAME AS 'PKCOLUMN_NAME',
//                         a.CONSTRAINT_NAME AS 'FK_NAME',
//                         a1.UPDATE_RULE,
//                         a1.DELETE_RULE
//                     FROM
//                         DBA_OBJECTS.KEY_COLUMN_USAGE a
//                     JOIN
//                         DBA_OBJECTS.REFERENTIAL_CONSTRAINTS a1
//                     ON
//                         a.CONSTRAINT_NAME = a1.CONSTRAINT_NAME
//                     WHERE
//                         a.REFERENCED_OWNER = ?
//                     AND
//                         a.TABLE_NAME = ?
//                     AND
//                         a.REFERENCED_TABLE_NAME IS NOT NULL;
//                     """;
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             statement.setString(2, tableName);
//             ResultSet resultSet = statement.executeQuery();
//             DBUtil.printMetaData(resultSet);
//             Map<String, DamengForeignKey> foreignKeyMap = new HashMap<>();
//             while (resultSet.next()) {
//                 String fkName = resultSet.getString("FK_NAME");
//                 String fkColumnName = resultSet.getString("FKCOLUMN_NAME");
//                 String pkColumnName = resultSet.getString("PKCOLUMN_NAME");
//                 DamengForeignKey foreignKey = foreignKeyMap.get(fkName);
//                 if (foreignKey == null) {
//                     String pkTableName = resultSet.getString("PKTABLE_NAME");
//                     String pkTableCat = resultSet.getString("PKTABLE_CAT");
//                     String updateRule = resultSet.getString("UPDATE_RULE");
//                     String deleteRule = resultSet.getString("DELETE_RULE");
//                     foreignKey = new DamengForeignKey();
//                     foreignKey.setName(fkName);
//                     foreignKey.setUpdatePolicy(updateRule == null ? null : updateRule.toUpperCase());
//                     foreignKey.setDeletePolicy(deleteRule == null ? null : deleteRule.toUpperCase());
//                     foreignKey.setPrimaryKeyTable(pkTableName);
//                     foreignKey.setPrimaryKeyDatabase(pkTableCat);
//                     foreignKeyMap.put(fkName, foreignKey);
//                 }
//                 foreignKey.addColumn(fkColumnName);
//                 foreignKey.addPrimaryKeyColumn(pkColumnName);
//             }
//             DBUtil.close(resultSet);
//             return new ArrayList<>(foreignKeyMap.values());
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     // @Override
//     // public DamengColumns tableColumns(String dbName, String schema, String tableName) {
//     //     if (StrUtil.isBlank(tableName)) {
//     //         return null;
//     //     }
//     //     try {
//     //         String sql = """
//     //                 SELECT
//     //                     a.EXTRA as COLUMN_EXTRA,
//     //                     a.COLUMN_KEY as COLUMN_KEY,
//     //                     a.COLUMN_COMMENT as REMARKS,
//     //                     a.COLUMN_TYPE as COLUMN_TYPE,
//     //                     a.COLUMN_NAME as COLUMN_NAME,
//     //                     a.IS_NULLABLE as IS_NULLABLE,
//     //                     a.COLUMN_DEFAULT as COLUMN_DEF,
//     //                     a.COLLATION_NAME as COLLATION_NAME,
//     //                     a.CHARACTER_SET_NAME as CHARSET_NAME,
//     //                     a.ORDINAL_POSITION as ORDINAL_POSITION
//     //                 FROM
//     //                     INFORMATION_SCHEMA.`COLUMNS` a
//     //                 WHERE
//     //                     a.OWNER = ?
//     //                 AND
//     //                     a.TABLE_NAME = ?
//     //                 """;
//     //         PreparedStatement statement = this.connection().prepareStatement(sql);
//     //         statement.setString(1, dbName);
//     //         statement.setString(2, tableName);
//     //         ResultSet resultSet = statement.executeQuery();
//     //         // 打印元数据
//     //         DBUtil.printMetaData(resultSet);
//     //         Map<String, DamengColumn> columns = new HashMap<>();
//     //         while (resultSet.next()) {
//     //             Object def = resultSet.getObject("COLUMN_DEF");
//     //             String remarks = resultSet.getString("REMARKS");
//     //             int position = resultSet.getInt("ORDINAL_POSITION");
//     //             String nullable = resultSet.getString("IS_NULLABLE");
//     //             String columnKey = resultSet.getString("COLUMN_KEY");
//     //             String columnType = resultSet.getString("COLUMN_TYPE");
//     //             String columnName = resultSet.getString("COLUMN_NAME");
//     //             String charsetName = resultSet.getString("CHARSET_NAME");
//     //             String columnExtra = resultSet.getString("COLUMN_EXTRA");
//     //             String collationName = resultSet.getString("COLLATION_NAME");
//     //             DamengColumn column = new DamengColumn();
//     //             column.initColumn(columnType, columnExtra);
//     //             column.setDbName(dbName);
//     //             column.setName(columnName);
//     //             column.setComment(remarks);
//     //             column.setDefaultValue(def);
//     //             column.setPosition(position);
//     //             column.setCharset(charsetName);
//     //             column.setTableName(tableName);
//     //             column.setCollation(collationName);
//     //             column.setNullable("yes".equalsIgnoreCase(nullable));
//     //             column.setPrimaryKey("pri".equalsIgnoreCase(columnKey));
//     //             columns.put(columnName, column);
//     //         }
//     //         DBUtil.close(resultSet);
//     //         DBUtil.close(statement);
//     //         // 初始化状态
//     //         for (DamengColumn value : columns.values()) {
//     //             value.initStatus();
//     //         }
//     //         // 返回排序后的数据
//     //         return new DamengColumns(CollUtil.sort(columns.values(), Comparator.comparingInt(DamengColumn::getPosition)));
//     //     } catch (Exception ex) {
//     //         ex.printStackTrace();
//     //         throw new DBException(ex);
//     //     }
//     // }
//
//     // @Override
//     // public List<DamengRecord> selectTableRecords(String dbName, String tableName, Long start, Long limit, DamengColumns dbColumns, List<DamengRecordFilter> filters, boolean readonly) {
//     //     try {
//     //         Connection connection = this.connection(dbName);
//     //         StringBuilder builder = new StringBuilder("SELECT * FROM ");
//     //         builder.append(DBUtil.wrap(dbName, tableName));
//     //         String filterCondition = DamengConditionUtil.buildCondition(filters);
//     //         if (StrUtil.isNotBlank(filterCondition)) {
//     //             builder.append(" WHERE ").append(filterCondition);
//     //         }
//     //         if (start != null && limit != null) {
//     //             builder.append(" LIMIT ").append(start).append(",").append(limit);
//     //         }
//     //         String sql = builder.toString();
//     //         DBUtil.printSql(sql);
//     //         Statement statement = connection.createStatement();
//     //         ResultSet resultSet = statement.executeQuery(sql);
//     //         DBUtil.printMetaData(resultSet);
//     //         List<DamengRecord> records = new ArrayList<>();
//     //         DamengColumns columns;
//     //         if (dbColumns != null && !dbColumns.isEmpty()) {
//     //             columns = dbColumns;
//     //         } else {
//     //             columns = DamengHelper.parseColumns(resultSet);
//     //         }
//     //         while (resultSet.next()) {
//     //             DamengRecord record = new DamengRecord(readonly);
//     //             for (DamengColumn column : columns) {
//     //                 Object data = resultSet.getObject(column.getName());
//     //                 // 获取几何值
//     //                 if (column.supportGeometry()) {
//     //                     data = DamengHelper.getGeometryString(connection, data);
//     //                 }
//     //                 record.putValue(column, data);
//     //             }
//     //             records.add(record);
//     //         }
//     //         DBUtil.close(resultSet);
//     //         DBUtil.close(statement);
//     //         return records;
//     //     } catch (Exception ex) {
//     //         throw new DBException(ex);
//     //     }
//     // }
//
//     @Override
//     public List<DamengColumn> viewColumns(String dbName, String viewName) {
//         try {
//             if (StrUtil.isBlank(viewName)) {
//                 return Collections.emptyList();
//             }
//             String sql = """
//                     SELECT
//                         a.EXTRA as COLUMN_EXTRA,
//                         a.COLUMN_KEY as COLUMN_KEY,
//                         a.COLUMN_COMMENT as REMARKS,
//                         a.COLUMN_TYPE as COLUMN_TYPE,
//                         a.COLUMN_NAME as COLUMN_NAME,
//                         a.IS_NULLABLE as IS_NULLABLE,
//                         a.COLUMN_DEFAULT as COLUMN_DEF,
//                         a.COLLATION_NAME as COLLATION_NAME,
//                         a.CHARACTER_SET_NAME as CHARSET_NAME,
//                         a.ORDINAL_POSITION as ORDINAL_POSITION
//                     FROM
//                         INFORMATION_SCHEMA.`COLUMNS` a
//                     WHERE
//                         a.OWNER = ?
//                     AND
//                         a.TABLE_NAME = ?
//                     """;
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             statement.setString(2, viewName);
//             ResultSet resultSet = statement.executeQuery();
//             // 打印元数据
//             DBUtil.printMetaData(resultSet);
//             Map<String, DamengColumn> columns = new HashMap<>();
//             while (resultSet.next()) {
//                 Object def = resultSet.getObject("COLUMN_DEF");
//                 String remarks = resultSet.getString("REMARKS");
//                 int position = resultSet.getInt("ORDINAL_POSITION");
//                 String nullable = resultSet.getString("IS_NULLABLE");
//                 String columnKey = resultSet.getString("COLUMN_KEY");
//                 String columnType = resultSet.getString("COLUMN_TYPE");
//                 String columnName = resultSet.getString("COLUMN_NAME");
//                 String charsetName = resultSet.getString("CHARSET_NAME");
//                 String columnExtra = resultSet.getString("COLUMN_EXTRA");
//                 String collationName = resultSet.getString("COLLATION_NAME");
//                 DamengColumn column = new DamengColumn();
//                 column.initColumn(columnType, columnExtra);
//                 column.setDbName(dbName);
//                 column.setName(columnName);
//                 column.setComment(remarks);
//                 column.setDefaultValue(def);
//                 column.setPosition(position);
//                 column.setCharset(charsetName);
//                 column.setTableName(viewName);
//                 column.setCollation(collationName);
//                 column.setNullable("yes".equalsIgnoreCase(nullable));
//                 column.setPrimaryKey("pri".equalsIgnoreCase(columnKey));
//                 columns.put(columnName, column);
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//
//             sql = "SELECT * FROM " + DBUtil.wrap(dbName, viewName) + " LIMIT 1";
//             PreparedStatement statement1 = this.connection().prepareStatement(sql);
//             ResultSet resultSet1 = statement1.executeQuery();
//             DamengColumns dbColumns = DamengHelper.parseColumns(resultSet1);
//             DBUtil.close(resultSet1);
//             DBUtil.close(statement1);
//
//             // 初始化状态
//             for (DamengColumn value : columns.values()) {
//                 DamengColumn dbColumn = dbColumns.column(value.getName());
//                 if (dbColumn != null) {
//                     value.setNullable(dbColumn.isNullable());
//                     value.setAutoIncrement(dbColumn.isAutoIncrement());
//                 }
//                 value.initStatus();
//             }
//             // 返回排序后的数据
//             return CollUtil.sort(columns.values(), Comparator.comparingInt(DamengColumn::getPosition));
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public List<DamengRecord> viewRecords(String dbName, String viewName, Long start, Long limit, List<DamengRecordFilter> filters) {
//         try {
//             Connection connection = this.connection(dbName);
//             StringBuilder builder = new StringBuilder("SELECT * FROM ");
//             builder.append(DBUtil.wrap(dbName, viewName));
//             String filterCondition = DamengConditionUtil.buildCondition(filters);
//             if (StrUtil.isNotBlank(filterCondition)) {
//                 builder.append(" WHERE ").append(filterCondition);
//             }
//             if (start != null && limit != null) {
//                 builder.append(" LIMIT ").append(start).append(",").append(limit);
//             }
//             String sql = builder.toString();
//             DBUtil.printSql(sql);
//             Statement statement = connection.createStatement();
//             ResultSet resultSet = statement.executeQuery(sql);
//             DBUtil.printMetaData(resultSet);
//             List<DamengRecord> records = new ArrayList<>();
//             boolean updatable = DamengHelper.isViewUpdatable(connection, dbName, viewName);
//             DamengColumns columns = DamengHelper.parseColumns(resultSet);
//             while (resultSet.next()) {
//                 DamengRecord record = new DamengRecord(!updatable);
//                 for (DamengColumn column : columns) {
//                     Object data = resultSet.getObject(column.getName());
//                     // 获取几何值
//                     if (column.supportGeometry()) {
//                         data = DamengHelper.getGeometryString(connection, data);
//                     }
//                     record.putValue(column, data);
//                 }
//                 records.add(record);
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return records;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public int insertRecord(String dbName, String tableName, DamengRecordData recordData, DamengRecordPrimaryKey primaryKey) {
//         if (recordData == null || recordData.isEmpty()) {
//             return 0;
//         }
//         try {
//             StringBuilder builder = new StringBuilder();
//             builder.append("INSERT INTO ").append(DBUtil.wrap(dbName, tableName)).append("(");
//             for (String column : recordData.columns()) {
//                 builder.append(DBUtil.wrap(column)).append(",");
//             }
//             builder.append(")");
//             builder.append(" VALUES(");
//             for (String column : recordData.columns()) {
//                 if (recordData.isTypeGeometry(column)) {
//                     builder.append("ST_GeomFromText(?),");
//                 } else {
//                     builder.append("?,");
//                 }
//             }
//             builder.append(")");
//             String sql = builder.toString();
//             sql = sql.replaceAll(",\\)", ")");
//             DBUtil.printSql(sql);
//             Connection connection = this.connection(dbName);
//             PreparedStatement statement = connection.prepareStatement(sql);
//             int index = 1;
//             for (String colName : recordData.columns()) {
//                 DBUtil.setVal(statement, recordData.value(colName), index++);
//             }
//             int count = statement.executeUpdate();
//             // 处理自动递增值
//             if (primaryKey != null && primaryKey.shouldReturnData()) {
//                 primaryKey.setReturnData(DamengHelper.lastInsertId(connection));
//             }
//             DBUtil.close(statement);
//             return count;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     // @Override
//     // public int insertViewRecord(String dbName, String viewName, DBTableRecordData recordData, DBTableRecordPrimaryKey primaryKey) {
//     //     if (recordData == null || recordData.isEmpty()) {
//     //         return 0;
//     //     }
//     //     try {
//     //         StringBuilder builder = new StringBuilder();
//     //         builder.append("INSERT INTO ").append(DBUtil.wrap(dbName, viewName)).append("(");
//     //         for (String column : recordData.columns()) {
//     //             builder.append(DBUtil.wrap(column)).append(",");
//     //         }
//     //         builder.append(")");
//     //         builder.append(" VALUES(");
//     //         for (String column : recordData.columns()) {
//     //             if (recordData.isTypeGeometry(column)) {
//     //                 builder.append("ST_GeomFromText(?),");
//     //             } else {
//     //                 builder.append("?,");
//     //             }
//     //         }
//     //         builder.append(")");
//     //         String sql = builder.toString();
//     //         sql = sql.replaceAll(",\\)", ")");
//     //         DBUtil.printSql(sql);
//     //         Connection connection = this.connection(dbName);
//     //         PreparedStatement statement = connection.prepareStatement(sql);
//     //         int index = 1;
//     //         for (String colName : recordData.columns()) {
//     //             DBUtil.setVal(statement, recordData.value(colName), index++);
//     //         }
//     //         int count = statement.executeUpdate();
//     //         // 处理自动递增值
//     //         if (primaryKey != null && primaryKey.shouldReturnData()) {
//     //             primaryKey.setReturnData(DamengHelper.lastInsertId(connection));
//     //         }
//     //         DBUtil.close(statement);
//     //         return count;
//     //     } catch (Exception ex) {
//     //         ex.printStackTrace();
//     //         throw new DBException(ex);
//     //     }
//     // }
//
//     @Override
//     public int deleteRecord(String dbName, String tableName, DamengRecordData recordData) {
//         try {
//             StringBuilder builder = new StringBuilder();
//             builder.append("DELETE FROM ").append(DBUtil.wrap(dbName, tableName));
//             // 参数
//             boolean first = true;
//             for (String colName : recordData.columns()) {
//                 if (first) {
//                     builder.append(" WHERE ");
//                     first = false;
//                 } else {
//                     builder.append(" AND ");
//                 }
//                 builder.append(DBUtil.wrap(colName)).append(" = ?");
//             }
//             // builder.append(" LIMIT 1");
//             String sql = builder.toString();
//             DBUtil.printSql(sql);
//             Connection connection = this.connection(dbName);
//             PreparedStatement statement = connection.prepareStatement(sql);
//             int index = 1;
//             // 设置参数
//             for (String colName : recordData.columns()) {
//                 DBUtil.setVal(statement, recordData.value(colName), index++);
//             }
//             int count = DBUtil.executeUpdate(statement);
//             DBUtil.close(statement);
//             return count;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     // @Override
//     // public int deleteViewRecord(String dbName, String viewName, DBTableRecordData recordData) {
//     //     try {
//     //         StringBuilder builder = new StringBuilder();
//     //         builder.append("DELETE FROM ").append(DBUtil.wrap(dbName, viewName));
//     //         // 参数
//     //         boolean first = true;
//     //         for (String colName : recordData.columns()) {
//     //             if (first) {
//     //                 builder.append(" WHERE ");
//     //                 first = false;
//     //             } else {
//     //                 builder.append(" AND ");
//     //             }
//     //             builder.append(DBUtil.wrap(colName)).append(" = ?");
//     //         }
//     //         String sql = builder.toString();
//     //         DBUtil.printInfo(sql, recordData);
//     //         Connection connection = this.connection(dbName);
//     //         PreparedStatement statement = connection.prepareStatement(sql);
//     //         int index = 1;
//     //         // 设置参数
//     //         for (String colName : recordData.columns()) {
//     //             DBUtil.setVal(statement, recordData.value(colName), index++);
//     //         }
//     //         int count = DBUtil.executeUpdate(statement);
//     //         DBUtil.close(statement);
//     //         return count;
//     //     } catch (Exception ex) {
//     //         throw new DBException(ex);
//     //     }
//     // }
//
//     @Override
//     public int deleteRecord(String dbName, String tableName, DamengRecordPrimaryKey primaryKey) {
//         try {
//             String sql = "DELETE FROM " + DBUtil.wrap(dbName, tableName) + " WHERE " + DBUtil.wrap(primaryKey.getColumnName()) + " = ?";
//             // String sql = "DELETE FROM " + DBUtil.wrap(dbName, tableName) + " WHERE " + DBUtil.wrap(primaryKey.getColumnName()) + " = ? LIMIT 1";
//             DBUtil.printSql(sql);
//             Connection connection = this.connection(dbName);
//             PreparedStatement statement = connection.prepareStatement(sql);
//             DBUtil.setVal(statement, primaryKey.originalData(), 1);
//             return DBUtil.executeUpdate(statement);
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     // @Override
//     // public int deleteViewRecord(String dbName, String viewName, DBTableRecordPrimaryKey primaryKey) {
//     //     try {
//     //         String sql = "DELETE FROM " + DBUtil.wrap(dbName, viewName) + " WHERE " + DBUtil.wrap(primaryKey.getColumnName()) + " = ?";
//     //         DBUtil.printSql(sql);
//     //         Connection connection = this.connection(dbName);
//     //         PreparedStatement statement = connection.prepareStatement(sql);
//     //         DBUtil.setVal(statement, primaryKey.originalData(), 1);
//     //         return DBUtil.executeUpdate(statement);
//     //     } catch (Exception ex) {
//     //         throw new DBException(ex);
//     //     }
//     // }
//
//     @Override
//     public int updateRecord(String dbName, String tableName, DamengRecordData recordData, DamengRecordData originalRecordData) {
//         try {
//             StringBuilder builder = new StringBuilder();
//             builder.append("UPDATE ").append(DBUtil.wrap(dbName, tableName)).append(" SET ");
//             // 值
//             for (String column : recordData.columns()) {
//                 builder.append(DBUtil.wrap(column)).append(" = ?,");
//             }
//             // 删除最后一个字符
//             builder.deleteCharAt(builder.length() - 1);
//             // 参数
//             boolean first = true;
//             for (String column : originalRecordData.columns()) {
//                 if (first) {
//                     builder.append(" WHERE ");
//                     first = false;
//                 } else {
//                     builder.append(" AND ");
//                 }
//                 builder.append(DBUtil.wrap(column)).append(" = ?");
//             }
//             // builder.append(" LIMIT 1");
//             String sql = builder.toString();
//             DBUtil.printSql(sql);
//             Connection connection = this.connection(dbName);
//             PreparedStatement statement = connection.prepareStatement(sql);
//             int index = 1;
//             // 设置值
//             for (String colName : recordData.columns()) {
//                 DBUtil.setVal(statement, recordData.value(colName), index++);
//             }
//             // 设置参数
//             for (String colName : originalRecordData.columns()) {
//                 DBUtil.setVal(statement, originalRecordData.value(colName), index++);
//             }
//             int count = DBUtil.executeUpdate(statement);
//             DBUtil.close(statement);
//             return count;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public int updateRecord(String dbName, String tableName, DamengRecordData recordData, DamengRecordPrimaryKey primaryKey) {
//         try {
//             StringBuilder builder = new StringBuilder();
//             builder.append("UPDATE ").append(DBUtil.wrap(dbName, tableName)).append(" SET ");
//             for (String column : recordData.columns()) {
//                 if (recordData.isTypeGeometry(column)) {
//                     builder.append(DBUtil.wrap(column)).append(" = ST_GeomFromText(?),");
//                 } else {
//                     builder.append(DBUtil.wrap(column)).append(" = ?,");
//                 }
//             }
//             // 删除最后一个字符
//             builder.deleteCharAt(builder.length() - 1);
//             // builder.append(" WHERE ").append(DBUtil.wrap(primaryKey.getColumnName())).append(" = ? LIMIT 1");
//             builder.append(" WHERE ").append(DBUtil.wrap(primaryKey.getColumnName())).append(" = ?");
//             String sql = builder.toString();
//             DBUtil.printInfo(sql, recordData);
//             Connection connection = this.connection(dbName);
//             PreparedStatement statement = connection.prepareStatement(sql);
//             int index = 1;
//             for (String colName : recordData.columns()) {
//                 DBUtil.setVal(statement, recordData.value(colName), index++);
//             }
//             DBUtil.setVal(statement, primaryKey.originalData(), index);
//             int count = DBUtil.executeUpdate(statement);
//             DBUtil.close(statement);
//             return count;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     // @Override
//     // public int updateViewRecord(String dbName, String tableName, DBTableRecordData recordData, DBTableRecordData originalRecordData) {
//     //     try {
//     //         StringBuilder builder = new StringBuilder();
//     //         builder.append("UPDATE ").append(DBUtil.wrap(dbName, tableName)).append(" SET ");
//     //         // 值
//     //         for (String column : recordData.columns()) {
//     //             builder.append(DBUtil.wrap(column)).append(" = ?,");
//     //         }
//     //         // 删除最后一个字符
//     //         builder.deleteCharAt(builder.length() - 1);
//     //         // 参数
//     //         boolean first = true;
//     //         for (String column : originalRecordData.columns()) {
//     //             if (first) {
//     //                 builder.append(" WHERE ");
//     //                 first = false;
//     //             } else {
//     //                 builder.append(" AND ");
//     //             }
//     //             builder.append(DBUtil.wrap(column)).append(" = ?");
//     //         }
//     //         String sql = builder.toString();
//     //         DBUtil.printSql(sql);
//     //         Connection connection = this.connection(dbName);
//     //         PreparedStatement statement = connection.prepareStatement(sql);
//     //         int index = 1;
//     //         DBUtil.printData(recordData);
//     //         DBUtil.printData(originalRecordData);
//     //         // 设置值
//     //         for (String colName : recordData.columns()) {
//     //             DBUtil.setVal(statement, recordData.value(colName), index++);
//     //         }
//     //         // 设置参数
//     //         for (String colName : originalRecordData.columns()) {
//     //             DBUtil.setVal(statement, originalRecordData.value(colName), index++);
//     //         }
//     //         int count = DBUtil.executeUpdate(statement);
//     //         DBUtil.close(statement);
//     //         return count;
//     //     } catch (Exception ex) {
//     //         ex.printStackTrace();
//     //         throw new DBException(ex);
//     //     }
//     // }
//
//     // @Override
//     // public int updateViewRecord(String dbName, String tableName, DBTableRecordData recordData, DBTableRecordPrimaryKey primaryKey) {
//     //     try {
//     //         StringBuilder builder = new StringBuilder();
//     //         builder.append("UPDATE ").append(DBUtil.wrap(dbName, tableName)).append(" SET ");
//     //         for (String column : recordData.columns()) {
//     //             if (recordData.isTypeGeometry(column)) {
//     //                 builder.append(DBUtil.wrap(column)).append(" = ST_GeomFromText(?),");
//     //             } else {
//     //                 builder.append(DBUtil.wrap(column)).append(" = ?,");
//     //             }
//     //         }
//     //         // 删除最后一个字符
//     //         builder.deleteCharAt(builder.length() - 1);
//     //         builder.append(" WHERE ").append(DBUtil.wrap(primaryKey.getColumnName())).append(" = ?");
//     //         String sql = builder.toString();
//     //         DBUtil.printInfo(sql, recordData);
//     //         Connection connection = this.connection(dbName);
//     //         PreparedStatement statement = connection.prepareStatement(sql);
//     //         int index = 1;
//     //         for (String colName : recordData.columns()) {
//     //             DBUtil.setVal(statement, recordData.value(colName), index++);
//     //         }
//     //         DBUtil.setVal(statement, primaryKey.originalData(), index);
//     //         int count = DBUtil.executeUpdate(statement);
//     //         DBUtil.close(statement);
//     //         return count;
//     //     } catch (Exception ex) {
//     //         ex.printStackTrace();
//     //         throw new DBException(ex);
//     //     }
//     // }
//
//     @Override
//     public void createTable(String dbName, DamengTable table) {
//         Connection connection = null;
//         try {
//             connection = this.connection(dbName);
//             Statement statement = connection.createStatement();
//             table.setDbName(dbName);
//             String sql = TableCreateSqlGenerator.generate(this.dialect(), table);
//             DBUtil.printSql(sql);
//             List<String> sqlList = DBSqlParser.parseSql(sql, this.dialect());
//             connection.setAutoCommit(false);
//             for (String sqlStr : sqlList) {
//                 statement.executeUpdate(sqlStr);
//             }
//             connection.commit();
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             DBUtil.rollback(connection);
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public void alertTable(DamengAlertTableParam param) {
//         Connection connection = null;
//         try {
//             String dbName = param.table().getDbName();
//
//             connection = this.connection(dbName);
//             // param.setDbName(dbName);
//             Statement statement = connection.createStatement();
//             // boolean hasPrimaryKey = DamengHelper.hasPrimaryKey(this.connection(), dbName, table.getName());
//             // table.setHasPrimaryKey(hasPrimaryKey);
//             String sql = TableAlertSqlGenerator.generate(this.dialect(), param);
//             DBUtil.printSql(sql);
//             List<String> sqlList = DBSqlParser.parseSql(sql, this.dialect());
//             connection.setAutoCommit(false);
//             for (String sqlStr : sqlList) {
//                 statement.executeUpdate(sqlStr);
//             }
//             connection.commit();
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             DBUtil.rollback(connection);
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public boolean existTable(String dbName, String tableName) {
//         boolean result;
//         try {
//             DatabaseMetaData metaData = this.schemaConnection(dbName).getMetaData();
//             ResultSet resultSet = metaData.getTables(null, dbName, tableName, new String[]{"TABLE"});
//             result = resultSet.next();
//             DBUtil.close(resultSet);
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//         return result;
//     }
//
//     @Override
//     public boolean renameTable(String dbName, String oldTableName, String newTableName) {
//         boolean result;
//         try {
//             Statement statement = this.connection().createStatement();
//             String sql = "RENAME TABLE " + DBUtil.wrap(dbName, oldTableName) + " TO " + DBUtil.wrap(dbName, newTableName);
//             DBUtil.printSql(sql);
//             statement.execute(sql);
//             DBUtil.close(statement);
//             result = true;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//         return result;
//     }
//
//     @Override
//     public boolean clearTable(String dbName, String tableName) {
//         try {
//             Statement statement = this.connection(dbName).createStatement();
//             String sql = "DELETE FROM " + DBUtil.wrap(dbName, tableName);
//             DBUtil.printSql(sql);
//             statement.executeUpdate(sql);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//         return true;
//     }
//
//     @Override
//     public void truncateTable(String dbName, String schema, String tableName) {
//         try {
//             Statement statement = this.connection(dbName).createStatement();
//             String sql = "TRUNCATE TABLE " + DBUtil.wrap(dbName, tableName);
//             DBUtil.printSql(sql);
//             statement.executeUpdate(sql);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public boolean dropTable(String dbName, String tableName) {
//         try {
//             Statement statement = this.connection(dbName).createStatement();
//             String sql = "DROP TABLE " + DBUtil.wrap(dbName, tableName);
//             DBUtil.printSql(sql);
//             statement.executeUpdate(sql);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//         return true;
//     }
//
//     @Override
//     public List<String> charsets() {
//         if (this.hasProperty("charsets")) {
//             return this.getProperty("charsets");
//         }
//         try {
//             List<String> charsets = new ArrayList<>();
//             Statement statement = this.connection().createStatement();
//             String sql = "SELECT CHARACTER_SET_NAME FROM INFORMATION_SCHEMA.CHARACTER_SETS;";
//             DBUtil.printSql(sql);
//             ResultSet resultSet = statement.executeQuery(sql);
//             while (resultSet.next()) {
//                 charsets.add(resultSet.getString(1));
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             this.putProperty("charsets", charsets);
//             return charsets;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//
//     @Override
//     public List<String> collation(String charset) {
//         Map<String, List<String>> collations = this.getProperty("collation");
//         if (collations == null) {
//             collations = new HashMap<>();
//             this.putProperty("collations", collations);
//         }
//         charset = charset.toUpperCase();
//         if (collations.containsKey(charset)) {
//             return collations.get(charset.toUpperCase());
//         }
//         try {
//             String sql = "SELECT COLLATION_NAME FROM INFORMATION_SCHEMA.COLLATIONS WHERE CHARACTER_SET_NAME = ?;";
//             DBUtil.printSql(sql);
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, charset);
//             ResultSet resultSet = statement.executeQuery();
//             List<String> list = new ArrayList<>();
//             while (resultSet.next()) {
//                 list.add(resultSet.getString(1));
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             collations.put(charset, list);
//             return list;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     // @Override
//     // public String columnCollation(String dbName, String tableName, String columnName) {
//     //     String collation = null;
//     //     try {
//     //         String sql = "SELECT COLLATION_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE OWNER = ? AND TABLE_NAME = ? AND COLUMN_NAME= ?;";
//     //         DBUtil.printSql(sql);
//     //         PreparedStatement statement = this.connection().prepareStatement(sql);
//     //         statement.setString(1, dbName);
//     //         statement.setString(2, tableName);
//     //         statement.setString(3, columnName);
//     //         ResultSet resultSet = statement.executeQuery();
//     //         while (resultSet.next()) {
//     //             collation = resultSet.getString(1);
//     //         }
//     //         DBUtil.close(resultSet);
//     //         DBUtil.close(statement);
//     //     } catch (Exception ex) {
//     //         throw new DBException(ex);
//     //     }
//     //     return collation;
//     // }
//
//     @Override
//     public boolean existDatabase(String dbName) {
//         boolean result = false;
//         try {
//             DatabaseMetaData metaData = this.connection().getMetaData();
//             // 执行查询操作，检查数据库是否存在
//             ResultSet resultSet = metaData.getCatalogs();
//             while (resultSet.next()) {
//                 String catalogName = resultSet.getString("TABLE_CAT");
//                 if (catalogName.equals(dbName)) {
//                     result = true;
//                     break;
//                 }
//             }
//             DBUtil.close(resultSet);
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//         return result;
//     }
//
//     @Override
//     public boolean createDatabase(DBDatabase database) {
//         try {
//             StringBuilder builder = new StringBuilder("CREATE DATABASE ").append(DBUtil.wrap(database.getName()));
//             if (database.getCharset() != null) {
//                 builder.append(" CHARACTER SET ").append(DBUtil.wrapData(database.getCharset()));
//             }
//             if (database.getCollation() != null) {
//                 builder.append(" COLLATE ").append(DBUtil.wrapData(database.getCollation()));
//             }
//             String sql = builder.toString();
//             DBUtil.printSql(sql);
//             Statement statement = this.connection().createStatement();
//             statement.execute(sql);
//             DBUtil.close(statement);
//             return true;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public boolean alterDatabase(DBDatabase database) {
//         try {
//             // 无变化
//             if (database.getCharset() == null && database.getCollation() == null) {
//                 return true;
//             }
//             StringBuilder builder = new StringBuilder("ALTER DATABASE ").append(DBUtil.wrap(database.getName()));
//             if (database.getCharset() != null) {
//                 builder.append(" CHARACTER SET ").append(DBUtil.wrapData(database.getCharset()));
//             }
//             if (database.getCollation() != null) {
//                 builder.append(" COLLATE ").append(DBUtil.wrapData(database.getCollation()));
//             }
//             String sql = builder.toString();
//             DBUtil.printSql(sql);
//             Statement statement = this.connection().createStatement();
//             statement.execute(sql);
//             DBUtil.close(statement);
//             return true;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public String databaseCollation(String dbName) {
//         String collation = null;
//         try {
//             String sql = "SELECT DEFAULT_COLLATION_NAME FROM ALL_USERS WHERE SCHEMA_NAME = ?;";
//             DBUtil.printSql(sql);
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             ResultSet resultSet = statement.executeQuery();
//             while (resultSet.next()) {
//                 collation = resultSet.getString(1);
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//         return collation;
//     }
//
//     @Override
//     public boolean dropDatabase(String dbName) {
//         try {
//             String sql = "DROP DATABASE " + DBUtil.wrap(dbName);
//             DBUtil.printSql(sql);
//             Statement statement = this.connection().createStatement();
//             statement.executeUpdate(sql);
//             DBUtil.close(statement);
//             return true;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     // @Override
//     // public boolean isUpdateOnCurrentTimestamp(String dbName, String tableName, String colName) {
//     //     try {
//     //         StringBuilder builder = new StringBuilder("SHOW FULL COLUMNS FROM ").append(DBUtil.wrap(dbName, tableName));
//     //         builder.append(" WHERE Field=").append(DBUtil.wrapData(colName));
//     //         builder.append(" AND Extra LIKE '%on update CURRENT_TIMESTAMP%'");
//     //         String sql = builder.toString();
//     //         DBUtil.printSql(sql);
//     //         Statement statement = this.connection().createStatement();
//     //         ResultSet resultSet = statement.executeQuery(sql);
//     //         DBUtil.printMetaData(resultSet);
//     //         boolean result = resultSet.next();
//     //         DBUtil.close(resultSet);
//     //         DBUtil.close(statement);
//     //         return result;
//     //     } catch (Exception ex) {
//     //         ex.printStackTrace();
//     //         throw new DBException(ex);
//     //     }
//     // }
//
//     @Override
//     public DamengQueryResults<DamengExplainResult> explainSql(String dbName, String sql) {
//         DamengQueryResults<DamengExplainResult> results = new DamengQueryResults<>();
//         Connection connection = null;
//         try {
//             DBUtil.printSql(sql);
//             DBSqlParser parser = DBSqlParser.getParser(sql, this.dialect());
//             List<String> list = parser.parseSql();
//             connection = this.connection(dbName);
//             connection.setAutoCommit(false);
//             Statement statement = connection.createStatement();
//             for (String execSql : list) {
//                 DamengExplainResult result = new DamengExplainResult();
//                 try {
//                     execSql = "EXPLAIN " + execSql.stripLeading();
//                     result.sql(execSql);
//                     long startTime = System.nanoTime();
//                     ResultSet resultSet = statement.executeQuery(execSql);
//                     result.parseResult(resultSet, connection);
//                     result.used(System.nanoTime() - startTime);
//                     DBUtil.close(resultSet);
//                     result.success(true);
//                 } catch (SQLException ex) {
//                     result.msg(ex.toString());
//                 }
//                 results.addResult(result);
//             }
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             DBUtil.rollback(connection);
//             results.parseError(ex);
//         }
//         return results;
//     }
//
//     @Override
//     public DamengExecuteResult executeSingleSql(String dbName, String sql) {
//         Connection connection = null;
//         DamengExecuteResult result = new DamengExecuteResult();
//         result.sql(sql);
//         try {
//             DBUtil.printSql(sql);
//             DBSqlParser parser = DBSqlParser.getParser(sql, this.dialect());
//             String execSql = parser.parseSingleSql();
//             connection = this.connection(dbName);
//             Statement statement = connection.createStatement();
//             try {
//                 long startTime = System.nanoTime();
//                 boolean isQuery = statement.execute(execSql);
//                 if (isQuery) {
//                     ResultSet resultSet = statement.getResultSet();
//                     if (parser.isSingle() && parser.isSelect()) {
//                         result.parseResult(resultSet, connection, false);
//                         result.fullColumn(parser.isFullColumn());
//                     } else {
//                         result.parseResult(resultSet, connection);
//                     }
//                     DBUtil.close(resultSet);
//                     result.success(true);
//                 } else {
//                     connection.setAutoCommit(false);
//                     int updateCount = statement.getUpdateCount();
//                     connection.commit();
//                     result.updateCount(updateCount);
//                     result.success(true);
//                 }
//                 long endTime = System.nanoTime();
//                 result.used(endTime - startTime);
//             } catch (SQLException ex) {
//                 result.msg(ex.getMessage());
//             }
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             DBUtil.rollback(connection);
//         }
//         return result;
//     }
//
//     @Override
//     public void executeSqlSimple(String dbName, String sql) {
//         Connection connection = null;
//         try {
//             DBUtil.printSql(sql);
//             connection = this.connection(dbName);
//             connection.setAutoCommit(false);
//             Statement statement = connection.createStatement();
//             statement.execute(sql);
//             connection.commit();
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             DBUtil.rollback(connection);
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public int insertBatch(String dbName, List<String> sqlList, boolean parallel) {
//         Connection connection = null;
//         int result = 0;
//         try {
//             connection = parallel ? this.newConnection(dbName) : this.connection(dbName);
//             connection.setAutoCommit(false);
//             Statement statement = connection.createStatement();
//             for (String sql : sqlList) {
//                 statement.addBatch(sql);
//             }
//             int[] results = statement.executeBatch();
//             connection.commit();
//             DBUtil.close(statement);
//             if (parallel) {
//                 DBUtil.close(connection);
//             }
//             for (int i : results) {
//                 result += i;
//             }
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             DBUtil.rollback(connection);
//             throw new DBException(ex);
//
//         }
//         return result;
//     }
//
//     @Override
//     public DBDialect dialect() {
//         return DBDialect.MYSQL;
//     }
//
//     @Override
//     public List<DamengFunction> functions(String dbName) {
//         try {
//             List<DamengFunction> list = new ArrayList<>();
//             String sql = """
//                     SELECT
//                         `ROUTINE_NAME`,
//                         `SECURITY_TYPE`,
//                         `SQL_DATA_ACCESS`,
//                         `ROUTINE_DEFINITION`
//                     FROM
//                         `INFORMATION_SCHEMA`.`ROUTINES`
//                     WHERE
//                         `OWNER` = ?
//                     AND
//                         `ROUTINE_TYPE` = 'FUNCTION'
//                     """;
//             DBUtil.printSql(sql);
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             // 执行SQL查询并获取结果集
//             ResultSet resultSet = statement.executeQuery();
//             // 打印元数据
//             DBUtil.printMetaData(resultSet);
//             // 遍历结果集
//             while (resultSet.next()) {
//                 DamengFunction function = new DamengFunction();
//                 String name = resultSet.getString("ROUTINE_NAME");
//                 List<DamengRoutineParam> params = DamengHelper.listFunctionParam(this.connection(), dbName, name);
//                 String securityType = resultSet.getString("SECURITY_TYPE");
//                 String definition = resultSet.getString("ROUTINE_DEFINITION");
//                 String sqlDataAccess = resultSet.getString("SQL_DATA_ACCESS");
//                 String createDefinition = DamengHelper.getFunctionDefinition(this.connection(dbName), name);
//                 function.setName(name);
//                 function.setDbName(dbName);
//                 function.setParams(params);
//                 function.setDefinition(definition);
//                 function.setSecurityType(securityType);
//                 function.setCharacteristic(sqlDataAccess);
//                 function.setCreateDefinition(createDefinition);
//                 list.add(function);
//             }
//             // 关闭连接和释放资源
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return list;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     // @Override
//     // public int functionSize(String dbName) {
//     //     int count = 0;
//     //     try {
//     //         String sql = "SELECT COUNT(*) FROM `INFORMATION_SCHEMA`.`ROUTINES` WHERE `OWNER` = ? AND `ROUTINE_TYPE` = 'FUNCTION'";
//     //         DBUtil.printSql(sql);
//     //         PreparedStatement statement = this.connection().prepareStatement(sql);
//     //         statement.setString(1, dbName);
//     //         // 执行SQL查询并获取结果集
//     //         ResultSet resultSet = statement.executeQuery();
//     //         // 打印元数据
//     //         DBUtil.printMetaData(resultSet);
//     //         // 遍历结果集
//     //         while (resultSet.next()) {
//     //             count = resultSet.getInt(1);
//     //         }
//     //         // 关闭连接和释放资源
//     //         DBUtil.close(resultSet);
//     //         DBUtil.close(statement);
//     //     } catch (Exception ex) {
//     //         throw new DBException(ex);
//     //     }
//     //     return count;
//     // }
//
//     @Override
//     public List<DamengProcedure> procedures(String dbName) {
//         try {
//             List<DamengProcedure> list = new ArrayList<>();
//             String sql = """
//                     SELECT
//                         `ROUTINE_NAME`,
//                         `SECURITY_TYPE`,
//                         `SQL_DATA_ACCESS`,
//                         `ROUTINE_DEFINITION`
//                     FROM
//                         `INFORMATION_SCHEMA`.`ROUTINES`
//                     WHERE
//                         `OWNER` = ?
//                     AND
//                         `ROUTINE_TYPE` = 'PROCEDURE'
//                     """;
//             DBUtil.printSql(sql);
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             // 执行SQL查询并获取结果集
//             ResultSet resultSet = statement.executeQuery();
//             // 打印元数据
//             DBUtil.printMetaData(resultSet);
//             // 遍历结果集
//             while (resultSet.next()) {
//                 DamengProcedure procedure = new DamengProcedure();
//                 String name = resultSet.getString("ROUTINE_NAME");
//                 String createDefinition = this.showCreateProcedure(dbName, name);
//                 List<DamengRoutineParam> params = DamengHelper.listProcedureParam(this.connection(), dbName, name);
//                 String securityType = resultSet.getString("SECURITY_TYPE");
//                 String definition = resultSet.getString("ROUTINE_DEFINITION");
//                 String sqlDataAccess = resultSet.getString("SQL_DATA_ACCESS");
//                 procedure.setName(name);
//                 procedure.setDbName(dbName);
//                 procedure.setParams(params);
//                 procedure.setDefinition(definition);
//                 procedure.setSecurityType(securityType);
//                 procedure.setCharacteristic(sqlDataAccess);
//                 procedure.setCreateDefinition(createDefinition);
//                 list.add(procedure);
//             }
//             // 关闭连接和释放资源
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return list;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public DamengProcedure selectProcedure(String dbName, String produceName) {
//         try {
//             String sql = """
//                     SELECT
//                         `SECURITY_TYPE`,
//                         `SQL_DATA_ACCESS`,
//                         `ROUTINE_DEFINITION`
//                     FROM
//                         `INFORMATION_SCHEMA`.`ROUTINES`
//                     WHERE
//                         `OWNER` = ?
//                     AND
//                         `ROUTINE_NAME` = ?
//                     AND
//                         `ROUTINE_TYPE` = 'PROCEDURE'
//                     """;
//             DBUtil.printSql(sql);
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             statement.setString(2, produceName);
//             // 执行SQL查询并获取结果集
//             ResultSet resultSet = statement.executeQuery();
//             // 打印元数据
//             DBUtil.printMetaData(resultSet);
//             DamengProcedure procedure = new DamengProcedure();
//             procedure.setDbName(dbName);
//             procedure.setName(produceName);
//             // 遍历结果集
//             while (resultSet.next()) {
//                 String createDefinition = this.showCreateProcedure(dbName, produceName);
//                 List<DamengRoutineParam> params = DamengHelper.listProcedureParam(this.connection(), dbName, produceName);
//                 String securityType = resultSet.getString("SECURITY_TYPE");
//                 String definition = resultSet.getString("ROUTINE_DEFINITION");
//                 String sqlDataAccess = resultSet.getString("SQL_DATA_ACCESS");
//                 procedure.setDbName(dbName);
//                 procedure.setParams(params);
//                 procedure.setDefinition(definition);
//                 procedure.setSecurityType(securityType);
//                 procedure.setCharacteristic(sqlDataAccess);
//                 procedure.setCreateDefinition(createDefinition);
//             }
//             // 关闭连接和释放资源
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return procedure;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     // @Override
//     // public int procedureSize(String dbName) {
//     //     int count = 0;
//     //     try {
//     //         String sql = "SELECT COUNT(*) FROM `INFORMATION_SCHEMA`.`ROUTINES` WHERE `OWNER` = ? AND `ROUTINE_TYPE` = 'PROCEDURE'";
//     //         DBUtil.printSql(sql);
//     //         PreparedStatement statement = this.connection().prepareStatement(sql);
//     //         statement.setString(1, dbName);
//     //         // 执行SQL查询并获取结果集
//     //         ResultSet resultSet = statement.executeQuery();
//     //         // 打印元数据
//     //         DBUtil.printMetaData(resultSet);
//     //         // 遍历结果集
//     //         while (resultSet.next()) {
//     //             count = resultSet.getInt(1);
//     //         }
//     //         // 关闭连接和释放资源
//     //         DBUtil.close(resultSet);
//     //         DBUtil.close(statement);
//     //     } catch (Exception ex) {
//     //         ex.printStackTrace();
//     //         throw new DBException(ex);
//     //     }
//     //     return count;
//     // }
//
//     @Override
//     public void dropProcedure(String dbName, DamengProcedure routine) {
//         try {
//             String sql = "DROP PROCEDURE IF EXISTS " + DBUtil.wrap(dbName, routine.getName());
//             DBUtil.printSql(sql);
//             Statement statement = this.connection(dbName).createStatement();
//             statement.executeUpdate(sql);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public void createProcedure(String dbName, DamengProcedure procedure) {
//         try {
//             String sql = DamengProcedureSqlGenerator.INSTANCE.generate(procedure);
//             DBUtil.printSql(sql);
//             Statement statement = this.connection(dbName).createStatement();
//             statement.executeUpdate(sql);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public void alertProcedure(String dbName, DamengProcedure procedure) {
//         try {
//             String sql = "DROP PROCEDURE IF EXISTS " + DBUtil.wrap(dbName, procedure.getName());
//             DBUtil.printSql(sql);
//             Statement statement = this.connection(dbName).createStatement();
//             statement.executeUpdate(sql);
//             sql = DamengProcedureSqlGenerator.INSTANCE.generate(procedure);
//             DBUtil.printSql(sql);
//             Statement statement1 = this.connection(dbName).createStatement();
//             statement1.executeUpdate(sql);
//             DBUtil.close(statement1);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public void dropFunction(String dbName, DamengFunction function) {
//         try {
//             String sql = "DROP function IF EXISTS " + DBUtil.wrap(dbName, function.getName());
//             DBUtil.printSql(sql);
//             Statement statement = this.connection(dbName).createStatement();
//             statement.executeUpdate(sql);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public DamengFunction selectFunction(String dbName, String functionName) {
//         try {
//             String sql = """
//                     SELECT
//                         `SECURITY_TYPE`,
//                         `SQL_DATA_ACCESS`,
//                         `ROUTINE_DEFINITION`
//                     FROM
//                         `INFORMATION_SCHEMA`.`ROUTINES`
//                     WHERE
//                         `OWNER` = ?
//                     AND
//                         `ROUTINE_NAME` = ?
//                     AND
//                         `ROUTINE_TYPE` = 'FUNCTION'
//                     """;
//             DBUtil.printSql(sql);
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             statement.setString(2, functionName);
//             // 执行SQL查询并获取结果集
//             ResultSet resultSet = statement.executeQuery();
//             // 打印元数据
//             DBUtil.printMetaData(resultSet);
//             DamengFunction function = new DamengFunction();
//             function.setDbName(dbName);
//             function.setName(functionName);
//             // 遍历结果集
//             while (resultSet.next()) {
//                 String securityType = resultSet.getString("SECURITY_TYPE");
//                 String definition = resultSet.getString("ROUTINE_DEFINITION");
//                 String sqlDataAccess = resultSet.getString("SQL_DATA_ACCESS");
//                 List<DamengRoutineParam> params = DamengHelper.listFunctionParam(this.connection(), dbName, functionName);
//                 String createDefinition = this.showCreateFunction(dbName, functionName);
//                 function.setDbName(dbName);
//                 function.setParams(params);
//                 function.setDefinition(definition);
//                 function.setSecurityType(securityType);
//                 function.setCharacteristic(sqlDataAccess);
//                 function.setCreateDefinition(createDefinition);
//             }
//             // 关闭连接和释放资源
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return function;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public void createFunction(String dbName, DamengFunction function) {
//         try {
//             String sql = DamengFunctionSqlGenerator.INSTANCE.generate(function);
//             DBUtil.printSql(sql);
//             Statement statement = this.connection(dbName).createStatement();
//             statement.executeUpdate(sql);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public void alertFunction(String dbName, DamengFunction function) {
//         try {
//             String sql = "DROP FUNCTION IF EXISTS " + DBUtil.wrap(dbName, function.getName());
//             DBUtil.printSql(sql);
//             Statement statement = this.connection(dbName).createStatement();
//             statement.executeUpdate(sql);
//             sql = DamengFunctionSqlGenerator.INSTANCE.generate(function);
//             DBUtil.printSql(sql);
//             Statement statement1 = this.connection(dbName).createStatement();
//             statement1.executeUpdate(sql);
//             DBUtil.close(statement1);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public List<DamengTrigger> triggers(String dbName) {
//         try {
//             String sql = """
//                         SELECT
//                             TRIGGER_NAME,ACTION_STATEMENT,ACTION_TIMING,EVENT_MANIPULATION,EVENT_OBJECT_TABLE
//                         FROM
//                             INFORMATION_SCHEMA.TRIGGERS
//                         WHERE
//                             OWNER = ?
//                     """;
//             DBUtil.printSql(sql);
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             ResultSet resultSet = statement.executeQuery();
//             List<DamengTrigger> list = new ArrayList<>();
//             while (resultSet.next()) {
//                 DamengTrigger trigger = new DamengTrigger();
//                 String name = resultSet.getString("TRIGGER_NAME");
//                 String timing = resultSet.getString("ACTION_TIMING");
//                 String tableName = resultSet.getString("EVENT_OBJECT_TABLE");
//                 String manipulation = resultSet.getString("EVENT_MANIPULATION");
//                 String actionStatement = resultSet.getString("ACTION_STATEMENT");
//                 trigger.setName(name);
//                 trigger.setTableName(tableName);
//                 trigger.setDefinition(actionStatement);
//                 trigger.setPolicy(timing, manipulation);
//                 list.add(trigger);
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return list;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     public List<DamengTrigger> triggers(String dbName, String tableName) {
//         try {
//             String sql = """
//                         SELECT
//                             TRIGGER_NAME,ACTION_STATEMENT,ACTION_TIMING,EVENT_MANIPULATION
//                         FROM
//                             INFORMATION_SCHEMA.TRIGGERS
//                         WHERE
//                             OWNER = ?
//                         AND
//                             EVENT_OBJECT_TABLE = ?
//                     """;
//             DBUtil.printSql(sql);
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             statement.setString(2, tableName);
//             ResultSet resultSet = statement.executeQuery();
//             List<DamengTrigger> list = new ArrayList<>();
//             while (resultSet.next()) {
//                 DamengTrigger trigger = new DamengTrigger();
//                 String name = resultSet.getString("TRIGGER_NAME");
//                 String timing = resultSet.getString("ACTION_TIMING");
//                 String manipulation = resultSet.getString("EVENT_MANIPULATION");
//                 String actionStatement = resultSet.getString("ACTION_STATEMENT");
//                 trigger.setName(name);
//                 trigger.setDefinition(actionStatement);
//                 trigger.setPolicy(timing, manipulation);
//                 list.add(trigger);
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return list;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public DamengRecord selectRecord(String dbName, String tableName, DamengRecordPrimaryKey primaryKey) {
//         try {
//             Connection connection = this.connection(dbName);
//             String sql = "SELECT * FROM " + DBUtil.wrap(dbName, tableName) + " WHERE " + DBUtil.wrap(primaryKey.getColumnName()) + " = ?";
//             // String sql = "SELECT * FROM " + DBUtil.wrap(dbName, tableName) + " WHERE " + DBUtil.wrap(primaryKey.getColumnName()) + " = ? LIMIT 1";
//             DBUtil.printSql(sql);
//             PreparedStatement statement = connection.prepareStatement(sql);
//             statement.setObject(1, primaryKey.data());
//             ResultSet resultSet = statement.executeQuery();
//             DBUtil.printMetaData(resultSet);
//             DamengRecord record = new DamengRecord();
//             DamengColumns columns = DamengHelper.parseColumns(resultSet);
//             while (resultSet.next()) {
//                 for (DamengColumn column : columns) {
//                     Object data = resultSet.getObject(column.getName());
//                     // 获取几何值
//                     if (column.supportGeometry()) {
//                         data = DamengHelper.getGeometryString(connection, data);
//                     }
//                     record.putValue(column, data);
//                 }
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return record;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public String selectVersion() {
//         if (this.hasProperty("version")) {
//             return this.getProperty("version");
//         }
//         String version = "";
//         try {
//             Connection conn = this.connection();
//             Statement stmt = conn.createStatement();
//             ResultSet resultSet = stmt.executeQuery("SELECT VERSION()");
//             if (resultSet.next()) {
//                 version = resultSet.getString(1);
//             }
//             this.putProperty("version", version);
//             DBUtil.close(resultSet);
//             DBUtil.close(stmt);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         return version;
//     }
//
//     @Override
//     public String selectClientCharacter() {
//         String character = "";
//         try {
//             Connection conn = this.connection();
//             Statement stmt = conn.createStatement();
//             ResultSet resultSet = stmt.executeQuery("SHOW VARIABLES LIKE 'character_set_%'");
//             while (resultSet.next()) {
//                 String name = resultSet.getString(1);
//                 if ("character_set_client".equalsIgnoreCase(name)) {
//                     character = resultSet.getString(2);
//                     break;
//                 }
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(stmt);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         return character;
//     }
//
//     @Override
//     public void dropEvent(String dbName, DamengEvent event) {
//         try {
//             String sql = "DROP EVENT " + DBUtil.wrap(event.getDbName(), event.getName());
//             DBUtil.printSql(sql);
//             Statement statement = this.connection(dbName).createStatement();
//             statement.executeUpdate(sql);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public void createEvent(String dbName, DamengEvent event) {
//         try {
//             String sql = EventCreateSqlGenerator.generate(this.dialect(), event);
//             DBUtil.printSql(sql);
//             Statement statement = this.connection(dbName).createStatement();
//             statement.executeUpdate(sql);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public void alertEvent(String dbName, DamengEvent event) {
//         try {
//             String sql = EventAlertSqlGenerator.generate(this.dialect(), event);
//             DBUtil.printSql(sql);
//             Statement statement = this.connection(dbName).createStatement();
//             statement.executeUpdate(sql);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public DamengEvent selectEvent(String dbName, String eventName) {
//         DamengEvent event = new DamengEvent();
//         try {
//             String sql = "SELECT * FROM `INFORMATION_SCHEMA`.`EVENTS` WHERE `OWNER` = ? AND `EVENT_NAME` = ?";
//             DBUtil.printSql(sql);
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             statement.setString(2, eventName);
//             ResultSet resultSet = statement.executeQuery();
//             DBUtil.printMetaData(resultSet);
//             while (resultSet.next()) {
//                 Date ends = resultSet.getDate("ENDS");
//                 Date starts = resultSet.getDate("STARTS");
//                 String status = resultSet.getString("STATUS");
//                 String type = resultSet.getString("EVENT_TYPE");
//                 Date executeAt = resultSet.getDate("EXECUTE_AT");
//                 String comment = resultSet.getString("EVENT_COMMENT");
//                 int intervalValue = resultSet.getInt("INTERVAL_VALUE");
//                 String onCompletion = resultSet.getString("ON_COMPLETION");
//                 String definition = resultSet.getString("EVENT_DEFINITION");
//                 String intervalField = resultSet.getString("INTERVAL_FIELD");
//                 String createDefinition = this.showCreateEvent(dbName, eventName);
//                 event.setName(eventName);
//                 event.setType(type);
//                 event.setEnds(ends);
//                 event.setStarts(starts);
//                 event.setDbName(dbName);
//                 event.setStatus(status);
//                 event.setComment(comment);
//                 event.setDefinition(definition);
//                 event.setOnCompletion(status);
//                 event.setExecuteAt(executeAt);
//                 event.setOnCompletion(onCompletion);
//                 event.setIntervalValue(intervalValue);
//                 event.setIntervalField(intervalField);
//                 event.setCreateDefinition(createDefinition);
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//         return event;
//     }
//
//     @Override
//     public Integer eventSize(String dbName) {
//         int count = 0;
//         try {
//             String sql = "SELECT COUNT(*) FROM `INFORMATION_SCHEMA`.`EVENTS` WHERE `OWNER` = ?";
//             DBUtil.printSql(sql);
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             ResultSet resultSet = statement.executeQuery();
//             DBUtil.printMetaData(resultSet);
//             while (resultSet.next()) {
//                 count = resultSet.getInt(1);
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//         return count;
//     }
//
//     @Override
//     public List<DamengEvent> events(String dbName) {
//         List<DamengEvent> list = new ArrayList<>();
//         try {
//             String sql = "SELECT * FROM `INFORMATION_SCHEMA`.`EVENTS` WHERE `OWNER` = ?";
//             DBUtil.printSql(sql);
//             PreparedStatement statement = this.connection().prepareStatement(sql);
//             statement.setString(1, dbName);
//             ResultSet resultSet = statement.executeQuery();
//             DBUtil.printMetaData(resultSet);
//             while (resultSet.next()) {
//                 DamengEvent event = new DamengEvent();
//                 Date ends = resultSet.getDate("ENDS");
//                 Date starts = resultSet.getDate("STARTS");
//                 String status = resultSet.getString("STATUS");
//                 String name = resultSet.getString("EVENT_NAME");
//                 String type = resultSet.getString("EVENT_TYPE");
//                 Date executeAt = resultSet.getDate("EXECUTE_AT");
//                 String comment = resultSet.getString("EVENT_COMMENT");
//                 int intervalValue = resultSet.getInt("INTERVAL_VALUE");
//                 String onCompletion = resultSet.getString("ON_COMPLETION");
//                 String definition = resultSet.getString("EVENT_DEFINITION");
//                 String intervalField = resultSet.getString("INTERVAL_FIELD");
//                 String createDefinition = this.showCreateEvent(dbName, name);
//                 event.setName(name);
//                 event.setType(type);
//                 event.setEnds(ends);
//                 event.setStarts(starts);
//                 event.setDbName(dbName);
//                 event.setStatus(status);
//                 event.setComment(comment);
//                 event.setDefinition(definition);
//                 event.setOnCompletion(status);
//                 event.setExecuteAt(executeAt);
//                 event.setOnCompletion(onCompletion);
//                 event.setIntervalValue(intervalValue);
//                 event.setIntervalField(intervalField);
//                 event.setCreateDefinition(createDefinition);
//                 list.add(event);
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//         return list;
//     }
//
//     @Override
//     public boolean isSupportFeature(DBFeature feature) {
//         try {
//             if (feature == DBFeature.EVENT) {
//                 return true;
//             }
//             // 检查约束
//             if (feature == DBFeature.CHECK) {
//                 // 最低支持版本8.0.16
//                 String version = this.selectVersion();
//                 String[] arr = version.split("\\.");
//                 if (Integer.parseInt(arr[0]) < 8) {
//                     return false;
//                 }
//                 return Integer.parseInt(arr[2]) >= 16;
//             }
//         } catch (Exception ex) {
//             ex.printStackTrace();
//         }
//         return false;
//     }
//
//     @Override
//     public List<DamengRecord> selectRecords(DamengSelectRecordParam param) {
//         try {
//             Connection connection = this.connection(param.dbName(), param.schema());
//             StringBuilder builder = new StringBuilder("SELECT * FROM ");
//             builder.append(DBUtil.wrap(param.dbName(), param.tableName(), this.dialect()));
//             String filterCondition = DamengConditionUtil.buildCondition(param.filters());
//             if (StrUtil.isNotBlank(filterCondition)) {
//                 builder.append(" WHERE ").append(filterCondition);
//             }
//             if (param.hasPageControl()) {
//                 builder.append(" LIMIT ")
//                         .append(param.start())
//                         .append(",")
//                         .append(param.limit());
//             }
//             String sql = builder.toString();
//             DBUtil.printSql(sql);
//             Statement statement = connection.createStatement();
//             ResultSet resultSet = statement.executeQuery(sql);
//             DBUtil.printMetaData(resultSet);
//             List<DamengRecord> records = new ArrayList<>();
//             List<DamengColumn> columns;
//             if (param.columns() != null) {
//                 columns = param.columns();
//             } else {
//                 columns = DamengHelper.parseColumns(resultSet);
//             }
//             while (resultSet.next()) {
//                 DamengRecord record = new DamengRecord(param.readonly());
//                 for (DamengColumn column : columns) {
//                     Object data = resultSet.getObject(column.getName());
//                     // 获取几何值
//                     if (column.supportGeometry()) {
//                         data = DamengHelper.getGeometryString(connection, data);
//                     }
//                     record.putValue(column, data);
//                 }
//                 records.add(record);
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return records;
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public long selectRecordCount(DamengSelectRecordParam param) {
//         long count = 0;
//         try {
//             Connection connection = this.connection(param.dbName(), param.schema());
//             StringBuilder builder = new StringBuilder("SELECT COUNT(*) FROM");
//             builder.append(DBUtil.wrap(param.schema(), param.tableName(), this.dialect()));
//             String filterCondition = DamengConditionUtil.buildCondition(param.filters());
//             if (StrUtil.isNotBlank(filterCondition)) {
//                 builder.append(" WHERE ").append(filterCondition);
//             }
//             String sql = builder.toString();
//             DBUtil.printSql(sql);
//             Statement statement = connection.createStatement();
//             ResultSet resultSet = statement.executeQuery(sql);
//             if (resultSet.next()) {
//                 count = resultSet.getLong(1);
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//         } catch (Exception ex) {
//             throw new DBException(ex);
//         }
//         return count;
//     }
//
//     @Override
//     public int insertRecord(DamengInsertRecordParam param) {
//         if (param == null || param.record() == null) {
//             return 0;
//         }
//         try {
//             StringBuilder builder = new StringBuilder();
//             builder.append("INSERT INTO ")
//                     .append(DBUtil.wrap(param.schema(), param.tableName(), this.dialect()))
//                     .append("(");
//             for (String column : param.record().columns()) {
//                 builder.append(DBUtil.wrap(column, this.dialect())).append(",");
//             }
//             builder.append(")");
//             builder.append(" VALUES(");
//             for (String column : param.record().columns()) {
//                 if (param.record().isTypeGeometry(column)) {
//                     builder.append("ST_GeomFromText(?),");
//                 } else {
//                     builder.append("?,");
//                 }
//             }
//             builder.append(")");
//             String sql = builder.toString();
//             sql = sql.replaceAll(",\\)", ")");
//             DBUtil.printSql(sql);
//             Connection connection = this.connection(param.dbName(), param.schema());
//             PreparedStatement statement = connection.prepareStatement(sql);
//             int index = 1;
//             for (String colName : param.record().columns()) {
//                 DBUtil.setVal(statement, param.record().value(colName), index++);
//             }
//             int count = statement.executeUpdate();
//             DamengRecordPrimaryKey primaryKey = param.primaryKey();
//             // 处理自动递增值
//             if (primaryKey != null && primaryKey.shouldReturnData()) {
//                 primaryKey.setReturnData(DamengHelper.lastInsertId(connection));
//             }
//             DBUtil.close(statement);
//             return count;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public int deleteRecord(DamengDeleteRecordParam param) {
//         try {
//             int updateCount;
//             String dbName = param.dbName();
//             String schema = param.schema();
//             String tableName = param.tableName();
//             Connection connection = this.connection(dbName, schema);
//             StringBuilder builder = new StringBuilder();
//             builder.append("DELETE FROM ")
//                     .append(DBUtil.wrap(schema, tableName, this.dialect()))
//                     .append(" WHERE ");
//             if (param.primaryKey() == null) {
//                 DamengRecordData recordData = param.record();
//                 boolean first = true;
//                 for (String colName : recordData.columns()) {
//                     if (first) {
//                         first = false;
//                     } else {
//                         builder.append(" AND ");
//                     }
//                     builder.append(DBUtil.wrap(colName, this.dialect()))
//                             .append(" = ?");
//                 }
//                 String sql = builder.toString();
//                 DBUtil.printSql(sql);
//                 PreparedStatement statement = connection.prepareStatement(sql);
//                 int index = 1;
//                 // 设置参数
//                 for (String colName : param.record().columns()) {
//                     DBUtil.setVal(statement, param.record().value(colName), index++);
//                 }
//                 updateCount = DBUtil.executeUpdate(statement);
//             } else {
//                 DamengRecordPrimaryKey primaryKey = param.primaryKey();
//                 builder.append(DBUtil.wrap(primaryKey.getColumnName(), this.dialect()))
//                         .append(" = ?");
//                 String sql = builder.toString();
//                 DBUtil.printSql(sql);
//                 PreparedStatement statement = connection.prepareStatement(sql);
//                 DBUtil.setVal(statement, primaryKey.originalData(), 1);
//                 updateCount = DBUtil.executeUpdate(statement);
//                 DBUtil.close(statement);
//             }
//             return updateCount;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     @Override
//     public int updateRecord(DamengUpdateRecordParam param) {
//         try {
//             int updateCount;
//             String dbName = param.dbName();
//             String schema = param.schema();
//             String tableName = param.tableName();
//             DamengRecordData recordData = param.updateRecord();
//             StringBuilder builder = new StringBuilder();
//             builder.append("UPDATE ")
//                     .append(DBUtil.wrap(schema, tableName, this.dialect()))
//                     .append(" SET ");
//             for (String column : recordData.columns()) {
//                 if (recordData.isTypeGeometry(column)) {
//                     builder.append(DBUtil.wrap(column, this.dialect())).append(" = ST_GeomFromText(?),");
//                 } else {
//                     builder.append(DBUtil.wrap(column, this.dialect())).append(" = ?,");
//                 }
//             }
//             builder.deleteCharAt(builder.length() - 1);
//             builder.append(" WHERE ");
//             Connection connection = this.connection(dbName, schema);
//             if (param.primaryKey() == null) {
//                 DamengRecordData originalRecordData = param.record();
//                 // 参数
//                 boolean first = true;
//                 for (String column : originalRecordData.columns()) {
//                     if (first) {
//                         first = false;
//                     } else {
//                         builder.append(" AND ");
//                     }
//                     builder.append(DBUtil.wrap(column, this.dialect())).append(" = ?");
//                 }
//                 int index = 1;
//                 String sql = builder.toString();
//                 DBUtil.printSql(sql);
//                 PreparedStatement statement = connection.prepareStatement(sql);
//                 // 设置值
//                 for (String colName : recordData.columns()) {
//                     DBUtil.setVal(statement, recordData.value(colName), index++);
//                 }
//                 // 设置参数
//                 for (String colName : originalRecordData.columns()) {
//                     DBUtil.setVal(statement, originalRecordData.value(colName), index++);
//                 }
//                 updateCount = DBUtil.executeUpdate(statement);
//                 DBUtil.close(statement);
//             } else {
//                 DamengRecordPrimaryKey primaryKey = param.primaryKey();
//                 builder.append(" WHERE ").append(DBUtil.wrap(primaryKey.getColumnName(), this.dialect())).append(" = ?");
//                 String sql = builder.toString();
//                 DBUtil.printInfo(sql, recordData);
//                 PreparedStatement statement = connection.prepareStatement(sql);
//                 int index = 1;
//                 // 设置值
//                 for (String colName : recordData.columns()) {
//                     DBUtil.setVal(statement, recordData.value(colName), index++);
//                 }
//                 // 设置参数
//                 DBUtil.setVal(statement, primaryKey.originalData(), index);
//                 updateCount = DBUtil.executeUpdate(statement);
//                 DBUtil.close(statement);
//             }
//             return updateCount;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     public String showCreateTable(String dbName, String tableName) {
//         try {
//             Connection connection = this.connection(dbName);
//             String sql = "SHOW CREATE TABLE " + DBUtil.wrap(tableName);
//             Statement stmt = connection.createStatement();
//             ResultSet resultSet = stmt.executeQuery(sql);
//             String createDefinition = "";
//             if (resultSet.next()) {
//                 createDefinition = resultSet.getString(2);
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(stmt);
//             return createDefinition;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     public String showCreateView(String dbName, String viewName) {
//         try {
//             Connection connection = this.connection(dbName);
//             String sql = "SHOW CREATE VIEW " + DBUtil.wrap(viewName);
//             Statement statement = connection.createStatement();
//             ResultSet resultSet = statement.executeQuery(sql);
//             String createDefinition = "";
//             if (resultSet.next()) {
//                 createDefinition = resultSet.getString("Create View");
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return createDefinition;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     public String showCreateFunction(String dbName, String functionName) {
//         try {
//             Connection connection = this.connection(dbName);
//             String sql = "SHOW CREATE FUNCTION " + DBUtil.wrap(functionName);
//             Statement statement = connection.createStatement();
//             ResultSet resultSet = statement.executeQuery(sql);
//             String createDefinition = "";
//             if (resultSet.next()) {
//                 createDefinition = resultSet.getString("Create Function");
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return createDefinition;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     public String showCreateProcedure(String dbName, String procedureName) {
//         try {
//             Connection connection = this.connection(dbName);
//             String sql = "SHOW CREATE PROCEDURE " + DBUtil.wrap(procedureName);
//             Statement statement = connection.createStatement();
//             ResultSet resultSet = statement.executeQuery(sql);
//             String createDefinition = "";
//             if (resultSet.next()) {
//                 createDefinition = resultSet.getString("Create Procedure");
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return createDefinition;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     public String showCreateTrigger(String dbName, String triggerName) {
//         try {
//             Connection connection = this.connection(dbName);
//             String sql = "SHOW CREATE TRIGGER " + DBUtil.wrap(triggerName);
//             Statement statement = connection.createStatement();
//             // 执行SQL查询并获取结果集
//             ResultSet resultSet = statement.executeQuery(sql);
//             String createDefinition = "";
//             if (resultSet.next()) {
//                 createDefinition = resultSet.getString("Sql Original Statement");
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return createDefinition;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
//
//     public String showCreateEvent(String dbName, String eventName) {
//         try {
//             Connection connection = this.connection(dbName);
//             String sql = "SHOW CREATE EVENT " + DBUtil.wrap(eventName);
//             Statement statement = connection.createStatement();
//             ResultSet resultSet = statement.executeQuery(sql);
//             String createDefinition = "";
//             if (resultSet.next()) {
//                 createDefinition = resultSet.getString("Create Event");
//             }
//             DBUtil.close(resultSet);
//             DBUtil.close(statement);
//             return createDefinition;
//         } catch (Exception ex) {
//             ex.printStackTrace();
//             throw new DBException(ex);
//         }
//     }
// }
