package cn.oyzh.easydameng.dameng.record;

import cn.oyzh.easydameng.dameng.column.DamengColumn;

import java.util.List;

/**
 * 达梦查询记录参数
 *
 * @author oyzh
 * @since 2024-09-13
 */
public class DamengSelectRecordParam {

    /**
     * 起始位置
     */
    private Long start;

    /**
     * 查询条数
     */
    private Long limit;

    /**
     * 数据库模式名
     */
    private String schema;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 是否只读
     */
    private boolean readonly;

    /**
     * 字段列表
     */
    private List<DamengColumn> columns;

    /**
     * 过滤条件列表
     */
    private List<DamengRecordFilter> filters;

    /**
     * 主键信息
     */
    private DamengRecordPrimaryKey primaryKey;

    /**
     * 是否存在分页
     *
     * @return 是否存在分页
     */
    public boolean hasPageControl() {
        return this.start != null && this.limit != null;
    }

    /**
     * 获取起始位置
     *
     * @return 起始位置
     */
    public Long getStart() {
        return start;
    }

    /**
     * 设置起始位置
     *
     * @param start 起始位置
     */
    public void setStart(Long start) {
        this.start = start;
    }

    /**
     * 获取查询条数
     *
     * @return 查询条数
     */
    public Long getLimit() {
        return limit;
    }

    /**
     * 设置查询条数
     *
     * @param limit 查询条数
     */
    public void setLimit(Long limit) {
        this.limit = limit;
    }

    /**
     * 获取数据库模式名
     *
     * @return 数据库模式名
     */
    public String getSchema() {
        return schema;
    }

    /**
     * 设置数据库模式名
     *
     * @param schema 数据库模式名
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 获取表名
     *
     * @return 表名
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 设置表名
     *
     * @param tableName 表名
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 是否只读
     *
     * @return 是否只读
     */
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * 设置是否只读
     *
     * @param readonly 是否只读
     */
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    /**
     * 获取字段列表
     *
     * @return 字段列表
     */
    public List<DamengColumn> getColumns() {
        return columns;
    }

    /**
     * 设置字段列表
     *
     * @param columns 字段列表
     */
    public void setColumns(List<DamengColumn> columns) {
        this.columns = columns;
    }

    /**
     * 获取过滤条件列表
     *
     * @return 过滤条件列表
     */
    public List<DamengRecordFilter> getFilters() {
        return filters;
    }

    /**
     * 设置过滤条件列表
     *
     * @param filters 过滤条件列表
     */
    public void setFilters(List<DamengRecordFilter> filters) {
        this.filters = filters;
    }

    /**
     * 获取主键信息
     *
     * @return 主键信息
     */
    public DamengRecordPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    /**
     * 设置主键信息
     *
     * @param primaryKey 主键信息
     */
    public void setPrimaryKey(DamengRecordPrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }
}
