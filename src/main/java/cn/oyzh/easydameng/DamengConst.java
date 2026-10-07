package cn.oyzh.easydameng;


import java.io.File;

/**
 * 达梦数据库客户端常量对象，集中定义存储路径、图标等全局常量。
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class DamengConst {

    /**
     * 数据保存路径
     */
    public static final String STORE_PATH = System.getProperty("user.home") + File.separator + ".easydameng" + File.separator;

    /**
     * 缓存保存路径
     */
    public static final String CACHE_PATH = STORE_PATH + "cache" + File.separator;

    /**
     * icon地址
     */
    public final static String ICON_PATH = "/image/db_clip.png";

    /**
     * 托盘icon地址
     */
    public final static String TRAY_ICON_PATH = "/image/db_clip.png";

}
