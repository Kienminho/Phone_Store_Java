package com.tdtu.phone_store_java.Controller;

import com.tdtu.phone_store_java.Common.Utils;
import com.tdtu.phone_store_java.Model.User;
import com.tdtu.phone_store_java.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth/")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("login")
    public String renderLogin() {
        return "login";
    }

    @GetMapping("logout")
    public String handelLogout() {
        Utils.isLogin = false;
        Utils.idUserLogin = 0L;
        Utils.userNameLogin = "";
        return "redirect:/auth/login";
    }
    //active
    @GetMapping("active/{token}")
    public String handleActive(@PathVariable String token, Model model) {

        User user = userRepository.getUserByToken(token);
        if(user == null) {
            model.addAttribute("message", "Invalid activation token!");
            model.addAttribute("isSuccess", false);
        }
        long currentTime = System.currentTimeMillis();
        long activationExpiresTime = user.getActivationExpires().getTime();
        long differenceInMinutes = (currentTime - activationExpiresTime) / (1000 * 60);
        if(differenceInMinutes <=1) {
            user.setIsActivated(true);
            userRepository.save(user);
            model.addAttribute("message", "Kích hoạt thành công, bạn sẽ được chuyển hướng sau vài giây.");
            model.addAttribute("isSuccess", true);
        }
        else {
            model.addAttribute("message", "Hết thời gian kích hoạt, vui lòng liên hệ với admin để cấp lại.");
            model.addAttribute("isSuccess", false);
        }
        return "active";
    }
    @GetMapping("change_password/{id}")
    public String renderChangePassword(@PathVariable String id) {
        return "changePassword";
    }
}
