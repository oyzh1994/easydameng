# util 包代码审查

通用工具包，提供客户端创建、连接测试、字段类型注册、表单/记录节点生成、数据参数化与导入导出 SQL 生成、页面工厂等能力。

---

## DBClientUtil
- 职责：达梦数据库客户端工具类。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `newClient(DamengConnect info)` | 根据连接信息创建客户端 | type 为空或解析为 `DBDialect.DAMENG` 时 `new ShellDamengClient(info)`（存在重复判断分支），否则返回 null |

- 调用链：`DBConnectUtil.testConnect → DBClientUtil.newClient → ShellDamengClient`

## DBConnectUtil
- 职责：达梦数据库连接工具类。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `testConnect(StageAdapter view, DamengConnect dbInfo)` | 测试连接 | 后台线程禁用窗口、显示等待光标；`newClient` 后 `client.start()`，已连接则关闭并 `MessageBox.okToast("连接成功！")`，否则 warn；finally 恢复窗口 |
  | `close(ShellDamengClient client, boolean async)` | 关闭连接 | 已连接时按 `async` 选择 `ThreadUtil.start` 或直接执行 `client.close()` |
  | `parse(String input)` | 解析命令行连接串 | 按空格分词，`-h/-p/-a/-n` 分别映射 host/port/password/db，返回 `dto.DamengConnect` |

- 调用链：`连接测试按钮 → DBConnectUtil.testConnect → DBClientUtil.newClient → ShellDamengClient.start`

## DamengProcessUtil
- 职责：达梦数据库客户端进程工具。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `restartApplication()` | 重启应用 | `ProcessUtil.restartApplication(100, StageManager::exit)` |

- 调用链：`设置页重启 → DamengProcessUtil.restartApplication → StageManager.exit`

## DamengI18nHelper
- 职责：达梦数据库国际化资源帮助类。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `tableTip2()/tableTip3()/tableTip4()` | 取表提示文案 | `I18nResourceBundle.i18nString("db.table.tip2/3/4")` |

- 调用链：`表设计界面 → DamengI18nHelper.tableTipN → I18nResourceBundle`

## DBExportUtil
- 职责：达梦数据库导出工具类（空壳，无实现）。
- 字段：无。
- 方法：无。
- 调用链：—

## DamengColumnUtil
- 职责：达梦字段类型工具类，负责向 `DBColumnFieldManager` 注册达梦支持的列类型及其特性。
- 字段：无（无实例字段）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init()` | 初始化并注册字段类型 | 构造大量 `DBColumnField`（CHAR/VARCHAR2/CLOB/NUMBER/INT/BIGINT/SMALLINT/TINYINT/FLOAT/DOUBLE/DECIMAL/NUMERIC/DATE/TIMESTAMP/DATETIME/TIME/BLOB/IMAGE/RAW/VARBINARY/BINARY/BIT 等），逐一设置 `suggestSize/support*` 特性后 `putFiled` |
  | `putFiled(DBColumnField)` | 注册字段类型 | `DBColumnFieldManager.putFiled(DBDialect.DAMENG, columnField)` |
  | `fields()` | 取字段类型名列表 | `DBColumnFieldManager.fields(DAMENG)` 映射为名称列表 |
  | `isYearType/isDateType/isDateTimeType/isTimeType(String)` | 类型判断 | 分别与 `YEAR/DATE/DATETIME/TIME` 忽略大小写比较 |
  | `isPolygonType/isMultiPolygonType/isPointType/isMultiPointType/isLineStringType/isMultiLineStringType/isGeomCollectionType/isGeometryType(String)` | 几何类型判断 | 与对应字符串忽略大小写比较 |
  | `defaultValue(String type)` | 取字段默认值 | 依据 `DBColumnFieldManager.support*` 返回数字/字符串/json/二进制默认值 |

- 调用链：`EasyDamengApp.start → DBColumnFieldManager.registerInitializer(DAMENG, DamengColumnUtil::init) → init → putFiled`

## DamengNodeUtil
- 职责：达梦数据库表单节点工具类，负责根据字段类型创建、赋值与标记表单节点。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getNodeVal(Node)` | 取节点当前值 | 按 `Time/DateTime/Date/Number/Decimal/Bit/ChooseFile/Text/TextArea/ComboBox` 分支取值 |
  | `setNodeVal(Node, Object)` | 设置节点值 | 先 `DamengDataUtil.valueStandardization`，再按节点类型 setValue/setText/select |
  | `generateNode(DamengColumn)` | 生成表单节点 | 委托 `generateNode(column, true)` |
  | `generateNode(DamengColumn, boolean handlerDefaultValue)` | 按字段类型生成节点 | 按 json/jsonArray/string/bit/boolean/integer/digits/year/time/date/timestamp/binary 分支创建控件，设置 id="value"，处理小数位/注释/默认值 |
  | `generateTags(DamengColumn)` | 生成标签列表 | nullable/autoIncrement/updateOnCurrentTimestamp/primaryKey/unsigned/zeroFill 生成带样式的 `FXLabel` |
  | `handlerDigits(Node, Integer)` | 处理小数位 | `DecimalTextField.setScaleLen` |
  | `handlerComment(Node, String)` | 处理注释 | 作为 `TextInputControl` 的 promptText |
  | `handlerDefaultValue(Node, Object)` | 处理默认值 | 数字/下拉/文本框赋值 |
  | `handlerExampleValue(Node, Object)` | 处理示例值 | 数字/文件/文本框赋值 |

- 调用链：`表设计/字段弹窗 → DamengNodeUtil.generateNode → DamengColumn.support*`；`提交 → getNodeVal → 值封装`

## DamengRecordUtil
- 职责：达梦数据库记录工具类，负责记录字段节点的创建、格式化与右键菜单生成。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getNode(DamengRecordProperty, Object, DamengColumn)` | 创建记录字段节点 | 先值规整化；按 json/text/binary/integer/digits/bit/boolean/date/time/year/timestamp/string 分支创建带底色控件；`FXTextField` 设置 null 提示与右键菜单，并监听文本变更置 `property.changed=true` |
  | `formatValue(Object, DamengColumn)` | 格式化字段值 | 无类型时按 CharSequence/byte[]/Date 处理；否则按类型调各控件的 `format` |
  | `getColumnContextMenu(DamengRecordProperty)` | 取字段右键菜单 | 由 `getColumnMenuItem` 构建 |
  | `getColumnMenuItem(DamengRecordProperty)` | 取菜单项列表 | 复制/粘贴/置空/置空串/复制为 INSERT/复制为 UPDATE 菜单项，绑定 `property.v*` 方法 |

- 调用链：`记录表渲染 → DamengRecordUtil.getNode → 编辑 → property.vCopy/vPaste`

## DamengDataUtil
- 职责：达梦数据工具类，将记录值按目标格式参数化并生成插入/更新 SQL 或结构化数据。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `parameterizedForJson/ForXml/ForHtml(DamengColumn, Object)` | 参数化为 json/xml/html | 按日期/时间戳/json/二进制/布尔/bit/字符串/数字分支转换（xml 与 json 实现一致），字符串 `TextUtil.escape` |
  | `parameterizedForCsv(DamengColumn, Object)` | 参数化为 csv | 空值返回 `""`；日期加引号、二进制转 hex、bit 转 `"b'...'"`、字符串加引号转义 |
  | `parameterizedForSql(DamengColumn, Object)` | 参数化为 sql | 空值 `NULL`；日期 `'...'`、二进制 hex、bit `b'...'`、字符串 `'...'` 转义 |
  | `parameterizedForXls(DamengColumn, Object)` | 参数化为 xls | 类似 html，时间戳格式 `yyyy/M/dd HH:mm:ss` |
  | `toInsertSql(columns, record, includeFields)` | 单条插入 SQL | 委托列表重载取首条 |
  | `toInsertSql(columns, records[, includeFields])` | 批量插入 SQL | `INSERT INTO 表 [(字段...)] VALUES (...)`，值经 `parameterizedForSql` |
  | `toUpdateSql(columns, record)` | 更新 SQL | 用 `ShellDamengUtil.initPrimaryKey` 取主键；无主键时全字段作条件并加 `LIMIT 1`，有主键时按主键条件 |
  | `toInsertJson/ToInsertXml` | 生成 Map 列表 | 逐列 `parameterizedForJson/Xml` 放入 `Map<String,Object>` |
  | `toInsertCsv/toInsertHtml/toInsertXls` | 生成 List 列表 | 逐列对应参数化后加入 `List<Object>` |
  | `valueStandardization(Object)` | 值规整化 | `DmdbClob`/`DmdbBlob` 解包为 `data` |

- 调用链：`数据导出 → DamengDataUtil.toInsertSql/toInsertJson → parameterizedFor*`；`记录编辑 → toUpdateSql → ShellDamengUtil.initPrimaryKey`

## DamengRecordUtil / DamengNodeUtil 协作
见上；二者分别服务于「记录视图」与「表单视图」。

## ShellDamengUtil
- 职责：达梦数据库工具类（保留少量元数据与主键处理能力）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `ENABLE_PRINT_METADATA` | `boolean`（静态） | 是否启用元数据打印，默认 true |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `isInternalDatabase(String dbName)` | 是否内部库 | 与 `SYS/SYSDBA/SYSSSO/SYSAUDITOR/CTISYS` 忽略大小写比较 |
  | `printMetaData(ResultSet)` | 打印元数据 | 遍历 `ResultSetMetaData` 列名 `JulLog.info` |
  | `initPrimaryKey(DamengColumns, DamengRecord)` | 初始化主键 | 取 `columns.primaryKeys()` 首个主键列，`DamengRecordPrimaryKey.init(column, record)` |

- 调用链：`DamengDataUtil.toUpdateSql → ShellDamengUtil.initPrimaryKey → DamengRecordPrimaryKey`；`树加载 → isInternalDatabase`

## ShellDamengViewFactory
- 职责：达梦数据库页面工厂，统一负责各类功能页面的创建与展示。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `exportData(client, schema, tableName)` / `exportData(client, schema, tableName, exportMode, exportTable)` | 打开数据导出页 | `StageManager.parseStage(ShellDamengDataExportController)` 并设置 dbName/dbClient/tableName/exportMode/exportTable 后 `display()` |
  | `importData(client, schema)` | 打开数据导入页 | 同模式创建 `ShellDamengDataImportController` |
  | `dumpData(client, schema, tableName, dumpType)` | 打开数据转储页 | 创建 `ShellDamengDataDumpController`，设置 dumpType/dbName/dbClient/tableName |
  | `runSqlFile(client, schema)` | 打开运行 SQL 文件页 | 创建 `ShellDamengDataRunSqlFileController` |
  | `transportData(connect, schema)` | 打开数据传输页 | 创建 `ShellDamengDataTransportController`，设置 connect/dbName |
  | `tableInfo(DamengTableTreeItem)` | 表信息页 | 创建 `ShellDamengTableInfoController`，设置 item |
  | `viewInfo(DamengViewTreeItem)` | 视图信息页 | 创建 `DamengViewInfoController`，设置 item |
  | `functionInfo(DamengFunctionTreeItem)` | 函数信息页 | 创建 `ShellDamengFunctionInfoController`，设置 item |
  | `procedureInfo(DamengProcedureTreeItem)` | 过程信息页 | 创建 `ShellDamengProcedureInfoController`，设置 item |

- 调用链：`表树右键 → ShellDamengViewFactory.exportData → ShellDamengDataExportController.display`；`表树双击 → tableInfo → ShellDamengTableInfoController`

---

## 已跳过的死代码

- `util/db/ShellDBUtil.java`：整文件被注释。
