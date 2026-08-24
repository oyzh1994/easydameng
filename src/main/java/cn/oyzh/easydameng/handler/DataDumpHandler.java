package cn.oyzh.easydameng.handler;

import cn.oyzh.common.date.DateHelper;
import cn.oyzh.common.file.FastFileWriter;
import cn.oyzh.easydameng.dameng.DamengClient;
import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.domain.DamengConnect;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/08/22
 */
public abstract class DataDumpHandler extends DataHandler {

    /**
     * 数据类型
     * 0 数据和结构
     * 1 仅结构
     */
    protected Byte dataType;

    /**
     * 库名称
     */
    protected String dbName;

    /**
     * 转储文件
     */
    protected File dumpFile;

    /**
     * 文件写入器
     */
    protected FastFileWriter fileWriter;

    /**
     * db客户端
     */
    protected DamengClient dbClient;

    /**
     * 1. 库
     * 2. 表
     */
    protected Byte dumpType;

    /**
     * 表名称
     */
    protected String tableName;

    /**
     * 连接信息
     */
    protected DamengConnect dbInfo;

    /**
     * 查询限制
     */
    protected int queryLimit = 1000;

    /**
     * 方言
     */
    private DBDialect dialect;

    public DataDumpHandler(DamengClient dbClient, String dbName) {
        this.dbClient = dbClient;
        this.dbName = dbName;
    }

    /**
     * 设置转储文件
     *
     * @param dumpFile 转储文件
     * @return 当前对象
     */
    public DataDumpHandler dumpFile(File dumpFile) throws IOException {
        this.dumpFile = dumpFile;
        if (this.fileWriter != null) {
            this.fileWriter.close();
        }
        this.fileWriter = new FastFileWriter(dumpFile);
        return this;
    }


    /**
     * 执行转储
     *
     * @throws Exception 异常
     */
    public abstract void doDump() throws Exception;

    /**
     * 写入头部
     */
    protected void writeHeader() throws IOException {
        String version = this.dbClient.selectVersion();
        String clientCharacter = this.dbClient.selectClientCharacter();
        String header = "/*\n";
        header += " EasyDB Data Transfer";
        header += "\n\n";
        header += " Source Server : " + this.dbInfo.getName();
        header += "\n";
        header += " Source Server Type : " + this.dbClient.dialect().name();
        header += "\n";
        header += " Source Server Version : " + version;
        header += "\n";
        header += " Source Host : " + this.dbInfo.getHost();
        header += "\n";
        header += " Source Schema : " + this.dbName;
        header += "\n\n";
        header += " Target Server Type : " + this.dbClient.dialect().name();
        header += "\n";
        header += " Target Server Version : " + version;
        header += "\n";
        header += " File Encoding : " + clientCharacter;
        header += "\n\n";
        header += " Date : " + DateHelper.formatDateTimeSimple();
        header += "\n";
        header += "*/";

        header += "\n\n";
        this.fileWriter.writeLines(List.of(header));
    }

    /**
     * 写入尾部
     */
    protected void writeTail() throws IOException {
    }

    public boolean isDumpRecord() {
        return this.dataType == 0;
    }

    /**
     * 创建新的处理器
     *
     * @param dbClient db客户端
     * @param dbName   数据库
     * @return DataDumpHandler
     */
    public static DataDumpHandler newHandler(DamengClient dbClient, String dbName) {
        DataDumpHandler handler = switch (dbClient.dialect()) {
            case DAMENG -> new DamengDataDumpHandler( dbClient, dbName);
            default -> null;
        };
        if (handler != null) {
            handler.dialect=dbClient.dialect();
        }
        return handler;
    }

    public Byte getDataType() {
        return dataType;
    }

    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public File getDumpFile() {
        return dumpFile;
    }

    public void setDumpFile(File dumpFile) {
        this.dumpFile = dumpFile;
    }

    public FastFileWriter getFileWriter() {
        return fileWriter;
    }

    public void setFileWriter(FastFileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

    public DamengClient getDbClient() {
        return dbClient;
    }

    public void setDbClient(DamengClient dbClient) {
        this.dbClient = dbClient;
    }

    public Byte getDumpType() {
        return dumpType;
    }

    public DataDumpHandler setDumpType(Byte dumpType) {
        this.dumpType = dumpType;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public DataDumpHandler setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public DamengConnect getDbInfo() {
        return dbInfo;
    }

    public DataDumpHandler setDbInfo(DamengConnect dbInfo) {
        this.dbInfo = dbInfo;
        return this;
    }

    public int getQueryLimit() {
        return queryLimit;
    }

    public DataDumpHandler setQueryLimit(int queryLimit) {
        this.queryLimit = queryLimit;
        return this;
    }

    public DBDialect getDialect() {
        return dialect;
    }

    public void setDialect(DBDialect dialect) {
        this.dialect = dialect;
    }
}

