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
    ID                  VARCHAR(50) not null COMMENT '表id',
    BUILDDATAID         VARCHAR(50) COMMENT '构建的数据id',
    BUILDNUMBERID       INTEGER COMMENT '构建编号',
    STATUS              TINYINT COMMENT '构建状态',
    STARTTIME           BIGINT COMMENT '开始时间',
    ENDTIME             BIGINT COMMENT '结束时间',
    RESULTDIRFILE       VARCHAR(200) COMMENT '构建产物目录',
    BUILDUSER           VARCHAR(50) COMMENT '构建人',
    RELEASEMETHOD       TINYINT COMMENT '发布方式',
    RELEASEMETHODDATAID VARCHAR(200) COMMENT '发布的数据id',
    AFTEROPT            TINYINT COMMENT '发布后操作',
    CONSTRAINT BUILDHISTORYLOG_PK PRIMARY KEY (ID)
);
COMMENT ON TABLE BUILDHISTORYLOG is '构建历史记录';

ALTER TABLE BUILDHISTORYLOG
    ADD IF NOT EXISTS CLEAROLD TINYINT COMMENT '是否清空发布';
ALTER TABLE BUILDHISTORYLOG
    ADD IF NOT EXISTS NAME VARCHAR(100) COMMENT '构建名称';
ALTER TABLE BUILDHISTORYLOG
    ADD IF NOT EXISTS NAME VARCHAR(100) COMMENT '构建名称';
ALTER TABLE BUILDHISTORYLOG
    ADD IF NOT EXISTS RELEASECOMMAND VARCHAR(100) COMMENT '发布命令';
ALTER TABLE BUILDHISTORYLOG
    ADD IF NOT EXISTS RELEASEPATH VARCHAR(100) COMMENT '发布到的目录';

-- 分发日志
CREATE TABLE IF NOT EXISTS PUBLIC.OUTGIVINGLOG
(
    ID          VARCHAR(50) not null comment 'id',
    OUTGIVINGID VARCHAR(50) comment '分发id',
    STATUS      TINYINT comment '状态',
    STARTTIME   BIGINT comment '开始时间',
    ENDTIME     BIGINT comment '结束时间',
    RESULT      VARCHAR(100000) comment '消息',
    NODEID      VARCHAR(100) comment '节点id',
    PROJECTID   VARCHAR(100) comment '项目id',
    CONSTRAINT OUTGIVINGLOG_PK PRIMARY KEY (ID)
);
comment on table OUTGIVINGLOG is '分发日志';

-- 系统监控记录
CREATE TABLE IF NOT EXISTS PUBLIC.SYSTEMMONITORLOG
(
    ID           VARCHAR(50) not null comment 'id',
    NODEID       VARCHAR(100) comment '节点id',
    MONITORTIME  BIGINT comment '监控时间',
    OCCUPYCPU    DOUBLE comment '占用cpu',
    OCCUPYMEMORY DOUBLE comment '占用内存',
    OCCUPYDISK   DOUBLE comment '占用磁盘',
    CONSTRAINT SYSTEMMONITORLOG_PK PRIMARY KEY (ID)
);
comment on table SYSTEMMONITORLOG is '系统监控记录';

ALTER TABLE SYSTEMMONITORLOG
    ALTER COLUMN ID VARCHAR(50);

ALTER TABLE SYSTEMMONITORLOG
    ALTER COLUMN MONITORTIME BIGINT;

ALTER TABLE BUILDHISTORYLOG
    ALTER COLUMN RELEASECOMMAND VARCHAR(5000) COMMENT '发布命令';

CREATE UNIQUE INDEX IF NOT EXISTS SYSTEMMONITORLOG_INDEX1 ON PUBLIC.SYSTEMMONITORLOG (NODEID, MONITORTIME);
