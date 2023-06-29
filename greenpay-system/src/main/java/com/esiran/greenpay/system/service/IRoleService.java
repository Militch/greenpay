package com.esiran.greenpay.system.service;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.system.entity.Role;
import com.esiran.greenpay.system.entity.dot.MenuDTO;
import com.esiran.greenpay.system.entity.dot.UserRoleInputDto;
import com.esiran.greenpay.system.entity.vo.MenuTreeVo;

import java.util.List;

/**
 * <p>
 * 系统角色 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IRoleService extends IService<Role> {


    Role selectById(Integer id) throws PostResourceException;

    boolean save(UserRoleInputDto roleDto) throws APIException;

    boolean edit(UserRoleInputDto roleDto) throws PostResourceException;

    boolean updateUserRole(UserRoleInputDto userRoleDto) throws ApiException;
    List<Role> selectByIds(List<Integer> ids);

    List<MenuTreeVo> getMenuListByUser(Integer userId);

}
