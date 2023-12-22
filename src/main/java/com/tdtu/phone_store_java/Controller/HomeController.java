package com.tdtu.phone_store_java.Controller;

import com.tdtu.phone_store_java.Common.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller

public class HomeController {
    @GetMapping("/")
    public String RenderLogin() {
        return "redirect:/auth/login";
    }
    @GetMapping("/home")
    public String RenderHome() {
        if (!Utils.isLogin)
            return "redirect:/auth/login";
        return "index";
    }
    @GetMapping("/product-manager")
    public String RenderProductManager() {
        if (!Utils.isLogin)
            return "redirect:/auth/login";
        return "productManager";
    }
    @GetMapping("/employee-manager")
    public String RenderEmployeeManager() {
        if (!Utils.isLogin)
            return "redirect:/auth/login";
        return "employeeManager";
    }
    @GetMapping("/payment")
    public String RenderPayment() {
        if (!Utils.isLogin)
            return "redirect:/auth/login";
        return "payment";
    }
    @GetMapping("/checkout")
    public String RenderCheckout() {
        if (!Utils.isLogin)
            return "redirect:/auth/login";
        return "checkout";
    }
    @GetMapping("/my-profile")
    public String RenderInfo() {
        if (!Utils.isLogin)
            return "redirect:/auth/login";
        return "accountSetting";
    }
    @GetMapping("/pay-success/{id}")
    public String RenderPaymentSuccess(@PathVariable String id, Model model) {
        String downloadLink = "http://localhost:8080/"+ id+".pdf";
        model.addAttribute("downloadLink", downloadLink);
        model.addAttribute("id", id);
        return "successPay";
    }
}
