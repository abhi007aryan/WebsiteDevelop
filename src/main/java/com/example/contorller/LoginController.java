package com.example.contorller;

import com.example.service.UserService;
import com.example.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/register"})
    @ResponseBody
    public String register (ModelAndView modelAndView, @RequestParam("username") String username,
                            @RequestParam("password") String password,
                            @RequestParam(value = "rember",defaultValue = "0") int rememberme,
                            HttpServletResponse response){

        try{
            logger.info("username="+username+"  pw= "+password);
            Map<String,Object> map = userService.Register(username,password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme > 0)
                    cookie.setMaxAge(3600*24*5);
                response.addCookie(cookie);

                return ToutiaoUtil.getJSONString(0,"注册成功");
            }
            else
                return ToutiaoUtil.getJSONString(1,map);
        } catch (Exception e){
            logger.error("注册异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1,"注册异常");
        }


    }

    @RequestMapping(path = {"/login"})
    @ResponseBody
    public String login (ModelAndView modelAndView, @RequestParam("username") String username,
                            @RequestParam("password") String password,
                            @RequestParam(value = "rember",defaultValue = "0") int rememberme,
                         HttpServletResponse response){

        try{
            Map<String,Object> map = userService.login(username,password);
            if(map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme>0)
                    cookie.setMaxAge(3600*24*5);
                response.addCookie(cookie);
                return ToutiaoUtil.getJSONString(0, "登陆成功");
            }
            else
                return ToutiaoUtil.getJSONString(1,map);
        } catch (Exception e){
            logger.error("登陆异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1,"登陆异常");
        }


    }
    @RequestMapping(path = "logout")
    @ResponseBody
    public String logout(@CookieValue("ticket") String ticket
                               ){

            userService.logout(ticket);
            return  "redirect:/";
    }
}
