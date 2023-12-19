package com.tdtu.phone_store_java.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class HomeController {
    @GetMapping("/")
    public String RenderLogin() {
        return "index";
    }
    @GetMapping("/product-manager")
    public String RenderProductManager() {
        return "productManager";
    }
    @GetMapping("/employee-manager")
    public String RenderEmployeeManager() {
        return "employeeManager";
    }
}
