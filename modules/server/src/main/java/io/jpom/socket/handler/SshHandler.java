/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 码之科技工作室
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.jpom.socket.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.ChannelType;
import cn.hutool.extra.ssh.JschUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.common.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import io.jpom.model.data.SshModel;
import io.jpom.model.data.UserModel;
import io.jpom.service.dblog.SshTerminalExecuteLogService;
import io.jpom.service.node.ssh.SshService;
import io.jpom.socket.BaseHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ssh 处理2
 *
 * @author bwcx_jzy
 * @date 2019/8/9
 */
public class SshHandler extends BaseHandler {

	private static final ConcurrentHashMap<String, HandlerItem> HANDLER_ITEM_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
	private SshTerminalExecuteLogService sshTerminalExecuteLogService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		SshModel sshItem = (SshModel) session.getAttributes().get("sshItem");
		//Map<String, String[]> parameterMap = (Map<String, String[]>) session.getAttributes().get("parameterMap");
//		String[] fileDirAlls;
//		//判断url是何操作请求
//		if (parameterMap.containsKey("tail")) {
//			fileDirAlls = parameterMap.get("tail");
//		} else if (parameterMap.containsKey("gz")) {
//			fileDirAlls = parameterMap.get("gz");
//		} else {
//			fileDirAlls = parameterMap.get("zip");
//		}
//		//检查文件路径
//		String fileDirAll = null;
//		if (fileDirAlls != null && fileDirAlls.length > 0 && !StrUtil.isEmptyOrUndefined(fileDirAlls[0])) {
//			fileDirAll = fileDirAlls[0];
//			List<String> fileDirs = sshItem.getFileDirs();
//			if (fileDirs == null) {
//				sendBinary(session, "没有配置路径");
//				return;
//			}
//			File file = FileUtil.file(fileDirAll);
//			boolean find = false;
//			for (String fileDir : fileDirs) {
//				if (FileUtil.isSub(FileUtil.file(fileDir), file)) {
//					find = true;
//					break;
//				}
//			}
//			if (!find) {
//				sendBinary(session, "非法路径");
//				return;
//			}
//		}
		//
		HandlerItem handlerItem;
		try {
			handlerItem = new HandlerItem(session, sshItem);
			handlerItem.startRead();
		} catch (Exception e) {
			// 输出超时日志 @author jzy
			DefaultSystemLog.getLog().error("ssh 控制台连接超时", e);
			sendBinary(session, "ssh 控制台连接超时");
			this.destroy(session);
			return;
		}
		HANDLER_ITEM_CONCURRENT_HASH_MAP.put(session.getId(), handlerItem);
		//
		Thread.sleep(1000);
//		//截取当前操作文件父路径
//		String fileLocalPath = null;
//		if (fileDirAll != null && fileDirAll.lastIndexOf("/") > -1) {
//			fileLocalPath = fileDirAll.substring(0, fileDirAll.lastIndexOf("/"));
//		}
//		if (fileDirAll == null) {
//			this.call(session, StrUtil.CR);
//		} else if (parameterMap.containsKey("tail")) {
//			// 查看文件
//			fileDirAll = FileUtil.normalize(fileDirAll);
//			this.call(session, StrUtil.format("tail -f {}", fileDirAll));
//			this.call(session, StrUtil.CR);
//		} else if (parameterMap.containsKey("zip")) {
//			//解压zip
//			fileDirAll = FileUtil.normalize(fileDirAll);
//			this.call(session, StrUtil.format("unzip -o {} -d " + "{}", fileDirAll, fileLocalPath));
//			this.call(session, StrUtil.CR);
//		} else {
//			//解压 tar和tar.gz
//			fileDirAll = FileUtil.normalize(fileDirAll);
//			this.call(session, StrUtil.format("tar -xzvf {} -C " + "{}", fileDirAll, fileLocalPath));
//			this.call(session, StrUtil.CR);
//		}
	}

//	private void call(WebSocketSession session, String msg) throws Exception {
//		JSONObject first = new JSONObject();
//		first.put("data", msg);
//		// 触发消息
//		//this.handleTextMessage(session, new TextMessage(first.toJSONString()));
//	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		HandlerItem handlerItem = HANDLER_ITEM_CONCURRENT_HASH_MAP.get(session.getId());
		if (handlerItem == null) {
			sendBinary(session, "已经离线啦");
			IoUtil.close(session);
			return;
		}
		String payload = message.getPayload();
		if (JSONValidator.from(payload).getType() == JSONValidator.Type.Object) {
			JSONObject jsonObject = JSONObject.parseObject(payload);
			String data = jsonObject.getString("data");
			if (StrUtil.equals(data, "jpom-heart")) {
				// 心跳消息不转发
				return;
			}
		}
		try {
			this.sendCommand(handlerItem, payload);
		} catch (Exception e) {
			sendBinary(session, "Failure:" + e.getMessage());
			DefaultSystemLog.getLog().error("执行命令异常", e);
		}
	}

	private void sendCommand(HandlerItem handlerItem, String data) throws Exception {
		if (handlerItem.checkInput(data)) {
			handlerItem.outputStream.write(data.getBytes());
		} else {
			handlerItem.outputStream.write("没有执行相关命令权限".getBytes());
			handlerItem.outputStream.flush();
			handlerItem.outputStream.write(new byte[]{3});
		}
		handlerItem.outputStream.flush();
	}

	/**
	 * 记录终端执行记录
	 *
	 * @param session 回话
	 * @param command 命令行
	 * @param refuse  是否拒绝
	 */
	private void logCommands(WebSocketSession session, String command, boolean refuse) {
		if (sshTerminalExecuteLogService == null) {
			sshTerminalExecuteLogService = SpringUtil.getBean(SshTerminalExecuteLogService.class);
		}
		List<String> split = StrUtil.split(command, StrUtil.CR);
		// 最后一个是否为回车
		boolean all = StrUtil.endWith(command, StrUtil.CR);
		int size = split.size();
		split = CollUtil.sub(split, 0, all ? size : size - 1);
		if (CollUtil.isEmpty(split)) {
			return;
		}
		// 获取基础信息
		Map<String, Object> attributes = session.getAttributes();
		UserModel userInfo = (UserModel) attributes.get("userInfo");
		String ip = (String) attributes.get("ip");
		String userAgent = (String) attributes.get(HttpHeaders.USER_AGENT);
		SshModel sshItem = (SshModel) attributes.get("sshItem");
		//
		sshTerminalExecuteLogService.batch(userInfo, sshItem, ip, userAgent, refuse, split);
	}

	private class HandlerItem implements Runnable {
		private final WebSocketSession session;
		private final InputStream inputStream;
		private final OutputStream outputStream;
		private final Session openSession;
		private final Channel channel;
		private final SshModel sshItem;
		private final StringBuilder nowLineInput = new StringBuilder();

		HandlerItem(WebSocketSession session, SshModel sshItem) throws IOException {
			this.session = session;
			this.sshItem = sshItem;
			this.openSession = SshService.getSession(sshItem);
			this.channel = JschUtil.createChannel(openSession, ChannelType.SHELL);
			this.inputStream = channel.getInputStream();
			this.outputStream = channel.getOutputStream();
		}

		void startRead() throws JSchException {
			this.channel.connect();
			ThreadUtil.execute(this);
		}

		/**
		 * 添加到命令队列
		 *
		 * @param msg 输入
		 * @return 当前待确认待所有命令
		 */
		private String append(String msg) {
			char[] x = msg.toCharArray();
			if (x.length == 1 && x[0] == 127) {
				// 退格键
				int length = nowLineInput.length();
				if (length > 0) {
					nowLineInput.delete(length - 1, length);
				}
			} else {
				nowLineInput.append(msg);
			}
			return nowLineInput.toString();
		}

		public boolean checkInput(String msg) {
			String allCommand = this.append(msg);
			boolean refuse;
			if (StrUtil.equalsAny(msg, StrUtil.CR, StrUtil.TAB)) {
				String join = nowLineInput.toString();
				if (StrUtil.equals(msg, StrUtil.CR)) {
					nowLineInput.setLength(0);
				}
				refuse = SshModel.checkInputItem(sshItem, join);
			} else {
				// 复制输出
				refuse = SshModel.checkInputItem(sshItem, msg);
			}
			// 执行命令行记录
			logCommands(session, allCommand, refuse);
			return refuse;
		}


		@Override
		public void run() {
			try {
				byte[] buffer = new byte[1024];
				int i;
				//如果没有数据来，线程会一直阻塞在这个地方等待数据。
				while ((i = inputStream.read(buffer)) != -1) {
					sendBinary(session, new String(Arrays.copyOfRange(buffer, 0, i), sshItem.getCharsetT()));
				}
			} catch (Exception e) {
				if (!this.openSession.isConnected()) {
					return;
				}
				DefaultSystemLog.getLog().error("读取错误", e);
				SshHandler.this.destroy(this.session);
			}
		}
	}

	@Override
	public void destroy(WebSocketSession session) {
		HandlerItem handlerItem = HANDLER_ITEM_CONCURRENT_HASH_MAP.get(session.getId());
		if (handlerItem != null) {
			IoUtil.close(handlerItem.inputStream);
			IoUtil.close(handlerItem.outputStream);
			JschUtil.close(handlerItem.channel);
			JschUtil.close(handlerItem.openSession);
		}
		IoUtil.close(session);
		HANDLER_ITEM_CONCURRENT_HASH_MAP.remove(session.getId());
	}

	private static void sendBinary(WebSocketSession session, String msg) {
		if (!session.isOpen()) {
			// 会话关闭不能发送消息 @author jzy 21-08-04
			DefaultSystemLog.getLog().warn("回话已经关闭啦，不能发送消息：{}", msg);
			return;
		}
		synchronized (session.getId()) {
			BinaryMessage byteBuffer = new BinaryMessage(msg.getBytes());
			try {
				session.sendMessage(byteBuffer);
			} catch (IOException e) {
				DefaultSystemLog.getLog().error("发送消息失败:" + msg, e);
			}
		}
	}
}
