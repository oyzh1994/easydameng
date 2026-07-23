package cn.oyzh.easydameng.dto;

import cn.oyzh.common.dto.Project;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easydameng.domain.DamengConnect;
import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * db连接导出对象
 *
 * @author oyzh
 * @since 2023/06/22
 */
public class DamengInfoExport {

    /**
     * 导出程序版本号
     */
    private String version;

    /**
     * 平台
     */
    private String platform;

    /**
     * 导出连接数据
     */
    private List<DamengConnect> connects;

    /**
     * 从db连接数据生成
     *
     * @param dbInfos 连接列表
     * @return DBInfoExport
     */
    public static DamengInfoExport fromConnects(List<DamengConnect> dbInfos) {
        DamengInfoExport export = new DamengInfoExport();
        Project project = Project.load();
        export.version = project.getVersion();
        export.connects = dbInfos;
        export.platform = System.getProperty("os.name");
        return export;
    }

    /**
     * 从json对象数据生成
     *
     * @param json json字符串
     * @return RedisInfoExport
     */
    public static DamengInfoExport fromJSON(String json) {
        JulLog.info("json: {}", json);
        JSONObject object = JSONUtil.parseObject(json);
        DamengInfoExport export = new DamengInfoExport();
        export.connects = new ArrayList<>();
        export.version = object.getString("version");
        export.connects = JSONUtil.toList(object, "connects", DamengConnect.class);
        return export;
    }

    /**
     * 转成json字符串
     *
     * @return json字符串
     */
    public String toJSONString() {
        return JSONUtil.toJson(this);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<DamengConnect> getConnects() {
        return connects;
    }

    public void setConnects(List<DamengConnect> connects) {
        this.connects = connects;
    }
}
