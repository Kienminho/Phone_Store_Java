package com.tdtu.phone_store_java.APIController;

import com.tdtu.phone_store_java.Common.Response;
import com.tdtu.phone_store_java.Common.Utils;
import com.tdtu.phone_store_java.Model.Role;
import com.tdtu.phone_store_java.Model.User;
import com.tdtu.phone_store_java.Repository.RoleRepository;
import com.tdtu.phone_store_java.Repository.UserRepository;
import com.tdtu.phone_store_java.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/")
public class APIController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MailService mailService;

    @PostMapping(value = "user/login")
    public Object handleLogin(@RequestBody Map<String, String> req) {
        String userName = req.get("username");
        String password = req.get("password");

        //check user exist
        User existUser = userRepository.getUserByName(userName);
        if(existUser == null) {
            return Response.createErrorResponseModel("Nhân viên chưa đăng ký hoặc chưa được kích hoạt, vui lòng thông báo với admin", false);
        }

        //check first login
        if(existUser.getFirstLogin()) {
            if(password.equals(existUser.getPassword())) {
                return Response.createResponseModel(
                        304,
                        "Lần đầu tiên đăng nhập, vui lòng đổi mật khẩu để tiếp tục truy cập hệ thống",0,
                        Map.of(
                                "urlRedirect", "/auth/change_password/" + existUser.getId(),
                                "token", existUser.getActivationToken()
                        )
                );
            }
        }

        // so sánh mật khẩu
        boolean match = Utils.verifyPassword(password, existUser.getPassword());
        if(match) {
            if (existUser.getIsDeleted()) {
                return Response.createErrorResponseModel("Tài khoản đã bị khoá, vui lòng liên hệ admin.", false);
            }
            Utils.userNameLogin = existUser.getUserName();
            Utils.idUserLogin = existUser.getId();
            return Response.createSuccessResponseModel(0, true);
        }
        return Response.createResponseModel(404,"Mật khẩu sai, vui lòng thử lại", 0, false);
    }

    //handle register
    @PostMapping(value = "user/register", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public Object handleRegister(@RequestParam Map<String, String> req) {
        String fullName = req.get("fullName");
        String email = req.get("email");
        String address = req.get("address");
        String phoneNumber = req.get("phoneNumber");
        User existUser = userRepository.getUserByEmail(email);
        if(existUser != null) {
            return Response.createErrorResponseModel("Nhân viên đã tồn tại", false);
        }

        try {
            Role r = roleRepository.getRoleById(2L);
            User user = new User(
                    Utils.GetUserNameByEmail(email),
                    Utils.GetUserNameByEmail(email),
                    r,
                    fullName,
                    email,
                    address,
                    phoneNumber,
                    null,
                    Utils.GenerateRandomToken(100),
                    new Date()

            );
            userRepository.save(user);
            mailService.sendMail(user.getActivationToken(), user.getEmail(), user.getUserName());
            return Response.createSuccessResponseModel(0, true);
        }
        catch (Exception ex) {
            return Response.createErrorResponseModel("Vui lòng đợi, hệ thống đang gặp vấn đề", false);
        }
    }

    //handle change password
    @PostMapping("user/change_password")
    public Object handleChangePassword(@RequestBody Map<String, String> req) {
        Long id = Long.parseLong(req.get("id"));
        String password = req.get("password");

        //find user
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return Response.createErrorResponseModel("Không tìm thấy dữ liệu", false);
        }

        User u = optionalUser.get();
        String hashPassword = Utils.hashPassword(password);
        u.setPassword(hashPassword);
        userRepository.save(u);
        return Response.createSuccessResponseModel(0,true);
    }

}
