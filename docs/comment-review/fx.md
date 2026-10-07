# fx 包代码审查

达梦客户端的 JavaFX 自定义控件包（`cn.oyzh.easydameng.fx`），大多数类是对 `cn.oyzh.fx.plus.controls` 框架控件的轻量子类（下拉框 `FXComboBox`、文本框 `FXTextField`、表格列/视图 `FXTableColumn`/`FXTableView` 等），用于表/视图/函数/过程设计器与数据导入导出界面；少数承载业务逻辑（记录表格列、字段/索引选择文本框）。

> 说明：本包中 `svg/glyph/*`、`data/DataTransport*`、`data/DataExportColumn`、`data/DataExportTable`、`record/DBJsonTextFiled*`、`record/DamengBinaryTextFiled`、`info/ServiceTypeCombobox`、`table/DamengFiledTypeComboBox`、`table/DamengConditionComboBox`、`table/DamengColumnComboBox`、`view/DamengViewCheckOptionComboBox`、`DBEditor`、`DBCharsetComboBox`、`DBCollationComboBox`、`DamengDesignTabPane` 等文件整文件被注释掉（死代码），未收录。

---

## 基础控件

## ShellDataEditor
- 职责：shell 数据编辑器，继承 `Editor`，按用户设置提供编辑器字体。
- 字段：无（继承）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getEditorFont()` | 返回编辑器字体 | 读取 `DamengSettingStore.SETTING` 的 `editorFontConfig()`，经 `FontManager.toFont` 转字体 |

- 调用链：`ShellDataEditor.getEditorFont → DamengSettingStore.SETTING → FontManager.toFont`

## DBSecurityTypeComboBox
- 职责：DB 安全类型下拉框（DEFINER / CURRENT_USER）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `select(String obj)` | 按值选中 | 非空转大写后 `select`，否则清空选择 |
  | `initNode()` | 初始化节点 | 添加 `DEFINER`、`CURRENT_USER` 选项 |

- 调用链：`DBSecurityTypeComboBox.initNode → addItem`

## DBTypeComboBox
- 职责：数据库类型下拉框，提供类型名称/达梦判断/按名选中。
- 字段：无（选项在实例初始化块中由 `DBDialect.valueList()` 填充）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getType()` | 返回选中类型名称 | `getSelectedItem().name()` |
  | `isDameng()` | 是否选中达梦 | `getSelectedItem() == DBDialect.DAMENG` |
  | `selectType(String type)` | 按类型名选中 | 遍历 `getItems()` 匹配 `name()` 后 `select` |

- 调用链：`DBTypeComboBox.selectType → DBDialect.name`

## DBStatusColumn
- 职责：数据库对象状态表格列，固定 25px 宽不可排序/缩放。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DBStatusColumn()` | 构造状态列 | `setCellValueFactory(new PropertyValueFactory<>("status"))`，设宽 25、不可排序/缩放/重排 |

- 调用链：`DBStatusColumn → PropertyValueFactory("status")`

## DBStatusTableView
- 职责：数据库对象状态表格视图，负责监听对象状态变化并维护待删除项。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `deleteItems` | `List<S>` | 待删除项列表（移除且非新建的对象） |
  | `statusListener` | `DBStatusListener` | 状态监听器（注册到每个对象的 statusProperty） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `reset()` | 重置状态 | 清空 `deleteItems` 并 `clearStatus()` |
  | `clearStatus()` | 清除全部对象状态 | 遍历 `getItems()` 调 `object.clearStatus()` |
  | `getDeleteItems()/setDeleteItems()` | 读写待删除项 | getter/setter |
  | `getStatusListener()/setStatusListener()` | 读写状态监听器 | getter/setter |

- 关键逻辑：实例初始化块向 `itemList()` 注册 `ListChangeListener`，按 `wasReplaced/wasAdded/wasRemoved` 分别给新增对象加监听、对被移除的非 `isCreated()` 对象收集进 `deleteItems` 并移除监听；add 时触发一次 `statusListener.changed`。
- 调用链：`DBStatusTableView.itemList().addListener → DBStatusListener.changed`

## DBMsgTextArea
- 职责：DB 消息文本域，订阅事件消息并带时间戳追加显示。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `onEventMsg(EventFormatter)`（@EventSubscribe） | 处理事件消息 | `formatter.eventFormat()` 非空则按 `时间 + 内容` `appendLine` |

- 调用链：`事件发布 → DBMsgTextArea.onEventMsg → appendLine`

## DBSchemaComboBox
- 职责：DB 模式（schema）选择框。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(ShellDamengClient client)` | 初始化 | 委托 `init(client, null)` |
  | `init(client, String schema)` | 初始化并选中 | 清空后调用 `client.schemas()` 取模式名填充，`schema` 非空则选中 |

- 调用链：`DBSchemaComboBox.init → ShellDamengClient.schemas`

## DBInfoComboBox
- 职责：DB 连接信息选择框，展示本地已保存的连接。
- 字段：无。
- 方法：无（构造中设置 `SimpleStringConverter` 以 `DamengConnect.getName()` 显示，并加载 `DamengConnectStore.INSTANCE.load()`）。
- 调用链：`DBInfoComboBox → DamengConnectStore.INSTANCE.load`

---

## 记录表格（record）

## DamengRecordTableRow
- 职责：达梦记录表格行（空子类）。
- 字段：无。
- 方法：无。
- 调用链：无（类型占位）。

## DamengRecordColumn
- 职责：达梦记录表格列，列头图形展示字段名/类型/注释，提供字段信息与复制列名右键菜单。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `column` | `DamengColumn` | 关联的数据库字段 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengRecordColumn(DamengColumn)` | 构造 | 委托 `(column, true)` |
  | `DamengRecordColumn(DamengColumn, boolean)` | 构造 | 设 cellValueFactory 取记录属性，`initContent` 构造列头 VBox |
  | `initContent(boolean showComment)` | 初始化列头内容 | 构建名称/类型(含长度)/注释标签，注册右键菜单与行高联动监听 |
  | `getMenuItems()` | 右键菜单项 | `MenuItemHelper.columnInfo`、`copyColumnName` |
  | `showColumnInfo()` | 显示字段信息 | 弹出 `DamengFieldInfoPopupController` 并按鼠标位置显示 |
  | `copyColumnName()` | 复制列名 | `ClipboardUtil.copy(getName())` |
  | `getFont()/getName()/getType()/supportSize()/getSize()` | 取字段元信息 | 转发到 `column` 或 `FontManager.currentFont()` |
  | `initNode()` | 初始化节点 | `showGraphicOnlyLater()` 后 `super.initNode()` |

- 关键逻辑：`vBox.heightProperty()` 变化时同步表格 `setHeaderHeight`。
- 调用链：`DamengRecordColumn.showColumnInfo → PopupManager.parsePopup(DamengFieldInfoPopupController)`

## DamengRecordTableView
- 职责：达梦记录表格视图，支持多选、记录/属性存在性判断。
- 字段：无（行工厂在初始化块/initNode 设置）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `hasProperty(DamengRecordProperty)` | 是否存在某属性 | 遍历 items 调 `record.hasProperty` |
  | `hasRecord(DamengRecord)` | 是否包含记录 | `getItems().contains(record)` |
  | `initNode()` | 初始化节点 | 多选模式、设置行工厂 `DamengRecordTableRow`、`destroyItemsOnRemoved()` |

- 调用链：`DamengRecordTableView.initNode → FXTableView.initNode`

---

## 字段/索引/表控件（table）

## DBEnumTextFiled
- 职责：枚举值文本框，通过弹窗选择枚举值并以 `'v1','v2'` 形式回填。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `values` | `List<String>` | 枚举值列表 |
  | `popup` | `PopupAdapter` | 枚举弹窗 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DBEnumTextFiled()/DBEnumTextFiled(List)` | 构造 | 可带初始枚举值 |
  | `initPopup()` | 初始化弹窗 | 弹出 `DamengColumnEnumPopupController`，提交时从 `listView` 回收值并 `initText` |
  | `initText()` | 刷新文本 | 空则清空，否则拼 `'..'` 逗号串 |
  | `setValues(List)` | 设置枚举值 | 同步到 listView 并刷新文本 |
  | `listView()` | 取弹窗内列表 | `popup.content().lookup("#listView")` |

- 调用链：`DBEnumTextFiled.initPopup → DamengColumnEnumPopupController`

## DBJoinSymbolComboBox
- 职责：连接符号下拉框（AND / OR）。
- 字段：无。
- 方法：无（实例初始化块添加 `AND`、`OR`）。
- 调用链：无。

## DamengColumnListView
- 职责：字段复选列表，提供初始化、选中回收与按名选中。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengColumnListView()/DamengColumnListView(List)` | 构造 | 可带字段列表初始化 |
  | `init(List)`/`init(List, List<String>)` | 初始化列表 | 每个字段生成 `FXCheckBox`，按已选名保持勾选 |
  | `getSelectedColumns()` | 取选中字段 | 过滤 `isSelected` 后取 `column` 属性 |
  | `getSelectedColumnNames()` | 取选中字段名 | 由选中字段映射 `getName` |
  | `select(Collection<String>)` | 按名勾选 | 忽略大小写匹配字段名后选中 |

- 调用链：`DamengColumnListView.init → FXCheckBox → ListViewUtil.selectRowOnMouseClicked`

## DamengDefaultValueTextFiled
- 职责：字段默认值文本框，支持可编辑输入或在 `NULL`/`EMPTY STRING`/空之间选择。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `editableFlag` | `boolean` | 是否允许编辑（选中首项时启用） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(DamengColumn)`/`init(DamengColumn, String)` | 初始化 | 清空并按默认值决定可编辑性与选项（`""`/`EMPTY STRING`/`NULL`） |
  | `getValue()` | 取默认值 | 可编辑取文本；否则 `NULL`→null、`EMPTY STRING`→`""` |

- 调用链：`DamengDefaultValueTextFiled.init → addItem/selectIndex`

## DamengFieldTextFiled
- 职责：字段文本框，通过弹窗从候选字段多选并以逗号拼接显示。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columns` | `List<DamengColumn>` | 候选字段 |
  | `selectedColumns` | `List<String>` | 已选字段名 |
  | `popup` | `PopupAdapter` | 字段选择弹窗 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `initPopup()` | 初始化弹窗 | 弹出 `DamengColumnFieldPopupController`，提交回收 `listView.getSelectedColumnNames()` |
  | `setColumns(List)` | 设置候选字段 | 同步 listView 初始化并刷新文本 |
  | `setSelectedColumns(List)` | 设置已选 | 同步 listView 选中并刷新文本 |
  | `getSelectedColumns()` | 取已选（非空） | 空时返回空列表 |
  | `initText()` | 刷新文本/提示 | 逗号拼接 `selectedColumns` |
  | `listView()` | 取弹窗内列表 | `lookup("#listView")` |

- 调用链：`DamengFieldTextFiled.initPopup → DamengColumnFieldPopupController`

## DamengForeignKeyPolicyComboBox
- 职责：外键删除策略下拉框（CASCADE / NO ACTION / RESTRICT / SET NULL）。
- 字段：无。
- 方法：无（实例初始化块添加选项）。
- 调用链：无。

## DamengIndexColumnListView
- 职责：索引字段选择列表，每行一个字段下拉框，回收为 `IndexColumn` 列表。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columnNames` | `List<String>` | 可选字段名 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(DamengIndex, List<DamengColumn>)` | 初始化 | 生成字段名列表并为索引已有列 `addColumn` |
  | `addColumn(IndexColumn)` | 添加一行 | 生成 `FXComboBox` 填充字段名并选中 |
  | `getColumns()` | 取索引字段 | 遍历行内下拉框值构造 `IndexColumn` |

- 调用链：`DamengIndexColumnListView.addColumn → FXComboBox.select`

## DamengIndexFieldTextFiled
- 职责：索引字段文本框，弹窗选择索引字段并在弹窗期间禁用自身。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dbIndex` | `DamengIndex` | 关联索引 |
  | `columnList` | `List<DamengColumn>` | 候选字段 |
  | `columns` | `List<DamengIndex.IndexColumn>` | 索引字段 |
  | `popup` | `PopupAdapter` | 索引字段弹窗 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `initPopup()` | 初始化弹窗 | 禁用自身、弹出 `DamengIndexFieldPopupController`，提交/隐藏时恢复并回收字段 |
  | `setColumns(List)` | 设置字段 | 刷新文本 |
  | `initText()` | 刷新文本/提示 | 逗号拼接字段名 |
  | `listView()` | 取弹窗内列表 | `lookup("#listView")` |
  | `getColumns()` | 取字段 | getter |

- 调用链：`DamengIndexFieldTextFiled.initPopup → DamengIndexFieldPopupController`

## DamengIndexMethodComboBox
- 职责：索引方法下拉框（空 / BTREE / HASH）。
- 字段：无。
- 方法：无。
- 调用链：无。

## DamengIndexTypeComboBox
- 职责：索引类型下拉框（NORMAL / UNIQUE）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `initNode()` | 初始化 | 添加 `NORMAL`、`UNIQUE` |

- 调用链：`DamengIndexTypeComboBox.initNode → addItem`

## DamengRowFormatComboBox
- 职责：行格式下拉框（COMPACT/COMPRESSED/DEFAULT/DYNAMIC/FIXED/REDUNDANT）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `select(String)` | 按值选中 | 转大写后 `select`，空则清空 |

- 调用链：无。

## DamengTableComboBox
- 职责：表选择下拉框，按 schema 加载表名。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(String schema, ShellDamengClient)` | 初始化 | 委托三参版本 |
  | `init(schema, tableName, client)` | 初始化并选中 | `client.selectTables(schema)` 取表名填充，指定表名则选中 |

- 调用链：`DamengTableComboBox.init → ShellDamengClient.selectTables`

## DamengTableSpaceComboBox
- 职责：表空间下拉框。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(ShellDamengClient)` | 初始化 | 清空后遍历 `client.tableSpaces()` 添加 |

- 调用链：`DamengTableSpaceComboBox.init → ShellDamengClient.tableSpaces`

## DamengTriggerPolicyComboBox
- 职责：触发器策略下拉框（BEFORE/AFTER × INSERT/UPDATE/DELETE）。
- 字段：无。
- 方法：无。
- 调用链：无。

---

## 参数/视图/事件（routine / view / event）

## DamengParamModeComboBox
- 职责：参数模式下拉框（IN / OUT / IN OUT）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `initNode()` | 初始化 | 添加 `IN`、`OUT`、`IN/OUT` |

- 调用链：无。

## DamengViewAlgorithmComboBox
- 职责：视图算法下拉框（UNDEFINED / MERGE / TEMPTABLE）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `select(String)` | 按值选中 | 大写后 `select`，空则清空 |

- 调用链：无。

## DamengEventStatusCombobox
- 职责：达梦事件状态下拉框，兼容 ENABLED/DISABLED/SLAVESIDE_DISABLED 别名。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `select(String)` | 按状态选中 | 将 ENABLED/DISABLED/SLAVESIDE_DISABLED 映射到 0/1/2 索引 |
  | `isSameStatus(String)` | 判断选中是否与给定状态一致 | 按 `getSelectedIndex()` 与别名比对 |

- 调用链：无。

## DamengEventIntervalTypeCombobox
- 职责：事件间隔类型下拉框（YEARLY…SECONDLY）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `select(String)` | 按值选中 | 大写后 `select` |
  | `initNode()` | 初始化 | 添加 7 种间隔类型 |

- 调用链：无。

## DamengEventOnCompletionCombobox
- 职责：事件完成后处理方式下拉框（PRESERVE / NOT PRESERVE）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `select(String)` | 按值选中 | 大写后 `select` |

- 调用链：无。

---

## 数据导入导出（data）

## DBDumpDataTypeComboBox
- 职责：数据转储类型下拉框（数据+结构 / 仅结构）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `isFull()` | 是否为数据+结构 | `getSelectedIndex() == 0` |

- 调用链：无。

## DamengDataImportFile
- 职责：数据导入文件模型，持有文件/目标表并在表格中渲染为文件路径控件与目标表下拉框。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `schema` | `String` | 模式名称 |
  | `dbClient` | `ShellDamengClient` | 数据库客户端 |
  | `fileProperty` | `ObjectProperty<File>` | 文件属性 |
  | `targetTableName` | `String` | 目标表名称 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `fileProperty()` | 取文件属性 | 懒初始化 `SimpleObjectProperty` |
  | `getFile()/getFilePath()/getFileName()/setFile()` | 文件读写 | 从属性派生 |
  | `getFilePathControl()` | 文件路径选择框 | 生成 `ChooseFileTextField` 并双向联动 |
  | `getTargetTableControl()` | 目标表下拉框 | `StageManager.showMask` 内 `comboBox.init(schema, tableName, dbClient)` |
  | `getTableName()` | 由文件名推表名 | 去扩展名 |
  | `getTargetTableName()/setTargetTableName()` | 目标表名读写 | 空时回退到 `getTableName()` |

- 调用链：`DamengDataImportFile.getTargetTableControl → DamengTableComboBox.init → ShellDamengClient.selectTables`

## DamengDataImportFileTableView
- 职责：数据导入文件表格视图（空子类）。
- 字段：无。
- 方法：无。
- 调用链：无。

## DataDateTextFiled
- 职责：数据日期格式文本框，内置常用日期时间格式候选项。
- 字段：无。
- 方法：无（实例初始化块添加多组 `yyyy-MM-dd HH:mm:ss` 等格式）。
- 调用链：无。

## DataExportColumnListView
- 职责：数据导出字段复选列表，勾选状态回写到导出列对象。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init(List<ShellDamengDataExportColumn>)` | 初始化 | 每列生成 `FXCheckBox`，选中变化写回 `column.setSelected` |

- 调用链：`DataExportColumnListView.init → FXCheckBox.selectedChanged`

## DataExportTableComboBox
- 职责：数据导出表下拉框，按 `getName()` 显示。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `initNode()` | 初始化 | 设置 `SimpleStringConverter` 后 `super.initNode()` |

- 调用链：无。

## DataExportTableTableView
- 职责：数据导出表表格视图，回收选中的导出表。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getSelectedTables()` | 取选中导出表 | 过滤 `isSelected()` |
  | `hasSelectedTable()` | 是否有选中 | 遍历判断 `isSelected()` |

- 调用链：无。

## DataFieldSeparatorComboBox
- 职责：数据字段分隔符下拉框（; , 空格）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `value()` | 取分隔符字符 | 按选中索引返回 `;`/`,`/空格 |

- 调用链：无。

## DataImportTableComboBox
- 职责：数据导入表下拉框，按导入文件表名显示。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getSelectedTableName()` | 取选中表名 | `getSelectedItem().getTableName()` |

- 调用链：无。

## DataRecordLabelComboBox
- 职责：数据记录标签下拉框（(Root) / RECORDS）。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `isRoot()` | 是否为根节点 | `getSelectedIndex() == 0` |

- 调用链：无。

## DataRecordSeparatorComboBox
- 职责：数据记录分隔符下拉框，按操作系统默认选中。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `value()` | 取分隔符 | 按索引返回 `\r\n`/`\n`/`\r` |

- 关键逻辑：初始化时依据 `OSUtil.isWindows/isLinux/isMacOS` 选中 CRLF 或 LF。
- 调用链：`DataRecordSeparatorComboBox → OSUtil.isWindows`

## DataTxtIdentifierComboBox
- 职责：数据文本标识符下拉框（双引号 / 单引号）。
- 字段：无。
- 方法：无。
- 调用链：无。
