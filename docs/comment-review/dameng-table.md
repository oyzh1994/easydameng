# 表/字段/索引/约束/触发器相关类审查

范围：`cn.oyzh.easydameng.dameng` 下的 `table`、`column`、`index`、`check`、`foreignKey`、`trigger` 六个子包中的正式类（16 个 + 1 个内部类）。
这些类描述达梦的库对象模型（表、字段、索引、检查约束、外键、触发器）及其编辑控件，多数继承自 fx-base 的 `DBObject` / `DBTable` / `DBColumn` 等接口体系，并通过 `xxxControl` 子类提供 JavaFX 表单控件。

跳过（整文件被注释，死代码）：`DamengIndexes`、`DamengChecks`、`DamengForeignKeys`、`DamengTriggers`。

---

## 一、表（table）

### DamengTable

- 职责：达梦表模型，保存名称、模式、注释、表空间、建表定义等。
- 继承：`extends DBObject implements DBTable, ObjectCopier<DamengTable>, ObjectComparator<DamengTable>`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `hasPrimaryKey` | `boolean` | 是否有主键 |
  | `createDefinition` | `String` | 表创建定义 |
  | `tableSpace` | `String` | 表空间 |
  | `schema` | `String` | 模式名称 |
  | `nameProperty` | `SimpleStringProperty` | 表名称属性（懒初始化） |
  | `commentProperty` | `SimpleStringProperty` | 表注释属性（懒初始化） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void setTableSpace(String)` / `String getTableSpace()` | 表空间读写 | 写入时 `super.putOriginalData("tableSpace", ...)` 记录原始值 |
  | `boolean isTableSpaceChanged()` | 表空间是否变更 | `super.checkOriginalData("tableSpace", this.tableSpace)` |
  | `boolean hasTableSpace()` / `boolean hasComment()` | 是否含表空间/注释 | 判空 |
  | `void copy(DamengTable table)` | 复制表信息 | 复制 comment、tableSpace、hasPrimaryKey、createDefinition |
  | `SimpleStringProperty nameProperty()` / `void setName` / `String getName` | 名称属性与读写 | 懒建 `SimpleStringProperty` |
  | `SimpleStringProperty commentProperty()` / `void setComment` / `String getComment` | 注释属性与读写 | 懒建 `SimpleStringProperty` |
  | `boolean compare(DamengTable table)` | 表比较 | 自反或 name+schema 相等即视为同一表 |
  | `isHasPrimaryKey/setHasPrimaryKey`、`getCreateDefinition/setCreateDefinition`、`getSchema/setSchema` | 属性访问器 | — |

- 调用链：`DamengCreateTableParam.schema()/tableName() → DamengTable.getSchema()/getName()`
- 调用链：`DamengTableCreateSqlGenerator → DamengTable.getTableSpace()/getComment() → 生成建表 SQL`

---

### DamengCreateTableParam

- 职责：创建表参数，聚合表、字段、索引、外键、检查约束、触发器。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `table` | `DamengTable` | 表 |
  | `checks` | `DBObjects<DamengCheck>` | 检查约束集合 |
  | `columns` | `DamengColumns` | 字段集合 |
  | `indexes` | `DBObjects<DamengIndex>` | 索引集合 |
  | `triggers` | `DBObjects<DamengTrigger>` | 触发器集合 |
  | `foreignKeys` | `DBObjects<DamengForeignKey>` | 外键集合 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `String schema()` | 库名称 | `table.getSchema()` |
  | `String tableName()` / `void setTableName(String)` | 表名称读写 | 委托 `table.getName()/setName()` |
  | `List<DamengColumn> primaryKeys()` | 主键字段列表 | `columns.primaryKeys()` |
  | `boolean hasColumns()/hasIndex()/hasForeignKey()/hasCheck()/hasTrigger()` | 集合非空判断 | `CollectionUtil.isNotEmpty(...)` |
  | `getTable/setTable`、`getChecks/setChecks`、`getColumns/setColumns`、`getIndexes/setIndexes`、`getTriggers/setTriggers`、`getForeignKeys/setForeignKeys` | 属性访问器 | — |

- 调用链：`DamengTableCreateSqlGenerator → DamengCreateTableParam.getColumns()/primaryKeys()/hasForeignKey() → 拼接 CREATE TABLE`

---

### DamengAlertTableParam

- 职责：修改表参数，在 `DamengCreateTableParam` 基础上增加主键/字段变更检测与自增标记。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `table` | `DamengTable` | 表 |
  | `checks` | `DBObjects<DamengCheck>` | 检查约束集合 |
  | `columns` | `DamengColumns` | 字段集合 |
  | `indexes` | `DBObjects<DamengIndex>` | 索引集合 |
  | `triggers` | `DBObjects<DamengTrigger>` | 触发器集合 |
  | `foreignKeys` | `DBObjects<DamengForeignKey>` | 外键集合 |
  | `existAutoIncrement` | `boolean` | 是否存在自动递增 |
  | `primaryKeys` | `List<String>` | 主键字段名列表 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `boolean primaryKeysChanged()`（`primaryKeyChanged`） | 主键是否已变更 | 遍历字段，`isPrimaryKeyChanged()` 或（`isCreated() && isPrimaryKey()`）即视为变更 |
  | `boolean columnChanged()` | 字段是否已变更 | 遍历字段，存在 `isDeleted()`/`isCreated()`/`isColumnChanged()` 即变更 |
  | `List<DamengColumn> primaryKeys()` | 主键字段列表 | `columns.primaryKeys()` |
  | `boolean isExistPrimaryKey()` | 是否存在主键 | `CollectionUtil.isNotEmpty(primaryKeys)` |
  | `boolean hasColumns()/hasIndex()/hasForeignKey()/hasCheck()/hasTrigger()` | 集合非空判断 | `CollectionUtil.isNotEmpty(...)` |
  | `String tableName()`/`setTableName`、`String getSchema()` | 库表名 | 委托 `table` |
  | `getTable/setTable`、`getChecks/setChecks`、`getColumns/setColumns`、`getIndexes/setIndexes`、`getTriggers/setTriggers`、`getForeignKeys/setForeignKeys`、`isExistAutoIncrement/setExistAutoIncrement`、`getPrimaryKeys/setPrimaryKeys` | 属性访问器 | — |

- 调用链：`DamengTableAlertSqlGenerator → DamengAlertTableParam.columnChanged()/primaryKeyChanged() → 生成 ALTER TABLE`

---

### DamengSelectTableParam

- 职责：查询表参数（模式、表名、是否查询完整定义）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `full` | `boolean` | 是否查询完整定义 |
  | `schema` | `String` | 库名称/模式名称 |
  | `tableName` | `String` | 表名称 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `isFull/setFull`、`getSchema/setSchema`、`getTableName/setTableName` | 属性访问器 | — |

- 调用链：`ShellDamengClient.selectTable(param) → DamengSelectTableParam → (查询表定义)`

---

## 二、字段（column）

### DamengColumn

- 职责：达梦字段（列）模型，承载类型、长度、精度、默认值、主键、自增等，并跟踪各属性变更。
- 继承：`extends DBObject implements DBColumn, ObjectCopier<DamengColumn>, Destroyable`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `schema` | `String` | 模式名称 |
  | `tableName` | `String` | 表名称 |
  | `size` | `Integer` | 字段大小（长度） |
  | `typeProperty` | `StringProperty` | 字段类型属性 |
  | `valueProperty` | `StringProperty` | 字段值属性 |
  | `comment` | `String` | 注释 |
  | `nullable` | `Boolean` | 是否可为 NULL |
  | `unsigned` | `Boolean` | 是否无符号 |
  | `zeroFill` | `Boolean` | 是否填充零 |
  | `updateOnCurrentTimestamp` | `Boolean` | 是否根据当前时间戳更新 |
  | `position` | `Integer` | 字段位置 |
  | `primaryKeyProperty` | `SimpleBooleanProperty` | 主键属性 |
  | `primaryKeySize` | `Integer` | 键长度 |
  | `defaultValue` | `Object` | 默认值 |
  | `digits` | `Integer` | 小数位 |
  | `autoIncrement` | `Boolean` | 是否自动递增 |
  | `name` | `String` | 名称 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengColumn()` / `DamengColumn(String name)` | 构造字段 | — |
  | `void setType(String)` / `String getType()` | 类型读写 | 存入前 `toUpperCase()`，并 `putOriginalData("type", ...)` |
  | `boolean isTypeChanged()/isSizeChanged()/isNameChanged()/isCommentChanged()/isDefaultValueChanged()/isAutoIncrementChanged()` | 各属性变更检测 | 逐一 `super.checkOriginalData(键, 当前值)` |
  | `String originalName()` | 原始名称 | `getOriginalData("name")` |
  | `boolean isColumnChanged()` | 字段是否变更 | 遍历 `originalData()`，存在非 `primaryKey`/`primaryKeySize` 键即视为变更 |
  | `boolean isPrimaryKeyChanged()` | 主键是否变更 | 5 项判定：主键、键长、新建且主键、改名且主键、删除且主键 |
  | `void initStatus()` | 初始化状态 | 对 size/value/digits/unsigned/zeroFill/autoIncrement/updateOnCurrentTimestamp 的空值调用 setter 记录原始值 |
  | `List<String> getValueList()` | 解析值列表 | 按 `,` 切分 `getValue()`，去掉单引号包裹 |
  | `Object getDefaultValueFix()` | 修正后的默认值 | 整型/小数类型时把 `null`/空串归一为 null、数字转 Long/Double |
  | `void parseKey(String key)` | 解析主键标识 | `"pri"` 时 `setPrimaryKey(true)` |
  | `void copy(DamengColumn column)` | 复制字段 | 逐属性 setter | 
  | `supportSize/supportUnsigned/supportDigits/supportInteger/supportAutoIncrement/supportDefaultValue/supportTimestamp/supportValue/supportBit/supportBoolean/supportJson/supportJsonArray/supportText/supportKeySize/supportString/supportBinary` | 类型能力判断 | 均委托 `DBColumnFieldManager.xxx(DBDialect.DAMENG, this.getType())` |
  | `minValue()/maxValue()/exampleValue()` | 取值范围/示例 | `DBColumnFieldManager.xxx(DAMENG, type)` |
  | `isYearType/isDateType/isDateTimeType/isTimeType()` | 时间类型判断 | 委托 `DamengColumnUtil.xxx(type)` |
  | `void destroy()` | 释放属性绑定 | `unbind()` 三个 Property 后 `super.destroy()` |
  | 各 `getX/setX`、`xxxProperty()` | 属性访问器 | setter 大多附带 `putOriginalData` 记录原始值 |

- 调用链：`DamengHelper.parseColumns(ResultSet) → new DamengColumn() → setDigits/setName/setType/...`
- 调用链：`DamengColumn.supportSize() → DBColumnFieldManager.supportSize(DBDialect.DAMENG, type)`

---

### DamengColumns

- 职责：达梦字段列表（`DBObjectList<DamengColumn>` 子类），提供按名查列、主键排序等工具方法。
- 继承：`extends DBObjectList<DamengColumn>`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengColumns()` / `DamengColumns(List<DamengColumn> list)` | 构造字段列表 | `addAll(list)` |
  | `List<DamengColumn> primaryKeys()` | 主键字段列表（自增优先） | 过滤未删除的主键字段，再按 `isAutoIncrement()` 排序（自增排前） |
  | `boolean primaryKeyChanged()` | 主键是否变更 | 恒返回 `false`（占位实现） |
  | `DamengColumn column(String name)` | 按名取字段 | 忽略大小写比较 `getName()` |
  | `int index(String name)` | 字段位置索引 | 遍历直到名称相等 |
  | `List<DamengColumn> sortOfPosition()` | 按位置排序 | `Comparator.comparing(DamengColumn::getPosition)` |
  | `String tableName()` / `String schema()` | 表名/模式名 | 取首个字段的 `getTableName()/getSchema()` |
  | `List<String> columnNames()` | 字段名列表 | 收集 name |
  | `boolean hasPrimaryKey()` | 是否含主键 | 任一字段 `isPrimaryKey()` 或 `isAutoIncrement()` |
  | `boolean hasAutoIncrement()` | 是否含自增字段 | 任一字段 `isAutoIncrement()` |

- 调用链：`DamengRecord(DamengColumns) → DamengColumns.column(name) → 取值`
- 调用链：`DamengCreateTableParam.primaryKeys() → DamengColumns.primaryKeys()`

---

### DamengColumnControl

- 职责：字段的表单编辑控件工厂，继承 `DamengColumn`，为各属性生成 JavaFX 控件。
- 继承：`extends DamengColumn`
- 字段：无自声明字段（继承 `DamengColumn`）。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `ClearableTextField getNameControl()` | 名称控件 | 双向同步 `setName`；`rowOnCtrlS`、`selectRowOnMouseClicked` |
  | `ClearableTextField getCommentControl()` | 注释控件 | 同步 `setComment` |
  | `NumberTextField getSizeControl()` | 长度控件 | 同步 `setSize(textField.getIntValue())`；支持推荐长度 |
  | `NumberTextField getDigitsControl()` | 小数位控件 | 同步 `setDigits(...)` |
  | `DBFiledTypeComboBox getTypeControl()` | 类型控件 | `setDialect(DAMENG)`，选中变更 `setType(newValue)` |
  | `FXCheckBox getNullableControl()` | 可空控件 | 选中变更 `setNullable`；监听 `primaryKeyProperty()` 为真时取消可空 |
  | `FXCheckBox getPrimaryKeyControl()` | 主键控件 | 选中变更 `setPrimaryKey` |
  | `static DamengColumnControl of(DamengColumn)` | 单字段转控件 | `copy(column)` |
  | `static List<DamengColumnControl> of(List<DamengColumn>)` | 批量转控件 | 逐个 `of(column)` |

- 调用链：`DamengColumnControl.of(column) → copy(column) → getNameControl()/getTypeControl() 等渲染表单`

---

### DamengSelectColumnParam

- 职责：查询字段参数（模式/表名）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `schema` | `String` | 库名称/模式名称 |
  | `tableName` | `String` | 表名称 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengSelectColumnParam()` / `DamengSelectColumnParam(String schema, String tableName)` | 构造参数 | — |
  | `getSchema/setSchema`、`getTableName/setTableName` | 属性访问器 | — |

- 调用链：`DamengForeignKeyControl.getPrimaryKeyColumnControl() → dbClient.selectColumns(new DamengSelectColumnParam(dbName, tableName))`

---

## 三、索引（index）

### DamengIndex

- 职责：达梦表索引模型，包含类型（NORMAL/UNIQUE）、方式（BTREE/HASH）与索引字段列表。
- 继承：`extends DBObject implements ObjectCopier<DamengIndex>`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `seqIndex` | `int` | 索引顺序 |
  | `type` | `String` | 类型（NORMAL/UNIQUE） |
  | `method` | `String` | 方式（空/BTREE/HASH） |
  | `name` | `String` | 名称 |
  | `columns` | `List<IndexColumn>` | 索引字段列表 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `String originalName()` | 原始名称 | `getOriginalData("name")` |
  | `void setName(String)` / `String getName()` | 名称读写 | 写入附带 `putOriginalData("name", ...)` |
  | `void addColumn(String column)` | 追加字段 | 懒建集合并 `new IndexColumn(column)` |
  | `void setColumns(List<IndexColumn>)` | 设置字段列表 | 附带 `putOriginalData` |
  | `void type(String type, int noneUnique)` | 依类型+唯一性推导 type/method | HASH/BTREE 结合 `noneUnique`（0 唯一）映射为 UNIQUE/NORMAL + 方式；默认 NORMAL+BTREE |
  | `String typeName()` / `String methodName()` | 类型/方式名称 | NORMAL 返回 null；method 空转 null |
  | `boolean isUnique()` | 是否唯一索引 | `method` 与 `"UNIQUE"` 忽略大小写比较 |
  | `boolean isInvalid()` | 是否无效 | name/type/columns 任一为空即无效 |
  | `void copy(DamengIndex t1)` | 复制索引 | 复制 name/type/method/columns/seqIndex |
  | `getSeqIndex/setSeqIndex`、`getType`、`getMethod` | 属性访问器 | — |

- 内部类 `IndexColumn`：索引字段，字段 `columnName`；方法 `IndexColumn()`、`IndexColumn(String)`、`equals(Object)`（按 columnName 比较）、`getColumnName/setColumnName`。

- 调用链：`ShellDamengClient.listIndexes(...) → DamengIndex.type(type, noneUnique)/addColumn(...)`
- 调用链：`DamengTableAlertSqlGenerator → DamengIndex.isInvalid()/isUnique() → 生成 CREATE/DROP INDEX`

---

### DamengIndexControl

- 职责：索引的表单编辑控件工厂，继承 `DamengIndex`。
- 继承：`extends DamengIndex`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columnList` | `List<DamengColumn>` | 候选字段列表 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void setColumnList(List<DamengColumn>)` | 设置候选字段 | — |
  | `ClearableTextField getNameControl()` | 名称控件 | 空名时 `DBUtil.genIndexName()` 生成默认名 |
  | `DamengIndexFieldTextFiled getColumnControl()` | 字段控件 | 以 `columnList` 与当前 `getColumns()` 构造，变更回写 `setColumns(...)` |
  | `DamengIndexTypeComboBox getTypeControl()` | 类型控件 | 选中变更 `setType`；初始化 `setType(comboBox.getValue())` |
  | `DamengIndexMethodComboBox getMethodControl()` | 方式控件 | 选中变更 `setMethod` |
  | `static DamengIndexControl of(DamengIndex)` / `of(List<DamengIndex>)` | 转换控件对象 | `copy(index)` |

- 调用链：`DamengIndexControl.of(index) → getTypeControl()/getColumnControl() 渲染索引编辑表单`

---

## 四、检查约束（check）

### DamengCheck

- 职责：达梦检查约束模型（名称、子语句 clause、库表）。
- 继承：`extends DBObject implements DBCheck, ObjectCopier<DamengCheck>`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `schema` | `String` | 库名称 |
  | `tableName` | `String` | 表名称 |
  | `name` | `String` | 名称 |
  | `clause` | `String` | 子语句（CHECK 表达式） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengCheck()` / `DamengCheck(String name)` | 构造检查约束 | — |
  | `void setName(String)` / `String getName()` | 名称读写 | 写入附带 `putOriginalData("name")` |
  | `boolean isNameChanged()` / `String originalName()` | 名称变更/原始名 | `checkOriginalData`/`getOriginalData` |
  | `void setClause(String)` / `String getClause()` | 子语句读写 | 写入附带 `putOriginalData("clause")` |
  | `boolean isClauseChanged()` | 子语句是否变更 | `checkOriginalData("clause", this.clause)` |
  | `boolean isInvalid()` | 是否无效 | 父类无效或 `clause` 为空 |
  | `void copy(DamengCheck check)` | 复制 | 复制 name/schema/clause/tableName |
  | `getSchema/setSchema`、`getTableName/setTableName` | 属性访问器 | — |

- 调用链：`DamengTableAlertSqlGenerator → DamengCheck.isInvalid()/getClause() → 生成 ADD/DROP CONSTRAINT`

---

### DamengCheckControl

- 职责：检查约束的表单编辑控件工厂，继承 `DamengCheck`。
- 继承：`extends DamengCheck`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `ClearableTextField getNameControl()` | 名称控件 | 空名时 `DBUtil.genCheckName()`；同步 `setName` |
  | `ClearableTextField getClauseControl()` | 子语句控件 | 同步 `setClause` |
  | `static DamengCheckControl of(DamengCheck)` / `of(List<DamengCheck>)` | 转换控件对象 | `copy(check)` |

- 调用链：`DamengCheckControl.of(check) → getClauseControl() → setClause(...)`

---

## 五、外键（foreignKey）

### DamengForeignKey

- 职责：达梦外键模型（外键字段、引用库/表/字段、删除与更新策略）。
- 继承：`extends DBObject implements DBForeignKey, ObjectCopier<DamengForeignKey>`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `name` | `String` | 外键名称 |
  | `columns` | `List<String>` | 外键字段列表 |
  | `primaryKeyDatabaseProperty` | `SimpleStringProperty` | 引用库名称属性 |
  | `primaryKeyTableProperty` | `SimpleStringProperty` | 引用表名称属性 |
  | `primaryKeyColumns` | `List<String>` | 引用字段列表 |
  | `deletePolicy` | `String` | 外键删除策略 |
  | `updatePolicy` | `String` | 外键更新策略 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `String originalName()` | 原始名称 | `getOriginalData("name")` |
  | `SimpleStringProperty primaryKeyDatabaseProperty()` / `primaryKeyTableProperty()` | 引用库/表属性 | 懒建 `SimpleStringProperty` |
  | `void setName(String)` / `String getName()` | 名称读写 | 写入附带 `putOriginalData` |
  | `void setColumns(List<String>)` / `List<String> getColumns()` | 外键字段读写 | 写入附带 `putOriginalData` |
  | `void setPrimaryKeyDatabase(String)` / `String getPrimaryKeyDatabase()` | 引用库读写 | 经属性 set/get，附带 `putOriginalData` |
  | `void setPrimaryKeyTable(String)` / `String getPrimaryKeyTable()` | 引用表读写 | 经属性 set/get，附带 `putOriginalData` |
  | `void setPrimaryKeyColumns(List<String>)` / `getPrimaryKeyColumns()` | 引用字段读写 | 写入附带 `putOriginalData` |
  | `void addColumn(String)` / `void addPrimaryKeyColumn(String)` | 追加外键/引用字段 | 懒建列表后 add |
  | `void setDeletePolicy(String)` / `setUpdatePolicy(String)` 及 getter | 策略读写 | 写入附带 `putOriginalData` |
  | `boolean isInvalid()` | 是否无效 | 父类无效，或外键/引用字段为空，或引用表/库为空 |
  | `void copy(DamengForeignKey t1)` | 复制外键 | 复制各字段与属性 |

- 调用链：`ShellDamengClient.listForeignKeys(...) → DamengForeignKey.addColumn/addPrimaryKeyColumn`
- 调用链：`DamengTableAlertSqlGenerator → DamengForeignKey.isInvalid()/getDeletePolicy() → 生成外键 DDL`

---

### DamengForeignKeyControl

- 职责：外键的表单编辑控件工厂，继承 `DamengForeignKey`，携带库模式与数据库客户端用于联动选择。
- 继承：`extends DamengForeignKey`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `schema` | `String` | 库名称 |
  | `dbClient` | `ShellDamengClient` | 数据库客户端 |
  | `columnList` | `List<DamengColumn>` | 候选字段列表 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void setSchema(String)` / `void setDbClient(ShellDamengClient)` / `void setColumnList(List<DamengColumn>)` | 注入依赖 | — |
  | `FXTextField getNameControl()` | 名称控件 | 空名时 `DBUtil.genForeignKeyName()`；异常时 `MessageBox.exception` |
  | `DamengFieldTextFiled getColumnControl()` | 外键字段控件 | 变更回写 `setColumns(textField.getSelectedColumns())` |
  | `DBSchemaComboBox getPrimaryKeyDatabaseControl()` | 引用库控件 | `comboBox.init(dbClient)`，选中变更 `setPrimaryKeyDatabase` |
  | `DamengTableComboBox getPrimaryKeyTableControl()` | 引用表控件 | `init(getPrimaryKeyDatabase(), dbClient)`；监听 `primaryKeyDatabaseProperty()` 变化重建并选中首项 |
  | `DamengForeignKeyPolicyComboBox getDeletePolicyControl()` | 删除策略控件 | 选中变更 `setDeletePolicy` |
  | `DamengFieldTextFiled getPrimaryKeyColumnControl()` | 引用字段控件 | 变更时按库/表 `dbClient.selectColumns(new DamengSelectColumnParam(dbName, tableName))` 重新加载 |
  | `DamengForeignKeyPolicyComboBox getUpdatePolicyControl()` | 更新策略控件 | 选中变更 `setUpdatePolicy` |
  | `static DamengForeignKeyControl of(DamengForeignKey)` / `of(List<...>)` | 转换控件对象 | `copy(foreignKey)` |
  | `String getPrimaryKeyDatabase()` | 引用库名称 | 父类为空时回退 `schema` |

- 调用链：`getPrimaryKeyTableControl() → primaryKeyTableProperty().addListener → getPrimaryKeyColumnControl() → dbClient.selectColumns(...)`

---

## 六、触发器（trigger）

### DamengTrigger

- 职责：达梦触发器模型（名称、策略、定义、表名）。
- 继承：`extends DBObject implements DBTrigger, ObjectCopier<DamengTrigger>`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `name` | `String` | 名称 |
  | `policy` | `String` | 触发策略（时机+操作） |
  | `definition` | `String` | 触发器定义体 |
  | `tableName` | `String` | 表名 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `String originalName()` | 原始名称 | `getOriginalData("name")` |
  | `void setName(String)` / `String getName()` | 名称读写 | 写入附带 `putOriginalData` |
  | `void setPolicy(String)` / `String getPolicy()` | 策略读写 | 写入附带 `putOriginalData` |
  | `void setPolicy(String timing, String manipulation)` | 由时机/操作组合策略 | `timing.toUpperCase() + " " + manipulation.toUpperCase()` |
  | `void setDefinition(String)` / `String getDefinition()` | 定义读写 | 写入附带 `putOriginalData` |
  | `void setTableName(String)` / `String getTableName()` | 表名读写 | 写入附带 `putOriginalData` |
  | `void copy(DamengTrigger t1)` | 复制触发器 | 复制 name/policy/definition |

- 调用链：`DamengHelper.fixTiggerDefinition(definition) → DamengTrigger.setDefinition(...)`
- 调用链：`DamengTableAlertSqlGenerator → DamengTrigger.getPolicy()/getDefinition() → 生成触发器 DDL`

---

### DamengTriggerControl

- 职责：触发器的表单编辑控件工厂，继承 `DamengTrigger`。
- 继承：`extends DamengTrigger`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `ClearableTextField getNameControl()` | 名称控件 | 空名时 `DBUtil.genTriggerName()`；同步 `setName` |
  | `DamengTriggerPolicyComboBox getPolicyControl()` | 策略控件 | 选中变更 `setPolicy` |
  | `EditorEnlargeTextFiled getDefinitionControl()` | 定义控件 | SQL 编辑器（`EditorFormatType.SQL`），变更 `setDefinition` |
  | `static DamengTriggerControl of(DamengTrigger)` / `of(List<DamengTrigger>)` | 转换控件对象 | `copy(trigger)` |

- 调用链：`DamengTriggerControl.of(trigger) → getDefinitionControl() → setDefinition(...)`

---

## 覆盖统计

- 覆盖类数：16 个顶层类 + 1 个内部类（`DamengIndex.IndexColumn`）。
- 类名清单：
  - table：`DamengTable`、`DamengCreateTableParam`、`DamengAlertTableParam`、`DamengSelectTableParam`
  - column：`DamengColumn`、`DamengColumns`、`DamengColumnControl`、`DamengSelectColumnParam`
  - index：`DamengIndex`（含内部类 `IndexColumn`）、`DamengIndexControl`
  - check：`DamengCheck`、`DamengCheckControl`
  - foreignKey：`DamengForeignKey`、`DamengForeignKeyControl`
  - trigger：`DamengTrigger`、`DamengTriggerControl`
- 跳过（死代码）：`DamengIndexes`、`DamengChecks`、`DamengForeignKeys`、`DamengTriggers`。
