package com.esiran.greenpay.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.system.entity.User;
import com.esiran.greenpay.system.entity.UserRole;
import com.esiran.greenpay.system.entity.dot.UserInputDto;
import com.esiran.greenpay.system.entity.dot.UserRoleInputDto;
import com.esiran.greenpay.system.mapper.UserRoleMapper;
import com.esiran.greenpay.system.service.IUserRoleService;
import com.esiran.greenpay.system.service.IUserService;
import org.springframework.data.redis.connection.ConnectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 系统用户角色 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    private IUserService userService;


    public UserRoleServiceImpl(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public IPage<UserRoleInputDto> selectUserRoles(Page<UserRole> userVoPage) {
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        return this.baseMapper.selectRole(userVoPage,wrapper);
    }

    @Override
    @Transactional
    public boolean addUserAndRole(UserInputDto userInputDto) throws APIException {

        User user = userService.addUser(userInputDto);
        String[] split = userInputDto.getRoleIds().split(",");
        if (split.length>0 && !split[0].equals("")) {
            UserRole role = new UserRole();
            for (String s : split) {
                Integer id = Integer.valueOf(s);
                role.setUserId(user.getId());
                role.setRoleId(id);
                this.save(role);
            }
        }

        return true;
    }

    @Override
    public  List<UserRole>  selectUserRoleById(Integer userId) {
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("system_user_role.user_id", userId);
        List<UserRole> userRoles = this.baseMapper.selectList(wrapper);
        return userRoles;
    }

    @Override
    public boolean updateUserRoleByUserId(Integer userId, String[] roleIds) {
        LambdaQueryWrapper<UserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserRole::getUserId, userId);
        remove(lambdaQueryWrapper);

        if (roleIds.length > 0 && !roleIds[0].equals("")) {
            for (String roleId : roleIds) {
                UserRole role = new UserRole();
                role.setUserId(userId);
                role.setRoleId(Integer.valueOf(roleId));
                role.setCreatedAt(LocalDateTime.now());
                role.setUpdatedAt(role.getCreatedAt());
                save(role);
            }
        }


        return false;
    }

    @Override
    @Transactional()
    public boolean updateUserAndRoles(Integer userId, UserInputDto userInputDto) throws APIException {

        User user = userService.getById(userId);

        LambdaQueryWrapper<User> queryWrapper ;
        if (!user.getEmail().equals(userInputDto.getEmail())){
            queryWrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<User> eq = queryWrapper.like(User::getEmail, userInputDto.getEmail());
            User u = userService.getOne(eq);
            if (u != null ) {
                throw new APIException("邮箱已经存在","400");
            }
        }
        if (!user.getUsername().equals(userInputDto.getUsername())){
            queryWrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<User> eq = queryWrapper.like(User::getUsername, userInputDto.getUsername());
            User u = userService.getOne(eq);
            if (u != null ) {
                throw new APIException("用户名已存在","400");
            }
        }

        user.setUsername(userInputDto.getUsername());
        user.setEmail(userInputDto.getEmail());

        user.setUpdatedAt(LocalDateTime.now());
        userService.updateById(user);

        //更新用戶權限
        String[] split = userInputDto.getRoleIds().split(",");
        updateUserRoleByUserId(user.getId(), split);
        return true;
    }


    @Override
    @Transactional
    public boolean removeUserRoleYyUserId(Integer userId) {
        if (userId <= 0) {
            return false;
        }
        LambdaQueryWrapper<UserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserRole::getUserId, userId);
        remove(lambdaQueryWrapper);

        userService.removeById(userId);
        return true;
    }
}
