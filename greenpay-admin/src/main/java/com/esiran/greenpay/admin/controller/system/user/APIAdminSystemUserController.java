package com.esiran.greenpay.admin.controller.system.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.admin.controller.CURDBaseController;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.framework.annotation.PageViewHandleError;
import com.esiran.greenpay.system.entity.Role;
import com.esiran.greenpay.system.entity.User;
import com.esiran.greenpay.system.entity.UserRole;
import com.esiran.greenpay.system.entity.dot.UserDTO;
import com.esiran.greenpay.system.entity.dot.UserInputDto;
import com.esiran.greenpay.system.service.IMenuService;
import com.esiran.greenpay.system.service.IRoleService;
import com.esiran.greenpay.system.service.IUserRoleService;
import com.esiran.greenpay.system.service.IUserService;
import org.apache.http.util.TextUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/api/v1/system/users")
public class APIAdminSystemUserController extends CURDBaseController {

    private static ModelMapper modelMapper = new ModelMapper();
    private final IUserService userService;
    private final IUserRoleService iUserRoleService;
    private final IMenuService iMenuService;
    private final IRoleService iRoleService;

    public APIAdminSystemUserController(IUserService userService, IUserRoleService iUserRoleService, IMenuService iMenuService, IRoleService iRoleService) {
        this.userService = userService;
        this.iUserRoleService = iUserRoleService;
        this.iMenuService = iMenuService;
        this.iRoleService = iRoleService;
    }

    @GetMapping("/getOne")
    public UserDTO getOneUser(@PathParam("userId") Integer userId) throws PostResourceException {
        if (userId == null || userId <= 0) {
            throw new PostResourceException("用户ID不正确");
        }
        UserDTO userDTO = userService.selectUserById(userId);
        return userDTO;
    }

    /**
     * 获取当前用户拥有的权限
     * @param modelMap
     * @param userId
     * @return
     * @throws PostResourceException
     */
    @GetMapping("/getUserAndRoles")
    public HashMap<String,Object> edit( ModelMap modelMap,@PathParam("userId")  Integer userId) throws PostResourceException {
        UserDTO user = userService.selectUserById(userId);

        List<UserRole> userRoles = iUserRoleService.selectUserRoleById(userId);
        List<Integer> collect = userRoles.stream().map(userRole -> userRole.getRoleId()).collect(Collectors.toList());
//        modelMap.addAttribute("user", user);
//        modelMap.addAttribute("userRoles", collect);
        HashMap<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("userRoles", collect);
        return data;
    }


    @PostMapping("/updateUserAndRoles")
    public boolean updateUserAndRoles(@PathParam("userId") Integer userId,@Valid UserInputDto userInputDto) throws APIException {

        if (StringUtils.isBlank(userInputDto.getUsername()) ||
                userInputDto.getUsername().length()<2) {

            throw new APIException("用户名格式不正确","400");
        }
        if (StringUtils.isBlank(userInputDto.getEmail())) {
            throw new APIException("用户名或Email为空","400");
        }
//        if (StringUtils.isBlank(userInputDto.getPassword()) || userInputDto.getPassword().length()<6) {
//            throw new PostResourceException("用户名密码至少6位");
//        }

//        if (StringUtils.isBlank(userInputDto.getRoleIds())) {
//            throw new PostResourceException("未选择角色权限");
//        }

        iUserRoleService.updateUserAndRoles(userId,userInputDto);

        return true;
    }

    @GetMapping
    public IPage<UserDTO> list(
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size ,
            UserDTO userDTO) {

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!TextUtils.isEmpty(userDTO.getUsername())) {
            userLambdaQueryWrapper.eq(User::getUsername, userDTO.getUsername());
        }
        Page<User> page = userService.page(new Page<>(current, size),userLambdaQueryWrapper);

        List<User> records = page.getRecords();
        User loginUser = theUser();
        if (records.contains(loginUser)) {
            records.remove(loginUser);
        }

        IPage<UserDTO> convert = page.convert(item -> modelMapper.map(item, UserDTO.class));

        for (UserDTO user : convert.getRecords()) {
            List<UserRole> userRoles = iUserRoleService.selectUserRoleById(user.getId());
            List<Integer> collect = userRoles.stream().map(role -> role.getRoleId()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                List<Role> roles = iRoleService.selectByIds(collect);
                String roleNames ="";
                for (int i = 0; i < roles.size(); i++) {
                    if (i != 0) {
                        roleNames += ",";
                    }
                    roleNames += roles.get(i).getName();
                }
                user.setRoleNames(roleNames);
            }

        }

        return convert;
    }


    @DeleteMapping("/del")
    public boolean del(@RequestParam Integer id) throws PostResourceException {
        if (id <= 0) {
            throw new PostResourceException("用户ID不正确");
        }
        UserDTO userDTO = userService.selectUserById(id);
        if (userDTO == null) {
            throw new PostResourceException("用户不存在");
        }

        iUserRoleService.removeUserRoleYyUserId(id);
        return true;
    }


    @PostMapping
    public ResponseEntity add(@Valid UserInputDto userInputDto) throws APIException {

        iUserRoleService.addUserAndRole(userInputDto);

        return ResponseEntity.status(HttpStatus.OK).body(true);
    }


}
