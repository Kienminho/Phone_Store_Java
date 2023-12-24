package com.tdtu.phone_store_java.APIController;

import com.itextpdf.html2pdf.HtmlConverter;
import com.tdtu.phone_store_java.Common.Response;
import com.tdtu.phone_store_java.Common.Utils;
import com.tdtu.phone_store_java.DTO.*;
import com.tdtu.phone_store_java.Model.*;
import com.tdtu.phone_store_java.Repository.*;
import com.tdtu.phone_store_java.Service.MailService;
import com.tdtu.phone_store_java.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/")
public class APIController {
    private final TemplateEngine templateEngine;
    public APIController(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private InvoiceItemRepository invoiceItemRepository;
    @Autowired
    private MailService mailService;
    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;
    @Value("${pdf.outputDirectory}")
    private String outputDirectory;

    // api user
    @PostMapping(value = "user/login")
    public Object handleLogin(@RequestBody Map<String, String> req) {
        String userName = req.get("username");
        String password = req.get("password");

        //check user exist
        UserDTO existUser = userRepository.getUserByName(userName);
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
            else {
                return Response.createResponseModel(404, "Mật khẩu sai, vui lòng thử lại", 0, false);
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
            Utils.isLogin = true;
            return Response.createSuccessResponseModel(0, Map.of(
                    "urlRedirect", "/home"
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
        u.setFirstLogin(false);
        u.setPassword(hashPassword);
        userRepository.save(u);
        return Response.createSuccessResponseModel(0, true);
    }

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

    @GetMapping("users/info-mine")
    public Object GetInfoMine() {
        UserDTO u = userRepository.getInfoMine(Utils.idUserLogin);
        return Response.createSuccessResponseModel(1,u);
    }

    @PostMapping(value = "users/uploadAvatar", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Object UploadAvatar(@ModelAttribute UserRequestUpload req, @RequestParam("avatar") MultipartFile image) {
        try {
            if(!image.isEmpty()) {
                String avatarLink = saveImage("avatar", image);
                User u = userRepository.getUserById(req.getId());
                u.setAvatar(avatarLink);
                userRepository.save(u);
                return Response.createSuccessResponseModel(0,true);
            }
            return Response.createErrorResponseModel("Vui lòng chọn ảnh.", false);
        }
        catch (Exception ex) {
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex);
        }
    }

    @PostMapping("users/change-password")
    public Object ChangePassword(@RequestBody Map<String, String> req) {
        try {
            Long id = Long.parseLong(req.get("id"));
            String oldPassword = req.get("oldPassword");
            String password = req.get("password");

            //find user
            User u = userRepository.getUserById(id);
            //check password
            boolean isMatch = Utils.verifyPassword(oldPassword, u.getPassword());
            if(!isMatch) return Response.createErrorResponseModel("Mật khẩu hiện tại không chính xác, hãy thử lại.", false);

            //hash new password
            String newPassword = Utils.hashPassword(password);
            u.setPassword(newPassword);
            userRepository.save();
            return Response.createSuccessResponseModel(0, true);
        }
        catch (Exception ex) {
            System.out.println("API-Controller-Line 225: "+ ex);
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex.getMessage());
        }
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

    @GetMapping("products/get-product-by-barcode/{keyword}")
    public Object GetProductByBarCode(@PathVariable String keyword) {
        try {
            List<ProductDTO> list = new ArrayList<>();
            if(keyword.equals("all"))
                list = productRepository.getAllProducts();
            else
                list = productRepository.getProductByKeyword(keyword);
            list.sort(Comparator.comparing(ProductDTO::getId));
            return Response.createSuccessResponseModel(list.size(), list);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex);
        }
    }

    @GetMapping("products/carts")
    public Object GetAllCarts() {
        try {
            List<Cart> list = cartRepository.getAll(Utils.idUserLogin);
            return Response.createSuccessResponseModel(list.size(), list);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex);
        }
    }

    @GetMapping("products/add-to-cart/{barCode}")
    public Object AddToCart(@PathVariable String barCode) {
        try {
            Product p = productRepository.getProductByBarCode(barCode);
            if(p == null)
                return Response.createErrorResponseModel("Không tìm thấy sản phẩm.", false);
            // Check if the product already exists in the cart
            Cart c = cartRepository.getCartItem(p.getId());
            if(c != null) {
                c.setQuantity(c.getQuantity()+1);
                c.setTotalMoney(c.getQuantity() * c.getSalePrice());
                cartRepository.save(c);
            }
            else {
                Cart newCart = new Cart(Utils.idUserLogin, p.getId(), p.getName(), p.getImageLink(), p.getPriceSale(), 1, p.getPriceSale());
                cartRepository.save(newCart);
            }
            return Response.createSuccessResponseModel(0, true);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex);
        }
    }

    @PostMapping("products/update-quantity")
    public Object UpdateQuantity(@RequestBody Map<String, String> req) {
        try {
            Long id = Long.parseLong(req.get("id"));
            int quantity = Integer.parseInt(req.get("value"));
            if(quantity < 1)
                return Response.createErrorResponseModel("Số lượng không thể nhỏ hơn 1.", false);
            Cart c = cartRepository.getCartItem(id);
            c.setQuantity(quantity);
            c.setTotalMoney(quantity * c.getSalePrice());
            cartRepository.save(c);
            return Response.createSuccessResponseModel(0, true);

        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex.getMessage());
        }
    }

    @DeleteMapping("products/delete-product-in-cart/{id}")
    public Object DeleteItemInCart(@PathVariable Long id) {
        try {
            cartRepository.deleteCartById(id);
            return Response.createSuccessResponseModel(0, true);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex.getMessage());
        }
    }

    //api cart
    @GetMapping("carts/get-info-cart")
    public Object GetInFoCart() {
        try {
            List<Cart> list = cartRepository.getCartsByIdSalePeople(Utils.idUserLogin);
            int totalQuantity = 0;
            int totalAmount = 0;
            for (Cart i: list) {
                totalQuantity += i.getQuantity();
                totalAmount += i.getTotalMoney();
            }
            return Response.createSuccessResponseModel(
                    3,
                    Map.of(
                            "totalQuantity", totalQuantity,
                            "totalAmount", totalAmount,
                            "cartItems", list
                    )
            );
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex);
        }
    }

    //api customer
    @PostMapping("customer/get-profile")
    public Object GetInfoCustomer(@RequestBody Map<String, String> req) {
        try {
            String phoneNumber = req.get("phoneNumber");
            Customer c = customerRepository.getCustomerByPhoneNumber(phoneNumber.trim());
            if(c == null)
                return Response.createErrorResponseModel("Khách hàng không tồn tại trong hệ thống", false);
            return Response.createSuccessResponseModel(1, c);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex);
        }
    }

    @PostMapping("customer/get-purchase-history")
    public Object GetPurchaseHistory(@RequestBody Map<String, String> req) {
        try {
            Long id = Long.parseLong(req.get("customerId"));
            Customer c = customerRepository.getReferenceById(id);
            List<InvoiceDTO> data = invoiceRepository.findAllWithCustomerAndSalesStaff(id);
            return Response.createSuccessResponseModel(data.size(), data);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex);
        }
    }

    //api Invoice
    @PostMapping("invoices")
    public Object CreateInvoice(@RequestBody InvoiceRequest req) {
        //tăng sale number
        for (Cart item: req.getItems()) {
            Product p = productRepository.findProductById(item.getIdProduct());
            if(p != null) {
                p.setSaleNumber(p.getSaleNumber() + item.getQuantity());
                productRepository.save(p);
            }
        }
        // kiểm tra customer
        Customer c = customerRepository.getCustomerByPhoneNumber(req.getPhoneNumber());
        if(c == null) {
             c = new Customer(req.getPhoneNumber(),req.getFullName(), req.getAddress());
            customerRepository.save(c);
        }
        String invoiceCode = String.valueOf(Utils.generateSixDigitNumber());
        User u = userRepository.getUserById(Utils.idUserLogin);
        //tạo hoá đơn
        Invoice invoice = new Invoice(invoiceCode, c, u, req.getReceiveMoney(), req.getMoneyBack(), req.getTotalMoney(), req.getQuantity(), new Date());
        invoiceRepository.save(invoice);
        //create invoice item
        for (Cart item: req.getItems()) {
            Product productInvoice = productRepository.findProductById(item.getIdProduct());
            InvoiceItem i = new InvoiceItem(invoice, productInvoice, item.getQuantity(), item.getTotalMoney(), new Date());
            invoiceItemRepository.save(i);
        }
        //xoá cart
        cartRepository.deleteCartBySalePeople(Utils.idUserLogin);
        Context thymeleafContext = new Context();
        //add data
        thymeleafContext.setVariable("invoice", invoice.getId());
        thymeleafContext.setVariable("customerName", req.getFullName());
        thymeleafContext.setVariable("salesStaffName", u.getFullName());
        thymeleafContext.setVariable("address", req.getAddress());
        thymeleafContext.setVariable("createdAt", invoice.getCreatedDate());
        thymeleafContext.setVariable("totalPrice", req.getTotalMoney());
        thymeleafContext.setVariable("totalProducts", req.getQuantity());
        thymeleafContext.setVariable("receiveMoney", req.getReceiveMoney());
        thymeleafContext.setVariable("excessMoney", req.getMoneyBack());
        thymeleafContext.setVariable("products", req.getItems());
        String processedHtml = templateEngine.process("invoice", thymeleafContext);
        String outputPath = outputDirectory + File.separator + invoice.getInvoiceCode()+".pdf";
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputPath)) {
            //fileOutputStream.write(processedHtml.getBytes(StandardCharsets.UTF_8));
            HtmlConverter.convertToPdf(processedHtml, fileOutputStream);
            return Response.createSuccessResponseModel(
                    0,
                    Map.of(
                            "downloadLink", outputPath,
                            "urlRedirect", "/pay-success/"+ invoice.getInvoiceCode()
                    )
            );
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex);
        }
    }

    @GetMapping("invoices/get-detail/{id}")
    public Object GetDetailInvoice(@PathVariable String id) {
        try {
            List<InvoiceItemDTO> data = invoiceItemRepository.getDetailInvoice(id);
            return Response.createSuccessResponseModel(data.size(), data);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex);
        }
    }

    //api statistical
    @GetMapping("statistical/get-data")
    public Object GetDataStatistical() {
        try {
            List<Invoice> listInvoice = invoiceRepository.findAll();
            List<User> listUser = userRepository.getAllUser();
            int totalMoney = 0;
            int totalQuantity = 0;
            for (Invoice i: listInvoice) {
                totalMoney += i.getTotalMoney();
                totalQuantity += i.getQuantity();
            }
            return Response.createSuccessResponseModel(1, Map.of(
                    "money", totalMoney,
                    "quantity", totalQuantity,
                    "invoiceNumber", listInvoice.size(),
                    "userNumber", listUser.size()
            ));
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex);
        }
    }

    @PostMapping("statistical/get-data-by-date")
    public Object GetDataByDate(@RequestBody Map<String, Date> req) {
        try {
            Date fromDate = req.get("fromDate");
            Date toDate = req.get("toDate");
            List<InvoiceDTO> data = invoiceRepository.findDateByDate(fromDate, toDate);
            return Response.createSuccessResponseModel(data.size(), data);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Response.createErrorResponseModel("Vui lòng thử lại.", ex);
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
