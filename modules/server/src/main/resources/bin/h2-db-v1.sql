--  创建操作记录表
CREATE TABLE IF NOT EXISTS PUBLIC.USEROPERATELOGV1
(
    REQID     VARCHAR(50) NOT NULL COMMENT '请求ID',
    IP        VARCHAR(30) COMMENT '客户端IP地址',
    USERID    VARCHAR(30) COMMENT '操作的用户ID',
    RESULTMSG VARCHAR(1000000000) COMMENT '操作的结果信息',
    OPTTYPE   INTEGER COMMENT '操作类型',
    OPTSTATUS INTEGER COMMENT '操作状态 成功/失败',
    OPTTIME   BIGINT COMMENT '操作时间',
    NODEID    VARCHAR(30) COMMENT '节点ID',
    DATAID    VARCHAR(50) COMMENT '操作的数据ID',
    USERAGENT VARCHAR(300) COMMENT '浏览器标识',
    REQDATA   VARCHAR(1000000000) COMMENT '用户请求参数',
    CONSTRAINT USEROPERATELOGV1_PK PRIMARY KEY (REQID)
);
COMMENT ON TABLE USEROPERATELOGV1 is '操作日志';

-- 监控异常记录表
CREATE TABLE IF NOT EXISTS PUBLIC.MONITORNOTIFYLOG
(
    LOGID        VARCHAR(50) NOT NULL COMMENT '记录id',
    MONITORID    varchar(50) COMMENT '监控id',
    NODEID       VARCHAR(30) COMMENT '节点id',
    PROJECTID    VARCHAR(30) COMMENT '项目id',
    CREATETIME   BIGINT COMMENT '异常时间',
    TITLE        VARCHAR(100) COMMENT '异常描述',
    CONTENT      VARCHAR(1000000) COMMENT '异常内容',
    STATUS       TINYINT COMMENT '当前状态',
    NOTIFYSTYLE  TINYINT COMMENT '通知方式',
    NOTIFYSTATUS TINYINT COMMENT '通知状态',
    NOTIFYOBJECT varchar(10000) COMMENT '通知对象',
    NOTIFYERROR  varchar(1000000) COMMENT '通知异常内容',
    CONSTRAINT MONITORNOTIFYLOG_PK PRIMARY KEY (LOGID)
);
COMMENT ON TABLE MONITORNOTIFYLOG is '监控异常日志记录';

-- 构建历史
CREATE TABLE IF NOT EXISTS PUBLIC.BUILDHISTORYLOG
(
    ID            VARCHAR(50) not null COMMENT '表id',
    BUILDDATAID   VARCHAR(50) COMMENT '构建的数据id',
    BUILDNUMBERID INTEGER COMMENT '构建编号',
    STATUS        TINYINT COMMENT '构建状态',
    STARTTIME     BIGINT COMMENT '开始时间',
    ENDTIME       BIGINT COMMENT '结束时间',
    RESULTDIRFILE VARCHAR(200) COMMENT '构建产物目录',
    CONSTRAINT BUILDHISTORYLOG_PK PRIMARY KEY (ID)
);
COMMENT ON TABLE BUILDHISTORYLOG is '构建历史记录';
