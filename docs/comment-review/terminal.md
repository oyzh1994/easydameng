# terminal 包代码审查

达梦交互式终端（类似 mysql client 的命令行）实现，基于 `cn.oyzh.fx.terminal` 框架，提供命令执行、SQL 关键字补全、历史命令、帮助与按键/鼠标处理，以及 `show databases`、`show tables`、`use` 等基础命令。

---

## DamengTerminalPane
- 职责：达梦终端文本域，承载终端输入输出与命令执行。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `client` | `ShellDamengClient` | 达梦客户端 |
  | `dbName` | `String` | 当前数据库名称 |
  | `TERMINAL_NAME` | `String`（静态常量） | 终端名 `"dameng"` |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getEditorFont()` | 编辑器字体 | 由 `DamengSettingStore.SETTING.terminalFontConfig()` 生成 |
  | `getClient()` / `shellConnect()` | 取客户端/所属连接 | `client.getDbConnect()` |
  | `flushPrompt()` | 刷新提示符 | 依据临时/常驻、连接状态拼接 `dameng@host(connected)>` 提示符 |
  | `terminalName()` | 终端名称 | 返回 `TERMINAL_NAME` |
  | `init(client, dbName)` | 初始化终端 | 保存 client/dbName，`FXUtil.runLater` 输出欢迎语、刷新提示符，并按临时/常驻分支初始化 |
  | `isTemporary()` | 是否临时连接 | `client==null || client.iid()==null` |
  | `isConnected()/isConnecting()/isClosed()` | 连接状态 | 委托 client 状态 |
  | `initByTemporary()` / `initByPermanent()` | 临时/常驻初始化 | 输出提示、追加提示符、启用输入、光标移尾 |
  | `enableInput()` | 启用输入 | 连接中禁用；已连接或临时连接时启用 |
  | `eval(String input)` | 执行命令 | 无 dbName 提示先选库；`client.executeSql(dbName,input)` 后按成功/行数/结果集拼装输出（含耗时毫秒、`formatResultSet`） |
  | `formatResultSet(DamengExecuteResult)` | 格式化结果集 | 按列宽对齐输出表头、分隔线、数据行，尾部附 `#n row(s) in set` |
  | `findHandler(String input)` | 查找命令处理器 | 先从 `TerminalManager` 查，无则用匿名处理器调用 `eval` |
  | `initNode()` | 初始化节点处理器 | 设置 key/help/mouse/history/complete 处理器 |
  | `fontSizeIncr/fontSizeDecr` | 字体缩放 | 变更后 `saveFontSize()` 持久化到设置 |
  | `outputPrompt()` | 输出提示符 | 非连接中时调用父类 |

- 调用链：`终端输入 → DamengTerminalPane.findHandler → eval → ShellDamengClient.executeSql`；`eval → formatResultSet → 终端输出`

## DamengTerminalCommandHandler
- 职责：达梦终端命令处理器基类（抽象），绑定 `DamengTerminalPane`。
- 字段：无。
- 方法：继承自 `BaseTerminalCommandHandler<C, DamengTerminalPane>`，无新增。
- 调用链：`DamengShowTablesTerminalCommandHandler → DamengTerminalCommandHandler`

## DamengTerminalManager
- 职责：达梦终端管理器，负责注册终端命令处理器。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `registerHandlers()` | 注册处理器 | 向 `TerminalManager` 注册 help、clear 标准命令及 show databases/dbs/tables、use 基础命令 |

- 调用链：`EasyDamengApp.start → TerminalManager.setLoadHandler → DamengTerminalManager.registerHandlers`

## DamengTerminalCompleteHandler
- 职责：终端补全器，基于 SQL 关键字提供自动补全。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `SQL_KEYWORDS` | `String[]` | 内置 SQL 关键字/函数/类型集合 |
  | `INSTANCE` | `DamengTerminalCompleteHandler` | 单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `newCommandHandler(String name)` | 构造补全候选处理器 | 匿名 `DamengTerminalCommandHandler`，execute 调 `terminal.eval` |
  | `findCommandHandlers(terminal, line)` | 查找候选 | 空行给全部关键字；否则先取父类结果，为空则按 `startsWith(大写行)` 匹配关键字 |
  | `completion(line, terminal)` | 执行补全 | 无/单/多匹配分别走 `noMatch`/`oneMatch`/`multiMatch` |

- 调用链：`终端 Tab → completion → findCommandHandlers → 候选处理器`

## DamengTerminalHistoryHandler
- 职责：终端历史命令处理器，读写终端历史记录。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengTerminalHistoryHandler` | 单例 |
  | `cecheList` | `List<ShellTerminalHistory>` | 内存缓存（初始容量 24） |
  | `historyStore` | `ShellTerminalHistoryStore` | 持久化存储（`INSTANCE`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `clearHistory()` | 清空历史 | 清空 store 与缓存 |
  | `listHistory()` | 列出历史 | 缓存为空时从 `historyStore.selectList()` 加载 |
  | `addHistory(TerminalHistory)` | 新增历史 | 组装 `ShellTerminalHistory`（保存时间、行），写入 store 与缓存 |

- 调用链：`终端命令执行 → addHistory → ShellTerminalHistoryStore.insert`

## DamengTerminalHelpHandler
- 职责：终端帮助命令处理器。
- 字段：`INSTANCE`（单例）。
- 方法：继承自 `BaseTerminalHelpHandler<DamengTerminalPane>`，无新增。
- 调用链：`终端 help → DamengTerminalHelpHandler`

## DamengTerminalKeyHandler
- 职责：终端按键处理器（实现 `TerminalKeyHandler<DamengTerminalPane>`）。
- 字段：`INSTANCE`（单例）。
- 方法：接口默认实现。
- 调用链：`终端按键 → DamengTerminalKeyHandler`

## DamengTerminalMouseHandler
- 职责：终端鼠标处理器（实现 `TerminalMouseHandler<DamengTerminalPane>`）。
- 字段：`INSTANCE`（单例）。
- 方法：接口默认实现。
- 调用链：`终端鼠标事件 → DamengTerminalMouseHandler`

## DamengTerminalUtil
- 职责：终端工具类（当前为空壳）。
- 字段：无。
- 方法：无。
- 调用链：—

---

## terminal.basic 子包

### DamengShowDatabasesTerminalCommandHandler
- 职责：`show databases;` 命令处理器，列出模式（数据库）名。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `parseCommand(line,args)` | 解析命令 | 组装 `TerminalCommand` 的 args/command |
  | `checkArgs(args)` | 参数校验 | `args.length == 2` |
  | `commandName()` / `commandSubName()` | 命令名 | `"show"` / `"databases;"` |
  | `execute(command,terminal)` | 执行 | 取 `client.schemas()`，以 `DamengSchema::getName` 换行连接输出 |

- 调用链：`show databases; → execute → client.schemas → 输出模式名`

### DamengShowDbsTerminalCommandHandler
- 职责：`show dbs;` 命令处理器（`show databases;` 的简写）。
- 字段：无。
- 方法：重写 `commandSubName()` 返回 `"dbs;"`，其余继承父类。
- 调用链：`show dbs; → 父类 execute → client.schemas`

### DamengShowTablesTerminalCommandHandler
- 职责：`show tables;` 命令处理器，列出当前库表名。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `commandSubName()` | 命令名 | `"tables;"` |
  | `execute(command,terminal)` | 执行 | `client.selectTablesSimple(terminal.getDbName())` 后换行连接表名 |

- 调用链：`show tables; → execute → client.selectTablesSimple → 输出表名`

### DamengUseTerminalCommandHandler
- 职责：`use <db>` 命令处理器，切换当前数据库。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `commandName()` | 命令名 | `"use"` |
  | `execute(command,terminal)` | 执行 | 设置 `terminal.setDbName(args[1])`；查询 `client.schemas()` 判断是否存在（注意：判断分支返回文案为“Database invalid / Database changed”，与 contains 结果相反，疑似逻辑反向） |

- 调用链：`use db → execute → terminal.setDbName + client.schemas`
