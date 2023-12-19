package com.tdtu.phone_store_java.APIController;

import com.tdtu.phone_store_java.Common.Response;
import com.tdtu.phone_store_java.Common.Utils;
import com.tdtu.phone_store_java.DTO.ProductDTO;
import com.tdtu.phone_store_java.Model.Product;
import com.tdtu.phone_store_java.Model.Role;
import com.tdtu.phone_store_java.Model.User;
import com.tdtu.phone_store_java.Repository.ProductRepository;
import com.tdtu.phone_store_java.Repository.RoleRepository;
import com.tdtu.phone_store_java.Repository.UserRepository;
import com.tdtu.phone_store_java.Service.MailService;
import com.tdtu.phone_store_java.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/")
public class APIController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private MailService mailService;
    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    // api user
    @PostMapping(value = "user/login")
    public Object handleLogin(@RequestBody Map<String, String> req) {
        String userName = req.get("username");
        String password = req.get("password");

        //check user exist
        User existUser = userRepository.getUserByName(userName);
        if (existUser == null) {
            return Response.createErrorResponseModel("Nhân viên chưa đăng ký , vui lòng liên hệ với admin", false);
        }
        if(!existUser.getIsActivated() || existUser.getIsDeleted()) {
            return Response.createErrorResponseModel("Tài khoản chưa kích hoạt hoặc đã bị khoá , vui lòng liên hệ với admin", false);
        }

        //check first login
        if (existUser.getFirstLogin()) {
            if (password.equals(existUser.getPassword())) {
                return Response.createResponseModel(
                        304,
                        "Lần đầu tiên đăng nhập, vui lòng đổi mật khẩu để tiếp tục truy cập hệ thống", 0,
                        Map.of(
                                "urlRedirect", "/auth/change_password/" + existUser.getId(),
                                "token", existUser.getActivationToken()
                        )
                );
            }
        }

        // so sánh mật khẩu
        boolean match = Utils.verifyPassword(password, existUser.getPassword());
        if (match) {
            if (existUser.getIsDeleted()) {
                return Response.createErrorResponseModel("Tài khoản đã bị khoá, vui lòng liên hệ admin.", false);
            }
            Utils.userNameLogin = existUser.getUserName();
            Utils.idUserLogin = existUser.getId();
            return Response.createSuccessResponseModel(0, Map.of(
                    "urlRedirect", "/"
            ));
        }
        return Response.createResponseModel(404, "Mật khẩu sai, vui lòng thử lại", 0, false);
    }

    //handle register
    @PostMapping(value = "user/register")
    public Object handleRegister(@RequestBody Map<String, String> req) {
        String fullName = req.get("fullName");
        String email = req.get("email");
        String address = req.get("address");
        String phoneNumber = req.get("phoneNumber");
        User existUser = userRepository.getUserByEmail(email);
        if (existUser != null) {
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
        } catch (Exception ex) {
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
        return Response.createSuccessResponseModel(0, true);
    }

    // api products
    @GetMapping("products/get-all-products")
    public Object GetAllProducts() {
        try {
            List<ProductDTO> list = productRepository.getAllProducts();
            list.sort(Comparator.comparing(ProductDTO::getId));
            return Response.createSuccessResponseModel(list.size(), list);
        } catch (Exception ex) {
            return Response.createErrorResponseModel(ex.getMessage(), ex);
        }
    }

    @PostMapping(value = "products/add-product", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Object AddProduct(@ModelAttribute ProductDTO req, @RequestParam("productImage") MultipartFile image) {
        try {
            String name = req.getName().trim();
            if (!image.isEmpty()) {

                String imageLink = saveImage(name, image);
                Product newProduct = productService.addProduct(req, imageLink);
                if (newProduct != null)
                    return Response.createSuccessResponseModel(1, newProduct);
            }
            return Response.createErrorResponseModel("Vui lòng thử lại.", false);
        } catch (Exception ex) {
            return Response.createErrorResponseModel(ex.getMessage(), ex);
        }
    }

    @DeleteMapping("products/delete/{id}")
    public Object DeleteProduct(@PathVariable String id) {
        try {
            Product p = productRepository.getProductByBarCode(id);
            if (p.getSaleNumber() > 0) {
                return Response.createResponseModel(400, "Sản phẩm đã bán ra, không thể xoá.", 0, false);
            }
            p.setDeleted(true);
            productRepository.save(p);
            return Response.createSuccessResponseModel(0, true);
        } catch (Exception ex) {
            return Response.createErrorResponseModel(ex.getMessage(), ex);
        }
    }

    @PutMapping(value = "products/update-product", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Object UpdateProduct(@ModelAttribute ProductDTO req, @RequestParam("productImageUpdate") MultipartFile image) throws IOException {
        try {
            Product p = productRepository.getProductByBarCode(req.getBarCode());
            p.setName(req.getName());
            p.setImportPrice(req.getImportPrice());
            p.setPriceSale(req.getPriceSale());
            p.setRam(req.getRam().trim() + " GB");
            p.setRom(req.getRom().trim() + " GB");
            p.setSaleNumber(req.getSaleNumber());
            p.setUpdatedDate(new Date());
            if (!image.isEmpty()) {
                String name = req.getName().trim();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String formattedDate = dateFormat.format(new Date());
                if (!image.isEmpty()) {
                    String imagePath = saveImage(name, image);
                    p.setImageLink(imagePath);
                }
            }
            productRepository.save(p);
            return Response.createSuccessResponseModel(0, true);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex);
        }

    }

    //api user
    @GetMapping("users/get-all-user")
    public Object GetAllUser() {
        List<User> list = userRepository.getAllUser();
        return Response.createSuccessResponseModel(list.size(), list);
    }

    @DeleteMapping("users/delete/{id}")
    public Object BlockUser(@PathVariable String id) {
        try {
            Long idUser = Long.parseLong(id);
            User u = userRepository.getUserById(idUser);
            u.setIsDeleted(!u.getIsDeleted());
            userRepository.save(u);
            return Response.createSuccessResponseModel(0, true);
        }
        catch (Exception ex) {
            return Response.createErrorResponseModel("Vui lòng thử lại", false);
        }
    }
    private String saveImage(String name, MultipartFile image) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = dateFormat.format(new Date());
        String imageName = name + '_' + formattedDate + ".jpg";
        String imagePath = uploadPath + imageName;
        File f = new File(uploadPath);
        if (!f.exists()) {
            f.mkdir();
        }
        // Save image to the specified path
        Files.copy(image.getInputStream(), Paths.get(imagePath));
        return "/images/" + imageName;
    }
}
