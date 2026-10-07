# 达梦核心类审查（客户端/连接/工具/模式）

范围：`cn.oyzh.easydameng.dameng` 顶层及 `schema` 子包中的正式类（4 个）：`ShellDamengClient`、`DamengConnManager`、`DamengHelper`、`DamengSchema`。
本文件是达梦方言实现的入口：`ShellDamengClient` 封装对达梦的元数据查询与 DDL/DML 执行，`DamengConnManager` 负责 JDBC 连接，`DamengHelper` 提供结果集解析与定义修正工具。

跳过（整文件被注释，死代码）：`database/DamengDatabase`。

---

## ShellDamengClient

- 职责：达梦数据库客户端封装（实现 fx-base 的 `DBClient`），对外提供表/视图/字段/记录/索引/约束/函数/过程等的查询与增删改、DDL 执行、SQL 执行与执行计划。
- 继承：`implements cn.oyzh.fx.db.DBClient`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dbConnect` | `DamengConnect` | 达梦连接信息（`protected final`） |
  | `connManager` | `DBConnManager` | 数据库连接管理器（`protected`） |
  | `properties` | `Map<String, Object>` | 属性缓存（如 version/product） |
  | `state` | `ReadOnlyObjectWrapper<DBConnState>` | 连接状态（`final`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `ShellDamengClient(DamengConnect dbInfo)` | 构造客户端 | 保存 `dbInfo`；为 `stateProperty()` 注册监听，`CLOSED`/`CONNECTED` 时分别触发 `DamengEventUtil.connectionClosed/connectionConnected` |
  | `DBConnManager getConnManager()` | 取连接管理器 | 懒建 `new DamengConnManager()` |
  | `Map<String,Object> getProperties()` | 取属性表 | 懒建 `HashMap` |
  | `DBConnState state()` / `ReadOnlyObjectProperty<DBConnState> stateProperty()` | 连接状态读写 | — |
  | `boolean isReadonly()` / `void throwReadonlyException()` | 只读判断/抛只读异常 | 只读时抛 `ReadonlyOperationException` |
  | `void start()` | 连接数据库 | 已在连接/连接中直接返回；`initClient()` 后置 `CONNECTING`，`connManager.connection().isValid(timeout)` 通过则 `CONNECTED`，否则 `close()` 并置 `FAILED` |
  | `void initClient()` | 初始化客户端 | 由 `dbConnect` 取 IP/端口构建 `DBConnConfig`，`new DamengConnManager()` 并 `setConfig` |
  | `void close()` | 关闭连接 | `IOUtil.close(connManager)` 后置 `CLOSED` |
  | `boolean isClosed()` / `isConnected()` / `isConnecting()` | 状态判断 | 基于 `state` |
  | `String connectName()` | 连接名称 | `dbConnect.getName()` |
  | `void addStateListener(ChangeListener<DBConnState>)` | 注册状态监听 | 转发到 `state` |
  | `int tableSize(String schema)` | 表数量 | `SELECT COUNT(*) FROM ALL_TABLES WHERE OWNER = ?` |
  | `int viewSize(String schema)` | 视图数量 | `SELECT COUNT(*) FROM ALL_VIEWS WHERE OWNER = ?` |
  | `int procedureSize(String schema)` | 过程数量 | `ALL_OBJECTS` 中 `OBJECT_TYPE='PROCEDURE'` 计数（`procedureConnection`） |
  | `int functionSize(String schema)` | 函数数量 | `ALL_OBJECTS` 中 `OBJECT_TYPE='FUNCTION'` 计数（`functionConnection`） |
  | `DBQueryResults<DamengExecuteResult> executeSql(String schema, String sql)` | 执行 SQL 脚本 | `DBSqlParser.getParser(sql, DAMENG).parseSql()` 拆分多语句，逐条 `statement.execute`；查询走 `DamengExecuteResult.parseResult`，否则记录 `updateCount`；记录耗时，异常写 `result.setMsg` |
  | `DBQueryResults<DamengExplainResult> explainSql(String schema, String sql)` | 执行计划 | 每条语句前加 `EXPLAIN FOR ` 后 `executeQuery` → `DamengExplainResult.parseResult` |
  | `DamengExecuteResult executeSingleSql(String schema, String sql)` | 执行单条 SQL | `parser.parseSingleSql()`；查询返回结果集，更新语句 `commit` 后记录影响行数 |
  | `void executeSqlSimple(String schema, String sql)` | 执行 SQL（不处理结果） | `setAutoCommit(false)` → `execute` → `commit`；异常回滚并抛 `DBException` |
  | `int insertBatch(String schema, List<String> sqlList)` / `insertBatch(schema, sqlList, parallel)` | 批量执行 | 逐条 `statement.addBatch`，`executeBatch` 后 `commit`；`parallel` 为真用 `connManager.newConnection` 并单独关闭，返回影响行数之和 |
  | `Long getGeneratedKeys(Statement)` | 获取自增主键 | 优先 `getGeneratedKeys()`，为空时回退 `SELECT @@IDENTITY FROM DUAL` |
  | `boolean isSupportFeature(DBFeature)` / `isSupportCheckFeature()` | 特性支持 | `EVENT`、`CHECK` 返回 true，其余 false |
  | `String selectVersion()` / `String selectProduct()` | 数据库版本/产品 | 查 `V$VERSION` 的 `BANNER`，结果缓存到 `properties` |
  | `List<DamengTable> selectTables(String schema)` / `selectTablesSimple(String schema)` / `selectTables(DamengSelectTableParam)` | 表列表（全量/简单/通用） | 简易重载构造 `DamengSelectTableParam` 后委托；主逻辑查 `ALL_TABLES` + `ALL_TAB_COMMENTS`，`isFull()` 时再 `showCreateTable` |
  | `DamengTable selectTable(String schema, String tableName)` / `selectTableSimple(...)` / `selectTable(DamengSelectTableParam)` | 单表（全量/简单/通用） | 查注释与表空间；`isFull()` 时 `showCreateTable` 填充 `createDefinition` |
  | `DamengColumns selectColumns(DamengSelectColumnParam)` | 字段列表 | 查 `ALL_TAB_COLUMNS` LEFT JOIN `SYS.SYSCOLUMNCOMMENTS`、`ALL_CONS_COLUMNS`；用 `DatabaseMetaData.getColumns` 判断 `IS_AUTOINCREMENT`；构造 `DamengColumn` 后 `sortOfPosition()` 返回 |
  | `List<DamengRecord> selectRecords(DamengSelectRecordParam)` | 记录列表 | `SELECT * FROM 库.表` + `DamengConditionUtil.buildCondition(filters)`；分页用 `OFFSET ... ROWS FETCH NEXT ... ROWS ONLY`；逐行 `DamengRecord.putValue` |
  | `long selectRecordCount(DamengSelectRecordParam)` | 记录数 | `SELECT COUNT(*)`（含过滤条件） |
  | `DamengRecord selectRecord(DamengSelectRecordParam)` | 单条记录 | 按主键 `WHERE 主键列 = ?` 查询并解析字段（当前仅返回空记录骨架） |
  | `int insertRecord(DamengInsertRecordParam)` | 插入记录 | 拼 `INSERT INTO 库.表(列...) VALUES(...)`，`PreparedStatement` 逐列 `DBUtil.setVal`；`primaryKey.shouldReturnData()` 时用 `getGeneratedKeys` 回填自增 |
  | `int deleteRecord(DamengDeleteRecordParam)` | 删除记录 | 有主键时按主键删除；否则按记录数据逐列生成 `col = ?` / `col IS NULL` |
  | `int updateRecord(DamengUpdateRecordParam)` | 更新记录 | `UPDATE 库.表 SET 列=? ... WHERE ...`；WHERE 用主键或原始记录数据 |
  | `String showCreateTable/showCreateView/showCreateFunction/showCreateProcedure/showCreateTrigger(...)` | 查询创建语句 | 分别查 `DBMS_METADATA.GET_DDL('TABLE'/'VIEW'/'FUNCTION'/'PROCEDURE'/'TRIGGER', ?)`（函数/过程用对应 connection） |
  | `List<String> tableSpaces()` | 表空间列表 | `SELECT TABLESPACE_NAME FROM DBA_TABLESPACES WHERE STATUS = 0` |
  | `List<DamengSchema> schemas()` | 模式列表 | `SELECT * FROM SYSOBJECTS WHERE TYPE$ = 'SCH'` |
  | `DamengSchema schema(String schema)` | 构造模式对象 | 直接 `new DamengSchema()` 并 `setName` |
  | `boolean existSchema(String schema)` | 模式是否存在 | `SYSOBJECTS` 中 `TYPE$='SCH' AND NAME = ?` 计数 |
  | `void createSchema(DamengSchema)` | 创建模式 | `CREATE SCHEMA 名` |
  | `boolean alterSchema(DamengSchema)` | 修改模式 | 达梦不支持，直接返回 true |
  | `boolean dropSchema(String schema)` | 删除模式 | `DROP SCHEMA 名 CASCADE` |
  | `List<DamengView> selectViews(String schema)` / `selectViewsSimple(...)` / `selectViews(DamengSelectViewParam)` | 视图列表 | 查 `ALL_VIEWS` + `ALL_TAB_COMMENTS`；`READ_ONLY` 为空时用 `DamengHelper.isViewUpdatable` 判断可更新性 |
  | `DamengView selectView(String schema, String viewName)` / `selectViewSimple(...)` / `selectView(DamengSelectViewParam)` | 单个视图 | 同上，单行 |
  | `boolean existView(String schema, String viewName)` | 视图是否存在 | `ALL_VIEWS` 计数 |
  | `void dropView(DamengView view)` | 删除视图 | `DROP VIEW IF EXISTS 库.视图` |
  | `void createView(DamengCreateViewParam)` / `void alertView(DamengAlertViewParam)` | 创建/修改视图 | 由 `DamengViewCreateSqlGenerator`/`DamengViewAlertSqlGenerator` 生成语句列表，事务内逐条执行并 `commit`，异常回滚 |
  | `List<DamengColumn> viewColumns(String schema, String viewName)` | 视图字段 | 先查 `ALL_TAB_COLUMNS` 取元数据，再 `SELECT * FROM 视图 FETCH FIRST 1 ROWS ONLY` 用 `DamengHelper.parseColumns` 校正可空/自增，按位置排序 |
  | `List<DamengRecord> viewRecords(String schema, String viewName, Long start, Long limit, List<DamengRecordFilter> filters)` | 视图记录 | 组装 SELECT + 过滤 + 分页；是否可更新决定记录 `readonly` |
  | `DBObjects<DamengIndex> indexes(String schema, String tableName)` | 索引列表 | 查 `ALL_INDEXES` JOIN `ALL_IND_COLUMNS`（排除 PRIMARY），用 `DamengIndex.type(indexType, noneUnique)` 归并类型并 `addColumn` |
  | `DBObjects<DamengCheck> checks(String schema, String tableName)` | 检查约束 | 不支持时返回 null；查 `ALL_CONSTRAINTS` 中 `CONSTRAINT_TYPE='C'` |
  | `DBObjects<DamengForeignKey> foreignKeys(String schema, String tableName)` | 外键列表 | 多表 JOIN `ALL_CONSTRAINTS`/`ALL_CONS_COLUMNS`（`CONSTRAINT_TYPE='R'`），按外键名归并并填列、引用表/库与更新/删除策略 |
  | `void createTable(DamengCreateTableParam)` / `void alertTable(DamengAlertTableParam)` | 创建/修改表 | `DamengTableCreateSqlGenerator`/`DamengTableAlertSqlGenerator` 生成语句，事务内执行 |
  | `boolean existTable(String schema, String tableName)` | 表是否存在（`@Deprecated`） | `ALL_TABLES` 计数 |
  | `void renameTable(String schema, String oldTableName, String newTableName)` | 重命名表 | `ALTER TABLE 库.旧名 RENAME TO 新名`（代替 MySQL 的 RENAME TABLE） |
  | `void clearTable(String schema, String tableName)` | 清空表 | `DELETE FROM 库.表` |
  | `void truncateTable(String schema, String tableName)` | 截断表 | `TRUNCATE TABLE 库.表` |
  | `void dropTable(String schema, String tableName)` | 删除表 | `DROP TABLE 库.表` |
  | `List<DamengFunction> selectFunctions(String schema)` / `selectFunctionsSimple(...)` / `selectFunctions(DamengSelectFunctionParam)` | 函数列表 | 查 `ALL_OBJECTS` LEFT JOIN `ALL_PROCEDURES`；`isFull()` 时 `listFunctionParam` 取参数、`showCreateFunction` 取体（从 `\nAS\n` 截取）并解析 `PARALLEL_ENABLE/RESULT_CACHE/AGGREGATE/PIPELINED/DETERMINISTIC` 特征 |
  | `DamengFunction selectFunction(String schema, String name)` / `selectFunctionSimple(...)` / `selectFunction(DamengSelectFunctionParam)` | 单个函数 | 同上，单行 |
  | `void createFunction(DamengCreateFunctionParam)` / `void alertFunction(DamengAlertFunctionParam)` | 创建/修改函数 | `DamengFunctionCreateSqlGenerator`/`DamengFunctionAlertSqlGenerator.generateSqlSingle` 后 `executeUpdate` |
  | `void dropFunction(DamengFunction)` | 删除函数 | `DROP function IF EXISTS 库.函数` |
  | `List<DamengProcedure> selectProcedures(String schema)` / `selectProceduresSimple(...)` / `selectProcedures(DamengSelectProcedureParam)` | 过程列表 | 同函数逻辑，`OBJECT_TYPE='PROCEDURE'` |
  | `DamengProcedure selectProcedure(String schema, String name)` / `selectProcedureSimple(...)` / `selectProcedure(DamengSelectProcedureParam)` | 单个过程 | 同上，单行 |
  | `void createProcedure(DamengCreateProcedureParam)` / `void alertProcedure(DamengAlertProcedureParam)` | 创建/修改过程 | 对应生成器生成语句后 `executeUpdate` |
  | `void dropProcedure(String schema, DamengProcedure)` | 删除过程 | `DROP PROCEDURE IF EXISTS 库.过程` |
  | `List<DamengRoutineParam> listRoutineParam(String schema, String routineName, String routineType)` | 例程参数列表 | 查 `ALL_ARGUMENTS`（`PACKAGE_NAME IS NULL`），逐行构造 `DamengRoutineParam`（size/digits/position/type/name/mode/charset） |
  | `List<DamengRoutineParam> listFunctionParam(...)` / `listProcedureParam(...)` | 函数/过程参数 | 委托 `listRoutineParam(..., "FUNCTION"/"PROCEDURE")` |
  | `String cloneTable(String schema, String tableName, boolean includeRecord)` | 克隆表 | 查检查/触发器/外键并改名、`setCreated(true)`；用 `showCreateTable` 替换表名后建表；再 `alertTable` 建约束/触发器/外键；`includeRecord` 时用 `SET IDENTITY_INSERT ... ON/OFF` + `INSERT ... SELECT` 复制数据，返回新表名 |
  | `String selectClientCharacter()` | 客户端字符集 | 达梦返回默认 `UTF-8` |
  | `boolean existAutoIncrement(String schema, String tableName)` | 是否存在自增列 | 查 `SYSCOLUMNS` JOIN `SYSOBJECTS` 且 `INFO2 = 1` |
  | `List<String> selectePrimaryKeys(String schema, String tableName)` | 主键名称列表 | 查 `ALL_CONSTRAINTS` 中 `CONSTRAINT_TYPE='P'` |
  | `DamengConnect getDbConnect()` | 连接信息 | 返回 `dbConnect` |
  | `String iid()` | 连接 id | `dbConnect.getId()` |
  | `DBDialect dialect()` | 方言 | 返回 `DBDialect.DAMENG` |
  | `void printSql(String sql)` | 打印/上报 SQL | `DBUtil.printSql` 后再 `DamengEventUtil.printSql(压缩后 sql, dbConnect)`（`private`） |

- 调用链：`start() → initClient() → connManager.connection().isValid(...) → state=CONNECTED → DamengEventUtil.connectionConnected(this)`
- 调用链：`createTable(param) → DamengTableCreateSqlGenerator.generateSql(param) → statement.executeUpdate(...) → connection.commit()`
- 调用链：`selectTables(param) → (isFull) showCreateTable(schema, tableName) → DBMS_METADATA.GET_DDL(...)`

---

## DamengConnManager

- 职责：达梦 JDBC 连接管理器，负责创建达梦连接与拼接连接串。
- 继承：`extends cn.oyzh.fx.db.DBConnManager`
- 字段：无自声明字段（使用父类 `config` 等）。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `Connection initConnection(String dbName, String user, String password)` | 初始化连接 | 加载驱动 `dm.jdbc.driver.DmDriver`；取 `getConnectionString()`（可追加 dbName）；构造 `Properties`（`Configuration.user/password`，并解析 `config.getEnv()` 的多行 `k=v` 环境参数）；`DriverManager.getConnection(host, props)`；打印连接属性与 `getClientInfo()` |
  | `String getConnectionString()` | 连接串 | `"jdbc:dm://" + host + ":" + port + "/"` |

- 调用链：`ShellDamengClient.start() → connManager.connection() → DamengConnManager.initConnection(...) → DriverManager.getConnection(...)`

---

## DamengHelper

- 职责：达梦数据库操作工具类，提供视图可更新性判断、结果集字段解析、触发器定义修正与自增异常识别。
- 字段：无自声明字段（另有大量历史实现被注释，未生效）。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static boolean isViewUpdatable(Connection connection, String schema, String viewName)` | 视图是否可更新 | 查 `USER_VIEWS.READ_ONLY`；为空或为 `N` 视为可更新；异常打印堆栈并返回 false |
  | `static DamengColumns parseColumns(ResultSet resultSet)` | 解析结果集字段列表 | 委托 `parseColumns(resultSet, Collections.emptyList())` |
  | `static DamengColumns parseColumns(ResultSet resultSet, List<String> excludes)` | 解析结果集字段列表（可排除） | 读 `ResultSetMetaData`，逐列按 label 过滤、取类型/长度/精度/可空/自增等；打印调试信息；构造 `DamengColumn` 并加入 `DamengColumns` |
  | `static String fixTiggerDefinition(String definition)` | 修正触发器定义 | 含 `" ROW BEGIN "` 与 `"END;"` 时截取两者之间的语句体 |
  | `static boolean isIdentityError(Throwable err)` | 是否自增列错误 | 消息含「不存在IDENTITY列」，或同时含 `[`、`]`、`IDENTITY` 时判定为 true |

- 调用链：`ShellDamengClient.selectRecords(...)/viewRecords(...) → DamengHelper.parseColumns(resultSet) → DamengColumn`
- 调用链：`ShellDamengClient.selectTriggers(...) → DamengHelper.fixTiggerDefinition(definition)`

---

## DamengSchema

- 职责：达梦模式（库）模型。
- 继承：`implements cn.oyzh.fx.db.DBSchema`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `name` | `String` | 模式名称 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `String getName()` / `void setName(String)` | 名称读写 | 属性访问器 |

- 调用链：`ShellDamengClient.schemas() → new DamengSchema() → setName(name)`

---

## 覆盖统计

- 覆盖类数：4 个。
- 类名清单：`ShellDamengClient`、`DamengConnManager`、`DamengHelper`、`DamengSchema`。
- 跳过（死代码）：`DamengDatabase`（`database` 包，整文件被注释）。
