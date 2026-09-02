//package cn.oyzh.easydameng.query;
//
//import cn.oyzh.common.thread.ThreadUtil;
//import cn.oyzh.common.util.TextUtil;
//import cn.oyzh.easydameng.dameng.ShellDamengClient;
//import cn.oyzh.easydameng.dameng.schema.DamengSchema;
//import cn.oyzh.fx.db.DBDialect;
//import cn.oyzh.easydameng.dameng.column.DamengColumn;
//import cn.oyzh.easydameng.dameng.function.DamengFunction;
//import cn.oyzh.easydameng.dameng.procedure.DamengProcedure;
//import cn.oyzh.easydameng.dameng.table.DamengTable;
//import cn.oyzh.easydameng.dameng.view.DamengView;
//import cn.oyzh.easydameng.util.DamengUtil;
//import cn.oyzh.fx.db.util.DBUtil;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.stream.Collectors;
//
///**
// * @author oyzh
// * @since 2024/2/21
// */
//public class DamengQueryUtil {
//
//    /**
//     * 0未初始化
//     * 1 初始化中
//     * 2 已初始化
//     */
//    private static int indexStatus = 0;
//
//    /**
//     * 关键字
//     */
//    private static final List<String> DB_KEYWORDS = new ArrayList<>();
//
//    /**
//     * 数据库
//     */
//    private static final List<DamengSchema> DB_DATABASES = new ArrayList<>();
//
//    /**
//     * 表
//     */
//    private static final List<DamengTable> DB_TABLES = new ArrayList<>();
//
//    /**
//     * 视图
//     */
//    private static final List<DamengView> DB_VIEWS = new ArrayList<>();
//
//    /**
//     * 函数
//     */
//    private static final List<DamengFunction> DB_FUNCTIONS = new ArrayList<>();
//
//    /**
//     * 过程
//     */
//    private static final List<DamengProcedure> DB_PROCEDURES = new ArrayList<>();
//
//    /**
//     * 字段
//     */
//    private static final List<DamengColumn> DB_COLUMNS = new ArrayList<>();
//
//    static {
//        // dml
//        DB_KEYWORDS.add("SELECT");
//        DB_KEYWORDS.add("UPDATE");
//        DB_KEYWORDS.add("DELETE");
//        DB_KEYWORDS.add("FROM");
//        DB_KEYWORDS.add("WHERE");
//        DB_KEYWORDS.add("WHEN");
//        DB_KEYWORDS.add("INSERT INTO");
//
//        // ddl
//        DB_KEYWORDS.add("DATABASE");
//        DB_KEYWORDS.add("DATABASES");
//        DB_KEYWORDS.add("SHOW");
//        DB_KEYWORDS.add("ALTER TABLE");
//        DB_KEYWORDS.add("CREATE TABLE");
//        DB_KEYWORDS.add("DROP TABLE");
//        DB_KEYWORDS.add("CHANGE");
//        DB_KEYWORDS.add("COLUMN");
//        DB_KEYWORDS.add("CHARACTER SET");
//        DB_KEYWORDS.add("COMMENT");
//        DB_KEYWORDS.add("FIRST");
//        DB_KEYWORDS.add("COLLATE");
//
//        // query
//        DB_KEYWORDS.add("AS");
//        DB_KEYWORDS.add("LIKE");
//        DB_KEYWORDS.add("IN");
//        DB_KEYWORDS.add("BETWEEN");
//        DB_KEYWORDS.add("AND");
//        DB_KEYWORDS.add("OR");
//        DB_KEYWORDS.add("NOT");
//        DB_KEYWORDS.add("NULL");
//        DB_KEYWORDS.add("IS");
//        DB_KEYWORDS.add("CASE");
//        DB_KEYWORDS.add("THEN");
//        DB_KEYWORDS.add("ELSE");
//        DB_KEYWORDS.add("END");
//        DB_KEYWORDS.add("GROUP BY");
//        DB_KEYWORDS.add("ORDER BY");
//        DB_KEYWORDS.add("LIMIT");
//        DB_KEYWORDS.add("HAVING");
//        DB_KEYWORDS.add("ON");
//        DB_KEYWORDS.add("JOIN");
//        DB_KEYWORDS.add("LEFT JOIN");
//        DB_KEYWORDS.add("RIGHT JOIN");
//        DB_KEYWORDS.add("CROSS JOIN");
//        DB_KEYWORDS.add("FULL JOIN");
//        DB_KEYWORDS.add("INNER JOIN");
//        DB_KEYWORDS.add("INTERSECT");
//        DB_KEYWORDS.add("UNION");
//        DB_KEYWORDS.add("UNION ALL");
//        DB_KEYWORDS.add("EXCEPT");
//        DB_KEYWORDS.add("COUNT");
//        DB_KEYWORDS.add("SUM");
//        DB_KEYWORDS.add("MAX");
//        DB_KEYWORDS.add("MIN");
//        DB_KEYWORDS.add("AVG");
//        DB_KEYWORDS.add("DISTINCT");
//        DB_KEYWORDS.add("ASC");
//        DB_KEYWORDS.add("DESC");
//        DB_KEYWORDS.add("EXISTS");
//        DB_KEYWORDS.add("ANY");
//        DB_KEYWORDS.add("ALL");
//
//        // 函数
//        DB_KEYWORDS.add("CONCAT");
//        DB_KEYWORDS.add("LENGTH");
//        DB_KEYWORDS.add("SUBSTRING");
//        DB_KEYWORDS.add("UPPER");
//        DB_KEYWORDS.add("LOWER");
//        DB_KEYWORDS.add("TRIM");
//        DB_KEYWORDS.add("ROUND");
//    }
//
//    public static List<String> getKeywords() {
//        return DB_KEYWORDS;
//    }
//
//    public static List<DamengSchema> getDatabases() {
//        return DB_DATABASES;
//    }
//
//    public static List<DamengTable> getTables() {
//        return DB_TABLES;
//    }
//
//    public static List<DamengView> getViews() {
//        return DB_VIEWS;
//    }
//
//    public static List<DamengFunction> getFunctions() {
//        return DB_FUNCTIONS;
//    }
//
//    public static List<DamengProcedure> getProcedures() {
//        return DB_PROCEDURES;
//    }
//
//    public static List<DamengColumn> getColumns() {
//        return DB_COLUMNS;
//    }
//
//    public static void updateIndex(ShellDamengClient client) {
//        updateIndex(client, true);
//    }
//
//    public static void updateIndex(ShellDamengClient client, boolean async) {
//        Runnable task = () -> {
//            if (indexStatus == 0) {
//                try {
//                    indexStatus = 1;
//                    DB_VIEWS.clear();
//                    DB_TABLES.clear();
//                    DB_COLUMNS.clear();
//                    DB_FUNCTIONS.clear();
//                    DB_PROCEDURES.clear();
//                    DB_DATABASES.clear();
//                    // 更新库索引
//                    List<DamengSchema> databases = client.schemas();
//                    DB_DATABASES.addAll(databases);
//                    // 更新表索引
//                    for (DamengSchema database : DB_DATABASES) {
//                        if (!DamengUtil.isInternalDatabase(database.getName())) {
//                            List<DamengTable> tables = client.selectTablesSimple(database.getName());
//                            DB_TABLES.addAll(tables);
//                        }
//                    }
//                    // 更新视图索引
//                    for (DamengSchema database : DB_DATABASES) {
//                        if (!DamengUtil.isInternalDatabase(database.getName())) {
//                            List<DamengView> views = client.selectViewsSimple(database.getName());
//                            DB_VIEWS.addAll(views);
//                        }
//                    }
//                    // 更新函数索引
//                    for (DamengSchema database : DB_DATABASES) {
//                        if (!DamengUtil.isInternalDatabase(database.getName())) {
//                            List<DamengFunction> functions = client.selectFunctionsSimple(database.getName());
//                            DB_FUNCTIONS.addAll(functions);
//                        }
//                    }
//                    // 更新过程索引
//                    for (DamengSchema database : DB_DATABASES) {
//                        if (!DamengUtil.isInternalDatabase(database.getName())) {
//                            List<DamengProcedure> procedures = client.selectProceduresSimple(database.getName());
//                            DB_PROCEDURES.addAll(procedures);
//                        }
//                    }
//                    // // 更新字段索引
//                    // for (DamengTable dbTable : DB_TABLES) {
//                    //     if (!DBUtil.isInternalDatabase(dbTable.getDbName())) {
//                    //         List<DamengColumn> columns = client.selectColumns(new DamengSelectColumnParam(dbTable.getDbName(), dbTable.getName()));
//                    //         DB_COLUMNS.addAll(columns);
//                    //     }
//                    // }
//                    indexStatus = 2;
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    indexStatus = 0;
//                }
//            }
//        };
//        if (async) {
//            ThreadUtil.start(task);
//        } else {
//            task.run();
//        }
//    }
//
////    public static double clacCorr(String str, String text) {
////        str = str.toUpperCase();
////        text = text.toUpperCase();
////        if (!str.contains(text) && !text.contains(str)) {
////            return 0.d;
////        }
////        double corr = StringUtil.similarity(str, text);
////        if (str.startsWith(text)) {
////            corr += 0.3;
////        } else if (str.contains(text)) {
////            corr += 0.2;
////        } else if (str.endsWith(text)) {
////            corr += 0.1;
////        }
////        return corr;
////    }
//
//    /**
//     * 初始化提示词
//     *
//     * @param token   提示词
//     * @param minCorr 最低相关度
//     * @return 结果
//     */
//    public static List<DamengQueryPromptItem> initPrompts(DamengQueryToken token, float minCorr) {
//        if (token == null || token.isEmpty()) {
//            return Collections.emptyList();
//        }
//        // 当前提示词
//        String text = token.getContent().toUpperCase();
//        // 提示词列表
//        final List<DamengQueryPromptItem> items = new CopyOnWriteArrayList<>();
//        // 任务列表
//        List<Runnable> tasks = new ArrayList<>();
//        // 关键字
//        if (token.isPossibilityKeyword()) {
//            tasks.add(() -> DamengQueryUtil.getKeywords().parallelStream().forEach(keyword -> {
//                // 计算相关度
//                double corr = TextUtil.clacCorr(keyword, text);
//                if (corr > minCorr) {
//                    DamengQueryPromptItem item = new DamengQueryPromptItem();
//                    item.setType((byte) 4);
//                    item.setContent(keyword);
//                    item.setCorrelation(corr);
//                    items.add(item);
//                }
//            }));
//        }
//        // 库
//        if (token.isPossibilityDatabase()) {
//            tasks.add(() -> DamengQueryUtil.getDatabases().parallelStream().forEach(database -> {
//                // 计算相关度
//                double corr = TextUtil.clacCorr(database.getName(), text);
//                if (corr > minCorr) {
//                    DamengQueryPromptItem item = new DamengQueryPromptItem();
//                    item.setType((byte) 1);
//                    item.setContent(database.getName());
//                    item.setCorrelation(corr);
//                    items.add(item);
//                }
//            }));
//        }
//        // 表
//        if (token.isPossibilityTable()) {
//            tasks.add(() -> DamengQueryUtil.getTables().parallelStream().forEach(dbTable -> {
//                // 计算相关度
//                double corr = TextUtil.clacCorr(dbTable.getName(), text);
//                if (corr > minCorr) {
//                    DamengQueryPromptItem item = new DamengQueryPromptItem();
//                    item.setType((byte) 2);
//                    item.setCorrelation(corr);
//                    item.setContent(dbTable.getName());
//                    item.setExtContent(dbTable.getSchema());
//                    items.add(item);
//                }
//            }));
//        }
//        // 视图
//        if (token.isPossibilityView()) {
//            tasks.add(() -> DamengQueryUtil.getViews().parallelStream().forEach(dbTable -> {
//                // 计算相关度
//                double corr = TextUtil.clacCorr(dbTable.getName(), text);
//                if (corr > minCorr) {
//                    DamengQueryPromptItem item = new DamengQueryPromptItem();
//                    item.setType((byte) 5);
//                    item.setCorrelation(corr);
//                    item.setContent(dbTable.getName());
//                    item.setExtContent(dbTable.getSchema());
//                    items.add(item);
//                }
//            }));
//        }
//        // 函数
//        if (token.isPossibilityFunction()) {
//            tasks.add(() -> DamengQueryUtil.getFunctions().parallelStream().forEach(function -> {
//                // 计算相关度
//                double corr = TextUtil.clacCorr(function.getName(), text);
//                if (corr > minCorr) {
//                    DamengQueryPromptItem item = new DamengQueryPromptItem();
//                    item.setType((byte) 6);
//                    item.setCorrelation(corr);
//                    item.setContent(function.getName());
//                    item.setExtContent(function.getSchema());
//                    items.add(item);
//                }
//            }));
//        }
//        // 过程
//        if (token.isPossibilityProcedure()) {
//            tasks.add(() -> DamengQueryUtil.getProcedures().parallelStream().forEach(procedure -> {
//                // 计算相关度
//                double corr = TextUtil.clacCorr(procedure.getName(), text);
//                if (corr > minCorr) {
//                    DamengQueryPromptItem item = new DamengQueryPromptItem();
//                    item.setType((byte) 7);
//                    item.setCorrelation(corr);
//                    item.setContent(procedure.getName());
//                    item.setExtContent(procedure.getSchema());
//                    items.add(item);
//                }
//            }));
//        }
//        // 字段
//        if (token.isPossibilityColumn()) {
//            tasks.add(() -> DamengQueryUtil.getColumns().parallelStream().forEach(column -> {
//                // 计算相关度
//                double corr = TextUtil.clacCorr(column.getName(), text);
//                if (corr > minCorr) {
//                    DamengQueryPromptItem item = new DamengQueryPromptItem();
//                    item.setType((byte) 3);
//                    item.setCorrelation(corr);
//                    item.setContent(column.getName());
//                    item.setExtContent(DBUtil.wrap(column.getSchema(), column.getTableName(), DBDialect.DAMENG));
//                    items.add(item);
//                }
//            }));
//        }
//        // 执行任务
//        ThreadUtil.submit(tasks);
//        // 根据相关度排序
//        List<DamengQueryPromptItem> itemList = items.parallelStream().sorted(Comparator.comparingDouble(DamengQueryPromptItem::getCorrelation)).collect(Collectors.toList());
//        // 反转列表
//        return itemList.reversed();
//    }
//}
