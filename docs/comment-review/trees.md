# trees 包代码审查

达梦客户端的数据库对象导航树（`cn.oyzh.easydameng.trees`），基于 `cn.oyzh.fx.gui.tree.view` 的 `RichTreeItem` / `RichTreeItemValue` / `RichTreeView`。层级为：根（`DBRootTreeItem`）→ 分组（`DBGroupTreeItem`）→ 连接（`DBConnectTreeItem`）→ 模式（`DamengSchemaTreeItem`）→ 各类型分类节点（表/视图/函数/过程/查询/终端）→ 具体对象节点。节点负责懒加载子级、右键菜单、拖拽、增删改，并统一通过 `DamengEventUtil` 派发业务事件。

> 说明：`trees/DBTreeCell.java`、`trees/DBTreeItemValue.java`、`trees/DamengTreeEventListener.java`、`trees/event/*`（事件树节点 4 个）整文件被注释掉（死代码），未收录。

---

## 核心（core）

## DBTreeItem
- 职责：基础树节点抽象基类，统一树视图类型。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DBTreeItem(RichTreeView)` | 构造 | 委托父类 |
  | `getTreeView()` | 取树视图 | 强转为 `DBTreeView` |

- 调用链：`DBTreeItem.getTreeView → DBTreeView`

## DBTreeView
- 职责：数据库树视图组件，创建根节点并订阅连接/分组相关事件驱动树更新。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `searching` | `volatile boolean` | 搜索中标志位（相关事件处理已注释，字段保留） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getItemFilter()` | 取/建过滤器 | 懒创建 `DBTreeItemFilter` |
  | `DBTreeView()` | 构造 | 设拖拽标识、单选、`RichTreeCell` 行工厂，根为 `DBRootTreeItem` 并展开 |
  | `root()` | 取根节点 | 强转 `DBRootTreeItem` |
  | `closeConnects()` | 关闭所有连接 | 遍历 `root().getConnectedItems()` 线程内 `closeConnect` |
  | `onInfoUpdate(DamengConnectUpdatedEvent)` | 连接修改事件 | 遍历根（含分组）定位并刷新对应 `DBConnectTreeItem.value` |
  | `addConnect(DBAddConnectEvent)` | 添加连接事件 | `StageManager.showStage(DamengConnectAddController)` |
  | `addGroup(DBAddGroupEvent)` | 添加分组事件 | `root().addGroup()` |
  | `infoAdded(DamengConnectAddedEvent)` | 连接新增事件 | `root().addConnect(event.data())` |
  | `infoUpdated(DamengConnectUpdatedEvent)` | 连接变更事件 | `root().infoUpdate(event.data())` |

- 调用链：`DBTreeView.addGroup → DBRootTreeItem.addGroup → DamengGroupStore.insert`

## DBTreeItemFilter
- 职责：树节点过滤器，保留“仅看收藏”开关。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `onlyCollect` | `boolean` | 是否仅看收藏 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `test(RichTreeItem)` | 过滤判断 | 不可过滤节点直接放行；搜索匹配逻辑已注释，当前恒返回 true |
  | `isOnlyCollect()/setOnlyCollect()` | 读写开关 | getter/setter |

- 调用链：无。

---

## 根节点（root）

## DBRootTreeItem
- 职责：树根节点，加载分组/连接子节点，实现 `DBConnectManager` 提供连接管理、导入导出与拖放。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `connectStore` | `DamengConnectStore` | 连接存储（单例） |
  | `groupStore` | `DamengGroupStore` | 分组存储（单例） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DBRootTreeItem(DBTreeView)` | 构造 | 设值并 `initChildes()` |
  | `initChildes()` | 初始化子节点 | 加载分组与连接生成对应树节点 |
  | `getMenuItems()` | 右键菜单 | 添加连接、导出/导入连接、添加分组（无子节点时禁用导出） |
  | `exportConnect()` | 导出连接 | `DamengInfoExport.fromConnects` 写 JSON 文件 |
  | `importConnect()/dragFile(List<File>)` | 导入连接 | 选文件/拖拽后 `parseConnect` |
  | `parseConnect(File)` | 解析连接文件 | 校验 json 后逐条 `connectStore.insert` 并 `addConnect` |
  | `addConnect()` | 弹出新增连接窗口 | `StageManager.showStage(DamengConnectAddController)` |
  | `addGroup()` | 新增分组 | 提示取名、查重、`groupStore.insert` 后加子节点 |
  | `getGroupItem(String)`/`getGroupItems()` | 查分组节点 | 按 `gid` 过滤 |
  | `infoAdd(DamengConnect)`/`infoUpdate(DamengConnect)` | 连接新增/变更 | `addConnect` / 遍历刷新连接节点值 |
  | `addConnect(...)` 等 `DBConnectManager` 实现 | 连接增删查 | `addConnectItem/addConnectItems/delConnectItem/getConnectItems/getConnectedItems` |
  | `allowDrop()/allowDropNode()/onDropNode()` | 拖放 | 仅接受 `DBConnectTreeItem`，放入根并清空分组 id |

- 调用链：`DBRootTreeItem.addGroup → DamengGroupStore.insert → DBGroupTreeItem`；`initChildes → DamengConnectStore.load → DBConnectTreeItem`

## DBRootTreeItemValue
- 职责：根节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `name()` | 显示名 | `I18nHelper.schema()` |
  | `graphic()` | 图标 | 懒创建 `SchemaSVGGlyph` |

- 调用链：无。

---

## 分组（group）

## DBGroupTreeItem
- 职责：分组节点，管理组内连接并支持重命名/删除/拖入，展开状态持久化。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `value` | `DamengGroup` | 分组对象 |
  | `connectStore` | `DamengConnectStore` | 连接存储 |
  | `groupStore` | `DamengGroupStore` | 分组存储 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DBGroupTreeItem(DamengGroup, DBTreeView)` | 构造 | 设值、按 `isExpand()` 展开，注册展开/收起事件持久化 |
  | `getMenuItems()` | 右键菜单 | 添加连接、重命名、删除分组 |
  | `rename()` | 重命名 | 提示输入、查重、`groupStore.update` 后刷新 |
  | `delete()` | 删除分组 | 确认后 `groupStore.delete`，子连接转移到父节点 |
  | `addConnect()` | 弹出新增连接窗口 | 传入 `group` 属性 |
  | `parent()` | 父节点 | 强转 `DBRootTreeItem` |
  | `addConnect/addConnectItem/addConnectItems/delConnectItem/getConnectItems` | 连接管理 | 维护分组 id 与存储 |
  | `allowDrop()/allowDropNode()/onDropNode()` | 拖放 | 仅接受非同组连接 |
  | `value()` | 取分组对象 | getter |

- 调用链：`DBGroupTreeItem.delete → DamengGroupStore.delete → DBRootTreeItem.addConnectItems`

## DBGroupTreeItemValue
- 职责：分组节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `item()` | 取所属节点 | 强转 `DBGroupTreeItem` |
  | `name()` | 显示名 | `item().value().getName()` |
  | `graphic()` | 图标 | `GroupSVGGlyph`（禁用主题） |
  | `graphicColor()` | 图标颜色 | 有子节点时 `DEEPSKYBLUE` |

- 调用链：无。

---

## 连接（connect）

## DBConnectTreeItem
- 职责：连接节点，管理数据库连接生命周期（连接/关闭/取消）、加载模式子节点及增删改连接。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `value` | `DamengConnect` | 连接信息 |
  | `client` | `ShellDamengClient` | 达梦客户端 |
  | `canceled` | `boolean` | 已取消操作标志 |
  | `connectStore` | `DamengConnectStore` | 连接存储 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DBConnectTreeItem(DamengConnect, DBTreeView)` | 构造 | `value(value)` 创建客户端与节点值 |
  | `value(DamengConnect)/value()` | 读写连接 | 设置时 `DBClientUtil.newClient` 并刷新节点值 |
  | `connect()` | 连接 | `TaskBuilder` 内 `client.start()`，成功 `loadChild`、失败提示 |
  | `cancelConnect()` | 取消连接 | 置 `canceled` 后线程内 `client.close` |
  | `closeConnect()`/`closeConnect(boolean)` | 关闭连接 | 视是否需要等待动画执行 `client.close` 并清子节点 |
  | `reloadChild()`/`loadChild()` | 加载模式 | `client.schemas()` 生成 `DamengSchemaTreeItem`，并 `ShellDamengQueryUtil.updateIndex` |
  | `getMenuItems()` | 右键菜单 | 按 连接中/已连接/未连接 三态给出取消/关闭/编辑/重连/新增模式/重命名/删除菜单 |
  | `addSchema()`/`addSchema(String)` | 新增模式 | 弹窗后 `client.schema(name)` 加子节点 |
  | `editConnect()`/`repeatConnect()`/`delete()`/`rename()` | 编辑/复制/删除/重命名 | 分别弹窗、复制并入库、删库事件、重命名入库 |
  | `isConnected()/isConnecting()` | 连接状态 | 转发 `client` |
  | `connectManager()` | 取连接管理器 | 父节点实现 `DBConnectManager` |
  | `onPrimaryDoubleClick()` | 双击 | 未连接则连接 |
  | `existSchema/createSchema/alterSchema/dropSchema` | 模式操作 | 转发 `client` |
  | `allowDrag()`/`type()`/`getClient()` | 其他 | 可拖拽、类型、客户端 getter |

- 调用链：`DBConnectTreeItem.connect → ShellDamengClient.start → loadChild → DamengSchemaTreeItem`

## DBConnectTreeItemValue
- 职责：连接节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `item()` | 取所属节点 | 强转 `DBConnectTreeItem` |
  | `name()` | 显示名 | `item().value().getName()` |
  | `graphic()` | 图标 | `DamengSVGGlyph`（禁用主题） |
  | `graphicColor()` | 图标颜色 | 有子节点时 `GREEN` |

- 调用链：无。

---

## 模式（schema）

## DamengSchemaTreeItem
- 职责：模式节点，加载类型分类子节点（表/视图/函数/过程/查询/终端）并作为模式级数据库操作的对外入口（大量转发 `ShellDamengClient`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `value` | `DamengSchema` | 模式对象 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengSchemaTreeItem(DamengSchema, RichTreeView)` | 构造 | 设值、不可排序、可过滤 |
  | `value()/schema()/userName()` | 元信息 | 模式对象/名称/用户名 |
  | `parent()` | 父节点 | 强转 `DBConnectTreeItem` |
  | `getMenuItems()` | 右键菜单 | 关闭/编辑/删除数据库、转储、运行 SQL 文件 |
  | `runSqlFile()/dump()/editDB()/closeDB()` | 功能入口 | 弹对应控制器窗口；关闭时清子节点与加载标志并发事件 |
  | `delete()` | 删除模式 | 确认后 `parent().dropSchema` 并发 `schemaDropped` |
  | `loadChild()` | 加载子节点 | 生成 `DamengTablesTreeItem`、`DamengViewsTreeItem`、`DamengFunctionsTreeItem`、`DamengProceduresTreeItem`、`DamengQueriesTreeItem`、`DamengTerminalTreeItem` |
  | `getTableTypeChild/getTableChild/getQueryTypeChild/getFunctionTypeChild/getFunctionChild/getProcedureTypeChild/getProcedureChild/getViewTypeChild/getViewChild` | 分类/对象子节点查询 | 遍历子节点按类型筛选 |
  | `client()`/`info()`/`infoName()`/`connectName()` | 连接上下文 | 转发父连接节点 |
  | `onPrimaryDoubleClick()` | 双击 | 未加载则 `loadChild` |
  | `createTable/alterTable/createTableParam/alterTableParam/selectePrimaryKeys/existAutoIncrement/existTable/renameTable/clearTable/truncateTable/dropTable` | 表操作 | 构造参数后转发 `client` |
  | `executeSql/executeSingleSql/explainSql` | SQL 执行 | 转发 `client` |
  | `createFunction/alertFunction/dropFunction/selectFunction` | 函数操作 | 构造参数后转发 `client` |
  | `createProcedure/alertProcedure/dropProcedure/selectProcedure` | 过程操作 | 构造参数后转发 `client` |
  | `createView/alertView/dropView/selectView/existView` | 视图操作 | 构造参数后转发 `client` |
  | `selectRecord/deleteRecord/columns/indexes/foreignKeys/checks/triggers/cloneTable/dbConnect/tableSize/viewSize/dialect/isSupportCheckFeature` | 记录与元数据 | 转发 `client` |
  | `itemVisible()` | 可见性 | `isVisible()` |

- 调用链：`DamengSchemaTreeItem.loadChild → DamengTablesTreeItem/DamengViewsTreeItem/... `；`createTable → ShellDamengClient.createTable`

## DamengSchemaTreeItemValue
- 职责：模式节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `item()` | 取所属节点 | 强转 `DamengSchemaTreeItem` |
  | `name()` | 显示名 | `item().schema()` |
  | `graphic()` | 图标 | `SchemaSVGGlyph`（禁用主题） |
  | `graphicColor()` | 图标颜色 | 有子节点时 `GREEN` |

- 调用链：无。

---

## 终端（terminal）

## DamengTerminalTreeItem
- 职责：终端节点，双击打开该模式终端。
- 字段：无（直接继承 `RichTreeItem`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengTerminalTreeItem(RichTreeView)` | 构造 | 设节点值 `DamengTerminalTreeItemValue` |
  | `parent()` | 父节点 | 强转 `DamengSchemaTreeItem` |
  | `shellConnect()` | 所属连接 | `parent().info()` |
  | `client()` | 客户端 | `parent().client()` |
  | `onPrimaryDoubleClick()` | 双击 | `DamengEventUtil.terminalOpen(client, schema)` |

- 调用链：`DamengTerminalTreeItem.onPrimaryDoubleClick → DamengEventUtil.terminalOpen`

## DamengTerminalTreeItemValue
- 职责：终端节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `graphic()` | 图标 | `TerminalSVGGlyph` |
  | `name()` | 显示名 | `I18nHelper.terminal()` |

- 调用链：无。

---

## 表（table）

## DamengTablesTreeItem
- 职责：表分类节点，懒加载表列表并支持新增/重载/导入导出数据。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengTablesTreeItem(RichTreeView)` | 构造 | 可过滤、设节点值 |
  | `parent()` | 父节点 | 强转 `DamengSchemaTreeItem` |
  | `getMenuItems()` | 右键菜单 | 新增表、重载、导出数据、导入数据 |
  | `exportData()/importData()` | 数据导入导出 | 弹 `ShellDamengDataExportController` / `ShellDamengDataImportController` |
  | `addTable()` | 新增表 | `DamengEventUtil.designTable` |
  | `loadChild()` | 加载表列表 | 空则批量新增，非空则做增/删/改差异同步 |
  | `reloadChild()` | 重载 | 清子节点后重新加载 |
  | `schema()/client()/tableSize()/info()/infoName()` | 上下文 | 转发父模式节点 |
  | `onPrimaryDoubleClick()` | 双击 | 未加载则加载 |
  | `addTable(DamengTable)` | 添加表节点 | 追加并排序 |
  | `itemVisible()` | 可见性 | `isVisible()` |

- 调用链：`DamengTablesTreeItem.loadChild → ShellDamengClient.selectTablesSimple → DamengTableTreeItem`；`addTable → DamengEventUtil.designTable`

## DamengTablesTreeItemValue
- 职责：表分类节点显示值（名称+数量）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `item()` | 取所属节点 | 强转 `DamengTablesTreeItem` |
  | `name()` | 显示名 | `I18nHelper.table()` |
  | `graphic()/graphicColor()` | 图标/颜色 | `TableSVGGlyph`；有子节点绿色 |
  | `extra()/extraColor()` | 数量后缀 | ` (n)`，色 `#228B22` |

- 调用链：无。

## DamengTableTreeItem
- 职责：表节点，提供表设计/重命名/清空/截断/删除、导入导出、克隆及记录增删改查等操作。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `value` | `DamengTable` | 表对象 |
  | `columns` | `DamengColumns` | 表列缓存 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengTableTreeItem(DamengTable, RichTreeView)` | 构造 | 设值、设节点值 |
  | `parent()/client()/schema()/tableName()/info()/infoName()` | 上下文 | 转发父节点 |
  | `getMenuItems()` | 右键菜单 | 打开/设计/重命名/清空/截断/删除/转储/导出/表信息/克隆表 |
  | `designTable()` | 设计表 | `reloadChild` 后 `DamengEventUtil.designTable` |
  | `rename()` | 重命名表 | 校验后 `dbItem().renameTable` 并刷新、发事件 |
  | `clearTableData()/truncateTable()/delete()` | 清空/截断/删除 | 确认后调 `dbItem()` 对应方法并发事件 |
  | `cloneTable(boolean)/doCloneTable(boolean)` | 克隆表 | `dbItem().cloneTable` 后加入表分类 |
  | `dump()/export()/tableInfo()` | 转储/导出/信息 | 弹对应控制器 |
  | `dbItem()` | 所属模式节点 | `parent().parent()` |
  | `recordPage(...)` | 分页查询记录 | 组装 `DamengSelectRecordParam` 调 `selectRecords`/`selectRecordCount` |
  | `columns()/indexes()/checks()/foreignKeys()/triggers()` | 元数据 | 转发 `client` |
  | `getPrimaryKey()/hasPrimaryKey()` | 主键 | 优先自动递增主键 |
  | `insertRecord/deleteRecord/selectRecord/updateRecord` | 记录 CRUD | 组装参数转发 `client` |
  | `loadChild()/reloadChild()` | 刷新表定义 | `selectTable` 后 `value.copy` |
  | `onPrimaryDoubleClick()` | 双击 | `DamengEventUtil.tableOpen` |
  | `value()` | 取表对象 | getter |

- 调用链：`DamengTableTreeItem.rename → DamengSchemaTreeItem.renameTable → ShellDamengClient.renameTable`；`delete → dropTable → DamengEventUtil.tableDropped`

## DamengTableTreeItemValue
- 职责：表节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `item()` | 取所属节点 | 强转 `DamengTableTreeItem` |
  | `graphic()` | 图标 | `TableSVGGlyph` |
  | `name()` | 显示名 | `item().tableName()` |

- 调用链：无。

---

## 视图（view）

## DamengViewsTreeItem
- 职责：视图分类节点，懒加载视图列表并支持新增/重载。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengViewsTreeItem(RichTreeView)` | 构造 | 可过滤、设节点值 |
  | `getMenuItems()` | 右键菜单 | 新增视图、刷新数据 |
  | `add()` | 新增视图 | `DamengEventUtil.designView` |
  | `loadChild()` | 加载视图列表 | 空批量新增，非空差异同步 |
  | `reloadChild()` | 重载 | 清子节点重载 |
  | `schema()/client()/viewSize()/info()/infoName()` | 上下文 | 转发父模式节点 |
  | `onPrimaryDoubleClick()` | 双击 | 未加载则加载 |
  | `addView(DamengView)` | 添加视图节点 | 追加并排序 |
  | `itemVisible()` | 可见性 | `isVisible()` |

- 调用链：`DamengViewsTreeItem.loadChild → ShellDamengClient.selectViewsSimple → DamengViewTreeItem`

## DamengViewsTreeItemValue
- 职责：视图分类节点显示值（名称+数量）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `item()` | 取所属节点 | 强转 `DamengViewsTreeItem` |
  | `name()` | 显示名 | `I18nHelper.view()` |
  | `graphic()/graphicColor()` | 图标/颜色 | `ViewSVGGlyph`；有子节点绿色 |
  | `extra()/extraColor()` | 数量后缀 | ` (n)`，色 `#228B22` |

- 调用链：无。

## DamengViewTreeItem
- 职责：视图节点，支持打开/设计/重命名/删除视图及视图记录分页查询与增删改。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `value` | `DamengView` | 视图对象 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengViewTreeItem(DamengView, RichTreeView)` | 构造 | 设值、可过滤、设节点值 |
  | `value()/parent()/client()/schema()/info()/infoName()/viewName()` | 上下文 | 转发父节点/视图对象 |
  | `getMenuItems()` | 右键菜单 | 打开/设计/重命名/删除/视图信息 |
  | `viewInfo()/designView()` | 信息/设计 | `ShellDamengViewFactory.viewInfo` / `DamengEventUtil.designView` |
  | `delete()/rename()` | 删除/重命名 | 转发 `dbItem().dropView` / `renameTable` 并发事件 |
  | `dbItem()` | 所属模式节点 | `parent().parent()` |
  | `viewColumns()/columns()` | 视图列 | 转发 `client.viewColumns` |
  | `recordPage(...)` | 分页查询 | `client.viewRecords` + `selectRecordCount` |
  | `getPrimaryKey()/isUpdatable()` | 主键/可更新 | 依赖视图列/对象 |
  | `insertRecord/deleteRecord/selectRecord/updateRecord` | 记录 CRUD | 组装参数转发 `client` |
  | `onPrimaryDoubleClick()` | 双击 | `DamengEventUtil.viewOpen` |

- 调用链：`DamengViewTreeItem.recordPage → ShellDamengClient.viewRecords`；`delete → dropView → DamengEventUtil.view`

## DamengViewTreeItemValue
- 职责：视图节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `item()` | 取所属节点 | 强转 `DamengViewTreeItem` |
  | `graphic()` | 图标 | `ViewSVGGlyph` |
  | `name()` | 显示名 | `item().viewName()` |

- 调用链：无。

---

## 函数（function）

## DamengFunctionsTreeItem
- 职责：函数分类节点，懒加载函数列表并支持新增/重载。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengFunctionsTreeItem(RichTreeView)` | 构造 | 可过滤、设节点值 |
  | `getMenuItems()` | 右键菜单 | 新增函数、刷新数据 |
  | `add()` | 新增函数 | `DamengEventUtil.designFunction` |
  | `loadChild()` | 加载函数列表 | 空批量新增，非空差异同步 |
  | `reloadChild()` | 重载 | 清子节点重载 |
  | `schema()/client()/info()/infoName()/functionSize()` | 上下文 | 转发父模式节点 |
  | `onPrimaryDoubleClick()` | 双击 | 未加载则加载 |
  | `addFunction(DamengFunction)` | 添加函数节点 | 追加并排序 |

- 调用链：`DamengFunctionsTreeItem.loadChild → ShellDamengClient.selectFunctionsSimple → DamengFunctionTreeItem`

## DamengFunctionsTreeItemValue
- 职责：函数分类节点显示值（名称+数量）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `item()` | 取所属节点 | 强转 `DamengFunctionsTreeItem` |
  | `name()` | 显示名 | `I18nHelper.function()` |
  | `graphic()/graphicColor()` | 图标/颜色 | `FunctionSVGGlyph`；有子节点绿色 |
  | `extra()/extraColor()` | 数量后缀 | ` (n)`，色 `#228B22` |

- 调用链：无。

## DamengFunctionTreeItem
- 职责：函数节点，支持设计/删除函数与查看函数信息。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `value` | `DamengFunction` | 函数对象 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengFunctionTreeItem(DamengFunction, RichTreeView)` | 构造 | 设值、可过滤、设节点值 |
  | `parent()/client()/info()/schema()/infoName()/functionName()/value()` | 上下文 | 转发父节点/函数对象 |
  | `getMenuItems()` | 右键菜单 | 设计函数、删除函数、函数信息 |
  | `functionInfo()` | 函数信息 | `ShellDamengViewFactory.functionInfo` |
  | `delete()` | 删除函数 | 确认后 `dbItem().dropFunction` 并移除节点 |
  | `dbItem()` | 所属模式节点 | `parent().parent()` |
  | `onPrimaryDoubleClick()` | 双击 | `DamengEventUtil.designFunction` |
  | `loadChild()/reloadChild()` | 加载函数详情 | `selectFunction` 后 `value.copy` |
  | `onPrimarySingleClick()` | 单击 | 调父类逻辑 |

- 调用链：`DamengFunctionTreeItem.onPrimaryDoubleClick → DamengEventUtil.designFunction`；`delete → DamengSchemaTreeItem.dropFunction`

## DamengFunctionTreeItemValue
- 职责：函数节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `item()` | 取所属节点 | 强转 `DamengFunctionTreeItem` |
  | `graphic()` | 图标 | `FunctionSVGGlyph` |
  | `name()` | 显示名 | `item().functionName()` |

- 调用链：无。

---

## 存储过程（procedure）

## DamengProceduresTreeItem
- 职责：存储过程分类节点，懒加载过程列表并支持新增/重载。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengProceduresTreeItem(RichTreeView)` | 构造 | 可过滤、设节点值 |
  | `getMenuItems()` | 右键菜单 | 新增存储过程、刷新数据 |
  | `add()` | 新增过程 | `DamengEventUtil.designProcedure` |
  | `loadChild()` | 加载过程列表 | 空批量新增，非空差异同步 |
  | `reloadChild()` | 重载 | 清子节点重载 |
  | `schema()/client()/info()/infoName()/procedureSize()` | 上下文 | 转发父模式节点 |
  | `onPrimaryDoubleClick()` | 双击 | 未加载则加载 |
  | `addProcedure(DamengProcedure)` | 添加过程节点 | 追加并排序 |

- 调用链：`DamengProceduresTreeItem.loadChild → ShellDamengClient.selectProceduresSimple → DamengProcedureTreeItem`

## DamengProceduresTreeItemValue
- 职责：存储过程分类节点显示值（名称+数量）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `item()` | 取所属节点 | 强转 `DamengProceduresTreeItem` |
  | `name()` | 显示名 | `I18nHelper.procedure()` |
  | `graphic()/graphicColor()` | 图标/颜色 | `ProcedureSVGGlyph`；有子节点绿色 |
  | `extra()/extraColor()` | 数量后缀 | ` (n)`，色 `#228B22` |

- 调用链：无。

## DamengProcedureTreeItem
- 职责：存储过程节点，支持设计/删除过程与查看过程信息。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `value` | `DamengProcedure` | 存储过程对象 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengProcedureTreeItem(DamengProcedure, RichTreeView)` | 构造 | 设值、可过滤、设节点值 |
  | `value()/parent()/client()/info()/schema()/infoName()/procedureName()` | 上下文 | 转发父节点/过程对象 |
  | `getMenuItems()` | 右键菜单 | 设计过程、删除过程、过程信息 |
  | `procedureInfo()` | 过程信息 | `ShellDamengViewFactory.procedureInfo` |
  | `delete()` | 删除过程 | 确认后 `dbItem().dropProcedure` 并移除节点 |
  | `dbItem()` | 所属模式节点 | `parent().parent()` |
  | `onPrimaryDoubleClick()` | 双击 | `DamengEventUtil.designProcedure` |

- 调用链：`DamengProcedureTreeItem.onPrimaryDoubleClick → DamengEventUtil.designProcedure`

## DamengProcedureTreeItemValue
- 职责：存储过程节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `item()` | 取所属节点 | 强转 `DamengProcedureTreeItem` |
  | `graphic()` | 图标 | `ProcedureSVGGlyph` |
  | `name()` | 显示名 | `item().procedureName()` |

- 调用链：无。

---

## 查询（query）

## DamengQueriesTreeItem
- 职责：查询分类节点，从本地存储加载已保存查询并支持新增/重载。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengQueriesTreeItem(RichTreeView)` | 构造 | 可过滤、设节点值 |
  | `getMenuItems()` | 右键菜单 | 新增查询、刷新数据 |
  | `addQuery()` | 新增查询 | `DamengEventUtil.queryAdd` |
  | `loadChild()` | 加载查询 | `DamengQueryStore.INSTANCE.list(infoId, schema)` 生成 `DamengQueryTreeItem` |
  | `reloadChild()` | 重载 | 清子节点重载 |
  | `addChild(DamengQuery)/addQuery(DamengQuery)` | 添加查询节点 | 追加（可排序） |
  | `schema()/client()/info()/querySize()/dbConnect()` | 上下文 | 转发父模式节点/数量 |
  | `onPrimaryDoubleClick()` | 双击 | 未加载则加载 |
  | `itemVisible()` | 可见性 | `isVisible()` |

- 调用链：`DamengQueriesTreeItem.loadChild → DamengQueryStore.INSTANCE.list → DamengQueryTreeItem`

## DamengQueriesTreeItemValue
- 职责：查询分类节点显示值（名称+数量）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `item()` | 取所属节点 | 强转 `DamengQueriesTreeItem` |
  | `name()` | 显示名 | `I18nHelper.queries()` |
  | `graphic()/graphicColor()` | 图标/颜色 | `QuerySVGGlyph`；有子节点绿色 |
  | `extra()/extraColor()` | 数量后缀 | ` (n)`，色 `#228B22` |

- 调用链：无。

## DamengQueryTreeItem
- 职责：查询节点，支持打开/重命名/删除已保存查询。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `value` | `DamengQuery` | 查询对象 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengQueryTreeItem(DamengQuery, RichTreeView)` | 构造 | 设值、可过滤、设节点值 |
  | `value()/parent()/client()/info()/schema()/queryName()/dbConnect()` | 上下文 | 转发父节点/查询对象 |
  | `getMenuItems()` | 右键菜单 | 打开查询、重命名、删除 |
  | `delete()` | 删除查询 | `DamengQueryStore.INSTANCE.delete` 后移除节点并发 `queryDeleted` |
  | `rename()` | 重命名 | 校验后入库更新并发 `queryRenamed`，失败回滚 |
  | `dbItem()` | 所属模式节点 | `parent().parent()` |
  | `onPrimaryDoubleClick()` | 双击 | `DamengEventUtil.queryOpen` |

- 调用链：`DamengQueryTreeItem.rename → DamengQueryStore.INSTANCE.update → DamengEventUtil.queryRenamed`

## DamengQueryTreeItemValue
- 职责：查询节点显示值。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `item()` | 取所属节点 | 强转 `DamengQueryTreeItem` |
  | `graphic()` | 图标 | `QuerySVGGlyph` |
  | `name()` | 显示名 | `item().queryName()` |

- 调用链：无。
