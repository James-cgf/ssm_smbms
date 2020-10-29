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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
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
//        int n=5/0;
        System.out.println("进  入跳转登录页面");
        return "login";
    }

    /**
     * 注销页面
     * redirect重定向，跳转页面不携带任何参数
     * @param session
     * @return
     */
    @RequestMapping("/out.html")
    public String out(HttpSession session) {
        session.removeAttribute(Constants.USER_SESSION);   //清空session
        return "redirect:/user/login.html";
    }

    /**
     * 处理登录页面
     *
     * @return
     */
    @RequestMapping(value = "/login.html", method = RequestMethod.POST)
    public String doLogin(String userCode, String userPassword, HttpSession session, HttpServletRequest request) {
        User user = userService.login(userCode, userPassword);
        if (user != null) {
            session.setAttribute(Constants.USER_SESSION, user);
            return "redirect:/user/frame.html";
        }else{
            request.setAttribute("error","用户名或密码不符");
            return "login";
        }
    }

//    /**
//     * 出现异常统一处理  @ExceptionHandler(value = RuntimeException.class)
//     * 只能铺货UserController类本类异常，不能铺货其他类异常
//     * @param e
//     * @param session
//     * @return
//     */
//    @ExceptionHandler(value = RuntimeException.class)
//    public String exceptionHandler(RuntimeException e,HttpSession session){
//        session.setAttribute("e",e.getMessage());
//        return "error";
//    }


    /**
     * 跳转到frame页面
     * 权限设置，没有登录时不能直接访问
     * @return
     */
    @RequestMapping("/frame.html")

    public String frame(HttpSession session){
       User user= (User) session.getAttribute(Constants.USER_SESSION);
//       int num=5/0;
       if (user==null){
           return "login";
       }
        return "frame";

    }
    @RequestMapping("/userlist.html")
    public String main(Model model, String queryname, @RequestParam(value = "queryUserRole", defaultValue = "0") Integer userRole,
                       @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex) {
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
        System.out.println("userlist长度"+userList.size());
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

    /**
     * 跳转到添加页面
     * @return
     */
    @RequestMapping("/addUser.html")
    public String addUser(){
        return "useradd";
    }

    /**
     * 处理添加功能
     * @return
     */
    @RequestMapping(value = "/addUser.html",method = RequestMethod.POST)
    public String saveUser(User user,HttpSession session){
        //创建时间
        user.setCreationDate(new Date());
        //创建者
        User user_login= (User) session.getAttribute(Constants.USER_SESSION);
        //拿到创建者的id
        user.setCreatedBy(user_login.getId());
        if (userService.add(user)){
            return "redirect:/user/userlist.html";
        }
        return "useradd";
    }

    /**
     * 修改跳转路径
     * @param uid
     * @param model
     * @return
     */
    @RequestMapping(value = "modify.html")
    public String modify(String uid,Model model){
        User user=userService.getUserById(uid);
        model.addAttribute("user",user);
        return "usermodify";
    }

    /**
     * 保存修改方法
     * @param user
     * @param session
     * @return
     */
    @RequestMapping(value = "modify.html",method = RequestMethod.POST)
    public String saveModify(User user,HttpSession session){
        user.setModifyDate(new Date());  //修改时间
        User user_login= (User) session.getAttribute(Constants.USER_SESSION);
        user.setModifyBy(user_login.getId());
        System.out.println("这是user对象"+user);
        if (userService.modify(user)){
            return "redirect:/user/userlist.html";
        }
        return "usermodify";
    }
}
