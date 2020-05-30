package com.esiran.greenpay.system.mapper;

import com.esiran.greenpay.system.entity.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 角色权限表 Mapper 接口
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    @Select("SELECT role_id AS roleId, menu_id AS menuId \n" +
            "FROM greenpay.system_role_menu LEFT JOIN system_menu ON (system_role_menu.menu_id = system_menu.id)\n" +
            "WHERE (system_menu.parent_id !=0) AND (system_menu.type =2) AND system_role_menu.role_id = #{roleId};")
    List<RoleMenu> selectRleMenusByRoleId(Integer roleId);

}
