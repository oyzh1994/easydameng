# 数据导入导出与传输对象审查

范围：`cn.oyzh.easydameng.dameng` 下的 `data`、`dto` 两个子包中的正式类。
`data` 包实现达梦数据的多格式导入/导出（CSV/TXT/JSON/XML/Excel/SQL/HTML）及导出/导入配置；`dto` 包为导出/导入过程中使用的承载对象。

跳过（整文件被注释，死代码）：`DamengDataExportHelper`；`dto` 下 `ShellDamengDataTransportView`、`ShellDamengDataTransportProcedure`、`ShellDamengDataTransportTable`、`ShellDamengDataTransportTrigger`、`ShellDamengDataTransportFunction`。

---

## 一、数据层（data）

### DamengTypeFileReader

- 职责：类型文件读取器抽象基类（`Closeable`），定义按对象逐条读取的通用协议。
- 继承：`implements Closeable`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void init()` | 初始化读取器（钩子） | 默认空实现 |
  | `Map<String, Object> readObject()` | 读取单条对象数据（抽象） | 由子类实现 |
  | `List<Map<String, Object>> readObjects(int count)` | 读取指定数量 | 循环 `readObject()` 直到达到数量或返回 null |
  | `List<String> parseLine(String line, char txtIdentifier, char fieldSeparator)` | 解析文本行 | 用 `StringReader` 逐字符，识别文本标识符包裹的字段，按分隔符切分字段 |

- 调用链：`DamengCsvTypeFileReader/DamengTxtTypeFileReader.readObject() → parseLine(...)`

---

### DamengTypeFileWriter

- 职责：类型文件写出器抽象基类（`Closeable`），定义写出协议与值参数化/行格式化通用逻辑。
- 继承：`implements Closeable`
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void init()` / `void writeHeader()` / `void writeTrial()` | 生命周期钩子 | 默认空实现 |
  | `Object parameterized(DamengColumn column, Object value, DamengDataExportConfig config)` | 值参数化 | 按列类型处理：日期 `DateUtil.format(date, config.getDateFormat())`；JSON/字符串 `TextUtil.escape`；二进制 `0x+HexUtil`；布尔转 `1/0`；BIT 转 `b'...'`；数值原样 |
  | `void writeObject(Map<String, Object>)` | 写入单条（抽象） | 由子类实现 |
  | `void writeObjects(List<Map<String, Object>>)` | 写入多条 | 逐条 `writeObject` |
  | `String formatLine(Object[] / List<?>, fieldSeparator, txtIdentifier, recordSeparator)` | 行格式化 | 各字段用 txtIdentifier 包裹并用 fieldSeparator 连接，末尾加 recordSeparator |

- 调用链：`DamengCsvTypeFileWriter.writeObject(...) → parameterized(...) + formatLine(...)`

---

### DamengCsvTypeFileReader

- 职责：达梦 CSV 类型文件读取器（逗号分隔）。
- 继承：`extends DamengTypeFileReader`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columns` | `List<String>` | 字段列表 |
  | `config` | `DamengDataImportConfig` | 导入配置 |
  | `reader` | `SkipAbleFileReader` | 文件读取器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengCsvTypeFileReader(File file, DamengDataImportConfig config)` | 构造 | 以 `config.getCharset()` 建 `SkipAbleFileReader`，调用 `init()` |
  | `void init()` | 初始化 | `jumpLine(config.getColumnIndex())` 跳到列名行，`parseLine(line, txtIdentifierChar(), ',')` 取列名，再 `jumpLine(config.getDataStartIndex())` |
  | `Map<String, Object> readObject()` | 读取一行 | 按 `,` 解析并与列名配对成 Map |
  | `void close()` | 关闭 | 关闭 reader 并置空字段 |

- 调用链：`new DamengCsvTypeFileReader(file, config) → init() → 读列名；readObject() → 逐行映射`

---

### DamengCsvTypeFileWriter

- 职责：达梦 CSV 类型文件写出器。
- 继承：`extends DamengTypeFileWriter`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columns` | `DamengColumns` | 字段列表 |
  | `config` | `DamengDataExportConfig` | 导出配置 |
  | `writer` | `LineFileWriter` | 文件写入器（`final`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengCsvTypeFileWriter(String filePath, DamengDataExportConfig config, DamengColumns columns)` | 构造 | `LineFileWriter.create(filePath, config.getCharset())` |
  | `void writeHeader()` | 写表头 | `formatLine(columns.columnNames(), ",", txtIdentifier, recordSeparator)` |
  | `void writeObject(Map<String, Object>)` | 写一行 | 按 `columns.index(name)` 定位并 `parameterized(...)`，再 `formatLine(values, ","...)` |
  | `void close()` | 关闭 | 关闭 writer 并置空 |

- 调用链：`writeHeader() → writeObject(...) → parameterized(...) → formatLine(...)`

---

### DamengTxtTypeFileReader

- 职责：达梦 TXT 类型文件读取器（可配置字段/记录分隔符与换行符）。
- 继承：`extends DamengTypeFileReader`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columns` | `List<String>` | 字段列表 |
  | `config` | `DamengDataImportConfig` | 导入配置 |
  | `reader` | `SkipAbleFileReader` | 文件读取器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengTxtTypeFileReader(File file, DamengDataImportConfig config)` | 构造 | 建 `SkipAbleFileReader`；记录分隔符与系统行分隔符不同时 `reader.lineBreak(...)` |
  | `void init()` | 初始化 | 跳到列名行，`parseLine(line, txtIdentifierChar(), fieldSeparatorChar())` 取列名，再跳到数据起始行 |
  | `Map<String, Object> readObject()` | 读取一行 | 按配置分隔符解析并与列名配对 |
  | `void close()` | 关闭 | 关闭并置空 |

- 调用链：`new DamengTxtTypeFileReader(file, config) → init() → readObject()`

---

### DamengTxtTypeFileWriter

- 职责：达梦 TXT 类型文件写出器（分隔符来自导出配置）。
- 继承：`extends DamengTypeFileWriter`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columns` | `DamengColumns` | 字段列表 |
  | `config` | `DamengDataExportConfig` | 导出配置 |
  | `writer` | `LineFileWriter` | 文件写入器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengTxtTypeFileWriter(String filePath, DamengDataExportConfig config, DamengColumns columns)` | 构造 | `LineFileWriter.create(...)` |
  | `void writeHeader()` | 写表头 | `formatLine(columnNames, config.getFieldSeparator(), ...)` |
  | `void writeObject(Map<String, Object>)` | 写一行 | 定位列、`parameterized(...)`、`formatLine(...)` |
  | `void close()` | 关闭 | 关闭并置空 |

- 调用链：`writeHeader() → writeObject(...)`

---

### DamengJsonTypeFileReader

- 职责：达梦 JSON 类型文件读取器（基于 fastjson2 `JSONReader`，支持纯数组与对象包装两种格式）。
- 继承：`extends DamengTypeFileReader`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `reader` | `JSONReader` | JSON 读取器 |
  | `config` | `DamengDataImportConfig` | 导入配置 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengJsonTypeFileReader(File file, DamengDataImportConfig config)` | 构造 | `JSONReader.of(FileUtil.getReader(file, charset))`，调用 `init()` |
  | `void init()` | 初始化 | `recordLabel` 为空时 `startArray()`；否则 `nextIfObjectStart()` 读字段名并匹配 `recordLabel` 后 `startArray()` |
  | `Map<String, Object> readObject()` | 读取一个对象 | 末尾或遇到 `]` 返回 null；`reader.readObject()` 后 `nextIfComma()` |
  | `void close()` | 关闭 | 包装格式时消费 `}`，关闭 reader |

- 调用链：`new DamengJsonTypeFileReader(file, config) → init() → readObject() → JSONReader.readObject()`

---

### DamengJsonTypeFileWriter

- 职责：达梦 JSON 类型文件写出器（支持早期版本 `{"RECORDS":[...]}` 与数组两种格式）。
- 继承：`extends DamengTypeFileWriter`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columns` | `DamengColumns` | 字段列表 |
  | `config` | `DamengDataExportConfig` | 导出配置 |
  | `writer` | `LineFileWriter` | 文件写入器 |
  | `firstWrite` | `boolean` | 是否首次写入（用于逗号分隔） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengJsonTypeFileWriter(String, DamengDataExportConfig, DamengColumns)` | 构造 | `LineFileWriter.create(...)` |
  | `void writeHeader()` | 写头 | 早期版本写 `{`+`"RECORDS": [`，否则写 `[` |
  | `void writeTrial()` | 写尾 | 早期版本 `]}`，否则 `]` |
  | `void writeObject(Map<String, Object>)` | 写对象 | 非首条前置 `,\n`；逐字段拼 `"key" : value`，数值裸写、其它加引号、null 写 `null` |
  | `Object parameterized(...)` | 值参数化 | null 返回 null，否则 `super.parameterized(...)` |
  | `void close()` | 关闭 | 关闭并置空 |

- 调用链：`writeHeader() → writeObject(...) → writeTrial() → close()`

---

### DamengXmlTypeFileReader

- 职责：达梦 XML 类型文件读取器（基于 StAX `XMLEventReader`）。
- 继承：`extends DamengTypeFileReader`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `reader` | `XMLEventReader` | XML 事件读取器 |
  | `config` | `DamengDataImportConfig` | 导入配置 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengXmlTypeFileReader(File file, DamengDataImportConfig config)` | 构造 | `XMLInputFactory.newInstance().createXMLEventReader(new FileInputStream(file), charset)`，调用 `init()` |
  | `void init()` | 初始化 | 前进到首个 `StartElement` |
  | `Map<String, Object> readObject()` | 读取一个 `<RECORD>` | `config.isAttrToColumn()` 为真时把属性读为字段；否则把子元素读为字段（名=子元素名、值=字符数据） |
  | `void close()` | 关闭 | 关闭 reader，异常包装为 `RuntimeException` |

- 调用链：`new DamengXmlTypeFileReader(file, config) → init() → readObject()`

---

### DamengXmlTypeFileWriter

- 职责：达梦 XML 类型文件写出器（字段可作属性或子元素）。
- 继承：`extends DamengTypeFileWriter`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columns` | `DamengColumns` | 字段列表 |
  | `config` | `DamengDataExportConfig` | 导出配置 |
  | `writer` | `LineFileWriter` | 文件写入器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengXmlTypeFileWriter(String, DamengDataExportConfig, DamengColumns)` | 构造 | `LineFileWriter.create(...)` |
  | `void writeHeader()` | 写头 | 写 XML 声明与 `<RECORDS>` |
  | `void writeTrial()` | 写尾 | 写 `</RECORDS>` |
  | `void writeObject(Map<String, Object>)` | 写记录 | `config.isFieldToAttr()` 为真输出 `<RECORD k="v" .../>`；否则输出 `<RECORD><k>v</k>...</RECORD>` |
  | `Object parameterized(...)` | 值参数化 | null 返回 null，否则 `super` |
  | `void close()` | 关闭 | `IOUtil.close(writer)` 并置空 |

- 调用链：`writeHeader() → writeObject(...) → writeTrial() → close()`

---

### DamengExcelTypeFileReader

- 职责：达梦 Excel（xls/xlsx）类型文件读取器（基于 POI `Workbook`）。
- 继承：`extends DamengTypeFileReader`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `workbook` | `Workbook` | 工作薄 |
  | `columns` | `List<String>` | 字段列表 |
  | `config` | `DamengDataImportConfig` | 导入配置 |
  | `currentRowIndex` | `Integer` | 当前行索引 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengExcelTypeFileReader(File file, DamengDataImportConfig config)` | 构造 | 按 `.xlsx` 后缀 `WorkbookHelper.create(isXlsx, file)`，调用 `init()` |
  | `void init()` | 初始化 | 取 `config.getColumnIndex()` 行为列名，`currentRowIndex = config.getDataStartIndex()` |
  | `Map<String, Object> readObject()` | 读取一行 | 按单元格类型取布尔/数值（含日期格式判断）/字符串，与列名配对 |
  | `void close()` | 关闭 | 关闭工作薄并置空 |

- 调用链：`new DamengExcelTypeFileReader(file, config) → init() → readObject()`

---

### DamengExcelTypeFileWriter

- 职责：达梦 Excel 类型文件写出器（POI），支持批量写入后统一落盘。
- 继承：`extends DamengTypeFileWriter`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columns` | `DamengColumns` | 字段列表 |
  | `config` | `DamengDataExportConfig` | 导出配置 |
  | `workbook` | `Workbook` | 工作薄 |
  | `xlsRowIndex` | `int` | 当前行索引（初始 1） |
  | `filePath` | `String` | 文件路径 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengExcelTypeFileWriter(String, DamengDataExportConfig, DamengColumns)` | 构造 | 按 `.xlsx` 后缀 `WorkbookHelper.create(isXlsx)` |
  | `void writeHeader()` | 写表头 | 重置行索引，用表名建 sheet，按 `sortOfPosition()` 写首行列名并落盘 |
  | `void writeObject(Map<String, Object>, boolean flush)` | 写一行（含 flush 开关） | 定位列并 `parameterized(...)`，按值类型（Date/Double/String/Boolean/Calendar/LocalDate/LocalDateTime/Number）写入单元格 |
  | `void writeObject(Map<String, Object>)` | 写一行 | 委托 `writeObject(object, true)` |
  | `void writeObjects(List<Map<String, Object>>)` | 批量写 | 逐条 `writeObject(object, false)` 后统一落盘 |
  | `void close()` | 关闭 | 关闭工作薄并置空 |
  | `Object parameterized(...)` | 值参数化 | null 返回 null，否则 `super` |

- 调用链：`writeHeader() → writeObjects(list) → writeObject(v, false) → WorkbookHelper.write(workbook, filePath)`

---

### DamengSqlTypeFileWriter

- 职责：达梦 SQL 文件写出器，把每行数据转为 `INSERT INTO ... VALUES (...)` 语句。
- 继承：`extends DamengTypeFileWriter`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columns` | `DamengColumns` | 字段列表 |
  | `config` | `DamengDataExportConfig` | 导出配置 |
  | `writer` | `LineFileWriter` | 文件写入器（`final`） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengSqlTypeFileWriter(String, DamengDataExportConfig, DamengColumns)` | 构造 | `LineFileWriter.create(...)` |
  | `void writeObject(Map<String, Object>)` | 写 INSERT | `INSERT INTO ` + 表名；`isIncludeFields()` 时拼列名；`VALUES (...)` 逐列 `parameterized(...)` |
  | `Object parameterized(DamengColumn, Object, DamengDataExportConfig)` | 值参数化（SQL 语义） | null → `NULL`；日期加单引号；JSON/字符串加单引号并 `TextUtil.escape`；二进制 `0x...`；布尔 `1/0`；BIT `b'...'` |
  | `void close()` | 关闭 | 关闭并置空 |

- 调用链：`writeObject(map) → columnList = columns.sortOfPosition() → parameterized(...) → INSERT 语句`

---

### DamengHtmlTypeFileWriter

- 职责：达梦 HTML 文件写出器，把数据渲染为 HTML 表格。
- 继承：`extends DamengTypeFileWriter`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `columns` | `DamengColumns` | 字段列表 |
  | `config` | `DamengDataExportConfig` | 导出配置 |
  | `writer` | `LineFileWriter` | 文件写入器 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `DamengHtmlTypeFileWriter(String, DamengDataExportConfig, DamengColumns)` | 构造 | `LineFileWriter.create(...)` |
  | `void writeHeader()` | 写 HTML 头与表头 | 内联 `<style>` 表格样式，按 `sortOfPosition()` 输出 `<th>` |
  | `void writeTrial()` | 写尾 | `</table></body></html>` |
  | `void writeObject(Map<String, Object>)` | 写一行 | 逐字段 `parameterized(...)` 后包 `<td>`，输出 `<tr>` |
  | `void close()` | 关闭 | 关闭并置空 |

- 调用链：`writeHeader() → writeObject(...) → writeTrial()`

---

### DamengDataExportConfig

- 职责：达梦数据导出配置（格式、分隔符、字符集等）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dateFormat` | `String` | 日期格式 |
  | `fieldToAttr` | `boolean` | 字段作为属性（XML） |
  | `includeFields` | `boolean` | 包含列标题（默认 true） |
  | `recordSeparator` | `String` | 记录分割符号（默认系统行分隔符） |
  | `fieldSeparator` | `String` | 字段分割符号（默认 `;`） |
  | `txtIdentifier` | `String` | 文本识别符号（默认 `"`） |
  | `charset` | `String` | 字符集（默认 UTF-8） |
  | `earlyVersion` | `boolean` | 是否早期版本（JSON 包装格式） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `getDateFormat/setDateFormat`、`isFieldToAttr/setFieldToAttr`、`isIncludeFields/setIncludeFields`、`getRecordSeparator/setRecordSeparator`、`getFieldSeparator/setFieldSeparator`、`getTxtIdentifier/setTxtIdentifier`、`getCharset/setCharset`、`isEarlyVersion/setEarlyVersion` | 属性访问器 | — |

- 调用链：`DamengTypeFileWriter.parameterized(column, value, config) → config.getDateFormat()`
- 调用链：`DamengCsvTypeFileWriter.writeHeader() → config.getTxtIdentifier()/getRecordSeparator()`

---

### DamengDataImportConfig

- 职责：达梦数据导入配置（导入模式、行索引、分隔符、JSON 标签等）。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dateFormat` | `String` | 日期格式 |
  | `importMode` | `String` | 导入模式（1 追加 / 2 复制，默认 `2`） |
  | `columnIndex` | `int` | 列名所在行索引（默认 0） |
  | `dataStartIndex` | `int` | 数据起始行索引（默认 1） |
  | `recordLabel` | `String` | 字段标签（JSON 包装键） |
  | `attrToColumn` | `boolean` | 属性作为字段（XML） |
  | `recordSeparator` | `String` | 记录分割符号 |
  | `fieldSeparator` | `String` | 字段分割符号（默认 `;`） |
  | `txtIdentifier` | `String` | 文本识别符号（默认 `"`） |
  | `charset` | `String` | 字符集（默认 UTF-8） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `boolean isAppendMode()` / `boolean isCopyMode()` | 导入模式判断 | `importMode` 与 `"1"` / `"2"` 比较 |
  | `char fieldSeparatorChar()` / `char txtIdentifierChar()` | 取首字符 | `fieldSeparator.charAt(0)` / `txtIdentifier.charAt(0)` |
  | `getDateFormat/setDateFormat`、`getImportMode/setImportMode`、`getColumnIndex/setColumnIndex`、`getDataStartIndex/setDataStartIndex`、`getRecordLabel/setRecordLabel`、`isAttrToColumn/setAttrToColumn`、`getRecordSeparator/setRecordSeparator`、`getFieldSeparator/setFieldSeparator`、`getTxtIdentifier/setTxtIdentifier`、`getCharset/setCharset` | 属性访问器 | — |

- 调用链：`DamengTxtTypeFileReader.init() → config.getColumnIndex()/getDataStartIndex()/fieldSeparatorChar()`

---

### DamengDataImportHelper

- 职责：达梦数据导入辅助工具（值参数化与生成 INSERT SQL）。
- 字段：无自声明字段。

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `static Object parameterized(DamengColumn column, Object value, DamengDataImportConfig config)` | 值参数化（导入语义） | 空值/空串 → null；日期/时间戳按 `config.getDateFormat()` 解析并格式化为 `yyyy-MM-dd HH:mm:ss`；字符串 `TextUtil.escape` |
  | `static List<String> toInsertSql(DamengColumns columns, List<DamengRecord> records, DamengDataImportConfig config)` | 生成 INSERT 语句列表 | 逐记录拼 `INSERT INTO 表(列...) VALUES (值...)`，值经 `DBUtil.unwrapData` → `parameterized` → `DBUtil.wrapData`；打印 SQL |

- 调用链：`DamengDataImportHelper.toInsertSql(columns, records, config) → parameterized(column, val, config) → DBUtil.wrapData(...)`

---

## 二、传输对象（dto）

### ShellDamengDataExportColumn

- 职责：导出字段（继承 `DamengColumn`），附带选中标志。
- 继承：`extends DamengColumn`
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `selected` | `boolean` | 是否选中（默认 true） |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `boolean isSelected()` / `void setSelected(boolean)` | 选中标志读写 | — |

- 调用链：`ShellDamengDataExportTable.columns(...) → new ShellDamengDataExportColumn() → copy(column)`

---

### ShellDamengDataExportTable

- 职责：导出表承载对象，管理表名、记录、导出字段、文件路径与选中状态，并提供对应 JavaFX 控件。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `name` | `String` | 表名称 |
  | `records` | `List<DamengRecord>` | 记录列表（查询导出用） |
  | `columns` | `List<ShellDamengDataExportColumn>` | 字段列表 |
  | `filePathProperty` | `StringProperty` | 文件路径属性 |
  | `selectedProperty` | `BooleanProperty` | 是否选中属性 |
  | `extensionProperty` | `ObjectProperty<FileExtensionFilter>` | 扩展后缀属性 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `BooleanProperty selectedProperty()` | 选中属性 | 懒建并监听：选中且路径为空时 `updateFilePath()` |
  | `boolean isSelected()` / `void setSelected(boolean)` | 选中读写 | — |
  | `FXCheckBox getSelectedControl()` | 选中控件 | 双向同步（用 `AtomicBoolean` 防止回环） |
  | `StringProperty filePathProperty()` / `String getFilePath()` / `void setFilePath(String)` | 文件路径读写 | — |
  | `SaveFileTextField getFilePathControl()` | 文件路径控件 | 绑定扩展名、初始文件名与选中文件回调 |
  | `ObjectProperty<FileExtensionFilter> extensionProperty()` | 扩展属性 | 懒建并监听变更 `updateFilePath()`；`getExtension/setExtension` 读写 |
  | `String fileName()` | 生成文件名 | `name + 扩展名`（去掉前导点） |
  | `void columns(List<? extends DamengColumn>)` | 设置导出字段 | 逐个 `copy` 为 `ShellDamengDataExportColumn` |
  | `List<DamengColumn> columns()` | 取字段列表 | 返回副本 |
  | `List<DamengColumn> selectedColumns()` / `List<String> selectedColumnNames()` | 取选中字段/名 | 过滤 `isSelected()` |
  | `boolean hasColumns()` | 是否含字段 | `CollectionUtil.isNotEmpty` |
  | `void updateFilePath()` | 更新默认路径 | 选中或有路径时设为桌面目录 + 文件名 |
  | `getName/setName`、`getColumns/setColumns`、`getRecords/setRecords` | 属性访问器 | — |

- 调用链：`ShellDamengDataExportTable.getFilePathControl() → SaveFileTextField.setOnSelectedFile(...) → setFilePath(...)`

---

### ShellDamengDataImportFile

- 职责：导入文件承载对象，管理待导入文件与目标表，并提供选择控件。
- 字段：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | `dbName` | `String` | 库名称 |
  | `dbClient` | `ShellDamengClient` | 数据库客户端 |
  | `fileProperty` | `ObjectProperty<File>` | 文件属性 |
  | `targetTableName` | `String` | 目标表名称 |

- 方法：

  | 方法 | 说明 | 关键逻辑/调用 |
  |---|---|---|
  | `void setDbName(String)` / `void setDbClient(ShellDamengClient)` | 注入库名/客户端 | — |
  | `ObjectProperty<File> fileProperty()` | 文件属性 | 懒建 `SimpleObjectProperty` |
  | `File getFile()` / `void setFile(File)` / `String getFilePath()` / `String getFileName()` | 文件读写 | 由 `fileProperty` 派生路径与文件名 |
  | `ChooseFileTextField getFilePathControl()` | 文件选择控件 | `setOnSelectedFile(this::setFile)`，双向同步路径 |
  | `DamengTableComboBox getTargetTableControl()` | 目标表控件 | `StageManager.showMask(() -> comboBox.init(dbName, getTableName(), dbClient))`，选中变更 `setTargetTableName` |
  | `String getTableName()` | 由文件名推断表名 | 去掉最后一个 `.` 后的后缀 |
  | `String getTargetTableName()` / `void setTargetTableName(String)` | 目标表名读写 | 为空时回退 `getTableName()` |

- 调用链：`ShellDamengDataImportFile.getTargetTableControl() → StageManager.showMask(...) → DamengTableComboBox.init(dbName, tableName, dbClient)`

---

## 覆盖统计

- 覆盖类数：20 个（data 17 + dto 3）。
- 类名清单：
  - data：`DamengTypeFileReader`、`DamengTypeFileWriter`、`DamengCsvTypeFileReader`、`DamengCsvTypeFileWriter`、`DamengTxtTypeFileReader`、`DamengTxtTypeFileWriter`、`DamengJsonTypeFileReader`、`DamengJsonTypeFileWriter`、`DamengXmlTypeFileReader`、`DamengXmlTypeFileWriter`、`DamengExcelTypeFileReader`、`DamengExcelTypeFileWriter`、`DamengSqlTypeFileWriter`、`DamengHtmlTypeFileWriter`、`DamengDataExportConfig`、`DamengDataImportConfig`、`DamengDataImportHelper`
  - dto：`ShellDamengDataExportColumn`、`ShellDamengDataExportTable`、`ShellDamengDataImportFile`
- 跳过（死代码）：`DamengDataExportHelper`、`ShellDamengDataTransportView`、`ShellDamengDataTransportProcedure`、`ShellDamengDataTransportTable`、`ShellDamengDataTransportTrigger`、`ShellDamengDataTransportFunction`。
