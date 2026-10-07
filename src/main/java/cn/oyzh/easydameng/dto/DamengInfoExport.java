package cn.oyzh.easydameng.dto;

import cn.oyzh.common.dto.Project;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easydameng.domain.DamengConnect;
import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 达梦数据库连接信息导出对象，用于连接配置的导出与导入。
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
     * 根据连接列表生成导出对象
     *
     * @param dbInfos 连接列表
     * @return 导出对象
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
     * 从json对象数据生成导出对象
     *
     * @param json json字符串
     * @return 导出对象
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

    /**
     * 获取导出程序版本号
     *
     * @return 导出程序版本号
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置导出程序版本号
     *
     * @param version 导出程序版本号
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取生成平台
     *
     * @return 生成平台
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * 设置生成平台
     *
     * @param platform 生成平台
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * 获取导出连接数据
     *
     * @return 导出连接数据
     */
    public List<DamengConnect> getConnects() {
        return connects;
    }

    /**
     * 设置导出连接数据
     *
     * @param connects 导出连接数据
     */
    public void setConnects(List<DamengConnect> connects) {
        this.connects = connects;
    }
}
