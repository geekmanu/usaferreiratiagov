package cn.keepbx.jpom.model.data;

import cn.keepbx.jpom.model.BaseJsonModel;

/**
 * 监控日志
 *
 * @author bwcx_jzy
 * @date 2019/7/13
 */
public class MonitorNotifyLog extends BaseJsonModel {
    /**
     * 表名
     */
    public static final String TABLE_NAME = MonitorNotifyLog.class.getSimpleName().toUpperCase();
    /**
     *
     */
    private String logId;
    private String nodeId;
    private String projectId;
    /**
     * 异常发生时间
     */
    private Long createTime;
    private String title;
    private String content;
    /**
     * 项目状态状态
     */
    private boolean status;
    /**
     * 通知方式
     */
    private int notifyStyle;
    /**
     * 通知发送状态
     */
    private boolean notifyStatus;
    /**
     * 监控id
     */
    private String monitorId;

    public boolean isNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(boolean notifyStatus) {
        this.notifyStatus = notifyStatus;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public int getNotifyStyle() {
        return notifyStyle;
    }

    public void setNotifyStyle(int notifyStyle) {
        this.notifyStyle = notifyStyle;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
