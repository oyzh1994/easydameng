package cn.oyzh.easydameng.data.dameng.handler;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.dameng.ShellDamengClient;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.data.DamengCsvTypeFileWriter;
import cn.oyzh.easydameng.dameng.data.DamengDataExportConfig;
import cn.oyzh.easydameng.dameng.data.DamengExcelTypeFileWriter;
import cn.oyzh.easydameng.dameng.data.DamengHtmlTypeFileWriter;
import cn.oyzh.easydameng.dameng.data.DamengJsonTypeFileWriter;
import cn.oyzh.easydameng.dameng.data.DamengSqlTypeFileWriter;
import cn.oyzh.easydameng.dameng.data.DamengTxtTypeFileWriter;
import cn.oyzh.easydameng.dameng.data.DamengTypeFileWriter;
import cn.oyzh.easydameng.dameng.data.DamengXmlTypeFileWriter;
import cn.oyzh.easydameng.dameng.dto.ShellDamengDataExportTable;
import cn.oyzh.easydameng.dameng.record.DamengRecord;
import cn.oyzh.easydameng.dameng.record.DamengSelectRecordParam;
import cn.oyzh.easydameng.util.DamengDataUtil;
import cn.oyzh.fx.db.data.handler.DBDataExportHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 达梦数据导出处理器
 *
 * @author oyzh
 * @since 2024/08/27
 */
public class DamengDataExportHandler extends DBDataExportHandler {

    /**
     * db客户端
     */
    private ShellDamengClient dbClient;

    /**
     * 导出配置
     */
    private final DamengDataExportConfig config;

    /**
     * 导出表
     */
    private List<ShellDamengDataExportTable> tables;

    /**
     * 构造数据导出处理器
     *
     * @param dbClient db客户端
     * @param schema   模式
     */
    public DamengDataExportHandler(ShellDamengClient dbClient, String schema) {
        super(schema);
        this.dbClient = dbClient;
        this.config = new DamengDataExportConfig();
    }

    @Override
    public void doExport() throws Exception {
        this.message("Export Starting");
        if (CollectionUtil.isNotEmpty(this.tables)) {
            for (ShellDamengDataExportTable table : this.tables) {
                this.checkInterrupt();
                this.exportTable(table);
                this.processedIncr();
            }
        }
        this.message("Export Finished");
    }

    /**
     * 初始化文件写入器
     *
     * @param filePath 文件路径
     * @param columns  字段集合
     * @return 文件写入器
     * @throws IOException IO异常
     */
    private DamengTypeFileWriter initWriter(String filePath, DamengColumns columns) throws IOException {
        if (this.isSqlType()) {
            return new DamengSqlTypeFileWriter(filePath, this.config, columns);
        }
        if (this.isExcelType()) {
            return new DamengExcelTypeFileWriter(filePath, this.config, columns);
        }
        if (this.isHtmlType()) {
            return new DamengHtmlTypeFileWriter(filePath, this.config, columns);
        }
        if (this.isJsonType()) {
            return new DamengJsonTypeFileWriter(filePath, this.config, columns);
        }
        if (this.isXmlType()) {
            return new DamengXmlTypeFileWriter(filePath, this.config, columns);
        }
        if (this.isCsvType()) {
            return new DamengCsvTypeFileWriter(filePath, this.config, columns);
        }
        if (this.isTxtType()) {
            return new DamengTxtTypeFileWriter(filePath, this.config, columns);
        }
        return null;
    }

    /**
     * 导出表
     *
     * @param table 表
     * @throws Exception 异常
     */
    protected void exportTable(ShellDamengDataExportTable table) throws Exception {
        String tableName = table.getName();
        this.message("Exporting Table " + table.getName());
        this.message("Exporting Records of Table " + table.getName());
        long start = 0;
        DamengColumns columns = new DamengColumns(table.selectedColumns());
        try (DamengTypeFileWriter writer = this.initWriter(table.getFilePath(), columns)) {
            this.writeHeader(writer, table, columns);
            if (!columns.isEmpty()) {
                boolean stop = false;
                while (!stop) {
                    this.checkInterrupt();
                    List<DamengRecord> records;
                    // 正常导出
                    if (table.getRecords() == null) {
                        long start1 = System.currentTimeMillis();
                        DamengSelectRecordParam param = new DamengSelectRecordParam();
                        param.setStart(start);
                        param.setReadonly(true);
                        param.setColumns(columns);
                        param.setSchema(this.name);
                        param.setTableName(tableName);
                        param.setLimit((long) this.queryLimit);
                        records = this.dbClient.selectRecords(param);
                        if (CollectionUtil.isEmpty(records)) {
                            break;
                        }
                        long end1 = System.currentTimeMillis();
                        JulLog.info("查询耗时: {}ms", (end1 - start1));
                    } else {// 查询导出
                        records = table.getRecords();
                        stop = true;
                    }
                    // 写入记录
                    long start2 = System.currentTimeMillis();
                    this.writeRecord(writer, table, columns, records);
                    long end2 = System.currentTimeMillis();
                    JulLog.info("写入耗时: {}ms", (end2 - start2));
                    start += this.queryLimit;
                    this.processed(records.size());
                }
            }
            this.writeTail(writer);
        } finally {
            this.message("Exporting Table " + tableName + " To -> " + table.getFilePath());
        }
    }

    /**
     * 写入头
     *
     * @param writer  文件写入器
     * @param table   导出表
     * @param columns 字段列表
     * @throws Exception 异常
     */
    private void writeHeader(DamengTypeFileWriter writer, ShellDamengDataExportTable table, DamengColumns columns) throws Exception {
        writer.writeHeader();
    }

    /**
     * 写入记录
     *
     * @param writer  文件写入器
     * @param table   导出表
     * @param columns 字段列表
     * @param records 记录列表
     * @throws Exception 异常
     */
    private void writeRecord(DamengTypeFileWriter writer, ShellDamengDataExportTable table, DamengColumns columns, List<DamengRecord> records) throws Exception {
        List<Map<String, Object>> objects = new ArrayList<>();
        for (DamengRecord object : records) {
            objects.add(object.toMap());
        }
        // 规整化值
        for (Map<String, Object> object : objects) {
            for (Map.Entry<String, Object> entry : object.entrySet()) {
                entry.setValue(DamengDataUtil.valueStandardization(entry.getValue()));
            }
        }
        writer.writeObjects(objects);
    }

    /**
     * 写入尾
     *
     * @param writer 文件写入器
     * @throws Exception 异常
     */
    private void writeTail(DamengTypeFileWriter writer) throws Exception {
        writer.writeTrial();
    }

    /**
     * 设置日期格式
     *
     * @param dateFormat 日期格式
     */
    public void dateFormat(String dateFormat) {
        if (StringUtil.isBlank(dateFormat)) {
            this.config.setDateFormat("yyyy-MM-dd HH:mm:ss");
        } else {
            this.config.setDateFormat(dateFormat);
        }
    }

    /**
     * 设置记录分隔符
     *
     * @param recordSeparator 记录分隔符
     */
    public void recordSeparator(String recordSeparator) {
        this.config.setRecordSeparator(recordSeparator);
    }

    /**
     * 设置文本限定符
     *
     * @param txtIdentifier 文本限定符
     */
    public void txtIdentifier(String txtIdentifier) {
        this.config.setTxtIdentifier(txtIdentifier);
    }

    /**
     * 设置字段分隔符
     *
     * @param fieldSeparator 字段分隔符
     */
    public void fieldSeparator(String fieldSeparator) {
        this.config.setFieldSeparator(fieldSeparator);
    }

    /**
     * 设置是否包含字段名
     *
     * @param includeFields 是否包含字段名
     */
    public void includeFields(boolean includeFields) {
        this.config.setIncludeFields(includeFields);
    }

    /**
     * 设置字段名转为属性名
     *
     * @param fieldToAttr 字段名转为属性名
     */
    public void fieldToAttr(boolean fieldToAttr) {
        this.config.setFieldToAttr(fieldToAttr);
    }

    /**
     * 设置是否兼容早期版本
     *
     * @param earlyVersion 是否兼容早期版本
     */
    public void earlyVersion(boolean earlyVersion) {
        this.config.setEarlyVersion(earlyVersion);
    }

    /**
     * 获取db客户端
     *
     * @return db客户端
     */
    public ShellDamengClient getDbClient() {
        return dbClient;
    }

    /**
     * 设置db客户端
     *
     * @param dbClient db客户端
     */
    public void setDbClient(ShellDamengClient dbClient) {
        this.dbClient = dbClient;
    }

    /**
     * 获取导出表集合
     *
     * @return 导出表集合
     */
    public List<ShellDamengDataExportTable> getTables() {
        return tables;
    }

    /**
     * 设置导出表集合
     *
     * @param tables 导出表集合
     */
    public void setTables(List<ShellDamengDataExportTable> tables) {
        this.tables = tables;
    }

    /**
     * 获取导出配置
     *
     * @return 导出配置
     */
    public DamengDataExportConfig getConfig() {
        return config;
    }

}
