package cn.keepbx.monitor;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.keepbx.jpom.model.data.MonitorModel;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.MediaType;

/**
 * 钉钉工具
 *
 * @author Arno
 */
public class DingTalkUtil implements INotify {

    /**
     * 发送钉钉群自定义机器人消息
     *
     * @param notify  通知对象
     * @param title   描述标签
     * @param context 消息内容
     */
    @Override
    public void send(MonitorModel.Notify notify, String title, String context) {
        JSONObject text = new JSONObject();
        JSONObject param = new JSONObject();
        //消息内容
        text.put("content", title + "\n" + context);
        param.put("msgtype", "text");
        param.put("text", text);
        HttpRequest request = HttpUtil.
                createPost(notify.getValue()).
                contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).
                body(param.toJSONString());
        request.execute();
    }
}
