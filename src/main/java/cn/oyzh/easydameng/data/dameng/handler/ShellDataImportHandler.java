package cn.oyzh.easydameng.data.dameng.handler;


/**
 * @author oyzh
 * @since 2024/08/27
 */
public abstract class ShellDataImportHandler extends ShellDataHandler {

    /**
     * 执行导入
     *
     * @throws Exception 异常
     */
    public abstract void doImport() throws Exception ;
}

