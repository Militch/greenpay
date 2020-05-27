package com.esiran.greenpay.admin.controller.user;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.esiran.greenpay.admin.controller.CURDBaseController;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.common.util.TOTPUtil;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.system.entity.User;
import com.esiran.greenpay.system.entity.vo.UserInputDTO;
import com.esiran.greenpay.system.entity.vo.UserInputVo;
import com.esiran.greenpay.system.service.IUserService;
import com.google.common.io.BaseEncoding;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/user")
public class AdminUserController extends CURDBaseController {
    private final IUserService userService;

    public AdminUserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @PageViewHandleError
    public String profile(ModelMap modelMap) {
        User user = theUser();
        modelMap.addAttribute("data", user);
        return "admin/user/profile";
    }

    @PostMapping("/profile")
    public String profilePost(@Valid UserInputDTO inputDto) throws PostResourceException {
        User theUser = theUser();
        userService.updateUser(theUser.getId(), inputDto);
        return redirect("/admin/user/profile");
    }

    @GetMapping("/security")
    @PageViewHandleError
    public String security(HttpServletRequest request, ModelMap modelMap) throws UnsupportedEncodingException {
        User user = theUser();
        User current = userService.getById(user.getId());
        String totpSecretKeySrc = userService.getTOTPSecretKey(user.getId());
        String totpSecretKey = null;
        String gaUri = null;
        if (totpSecretKeySrc != null && !current.getTotpVerified()) {
            totpSecretKey = BaseEncoding.base32().encode(totpSecretKeySrc.getBytes())
                    .replaceAll("=", "");
            gaUri = TOTPUtil.gaUri(current.getUsername(), totpSecretKey, request.getServerName());
        }
        modelMap.put("totpSecretKey",totpSecretKey);
        modelMap.put("gaUri",gaUri);
        modelMap.put("totpVerified",current.getTotpVerified());
        return "admin/user/security";
    }


    @RequestMapping(value = "/security",method = RequestMethod.POST, params = {"action=restPassword"})
    public String security(@Valid UserInputVo userInputVo) throws PostResourceException {
        userService.updateUserPWD(theUser().getId(), userInputVo);
        SecurityUtils.getSubject().logout();
        return redirect("/admin/login");
    }

    @RequestMapping(value = "/security",method = RequestMethod.POST, params = {"action=restTOTPSecretKey"})
    public String restTOTPSecretKey() throws PostResourceException {
        User user = theUser();
        userService.resetTOTPSecretKey(user.getId());
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getTotpVerified,false)
                .eq(User::getId,user.getId());
        this.userService.update(updateWrapper);
        return redirect("/user/security");
    }

    @RequestMapping(value = "/security",method = RequestMethod.POST, params = {"action=checkTOTPCode"})
    public String checkTOTPCode(@RequestParam String totpCode) throws PostResourceException {
        User user = theUser();
        try {
            boolean result = userService.verifyTOTPPass(user.getId(),totpCode);
            if (!result)
                throw new IllegalArgumentException("校验失败");
        }catch (Exception e){
            throw new PostResourceException(e.getMessage());
        }
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getTotpVerified,true)
                .eq(User::getId,user.getId());
        this.userService.update(updateWrapper);
        return redirect("/user/security");
    }
}
