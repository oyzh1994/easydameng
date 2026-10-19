# controller 包代码审查

主界面与各功能窗口的控制器包，基于 `StageController` / `SubStageController` + `@StageAttribute`（对应 fxml），涵盖主页、头部、连接增改、库表/函数/过程/视图信息、数据导入导出、转储、运行 SQL、设置等。

---

## MainController
- 职责：程序主页，聚合头部与数据库主页两个子控制器，管理窗口关闭与退出策略。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `project` | `Project` | 项目信息（`Project.load()`） |
  | `headerController` | `HeaderController`（@FXML） | 头部页面 |
  | `damengMainController` | `DamengMainController`（@FXML） | db 主页业务 |
  | `setting` | `DamengSetting` | 相关配置（`SETTING`） |
  | `settingStore` | `DamengSettingStore` | 设置存储（`INSTANCE`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getSubControllers()` | 子控制器列表 | 返回 damengMainController、headerController |
  | `onWindowCloseRequest(WindowEvent)` | 关闭确认 | 依据 `setting.isExitDirectly/isExitAsk/isExitTray` 决定直接退出/询问/显示托盘 |
  | `onSystemExit()` | 系统退出 | 按 `isRememberPageSize/isRememberPageLocation` 保存窗口尺寸与位置后 `settingStore.replace`，销毁托盘 |
  | `onStageInitialize(StageAdapter)` | 初始化 | 恢复上次窗口大小/位置/最大化 |
  | `onWindowShown(WindowEvent)` | 显示 | 调用父类 |
  | `getViewTitle()` | 标题 | `i18nString("db.title.main")` |

- 调用链：`EasyDamengApp.showMainView → StageManager.showStage(MainController) → onStageInitialize 恢复窗口`

## DamengMainController
- 职责：数据库主页，承载左侧连接树与右侧标签面板，响应布局切换与树节点变化。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `root` | `FXSplitPane`（@FXML） | 根分割面板 |
  | `connect` | `FXVBox`（@FXML） | 左侧组件 |
  | `tabPane` | `DamengTabPane`（@FXML） | db 切换面板 |
  | `connectController` | `ConnectController`（@FXML） | 连接业务 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `flushViewTitle(DamengConnect)` | 刷新窗口标题 | 有 info 追加 `(名称)`，否则 `restoreTitle` |
  | `treeItemChanged(TreeItem)` | 内部节点变化 | 按节点类型取 info 刷新标题 |
  | `bindListeners()` | 绑定监听 | 现基本为空（大量注释） |
  | `treeItemChanged(DamengTreeItemChangedEvent)` | 事件订阅 | `@EventSubscribe`，按事件数据类型刷新标题 |
  | `layout2(Layout2Event)` | 布局2 | `@EventSubscribe`，显示左侧并把分割线位置设为 0.25/0.75 |
  | `layout1(Layout1Event)` | 布局1 | `@EventSubscribe`，隐藏左侧并记录分割位置 |
  | `getSubControllers()` | 子控制器 | `connectController` |

- 调用链：`HeaderController.layout → DamengEventUtil.layout1/2 → DamengMainController.layout1/2`；`树选择 → DamengTreeItemChangedEvent → treeItemChanged`

## HeaderController
- 职责：主页头部业务，提供布局切换、数据传输、设置、关于、退出、消息入口。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `layoutPane` | `LayoutSVGPane`（@FXML） | 布局组件 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `layout()` | 切换布局 | `layoutPane.isLayout1()` 决定触发 `DamengEventUtil.layout2/layout1` |
  | `transport()` | 数据传输 | 打开/前置 `ShellDamengDataTransportController` |
  | `setting()` | 设置 | 打开/前置 `SettingController2` |
  | `about()` | 关于 | `StageManager.showStage(AboutController)` |
  | `quit()` | 退出 | `MessageBox.confirm` 后 `StageManager.exit()` |
  | `message()` | 消息 | `DamengEventUtil.showMessage()` |
  | `layout1/layout2(Event)` | 布局事件 | `@EventSubscribe`，设置提示文案并调用 `layoutPane.layout1/2` |
  | `onWindowShowing(WindowEvent)` | 显示 | 设置提示为“隐藏左侧” |

- 调用链：`头部按钮 → HeaderController.transport → ShellDamengDataTransportController`

## AboutController
- 职责：关于窗口业务，展示项目名称/版本/更新日期/版权等。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `name/type/version/updateDate/copyright` | `FXText`（@FXML） | 展示文本 |
  | `project` | `Project` | 项目信息 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `onWindowShown(WindowEvent)` | 显示 | 从 `project` 填充各文本，`type` 依据 build 映射中英文，追加标题、`hideOnEscape` |
  | `getViewTitle()` | 标题 | `i18nString("base.title.about")` |

- 调用链：`HeaderController.about → AboutController.onWindowShown`

## SettingController2
- 职责：应用设置业务，编辑退出方式、页面记忆、主题颜色、字体、区域与透明度等并持久化。
- 字段：大量 `@FXML` 控件，包括 `root`（`SettingMainPane`）、`exitMode`/`exitMode0-2`、`pageSize/pageResize/pageLocation`、`keyLoadLimit`、`theme`/`bgColor`/`fgColor`/`accentColor` 及各 `*Box`、通用/编辑器/终端字体控件、`locale`、`opacity`/`titleBarOpacity`，以及 `setting`（`DamengSetting`）与 `settingStore`（`DamengSettingStore`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `onWindowShowing(WindowEvent)` | 回填 | 依据 `setting` 回填退出方式、页面记忆、主题色、字体、区域、透明度 |
  | `saveSetting()` | 保存设置 | 汇总各控件值写入 `setting`，`settingStore.update` 成功后关窗并 `I18nManager/FontManager/ThemeManager/OpacityManager.apply`；`checkConfigForRestart` 非空时确认后 `DamengProcessUtil.restartApplication` |
  | `checkConfigForRestart(String)` | 判断是否需重启 | locale 变化返回重启提示文案 |
  | `bindListeners()` | 绑定 | 前景/背景色与强调色联动禁用；主题切换更新提示色 |
  | `onWindowShown(WindowEvent)` | 显示 | 构建左侧设置树并选中 `dameng_box` |
  | `resetFgColor/resetBgColor/resetAccentColor/resetLocale/resetOpacity/resetTitleBarOpacity` | 重置项 | 恢复主题默认色/区域/默认透明度 |
  | `resetFontFamily/resetFontSize/resetFontWeight` 及 editor/terminal 系列 | 重置字体 | 恢复 `AppSetting.default*` |
  | `getViewTitle()` | 标题 | `I18nHelper.settingTitle()` |

- 调用链：`HeaderController.setting → SettingController2 → saveSetting → DamengSettingStore.update → ThemeManager/FontManager.apply`

## DamengConnectAddController
- 职责：添加连接信息业务（含 SSH 配置与连接测试）。
- 字段：`@FXML` 控件 `readonly`、`tabPane`、`name`、`type`、`user`、`password`、`remark`、`hostIp`、`hostPort`、`connectTimeOut`、`sshTab`、`sshForward`、`sshHost`、`sshPort`、`sshTimeout`、`sshUser`、`sshPassword`；`group`（`DamengGroup`）；`connectStore`（`DamengConnectStore`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getHost()` | 组装 host | 校验端口/ip 后返回 `ip:port` |
  | `getSSHConfig()` | 组装 SSH 配置 | 读取 ssh 控件构建 `DamengSSHConfig` |
  | `testConnect()` | 测试连接 | 由当前输入构造 `DamengConnect`，`DBConnectUtil.testConnect` |
  | `add()` | 添加连接 | 组装 `DamengConnect`（host/name/type/ssh 等），`connectStore.replace` 成功后 `DamengEventUtil.connectAdded` |
  | `bindListeners()` | 绑定 | `sshForward` 切换时启用/禁用 ssh 分组 |
  | `onWindowShown(WindowEvent)` | 显示 | 取 `group` 属性，`switchOnTab`、`hideOnEscape` |
  | `getViewTitle()` | 标题 | `I18nHelper.connectAddTitle()` |

- 调用链：`新增连接 → add → DamengConnectStore.replace → DamengEventUtil.connectAdded`

## DamengConnectUpdateController
- 职责：连接信息修改业务（含 SSH 配置同步与连接测试）。
- 字段：与新增控制器类似，另有 `damengConnect`、`executeTimeOut`、`sshConfigStore`（`DamengSSHConfigStore`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getHost()` / `getSSHConfig()` | 组装 host/SSH | 同新增控制器 |
  | `testConnect()` | 测试连接 | 基于已有连接 id 构造临时连接测试 |
  | `update()` | 修改连接 | 回写字段后 `connectStore.replace`，成功后 `DamengEventUtil.connectUpdated` 并提示成功 |
  | `bindListeners()` | 绑定 | hostIp 输入含 `:` 时自动拆分 ip/端口；sshForward 切换启停 ssh 分组 |
  | `onWindowShown(WindowEvent)` | 显示 | 取 `info` 回填所有字段，从 `sshConfigStore.getByIid` 回填 SSH 配置 |
  | `getViewTitle()` | 标题 | `I18nHelper.connectUpdateTitle()` |

- 调用链：`编辑连接 → onWindowShown(getByIid 回填) → update → DamengConnectStore.replace → DamengEventUtil.connectUpdated`

## ShellDamengSchemaAddController
- 职责：添加数据库模式业务。
- 字段：`name`（`ClearableTextField`，@FXML，字符集/排序控件被注释）；`connectItem`（`DBConnectTreeItem`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `add()` | 添加库 | 校验名称、`connectItem.existSchema` 判重，`createSchema` 后触发 `DamengEventUtil.schemaAdded` |
  | `bindListeners()` | 绑定 | 父类（原字符集联动被注释） |
  | `getViewTitle()` / `onWindowShown(WindowEvent)` | 标题/显示 | `addDatabase` 标题；取 `connectItem` 属性 |

- 调用链：`新增库 → add → connectItem.createSchema → DamengEventUtil.schemaAdded`

## ShellDamengSchemaUpdateController
- 职责：编辑数据库模式业务。
- 字段：`name`（`ReadOnlyTextField`，@FXML）；`schema`（`DamengSchema`）；`connectItem`（`DBConnectTreeItem`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `save()` | 保存 | 构造临时 `DamengSchema`，`connectItem.alterSchema` 成功后触发 `DamengEventUtil.schemaUpdated` |
  | `onWindowShown(WindowEvent)` | 显示 | 取 `database`/`connectItem` 属性，回填名称 |
  | `getViewTitle()` | 标题 | `I18nHelper.updateDatabase()` |

- 调用链：`编辑库 → save → connectItem.alterSchema → DamengEventUtil.schemaUpdated`

## ShellDamengTableInfoController
- 职责：表信息业务（只读展示名称/表空间/注释/建表 DDL）。
- 字段：`name`、`tableSpace`（`ReadOnlyTextField`）、`comment`（`ReadOnlyTextArea`）、`createDefinition`（`Editor`）；`treeItem`（`DamengTableTreeItem`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `initInfo()` | 初始化信息 | `treeItem.dbItem().selectTable(名)` 后回填各控件 |
  | `onWindowShown(WindowEvent)` | 显示 | 取 `item` 属性，`StageManager.showMask(this.getWindow(), this::initInfo)` |
  | `getViewTitle()` | 标题 | `I18nHelper.tableInfo()` |

- 调用链：`ShellDamengViewFactory.tableInfo → ShellDamengTableInfoController.onWindowShown → initInfo → dbItem.selectTable`

## ShellDamengFunctionInfoController
- 职责：函数信息业务，展示定义与 DDL。
- 字段：`name`、`comment`、`definition`、`createDefinition`；`treeItem`（`DamengFunctionTreeItem`）。
- 方法：`initInfo()`（`dbItem.selectFunction` 后回填）、`onWindowShown`、`getViewTitle`、`destroy()`（销毁两个 `Editor`）。
- 调用链：`ShellDamengViewFactory.functionInfo → ShellDamengFunctionInfoController.initInfo → dbItem.selectFunction`

## ShellDamengProcedureInfoController
- 职责：存储过程信息业务，展示定义与 DDL。
- 字段：`name`、`comment`、`definition`、`createDefinition`；`treeItem`（`DamengProcedureTreeItem`）。
- 方法：`initInfo()`（`dbItem.selectProcedure` 后回填）、`onWindowShown`、`getViewTitle`、`destroy()`。
- 调用链：`ShellDamengViewFactory.procedureInfo → ShellDamengProcedureInfoController.initInfo → dbItem.selectProcedure`

## DamengViewInfoController
- 职责：视图信息业务，展示注释/定义/DDL。
- 字段：`name`、`comment`、`definition`、`createDefinition`；`treeItem`（`DamengViewTreeItem`）。
- 方法：`initInfo()`（`dbItem.selectView` 后回填）、`onWindowShown`、`getViewTitle`。
- 调用链：`ShellDamengViewFactory.viewInfo → DamengViewInfoController.initInfo → dbItem.selectView`

## ConnectController
- 职责：连接业务，维护左侧连接树、排序、过滤、导入导出连接。
- 字段：`tree`（`DBTreeView`，@FXML）、`sortPane`（`SortSVGPane`）、`filter`（`ClearableTextField`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `positionNode()` | 定位节点 | `tree.scrollTo(tree.getSelectedItem())` |
  | `onWindowHidden(WindowEvent)` | 隐藏 | `tree.closeConnects()`，取消 F5 监听 |
  | `bindListeners()` | 绑定 | 树选择变化 `DamengEventUtil.treeItemChanged`；初始化文件拖拽；F5 刷新 |
  | `sortTree()` | 排序 | 依据 `sortPane.isAsc()` 切换升/降序 |
  | `importConnect()` / `exportConnect()` | 导入/导出连接 | 委托 `tree.root().importConnect()/exportConnect()` |
  | `onStageInitialize(StageAdapter)` | 初始化 | 过滤框文本变化时设置高亮并 `tree.filter()` |

- 调用链：`连接树选择 → DamengEventUtil.treeItemChanged`；`导入/导出 → tree.root().importConnect/exportConnect`

## ShellDamengDataExportController
- 职责：数据导出业务（多步骤向导，支持 sql/txt/json/csv/html/xml/xls 等格式）。
- 字段：步骤 `step1~step5`；`database`（`DBSchemaComboBox`）、`tableCombobox`（`DataExportTableComboBox`）、`tableColumns`（`DataExportColumnListView`）、`exportTableView`（`DataExportTableTableView`）、`fileType`（`FXToggleGroup`）、`datePreview`/`dateFormat`、`recordSeparator`/`fieldSeparator`/`txtIdentifier`、`includeFields`/`fieldToAttr`/`earlyVersion`、`stopExportBtn`/`exportStatus`/`exportMsg`；`dbClient`、`execTask`、`counter`、`exportHandler`（`DamengDataExportHandler`）、`dbName`、`tableName`、`exportMode`、`exportTable`。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `doExport()` | 执行导出 | 生成/复用 `DamengDataExportHandler`，按文件类型设置参数（表、分页、日期格式、分隔符、识别符等），后台线程 `doExport`，finally 恢复界面并 `SystemUtil.gcLater` |
  | `stopExport()` | 结束导出 | 中断线程与处理器 |
  | `bindListeners()` | 绑定 | 表选择→初始化字段列表；日期格式→刷新预览；库选择→`initTables` |
  | `flushDatePreview()` | 刷新日期预览 | 按格式格式化当前时间，异常显示“格式无效” |
  | `onWindowShown/onWindowHidden` | 显示/隐藏 | 取 dbClient/dbName/tableName/exportMode/exportTable，初始化并可选锁定库；隐藏时停止导出 |
  | `showStep1`~`showStep5` | 步骤切换 | 按文件类型显示/隐藏各参数区（sql/txt/json/xls/csv/html/xml 分支） |
  | `initTables()` | 初始化表列表 | 正常导出拉取 `selectTables`，查询导出直接加入 `exportTable`，设置扩展名 |
  | `selectAllTable/unselectAllTable/selectAllFiled/unselectAllField` | 全选/反选 | 遍历置选中态 |

- 调用链：`表右键导出 → ShellDamengViewFactory.exportData → ShellDamengDataExportController.doExport → DamengDataExportHandler.doExport`

## ShellDamengDataImportController
- 职责：数据导入业务（多步骤向导）。
- 字段：步骤 `step1~step6`；`importFileTableView`、`sourceTableCombobox`、`targetTableCombobox`、`fileType`、`recordLabel`、`attrToColumn`、`columnIndex`、`dataStartIndex`、`datePreview`/`dateFormat`、`recordSeparator`/`fieldSeparator`/`txtIdentifier`、`importMode`、`stopImportBtn`/`importStatus`/`importMsg`；`dbClient`、`execTask`、`counter`、`importHandler`（`DamengDataImportHandler`）、`dbName`。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `doImport()` | 执行导入 | 生成/复用 `DamengDataImportHandler`，设置文件类型/文件/分隔符/识别符/日期格式/属性转列/导入模式/字段索引（减 1）/数据起始（减 1）/记录标签，后台线程导入 |
  | `stopImport()` | 结束导入 | 中断线程与处理器 |
  | `bindListeners()` | 绑定 | 日期格式刷新预览；目标表选择写入来源文件；文件列表变更时 `initFileTable` |
  | `initFileTable()` | 初始化文件 | 为每个导入文件设置 schema 与 dbClient |
  | `flushDatePreview()` | 日期预览 | 同导出 |
  | `onWindowShown/onWindowHidden` | 显示/隐藏 | 取 dbName/dbClient；隐藏时停止导入 |
  | `showStep1`~`showStep6` | 步骤切换 | 按文件类型（json/txt/csv/xml/excel）显示/隐藏参数区；选择源表与目标表 |
  | `addFile()` / `deleteFile()` | 增删文件 | 文件选择器添加 `DamengDataImportFile` / 移除选中项 |

- 调用链：`ShellDamengViewFactory.importData → ShellDamengDataImportController.doImport → DamengDataImportHandler.doImport`

## ShellDamengDataDumpController
- 职责：数据转储业务（库/表结构或全量导出为 sql 文件）。
- 字段：`stopDumpBtn`、`dumpStatus`、`dumpMsg`、`connect`、`database`、`tableBox`、`table`、`dataType`（`DBDumpDataTypeComboBox`）；`dbInfo`、`dbClient`、`dumpType`（1 库/2 表）、`execTask`、`counter`、`dumpFile`、`dumpHandler`（`DamengDataDumpHandler`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `checkDumpFile()` | 检查/选择转储文件 | 无文件时按 dumpType 命名（`.full`/`.structure`），`FileChooserHelper.save` 后 `FileUtil.touch` |
  | `doDump()` | 执行转储 | 生成/复用 `DamengDataDumpHandler`，设置分页/文件/表名/转储类型/数据类型，后台线程 `doDump` |
  | `stopDump()` | 结束转储 | 中断线程与处理器 |
  | `onWindowShown/onWindowHidden` | 显示/隐藏 | 取 dbInfo/dbClient/dumpType/dbName/tableName，dumpType=2 时显示表区；隐藏时停止 |
  | `onStageInitialize` / `bindListeners` | 初始化/绑定 | 表区 `managedBindVisible`；数据类型变化清空 dumpFile |
  | `updateStatus(String)` | 更新状态 | 由计数器格式化状态文本 |

- 调用链：`表右键转储 → ShellDamengViewFactory.dumpData → ShellDamengDataDumpController.doDump → DamengDataDumpHandler.doDump`

## ShellDamengDataRunSqlFileController
- 职责：运行 SQL 文件业务。
- 字段：`stopSqlFileBtn`、`execStatus`、`execMsg`、`connect`、`database`、`continueWithErrors`、`file`（`ChooseFileTextField`）；`dbInfo`、`dbClient`、`execTask`、`counter`、`sqlFileHandler`（`DamengDataRunSqlFileHandler`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `checkSqlFile()` | 检查文件 | 未选择文件则告警 |
  | `runSqlFile()` | 执行 | 生成/复用 `DamengDataRunSqlFileHandler`，设置文件与“遇错继续”，后台线程 `runFile`，异常区分中断与失败 |
  | `stopSqlFile()` | 结束 | 中断线程与处理器 |
  | `onWindowShown/onWindowHidden` | 显示/隐藏 | 取 dbInfo/dbClient/dbName；隐藏时停止 |
  | `onStageInitialize` | 初始化 | 文件选择过滤 SQL 扩展名 |
  | `getViewTitle()` | 标题 | `i18nString("base.runSqlFile")` |

- 调用链：`ShellDamengViewFactory.runSqlFile → ShellDamengDataRunSqlFileController.runSqlFile → DamengDataRunSqlFileHandler.runFile`

## ShellDamengDataTransportController
- 职责：数据传输业务（在源/目标库之间复制表、视图、函数、过程、触发器）。
- 字段：`step1~step3`；来源/目标相关 `sourceInfo`/`targetInfo`（`DBInfoComboBox`）、`sourceDatabase`/`targetDatabase`（`DBSchemaComboBox`）、各 `source*/target*` 标签；`stopTransportBtn`/`transportStatus`/`transportMsg`；`tablePane/viewPane/functionPane/procedurePane/triggerPane`（`FXTitledPane`）与对应列表控件 `tableList/viewList/functionList/procedureList/triggerList`；`sourceClient`/`targetClient`、`execTask`、`counter`、`transportHandler`（`DamengDataTransportHandler`）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `doTransport()` | 执行传输 | 生成/复用 `DamengDataTransportHandler`，设置源/目标客户端与库、各类对象列表，后台线程 `doTransport` |
  | `stopTransport()` | 结束传输 | 中断线程与处理器 |
  | `bindListeners()` | 绑定 | 源/目标连接变化时关闭旧客户端、`DBClientUtil.newClient` 新建并 `start`、初始化库列表与版本；各列表选中变化刷新面板计数 |
  | `onWindowShown/onWindowHidden` | 显示/隐藏 | 隐藏时停止传输 |
  | `showStep1`~`showStep3` | 步骤切换 | 第 2 步校验源/目标连接与库并加载各类对象 |
  | `clearList()` / `flushPaneText(String)` | 清空/刷新计数 | 清空各列表；面板追加 `(选中/总数)` |
  | `onStageInitialize` | 初始化 | 步骤分区 `managedBindVisible` |

- 调用链：`HeaderController.transport → ShellDamengDataTransportController → doTransport → DamengDataTransportHandler.doTransport`

---

## 已跳过的死代码

- `controller/HeaderController2.java`：整文件被注释。
- `controller/main/MessageController.java`：整文件被注释。
