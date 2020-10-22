package cn.smbms.controller;

import cn.smbms.pojo.User;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.annotation.Resource;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 跳转登录页面
     * /login.html  ;伪静态命名
     * @return
     */
    @RequestMapping("/login.html")
    public String login() {
        System.out.println("进入跳转登录页面");
        return "login";
    }

    /**
     * 处理登录页面
     * @return
     */
    @RequestMapping(value = "/login.html",method = RequestMethod.POST)
    public String doLogin(String userCode, String userPassword, Model model) {
        System.out.println("进入处理登录页面");
        User user=userService.login(userCode,userPassword);
        if (user!=null){
            model.addAttribute(Constants.USER_SESSION,user);
            return "frame";
        }
        return "login";
    }
}
