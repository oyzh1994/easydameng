package cn.oyzh.easydameng.handler;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easydameng.dameng.DamengClient;
import cn.oyzh.easydameng.db.DBDialect;
import cn.oyzh.easydameng.dameng.column.DamengColumns;
import cn.oyzh.easydameng.dameng.column.DamengSelectColumnParam;
import cn.oyzh.easydameng.dameng.event.DamengEvent;
import cn.oyzh.easydameng.dameng.function.DamengFunction;
import cn.oyzh.easydameng.dameng.procedure.DamengProcedure;
import cn.oyzh.easydameng.dameng.record.DamengRecord;
import cn.oyzh.easydameng.dameng.record.DamengSelectRecordParam;
import cn.oyzh.easydameng.dameng.table.DamengSelectTableParam;
import cn.oyzh.easydameng.dameng.table.DamengTable;
import cn.oyzh.easydameng.dameng.trigger.DamengTrigger;
import cn.oyzh.easydameng.dameng.view.DamengView;
import cn.oyzh.easydameng.util.DBDataUtil;
import cn.oyzh.easydameng.util.DBUtil;

import java.io.IOException;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/10
 */
public class DamengDataDumpHandler extends DataDumpHandler {

    public DamengDataDumpHandler(DamengClient dbClient, String dbName) {
        super(dbClient, dbName);
    }

    @Override
    public void doDump() throws Exception {
        if (this.fileWriter == null || this.dumpType == null || this.dataType == null) {
            throw new RuntimeException("parameter invalid!");
        }
        this.message("Dump Starting");
        this.writeHeader();
        if (this.dumpType == 1) {
            this.dumpTable();
            this.dumpView();
            this.dumpFunction();
            this.dumpProcedure();
            this.dumpTrigger();
            this.dumpEvent();
        } else if (this.dumpType == 2) {
            DamengSelectTableParam selectTableParam = new DamengSelectTableParam();
            selectTableParam.setFull(true);
            selectTableParam.setDbName(this.dbName);
            selectTableParam.setTableName(this.tableName);
            DamengTable table = this.dbClient.selectFullTable(selectTableParam);
            this.dumpTable(table);
        }
        this.writeTail();
        this.fileWriter.close();
        this.message("Dump Finished");
        this.message("Dump File To -> " + this.dumpFile.getPath());
    }

    protected void dumpTable() throws InterruptedException, IOException {
        DamengSelectTableParam selectTableParam = new DamengSelectTableParam();
        selectTableParam.setFull(true);
        selectTableParam.setDbName(this.dbName);
        List<DamengTable> tables = this.dbClient.selectTables(selectTableParam);
        if (CollectionUtil.isNotEmpty(tables)) {
            for (DamengTable table : tables) {
                this.checkInterrupt();
                this.dumpTable(table);
            }
            this.processed(tables.size());
        }
    }

    protected void dumpTable(DamengTable table) throws InterruptedException, IOException {
        String line0 = "";
        String line1 = "-- ----------------------------";
        String line2 = "-- Table structure for " + table.getName();
        String line3 = "-- ----------------------------";
        String dropTable = "DROP TABLE IF EXISTS " + DBUtil.wrap(table.getName(), DBDialect.DAMENG) + ";";
        String createDefinition = table.getCreateDefinition();
        if (!createDefinition.endsWith(";")) {
            createDefinition += ";";
        }
        this.message("Dumping Table " + table.getName());
        this.fileWriter.appendLines(List.of(line0, line1, line2, line3, dropTable, createDefinition));
        if (this.isDumpRecord()) {
            this.message("Dumping Records of Table " + table.getName());
            this.dumpRecord(table.getName());
        }
    }

    protected void dumpRecord(String tableName) throws InterruptedException, IOException {
        long start = 0;
        String line0 = "";
        String line1 = "-- ----------------------------";
        String line2 = "-- Records of " + tableName;
        String line3 = "-- ----------------------------";
        this.fileWriter.appendLines(List.of(line0, line1, line2, line3));
        DamengColumns columns = new DamengColumns(this.dbClient.selectColumns(new DamengSelectColumnParam(this.dbName, tableName)));
        while (true) {
            this.checkInterrupt();
            long start1 = System.currentTimeMillis();
            DamengSelectRecordParam param = new DamengSelectRecordParam();
            param.setStart(start);
            param.setReadonly(true);
            param.setColumns(columns);
            param.setDbName(this.dbName);
            param.setTableName(tableName);
            param.setLimit((long) this.queryLimit);
            List<DamengRecord> records = this.dbClient.selectRecords(param);
            if (CollectionUtil.isEmpty(records)) {
                break;
            }
            long end1 = System.currentTimeMillis();
            JulLog.info("查询耗时: {}ms", (end1 - start1));
            long start2 = System.currentTimeMillis();
            List<String> inserts = DBDataUtil.toInsertSql(columns, records);
            this.fileWriter.appendLines(inserts);
            long end2 = System.currentTimeMillis();
            JulLog.info("写入耗时: {}ms", (end2 - start2));
            start += this.queryLimit;
            this.processed(records.size());
        }
    }

    protected void dumpView() throws Exception {
        List<DamengView> views = this.dbClient.views(this.dbName);
        if (CollectionUtil.isNotEmpty(views)) {
            for (DamengView view : views) {
                this.checkInterrupt();
                this.message("Dumping View " + view.getName());
                String line0 = "";
                String line1 = "-- ----------------------------";
                String line2 = "-- View structure for " + view.getName();
                String line3 = "-- ----------------------------";
                String dropTable = "DROP VIEW IF EXISTS " + DBUtil.wrap(view.getName(), DBDialect.DAMENG) + ";";
                String createDefinition = this.dbClient.showCreateView(this.dbName, view.getName());
                if (!createDefinition.endsWith(";")) {
                    createDefinition += ";";
                }
                this.fileWriter.appendLines(List.of(line0, line1, line2, line3, dropTable, createDefinition));
            }
            this.processed(views.size());
        }
    }

    protected void dumpFunction() throws Exception {
        List<DamengFunction> functions = this.dbClient.functions(this.dbName);
        if (CollectionUtil.isNotEmpty(functions)) {
            for (DamengFunction function : functions) {
                this.checkInterrupt();
                this.message("Dumping Function " + function.getName());
                String line0 = "";
                String line1 = "-- ----------------------------";
                String line2 = "-- Function structure for " + function.getName();
                String line3 = "-- ----------------------------";
                String dropFunction = "DROP FUNCTION IF EXISTS " + DBUtil.wrap(function.getName(), DBDialect.DAMENG) + ";";
                String line4 = "delimiter ;;";
                String line5 = ";;";
                String line6 = "delimiter ;";
                String createDefinition = this.dbClient.showCreateFunction(this.dbName, function.getName());
                this.fileWriter.appendLines(List.of(line0, line1, line2, line3, dropFunction, line4, createDefinition, line5, line6));
            }
            this.processed(functions.size());
        }
    }

    protected void dumpProcedure() throws Exception {
        List<DamengProcedure> procedures = this.dbClient.procedures(this.dbName);
        if (CollectionUtil.isNotEmpty(procedures)) {
            for (DamengProcedure procedure : procedures) {
                this.checkInterrupt();
                this.message("Dumping Procedure " + procedure.getName());
                String line0 = "";
                String line1 = "-- ----------------------------";
                String line2 = "-- Procedure structure for " + procedure.getName();
                String line3 = "-- ----------------------------";
                String dropProcedure = "DROP PROCEDURE IF EXISTS " + DBUtil.wrap(procedure.getName(), DBDialect.DAMENG) + ";";
                String line4 = "delimiter ;;";
                String line5 = ";;";
                String line6 = "delimiter ;";
                String createDefinition = this.dbClient.showCreateProcedure(this.dbName, procedure.getName());
                this.fileWriter.appendLines(List.of(line0, line1, line2, line3, dropProcedure, line4, createDefinition, line5, line6));
            }
            this.processed(procedures.size());
        }
    }

    protected void dumpTrigger() throws Exception {
        List<DamengTrigger> triggers = this.dbClient.selectTriggers(this.dbName);
        if (CollectionUtil.isNotEmpty(triggers)) {
            for (DamengTrigger trigger : triggers) {
                this.message("Dumping Trigger " + trigger.getName());
                String line0 = "";
                String line1 = "-- ----------------------------";
                String line2 = "-- Trigger structure for " + trigger.getName();
                String line3 = "-- ----------------------------";
                String dropTrigger = "DROP TRIGGER IF EXISTS " + DBUtil.wrap(trigger.getName(), DBDialect.DAMENG) + ";";
                String line4 = "delimiter ;;";
                String line5 = ";;";
                String line6 = "delimiter ;";
                String createDefinition = this.dbClient.showCreateTrigger(this.dbName, trigger.getName());
                this.fileWriter.appendLines(List.of(line0, line1, line2, line3, dropTrigger, line4, createDefinition, line5, line6));
            }
            this.processed(triggers.size());
        }
    }

    protected void dumpEvent() throws Exception {
        List<DamengEvent> events = this.dbClient.events(this.dbName);
        if (CollectionUtil.isNotEmpty(events)) {
            for (DamengEvent event : events) {
                this.message("Dumping Event " + event.getName());
                String line0 = "";
                String line1 = "-- ----------------------------";
                String line2 = "-- Event structure for " + event.getName();
                String line3 = "-- ----------------------------";
                String dropTrigger = "DROP EVENT IF EXISTS " + DBUtil.wrap(event.getName(), DBDialect.DAMENG) + ";";
                String line4 = "delimiter ;;";
                String line5 = ";;";
                String line6 = "delimiter ;";
                String createDefinition = event.getCreateDefinition();
                this.fileWriter.appendLines(List.of(line0, line1, line2, line3, dropTrigger, line4, createDefinition, line5, line6));
            }
            this.processed(events.size());
        }
    }
}

