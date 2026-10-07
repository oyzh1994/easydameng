# 查询条件相关类审查

范围：`cn.oyzh.easydameng.dameng.condition` 包下全部正式类（22 个）。
该包定义达梦方言下的 SQL 条件对象（等于、区间、模糊匹配、空值等），全部继承自 fx-base 的 `cn.oyzh.fx.db.condition.DBCondition`，并以单例（`INSTANCE`）形式提供，由 `DamengConditionUtil.init()` 注册到 `DBConditionManager`。

说明：标识符保留原文，术语用中文；工厂单例与无参构造器合并说明。

---

## DamengCondition

- 职责：达梦数据库条件对象的抽象基类，封装条件值到 SQL 片段的包装逻辑。
- 继承：`extends cn.oyzh.fx.db.condition.DBCondition`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengCondition()` | 无参构造 | 调用 `super()` |
  | `DamengCondition(String name, String value)` | 构造条件 | 调用 `super(name, value)` |
  | `DamengCondition(String name, String value, boolean requireCondition)` | 构造条件 | 调用 `super(name, value, requireCondition)` |
  | `String wrapCondition(Object condition)` | 包装单个条件值 | 调用 `DBUtil.wrapData(condition, DBDialect.MYSQL)` 做值转义（注意此处传入的是 `DBDialect.MYSQL`） |
  | `String wrapCondition(String columnName, Object condition)` | 包装「列 + 条件」 | 若 `isRequireCondition()` 为真，返回 `值 + " " + wrapCondition(condition)`；否则仅返回 `wrapCondition(condition)` |

- 调用链：`DamengRecordFilter.condition() → DamengCondition.wrapCondition(value) → DBUtil.wrapData(...)`
- 调用链：`DamengInListCondition.wrapCondition(Object) → DamengCondition.wrapCondition(Object)`

---

## DamengConditionUtil

- 职责：条件注册与条件相关的 UI 节点生成/取值工具类（静态方法）。
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static void init()` | 将 20 个达梦条件单例注册到条件管理器 | 循环调用 `DBConditionManager.putCondition(DBDialect.DAMENG, xxxCondition.INSTANCE)` |
  | `static String buildCondition(List<DamengRecordFilter> filters)` | 由过滤条件列表拼装 WHERE 片段 | 逐个 `filter.condition()`，用 `DBUtil.wrap(filter.column(), DBDialect.DAMENG)` 包装列名，非末项拼接 `filter.getJoinSymbol()` |
  | `static boolean isInCondition(DamengCondition condition)` | 判断是否 IN/NOT IN 条件 | 与 `DamengInListCondition.INSTANCE`、`DamengNotInListCondition.INSTANCE` 比较 |
  | `static boolean isBetweenCondition(DamengCondition condition)` | 判断是否 BETWEEN 条件 | 与 `DamengBetweenCondition.INSTANCE`、`DamengNotBetweenCondition.INSTANCE` 比较 |
  | `static List<Node> generateNode(DamengColumn column, DamengCondition condition)` | 按条件类型生成值输入节点 | IN → `ClearableTextField`；BETWEEN → 两个 `DamengNodeUtil.generateNode`；其它 → 单个节点；`isRequireCondition()` 决定是否禁用 |
  | `static void setNodeVal(List<Node> controls, Object value)` | 回填节点值 | 值为 `List` 时按下标 `DamengNodeUtil.setNodeVal`，否则逐个赋同值 |
  | `static Object getNodeVal(List<Node> controls)` | 读取节点值 | 单节点返回单值；多节点返回 `List<Object>` |

- 调用链：`DamengRecordFilter.getColumnControl()/getConditionControl() → updateValueControl() → DamengConditionUtil.generateNode(...)`
- 调用链：`DamengConditionUtil.buildCondition(filters) → 生成 WHERE 片段 → (调用方拼接 SQL)`

---

## DamengEqCondition

- 职责：等于条件（运算符 `=`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengEqCondition` | 等于条件单例（`public static final`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengEqCondition()` | 构造等于条件 | 调用 `super(I18nHelper.eq(), "=")` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengEqCondition.INSTANCE)`

---

## DamengNotEqCondition

- 职责：不等于条件（运算符 `!=`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengNotEqCondition` | 不等于条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengNotEqCondition()` | 构造不等于条件 | `super(I18nHelper.notEq(), "!=")` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengNotEqCondition.INSTANCE)`

---

## DamengGtCondition

- 职责：大于条件（运算符 `>`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengGtCondition` | 大于条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengGtCondition()` | 构造大于条件 | `super(I18nHelper.gt(), ">")` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengGtCondition.INSTANCE)`

---

## DamengGtEqCondition

- 职责：大于等于条件（运算符 `>=`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengGtEqCondition` | 大于等于条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengGtEqCondition()` | 构造大于等于条件 | `super(I18nHelper.gtEq(), ">=")` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengGtEqCondition.INSTANCE)`

---

## DamengLtCondition

- 职责：小于条件（运算符 `<`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengLtCondition` | 小于条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengLtCondition()` | 构造小于条件 | `super(I18nHelper.lt(), "<")` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengLtCondition.INSTANCE)`

---

## DamengLtEqCondition

- 职责：小于等于条件（运算符 `<=`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengLtEqCondition` | 小于等于条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengLtEqCondition()` | 构造小于等于条件 | `super(I18nHelper.ltEq(), "<=")` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengLtEqCondition.INSTANCE)`

---

## DamengEmptyCondition

- 职责：空字符串条件（`=''`，不需要条件值）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengEmptyCondition` | 空字符串条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengEmptyCondition()` | 构造空字符串条件 | `super(I18nHelper.isEmpty(), "=''", false)`，`requireCondition=false` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengEmptyCondition.INSTANCE)`

---

## DamengNotEmptyCondition

- 职责：非空字符串条件（`!=''`，不需要条件值）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengNotEmptyCondition` | 非空字符串条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengNotEmptyCondition()` | 构造非空字符串条件 | `super(I18nHelper.notIsEmpty(), "!=''", false)` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengNotEmptyCondition.INSTANCE)`

---

## DamengNullCondition

- 职责：值为 NULL 条件（`IS NULL`，不需要条件值）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengNullCondition` | 空值条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengNullCondition()` | 构造空值条件 | `super(I18nHelper.isNull(), "IS NULL", false)` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengNullCondition.INSTANCE)`

---

## DamengNotNullCondition

- 职责：值非 NULL 条件（`IS NOT NULL`，不需要条件值）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengNotNullCondition` | 非空值条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengNotNullCondition()` | 构造非空值条件 | `super(I18nHelper.notIsNull(), "IS NOT NULL", false)` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengNotNullCondition.INSTANCE)`

---

## DamengInListCondition

- 职责：在列表内条件（`IN`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengInListCondition` | 在列表条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengInListCondition()` | 构造在列表条件 | `super(I18nHelper.inList(), "IN")` |
  | `String wrapCondition(Object condition)` | 包装 IN 值 | 生成 `"IN (" + DBUtil.wrapData(condition, DAMENG) + ")"`；`condition` 为空时回退父类 |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengInListCondition.INSTANCE)`
- 调用链：`DamengConditionUtil.isInCondition(...) → 命中 DamengInListCondition.INSTANCE`

---

## DamengNotInListCondition

- 职责：不在列表内条件（`NOT IN`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengNotInListCondition` | 不在列表条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengNotInListCondition()` | 构造不在列表条件 | `super(I18nHelper.notInList(), "NOT IN")` |
  | `String wrapCondition(Object condition)` | 包装 NOT IN 值 | 生成 `"NOT IN (" + DBUtil.wrapData(condition, DAMENG) + ")"` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengNotInListCondition.INSTANCE)`

---

## DamengBetweenCondition

- 职责：介于区间条件（`BETWEEN`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengBetweenCondition` | 介于条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengBetweenCondition()` | 构造介于条件 | `super(I18nHelper.between(), "BETWEEN")` |
  | `String wrapCondition(Object condition)` | 包装区间值 | `Object[]` 或 `Collection` 时生成 `"BETWEEN " + 值0 + " AND " + 值1`（`DBUtil.wrapData(..., DAMENG)`），否则回退父类 |

- 调用链：`DamengConditionUtil.generateNode() → isBetweenCondition() 命中 → 生成两个输入节点`
- 调用链：`DamengRecordFilter.condition() → DamengBetweenCondition.wrapCondition(value)`

---

## DamengNotBetweenCondition

- 职责：不介于区间条件（`NOT BETWEEN`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengNotBetweenCondition` | 不介于条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengNotBetweenCondition()` | 构造不介于条件 | `super(I18nHelper.notBetween(), "NOT BETWEEN")` |
  | `String wrapCondition(Object condition)` | 包装区间值 | 生成 `"NOT BETWEEN " + 值0 + " AND " + 值1` |

- 调用链：`DamengConditionUtil.isBetweenCondition(...) → 命中 DamengNotBetweenCondition.INSTANCE`

---

## DamengContainsCondition

- 职责：包含条件（`LIKE '%值%'`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengContainsCondition` | 包含条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengContainsCondition()` | 构造包含条件 | `super(I18nHelper.contains(), "LIKE")` |
  | `String wrapCondition(Object condition)` | 包装模糊值 | 值为非空时调用 `super.wrapCondition("%" + condition + "%")` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengContainsCondition.INSTANCE)`

---

## DamengNotContainsCondition

- 职责：不包含条件（`NOT LIKE '%值%'`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengNotContainsCondition` | 不包含条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengNotContainsCondition()` | 构造不包含条件 | `super(I18nHelper.notContains(), "NOT LIKE")` |
  | `String wrapCondition(Object condition)` | 包装模糊值 | 调用 `super.wrapCondition("%" + condition + "%")` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengNotContainsCondition.INSTANCE)`

---

## DamengStartWithCondition

- 职责：以指定值开头条件（`LIKE '%值'`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengStartWithCondition` | 以指定值开头条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengStartWithCondition()` | 构造开头匹配条件 | `super(I18nHelper.startWith(), "LIKE")` |
  | `String wrapCondition(Object condition)` | 包装模糊值 | 调用 `super.wrapCondition("%" + condition)` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengStartWithCondition.INSTANCE)`

---

## DamengNotStartWithCondition

- 职责：不以指定值开头条件（`NOT LIKE '%值'`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengNotStartWithCondition` | 不以指定值开头条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengNotStartWithCondition()` | 构造非开头匹配条件 | `super(I18nHelper.notStartWith(), "NOT LIKE")` |
  | `String wrapCondition(Object condition)` | 包装模糊值 | 调用 `super.wrapCondition("%" + condition)` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengNotStartWithCondition.INSTANCE)`

---

## DamengEndWithCondition

- 职责：以指定值结尾条件（`LIKE '值%'`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengEndWithCondition` | 以指定值结尾条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengEndWithCondition()` | 构造结尾匹配条件 | `super(I18nHelper.endWith(), "LIKE")` |
  | `String wrapCondition(Object condition)` | 包装模糊值 | 调用 `super.wrapCondition(condition + "%")` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengEndWithCondition.INSTANCE)`

---

## DamengNotEndWithCondition

- 职责：不以指定值结尾条件（`NOT LIKE '值%'`）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `INSTANCE` | `DamengNotEndWithCondition` | 不以指定值结尾条件单例 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengNotEndWithCondition()` | 构造非结尾匹配条件 | `super(I18nHelper.notEndWith(), "NOT LIKE")` |
  | `String wrapCondition(Object condition)` | 包装模糊值 | 调用 `super.wrapCondition(condition + "%")` |

- 调用链：`DamengConditionUtil.init() → DBConditionManager.putCondition(DAMENG, DamengNotEndWithCondition.INSTANCE)`

---

## 覆盖统计

- 覆盖类数：22 个。
- 类名清单：`DamengCondition`、`DamengConditionUtil`、`DamengEqCondition`、`DamengNotEqCondition`、`DamengGtCondition`、`DamengGtEqCondition`、`DamengLtCondition`、`DamengLtEqCondition`、`DamengEmptyCondition`、`DamengNotEmptyCondition`、`DamengNullCondition`、`DamengNotNullCondition`、`DamengInListCondition`、`DamengNotInListCondition`、`DamengBetweenCondition`、`DamengNotBetweenCondition`、`DamengContainsCondition`、`DamengNotContainsCondition`、`DamengStartWithCondition`、`DamengNotStartWithCondition`、`DamengEndWithCondition`、`DamengNotEndWithCondition`。
