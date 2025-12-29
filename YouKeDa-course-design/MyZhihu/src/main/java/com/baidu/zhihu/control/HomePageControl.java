package com.baidu.zhihu.control;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.baidu.zhihu.model.Comment;
import com.baidu.zhihu.model.Date;
import com.baidu.zhihu.model.Post;
import com.baidu.zhihu.model.User;
import com.baidu.zhihu.service.CommentSerice;
import com.baidu.zhihu.service.HotSearchService;
import com.baidu.zhihu.service.PostService;
import com.baidu.zhihu.service.UserService;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(path = "/zhihu")
public class HomePageControl {

    @Autowired
    private PostService postService;
    @Autowired
    private CommentSerice commentSerice;
    @Autowired
    private HotSearchService hotSearchService;
    @Autowired
    private UserService userService;

    private static final Logger LOG = LoggerFactory.getLogger(HomePageControl.class);

    @GetMapping("/homepage")
    public String homePage(@RequestParam(required=false) String type, Model model) {
        List<Post> posts = postService.listPosts();
        model.addAttribute("posts", posts);
        model.addAttribute("hotSearches",
                hotSearchService.getHotSearches());
        model.addAttribute("infoes", getInfo());
        return "HomePage.html";
    }

    @GetMapping("/homepage_logsuccess")
    public String homepageLoginSuccess(Model model, @RequestParam String userId, @RequestParam String type) {
        type = "home";
        User user = userService.get(userId);
        model.addAttribute("user", user);
        List<Post> posts = postService.listPosts();
        model.addAttribute("posts", posts);
        model.addAttribute("hotSearches",
                hotSearchService.getHotSearches());
        model.addAttribute("infoes", getInfo());
        return "Homepage_LogSuccess.html";
    }

    @PostMapping("/addFavor")
    public void addPostFavor(@RequestParam String userId,@RequestParam String postId, HttpServletResponse response) throws IOException {
        postService.addFavor(postId);
        if(userId.isBlank()||userId.isEmpty()){
            response.sendRedirect("/zhihu/homepage?type=home");
        }
        else{
            response.sendRedirect("/zhihu/homepage?"+"userId="+userId+"&type=home");
        }
    }

    @PostMapping("/addComment")
    public void addCommentInPage(@RequestParam(required = false) String userId, @RequestParam("postId") String postId,
            @RequestParam String content, HttpServletResponse response)
            throws IOException {
        // 当前用户不存在
        if (userId.isEmpty() || userId.isBlank()) {
            response.sendRedirect("/zhihu/homepage?type=home");
            return;
        }
        // 当前帖子或content不存在
        Post post = postService.get(postId);
        if (post == null || content.isEmpty()) {
            LOG.warn("没有找到对应数据");
            return;
        }

        Comment comment = new Comment();
        comment.setContent(content);

        LocalDateTime now = LocalDateTime.now();
        Date date = new Date();
        date.setYear(now.getYear());
        date.setMonth(now.getMonthValue());
        date.setDay(now.getDayOfMonth());
        date.setHour(now.getHour());
        date.setMinute(now.getMinute());
        comment.setDate(date);

        User user = userService.get(userId);
        comment.setUser(user);
        commentSerice.addComment(post, comment);
        LOG.info("添加成功");
        response.sendRedirect("/zhihu/homepage_logsuccess?userId=" + userId+"&type=home");
        return;
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
