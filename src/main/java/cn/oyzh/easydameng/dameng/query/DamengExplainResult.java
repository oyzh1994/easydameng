package cn.oyzh.easydameng.dameng.query;

import cn.oyzh.easydameng.dameng.DamengHelper;
import cn.oyzh.easydameng.dameng.column.DamengColumn;
import cn.oyzh.easydameng.dameng.record.DamengRecord;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 达梦执行计划结果
 *
 * @author oyzh
 * @since 2024/08/16
 */
public class DamengExplainResult extends DamengQueryResult {

    @Override
    public void parseResult(ResultSet resultSet, Connection connection, boolean readonly) throws SQLException {
        this.columns = DamengHelper.parseColumns(resultSet);
        this.records = new ArrayList<>();
        while (resultSet.next()) {
            int colIndex = 1;
            DamengRecord record = new DamengRecord(this.columns, readonly);
            for (DamengColumn dbColumn : this.columns) {
                Object data = resultSet.getObject(colIndex++);
                record.putValue(dbColumn, data);
            }
            this.records.add(record);
        }
    }

}
