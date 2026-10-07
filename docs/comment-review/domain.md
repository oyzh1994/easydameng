# domain 包代码审查

领域实体包，定义达梦客户端的持久化对象（连接、分组、查询、SSH 配置、设置、终端历史），均与 `cn.oyzh.store.jdbc` 的 `@Table/@Column` 注解配合完成存储映射。

---

## DamengConnect
- 职责：达梦数据库连接信息实体，映射表 `t_connect`。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `id` | `String` | 数据 id（`@PrimaryKey`） |
  | `host` | `String` | 连接地址 |
  | `name` | `String` | 名称 |
  | `user` | `String` | 认证用户 |
  | `type` | `String` | 类型 |
  | `password` | `String` | 认证密码 |
  | `remark` | `String` | 备注信息 |
  | `readonly` | `Boolean` | 只读模式 |
  | `groupId` | `String` | 分组 id |
  | `collects` | `List<String>` | 收藏列表 |
  | `connectTimeOut` | `Integer` | 连接超时时间（秒） |
  | `sshForward` | `Boolean` | 是否开启 ssh 转发 |
  | `sshConfig` | `DamengSSHConfig` | ssh 信息 |
  | `sid` | `String` | 达梦服务标识 |
  | `serviceName` | `String` | 服务名 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `copy(DamengConnect)` | 复制连接信息到当前对象 | 逐字段赋值，不含 `id`/`sid`/`serviceName` |
  | `isSSHForward()` | 是否 ssh 转发 | `BooleanUtil.isTrue(sshForward)` |
  | `isReadonly()` | 是否只读模式 | `BooleanUtil.isTrue(readonly)` |
  | `isCollect(String path)` | 是否已收藏 | `collects.contains(path)` |
  | `addCollect(String path)` | 添加收藏 | 懒初始化 list 并去重添加 |
  | `removeCollect(String path)` | 取消收藏 | `collects.remove(path)` |
  | `getConnectTimeOut()` | 获取超时（秒） | 空或 `<1` 时默认 5 |
  | `connectTimeOutMs()` | 超时毫秒值 | `getConnectTimeOut() * 1000` |
  | `hostIp()` | 取连接 ip | 按 `:` 切分 host 取第 0 段 |
  | `hostPort()` | 取连接端口 | host 含 `:` 且不含 `,` 时取第 1 段转 int，异常返回 -1 |
  | `serviceName()` | 服务名，优先 sid | `sid == null ? serviceName : sid` |
  | `checkServiceType()` | 服务类型 | `sid == null ? "sid" : "serviceName"` |
  | `compareTo` / `compare` | 排序与相等 | 按 `name`（忽略大小写）/ `name` 相等 |

- 调用链：`DBConnManager → DamengConnect(copy) → 建立连接`；`连接树节点 → DamengConnect.isSSHForward → SSH 转发`

## DamengGroup
- 职责：达梦数据库连接分组，映射表 `t_group`，继承 `AppGroup`。
- 字段：无（继承父类）。
- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengGroup()` | 默认构造 | 委托 `AppGroup()` |
  | `DamengGroup(name, groupId, expand)` | 构造分组 | 委托 `AppGroup(name, groupId, expand)` |
  | `compare(DamengGroup)` | 相等比较 | 按 `name` 比较 |

- 调用链：`DamengGroupStore → DamengGroup`；`分组树节点 → DamengGroup`

## DamengQuery
- 职责：达梦数据库查询对象（保存 SQL 及归属连接），映射表 `t_query`。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `uid` | `String` | 数据 id（`@PrimaryKey`） |
  | `iid` | `String` | 连接 id |
  | `dbName` | `String` | 数据库名称 |
  | `name` | `String` | 名称 |
  | `content` | `String` | 内容（SQL 文本） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `copy(DamengQuery)` | 复制查询信息 | 复制 `iid`/`name`/`dbName`/`content` |
  | `compareTo` / `compare` | 排序与相等 | 按 `uid` 比较 |
  | `isNew()` | 是否新增记录 | `getUid() == null` |
  | 其余 | 字段 getter/setter | — |

- 调用链：`DamengQueryStore → DamengQuery`；`查询树 → DamengQuery.content → 查询编辑器`

## DamengSSHConfig
- 职责：达梦数据库 SSH 连接配置，映射表 `t_ssh_config`，继承 `SSHConnect`。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `iid` | `String` | 连接 id（`@PrimaryKey`），关联 `DamengConnect` |

- 方法：`getIid()` / `setIid(String)`。
- 调用链：`DamengSSHConfigStore → DamengSSHConfig`；`DamengConnect.sshConfig → 建立 SSH 隧道`

## DamengSetting
- 职责：达梦数据库客户端设置，映射表 `t_setting`，继承 `AppSetting`。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `recordPageLimit` | `Integer` | 记录每页限制 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `setRecordPageLimit(Integer)` | 设置每页限制 | 非法（null 或 `<=0`）时重置为 100 |
  | `getRecordPageLimit()` | 获取每页限制 | 非法时返回默认 100 |

- 调用链：`DamengSettingStore.SETTING → DamengSetting.getRecordPageLimit → 记录分页`

## ShellTerminalHistory
- 职责：Shell 终端历史记录，映射表 `t_terminal_history`，继承 `TerminalHistory`。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `tid` | `String` | 数据 id（`@PrimaryKey`） |

- 方法：`getTid()` / `setTid(String)`。
- 调用链：`ShellTerminalHistoryStore → ShellTerminalHistory → 终端历史面板`

---

## 已跳过的死代码

- `domain/DamengPageInfo.java`：整文件被注释。
- `domain/DamengSearchHistory.java`：整文件被注释。
