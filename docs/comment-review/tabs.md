# tabs 包代码审查

达梦客户端主界面的各功能页签（`cn.oyzh.easydameng.tabs`），基于 `cn.oyzh.fx.gui.tabs.RichTab` / `RichTabPane` / `RichTabController`。抽象基类 `DamengTab` 定义标签页资源路径；各功能页签（表/视图/函数/过程/查询/终端/主页/消息）由「Tab 类 + Controller 业务类」成对组成，页签负责标题/图标/关闭确认，Controller 负责数据加载、编辑、保存与预览。

> 说明：`tabs/event/DamengEventDesignTab.java`、`tabs/event/DamengEventDesignTabController.java` 整文件被注释掉（死代码），未收录。

---

## 基类（core）

## DamengTab
- 职责：达梦标签页抽象基类，定义资源基础路径与数据库节点访问入口。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `BASE_PATH` | `String`（静态常量） | 标签页资源基础路径 `"/tabs/"` |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getBasePath()` | 取资源基础路径 | 返回 `BASE_PATH` |
  | `dbItem()`（抽象） | 取数据库节点 | 子类实现，返回 `DamengSchemaTreeItem` |

- 调用链：`子类 url() → getBasePath()`

## DamengTabPane
- 职责：DB 页签面板，管理主页与终端页签、订阅终端开/关事件。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `listener` | `DamengTabEventListener` | 页签事件监听器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `register()/unregister()` | 注册/注销 | 转发给 `listener` 并调用父类 |
  | `flushHomeTab()` | 刷新主页页签 | 无页签则初始化主页，多于一个则关闭主页 |
  | `getHomeTab()/initHomeTab()/closeHomeTab()` | 主页页签管理 | 增删 `DBHomeTab` |
  | `getTerminalTab(client, schema)` | 查终端页签 | 按客户端与模式匹配 |
  | `terminalOpen(DBTerminalOpenEvent)` | 终端打开事件 | 无则新建 `DamengTerminalTab` 并 `init`，选中 |
  | `terminalClose(DBTerminalCloseEvent)` | 终端关闭事件 | 移除该客户端所有终端页签 |

- 调用链：`DBTerminalOpenEvent → DamengTabPane.terminalOpen → DamengTerminalTab.init`

## DamengTabEventListener
- 职责：页签事件监听器，响应表/视图/函数/过程/查询/连接等事件增删切换页签。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `tabPane` | `DamengTabPane` | 所属页签面板 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getTabs/addTab/select/removeTab` | 页签操作 | 转发 `tabPane` |
  | `getDamengTabs()`/`getDamengTabs(dbItem)` | 取页签列表 | 按 `DamengTab`/节点过滤 |
  | `onDamengTableOpen/onDamengTableRenamed/onDamengTableCleared/onDamengTableTruncated/onDamengTableDropped/onDamengTableFiltered/onDamengTableAlerted` | 表相关事件 | 定位 `DamengTableRecordTab`/`DamengTableDesignTab` 后 `init/reload/flushTitle/closeTab` |
  | `onDamengTableDesign` | 表设计事件 | 复用或新建 `DamengTableDesignTab` |
  | `onDamengViewOpen/onDamengViewFiltered/viewAlerted/onViewRenamed` | 视图相关事件 | 定位 `DamengViewRecordTab` 操作 |
  | `onDamengViewDesign` | 视图设计事件 | 复用或新建 `DamengViewDesignTab` |
  | `onDamengQueryAdd/Deleted/Open/Renamed` | 查询相关事件 | 新建/移除/选中 `DamengQueryMainTab` |
  | `onDamengFunctionDesign`/`onDamengProcedureDesign` | 函数/过程设计事件 | 复用或新建对应设计页签 |
  | `onDamengDatabaseClosed/onDamengDatabaseDropped` | 库关闭/删除事件 | 移除该节点全部页签 |
  | `onConnectionClosed(DBConnectionClosedEvent)` | 连接关闭事件 | 移除全部达梦页签 |
  | `showMessage(ShellShowMessageEvent)` | 显示消息事件 | 复用或新建 `ShellMessageTab` 并选中 |

- 调用链：`DamengTableOpenEvent → DamengTabEventListener.onDamengTableOpen → DamengTableRecordTab`

---

## 主页（home）

## DBHomeTab
- 职责：数据库主页页签（不可关闭）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DBHomeTab()` | 构造 | 调用 `flush()` |
  | `url()` | 资源路径 | `/tabs/home/dbHomeTab.fxml` |
  | `flushGraphic()` | 图标 | `HomeSVGGlyph` |
  | `getTabTitle()` | 标题 | i18n `base.title.home` |
  | `initNode()` | 初始化节点 | 不可关闭 |

- 调用链：`DamengTabPane.initHomeTab → DBHomeTab`

## DBHomeTabController
- 职责：主页内容控制器，显示软件/环境信息并提供新增连接、分组、更新日志入口。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `softInfo` | `FXLabel`（@FXML） | 软件信息标签 |
  | `jdkInfo` | `FXLabel`（@FXML） | 环境信息标签 |
  | `project` | `Project` | 项目信息（`Project.load()`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `initialize(...)` | 初始化 | 填充版本与 JVM 名称/版本 |
  | `addConnect()` | 新增连接 | `DamengEventUtil.addConnect()` |
  | `addGroup()` | 添加分组 | `DamengEventUtil.addGroup()` |
  | `changelog()` | 更新日志 | `DamengEventUtil.changelog()` |

- 调用链：`DBHomeTabController.addConnect → DamengEventUtil.addConnect`

---

## 消息（message）

## ShellMessageTab
- 职责：消息页签。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `ShellMessageTab()` | 构造 | `flush()` 并 `ObjectWatcherManager.watch(this)` |
  | `url()` | 资源路径 | `/tabs/message/shellMessageTab.fxml` |
  | `flushGraphic()` | 图标 | `MessageSVGGlyph` |
  | `getTabTitle()` | 标题 | `I18nHelper.message()` |

- 调用链：`DamengTabEventListener.showMessage → ShellMessageTab`

## ShellMessageTabController
- 职责：消息内容控制器，展示事件消息列表并支持搜索/高亮/清空。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `EVENT_MESSAGES` | `ObservableList<EventFormatter>`（静态） | 全局事件消息缓冲 |
  | `changeListener` | `ListChangeListener<EventFormatter>` | 消息变更监听器 |
  | `msgArea` | `RichMsgTextArea`（@FXML） | 消息文本域 |
  | `filter` | `HighlightTextField`（@FXML） | 搜索消息输入框 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `clearMsg()` | 清空消息 | 清空文本域与 `EVENT_MESSAGES` |
  | `onTabInit(FXTab)` | 页签初始化 | 追加已有消息并注册监听，绑定高亮属性到 `filter` |
  | `onTabClosed(Event)` | 页签关闭 | 移除监听器 |
  | `appendMsg(List<EventFormatter>)` | 追加消息 | 逐条 `eventFormat()` 后 `msgArea.appendLines` |
  | `destroy()` | 销毁 | 释放监听器与文本域 |

- 调用链：`事件 → ShellMessageTabController.appendMsg → msgArea.appendLines`

---

## 终端（terminal）

## DamengTerminalTab
- 职责：终端页签，承载命令行终端。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `controller()` | 取控制器 | 强转 `DamengTerminalTabController` |
  | `url()` | 资源路径 | `/tabs/terminal/damengTerminalTab.fxml` |
  | `flushGraphic()` | 图标 | `TerminalSVGGlyph` |
  | `getTabTitle()` | 标题 | 连接名或“未命名连接” |
  | `init(ShellDamengClient, String)` | 初始化 | 客户端为空则新建 `ShellDamengClient`，再 `controller().init` |
  | `shellConnect()/client()/schema()` | 上下文 | 转发控制器 |

- 调用链：`DamengTabPane.terminalOpen → DamengTerminalTab.init → DamengTerminalTabController.init`

## DamengTerminalTabController
- 职责：终端页签内容控制器。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `terminal` | `DamengTerminalPane`（@FXML） | 命令行文本域 |
  | `schema` | `String` | 模式 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(ShellDamengClient, String)` | 初始化 | `terminal.init(client, schema)` |
  | `getSchema()` | 取模式 | getter |
  | `shellConnect()` | 连接信息 | `terminal.shellConnect()` |
  | `client()` | 客户端 | `terminal.getClient()` |
  | `onTabClosed(Event)` | 页签关闭 | 临时终端则关闭客户端 |

- 调用链：`DamengTerminalTabController.init → DamengTerminalPane.init`

---

## 表（table）

## DamengTableDesignTab
- 职责：表设计页签，含未保存关闭确认。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `url()` | 资源路径 | `table/damengTableDesignTab.fxml` |
  | `flushGraphic()/flushTitle()` | 图标/标题 | 表图标；`* 模式-表名` 表示未保存 |
  | `tableName()/schema()` | 元信息 | 转发控制器 |
  | `init(DamengTable, DamengSchemaTreeItem)` | 初始化 | 遮罩内 `controller().init` 并 `flush` |
  | `isUnsaved()` | 是否未保存 | 转发控制器 |
  | `onTabCloseRequest(Event)` | 关闭请求 | 未保存时确认 |
  | `dbItem()` | 数据库节点 | 转发控制器 |

- 调用链：`DamengTabEventListener.onDamengTableDesign → DamengTableDesignTab.init`

## DamengTableDesignTabController
- 职责：表设计业务，管理字段/索引/外键/触发器/检查约束，构建建表/改表参数并预览 SQL。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `tabPane` | `FXTabPane`（@FXML） | 切换面板 |
  | `tableSpace` | `DamengTableSpaceComboBox`（@FXML） | 表空间 |
  | `tableComment` | `FXTextArea`（@FXML） | 注释 |
  | `preview` | `Editor`（@FXML） | SQL 预览 |
  | `columnTable` | `DBStatusTableView<DamengColumnControl>` | 字段表格 |
  | `indexTable` | `DBStatusTableView<DamengIndexControl>` | 索引表格 |
  | `foreignKeyTable` | `DBStatusTableView<DamengForeignKeyControl>` | 外键表格 |
  | `triggerTable` | `DBStatusTableView<DamengTriggerControl>` | 触发器表格 |
  | `checkTable` | `DBStatusTableView<DamengCheckControl>` | 检查约束表格 |
  | `table` | `DamengTable` | 表对象 |
  | `dbItem` | `DamengSchemaTreeItem` | 库节点 |
  | `listener` | `DBStatusListener` | 数据监听器 |
  | `unsaved/newData/initiating` | `boolean` | 状态标志 |
  | `tableColumnExtraController` | `DamengTableColumnExtraController`（@FXML） | 字段额外信息子控制器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(DamengTable, DamengSchemaTreeItem)` | 初始化 | 设 `newData`，遮罩 `doInit` |
  | `doInit()` | 执行初始化 | 初始化监听器、表空间、信息，按特性移除检查页签 |
  | `initInfo()` | 初始化信息 | 新表 `initNew`，否则查表 `initNormal` |
  | `initNormal()` | 回填信息 | 加载注释/表空间及五类控件集合 |
  | `initParam(boolean)` | 构建参数 | 汇总不含无效项及删除项，构造建表/改表参数 |
  | `save()`/`doSave()` | 保存 | 逐类校验有效性，新表 `createTable` 否则 `alterTable`，发事件并刷新 |
  | `refresh()` | 刷新 | 确认后重新 `init` |
  | `addColumn/deleteColumn/addIndex/deleteIndex/addForeignKey/deleteForeignKey/addTrigger/deleteTrigger/addCheck/deleteCheck` | 增删各类 | 标记 `created/deleted` 并维护表格 |
  | `doAdd/doDelete/doMoveUp/doMoveDown` | 工具栏操作 | 按当前选中页签分派 |
  | `initPreview()` | 初始化预览 | 生成建表/改表 SQL 显示到 `preview` |
  | `bindListeners()` | 绑定监听 | 页签切换显隐按钮、列变化联动索引/外键、预览触发 |
  | `getSubControllers()` | 子控制器 | 返回 `tableColumnExtraController` |
  | `tableName()/schema()/getDbItem()/isUnsaved()` | 元信息 | getter |

- 调用链：`save → initCreateParam → DamengSchemaTreeItem.createTable → ShellDamengClient.createTable`；`initPreview → DamengTableCreateSqlGenerator.generateSqlSingle`

## DamengTableRecordTab
- 职责：表数据页签，展示/编辑表记录。
- 字段：无（继承 `RichTab`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `url()` | 资源路径 | `table/damengTableRecordTab.fxml` |
  | `flushGraphic()/flushTitle()` | 图标/标题 | 表图标；`表名@模式(连接名)` |
  | `init(DamengTableTreeItem)` | 初始化 | 控制器 init 并 `flush` |
  | `reload()` | 重载 | 转发控制器 |
  | `client()` | 客户端 | `item().client()` |
  | `setFilters(List)` | 设置过滤 | 转发控制器 |
  | `item()/tableName()/dbItem()/schema()` | 上下文 | 转发控制器/表节点 |

- 调用链：`DamengTabEventListener.onDamengTableOpen → DamengTableRecordTab.init`

## DamengTableRecordTabController
- 职责：表数据业务，分页查询/过滤/增删改记录并批量应用变更。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `root` | `FXVBox`（@FXML） | 根节点 |
  | `itemProperty` | `ObjectProperty<DamengTableTreeItem>` | 表树节点属性 |
  | `pageData` | `Paging<DamengRecord>` | 分页数据 |
  | `filter` | `SVGGlyph`（@FXML） | 记录过滤按钮 |
  | `missPrimaryKey` | `SVGGlyph`（@FXML） | 缺少主键警告 |
  | `pageBox` | `PageBox<DamengRecord>`（@FXML） | 分页组件 |
  | `recordTable` | `DamengRecordTableView`（@FXML） | 记录表格 |
  | `filters` | `List<DamengRecordFilter>` | 过滤条件 |
  | `apply/discard` | `SVGGlyph`（@FXML） | 应用/抛弃按钮 |
  | `changeListener` | `DBStatusListener` | 记录变更监听器 |
  | `columns` | `DamengColumns` | 字段列表 |
  | `setting` | `DamengSetting` | 设置 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(DamengTableTreeItem)` | 初始化 | 监听节点/父节点变化自动关页签，`reload` 并建变更监听 |
  | `initDataList(long pageNo)` | 加载数据 | `item.recordPage(...)` 并刷新分页与表格 |
  | `initColumns/initRecords` | 初始化列/记录 | 生成 `DBStatusColumn` + `DamengRecordColumn` |
  | `addRecord()` | 新增记录 | 构建带默认值的 `DamengRecord` |
  | `insertRecord/updateRecord/initPrimaryKey` | 记录写库 | 有主键按主键，否则按全字段 |
  | `apply()` | 应用变更 | 遍历新增/变更记录分别插入/更新并清状态 |
  | `discard()` | 抛弃变更 | 移除新增记录、还原变更记录 |
  | `reload()`/`doReload()` | 重载 | 未保存确认后重建列与数据 |
  | `filter()` | 过滤 | 弹 `DamengTableRecordFilterPopupController` |
  | `nextPage/prevPage/lastPage/firstPage/pageJump/pageSetting` | 分页 | 操作 `pageData` 或弹 `DamengPageSettingPopupController` |
  | `deleteRecord()`/`doDeleteRecord(record)` | 删除记录 | 新增直接移除，否则按主键/全字段删除 |
  | `importData()/exportData()` | 导入导出 | `ShellDamengViewFactory` |
  | `getFilters()/setFilters()` | 过滤条件读写 | getter/setter |

- 调用链：`apply → insertRecord → DamengTableTreeItem.insertRecord → ShellDamengClient.insertRecord`；`reload → DamengTableTreeItem.recordPage`

## DamengTableColumnExtraController
- 职责：字段额外信息子控制器（弹出式），按字段类型显隐默认值/值/主键长度/自增/无符号/时间戳更新等设置。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `defaultValueBox/defaultValue` | `FXHBox`/`DamengDefaultValueTextFiled`（@FXML） | 默认值 |
  | `valueBox/value` | `FXHBox`/`DBEnumTextFiled`（@FXML） | 枚举值 |
  | `primaryKeySizeBox/primaryKeySize` | `FXHBox`/`NumberTextField`（@FXML） | 主键长度 |
  | `autoIncrementBox/autoIncrement` | `FXHBox`/`FXCheckBox`（@FXML） | 自动递增 |
  | `unsignedBox/unsigned` | `FXHBox`/`FXCheckBox`（@FXML） | 无符号 |
  | `currentTimestampBox/currentTimestamp` | `FXHBox`/`FXCheckBox`（@FXML） | 按时间戳更新 |
  | `column` | `DamengColumn` | 字段 |
  | `dbClient` | `ShellDamengClient` | 客户端 |
  | `ignoreChanged` | `boolean` | 忽略变更标志 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `apply()` | 应用值到字段 | 按各控件可见性写回字段属性 |
  | `init(DamengColumn, ShellDamengClient)` | 初始化 | 绑定字段类型变更监听并 `doInit` |
  | `doInit()` | 按类型显隐并回填 | `ignoreChanged` 期间避免触发变更 |
  | `listenColumnTypeChanged(...)` | 类型变更回调 | 重新 `doInit` |
  | `bindListeners()` | 绑定监听 | 各控件变化触发 `apply` |
  | `initialize(...)` | FXML 初始化 | 各 Box `managedBindVisible` |

- 调用链：`DamengTableDesignTabController.selectedIndexChanged → DamengTableColumnExtraController.init`

---

## 视图（view）

## DamengViewDesignTab
- 职责：视图设计页签。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `url()` | 资源路径 | `view/damengViewDesignTab.fxml` |
  | `flushGraphic()/flushTitle()` | 图标/标题 | `* 模式-视图名` 表示未保存 |
  | `schema()/viewName()/dbItem()` | 元信息 | 转发控制器 |
  | `init(DamengView, DamengSchemaTreeItem)` | 初始化 | 控制器 init 并 `flush` |
  | `isUnsaved()` | 是否未保存 | 转发控制器 |
  | `onTabCloseRequest(Event)` | 关闭请求 | 未保存确认 |

- 调用链：`DamengTabEventListener.onDamengViewDesign → DamengViewDesignTab.init`

## DamengViewDesignTabController
- 职责：视图设计业务，编辑注释/定义/只读/安全性并预览 SQL。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `view` | `DamengView` | 视图对象 |
  | `dbItem` | `DamengSchemaTreeItem` | 库节点 |
  | `readonly` | `FXCheckBox`（@FXML） | 只读模式 |
  | `comment` | `FXTextArea`（@FXML） | 注释 |
  | `securityType` | `DBSecurityTypeComboBox`（@FXML） | 安全性 |
  | `definition` | `Editor`（@FXML） | 定义 |
  | `preview` | `Editor`（@FXML） | 预览 |
  | `tabPane` | `FXTabPane`（@FXML） | 切换面板 |
  | `listener` | `DBStatusListener` | 数据监听器 |
  | `unsaved/newData/initiating` | `boolean` | 状态标志 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(DamengView, DamengSchemaTreeItem)` | 初始化 | 设 `newData`，遮罩 `doInit` |
  | `doInit()` | 执行初始化 | 建监听器并绑定各控件 |
  | `initInfo()` | 初始化信息 | 新视图默认变更，否则查视图回填 |
  | `tempData()` | 构建临时视图 | 汇总注释/只读/定义/安全类型 |
  | `save()`/`doSave()` | 保存 | 新视图 `createView` 否则 `alertView`，发事件 |
  | `initPreview()` | 预览 | 生成建/改视图 SQL |
  | `refresh()` | 刷新 | 确认后 `init` |
  | `bindListeners()` | 绑定监听 | Ctrl+S 保存、切换面板触发预览 |
  | `schema()/viewName()/getDbItem()/isUnsaved()` | 元信息 | getter |

- 调用链：`save → DamengSchemaTreeItem.createView → ShellDamengClient.createView`；`initPreview → DamengViewCreateSqlGenerator.generateSqlSingle`

## DamengViewRecordTab
- 职责：视图数据页签。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `openedTime` | `long` | 页签打开时间 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `url()` | 资源路径 | `view/damengViewRecordTab.fxml` |
  | `flushGraphic()/flushTitle()` | 图标/标题 | `视图名@模式(连接名)` |
  | `init(DamengViewTreeItem)` | 初始化 | 控制器 init 并 `flush` |
  | `reload()/setFilters()` | 重载/过滤 | 转发控制器 |
  | `item()/client()/viewName()/dbItem()` | 上下文 | 转发控制器/视图节点 |

- 调用链：`DamengTabEventListener.onDamengViewOpen → DamengViewRecordTab.init`

## DamengViewRecordTabController
- 职责：视图数据业务，分页查询/过滤/增删改视图记录（仅可更新视图开放编辑）。
- 字段：与 `DamengTableRecordTabController` 结构一致（`root`、`itemProperty`、`pageData`、`filter`、`missPrimaryKey`、`pageBox`、`recordTable`、`filters`、`apply`、`discard`、`changeListener`、`columns`、`setting`），`itemProperty` 类型为 `ObjectProperty<DamengViewTreeItem>`。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(DamengViewTreeItem)` | 初始化 | 可更新视图才启用编辑按钮 |
  | `initDataList/initDataListByMask` | 加载数据 | `item.recordPage(...)` |
  | `initColumns/initRecords/initCount` | 初始化列/记录/计数 | 生成列并刷新分页计数 |
  | `addRecord()` | 新增记录 | 构建带默认值记录 |
  | `insertRecord/updateRecord/initPrimaryKey` | 记录写库 | 有主键按主键，否则按全字段 |
  | `apply()/discard()` | 应用/抛弃变更 | 遍历记录写库或还原 |
  | `reload()/doReload()` | 重载 | 重建列与数据 |
  | `filter()` | 过滤 | 弹 `DamengViewRecordFilterPopupController` |
  | `nextPage/prevPage/lastPage/firstPage/pageJump/pageSetting` | 分页 | 操作分页数据 |
  | `deleteRecord()`/`deleteRecords`/`deleteRecord(record)` | 删除记录 | 支持多选批量删除 |
  | `getFilters()/setFilters()` | 过滤条件 | getter/setter |

- 调用链：`reload → DamengViewTreeItem.recordPage → ShellDamengClient.viewRecords`

---

## 函数（function）

## DamengFunctionDesignTab
- 职责：函数设计页签。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `url()` | 资源路径 | `function/damengFunctionDesignTab.fxml` |
  | `flushGraphic()/flushTitle()` | 图标/标题 | `* 函数名@模式(连接名)` 表示未保存 |
  | `functionName()/dbItem()/schema()/connectName()` | 元信息 | 转发控制器/库节点 |
  | `init(DamengFunction, DamengSchemaTreeItem)` | 初始化 | 控制器 init 并 `flush` |
  | `isUnsaved()` | 是否未保存 | 转发控制器 |
  | `onTabCloseRequest(Event)` | 关闭请求 | 未保存确认 |

- 调用链：`DamengTabEventListener.onDamengFunctionDesign → DamengFunctionDesignTab.init`

## DamengFunctionDesignTabController
- 职责：函数设计业务，编辑定义/参数/特征/返回值并预览 SQL。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `function` | `DamengFunction` | 函数对象 |
  | `dbItem` | `DamengSchemaTreeItem` | 库节点 |
  | `definition/preview` | `Editor`（@FXML） | 定义/预览 |
  | `tabPane` | `FXTabPane`（@FXML） | 切换面板 |
  | `securityType` | `DBSecurityTypeComboBox`（@FXML） | 安全性 |
  | `parallelEnable/deterministic/resultCache/pipelined/aggregate` | `FXCheckBox`（@FXML） | 函数特征 |
  | `paramTable` | `DBStatusTableView<DamengRoutineParam>`（@FXML） | 参数表单 |
  | `returnType` | `DBFiledTypeComboBox`（@FXML） | 返回值类型 |
  | `returnValues` | `DBEnumTextFiled`（@FXML） | 返回值列表 |
  | `returnDigits/returnSize` | `NumberTextField`（@FXML） | 返回值小数/长度 |
  | `listener` | `DBStatusListener` | 数据监听器 |
  | `unsaved/newData/initiating` | `boolean` | 状态标志 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(DamengFunction, DamengSchemaTreeItem)` | 初始化 | 设 `newData`，遮罩 `doInit` |
  | `doInit()` | 执行初始化 | 建监听器并 `initInfo` |
  | `initDBListener()` | 初始化监听器 | 绑定定义/返回值/特征/安全性等，`paramTable` 同步 |
  | `initInfo()` | 初始化信息 | 新函数给默认体，否则查函数回填定义/参数/特征/返回值 |
  | `tempData()` | 构建临时函数 | 汇总定义/参数/安全类型/特征/返回值 |
  | `save()`/`doSave()` | 保存 | 新函数 `createFunction` 否则 `alertFunction`，发事件 |
  | `initPreview()` | 预览 | 生成建/改函数 SQL |
  | `addParam/deleteParam/moveParamUp/moveParamDown` | 参数操作 | 增删/移动 `paramTable` 行 |
  | `bindListeners()` | 绑定监听 | Ctrl+S、返回值类型联动、切换面板触发预览 |
  | `refresh()` | 刷新 | 确认后 `init` |
  | `schema()/getFunction()/getDbItem()/isUnsaved()/setUnsaved()` | 元信息 | getter/setter |

- 调用链：`save → DamengSchemaTreeItem.createFunction → ShellDamengClient.createFunction`；`initPreview → DamengFunctionCreateSqlGenerator.generateSqlSingle`

---

## 存储过程（procedure）

## DamengProcedureDesignTab
- 职责：存储过程设计页签。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `url()` | 资源路径 | `procedure/damengProcedureDesignTab.fxml` |
  | `flushGraphic()/flushTitle()` | 图标/标题 | `* 过程名@模式(连接名)` 表示未保存 |
  | `procedure()/procedureName()/dbItem()/schema()/connectName()` | 元信息 | 转发控制器/库节点 |
  | `init(DamengProcedure, DamengSchemaTreeItem)` | 初始化 | 控制器 init 并 `flush` |
  | `isUnsaved()` | 是否未保存 | 转发控制器 |
  | `onTabCloseRequest(Event)` | 关闭请求 | 未保存确认 |

- 调用链：`DamengTabEventListener.onDamengProcedureDesign → DamengProcedureDesignTab.init`

## DamengProcedureDesignTabController
- 职责：存储过程设计业务，编辑定义/参数/特征并预览 SQL。
- 字段：与函数设计控制器类似（`procedure`、`dbItem`、`definition`、`preview`、`tabPane`、`securityType`、`parallelEnable`、`paramTable`、`listener`、`unsaved/newData/initiating`），无返回值相关控件。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(DamengProcedure, DamengSchemaTreeItem)` | 初始化 | 设 `newData`，遮罩 `doInit` |
  | `doInit()`/`initDBListener()` | 初始化 | 建监听器并绑定定义/安全类型/特征 |
  | `initInfo()` | 初始化信息 | 新过程给默认体，否则查过程回填 |
  | `tempData()` | 构建临时过程 | 汇总定义/参数/安全类型/特征 |
  | `save()`/`doSave()` | 保存 | 新过程 `createProcedure` 否则 `alertProcedure`，发事件 |
  | `initPreview()` | 预览 | 生成建/改过程 SQL |
  | `addParam/deleteParam/moveParamUp/moveParamDown` | 参数操作 | 增删/移动参数行 |
  | `bindListeners()` | 绑定监听 | Ctrl+S、切换面板触发预览 |
  | `schema()/getProcedure()/getDbItem()/isUnsaved()` | 元信息 | getter |

- 调用链：`save → DamengSchemaTreeItem.createProcedure → ShellDamengClient.createProcedure`；`initPreview → DamengProcedureCreateSqlGenerator.generateSqlSingle`

---

## 查询（query）

## DamengQueryMainTab
- 职责：查询主页签，承载 SQL 编辑与结果展示。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `url()` | 资源路径 | `query/damengQueryMainTab.fxml` |
  | `flushGraphic()/flushTitle()` | 图标/标题 | `* 查询名@模式(连接名)` 表示未保存 |
  | `query()/queryId()/dbItem()/schema()/connectName()` | 元信息 | 转发控制器/查询对象 |
  | `init(DamengQuery, DamengSchemaTreeItem)` | 初始化 | 控制器 init 并 `flush` |

- 调用链：`DamengTabEventListener.onDamengQueryOpen → DamengQueryMainTab.init`

## DamengQueryMainTabController
- 职责：查询业务，编辑 SQL、运行/解释、保存查询与结果页签管理。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `query` | `DamengQuery` | 查询对象 |
  | `unsaved` | `boolean` | 未保存标志 |
  | `dbItem` | `DamengSchemaTreeItem` | 库节点 |
  | `queryArea` | `ShellDamengQueryEditor`（@FXML） | 查询文本域 |
  | `resultTabPane` | `FXTabPane`（@FXML） | 结果页签面板 |
  | `root` | `FXVBox`（@FXML） | 根节点 |
  | `infoTab` | `DamengQueryInfoTab`（@FXML） | 结果信息页签 |
  | `splitPane` | `FXSplitPane`（@FXML） | 分割面板 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(DamengQuery, DamengSchemaTreeItem)` | 初始化 | 填充 SQL、设方言、监听文本变化置未保存 |
  | `run()`/`doRun(String)` | 运行 SQL | 取选中/全部 SQL，`dbItem.executeSql` 后逐条生成结果页签 |
  | `explain()` | 解释 SQL | `dbItem.explainSql` 后生成解释页签 |
  | `initInfoTab/initSelectTab/initExplainTab` | 生成结果页签 | 分别创建信息/查询/解释页签 |
  | `clearTabs()` | 清理结果页签 | 除信息页签外全部移除 |
  | `save()` | 保存查询 | 新查询 `DamengQueryStore.insert` 否则 `update` |
  | `pretty()` | 美化 SQL | `queryArea.pretty()` |
  | `queryKeyPressed(KeyEvent)` | 按键事件 | Ctrl+S 保存、Ctrl+R 运行 |
  | `showNode(int)` | 显隐结果面板 | 控制分割面板分隔条 |
  | `bindListeners()` | 绑定监听 | 结果页签切换、运行回调 |
  | `getQuery()/getDbItem()/isUnsaved()` | 元信息 | getter |

- 调用链：`run → DamengQueryMainTabController.doRun → DamengSchemaTreeItem.executeSql → ShellDamengClient.executeSql`

## DamengQuerySelectTab
- 职责：查询结果页签（不可关闭）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `url()` | 资源路径 | `query/damengQuerySelectTab.fxml` |
  | `init(title, DamengExecuteResult, DamengSchemaTreeItem)` | 初始化 | 设标题并控制器 init |
  | `initNode()` | 初始化节点 | 不可关闭 |

- 调用链：`DamengQueryMainTabController.initSelectTab → DamengQuerySelectTab.init`

## DamengQuerySelectTabController
- 职责：查询结果业务，展示结果集并支持在结果表上增删改记录。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `root` | `FXVBox`（@FXML） | 根节点 |
  | `sql/used/count` | `FXText`（@FXML） | SQL/耗时/计数 |
  | `recordTable` | `DamengRecordTableView`（@FXML） | 结果表格 |
  | `dbItem` | `DamengSchemaTreeItem` | 库节点 |
  | `result` | `DamengExecuteResult` | 执行结果 |
  | `add/delete/apply/discard` | `SVGGlyph`（@FXML） | 操作按钮 |
  | `changeListener` | `DBStatusListener` | 记录变更监听器 |
  | `columns` | `List<DamengColumn>` | 结果列 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(DamengExecuteResult, DamengSchemaTreeItem)` | 初始化 | 结果可更新时启用编辑按钮 |
  | `initDataList/initColumns/initRecords/initCount` | 初始化 | 填充列/记录/SQL/耗时/计数 |
  | `addRecord()` | 新增记录 | 构建带默认值记录 |
  | `insertRecord/updateRecord/initPrimaryKey` | 记录写库 | 按主键或全字段 |
  | `apply()/discard()` | 应用/抛弃变更 | 遍历记录写库或还原 |
  | `reload()` | 重载 | 重新执行 SQL 并刷新 |
  | `exportRecord()` | 导出记录 | 组装 `ShellDamengDataExportTable` 导出 |
  | `deleteRecord()`/`deleteRecords`/`deleteRecord(record)` | 删除记录 | 支持多选批量删除 |
  | `bindListeners()` | 绑定监听 | 选中可编辑、变更启用应用按钮 |

- 调用链：`reload → DamengSchemaTreeItem.executeSingleSql → ShellDamengClient.executeSingleSql`

## DamengQueryInfoTab
- 职责：查询信息页签（不可关闭），展示批量执行的每条结果信息。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `url()` | 资源路径 | `query/damengQueryInfoTab.fxml` |
  | `init(DBQueryResults<?>)` | 初始化 | 控制器 init |
  | `initNode()` | 初始化节点 | 不可关闭 |

- 调用链：`DamengQueryMainTabController.initInfoTab → DamengQueryInfoTab.init`

## DamengQueryInfoTabController
- 职责：查询信息业务，逐条拼接结果内容/影响行数/耗时。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `infoArea` | `Editor`（@FXML） | 信息文本域 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(DBQueryResults<?>)` | 初始化 | 成功则逐条追加内容、影响行数/OK、耗时；失败追加错误信息 |

- 调用链：`DamengQueryInfoTabController.init → infoArea.appendLine`

## DamengQueryExplainTab
- 职责：执行计划解释页签（不可关闭）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `url()` | 资源路径 | `query/damengQueryExplainTab.fxml` |
  | `init(String, DamengExplainResult)` | 初始化 | 设标题并控制器 init |
  | `initNode()` | 初始化节点 | 不可关闭 |

- 调用链：`DamengQueryMainTabController.initExplainTab → DamengQueryExplainTab.init`

## DamengQueryExplainTabController
- 职责：执行计划解释业务，展示解释结果集。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `sql/used/count` | `FXText`（@FXML） | SQL/耗时/计数 |
  | `recordTable` | `DamengRecordTableView`（@FXML） | 结果表格 |
  | `result` | `DamengExplainResult` | 解释结果 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(DamengExplainResult)` | 初始化 | 记录结果并 `initDataList` |
  | `initDataList()` | 初始化数据 | 填充列/记录/SQL/耗时/计数 |
  | `initColumns/initRecords` | 初始化列/记录 | 生成 `DamengRecordColumn` 并设数据 |

- 调用链：`DamengQueryExplainTabController.init → DamengQueryExplainTabController.initDataList`
