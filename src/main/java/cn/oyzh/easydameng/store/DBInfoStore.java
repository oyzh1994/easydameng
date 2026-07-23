//package cn.oyzh.easydameng.store;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.lang.UUID;
//import cn.hutool.core.util.StrUtil;
//import cn.oyzh.common.dto.Paging;
//import cn.oyzh.common.json.JSONUtil;
//import cn.oyzh.common.log.JulLog;
//import cn.oyzh.easydameng.DamengConst;
//import cn.oyzh.easydameng.domain.DamengConnect;
//import cn.oyzh.store.json.ArrayFileStore;
//import lombok.NonNull;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * db信息存储
// *
// * @author oyzh
// * @since 2020/5/23
// */
//public class DBInfoStore extends ArrayFileStore<DamengConnect> {
//
//    /**
//     * 当前实例
//     */
//    public static final DBInfoStore INSTANCE = new DBInfoStore();
//
//    /**
//     * 已加载的db节点
//     */
//    private final List<DamengConnect> infos;
//
//    {
//        this.filePath(DamengConst.STORE_PATH + "db_info.json");
//        JulLog.info("DBInfoStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
//        this.infos = this.load();
//        for (DamengConnect dbInfo : this.infos) {
//            if (StrUtil.isBlank(dbInfo.getId())) {
//                dbInfo.setId(UUID.fastUUID().toString(true));
//                this.update(dbInfo);
//            }
//        }
//    }
//
//    @Override
//    public synchronized List<DamengConnect> load() {
//        // 如果infos为空
//        if (this.infos == null) {
//            // 读取storeFile文件的内容
//            String text = FileUtil.readString(this.storeFile(), this.charset());
//            // 如果文件内容为空
//            if (StrUtil.isBlank(text)) {
//                // 返回空列表
//                return new ArrayList<>();
//            }
//            // 将文件内容解析为dbInfo列表
//            List<DamengConnect> infos = JSONUtil.toBeanList(text, DamengConnect.class);
//            // 如果dbInfo列表非空
//            if (CollUtil.isNotEmpty(infos)) {
//                // 对dbInfo列表进行排序
//                infos = infos.parallelStream().sorted().collect(Collectors.toList());
//            }
//            // 返回排序后的dbInfo列表
//            return infos;
//        }
//        // 返回已有的dbInfo列表
//        return this.infos;
//    }
//
//    @Override
//    public synchronized boolean add( DamengConnect dbInfo) {
//        try {
//            if (!this.infos.contains(dbInfo)) {
//                if (StrUtil.isBlank(dbInfo.getId())) {
//                    dbInfo.setId(UUID.fastUUID().toString(true));
//                }
//                // 添加到集合
//                this.infos.add(dbInfo);
//                // 更新数据
//                return this.save(this.infos);
//            }
//        } catch (Exception e) {
//            JulLog.warn("add error,err:{}", e.getMessage());
//        }
//        return false;
//    }
//
//    @Override
//    public synchronized boolean update( DamengConnect dbInfo) {
//        try {
//            // 更新数据
//            if (this.infos.contains(dbInfo)) {
//                return this.save(this.infos);
//            }
//        } catch (Exception e) {
//            JulLog.warn("update error,err:{}", e.getMessage());
//        }
//        return false;
//    }
//
//    @Override
//    public synchronized boolean delete( DamengConnect dbInfo) {
//        try {
//            // 删除数据
//            if (this.infos.remove(dbInfo)) {
//                return this.save(this.infos);
//            }
//        } catch (Exception e) {
//            JulLog.warn("delete error,err:{}", e.getMessage());
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public synchronized Paging<DamengConnect> getPage(int limit, Map<String, Object> params) {
//        // 加载数据
//        List<DamengConnect> infos = this.load();
//        // 分页对象
//        Paging<DamengConnect> paging = new Paging<>(infos, limit);
//        // 数据为空
//        if (CollUtil.isNotEmpty(infos)) {
//            String searchKeyWord = params == null ? null : (String) params.get("searchKeyWord");
//            // 过滤数据
//            if (StrUtil.isNotBlank(searchKeyWord)) {
//                final String kw = searchKeyWord.toLowerCase().trim();
//                infos = infos.parallelStream().filter(z ->
//                        z.getHost() != null && z.getHost().contains(kw)
//                                || z.getName() != null && z.getName().toLowerCase().contains(kw)
//                                || z.getRemark() != null && z.getRemark().toLowerCase().contains(kw)
//                ).collect(Collectors.toList());
//            }
//            // 对数据排序
//            infos = infos.parallelStream().sorted().collect(Collectors.toList());
//            // 添加到分页数据
//            paging.dataList(infos);
//        }
//        return paging;
//    }
//}
