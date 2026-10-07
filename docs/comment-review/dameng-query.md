# 查询/编辑器相关类审查

范围：`cn.oyzh.easydameng.dameng.query` 包下正式类（10 个）。
该包负责 SQL 查询编辑器、语法自动提示（词法分析 → 提示项匹配 → 下拉视图）、查询/执行计划结果的解析。
跳过（整文件被注释，死代码）：`DamengQueryResults`。

---

## ShellDamengQueryEditor

- 职责：达梦 SQL 查询文本编辑器（继承 fx-base 的 `DBQueryEditor`），支持注释切换、SQL 美化与提示弹出。
- 继承：`extends cn.oyzh.fx.db.query.DBQueryEditor`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dialect` | `DBDialect` | 方言 |
  | `promptPopup` | `ShellDamengQueryPromptPopup` | 提示词组件 |
  | `runCallback` | `Runnable` | 运行回调 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DBDialect getDialect()` / `void setDialect(DBDialect)` | 方言读写 | — |
  | `ShellDamengQueryPromptPopup promptPopup()` | 获取/懒建提示弹框 | `new ShellDamengQueryPromptPopup()` |
  | `void initNode()` | 初始化 | 提示项选中回调 `promptPopup().autoComplete(this, item)`；`setFormatType(SQL)` |
  | `void doComment()` | 注释/反注释切换 | 读选区行，若全部以 `-- ` 开头则反注释，否则加 `-- `；按行边界修正选区范围 |
  | `void pretty()` | SQL 美化 | `DBSqlParser.prettySql(sql, dialect)` 后回填文本 |
  | `List<? extends MenuItem> getMenuItems()` | 右键菜单 | 有选中文本时加入「运行选中」项（`MenuItemHelper.runSelected`），再拼接父类菜单 |
  | `void setRunCallback(Runnable)` / `void run()` | 运行回调读写与触发 | `run()` 调用 `runCallback.run()` |
  | `Font getEditorFont()` | 编辑器字体 | 读 `DamengSettingStore.SETTING.editorFontConfig()` → `FontManager.toFont(...)` |

- 调用链：`ShellDamengQueryEditor.promptPopup() → ShellDamengQueryPromptPopup → ShellDamengQueryTokenAnalyzer → ShellDamengQueryUtil.getXxx()`

---

## ShellDamengQueryPromptPopup

- 职责：达梦查询语法提示弹框（泛型化提示项与词法单元）。
- 继承：`extends DBQueryPromptPopup<ShellDamengQueryPromptItem, ShellDamengQueryToken>`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DBQueryPromptListView<ShellDamengQueryPromptItem> initListView()` | 初始化提示列表视图 | `new ShellDamengQueryPromptListView()` |
  | `DBQueryTokenAnalyzer<...> tokenAnalyzer()` | 获取词法分析器 | 返回 `ShellDamengQueryTokenAnalyzer.INSTANCE` |
  | `void autoComplete(DBQueryEditor editor, ShellDamengQueryPromptItem item)` | 自动补全 | `token != null` 时 `super.replaceText(editor, item.wrapContent())` |

- 调用链：`ShellDamengQueryEditor.promptPopup().autoComplete(...) → item.wrapContent() → ShellDamengQueryPromptItem.wrapContent()`

---

## DamengQueryResult

- 职责：达梦查询结果抽象基类，持有字段列表与记录列表，提供库/表/主键等派生信息。
- 继承：`extends cn.oyzh.fx.db.query.DBQueryResult`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columns` | `DamengColumns` | 字段列表 |
  | `records` | `List<DamengRecord>` | 行（记录）列表 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `int getCount()` | 行数 | `records` 为空返回 0 |
  | `String schema()` | 库名 | 取首个字段的 `getSchema()` |
  | `String tableName()` | 表名 | 取首个字段的 `getTableName()` |
  | `DamengColumn getPrimaryKey()` | 主键字段 | 返回首个 `isAutoIncrement()` 字段 |
  | `boolean isUpdatable()` | 是否可更新 | 存在自增字段即视为可更新 |
  | `List<DamengColumn> columnList()` | 字段列表 | 空时返回 `Collections.emptyList()` |
  | `getColumns/setColumns`、`getRecords/setRecords` | 属性访问器 | — |

- 调用链：`DamengExecuteResult/DamengExplainResult extends DamengQueryResult → parseResult(...) 填充 columns/records`

---

## DamengExecuteResult

- 职责：达梦 SQL 执行结果，解析结果集为记录列表。
- 继承：`extends DamengQueryResult`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `fullColumn` | `boolean` | 是否全字段 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void parseResult(ResultSet, Connection, boolean readonly)` | 解析结果集 | `DamengHelper.parseColumns(resultSet)` 取字段；逐行 `new DamengRecord(columns, readonly)` 并 `putValue(dbColumn, data)` |
  | `boolean isFullColumn()` / `void setFullColumn(boolean)` | 全字段标志读写 | — |

- 调用链：`(执行 SQL) → DamengExecuteResult.parseResult(rs, conn, readonly) → DamengHelper.parseColumns(...) → DamengRecord.putValue(...)`

---

## DamengExplainResult

- 职责：达梦执行计划（EXPLAIN）结果，解析结果集。
- 继承：`extends DamengQueryResult`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void parseResult(ResultSet, Connection, boolean)` | 解析结果集 | `DamengHelper.parseColumns(resultSet)`；逐行构造 `DamengRecord` 并 `putValue(dbColumn, data)` |

- 调用链：`(EXPLAIN SQL) → DamengExplainResult.parseResult(...) → DamengHelper.parseColumns(...)`

---

## ShellDamengQueryToken

- 职责：达梦查询词法单元（继承 fx-base `DBQueryToken`），按 token 字符判断可能匹配的对象类型。
- 继承：`extends cn.oyzh.fx.db.query.DBQueryToken`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `boolean isPossibilityKeyword()` | 是否可能为关键字 | token 为 `' '`、`'n'` 或 `'\0'` |
  | `boolean isPossibilityTable()/isPossibilityView()/isPossibilityFunction()/isPossibilityProcedure()/isPossibilityColumn()` | 是否可能为表/视图/函数/过程/字段 | token 为 `' '`、`'"'`、`','` 或 `'.'` |
  | `boolean isPossibilityDatabase()` | 是否可能为库 | token 为 `'"'` 或 `' '` |

- 调用链：`ShellDamengQueryTokenAnalyzer.initPrompts(token, minCorr) → token.isPossibilityXxx()`

---

## ShellDamengQueryTokenAnalyzer

- 职责：达梦查询词法分析器（单例），从编辑内容定位当前 token 并生成候选提示项。
- 继承：`extends DBQueryTokenAnalyzer<ShellDamengQueryPromptItem, ShellDamengQueryToken>`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `ShellDamengQueryTokenAnalyzer` | 单例（`public static final`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `ShellDamengQueryToken currentToken(String content, int currentIndex)` | 定位光标前的 token | 截取 `content[0, currentIndex)`，反向扫描找最近的分隔符（`\n`/空格/反引号/`.`/`,`）；无分隔符时 token 类型为 `\0`；设置 token、起止下标与 trim 后内容 |
  | `List<ShellDamengQueryPromptItem> initPrompts(ShellDamengQueryToken token, float minCorr)` | 生成候选提示项 | 按 token 可能性从 `ShellDamengQueryUtil` 取关键字/库/表/视图/函数/过程/字段，用 `TextUtil.clacCorr` 算相关度过滤（`> minCorr`），构造 `ShellDamengQueryPromptItem` 并按 type 标记；`ThreadUtil.submit` 并行执行后按相关度升序再 `reversed()` 返回 |

- 调用链：`ShellDamengQueryPromptPopup.tokenAnalyzer() → ShellDamengQueryTokenAnalyzer.INSTANCE.initPrompts(...) → ShellDamengQueryUtil.getTables()/getFunctions()/...`

---

## ShellDamengQueryPromptItem

- 职责：查询提示内容项（继承 fx-base `DBQueryPromptItem`），按 type 数字标识对象类别。
- 继承：`extends cn.oyzh.fx.db.query.DBQueryPromptItem`
- 字段：无自声明字段。
- 类型约定：1=数据库、2=表、3=字段、4=关键字、5=视图、6=函数、7=过程。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `boolean isDatabaseType()` | 是否数据库类型 | `getType() == 1` |
  | `boolean isTableType()` | 是否表类型 | `getType() == 2` |
  | `boolean isColumnType()` | 是否字段类型 | `getType() == 3` |
  | `boolean isKeywordType()` | 是否关键字类型 | `getType() == 4` |
  | `boolean isViewType()` | 是否视图类型 | `getType() == 5` |
  | `boolean isFunctionType()` | 是否函数类型 | `getType() == 6` |
  | `boolean isProcedureType()` | 是否过程类型 | `getType() == 7` |
  | `String wrapContent()` | 包装补全内容 | 字段类型用 `DBUtil.wrap(content, DBDialect.MYSQL)` 包裹，其它原样返回 |

- 调用链：`ShellDamengQueryTokenAnalyzer.initPrompts(...) → new ShellDamengQueryPromptItem() → setType((byte) 2) 等`

---

## ShellDamengQueryPromptListView

- 职责：查询提示列表视图，按提示项类型渲染不同 SVG 图标。
- 继承：`extends DBQueryPromptListView<ShellDamengQueryPromptItem>`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `SVGLabel initPromptLabel(ShellDamengQueryPromptItem item)` | 生成主标签 | 依 `isDatabaseType/isKeywordType/isTableType/isColumnType/isViewType/isFunctionType/isProcedureType` 选用对应 `xxxSVGGlyph` 组装 `SVGLabel`（表/字段/函数/过程设置宽度 240） |
  | `FXLabel initExtLabel(ShellDamengQueryPromptItem item)` | 生成扩展标签 | 表/视图/字段类型时展示 `getExtContent()`（如库.表），灰色文字 |

- 调用链：`ShellDamengQueryPromptPopup.initListView() → ShellDamengQueryPromptListView.initPromptLabel(item)`

---

## ShellDamengQueryUtil

- 职责：查询提示的数据索引静态工具（缓存关键字、库、表、视图、函数、过程、字段），供提示匹配使用。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `indexStatus` | `int` | 索引状态（0 未初始化 / 1 初始化中 / 2 已初始化） |
  | `DB_KEYWORDS` | `List<String>` | 关键字列表（`static final`，静态块填充 DML/DDL/查询/函数关键字） |
  | `DB_SCHEMAS` | `List<DamengSchema>` | 数据库(模式)列表 |
  | `DB_TABLES` | `List<DamengTable>` | 表列表 |
  | `DB_VIEWS` | `List<DamengView>` | 视图列表 |
  | `DB_FUNCTIONS` | `List<DamengFunction>` | 函数列表 |
  | `DB_PROCEDURES` | `List<DamengProcedure>` | 过程列表 |
  | `DB_COLUMNS` | `List<DamengColumn>` | 字段列表 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static List<String> getKeywords()` | 关键字列表 | — |
  | `static List<DamengSchema> getSchemas()` | 库列表 | — |
  | `static List<DamengTable> getTables()` | 表列表 | — |
  | `static List<DamengView> getViews()` | 视图列表 | — |
  | `static List<DamengFunction> getFunctions()` | 函数列表 | — |
  | `static List<DamengProcedure> getProcedures()` | 过程列表 | — |
  | `static List<DamengColumn> getColumns()` | 字段列表 | — |
  | `static void updateIndex(ShellDamengClient client)` | 异步重建索引 | 状态为 0 时置 1，清空各级缓存，`client.schemas()` 加载库，再对非内部库并行 `selectTablesSimple/selectViewsSimple/selectFunctionsSimple/selectProceduresSimple`；`ThreadUtil.submit(tasks)` 后置 2，异常回置 0 |

- 调用链：`ShellDamengQueryTokenAnalyzer.initPrompts(...) → ShellDamengQueryUtil.getTables()/getSchemas()/getColumns()`
- 调用链：`ShellDamengQueryUtil.updateIndex(client) → client.selectTablesSimple(...) 等 → ThreadUtil.submit(...)`

---

## 覆盖统计

- 覆盖类数：10 个。
- 类名清单：`ShellDamengQueryEditor`、`ShellDamengQueryPromptPopup`、`DamengQueryResult`、`DamengExecuteResult`、`DamengExplainResult`、`ShellDamengQueryToken`、`ShellDamengQueryTokenAnalyzer`、`ShellDamengQueryPromptItem`、`ShellDamengQueryPromptListView`、`ShellDamengQueryUtil`。
- 跳过（死代码）：`DamengQueryResults`。
