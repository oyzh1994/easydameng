# easydameng data 包 代码审查文档

- 范围：`easydameng/src/main/java/cn/oyzh/easydameng/data/`（递归）。
- 已跳过的整文件注释死代码（`//` 整文件注释，无有效代码）：
  - `data/`：`ShellBatchInsertable.java`、`ShellDataHandler.java`、`ShellDataExportHandler.java`、`ShellDataImportHandler.java`、`ShellDataRunFileHandler.java`、`ShellDataTransportHandler.java`
  - `data/db/`：`DBConnConfig.java`、`DBConnectionManager.java`、`DBDialect.java`、`DBEvents.java`、`DBFeature.java`、`DBObjectList.java`、`DBObjectStatus.java`、`DBSqlGenerator.java`
  - `data/db/dto/DBDataTransportObject.java`、`data/db/ui/DBDataTransportObjectListView.java`
  - `data/db/handler/`：`DBDataDumpHandler.java`、`DBDataExportHandler.java`、`DBDataImportHandler.java`、`DBDataRunFileHandler.java`、`DBDataTransportHandler.java`
  - `data/dameng/handler/`：`DataHandler.java`、`DataDumpHandler.java`、`DataRunSqlFileHandler.java`
- 通用约定：达梦处理器继承第三方基类 `cn.oyzh.fx.db.data.handler.*`（`DBDataExportHandler`/`DBDataImportHandler`/`DBDataRunFileHandler`/`DBDataDumpHandler`/`DBDataTransportHandler`）。基类提供中断检查 `checkInterrupt()`、消息/进度回调 `message()`/`processed()`/`processedIncr()`/`processedDecr()`、异常处理 `exception()`、批量插入 `addInsert()`/`doBatchInsert()`，以及 `dbName`/`batchLimit`/`queryLimit`/`selectLimit`/`readLimit`/`continueWithErrors`/`file`/`fileWriter`/`dumpType`/`dataType`/`sourceDatabase`/`targetDatabase` 等属性；类型判断 `isSqlType()`/`isCsvType()`/`isExcelType()` 等亦来自基类。本文档字段表仅列本类声明字段。

---

# 一、db 子包

## DBConnState
- 职责：数据库连接状态枚举，定义连接生命周期各状态及“是否已连接”语义。
- 字段（枚举常量，均重写 `isConnected()`）：

| 字段 | 类型 | 含义 |
|---|---|---|
| `NOT_INITIALIZED` | `DBConnState` | 未初始化，`isConnected()=false` |
| `CONNECTED` | `DBConnState` | 已连接，`isConnected()=true` |
| `CONNECTING` | `DBConnState` | 连接中，`isConnected()=false` |
| `CLOSED` | `DBConnState` | 已关闭，`isConnected()=false` |
| `FAILED` | `DBConnState` | 失败，`isConnected()=false` |
| `BROKEN` | `DBConnState` | 错误，`isConnected()=false` |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `isConnected()` | 是否已连接 | 抽象方法，各常量分别实现（仅 `CONNECTED` 返回 `true`） |

- 调用链：`其他组件 → DBConnState.CONNECTED.isConnected() → true`

## DBConnectManager
- 职责：连接管理接口，抽象连接对象与连接树节点的增删查能力。
- 字段：无
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `addConnect(DamengConnect)` | 添加连接 | 抽象，待实现 |
| `addConnects(List<DamengConnect>)` | 批量添加连接 | 默认方法，非空遍历调用 `addConnect` |
| `addConnectItem(DBConnectTreeItem)` | 添加连接树节点 | 抽象 |
| `addConnectItems(List<DBConnectTreeItem>)` | 批量添加连接树节点 | 抽象 |
| `delConnectItem(DBConnectTreeItem)` | 删除连接树节点 | 抽象，返回 `boolean` |
| `getConnectItems()` | 获取连接树节点列表 | 抽象 |
| `getConnectedItems()` | 获取已连接的连接节点 | 默认方法，`getConnectItems().parallelStream().filter(DBConnectTreeItem::isConnected)` |

- 调用链：
  - `调用方 → DBConnectManager.addConnects → addConnect（逐个）`
  - `调用方 → DBConnectManager.getConnectedItems → getConnectItems → 过滤 isConnected`

---

# 二、dameng/handler 子包

## DamengDataRunSqlFileHandler
- 职责：达梦 SQL 文件执行处理器，逐行解析 SQL 文件并执行/批量插入。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbClient` | `ShellDamengClient` | db 客户端 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `DamengDataRunSqlFileHandler(ShellDamengClient, String)` | 构造 | `super(dbName)` 后保存 `dbClient` |
| `runFile()` | 执行 SQL 文件 | 以 UTF-8 读取 `file`，逐行 `checkInterrupt`；跳过 `-- `/`#` 及 `/* */` 注释；`INSERT INTO`/`SET IDENTITY_INSERT` 走 `addInsert` 批量；`SET `/`DROP `/`COMMENT ON ` 直接 `dbClient.executeSqlSimple`；`CREATE` 语句用 `createFlag1` 聚合多行至 `;`；`delimiter ;` 包裹的函数/过程/触发器用 `createFlag2` 聚合；末尾 `doBatchInsert()` 收尾；异常 `exception()`，`continueWithErrors=false` 时中断 |
| `doBatchInsert(List<String>, boolean)` | 批量插入 | `dbClient.insertBatch(dbName, list, parallel)`，成功 `processedIncr(result)`，失败 `processedDecr(list.size())` 并抛出 |

- 调用链：
  - `runFile → dbClient.executeSqlSimple（普通语句）`
  - `runFile → addInsert → doBatchInsert → dbClient.insertBatch`

## DamengDataExportHandler
- 职责：达梦数据导出处理器，按配置将表数据导出为多种格式文件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbClient` | `ShellDamengClient` | db 客户端 |
| `config` | `DamengDataExportConfig` | 导出配置 |
| `tables` | `List<ShellDamengDataExportTable>` | 待导出表集合 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `DamengDataExportHandler(ShellDamengClient, String)` | 构造 | `super(schema)`，新建 `config` |
| `doExport()` | 执行导出 | 遍历 `tables`，`checkInterrupt` 后 `exportTable`，`processedIncr` |
| `initWriter(String, DamengColumns)` | 初始化文件写入器 | 按 `isXxxType()` 分派到 `DamengSql/Excel/Html/Json/Xml/Csv/TxtTypeFileWriter` |
| `exportTable(ShellDamengDataExportTable)` | 导出单表 | `initWriter` 后 `writeHeader`；循环按 `queryLimit` 分页 `dbClient.selectRecords`（`table.getRecords()` 非空则直接采用并结束）→ `writeRecord` → `processed(records.size())`；末尾 `writeTail` |
| `writeHeader(...)` | 写入头 | 调用 `writer.writeHeader()` |
| `writeRecord(...)` | 写入记录 | 记录 `toMap()` 后经 `DamengDataUtil.valueStandardization` 规整，再 `writer.writeObjects` |
| `writeTail(...)` | 写入尾 | `writer.writeTrial()` |
| `dateFormat(String)` | 设置日期格式 | 空白则默认 `yyyy-MM-dd HH:mm:ss`，写入 config |
| `recordSeparator(String)` / `txtIdentifier(String)` / `fieldSeparator(String)` | 设置分隔符/限定符 | 写入 config |
| `includeFields(boolean)` / `fieldToAttr(boolean)` / `earlyVersion(boolean)` | 设置导出选项 | 写入 config |
| `getDbClient()` / `setDbClient(...)` | 读写客户端 | 普通存取 |
| `getTables()` / `setTables(...)` | 读写导出表集合 | 普通存取 |
| `getConfig()` | 获取导出配置 | 普通存取 |

- 调用链：
  - `doExport → exportTable → initWriter → DamengXxxTypeFileWriter`
  - `exportTable → dbClient.selectRecords → writeRecord → writer.writeObjects`

## DamengDataImportHandler
- 职责：达梦数据导入处理器，将多种格式文件导入表并批量插入。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbClient` | `ShellDamengClient` | db 客户端 |
| `files` | `List<DamengDataImportFile>` | 导入文件集合 |
| `config` | `DamengDataImportConfig` | 导入配置 |
| `insertList` | `List<String>` | 待批量执行的插入 SQL 缓冲 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `DamengDataImportHandler(ShellDamengClient, String)` | 构造 | `super(name)`，新建 `config` |
| `doImport()` | 执行导入 | 遍历 `files`，`checkInterrupt` 后 `importRecord`；`processed(files.size())` |
| `importRecord(DamengDataImportFile)` | 导入单表 | 复制模式先 `dbClient.clearTable`；`initReader` 打开文件；`dbClient.selectColumns` 取目标列；循环 `readRecords`→`writeRecord`→`processed`；末尾 `doBatchInsert` |
| `initReader(File)` | 初始化文件读取器 | 按 `isXxxType()` 分派到 `DamengCsv/Json/Xml/Excel/TxtTypeFileReader` |
| `readRecords(DamengTypeFileReader, int)` | 读取记录 | `reader.readObjects(count)` 转 `DamengRecord` 列表 |
| `writeRecord(DamengColumns, List<DamengRecord>)` | 写入记录 | `DamengDataImportHelper.toInsertSql` 生成 SQL，`addInsertSql` |
| `addInsertSql(List<String>)` | 追加插入 SQL | 累积至 `insertList`，达到 `batchLimit` 触发 `doBatchInsert` |
| `doBatchInsert(List<String>, boolean)` | 批量插入 | `dbClient.insertBatch(name, list, parallel)`，成功/失败更新进度 |
| `dateFormat(String)` / `importMode(String)` / `columnIndex(int)` / `dataStartIndex(int)` / `recordLabel(String)` / `attrToColumn(boolean)` / `recordSeparator(String)` / `txtIdentifier(String)` / `fieldSeparator(String)` | 设置导入参数 | 写入 config |
| `getDbClient()` / `getFiles()` / `setFiles(...)` / `getConfig()` | 读写属性 | 普通存取 |

- 调用链：
  - `doImport → importRecord → initReader → DamengXxxTypeFileReader.readObjects`
  - `importRecord → writeRecord → DamengDataImportHelper.toInsertSql → addInsertSql → doBatchInsert → dbClient.insertBatch`

## DamengDataTransportHandler
- 职责：达梦数据传输处理器，将源库的表/视图/函数/过程/触发器结构与数据复制到目标库。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `sourceClient` | `ShellDamengClient` | 来源客户端 |
| `targetClient` | `ShellDamengClient` | 目标客户端 |
| `views` | `List<DBDataTransportObject>` | 视图集合 |
| `tables` | `List<DBDataTransportObject>` | 表集合 |
| `triggers` | `List<DBDataTransportObject>` | 触发器集合 |
| `functions` | `List<DBDataTransportObject>` | 函数集合 |
| `procedures` | `List<DBDataTransportObject>` | 过程集合 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `DamengDataTransportHandler()` | 构造 | `super(DBDialect.DAMENG)` |
| `doTransport()` | 执行传输 | 依次传输 tables/views/functions/procedures/triggers，异常 `exception()` |
| `transportTable(String)` | 传输表 | 目标库 `DROP TABLE IF EXISTS`；`sourceClient.showCreateTable` 去除架构前缀后建表；含自增列时开关 `SET IDENTITY_INSERT ON/OFF`；分页 `sourceClient.selectRecords` → `DamengDataUtil.toInsertSql` → `addInsert` → 末尾 `doBatchInsert` |
| `transportView(String)` | 传输视图 | 目标 `DROP VIEW IF EXISTS`，`showCreateView` 后建视图 |
| `transportFunction(String)` | 传输函数 | 目标 `DROP FUNCTION IF EXISTS`，`showCreateFunction` 后建函数 |
| `transportProcedure(String)` | 传输过程 | 目标 `DROP PROCEDURE IF EXISTS`，`showCreateProcedure` 后建过程 |
| `transportTrigger(String)` | 传输触发器 | 目标 `DROP TRIGGER IF EXISTS`，`showCreateTrigger` 后建触发器 |
| `doBatchInsert(List<String>, boolean)` | 批量插入 | `targetClient.insertBatch(targetDatabase, list, parallel)` |
| `getSourceClient()` / `setSourceClient(...)` / `getTargetClient()` / `setTargetClient(...)` | 读写客户端 | 普通存取 |
| `getViews/setViews`、`getTables/setTables`、`getTriggers/setTriggers`、`getFunctions/setFunctions`、`getProcedures/setProcedures` | 读写各类对象集合 | 普通存取 |

- 调用链：
  - `doTransport → transportTable → sourceClient.showCreateTable / selectRecords → targetClient.executeSqlSimple / insertBatch`
  - `doTransport → transportView/Function/Procedure/Trigger → showCreateXxx → targetClient.executeSqlSimple`

## DamengDataDumpHandler
- 职责：达梦数据转储处理器，将库结构（可选数据）转储为可执行的 SQL 文件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbClient` | `ShellDamengClient` | db 客户端 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `DamengDataDumpHandler(ShellDamengClient, String)` | 构造 | `super(dbName, DBDialect.DAMENG)`，保存 `dbClient` |
| `doDump()` | 执行转储 | 校验 `fileWriter`/`dumpType`/`dataType`；`writeHeader`；`dumpType==1` 转储表+视图+函数+过程+触发器，`dumpType==2` 按 `tableName` 转储单表；`writeTail`、关闭写入器 |
| `writeHeader()` | 写文件头 | 拼接源服务器/类型/版本/主机/schema、目标类型、文件编码、日期等注释块，`fileWriter.writeLines` |
| `writeTail()` | 写文件尾 | 空实现 |
| `dumpTable()` | 转储全部表 | `selectTables`（`full=true`）遍历 `dumpTable(table)` |
| `dumpTable(DamengTable)` | 转储单表结构 | 生成 `DROP TABLE IF EXISTS` + 建表语句（去除架构前缀）；遍历字段生成 `COMMENT ON COLUMN`，生成 `COMMENT ON TABLE`；`isDumpRecord()` 时 `dumpRecord` |
| `dumpRecord(String, DamengColumns)` | 转储表数据 | 自增列包 `SET IDENTITY_INSERT ON/OFF`；分页 `selectRecords` → `DamengDataUtil.toInsertSql` → `fileWriter.appendLines` |
| `dumpView()` | 转储全部视图 | 遍历 `selectViews`，输出 drop+`showCreateView` |
| `dumpFunction()` | 转储全部函数 | 遍历 `selectFunctions`，输出 drop+`delimiter ;;`+`showCreateFunction` |
| `dumpProcedure()` | 转储全部过程 | 遍历 `selectProcedures`，输出 drop+`delimiter ;;`+`showCreateProcedure` |
| `dumpTrigger()` | 转储全部触发器 | 遍历 `selectTriggers`，输出 drop+`delimiter ;;`+`showCreateTrigger` |

- 调用链：
  - `doDump → dumpTable（全部）→ dumpTable（单体）→ dumpRecord → DamengDataUtil.toInsertSql → fileWriter.appendLines`
  - `doDump → dumpType==2 → dbClient.selectTable → dumpTable(table)`

---

# 三、dameng/ui 子包

说明：5 个类均继承第三方 `cn.oyzh.fx.db.data.ui.DBDataTransportObjectListView`（继承其 `init(List<DBDataTransportObject>)`、`getSelectedObjects()`、`getSelectedSize()`、`selectedChanged` 等能力），职责为把达梦对象集合转换为复选框列表项。

## DamengDataTransportTableListView
- 职责：达梦数据传输表列表视图。
- 字段：无（继承基类 `selectedChanged`）
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `of(List<DamengTable>)` | 依据表集合初始化列表 | 逐个取 `table.getName()` 构造 `DBDataTransportObject`，调用基类 `init(list)` |

- 调用链：`调用方 → of → DBDataTransportObject → DBDataTransportObjectListView.init`

## DamengDataTransportViewListView
- 职责：达梦数据传输视图列表视图。
- 字段：无
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `of(List<DamengView>)` | 依据视图集合初始化列表 | 取 `view.getName()` 构造 `DBDataTransportObject`，`init(list)` |

- 调用链：`调用方 → of → DBDataTransportObjectListView.init`

## DamengDataTransportFunctionListView
- 职责：达梦数据传输函数列表视图。
- 字段：无
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `of(List<DamengFunction>)` | 依据函数集合初始化列表 | 取 `function.getName()` 构造 `DBDataTransportObject`，`init(list)` |

- 调用链：`调用方 → of → DBDataTransportObjectListView.init`

## DamengDataTransportProcedureListView
- 职责：达梦数据传输过程列表视图。
- 字段：无
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `of(List<DamengProcedure>)` | 依据过程集合初始化列表 | 取 `procedure.getName()` 构造 `DBDataTransportObject`，`init(list)` |

- 调用链：`调用方 → of → DBDataTransportObjectListView.init`

## DamengDataTransportTriggerListView
- 职责：达梦数据传输触发器列表视图。
- 字段：无
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `of(List<DamengTrigger>)` | 依据触发器集合初始化列表 | 取 `trigger.getName()` 构造 `DBDataTransportObject`，`init(list)` |

- 调用链：`调用方 → of → DBDataTransportObjectListView.init`
