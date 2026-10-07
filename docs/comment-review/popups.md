# popups 包代码审查

弹窗控制器包，基于 `PopupController` + `@PopupAttribute`（对应 `resources` 下 fxml），用于字段/索引列表编辑、枚举值、分页设置、字段信息与记录过滤等弹出交互。

---

## DamengColumnFieldPopupController
- 职责：字段列表弹窗业务（从候选字段中选列并支持排序）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `onSubmit` | `Runnable` | 提交回调 |
  | `listView` | `DamengColumnListView`（@FXML） | 字段列表控件 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `submit()` | 提交 | 执行 `onSubmit` 后关窗 |
  | `close()` | 关闭 | `closeWindow()` |
  | `moveUpRow()` / `moveDownRow()` | 上/下移行 | `ListViewUtil.moveUp/moveDown` |
  | `onWindowShowing(WindowEvent)` | 显示时初始化 | 读取 `onSubmit`/`columns`/`selectedColumns` 属性，`listView.init` 与 `select` |

- 调用链：`字段选择按钮 → 弹窗 → onWindowShowing → listView.init`；`确定 → submit → onSubmit.run`

## DamengIndexFieldPopupController
- 职责：索引字段列表弹窗业务。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `onSubmit` | `Runnable` | 提交回调 |
  | `listView` | `DamengIndexColumnListView`（@FXML） | 索引列列表控件 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `submit()` / `close()` | 提交/关闭 | 执行回调、关窗 |
  | `addRow()` | 添加列 | `listView.addColumn(new DamengIndex.IndexColumn())` 并选中末行 |
  | `deleteRow()` | 删除列 | `listView.removeSelectedItem()` |
  | `moveUpRow()` / `moveDownRow()` | 上/下移行 | `ListViewUtil.moveUp/moveDown` |
  | `onWindowShowing(WindowEvent)` | 初始化 | 取 `onSubmit`/`dbIndex`/`columnList`，`listView.init(dbIndex, columnList)` |

- 调用链：`索引编辑 → 弹窗 → listView.init → 编辑列 → submit`

## DamengColumnEnumPopupController
- 职责：字段值（枚举）列表弹窗业务。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `onSubmit` | `Runnable` | 提交回调 |
  | `listView` | `FXListView<ClearableTextField>`（@FXML） | 值列表控件 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `submit()` / `close()` | 提交/关闭 | 执行回调、关窗 |
  | `addRow()` | 新增值行 | `createNode("")` 加入并选中末行 |
  | `deleteRow()` | 删除行 | `listView.removeSelectedItem()` |
  | `createNode(String)` | 创建值输入组件 | 构造 `ClearableTextField` 并设置尺寸/下边框/行点击选中 |
  | `onWindowShowing(WindowEvent)` | 初始化 | 取 `onSubmit`/`values`，为每个值创建输入行 |
  | `onPopupInitialize(PopupAdapter)` | 初始化 | 自定义 `cellFactory`，用输入组件作为 graphic 并高亮 |

- 调用链：`字段枚举设置 → 弹窗 → onWindowShowing 建行 → submit`

## DamengRecordEnumPopupController
- 职责：数据枚举（多选）弹窗业务。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `onSubmit` | `Runnable` | 提交回调 |
  | `listView` | `FXListView<CheckBox>`（@FXML） | 多选项列表 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `submit()` / `close()` | 提交/关闭 | 执行回调、关窗 |
  | `onWindowShowing(WindowEvent)` | 初始化 | 取 `values`/`allValues`，为每个候选值创建 `FXCheckBox`，命中 `values` 则勾选 |

- 调用链：`记录枚举选择 → 弹窗 → onWindowShowing → 生成勾选框`

## DamengPageSettingPopupController
- 职责：页码（每页记录数）设置弹窗。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `limit` | `NumberTextField`（@FXML） | 每页限制 |
  | `setting` | `DamengSetting` | 当前设置（`SETTING`） |
  | `settingStore` | `DamengSettingStore` | 设置存储（`INSTANCE`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `apply()` | 应用 | `setting.setRecordPageLimit(limit)` → `settingStore.update` → `submit(limit)` → 关窗 |
  | `close()` | 关闭 | `closeWindow()` |
  | `onWindowShowing(WindowEvent)` | 初始化 | 用 `setting.getRecordPageLimit()` 回填输入框 |
  | `onWindowHidden(WindowEvent)` | 隐藏 | 置空 `limit` |

- 调用链：`记录分页设置 → apply → DamengSettingStore.update → submit(limit)`

## DamengFieldInfoPopupController
- 职责：字段信息弹窗业务（只读展示字段详情）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `name` | `TextField`（@FXML） | 名称 |
  | `size` | `NumberTextField`（@FXML） | 字段长 |
  | `type` | `TextField`（@FXML） | 类型 |
  | `value` | `TextField`（@FXML） | 值 |
  | `comment` | `TextArea`（@FXML） | 注释 |
  | `defaultValue` | `TextField`（@FXML） | 默认值 |
  | `sizeBox/tagsBox/valueBox/defaultValueBox` | `FXHBox`（@FXML） | 各分区容器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `close()` | 关闭 | `closeWindow()` |
  | `onWindowShowing(WindowEvent)` | 初始化 | 取 `column`；按 `supportSize/supportValue/supportDefaultValue` 显示对应分区；`DamengNodeUtil.generateTags` 生成标签并设置间距；回填名称/类型/注释 |
  | `onPopupInitialize(PopupAdapter)` | 初始化 | 各分区 `managedBindVisible` |

- 调用链：`字段信息查看 → onWindowShowing → DamengNodeUtil.generateTags`

## DamengViewRecordFilterPopupController
- 职责：视图数据过滤弹窗业务。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `filterTable` | `FXTableView<DamengRecordFilter>`（@FXML） | 过滤条件表单 |
  | `treeItem` | `DamengViewTreeItem` | 视图树节点 |
  | `columnList` | `List<DamengColumn>` | 字段列表缓存 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `apply()` | 应用 | `submit(filterTable.getItems())` 后关窗 |
  | `close()` | 关闭 | `closeWindow()` |
  | `onWindowShowing(WindowEvent)` | 初始化 | 取 `item`/`filters`，`filterTable.setItem(filters)` |
  | `onWindowHidden(WindowEvent)` | 隐藏 | 置空 `columnList` |
  | `addFilter()` | 新增过滤条件 | 懒加载 `treeItem.columns()`，设置后加入表单 |
  | `deleteFilter()` | 删除过滤条件 | 移除选中条件 |

- 调用链：`视图记录过滤 → onWindowShowing → addFilter → apply → submit(filters)`

## DamengTableRecordFilterPopupController
- 职责：表数据过滤弹窗业务（结构与视图过滤控制器一致）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `filterTable` | `FXTableView<DamengRecordFilter>`（@FXML） | 过滤条件表单 |
  | `treeItem` | `DamengTableTreeItem` | 表树节点 |
  | `columnList` | `List<DamengColumn>` | 字段列表缓存 |

- 方法：`apply`、`close`、`onWindowShowing`、`onWindowHidden`、`addFilter`、`deleteFilter`，逻辑同 `DamengViewRecordFilterPopupController`（`treeItem` 类型为表节点）。
- 调用链：`表记录过滤 → onWindowShowing → addFilter → apply → submit(filters)`

## DamengColumnConfigPopupController
- 职责：字段（列）扩展配置弹窗，编辑值/默认值/主键长度/填充零/无符号/自增/时间戳更新等。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `defaultValueBox` | `FXHBox`（@FXML） | 默认值容器 |
  | `defaultValue` | `DamengDefaultValueTextFiled`（@FXML） | 默认值控件 |
  | `valueBox` | `FXHBox`（@FXML） | 字段值容器 |
  | `value` | `DBEnumTextFiled`（@FXML） | 字段值控件 |
  | `primaryKeySizeBox` / `primaryKeySize` | `FXHBox` / `NumberTextField`（@FXML） | 主键长度容器/控件 |
  | `zeroFillBox` / `zeroFill` | `FXHBox` / `FXCheckBox`（@FXML） | 填充零容器/控件 |
  | `autoIncrementBox` / `autoIncrement` | `FXHBox` / `FXCheckBox`（@FXML） | 自增容器/控件 |
  | `unsignedBox` / `unsigned` | `FXHBox` / `FXCheckBox`（@FXML） | 无符号容器/控件 |
  | `currentTimestampBox` / `currentTimestamp` | `FXHBox` / `FXCheckBox`（@FXML） | 时间戳更新容器/控件 |
  | `dbColumn` | `DamengColumn` | 当前字段 |
  | `dbClient` | `ShellDamengClient` | 客户端 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `submit()` | 提交配置 | 按各分区可见性回写 `dbColumn` 的 value/zeroFill/unsigned/defaultValue/autoIncrement/primaryKeySize/updateOnCurrentTimestamp 后关窗 |
  | `close()` | 关闭 | `closeWindow()` |
  | `onWindowShowing(WindowEvent)` | 初始化 | 取 `dbColumn`/`dbClient`；按 `supportValue/supportDefaultValue/supportAutoIncrement/supportUnsigned/isPrimaryKey+supportKeySize/supportTimestamp` 显示分区并回填 |
  | `onPopupInitialize(PopupAdapter)` | 初始化 | 各分区 `managedBindVisible` |

- 调用链：`字段配置 → onWindowShowing 回填 → submit → dbColumn.set*`
