package com.baidu.zhihu.control;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.baidu.zhihu.model.User;
import com.baidu.zhihu.service.HotSearchService;
import com.baidu.zhihu.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping(path = "zhihu")
public class UserPageControl {

    @Autowired
    private UserService userService;

    @Autowired
    private HotSearchService hotSearchService;

    @GetMapping("userpage")
    public String userPage(@RequestParam String type, Model model) {
        type = "user";
        model.addAttribute("hotSearches",
                hotSearchService.getHotSearches());
        model.addAttribute("infoes", getInfo());
        return "UserPage.html";
    }

    @GetMapping("/userpage_logsuccess")
    public String userpageLoginSuccess(@RequestParam String type, Model model, @RequestParam String userId) {
        type = "user";
        User user = userService.get(userId);
        model.addAttribute("user", user);
        model.addAttribute("hotSearches",
                hotSearchService.getHotSearches());
        model.addAttribute("infoes", getInfo());
        return "UserPage_LogSuccess.html";
    }

    @SuppressWarnings("rawtypes")
    public List getInfo() {
        List<String> infoes = new ArrayList<>();
        infoes.add("刘看山知乎指南知乎协议知乎隐私保护指引");
        infoes.add("应用工作");
        infoes.add("侵权举报网上有害信息举报专区");
        infoes.add("京ICP证110745号");
        infoes.add("京ICP备13052560号-1");
        infoes.add("京公网安备11010802020088 号");
        infoes.add("互联网药品信息服务资格证书");
        infoes.add("（京）- 非经营性 - 2017 - 0067");
        infoes.add("服务热线：400-919-0001");
        infoes.add("违法和不良信息举报：010-82716601");
        infoes.add("举报邮箱：jubao@zhihu.com");
        infoes.add("儿童色情信息举报专区");
        infoes.add("信息安全漏洞反馈专区");
        infoes.add("内容从业人员违法违规行为举报");
        infoes.add("证照中心Investor Relations");
        infoes.add("联系我们 © 2022 知乎");
        return infoes;
    }
}
