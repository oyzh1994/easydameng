# db 包代码审查

`cn.oyzh.easydameng.db` 包内的 6 个文件**整文件均被注释掉**（每行以 `//` 开头，包声明为 `//package cn.oyzh.fx.db;`，为早期从 `cn.oyzh.fx.db` 迁移后遗留），属于死代码，按审查范围要求跳过，无可用条目。

涉及文件（均为死代码）：

- `db/DBColumn.java`（接口，注释）
- `db/DBColumnField.java`（注释）
- `db/DBColumnFieldManager.java`（注释）
- `db/DBConnConfig.java`（连接配置实体，注释）
- `db/DBConnManager.java`（注释）
- `db/DBRecordProperty.java`（注释）

> 结论：该包当前无活跃代码，未生成逐类条目。相关能力已由 `cn.oyzh.fx.db`（fx-base 项目）与 `cn.oyzh.easydameng.dameng.*`/`data` 包提供。
