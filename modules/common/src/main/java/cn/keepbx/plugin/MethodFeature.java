package cn.keepbx.plugin;

/**
 * @author bwcx_jzy
 * @date 2019/8/13
 */
public enum MethodFeature {
    /**
     * 没有
     */
    NULL(""),
    /**
     * 文件管理
     */
    FILE("文件管理"),
    EDIT("修改"),
    DEL("删除"),
    INSTALL("安装"),
    LIST("列表"),
    TERMINAL("终端"),
    DOWNLOAD("下载"),
    LOG("日志"),
    UPLOAD("上传"),
    WHITELIST("白名单"),
    EXECUTE("执行"),
    ;

    private String name;

    public String getName() {
        return name;
    }

    MethodFeature(String name) {
        this.name = name;
    }
}
