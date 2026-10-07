# 存储过程/函数/视图/事件相关类审查

范围：`cn.oyzh.easydameng.dameng` 下的 `routine`、`function`、`view`、`event` 四个子包中的正式类（10 个）。
`routine` 提供函数/存储过程的基础模型与参数对象，`function`/`view` 分别提供函数与视图的模型及创建/修改/查询参数。

跳过（整文件被注释，死代码）：`event/DamengEvent`、`event/DamengEvents`。

---

## 一、程序基础（routine）

### DamengRoutineSchema

- 职责：达梦函数/存储过程的公共基础模型（名称、定义、创建定义、参数、安全性与特征）。
- 继承：`implements cn.oyzh.fx.db.DBRoutineSchema, ObjectComparator<DamengRoutineSchema>`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `params` | `List<DamengRoutineParam>` | 参数列表 |
  | `schema` | `String` | 库名称 |
  | `securityType` | `String` | 安全性（AUTHID） |
  | `characteristic` | `String` | 特征（如 PIPELINED、PARALLEL_ENABLE 等） |
  | `nameProperty` | `SimpleStringProperty` | 程序名称属性 |
  | `definitionProperty` | `SimpleStringProperty` | 程序定义属性 |
  | `createDefinitionProperty` | `SimpleStringProperty` | 程序创建定义属性 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `SimpleStringProperty nameProperty()` / `void setName` / `String getName` | 名称属性与读写 | 懒建 `SimpleStringProperty` |
  | `SimpleStringProperty definitionProperty()` / `void setDefinition` / `String getDefinition` | 定义属性与读写 | 懒建 |
  | `SimpleStringProperty createDefinitionProperty()` / `void setCreateDefinition` / `String getCreateDefinition` | 创建定义属性与读写 | 懒建 |
  | `boolean compare(DamengRoutineSchema routine)` | 比较 | 自反或 schema+name 相等 |
  | `List<DamengRoutineParam> getParams()` / `void setParams(...)` | 参数列表读写 | — |
  | `getSchema/setSchema`、`getSecurityType/setSecurityType`、`getCharacteristic/setCharacteristic` | 属性访问器 | — |

- 调用链：`DamengFunction extends DamengRoutineSchema → getParams()/getDefinition() → SQL 生成器`

---

### DamengRoutineParam

- 职责：函数/存储过程的参数模型，负责参数定义文本生成与相关表单控件。
- 继承：`extends cn.oyzh.fx.db.DBObject`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dbClient` | `ShellDamengClient` | 数据库客户端 |
  | `name` | `String` | 名称 |
  | `typeProperty` | `StringProperty` | 类型属性（`final`） |
  | `mode` | `String` | 模式（IN/OUT/IN OUT） |
  | `size` | `Integer` | 长度 |
  | `digits` | `Integer` | 小数位 |
  | `position` | `Integer` | 位置 |
  | `value` | `String` | 值 |
  | `charsetProperty` | `StringProperty` | 字符集属性（`final`） |
  | `digitsControl` | `NumberTextField` | 小数位组件（缓存） |
  | `sizeControl` | `NumberTextField` | 长度组件（缓存） |
  | `valueControl` | `DBEnumTextFiled` | 值组件（缓存） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void setType(String)` / `String getType()` | 类型读写 | 写入附带 `putOriginalData("type")` |
  | `void setPosition/getPosition`、`setCharset/getCharset`、`setName/getName`、`setMode/getMode`、`setSize/getSize`、`setDigits/getDigits`、`setValue/getValue` | 属性访问器 | 各 setter 附带 `putOriginalData` |
  | `String getDefinition()` | 生成参数定义文本 | `列名 IN/OUT/IN OUT 类型 (长度[,小数位])`，支持 digits/value 时拼接，`()` 被清理 |
  | `void setDtdIdentifier(String dtdIdentifier)` | 解析数据类型标识 | 含 `(` 时拆出类型名与括号内参数，`supportDigits` 且含逗号时解析 size/digits，否则作为 size；类型转大写 |
  | `boolean isReturnParam()` | 是否返回参数 | 名称 `V_RET` 且 mode 为 `OUT` 且 `position == 0` |
  | `List<String> getValueList()` | 解析值列表 | 按 `,` 切分并去除单引号 |
  | `void setDbClient(ShellDamengClient)` | 注入客户端并注册类型监听 | 首次注入时监听 `typeProperty`：按 `DBColumnFieldManager.supportDigits/supportSize/supportValue` 启用/禁用并清空对应控件 |
  | `ClearableTextField getNameControl()` | 名称控件 | 同步 `setName` |
  | `DBFiledTypeComboBox getTypeControl()` | 类型控件 | `setDialect(DAMENG)`，选中变更 `setType` |
  | `NumberTextField getDigitsControl()` / `getSizeControl()` | 小数位/长度控件 | 懒建并缓存，变更写回 |
  | `DBEnumTextFiled getValueControl()` | 值控件 | 懒建并缓存，`setValues(getValueList())` |
  | `DamengParamModeComboBox getModeControl()` | 模式控件 | 选中变更 `this.mode` |
  | `StringProperty typeProperty()` / `charsetProperty()` | 属性暴露 | — |

- 调用链：`DamengFunctionCreateSqlGenerator._generate(param) → DamengRoutineParam.getDefinition()`
- 调用链：`DamengRoutineParam.setDbClient(client) → typeProperty 监听 → getDigitsControl()/getSizeControl()/getValueControl()`

---

## 二、函数（function）

### DamengFunction

- 职责：达梦函数对象，继承 `DamengRoutineSchema`，额外维护返回参数。
- 继承：`extends DamengRoutineSchema implements ObjectCopier<DamengFunction>`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `returnParam` | `DamengRoutineParam` | 返回参数 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void setParams(List<DamengRoutineParam> params)` | 设置参数（拆出返回参数） | 遍历时 `isReturnParam()` 的存入 `returnParam`，其余交给父类 |
  | `String getReturnType()` | 返回类型 | `returnParam.getType()` |
  | `void copy(DamengFunction function)` | 复制函数 | 复制 params/definition/returnParam/securityType/characteristic |
  | `DamengRoutineParam getReturnParam()` / `void setReturnParam(...)` | 返回参数读写 | — |

- 调用链：`DamengFunction.setParams(...) → DamengRoutineParam.isReturnParam() → returnParam`
- 调用链：`DamengFunctionCreateSqlGenerator._generate(param) → function.getReturnParam().getDefinition()`

---

### DamengCreateFunctionParam

- 职责：函数创建参数（模式 + 函数）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `schema` | `String` | 模式名称 |
  | `function` | `DamengFunction` | 函数 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getSchema/setSchema`、`getFunction/setFunction` | 属性访问器 | — |
  | `String getFunctionName()` / `void setFunctionName(String)` | 函数名读写 | 委托 `function.getName()/setName()` |

- 调用链：`DamengFunctionCreateSqlGenerator.generateSqlSingle(param) → param.getFunction()`

---

### DamengAlertFunctionParam

- 职责：函数修改参数（模式 + 函数）。字段与方法同 `DamengCreateFunctionParam`。
- 字段：`schema`（`String`，模式名称）、`function`（`DamengFunction`，函数）。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getSchema/setSchema`、`getFunction/setFunction` | 属性访问器 | — |
  | `String getFunctionName()` / `void setFunctionName(String)` | 函数名读写 | 委托 `function` |

- 调用链：`DamengFunctionAlertSqlGenerator.generateSqlSingle(param) → param.getFunction()`

---

### DamengSelectFunctionParam

- 职责：函数查询参数（模式、函数名、是否查询完整定义）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `full` | `boolean` | 是否查询完整定义 |
  | `schema` | `String` | 模式名称 |
  | `functionName` | `String` | 函数名称 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `isFull/setFull`、`getSchema/setSchema`、`getFunctionName/setFunctionName` | 属性访问器 | — |

- 调用链：`ShellDamengClient.selectFunction(param) → DamengSelectFunctionParam`

---

## 三、视图（view）

### DamengView

- 职责：达梦视图模型（定义、创建定义、可更新性、安全性、字段与注释）。
- 继承：`extends DBObject implements DBView, ObjectCopier<DamengView>, ObjectComparator<DamengView>`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `updatable` | `boolean` | 是否可变更 |
  | `securityType` | `String` | 安全性（BEQUEATH） |
  | `definitionProperty` | `SimpleStringProperty` | 视图定义属性 |
  | `createDefinition` | `String` | 视图创建定义 |
  | `schema` | `String` | 模式名称 |
  | `columns` | `DamengColumns` | 字段集合（`protected`） |
  | `nameProperty` | `SimpleStringProperty` | 名称属性 |
  | `commentProperty` | `SimpleStringProperty` | 注释属性 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `SimpleStringProperty definitionProperty()` / `setDefinition` / `getDefinition` | 定义属性与读写 | 懒建 |
  | `setCreateDefinition/getCreateDefinition` | 创建定义读写 | — |
  | `void copy(DamengView f)` | 复制视图 | 复制 comment/columns/updatable/definition/securityType |
  | `SimpleStringProperty nameProperty()` / `setName` / `getName` | 名称属性与读写 | 懒建 |
  | `SimpleStringProperty commentProperty()` / `setComment` / `getComment` | 注释属性与读写 | 懒建 |
  | `boolean primaryKeyChanged()` | 主键是否变更 | 委托 `columns.primaryKeyChanged()`，或存在新建的主键字段 |
  | `List<DamengColumn> primaryKeys()` | 主键字段列表 | `columns.primaryKeys()`，无字段返回空列表 |
  | `boolean hasPrimaryKey()` / `boolean hasColumns()` / `boolean hasComment()` | 非空判断 | — |
  | `DamengColumns columns()` | 取/懒建字段集合 | — |
  | `boolean compare(DamengView view)` | 比较 | 自反或 name+schema 相等 |
  | `void removeColumn(DamengColumn column)` | 移除字段 | `columns().remove(column)` |
  | `isUpdatable/setUpdatable`、`getSecurityType/setSecurityType`、`getSchema/setSchema`、`getColumns/setColumns`、`getDefinitionProperty/setDefinitionProperty/definitionPropertyProperty` | 属性访问器 | — |

- 调用链：`DamengViewCreateSqlGenerator._generate(param) → DamengView.getDefinition()/isUpdatable()/getSecurityType()`

---

### DamengCreateViewParam

- 职责：视图创建参数（模式 + 视图）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `schema` | `String` | 库名称/模式名称 |
  | `view` | `DamengView` | 视图 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getSchema/setSchema`、`getView/setView` | 属性访问器 | — |
  | `String getViewName()` / `void setViewName(String)` | 视图名读写 | 委托 `view.getName()/setName()` |

- 调用链：`DamengViewCreateSqlGenerator.generateSql(param) → param.getView()`

---

### DamengAlertViewParam

- 职责：视图修改参数（模式 + 视图）。字段与方法同 `DamengCreateViewParam`。
- 字段：`schema`（`String`）、`view`（`DamengView`）。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getSchema/setSchema`、`getView/setView` | 属性访问器 | — |
  | `String getViewName()` / `void setViewName(String)` | 视图名读写 | 委托 `view` |

- 调用链：`DamengViewAlertSqlGenerator.generateSql(param) → param.getView()`

---

### DamengSelectViewParam

- 职责：视图查询参数（模式、视图名、是否查询完整定义）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `full` | `boolean` | 是否查询完整定义 |
  | `schema` | `String` | 库名称/模式名称 |
  | `viewName` | `String` | 视图名称 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `isFull/setFull`、`getSchema/setSchema`、`getViewName/setViewName` | 属性访问器 | — |

- 调用链：`ShellDamengClient.selectView(param) → DamengSelectViewParam`

---

## 四、事件（event）

跳过的死代码：`DamengEvent`（模型，整文件被注释）、`DamengEvents`（列表，整文件被注释）。达梦事件/调度作业相关的生成器（`generator/event/*`、`generator/job/*`）同样被注释，见 `dameng-generator.md`。

---

## 覆盖统计

- 覆盖类数：10 个。
- 类名清单：
  - routine：`DamengRoutineSchema`、`DamengRoutineParam`
  - function：`DamengFunction`、`DamengCreateFunctionParam`、`DamengAlertFunctionParam`、`DamengSelectFunctionParam`
  - view：`DamengView`、`DamengCreateViewParam`、`DamengAlertViewParam`、`DamengSelectViewParam`
  - event：无（全部为死代码）
- 跳过（死代码）：`event/DamengEvent`、`event/DamengEvents`。
