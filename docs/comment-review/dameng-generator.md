# SQL 生成器相关类审查

范围：`cn.oyzh.easydameng.dameng.generator` 下的正式类（8 个）。
这些类继承 fx-base 的 `cn.oyzh.fx.db.DBSqlGenerator`，负责把库对象参数转换为达梦 DDL/DML 文本，覆盖建表/改表、建视图/改视图、建函数/改函数、建存储过程/改存储过程。

跳过（整文件被注释，死代码）：`generator/routine/DamengFunctionSqlGenerator`、`generator/routine/DamengProcedureSqlGenerator`、`generator/job/DamengJobCreateSqlGenerator`、`generator/event/*`（`DamengEventCreateSqlGenerator`、`DamengEventAlertSqlGenerator`、`EventCreateSqlGenerator`、`EventAlertSqlGenerator`）。

---

## DamengTableCreateSqlGenerator

- 职责：达梦建表 SQL 生成器，将 `DamengCreateTableParam` 生成 CREATE TABLE 及配套语句。
- 继承：`extends cn.oyzh.fx.db.DBSqlGenerator`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `changeFlag` | `boolean` | 变更标志位（用于判断是否需要删除末尾多余逗号） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void _generate(DamengCreateTableParam param)` | 生成核心逻辑 | 拼 `CREATE TABLE 库.表 ( ... )`；依次调用 `columnHandle/primaryKeyHandle/indexHandle/foreignKeyHandle/checkHandle`；`changeFlag` 时 `StringUtil.deleteLast(sqlBuilder, ",")`；有表空间追加 `STORAGE (ON xxx)`；`hasTrigger()` 时 `triggerHandle`；有注释追加 `COMMENT ON TABLE ...` |
  | `void triggerHandle(DamengCreateTableParam)` | 触发器 DDL | 逐触发器生成 `CREATE OR REPLACE TRIGGER ... <policy> ON 表 FOR EACH ROW BEGIN <body> END;`（body 缺分号自动补） |
  | `void columnHandle(StringBuilder, DamengCreateTableParam)` | 字段定义 | 逐字段拼「列名 + 类型 + (长度[,小数位])」；自增列 `IDENTITY(1,1)`；非自增且有默认值追加 `DEFAULT`；可空 `NULL`/`NOT NULL`；列注释单独生成 `COMMENT ON COLUMN "库"."表"."列" IS '注释';` |
  | `void primaryKeyHandle(StringBuilder, DamengCreateTableParam)` | 主键定义 | `PRIMARY KEY (列...)` |
  | `void indexHandle(StringBuilder, DamengCreateTableParam)` | 索引定义 | 唯一索引加 `UNIQUE`，`INDEX 名 (列...)`，方法名非空追加 `USING BTREE/HASH` |
  | `void foreignKeyHandle(StringBuilder, DamengCreateTableParam)` | 外键定义 | `CONSTRAINT 名 FOREIGN KEY (列...) REFERENCES 库.表 (列...) ON DELETE xxx ON UPDATE xxx` |
  | `void checkHandle(StringBuilder, DamengCreateTableParam)` | 检查约束 | `CONSTRAINT 名 CHECK (clause)` |
  | `List<String> generate(DamengCreateTableParam)` | 生成多条 | `_generate` 后 `buildSql()` |
  | `String generateSingle(DamengCreateTableParam)` | 生成单条 | `_generate` 后 `buildSqlSingle()` |
  | `static List<String> generateSql(DamengCreateTableParam)` / `static String generateSqlSingle(DamengCreateTableParam)` | 静态入口 | `new DamengTableCreateSqlGenerator().generate(...)` |

- 调用链：`DamengTableCreateSqlGenerator.generateSql(param) → _generate(param) → columnHandle/primaryKeyHandle/... → buildSql()`

---

## DamengTableAlertSqlGenerator

- 职责：达梦改表 SQL 生成器，将 `DamengAlertTableParam` 的差异生成 ALTER TABLE / DROP / CREATE 等语句。
- 继承：`extends cn.oyzh.fx.db.DBSqlGenerator`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void _generate(DamengAlertTableParam param)` | 生成核心逻辑 | 主键变更 → `primaryKeyHandle`；字段变更 → `columnHandle`；索引/外键/检查/触发器分别处理；有表空间 → `ALTER TABLE ... MOVE TABLESPACE ...`；有注释 → `COMMENT ON TABLE ...` |
  | `void triggerHandle(DamengAlertTableParam)` | 触发器变更 | 被删除或变更的触发器先 `DROP TRIGGER <原名>;`；新增或变更的再 `CREATE OR REPLACE TRIGGER ...`（先删后建避免异常） |
  | `void columnHandle(DamengAlertTableParam)` | 字段变更 | ① 重命名：`ALTER TABLE ... RENAME COLUMN 原名 TO 新名, ...;`；② 删除：`ALTER TABLE ... DROP COLUMN 列, ...;`；③ 新增：`ALTER TABLE ... ADD COLUMN ( 列 类型..., ...);`；④ 修改：必要时 `DROP IDENTITY;` 后 `ALTER TABLE ... MODIFY 列 类型...;`，自增列再 `ADD COLUMN 列 IDENTITY(1, 1);`；⑤ 注释变更生成 `COMMENT ON COLUMN` |
  | `void primaryKeyHandle(DamengAlertTableParam)` | 主键变更 | 存在主键时逐项 `ALTER TABLE ... DROP CONSTRAINT 主键 CASCADE;`；再 `ALTER TABLE ... ADD PRIMARY KEY (列[ (键长) ]...);` |
  | `void indexHandle(DamengAlertTableParam)` | 索引变更 | 删除/变更：`DROP INDEX 原名;`；新增/变更：`CREATE [UNIQUE] INDEX 名 ON 库.表 (列...);`（过滤 FULLTEXT/SPATIAL） |
  | `void foreignKeyHandle(DamengAlertTableParam)` | 外键变更 | 删除/变更：`ALTER TABLE ... DROP CONSTRAINT 原名;`（名为空跳过）；新增/变更：`ALTER TABLE ... ADD CONSTRAINT 名 FOREIGN KEY (列...) REFERENCES 库.表 (列...) ON DELETE xxx ON UPDATE xxx;` |
  | `void checkHandle(DamengAlertTableParam)` | 检查约束变更 | 删除/变更：`ALTER TABLE ... DROP CONSTRAINT 原名;`；新增/变更：`ALTER TABLE ... ADD CONSTRAINT 名 CHECK (clause);` |
  | `void appendColumnType(StringBuilder, DamengColumn, boolean created)` | 追加列类型定义 | 类型 + 长度/小数位 + 默认值 + NULL/NOT NULL；自增且 `created` 时追加 `IDENTITY(1,1)` |
  | `List<String> generate(DamengAlertTableParam)` / `String generateSingle(DamengAlertTableParam)` | 生成多条/单条 | `_generate` 后 `buildSql()` / `buildSqlSingle()` |
  | `static List<String> generateSql(DamengAlertTableParam)` / `static String generateSqlSingle(DamengAlertTableParam)` | 静态入口 | `new DamengTableAlertSqlGenerator()...` |

- 调用链：`DamengTableAlertSqlGenerator.generateSql(param) → _generate(param) → columnHandle/primaryKeyHandle/indexHandle/foreignKeyHandle/checkHandle/triggerHandle`

---

## DamengViewCreateSqlGenerator

- 职责：达梦视图创建 SQL 生成器。
- 继承：`extends cn.oyzh.fx.db.DBSqlGenerator`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void _generate(DamengCreateViewParam param)` | 生成 CREATE VIEW | `CREATE VIEW 库.视图 AS <definition>`；`securityType` 非空追加 `BEQUEATH xxx`；不可更新追加 `WITH READ ONLY`；有注释追加 `COMMENT ON VIEW ...` |
  | `List<String> generate(DamengCreateViewParam)` / `String generateSingle(DamengCreateViewParam)` | 生成多条/单条 | `_generate` 后 `buildSql()` / `buildSqlSingle()` |
  | `static List<String> generateSql(...)` / `static String generateSqlSingle(...)` | 静态入口 | `new DamengViewCreateSqlGenerator()...` |

- 调用链：`DamengViewCreateSqlGenerator.generateSql(param) → _generate(param) → DamengView.getDefinition()/isUpdatable()/getSecurityType()`

---

## DamengViewAlertSqlGenerator

- 职责：达梦视图修改 SQL 生成器。
- 继承：`extends cn.oyzh.fx.db.DBSqlGenerator`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void _generate(DamengAlertViewParam param)` | 生成 CREATE OR REPLACE VIEW | 与创建生成器逻辑一致，但前缀为 `CREATE OR REPLACE VIEW` |
  | `List<String> generate(DamengAlertViewParam)` / `String generateSingle(DamengAlertViewParam)` | 生成多条/单条 | `_generate` 后 `buildSql()` / `buildSqlSingle()` |
  | `static List<String> generateSql(...)` / `static String generateSqlSingle(...)` | 静态入口 | `new DamengViewAlertSqlGenerator()...` |

- 调用链：`DamengViewAlertSqlGenerator.generateSql(param) → _generate(param) → DamengView.getDefinition()`

---

## DamengFunctionCreateSqlGenerator

- 职责：达梦函数创建 SQL 生成器。
- 继承：`extends cn.oyzh.fx.db.DBSqlGenerator`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void _generate(DamengCreateFunctionParam param)` | 生成 CREATE FUNCTION | `CREATE FUNCTION 库.函数 (参数定义...)`；有返回参数追加 `RETURN <定义>`；按 `characteristic` 追加 `PIPELINED/PARALLEL_ENABLE/DETERMINISTIC`、`RESULT_CACHE/AGGREGATE`；`securityType` 非空追加 `AUTHID xxx`；`AS <函数体>` |
  | `String generateSingle(DamengCreateFunctionParam)` | 生成单条 | `_generate` 后 `buildSqlSingle()` |
  | `static String generateSqlSingle(DamengCreateFunctionParam)` | 静态入口 | `new DamengFunctionCreateSqlGenerator()...` |

- 调用链：`DamengFunctionCreateSqlGenerator.generateSqlSingle(param) → _generate(param) → DamengRoutineParam.getDefinition()`

---

## DamengFunctionAlertSqlGenerator

- 职责：达梦函数修改 SQL 生成器。
- 继承：`extends cn.oyzh.fx.db.DBSqlGenerator`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void _generate(DamengAlertFunctionParam param)` | 生成 CREATE OR REPLACE FUNCTION | 与创建生成器逻辑一致，前缀为 `CREATE OR REPLACE FUNCTION` |
  | `String generateSingle(DamengAlertFunctionParam)` | 生成单条 | `_generate` 后 `buildSqlSingle()` |
  | `static String generateSqlSingle(DamengAlertFunctionParam)` | 静态入口 | `new DamengFunctionAlertSqlGenerator()...` |

- 调用链：`DamengFunctionAlertSqlGenerator.generateSqlSingle(param) → _generate(param) → DamengFunction.getParams()/getReturnParam()`

---

## DamengProcedureCreateSqlGenerator

- 职责：达梦存储过程创建 SQL 生成器。
- 继承：`extends cn.oyzh.fx.db.DBSqlGenerator`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void _generate(DamengCreateProcedureParam param)` | 生成 CREATE PROCEDURE | `CREATE PROCEDURE 过程名 (参数定义...)`；按 `characteristic` 追加 `PARALLEL_ENABLE`；`securityType` 非空追加 `AUTHID xxx`；`AS <过程体>` |
  | `String generateSingle(DamengCreateProcedureParam)` | 生成单条 | `_generate` 后 `buildSqlSingle()` |
  | `static String generateSqlSingle(DamengCreateProcedureParam)` | 静态入口 | `new DamengProcedureCreateSqlGenerator()...` |

- 调用链：`DamengProcedureCreateSqlGenerator.generateSqlSingle(param) → _generate(param) → DamengProcedure.getParams()/getDefinition()`

---

## DamengProcedureAlertSqlGenerator

- 职责：达梦存储过程修改 SQL 生成器。
- 继承：`extends cn.oyzh.fx.db.DBSqlGenerator`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void _generate(DamengAlertProcedureParam param)` | 生成 CREATE OR REPLACE PROCEDURE | 与创建生成器逻辑一致，前缀为 `CREATE OR REPLACE PROCEDURE` |
  | `List<String> generate(DamengAlertProcedureParam)` / `String generateSingle(DamengAlertProcedureParam)` | 生成多条/单条 | `_generate` 后 `buildSql()` / `buildSqlSingle()` |
  | `static List<String> generateSql(...)` / `static String generateSqlSingle(...)` | 静态入口 | `new DamengProcedureAlertSqlGenerator()...` |

- 调用链：`DamengProcedureAlertSqlGenerator.generateSql(param) → _generate(param) → DamengProcedure.getParams()`

---

## 覆盖统计

- 覆盖类数：8 个。
- 类名清单：`DamengTableCreateSqlGenerator`、`DamengTableAlertSqlGenerator`、`DamengViewCreateSqlGenerator`、`DamengViewAlertSqlGenerator`、`DamengFunctionCreateSqlGenerator`、`DamengFunctionAlertSqlGenerator`、`DamengProcedureCreateSqlGenerator`、`DamengProcedureAlertSqlGenerator`。
- 跳过（死代码）：`DamengFunctionSqlGenerator`、`DamengProcedureSqlGenerator`、`DamengJobCreateSqlGenerator`、`DamengEventCreateSqlGenerator`、`DamengEventAlertSqlGenerator`、`EventCreateSqlGenerator`、`EventAlertSqlGenerator`。

说明：存储过程生成器依赖 `cn.oyzh.easydameng.procedure` 包（`DamengProcedure`、`DamengCreateProcedureParam`、`DamengAlertProcedureParam`），该包不在本任务范围内。
