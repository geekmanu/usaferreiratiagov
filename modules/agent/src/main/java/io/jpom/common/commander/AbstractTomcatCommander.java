package io.jpom.common.commander;

import cn.hutool.http.HttpRequest;
import cn.hutool.system.SystemUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import io.jpom.common.commander.impl.LinuxTomcatCommander;
import io.jpom.common.commander.impl.WindowsTomcatCommander;
import io.jpom.model.data.TomcatInfoModel;
import io.jpom.system.JpomRuntimeException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * tomcat命令执行工具类
 *
 * @author LF
 */
public abstract class AbstractTomcatCommander {

    private static AbstractTomcatCommander abstractTomcatCommander;

    public static AbstractTomcatCommander getInstance() {
        if (abstractTomcatCommander != null) {
            return abstractTomcatCommander;
        }
        if (SystemUtil.getOsInfo().isLinux()) {
            // Linux系统
            abstractTomcatCommander = new LinuxTomcatCommander();
        } else if (SystemUtil.getOsInfo().isWindows()) {
            // Windows系统
            abstractTomcatCommander = new WindowsTomcatCommander();
        } else if (SystemUtil.getOsInfo().isMac()) {
            abstractTomcatCommander = new LinuxTomcatCommander();
        } else {
            throw new JpomRuntimeException("不支持的：" + SystemUtil.getOsInfo().getName());
        }
        return abstractTomcatCommander;
    }

    /**
     * 执行tomcat命令
     *
     * @param tomcatInfoModel tomcat信息
     * @param cmd             执行的命令，包括start stop
     * @return 返回tomcat启动结果
     */
    public abstract String execCmd(TomcatInfoModel tomcatInfoModel, String cmd);

    /**
     * 检查tomcat状态
     *
     * @param tomcatInfoModel tomcat信息
     * @param cmd             操作命令
     * @return 状态结果
     */
    protected String getStatus(TomcatInfoModel tomcatInfoModel, String cmd) {
        String strReturn = "start".equals(cmd) ? "stopped" : "started";
        int i = 0;
        while (i < 10) {
            int result = 0;
            String url = String.format("http://127.0.0.1:%d/", tomcatInfoModel.getPort());
            HttpRequest httpRequest = new HttpRequest(url);
            // 设置超时时间为3秒
            httpRequest.setConnectionTimeout(3000);
            try {
                httpRequest.execute();
                result = 1;
            } catch (Exception ignored) {
            }

            i++;
            if ("start".equals(cmd) && result == 1) {
                strReturn = "started";
                break;
            }
            if ("stop".equals(cmd) && result == 0) {
                strReturn = "stopped";
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
        return strReturn;
    }

    protected void exec(String command, boolean close) {
        DefaultSystemLog.getLog().info(command);
        try {
            // 执行命令
            Process process = Runtime.getRuntime().exec(command);
            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
            process.waitFor(5, TimeUnit.SECONDS);
            if (close) {
                process.destroy();
            }
        } catch (IOException | InterruptedException e) {
            DefaultSystemLog.getLog().error("tomcat执行名称失败", e);
        }
    }

    //判断是否为数字型字符串
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
