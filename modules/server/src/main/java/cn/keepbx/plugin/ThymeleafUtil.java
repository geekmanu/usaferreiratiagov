package cn.keepbx.plugin;

import cn.hutool.core.io.FileUtil;
import cn.jiangzeyin.common.spring.SpringUtil;
import cn.keepbx.jpom.common.BaseServerController;
import cn.keepbx.jpom.common.interceptor.LoginInterceptor;
import cn.keepbx.jpom.model.data.UserModel;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bwcx_jzy
 * @date 2019/8/13
 */
@Configuration
public class ThymeleafUtil {

    @Resource
    private SpringTemplateEngine springTemplateEngine;

    /**
     * 模板名称需要在 classpath:templates/plugin 下
     *
     * @param template  模板名称
     * @param variables 变量
     * @return 转换后的
     */
    public static String process(String template, Map<String, Object> variables) {
        Context context = new Context();
        if (variables == null) {
            variables = new HashMap<>(10);
        }
        String normalize = FileUtil.normalize("plugin/" + template);
        // 用户变量
        UserModel userModel = BaseServerController.getUserModel();
        variables.put(LoginInterceptor.SESSION_NAME, userModel);
        context.setVariables(variables);
        ThymeleafUtil thymeleafUtil = SpringUtil.getBean(ThymeleafUtil.class);
        return thymeleafUtil.springTemplateEngine.process(normalize, context);
    }
}
