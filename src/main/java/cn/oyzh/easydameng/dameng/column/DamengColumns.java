package cn.oyzh.easydameng.dameng.column;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.db.DBObjectList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * db字段列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class DamengColumns extends DBObjectList<DamengColumn> {

    /**
     * 构造字段列表
     */
    public DamengColumns() {

    }

    /**
     * 构造字段列表
     *
     * @param list 字段列表
     */
    public DamengColumns(List<DamengColumn> list) {
        super.addAll(list);
    }

    /**
     * 获取主键字段列表（自增字段优先）
     *
     * @return 主键字段列表
     */
    public List<DamengColumn> primaryKeys() {
        List<DamengColumn> list1 = new ArrayList<>();
        for (DamengColumn column : this) {
            if (column.isPrimaryKey() && !DBObjectList.isDeleted(column)) {
                list1.add(column);
            }
        }
        return list1.parallelStream().filter(DamengColumn::isPrimaryKey).sorted((o1, o2) -> {
            if (o1.isAutoIncrement() && !o2.isAutoIncrement()) {
                return -1;
            }
            if (o1.isAutoIncrement() && o2.isAutoIncrement()) {
                return 0;
            }
            return 1;
        }).collect(Collectors.toList());
    }

    /**
     * 主键是否已变更
     *
     * @return 结果
     */
    public boolean primaryKeyChanged() {
        return false;
    }

    /**
     * 根据名称获取字段
     *
     * @param name 字段名称
     * @return 字段
     */
    public DamengColumn column(String name) {
        if (!this.isEmpty()) {
            for (DamengColumn dbColumn : this) {
                if (StringUtil.equalsAnyIgnoreCase(dbColumn.getName(), name)) {
                    return dbColumn;
                }
            }
        }
        return null;
    }

    /**
     * 获取字段位置索引
     *
     * @param name 字段名称
     * @return 位置索引
     */
    public int index(String name) {
        int index = 0;
        for (DamengColumn dbColumn : this) {
            if (dbColumn.getName().equals(name)) {
                break;
            }
            index++;
        }
        return index;
    }

    /**
     * 获取按位置排序后的字段列表
     *
     * @return 字段列表
     */
    public List<DamengColumn> sortOfPosition() {
        return this.parallelStream()
                .sorted(Comparator.comparing(DamengColumn::getPosition))
                .collect(Collectors.toList());
    }

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String tableName() {
        for (DamengColumn dbColumn : this) {
            return dbColumn.getTableName();
        }
        return null;
    }

    /**
     * 获取库名称
     *
     * @return 库名称
     */
    public String schema() {
        for (DamengColumn dbColumn : this) {
            return dbColumn.getSchema();
        }
        return null;
    }

    /**
     * 获取字段名称列表
     *
     * @return 字段名称列表
     */
    public List<String> columnNames() {
        List<String> list = new ArrayList<>();
        for (DamengColumn dbColumn : this) {
            list.add(dbColumn.getName());
        }
        return list;
    }

    /**
     * 是否包含主键
     *
     * @return 结果
     */
    public boolean hasPrimaryKey() {
        for (DamengColumn column : this) {
            if (column.isPrimaryKey() || column.isAutoIncrement()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否包含自增字段
     *
     * @return 结果
     */
    public boolean hasAutoIncrement() {
        for (DamengColumn column : this) {
            if (column.isAutoIncrement()) {
                return true;
            }
        }
        return false;
    }
}
