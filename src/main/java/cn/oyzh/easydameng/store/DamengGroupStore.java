package cn.oyzh.easydameng.store;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easydameng.domain.DamengGroup;
import cn.oyzh.store.jdbc.param.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.param.QueryParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 达梦数据库连接分组存储。
 *
 * @author oyzh
 * @since 2023/5/12
 */
public class DamengGroupStore extends JdbcStandardStore<DamengGroup> {

    /**
     * 当前实例
     */
    public static final DamengGroupStore INSTANCE = new DamengGroupStore();

    /**
     * 加载分组列表
     *
     * @return 分组列表
     */
    public List<DamengGroup> load() {
        return super.selectList();
    }

    /**
     * 保存分组，已存在则更新，否则新增
     *
     * @param group 分组对象
     * @return 结果
     */
    public boolean replace(DamengGroup group) {
        if (group != null) {
            if (this.exist(group.getName()) || super.exist(group.getGid())) {
                return this.update(group);
            }
            return this.insert(group);
        }
        return false;
    }

    /**
     * 根据分组名称删除分组
     *
     * @param name 分组名称
     * @return 结果
     */
    public boolean delete(String name) {
        if (StringUtil.isNotBlank(name)) {
            DeleteParam param = new DeleteParam();
            param.addQueryParam(new QueryParam("name", name));
            return this.delete(param);
        }
        return false;
    }

    /**
     * 是否存在此分组信息
     *
     * @param name 分组信息
     * @return 结果
     */
    public boolean exist(String name) {
        if (StringUtil.isNotBlank(name)) {
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            return super.exist(params);
        }
        return false;
    }

    @Override
    protected Class<DamengGroup> modelClass() {
        return DamengGroup.class;
    }
}
