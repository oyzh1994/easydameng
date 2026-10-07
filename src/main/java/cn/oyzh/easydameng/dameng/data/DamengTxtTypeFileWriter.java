package cn.oyzh.easydameng.dameng.data;

import cn.oyzh.common.file.LineFileWriter;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.column.DamengColumns;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * 达梦 TXT 类型文件写出器
 *
 * @author oyzh
 * @since 2024-09-04
 */
public class DamengTxtTypeFileWriter extends DamengTypeFileWriter {

    /**
     * 字段列表
     */
    private DamengColumns columns;

    /**
     * 导出配置
     */
    private DamengDataExportConfig config;

    /**
     * 文件写入器
     */
    private LineFileWriter writer;

    /**
     * 构造 TXT 类型文件写出器
     *
     * @param filePath 目标文件路径
     * @param config   导出配置
     * @param columns  字段列表
     * @throws FileNotFoundException 文件不存在异常
     */
    public DamengTxtTypeFileWriter(String filePath, DamengDataExportConfig config, DamengColumns columns) throws FileNotFoundException {
        this.columns = columns;
        this.config = config;
        this.writer = LineFileWriter.create(filePath, config.getCharset());
    }

    @Override
    public void writeHeader() throws Exception {
        this.writer.write(this.formatLine(this.columns.columnNames(), this.config.getFieldSeparator(), this.config.getTxtIdentifier(),
                this.config.getRecordSeparator()));
    }

    @Override
    public void writeObject(Map<String, Object> object) throws Exception {
        Object[] values = new Object[this.columns.size()];
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            int index = this.columns.index(entry.getKey());
            DamengColumn column = this.columns.column(entry.getKey());
            Object val = this.parameterized(column, entry.getValue(), this.config);
            values[index] = val;
        }
        this.writer.write(this.formatLine(values,
                this.config.getFieldSeparator(),
                this.config.getTxtIdentifier(),
                this.config.getRecordSeparator()));
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
        this.writer = null;
        this.config = null;
        this.columns = null;
    }
}
