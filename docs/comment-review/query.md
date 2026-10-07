# query 包代码审查

`cn.oyzh.easydameng.query` 包内的 7 个文件**整文件均被注释掉**（每行以 `//` 开头，包声明为 `//package cn.oyzh.easydameng.query;`），属于死代码，按审查范围要求跳过，无可用条目。

涉及文件（均为死代码）：

- `query/DamengQueryEditor.java`（SQL 编辑器，注释）
- `query/DamengQueryPromptItem.java`（注释）
- `query/DamengQueryPromptListView.java`（注释）
- `query/DamengQueryPromptPopup.java`（提示弹窗，注释）
- `query/DamengQueryToken.java`（注释）
- `query/DamengQueryTokenAnalyzer.java`（词法分析，注释）
- `query/DamengQueryUtil.java`（查询工具，注释）

> 结论：该包当前无活跃代码，未生成逐类条目。SQL 编辑与词法/提示能力已由 `cn.oyzh.easydameng.dameng.query.ShellDamengQueryEditor` 及 `tabs/query` 下的查询页签承接。
