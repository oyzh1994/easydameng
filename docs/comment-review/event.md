# easydameng event 包 代码审查文档

- 范围：`easydameng/src/main/java/cn/oyzh/easydameng/event/`（递归）。
- 已跳过的整文件注释死代码：`event/event/*`（`DamengEventAddedEvent`、`DamengEventAlertedEvent`、`DamengEventDesignEvent`、`DamengEventRenamedEvent`）、`event/record/RecordDeleteEvent.java`。
- 通用约定：事件类均继承 `cn.oyzh.event.Event<T>`，泛型 `T` 为事件载荷（data），通过父类 `data()` / `data(T)` 读写；事件由 `cn.oyzh.event.EventUtil.post(event)` 发布。实现 `EventFormatter` 的类通过 `eventFormat()` 生成日志文本。仅声明字段与自有方法，父类通用方法不重复列出。

---

## DamengEventUtil
- 职责：达梦事件发布的统一工具类，集中构造各类事件对象并调用 `EventUtil.post` 投递。
- 字段：无
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `tableOpen(DamengTableTreeItem, DamengSchemaTreeItem)` | 打开表 | 构造 `DamengTableOpenEvent`，`data(item)`、`setDbItem(dbItem)`、`post` |
| `tableAdded(DamengSchemaTreeItem)` | 表已新增 | 构造 `DamengTableAddedEvent`，`data(item)`、`post` |
| `tableAlerted(String, DamengSchemaTreeItem)` | 表已刷新 | 构造 `DamengTableAlertedEvent`，`data(tableName)`、`setDbItem` |
| `tableRenamed(DamengTableTreeItem, DamengSchemaTreeItem)` | 表已重命名 | 构造 `DamengTableRenamedEvent` |
| `tableCleared(DamengTableTreeItem, DamengSchemaTreeItem)` | 表已清空 | 构造 `DamengTableClearedEvent` |
| `tableFiltered(DamengTableTreeItem, List<DamengRecordFilter>)` | 表已过滤 | 构造 `DamengTableFilteredEvent`，`setFilters(filters)` |
| `tableTruncated(DamengTableTreeItem, DamengSchemaTreeItem)` | 表已截断 | 构造 `DamengTableTruncatedEvent` |
| `tableDropped(DamengTableTreeItem, DamengSchemaTreeItem)` | 表已删除 | 构造 `DamengTableDroppedEvent` |
| `designTable(DamengTable, DamengSchemaTreeItem)` | 设计表 | 构造 `DamengTableDesignEvent`，`data(table)` |
| `procedureAdded(DamengSchemaTreeItem)` | 存储过程已新增 | 构造 `DamengProcedureAddedEvent` |
| `procedureAlerted(String, DamengSchemaTreeItem)` | 存储过程已刷新 | 构造 `DamengProcedureAlertedEvent` |
| `designProcedure(DamengProcedure, DamengSchemaTreeItem)` | 设计存储过程 | 构造 `DamengProcedureDesignEvent` |
| `functionAdded(DamengSchemaTreeItem)` | 函数已新增 | 构造 `DamengFunctionAddedEvent` |
| `functionAlerted(String, DamengSchemaTreeItem)` | 函数已刷新 | 构造 `DamengFunctionAlertedEvent` |
| `designFunction(DamengFunction, DamengSchemaTreeItem)` | 设计函数 | 构造 `DamengFunctionDesignEvent` |
| `viewAdded(DamengSchemaTreeItem)` | 视图已新增 | 构造 `DamengViewAddedEvent` |
| `viewAlerted(String, DamengSchemaTreeItem)` | 视图已刷新 | 构造 `DamengViewAlertedEvent` |
| `viewRenamed(DamengViewTreeItem, DamengSchemaTreeItem)` | 视图已重命名 | 构造 `DamengViewRenamedEvent` |
| `viewFiltered(DamengViewTreeItem, List<DamengRecordFilter>)` | 视图已过滤 | 构造 `DamengViewFilteredEvent`，`setFilters(filters)` |
| `viewOpen(DamengViewTreeItem, DamengSchemaTreeItem)` | 视图打开 | 构造 `DamengViewOpenEvent` |
| `designView(DamengView, DamengSchemaTreeItem)` | 设计视图 | 构造 `DamengViewDesignEvent`，`data(dbView)` |
| `schemaAdded(DBConnectTreeItem, DamengSchema)` | 数据库已新增 | 构造 `DamengSchemaAddedEvent`，`setConnectItem` |
| `schemaUpdated(DBConnectTreeItem, DamengSchema)` | 数据库已修改 | 构造 `DamengSchemaUpdatedEvent`，`setConnectItem` |
| `schemaClosed(DamengSchemaTreeItem)` | 数据库已关闭 | 构造 `DamengSchemaClosedEvent` |
| `schemaDropped(DamengSchemaTreeItem)` | 数据库已删除 | 构造 `DamengSchemaDroppedEvent` |
| `queryAdd(DamengSchemaTreeItem)` | 查询添加 | 构造 `DamengQueryAddEvent` |
| `queryAdded(DamengQuery, DamengSchemaTreeItem)` | 查询已新增 | 构造 `DamengQueryAddedEvent`，`data(query)` |
| `queryDeleted(DamengQueryTreeItem)` | 查询已删除 | 构造 `DamengQueryDeletedEvent` |
| `queryOpen(DamengQuery, DamengSchemaTreeItem)` | 查询打开 | 构造 `DamengQueryOpenEvent` |
| `queryRenamed(DamengQuery, DamengSchemaTreeItem)` | 查询已重命名 | 构造 `DamengQueryRenamedEvent` |
| `connectionClosed(ShellDamengClient)` | 连接关闭 | 构造 `DBConnectionClosedEvent`，`data(client)` |
| `connectionConnected(ShellDamengClient)` | 连接成功 | 构造 `DBConnectionConnectedEvent` |
| `terminalOpen(ShellDamengClient, String)` | 终端打开 | 构造 `DBTerminalOpenEvent`，`setSchema(schema)` |
| `terminalClose(ShellDamengClient)` | 终端关闭 | 构造 `DBTerminalCloseEvent` |
| `connectAdded(DamengConnect)` | 连接已新增 | 构造 `DamengConnectAddedEvent` |
| `connectUpdated(DamengConnect)` | 连接已修改 | 构造 `DamengConnectUpdatedEvent` |
| `connectDeleted(DamengConnect)` | 连接已删除 | 构造 `DamengConnectDeletedEvent` |
| `infoDeleted(DamengConnect)` | 连接已删除（历史命名） | 构造 `DamengConnectDeletedEvent` |
| `addConnect()` | 添加连接 | 直接 `post(new DBAddConnectEvent())` |
| `addGroup()` | 添加分组 | 直接 `post(new DBAddGroupEvent())` |
| `changelog()` | 更新日志 | `post(new ChangelogEvent())`（第三方事件） |
| `layout1()` | 布局1 | `post(new Layout1Event())`（第三方事件） |
| `layout2()` | 布局2 | `post(new Layout2Event())`（第三方事件） |
| `treeItemChanged(TreeItem<?>)` | 节点选中 | 构造 `DamengTreeItemChangedEvent` |
| `showMessage()` | 显示消息页面 | `post(new ShellShowMessageEvent())` |
| `printSql(String, DamengConnect)` | 打印SQL | 构造 `ShellPrintSqlEvent`，`data(sql)`、`setConnect(connect)` |

- 调用链：
  - `业务动作 → DamengEventUtil.tableOpen → DamengTableOpenEvent → EventUtil.post`
  - `业务动作 → DamengEventUtil.printSql → ShellPrintSqlEvent.eventFormat → EventUtil.post`
  - `业务动作 → DamengEventUtil.schemaAdded → DamengSchemaAddedEvent → EventUtil.post`

---

## connect 子包

### DBAddConnectEvent
- 职责：添加连接事件（空事件，仅作信号）。
- 字段：无（载荷类型 `Object`）
- 方法：无
- 调用链：`DamengEventUtil.addConnect → DBAddConnectEvent → EventUtil.post`

### DBConnectionClosedEvent
- 职责：数据库连接已关闭事件，携带达梦客户端。
- 字段：无（载荷类型 `ShellDamengClient`）
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `eventFormat()` | 生成事件文本 | `[%s] 客户端已断开`，取 `data().connectName()` |
| `dbConnect()` | 获取数据库连接 | 返回 `data().getDbConnect()` |
| `isDamengType()` | 是否达梦类型 | `data().dialect() == DBDialect.DAMENG` |

- 调用链：`DamengEventUtil.connectionClosed → DBConnectionClosedEvent.eventFormat`

### DBConnectionConnectedEvent
- 职责：数据库连接已连接事件，携带达梦客户端。
- 字段：无（载荷类型 `ShellDamengClient`）
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `eventFormat()` | 生成事件文本 | `[%s] 客户端已连接`，取 `data().connectName()` |
| `dbConnect()` | 获取数据库连接 | 返回 `data().getDbConnect()` |

- 调用链：`DamengEventUtil.connectionConnected → DBConnectionConnectedEvent.eventFormat`

### DamengConnectAddedEvent
- 职责：达梦连接已新增事件。
- 字段：无（载荷类型 `DamengConnect`）
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `eventFormat()` | 生成事件文本 | `连接[%s] 已新增`，取 `data().getName()` |

- 调用链：`DamengEventUtil.connectAdded → DamengConnectAddedEvent.eventFormat`

### DamengConnectDeletedEvent
- 职责：达梦连接已删除事件。
- 字段：无（载荷类型 `DamengConnect`）
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `eventFormat()` | 生成事件文本 | `连接[%s] 已删除`，取 `data().getName()` |

- 调用链：`DamengEventUtil.connectDeleted / infoDeleted → DamengConnectDeletedEvent.eventFormat`

### DamengConnectUpdatedEvent
- 职责：达梦连接已修改事件。
- 字段：无（载荷类型 `DamengConnect`）
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `eventFormat()` | 生成事件文本 | `连接[%s] 已修改`，取 `data().getName()` |

- 调用链：`DamengEventUtil.connectUpdated → DamengConnectUpdatedEvent.eventFormat`

---

## function 子包

### DamengFunctionAddedEvent
- 职责：达梦函数已新增事件。
- 字段：无（载荷类型 `DamengSchemaTreeItem`）
- 方法：无
- 调用链：`DamengEventUtil.functionAdded → DamengFunctionAddedEvent → EventUtil.post`

### DamengFunctionAlertedEvent
- 职责：达梦函数已刷新事件，携带函数名与数据库树节点。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 数据库（模式）树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写数据库树节点 | 普通存取 |

- 调用链：`DamengEventUtil.functionAlerted → DamengFunctionAlertedEvent → EventUtil.post`

### DamengFunctionDesignEvent
- 职责：达梦函数设计事件，携带函数对象与数据库树节点。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 数据库（模式）树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `functionName()` | 获取函数名称 | `data().getName()` |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写数据库树节点 | 普通存取 |

- 调用链：`DamengEventUtil.designFunction → DamengFunctionDesignEvent.functionName`

---

## group 子包

### DBAddGroupEvent
- 职责：添加分组事件（空事件，仅作信号）。
- 字段：无（载荷类型 `Object`）
- 方法：无
- 调用链：`DamengEventUtil.addGroup → DBAddGroupEvent → EventUtil.post`

---

## procedure 子包

### DamengProcedureAddedEvent
- 职责：达梦存储过程已新增事件。
- 字段：无（载荷类型 `DamengSchemaTreeItem`）
- 方法：无
- 调用链：`DamengEventUtil.procedureAdded → DamengProcedureAddedEvent → EventUtil.post`

### DamengProcedureAlertedEvent
- 职责：达梦存储过程已刷新事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 数据库（模式）树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写数据库树节点 | 普通存取 |

- 调用链：`DamengEventUtil.procedureAlerted → DamengProcedureAlertedEvent → EventUtil.post`

### DamengProcedureDesignEvent
- 职责：达梦存储过程设计事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 数据库（模式）树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `procedureName()` | 获取存储过程名称 | `data().getName()` |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写数据库树节点 | 普通存取 |

- 调用链：`DamengEventUtil.designProcedure → DamengProcedureDesignEvent.procedureName`

---

## query 子包

### DamengQueryAddEvent
- 职责：达梦查询添加事件。
- 字段：无（载荷类型 `DamengSchemaTreeItem`）
- 方法：无
- 调用链：`DamengEventUtil.queryAdd → DamengQueryAddEvent → EventUtil.post`

### DamengQueryAddedEvent
- 职责：达梦查询已新增事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 数据库（模式）树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写数据库树节点 | 普通存取 |

- 调用链：`DamengEventUtil.queryAdded → DamengQueryAddedEvent → EventUtil.post`

### DamengQueryDeletedEvent
- 职责：达梦查询已删除事件。
- 字段：无（载荷类型 `DamengQueryTreeItem`）
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `queryId()` | 获取查询唯一标识 | `data().value().getUid()` |

- 调用链：`DamengEventUtil.queryDeleted → DamengQueryDeletedEvent.queryId`

### DamengQueryOpenEvent
- 职责：达梦查询打开事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 数据库（模式）树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `queryId()` | 获取查询唯一标识 | `data().getUid()` |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写数据库树节点 | 普通存取 |

- 调用链：`DamengEventUtil.queryOpen → DamengQueryOpenEvent.queryId`

### DamengQueryRenamedEvent
- 职责：达梦查询已重命名事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 数据库（模式）树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `queryName()` | 获取查询名称 | `data().getName()` |
| `queryId()` | 获取查询唯一标识 | `data().getUid()` |
| `schema()` | 获取数据库模式 | `dbItem.schema()` |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写数据库树节点 | 普通存取 |

- 调用链：`DamengEventUtil.queryRenamed → DamengQueryRenamedEvent.schema`

---

## schema 子包

### DamengSchemaAddedEvent
- 职责：达梦数据库已新增事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `connectItem` | `DBConnectTreeItem` | 连接树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `eventFormat()` | 生成事件文本 | `[%s] 数据库已新增`，取 `data().getName()` |
| `getConnectItem()` / `setConnectItem(DBConnectTreeItem)` | 读写连接树节点 | 普通存取 |

- 调用链：`DamengEventUtil.schemaAdded → DamengSchemaAddedEvent.eventFormat`

### DamengSchemaUpdatedEvent
- 职责：达梦数据库已修改事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `connectItem` | `DBConnectTreeItem` | 连接树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `eventFormat()` | 生成事件文本 | `[%s] 数据库已修改`，取 `data().getName()` |
| `getConnectItem()` / `setConnectItem(DBConnectTreeItem)` | 读写连接树节点 | 普通存取 |

- 调用链：`DamengEventUtil.schemaUpdated → DamengSchemaUpdatedEvent.eventFormat`

### DamengSchemaClosedEvent
- 职责：达梦数据库已关闭事件。
- 字段：无（载荷类型 `DamengSchemaTreeItem`）
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `eventFormat()` | 生成事件文本 | `[%s] 数据库已关闭`，取 `data().value()` |

- 调用链：`DamengEventUtil.schemaClosed → DamengSchemaClosedEvent.eventFormat`

### DamengSchemaDroppedEvent
- 职责：达梦数据库已删除事件。
- 字段：无（载荷类型 `DamengSchemaTreeItem`）
- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `eventFormat()` | 生成事件文本 | `[%s] 数据库已删除`，取 `data().schema()` |

- 调用链：`DamengEventUtil.schemaDropped → DamengSchemaDroppedEvent.eventFormat`

---

## sql 子包

### ShellPrintSqlEvent
- 职责：打印 SQL 事件，携带 SQL 文本与数据库连接。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `connect` | `DamengConnect` | 数据库连接 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `setConnect(DamengConnect)` | 设置数据库连接 | 普通存取 |
| `eventFormat()` | 生成事件文本 | `" " + connect.getName() + " > " + data()` |

- 调用链：`DamengEventUtil.printSql → ShellPrintSqlEvent.eventFormat`

---

## table 子包

### DamengTableAddedEvent
- 职责：达梦表新增事件。
- 字段：无（载荷类型 `DamengSchemaTreeItem`）
- 方法：无
- 调用链：`DamengEventUtil.tableAdded → DamengTableAddedEvent → EventUtil.post`

### DamengTableAlertedEvent
- 职责：达梦表修改（刷新）事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 模式树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写模式树节点 | 普通存取 |

- 调用链：`DamengEventUtil.tableAlerted → DamengTableAlertedEvent → EventUtil.post`

### DamengTableClearedEvent
- 职责：达梦表数据清空事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 模式树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `tableName()` | 获取表名称 | `data().tableName()` |
| `schema()` | 获取模式 | `dbItem.schema()` |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写模式树节点 | 普通存取 |

- 调用链：`DamengEventUtil.tableCleared → DamengTableClearedEvent.tableName`

### DamengTableDesignEvent
- 职责：达梦表设计事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 模式树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `schema()` | 获取模式 | `dbItem.schema()` |
| `tableName()` | 获取表名称 | `data().getName()` |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写模式树节点 | 普通存取 |

- 调用链：`DamengEventUtil.designTable → DamengTableDesignEvent.tableName`

### DamengTableDroppedEvent
- 职责：达梦表删除事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 模式树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `tableName()` | 获取表名称 | `data().tableName()` |
| `schema()` | 获取模式 | `dbItem.schema()` |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写模式树节点 | 普通存取 |

- 调用链：`DamengEventUtil.tableDropped → DamengTableDroppedEvent.tableName`

### DamengTableFilteredEvent
- 职责：达梦表数据过滤事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `filters` | `List<DamengRecordFilter>` | 过滤条件集合 |
| `dbItem` | `DamengSchemaTreeItem` | 模式树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `tableName()` | 获取表名称 | `data().tableName()` |
| `getFilters()` / `setFilters(List<DamengRecordFilter>)` | 读写过滤条件集合 | 普通存取 |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写模式树节点 | 普通存取 |

- 调用链：`DamengEventUtil.tableFiltered → DamengTableFilteredEvent.getFilters`

### DamengTableOpenEvent
- 职责：达梦表打开事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 模式树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `tableName()` | 获取表名称 | `data().tableName()` |
| `schema()` | 获取模式 | `dbItem.schema()` |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写模式树节点 | 普通存取 |

- 调用链：`DamengEventUtil.tableOpen → DamengTableOpenEvent.tableName`

### DamengTableRenamedEvent
- 职责：达梦表重命名事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 模式树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `tableName()` | 获取表名称 | `data().tableName()` |
| `schema()` | 获取模式 | `dbItem.schema()` |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写模式树节点 | 普通存取 |

- 调用链：`DamengEventUtil.tableRenamed → DamengTableRenamedEvent.tableName`

### DamengTableTruncatedEvent
- 职责：达梦表截断事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 模式树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `tableName()` | 获取表名称 | `data().tableName()` |
| `schema()` | 获取模式 | `dbItem.schema()` |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写模式树节点 | 普通存取 |

- 调用链：`DamengEventUtil.tableTruncated → DamengTableTruncatedEvent.tableName`

---

## terminal 子包

### DBTerminalCloseEvent
- 职责：数据库终端关闭事件。
- 字段：无（载荷类型 `ShellDamengClient`）
- 方法：无
- 调用链：`DamengEventUtil.terminalClose → DBTerminalCloseEvent → EventUtil.post`

### DBTerminalOpenEvent
- 职责：数据库终端打开事件，携带模式名。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `schema` | `String` | 模式名 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getSchema()` / `setSchema(String)` | 读写模式名 | 普通存取 |

- 调用链：`DamengEventUtil.terminalOpen → DBTerminalOpenEvent.getSchema`

---

## tree 子包

### DamengTreeItemChangedEvent
- 职责：达梦树节点变更（选中）事件。
- 字段：无（载荷类型 `TreeItem<?>`）
- 方法：无
- 调用链：`DamengEventUtil.treeItemChanged → DamengTreeItemChangedEvent → EventUtil.post`

---

## view 子包

### DamengViewAddedEvent
- 职责：达梦视图新增事件。
- 字段：无（载荷类型 `DamengSchemaTreeItem`）
- 方法：无
- 调用链：`DamengEventUtil.viewAdded → DamengViewAddedEvent → EventUtil.post`

### DamengViewAlertedEvent
- 职责：达梦视图修改（刷新）事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 模式树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写模式树节点 | 普通存取 |

- 调用链：`DamengEventUtil.viewAlerted → DamengViewAlertedEvent → EventUtil.post`

### DamengViewDesignEvent
- 职责：达梦视图设计事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 模式树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `viewName()` | 获取视图名称 | `data().getName()` |
| `schema()` | 获取模式 | `dbItem.schema()` |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写模式树节点 | 普通存取 |

- 调用链：`DamengEventUtil.designView → DamengViewDesignEvent.viewName`

### DamengViewFilteredEvent
- 职责：达梦视图数据过滤事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `filters` | `List<DamengRecordFilter>` | 过滤条件集合 |
| `dbItem` | `DamengSchemaTreeItem` | 模式树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `viewName()` | 获取视图名称 | `data().viewName()` |
| `getFilters()` / `setFilters(List<DamengRecordFilter>)` | 读写过滤条件集合 | 普通存取 |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写模式树节点 | 普通存取 |

- 调用链：`DamengEventUtil.viewFiltered → DamengViewFilteredEvent.getFilters`

### DamengViewOpenEvent
- 职责：达梦视图打开事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 模式树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `viewName()` | 获取视图名称 | `data().viewName()` |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写模式树节点 | 普通存取 |

- 调用链：`DamengEventUtil.viewOpen → DamengViewOpenEvent.viewName`

### DamengViewRenamedEvent
- 职责：达梦视图重命名事件。
- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbItem` | `DamengSchemaTreeItem` | 模式树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `viewName()` | 获取视图名称 | `data().viewName()` |
| `schema()` | 获取模式 | `dbItem.schema()` |
| `getDbItem()` / `setDbItem(DamengSchemaTreeItem)` | 读写模式树节点 | 普通存取 |

- 调用链：`DamengEventUtil.viewRenamed → DamengViewRenamedEvent.viewName`

---

## window 子包

### ShellShowMessageEvent
- 职责：窗口展示消息页面事件（空事件，仅作信号）。
- 字段：无（载荷类型 `Object`）
- 方法：无
- 调用链：`DamengEventUtil.showMessage → ShellShowMessageEvent → EventUtil.post`
