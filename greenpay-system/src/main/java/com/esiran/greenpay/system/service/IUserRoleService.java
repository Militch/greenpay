package com.esiran.greenpay.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.system.entity.UserRole;
import com.esiran.greenpay.system.entity.dot.UserInputDto;
import com.esiran.greenpay.system.entity.dot.UserRoleDto;

import java.util.List;

/**
 * <p>
 * 系统用户角色 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IUserRoleService extends IService<UserRole> {
    IPage<UserRoleDto> selectUserRoles(Page<UserRole> userVoPage);

    boolean addUserAndRole(UserInputDto userInputDto) throws PostResourceException;

    List<UserRole> selectUserRoleById(Integer userId);

    boolean updateUserAndRoles(Integer userId, UserInputDto userInputDTO) throws PostResourceException;



    boolean updateUserRoleByUserId(Integer userId, String[] roleIds);

    boolean removeUserRoleYyUserId(Integer userId);

}
