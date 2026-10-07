# misc 包代码审查（procedure / dto / exception 等小包）

汇总 `procedure`、`dto`、`exception` 三个小型包。`listener`、`sql` 两个包整文件均为注释掉的死代码，已跳过（见文末说明）。

---

## procedure 包

### DamengProcedure
- 职责：达梦存储过程对象，继承自 `DamengRoutineSchema`，支持在对象间复制存储过程信息。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `copy(DamengProcedure procedure)` | 从另一存储过程复制内容 | 复制 `params`、`definition`、`securityType`、`characteristic`；`definer`、`comment` 复制逻辑被注释 |

- 调用链：`DamengProcedure.copy → DamengRoutineSchema.getParams/getDefinition`

### DamengCreateProcedureParam
- 职责：达梦存储过程创建参数。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `schema` | `String` | 模式名 |
  | `procedure` | `DamengProcedure` | 存储过程对象 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getProcedureName()` | 获取存储过程名 | 委托 `procedure.getName()` |
  | `setProcedureName(String)` | 设置存储过程名 | 委托 `procedure.setName(...)` |
  | 其余 | 字段 getter/setter | — |

- 调用链：`DamengCreateProcedureParam → DamengProcedure.getName/setName`

### DamengAlertProcedureParam
- 职责：达梦存储过程修改参数（结构与创建参数一致）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `schema` | `String` | 模式名 |
  | `procedure` | `DamengProcedure` | 存储过程对象 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getProcedureName()` / `setProcedureName(String)` | 代理到 `procedure` 的名称 | `procedure.getName()/setName(...)` |
  | 其余 | 字段 getter/setter | — |

- 调用链：`DamengAlertProcedureParam → DamengProcedure.getName/setName`

### DamengSelectProcedureParam
- 职责：达梦存储过程查询参数。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `full` | `boolean` | 是否查询全部 |
  | `schema` | `String` | 模式名 |
  | `procedureName` | `String` | 存储过程名 |

- 方法：均为字段 getter/setter（`isFull` / `setFull` / `getSchema` / `setSchema` / `getProcedureName` / `setProcedureName`）。
- 调用链：`DamengSelectProcedureParam → 查询接口`

---

## dto 包

### DamengConnect (dto)
- 职责：达梦数据库连接对象，保存连接所需的地址、端口与账号信息。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `host` | `String` | 地址，默认 `127.0.0.1` |
  | `port` | `int` | 端口，默认 `5236` |
  | `user` | `String` | 用户 |
  | `password` | `String` | 密码 |
  | `db` | `int` | db 索引，默认 `0` |

- 方法：均为字段 getter/setter。
- 调用链：`DamengConnect → 连接建立工具`

### DamengInfoExport
- 职责：达梦数据库连接信息导出对象，用于连接配置的导出与导入。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `version` | `String` | 导出程序版本号 |
  | `platform` | `String` | 平台（`os.name`） |
  | `connects` | `List<DamengConnect>` | 导出连接数据 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `fromConnects(List<DamengConnect>)` | 由连接列表生成导出对象 | 读取 `Project.load()` 版本号、`os.name` 作为平台 |
  | `fromJSON(String)` | 由 json 生成导出对象 | `JSONUtil.parseObject` → `JSONUtil.toList(object, "connects", DamengConnect.class)` |
  | `toJSONString()` | 序列化为 json | `JSONUtil.toJson(this)` |
  | 其余 | 字段 getter/setter | — |

- 调用链：`DamengInfoExport.fromJSON → JSONUtil.toList → DamengConnect`；`连接导出 → DamengInfoExport.fromConnects → toJSONString`

---

## exception 包

### DBException
- 职责：达梦数据库异常，统一表示数据库操作过程中出现的异常。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DBException()` / `DBException(String)` / `DBException(Exception)` | 三种构造 | 分别委托 `RuntimeException` 的无参/消息/原因构造 |

- 调用链：`DBException ← 业务代码抛出 ↗ DBExceptionParser`

### DBExceptionParser
- 职责：达梦数据库异常信息解析器，将各类异常转换为可展示的提示信息（实现 `Function<Throwable,String>`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DBExceptionParser` | 单例实例（`public static final`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `apply(Throwable e)` | 解析异常为提示文案 | `DBException` 直接返回 message；`RuntimeException` 解包 `getCause()`；`SSHException` 判断 "Auth fail" 返回 ssh 认证失败文案；`UnsupportedOperationException` / `IllegalArgumentException` 返回 message；其余打印堆栈后返回 message |

- 调用链：`MessageBox异常展示 → DBExceptionParser.apply → 用户提示`

### ReadonlyOperationException
- 职责：只读操作异常，表示只读模式下执行了不被允许的写操作。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `ReadonlyOperationException()` | 默认提示 | 委托 `this("只读模式不支持此操作")` |
  | `ReadonlyOperationException(String msg)` | 指定提示 | 委托父类 `DBException(String)` |

- 调用链：`只读校验 → ReadonlyOperationException → DBExceptionParser.apply`

### ShellException
- 职责：Shell 命令执行异常，表示远程命令执行过程中出现的异常。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `ShellException()` / `ShellException(String)` / `ShellException(Throwable)` | 三种构造 | 委托 `RuntimeException` 对应构造 |

- 调用链：`Shell 远程执行 → ShellException ↗ DBExceptionParser`

---

## 已跳过的死代码

以下文件整文件被注释，属死代码，未纳入审查：

- `listener/DBStatusListener.java`：整文件被注释。
- `listener/DBStatusListenerManager.java`：整文件被注释。
- `sql/DBSqlParser.java`：整文件被注释。
- `sql/DruidSqlParser.java`：整文件被注释。

此外，`db` 包（`DBColumn`、`DBColumnField`、`DBColumnFieldManager`、`DBConnConfig`、`DBConnManager`、`DBRecordProperty`）与 `query` 包（`DamengQueryEditor`、`DamengQueryPromptItem`、`DamengQueryPromptListView`、`DamengQueryPromptPopup`、`DamengQueryToken`、`DamengQueryTokenAnalyzer`、`DamengQueryUtil`）**全部文件均为整文件注释的死代码**，故未单独生成本包文档。
