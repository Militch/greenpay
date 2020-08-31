package com.esiran.greenadmin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.system.entity.User;
import com.esiran.greenadmin.system.entity.UserRole;
import com.esiran.greenadmin.system.entity.dot.UserInputDto;
import com.esiran.greenadmin.system.entity.dot.UserRoleInputDto;
import com.esiran.greenadmin.system.mapper.UserRoleMapper;
import com.esiran.greenadmin.system.service.IUserRoleService;
import com.esiran.greenadmin.system.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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


    @Override
    public void resetUserRoles(Integer userId, List<Integer> roleIds) {
        if (userId == null) throw new IllegalArgumentException("userId is Null");
        if (roleIds == null) throw new IllegalArgumentException("roleIds is Null");
        remove(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId,userId));
        List<UserRole> userRoles = roleIds.stream().map(item->{
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(item);
            return ur;
        }).collect(Collectors.toList());
        saveBatch(userRoles);
    }

    @Override
    public IPage<UserRoleInputDto> selectUserRoles(Page<UserRole> userVoPage) {
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        return this.baseMapper.selectRole(userVoPage,wrapper);
    }

    @Override
    @Transactional
    public boolean addUserAndRole(UserInputDto userInputDto) throws APIException {

//        User user = userService.addUser(userInputDto);
//        String[] split = userInputDto.getRoleIds().split(",");
//        if (split.length>0 && !split[0].equals("")) {
//            UserRole role = new UserRole();
//            for (String s : split) {
//                Integer id = Integer.valueOf(s);
//                role.setUserId(user.getId());
//                role.setRoleId(id);
//                this.save(role);
//            }
//        }

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


}
