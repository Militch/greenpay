package com.esiran.greenadmin.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.system.entity.UserRole;
import com.esiran.greenadmin.system.entity.dot.UserInputDto;
import com.esiran.greenadmin.system.entity.dot.UserRoleInputDto;

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
    IPage<UserRoleInputDto> selectUserRoles(Page<UserRole> userVoPage);

    boolean addUserAndRole(UserInputDto userInputDto) throws APIException;

    List<UserRole> selectUserRoleById(Integer userId);

    boolean updateUserAndRoles(Integer userId, UserInputDto userInputDTO) throws APIException;


    boolean updateUserRoleByUserId(Integer userId, String[] roleIds);

    boolean removeUserRoleYyUserId(Integer userId);

}
