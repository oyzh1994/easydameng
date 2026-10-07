# 记录相关类审查

范围：`cn.oyzh.easydameng.dameng.record` 包下正式类（8 个）。
该包封装表记录的读写模型（值、原始值、变更状态）、过滤条件 UI，以及增删改查的参数对象。
跳过：`DamengRecordData.java` 整个文件被注释掉（死代码），不生成条目。

说明：标识符保留原文，术语用中文；纯 POJO 的成对属性访问器合并为一行。

---

## DamengRecord

- 职责：达梦表记录模型，按字段保存属性值并跟踪变更状态。
- 继承：`extends cn.oyzh.fx.db.DBObject implements Destroyable`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `readonly` | `boolean` | 是否只读（`final`） |
  | `editable` | `boolean` | 是否可编辑 |
  | `columns` | `DamengColumns` | 字段列表 |
  | `properties` | `HashMap<String, DamengRecordProperty>` | 字段名 → 记录属性 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengRecord(DamengColumns columns)` | 以字段列表构造（非只读） | 委托 `this(columns, false)` |
  | `DamengRecord(List<DamengColumn> columns)` | 以字段列表构造 | 包装为 `DamengColumns` 后委托 |
  | `DamengRecord(List<DamengColumn> columns, boolean readonly)` | 构造 | 包装后委托 |
  | `DamengRecord(DamengColumns columns, boolean readonly)` | 构造 | 赋值 `columns`/`readonly` |
  | `DamengRecordProperty putValue(String column, Object value)` | 按字段名写入值 | 无属性时 `putValue(new DamengColumn(column), value)`，否则 `property.setValue(value)` |
  | `DamengRecordProperty putValue(DamengColumn column, Object value)` | 按字段写入值 | 新建 `DamengRecordProperty`，对其 `changedProperty()` 加监听触发 `updateStatus()`，存入 `properties` |
  | `Object getValue(String column)` | 读取值 | `getProperty(column).get()` |
  | `Object getOriginal(String column)` | 读取原始值 | `getProperty(column).getOriginal()` |
  | `Set<String> columns()` | 字段名集合 | 返回 `properties.keySet()` |
  | `DamengRecordProperty getProperty(String key)` | 取记录属性 | `properties.get(key)` |
  | `boolean hasProperty(DamengRecordProperty)` | 是否存在该属性 | `properties.containsValue(...)` |
  | `void clear()` | 清空数据 | `properties.clear()` |
  | `void update(Map<String, Object> rowData)` | 批量更新 | 遍历 `putValue(entry.getKey(), entry.getValue())` |
  | `boolean isChanged()` | 是否变更 | 父类变更或任一属性 `isChanged()` |
  | `void clearStatus()` | 清除变更状态 | 逐属性 `setChanged(false)`+`updateOriginal()`，再 `super.clearStatus()` |
  | `void discard()` | 抛弃变更 | 逐属性 `discard()`，`super.clearStatus()` |
  | `void copy(DamengRecord record)` | 复制另一记录数据 | 遍历其 `columns()`，非空值 `putValue` 覆盖 |
  | `DBRecordData getRecordData()` | 导出全部非空记录数据 | 跳过值为空的字段；对 `supportTimestamp()` 且值为 `CURRENT_TIMESTAMP` 的字段也跳过 |
  | `DBRecordData getChangedRecordData()` | 导出已变更字段 | 仅收集 `property.isChanged()` 的字段 |
  | `DBRecordData getOriginalRecordData()` | 导出原始数据 | 收集各属性 `getOriginal()` |
  | `boolean isColumnChanged(String column)` | 字段是否变更 | `getProperty(column).isChanged()` |
  | `Map<String, Object> toMap()` | 转为映射 | 遍历属性写入 `key → value.get()` |
  | `boolean isEditable()` / `void setEditable(boolean)` | 是否可编辑的读写 | 属性访问器 |
  | `void destroy()` | 释放资源 | 清空 `columns`、逐属性 `destroy()`、清空并置空 `properties` |

- 调用链：`DamengHelper.parseColumns(ResultSet) → DamengColumns → new DamengRecord(columns) → putValue(...)`
- 调用链：`DamengRecord.getChangedRecordData()/getOriginalRecordData() → DamengUpdateRecordParam → SQL 生成/执行`

---

## DamengRecordFilter

- 职责：单条记录过滤条件，负责字段/条件/值三者对应的 UI 组件与条件表达式生成。
- 继承：`extends cn.oyzh.fx.db.DBRecordFilter`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `condition` | `DamengCondition` | 当前条件对象 |
  | `valueBox` | `FXHBox` | 条件值容器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `Object value()` | 读取条件值 | 容器为空时返回 `this.value`，否则 `DamengConditionUtil.getNodeVal(valueBox.getChildren())` 并缓存 |
  | `Node getValueControl()` | 获取值组件 | 先 `updateValueControl()` 再返回值容器 |
  | `void updateValueControl()` | 生成/刷新值组件 | 懒建 `FXHBox`（`FlexUtil.flexWidth`）；由 `DamengConditionUtil.generateNode(column, condition)` 生成节点并回填值；按 1/2 个节点分别设置 flex 宽度、提示文案、鼠标点击选中行 |
  | `DBColumnComboBox getColumnControl()` | 字段选择组件 | 新建 `DBColumnComboBox(columns)`，选中变化时更新 `column` 与值组件 |
  | `DBConditionComboBox getConditionControl()` | 条件选择组件 | 新建 `DBConditionComboBox(DBDialect.DAMENG)`，选中变化时更新 `condition` 与值组件 |
  | `String column()` | 字段名 | `this.column.getName()` |
  | `String condition()` | 条件表达式 | `this.condition.wrapCondition(this.value())` |
  | `boolean isRequireCondition()` | 是否需要条件值 | `this.condition.isRequireCondition()` |
  | `DamengCondition getCondition()` / `void setCondition(DamengCondition)` | 条件读写 | 属性访问器 |

- 调用链：`DamengRecordFilter.getConditionControl() → DBConditionComboBox(DBDialect.DAMENG) → updateValueControl()`
- 调用链：`DamengConditionUtil.buildCondition(filters) → filter.condition() → DamengCondition.wrapCondition(value)`

---

## DamengRecordProperty

- 职责：单字段的记录属性，负责单元格编辑节点与值的双向同步、变更与原始值管理。
- 继承：`extends cn.oyzh.fx.db.DBRecordProperty`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `column` | `DamengColumn` | 所属表字段 |
  | `record` | `DamengRecord` | 所属表记录 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengRecordProperty(DamengRecord record, DamengColumn column, Object value, boolean readonly)` | 构造 | `super(value)`；非只读时 `original = value`；赋值 `readonly` |
  | `Object get()` | 读取当前值 | 只读或未变更或无编辑节点时 `super.get()`；`setToNullFlag` 时返回 null；否则 `DamengNodeUtil.getNodeVal(node)` |
  | `void set(Object newValue)` | 设置值 | `super.set(newValue)` 并同步到编辑节点 |
  | `Object getValue()` | 获取单元格显示内容 | 只读时返回格式化值；否则懒建编辑节点（`DamengRecordUtil.getNode`）并绑定 `rowOnCtrlS`、`selectRowOnMouseClicked` |
  | `void discard()` | 抛弃变更 | 已变更且有节点时回填原值，`setChanged(false)` |
  | `void setChanged(boolean changed)` | 设置变更状态 | 通知 `DBStatusListenerManager` 对应监听器 `changed(...)`；取消变更且为 `BinaryTextFiled` 时重新格式化；重置 `setToNullFlag` |
  | `void updateOriginal()` | 更新原始值 | 从编辑节点读值写入 `super.set(...)` 并更新 `original` |
  | `void vCopyAsInsertSql()` | 复制为 INSERT 语句 | `DamengDataUtil.toInsertSql(columns, record, true)` → `ClipboardUtil.copy` |
  | `void vCopyAsUpdateSql()` | 复制为 UPDATE 语句 | `DamengDataUtil.toUpdateSql(columns, record)` → `ClipboardUtil.copy` |
  | `DamengColumn getColumn()` / `void setColumn(DamengColumn)` | 字段读写 | 属性访问器 |
  | `void destroy()` | 释放资源 | 有节点时置空 `column`/`record` 并 `super.destroy()` |

- 调用链：`DamengRecord.putValue(...) → new DamengRecordProperty(...) → getValue() → DamengRecordUtil.getNode(...)`
- 调用链：`DamengRecordProperty.vCopyAsInsertSql() → DamengDataUtil.toInsertSql(...) → ClipboardUtil.copy(...)`

---

## DamengRecordPrimaryKey

- 职责：记录主键信息载体，保存主键字段、当前值、原始值与自增返回信息。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `data` | `Object` | 当前数据 |
  | `columnName` | `String` | 字段名称 |
  | `column` | `DamengColumn` | 字段 |
  | `returnData` | `Object` | 自增（IDENTITY）返回值 |
  | `originalData` | `Object` | 编辑前的原始数据 |
  | `autoIncrement` | `boolean` | 是否自增 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void init(DamengColumn column, DamengRecord record)` | 初始化主键信息 | 取 `column.getName()`、`column.isAutoIncrement()`，并读取 `record.getValue/getOriginal` |
  | `Object data()` | 取主键数据 | 优先 `data`，否则 `returnData` |
  | `Object originalData()` | 取主键原始数据 | 优先 `originalData`，否则 `data` |
  | `boolean shouldReturnData()` | 是否需要回填数据 | `data == null && autoIncrement` |
  | `boolean isChanged()` | 主键是否变更 | `originalData` 为空返回 false，否则 `!Objects.equals(originalData, data)` |
  | `getData/setData`、`getColumnName/setColumnName`、`getReturnData/setReturnData`、`getOriginalData/setOriginalData`、`isAutoIncrement/setAutoIncrement`、`getColumn` | 属性访问器 | — |

- 调用链：`DamengRecordPrimaryKey.init(column, record) → record.getValue/getOriginal(...)`
- 调用链：`DamengRecordPrimaryKey.shouldReturnData()/data() → 新增记录后回填自增主键`

---

## DamengSelectRecordParam

- 职责：查询记录参数（分页、库表、字段、过滤、主键）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `start` | `Long` | 起始位置 |
  | `limit` | `Long` | 查询条数 |
  | `schema` | `String` | 数据库模式名 |
  | `tableName` | `String` | 表名 |
  | `readonly` | `boolean` | 是否只读 |
  | `columns` | `List<DamengColumn>` | 字段列表 |
  | `filters` | `List<DamengRecordFilter>` | 过滤条件列表 |
  | `primaryKey` | `DamengRecordPrimaryKey` | 主键信息 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `boolean hasPageControl()` | 是否分页 | `start != null && limit != null` |
  | `getStart/setStart`、`getLimit/setLimit`、`getSchema/setSchema`、`getTableName/setTableName`、`isReadonly/setReadonly`、`getColumns/setColumns`、`getFilters/setFilters`、`getPrimaryKey/setPrimaryKey` | 属性访问器 | — |

- 调用链：`调用方 new DamengSelectRecordParam() → setFilters/setColumns → (查询记录)`

---

## DamengInsertRecordParam

- 职责：新增记录参数（模式、表、记录数据、主键）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `schema` | `String` | 数据库模式名 |
  | `tableName` | `String` | 表名 |
  | `record` | `DBRecordData` | 记录数据 |
  | `primaryKey` | `DamengRecordPrimaryKey` | 主键信息 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getSchema/setSchema`、`getTableName/setTableName`、`getRecord/setRecord`、`getPrimaryKey/setPrimaryKey` | 属性访问器 | — |

- 调用链：`DamengRecord.getRecordData() → new DamengInsertRecordParam() → (执行插入)`

---

## DamengUpdateRecordParam

- 职责：修改记录参数（模式、表、原始记录、变更记录、主键）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `schema` | `String` | 数据库模式名 |
  | `tableName` | `String` | 表名 |
  | `record` | `DBRecordData` | 原始记录数据 |
  | `updateRecord` | `DBRecordData` | 修改后的记录数据 |
  | `primaryKey` | `DamengRecordPrimaryKey` | 主键信息 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getSchema/setSchema`、`getTableName/setTableName`、`getRecord/setRecord`、`getUpdateRecord/setUpdateRecord`、`getPrimaryKey/setPrimaryKey` | 属性访问器 | — |

- 调用链：`DamengRecord.getOriginalRecordData()/getChangedRecordData() → new DamengUpdateRecordParam() → (执行更新)`

---

## DamengDeleteRecordParam

- 职责：删除记录参数（模式、表、记录数据、主键）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `schema` | `String` | 数据库模式名 |
  | `tableName` | `String` | 表名 |
  | `record` | `DBRecordData` | 记录数据 |
  | `primaryKey` | `DamengRecordPrimaryKey` | 主键信息 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getSchema/setSchema`、`getTableName/setTableName`、`getRecord/setRecord`、`getPrimaryKey/setPrimaryKey` | 属性访问器 | — |

- 调用链：`DamengRecordPrimaryKey → new DamengDeleteRecordParam() → (执行删除)`

---

## 覆盖统计

- 覆盖类数：8 个。
- 类名清单：`DamengRecord`、`DamengRecordFilter`、`DamengRecordProperty`、`DamengRecordPrimaryKey`、`DamengSelectRecordParam`、`DamengInsertRecordParam`、`DamengUpdateRecordParam`、`DamengDeleteRecordParam`。
- 跳过：`DamengRecordData`（整文件被注释，死代码）。
