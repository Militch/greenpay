package com.esiran.greenadmin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.common.exception.PostResourceException;
import com.esiran.greenadmin.common.util.TOTPUtil;
import com.esiran.greenadmin.system.entity.Role;
import com.esiran.greenadmin.system.entity.RoleMenu;
import com.esiran.greenadmin.system.entity.User;
import com.esiran.greenadmin.system.entity.UserRole;
import com.esiran.greenadmin.system.entity.dot.UserDTO;
import com.esiran.greenadmin.system.entity.dot.UserInputDto;
import com.esiran.greenadmin.system.entity.dot.UserUpdateDto;
import com.esiran.greenadmin.system.entity.vo.UserInputDTO;
import com.esiran.greenadmin.system.entity.vo.UserInputVo;
import com.esiran.greenadmin.system.mapper.UserMapper;
import com.esiran.greenadmin.system.service.IUserRoleService;
import com.esiran.greenadmin.system.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private static final ModelMapper modelMapper = new ModelMapper();
    private final IUserRoleService iUserRoleService;

    public UserServiceImpl(IUserRoleService iUserRoleService) {
        this.iUserRoleService = iUserRoleService;
    }

    @Override
    public UserDTO addUser(UserInputDto userInputDto) throws APIException {
        User user = modelMapper.map(userInputDto, User.class);
        User oldUser = getUserByUsernameOrEmail(user.getUsername(), user.getEmail());
        if (oldUser != null) {
            throw new APIException("用户名或邮箱已经存在","USER_NAME_OR_EMAIL_NO_USED");
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        save(user);
        if (userInputDto.getRoleIds() != null){
            List<String> roleIds = Arrays.stream(userInputDto.getRoleIds()).filter(Objects::nonNull).collect(Collectors.toList());
            List<Integer> userRoles = roleIds.stream()
                    .map(Integer::parseInt).collect(Collectors.toList());
            iUserRoleService.resetUserRoles(user.getId(),userRoles);
        }
        return modelMapper.map(user,UserDTO.class);
    }

    @Override
    public UserDTO updateUserById(UserUpdateDto userUpdateDto) throws APIException, PostResourceException {
        User u = getOne(new QueryWrapper<User>().lambda().eq(User::getId,userUpdateDto.getId()));
        if (u == null) throw new PostResourceException("用户ID不存在，请重试");
        User user = modelMapper.map(userUpdateDto, User.class);
        User oldUser = getUserByUsernameOrEmail(user.getUsername(), user.getEmail());
        if (oldUser != null && !oldUser.getId().equals(user.getId())) {
            throw new APIException("用户名或邮箱已经存在","USER_NAME_OR_EMAIL_NO_USED");
        }
        user.setUpdatedAt(LocalDateTime.now());
        if (StringUtils.isBlank(user.getPassword())) user.setPassword(null);
        updateById(user);
        if (userUpdateDto.getRoleIds() != null){
            List<String> roleIds = Arrays.stream(userUpdateDto.getRoleIds()).filter(Objects::nonNull).collect(Collectors.toList());
            List<Integer> userRoles = roleIds.stream()
                    .map(Integer::parseInt).collect(Collectors.toList());
            iUserRoleService.resetUserRoles(user.getId(),userRoles);
        }else{
            iUserRoleService.remove(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId,user.getId()));
        }
        return modelMapper.map(user,UserDTO.class);
    }

    @Override
    public User getUserByUsernameOrEmail(String username, String emails) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, username).or()
                .eq(User::getEmail, emails);
        return getOne(new QueryWrapper<User>().lambda()
                .eq(User::getUsername,username)
                .eq(User::getEmail,emails));
    }

    @Override
    public UserDTO selectUserById(Integer userId) throws PostResourceException {
        User user = getById(userId);
        if (user == null) {
            throw new PostResourceException("用户不存在");
        }
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public void removeUserById(Integer id) throws PostResourceException {
        User user = getOne(new QueryWrapper<User>().lambda().eq(User::getId,id));
        if (user == null) throw new PostResourceException("所需操作的用户不存在");
        remove(new QueryWrapper<User>().lambda().eq(User::getId,id));
        iUserRoleService.remove(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId,id));
    }

    @Override
    public void updateUserById(Integer userId, UserInputDTO userInputDTO) throws PostResourceException {
        User oldUser = this.getById(userId);
        if (oldUser == null) {
            throw new PostResourceException("用户不存在");
        }
        User user = modelMapper.map(userInputDTO, User.class);
        user.setId(oldUser.getId());
        user.setUpdatedAt(LocalDateTime.now());
        updateById(user);

    }

    @Override
    public void updateUserPWD(Integer userId, UserInputVo userInputVo) throws PostResourceException {
        User oldUser = this.getById(userId);
        if (oldUser == null) {
            throw new PostResourceException("未查询到用户");
        }
        if (!oldUser.getPassword().equals(userInputVo.getOldPassword())) {
            throw new PostResourceException("原密码不正确");
        }
        if (oldUser.getPassword().equals(userInputVo.getNewPassword())) {
            throw new PostResourceException("新密码与旧密码相同");
        }

        if (!userInputVo.getNewPassword().equals(userInputVo.getConfirmPassword())) {
            throw new PostResourceException("两次输入的密码不一致");
        }

        oldUser.setPassword(userInputVo.getNewPassword());
        oldUser.setUpdatedAt(LocalDateTime.now());
        updateById(oldUser);


    }

    @Override
    public boolean verifyTOTPPass(Integer userId, String pass) {
        Pattern pattern = Pattern.compile("[0-9]{6}");
        Matcher matcher = pattern.matcher(pass);
        if (!matcher.matches()){
            throw new IllegalArgumentException("动态密码格式错误");
        }
        User user = this.getById(userId);
        if (user == null)
            throw new IllegalArgumentException("用户不存在");
        String totpSecretKey = user.getTotpSecretKey();

        if (totpSecretKey == null || totpSecretKey.length() == 0)
            throw new IllegalArgumentException("当前用户未开启两步验证码");
        try {
            String target = TOTPUtil.nextCode(totpSecretKey,30,"SHA1");
            return target.equals(pass);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getTOTPSecretKey(Integer userId) {
        User user = getById(userId);
        if (user == null || user.getTotpSecretKey() == null
                || user.getTotpSecretKey().length() == 0) return null;
        return user.getTotpSecretKey();
    }

    @Override
    public void resetTOTPSecretKey(Integer userId) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        String totpSecretKey = UUID.randomUUID().toString().replaceAll("-","");
        updateWrapper.set(User::getTotpSecretKey,totpSecretKey);
        updateWrapper.eq(User::getId,userId);
        this.update(updateWrapper);
    }

}
