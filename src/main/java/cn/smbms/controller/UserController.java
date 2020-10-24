package cn.smbms.controller;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.role.RoleServiceImpl;
import cn.smbms.service.user.UserService;
import cn.smbms.service.user.UserServiceImpl;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    RoleService roleService;

    /**
     * 跳转登录页面
     * /login.html  ;伪静态命名
     *
     * @return
     */
    @RequestMapping("/login.html")
    public String login() {
        System.out.println("进入跳转登录页面");
        return "login";
    }

    /**
     * 处理登录页面
     *
     * @return
     */
    @RequestMapping(value = "/login.html", method = RequestMethod.POST)
    public String doLogin(String userCode, String userPassword, Model model) {
        System.out.println("进入处理登录页面");
        User user = userService.login(userCode, userPassword);
        if (user != null) {
            model.addAttribute(Constants.USER_SESSION, user);
            return "frame";
        }
        return "login";
    }

    @RequestMapping("/userlist.html")
    public String main(Model model, String queryname, @RequestParam(value = "queryname", defaultValue = "0") Integer userRole,
                       @RequestParam(value = "queryname", defaultValue = "1") Integer pageIndex) {
        //查询用户列表
        List<User> userList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        System.out.println("queryUserName servlet--------" + queryname);
        System.out.println("queryUserRole servlet--------" + userRole);
        System.out.println("query pageIndex--------- > " + pageIndex);

        //总数量（表）
        int totalCount = userService.getUserCount(queryname, userRole);
        //总页数
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(pageIndex);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);

        int totalPageCount = pages.getTotalPageCount();

        //控制首页和尾页
        if (pageIndex < 1) {
            pageIndex = 1;
        } else if (pageIndex > totalPageCount) {
            pageIndex = totalPageCount;
        }
        userList = userService.getUserList(queryname, userRole, pageIndex, pageSize);
        model.addAttribute("userList", userList);
        List<Role> roleList = null;
        roleList = roleService.getRoleList();
        model.addAttribute("roleList", roleList);
        model.addAttribute("queryUserName", queryname);
        model.addAttribute("queryUserRole", userRole);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPageNo", pageIndex);
        return "userlist";
    }
}
