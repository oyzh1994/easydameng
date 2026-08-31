//package cn.oyzh.easydameng.handler;
//
//import cn.oyzh.common.thread.ThreadUtil;
//import cn.oyzh.common.util.CollectionUtil;
//import cn.oyzh.common.util.StringUtil;
//import cn.oyzh.easydameng.dameng.ShellDamengClient;
//import cn.oyzh.easydameng.db.DBDialect;
//import cn.oyzh.easydameng.domain.DamengConnect;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author oyzh
// * @since 2024/08/29
// */
//public abstract class DataRunSqlFileHandler extends DataHandler {
//
//    /**
//     * 库名称
//     */
//    protected String dbName;
//
//    /**
//     * sql文件
//     */
//    protected File sqlFile;
//
//    /**
//     * db客户端
//     */
//    protected ShellDamengClient dbClient;
//
//    /**
//     * 连接信息
//     */
//    protected DamengConnect dbInfo;
//
//    /**
//     * 插入限制
//     */
//    protected int insertLimit = 1;
//
//    /**
//     * 批量限制
//     */
//    protected int batchLimit = 1;
//
//    /**
//     * 遇到错误时继续
//     */
//    protected boolean continueWithErrors = true;
//
//    /**
//     * 方言
//     */
//    private DBDialect dialect;
//
//    public DataRunSqlFileHandler(ShellDamengClient dbClient, String dbName) {
//        this.dbClient = dbClient;
//        this.dbName = dbName;
//    }
//
//    /**
//     * 设置sql文件
//     *
//     * @param sqlFile sql文件
//     * @return 当前对象
//     */
//    public DataRunSqlFileHandler sqlFile(File sqlFile) {
//        this.sqlFile = sqlFile;
//        return this;
//    }
//
//    /**
//     * 运行sql文件
//     */
//    public abstract void runSqlFile() throws Exception ;
//
//    /**
//     * 插入集合
//     */
//    protected List<String> insertList;
//
//    /**
//     * 添加插入sql
//     *
//     * @param sql 插入sql
//     */
//    protected void addInsertSql(String sql) {
//        if (StringUtil.isNotBlank(sql)) {
//            if (this.insertList == null) {
//                this.insertList = new ArrayList<>();
//            }
//            this.insertList.add(sql);
//            if (this.insertList.size() >= this.insertLimit) {
//                this.doBatchInsert();
//            }
//        }
//    }
//
//    /**
//     * 执行批量插入
//     */
//    protected void doBatchInsert() {
//        if (CollectionUtil.isNotEmpty(this.insertList)) {
//            try {
//                if (this.insertList.size() <= this.batchLimit) {
//                    this.doBatchInsert(this.insertList, false);
//                } else {
//                    List<List<String>> lists = CollectionUtil.split(this.insertList, this.batchLimit);
//                    List<Runnable> tasks = new ArrayList<>();
//                    for (List<String> list : lists) {
//                        tasks.add(() -> this.doBatchInsert(list, true));
//                    }
//                    ThreadUtil.submit(tasks);
//                }
//            } finally {
//                this.insertList.clear();
//            }
//        }
//    }
//
//    /**
//     * 执行批量插入
//     *
//     * @param sqlList  sql列表
//     * @param parallel 是否并发
//     */
//    protected void doBatchInsert(List<String> sqlList, boolean parallel) {
//        try {
//            int result = this.dbClient.insertBatch(this.dbName, sqlList, parallel);
//            this.processedIncr(result);
//        } catch (Exception ex) {
//            this.processedDecr(sqlList.size());
//            throw ex;
//        }
//    }
//
//    /**
//     * 创建新的处理器
//     *
//     * @param dbClient db客户端
//     * @param dbName   数据库
//     * @return DataDumpHandler
//     */
//    public static DataRunSqlFileHandler newHandler(ShellDamengClient dbClient, String dbName) {
//        DataRunSqlFileHandler handler = switch (dbClient.dialect()) {
//            case DAMENG -> new DamengDataRunSqlFileHandler(dbClient, dbName);
//            default -> null;
//        };
//        if (handler != null) {
//            handler.setDialect(dbClient.dialect());
//        }
//        return handler;
//    }
//
//    public String getDbName() {
//        return dbName;
//    }
//
//    public void setDbName(String dbName) {
//        this.dbName = dbName;
//    }
//
//    public File getSqlFile() {
//        return sqlFile;
//    }
//
//    public void setSqlFile(File sqlFile) {
//        this.sqlFile = sqlFile;
//    }
//
//    public ShellDamengClient getDbClient() {
//        return dbClient;
//    }
//
//    public void setDbClient(ShellDamengClient dbClient) {
//        this.dbClient = dbClient;
//    }
//
//    public DamengConnect getDbInfo() {
//        return dbInfo;
//    }
//
//    public DataRunSqlFileHandler setDbInfo(DamengConnect dbInfo) {
//        this.dbInfo = dbInfo;
//        return this;
//    }
//
//    public int getInsertLimit() {
//        return insertLimit;
//    }
//
//    public void setInsertLimit(int insertLimit) {
//        this.insertLimit = insertLimit;
//    }
//
//    public int getBatchLimit() {
//        return batchLimit;
//    }
//
//    public void setBatchLimit(int batchLimit) {
//        this.batchLimit = batchLimit;
//    }
//
//    public boolean isContinueWithErrors() {
//        return continueWithErrors;
//    }
//
//    public void setContinueWithErrors(boolean continueWithErrors) {
//        this.continueWithErrors = continueWithErrors;
//    }
//
//    public DBDialect getDialect() {
//        return dialect;
//    }
//
//    public void setDialect(DBDialect dialect) {
//        this.dialect = dialect;
//    }
//}
//
