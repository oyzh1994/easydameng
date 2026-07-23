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
 * redis分组存储
 *
 * @author oyzh
 * @since 2023/5/12
 */
public class DamengGroupStore extends JdbcStandardStore<DamengGroup> {

    /**
     * 当前实例
     */
    public static final DamengGroupStore INSTANCE = new DamengGroupStore();

    public List<DamengGroup> load() {
        return super.selectList();
    }

    public boolean replace(DamengGroup group) {
        if (group != null) {
            if (this.exist(group.getName()) || super.exist(group.getGid())) {
                return this.update(group);
            }
            return this.insert(group);
        }
        return false;
    }

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
