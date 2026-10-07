# store 包代码审查

本地持久化存储层，基于 `cn.oyzh.store.jdbc` 的 `JdbcStandardStore` / `JdbcKeyValueStore` 提供连接、分组、查询、SSH 配置、设置与终端历史的增删改查；所有 Store 均为单例（`INSTANCE`）。

---

## DamengStoreUtil
- 职责：达梦数据库本地存储初始化工具。
- 字段：无。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `init()` | 初始化本地存储 | 设置缓存/页大小、方言为 `JdbcDialect.H2`、库文件为 `STORE_PATH+"db"`，调用 `JdbcManager.takeoff()`；若异常信息含 "Database may be already in use" 则 `MessageBox.warn` 提示 |

- 调用链：`EasyDamengApp.main → DamengStoreUtil.init → JdbcManager.takeoff`

## DamengSettingStore
- 职责：达梦数据库设置储存（键值型）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengSettingStore` | 单例 |
  | `SETTING` | `DamengSetting` | 当前设置（静态，启动时加载） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `load()` | 加载设置 | `super.select()`，异常/空时返回 `new DamengSetting()` |
  | `replace(DamengSetting)` | 保存设置 | `update(model)` |
  | `modelClass()` | 模型类型 | 返回 `DamengSetting.class` |

- 调用链：`EasyDamengApp → DamengSettingStore.SETTING → DamengSetting.getLocale/fontConfig/themeConfig`

## DamengConnectStore
- 职责：达梦数据库连接存储，并同步维护关联 SSH 配置。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengConnectStore` | 单例 |
  | `sshConfigStore` | `DamengSSHConfigStore` | SSH 配置存储（`INSTANCE`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `load()` | 加载连接列表 | `super.selectList()` |
  | `replace(DamengConnect)` | 保存连接并同步 SSH | `exist(id)` 决定 `update`/`insert`；随后 sshConfig 非空则 `setIid(id)` 后 `sshConfigStore.replace`，否则 `deleteByIid(id)` |
  | `delete(DamengConnect)` | 删除连接 | `super.delete` 成功后 `sshConfigStore.deleteByIid(id)` |
  | `modelClass()` | 模型类型 | `DamengConnect.class` |

- 调用链：`连接编辑保存 → DamengConnectStore.replace → DamengSSHConfigStore.replace`

## DamengSSHConfigStore
- 职责：达梦数据库 SSH 配置存储。
- 字段：`INSTANCE`（单例）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `replace(DamengSSHConfig)` | 保存 SSH 配置 | 以 `iid` 判断 `update`/`insert` |
  | `deleteByIid(String)` | 按连接 id 删除 | `DeleteParam` + `QueryParam("iid", iid)` |
  | `getByIid(String)` | 按连接 id 查询 | `super.selectOne(QueryParam.of("iid", iid))` |
  | `modelClass()` | 模型类型 | `DamengSSHConfig.class` |

- 调用链：`DamengConnectStore.replace → DamengSSHConfigStore.replace`；`连接使用 → getByIid → 建立 SSH 隧道`

## DamengGroupStore
- 职责：达梦数据库连接分组存储。
- 字段：`INSTANCE`（单例）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `load()` | 加载分组列表 | `super.selectList()` |
  | `replace(DamengGroup)` | 保存分组 | 按 `name` 或 `gid` 存在性决定 `update`/`insert` |
  | `delete(String name)` | 按名称删除 | 构造 `DeleteParam` + `QueryParam("name", name)` |
  | `exist(String name)` | 名称是否存在 | `params{name}` 调 `super.exist` |
  | `modelClass()` | 模型类型 | `DamengGroup.class` |

- 调用链：`分组长编辑 → DamengGroupStore.replace/exist/delete`

## DamengQueryStore
- 职责：达梦数据库查询存储。
- 字段：`INSTANCE`（单例）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `list(String iid, String dbName)` | 按连接与库加载查询 | 组合两个 `QueryParam` 到 `SelectParam` 后 `selectList` |
  | `replace(DamengQuery)` | 保存查询 | 按 `uid` 存在性决定 `insert`/`update` |
  | `deleteByIid(String)` | 按连接 id 删除 | 注意：条件判断为 `StringUtil.isEmpty(iid)`（为空才执行删除，疑似逻辑反向） |
  | `modelClass()` | 模型类型 | `DamengQuery.class` |

- 调用链：`查询树展开 → DamengQueryStore.list(iid, dbName)`；`查询保存 → replace`

## ShellTerminalHistoryStore
- 职责：Shell 终端历史记录存储。
- 字段：`INSTANCE`（单例）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `replace(ShellTerminalHistory)` | 保存历史 | 直接 `insert(model)` |
  | `modelClass()` | 模型类型 | `ShellTerminalHistory.class` |

- 调用链：`终端命令执行 → ShellTerminalHistoryStore.replace`

---

## 已跳过的死代码

以下文件整文件被注释（旧版 JSON 文件存储实现），已跳过：

- `store/DBGroupStore.java`
- `store/DBInfoStore.java`
- `store/DBPageInfoStore.java`
- `store/DBQueryStore.java`
- `store/DBSearchHistoryStore.java`
- `store/DBSettingStore.java`
