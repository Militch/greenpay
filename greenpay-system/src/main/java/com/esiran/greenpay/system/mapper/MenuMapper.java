package com.esiran.greenpay.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.esiran.greenpay.system.entity.Menu;
import com.esiran.greenpay.system.entity.vo.MenuVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统菜单 Mapper 接口
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface MenuMapper extends BaseMapper<Menu> {
    IPage<MenuVo> selectMenu(IPage<Menu> iPage, @Param(Constants.WRAPPER) Wrapper<Menu> wrapper);
    MenuVo selectMenu(@Param(Constants.WRAPPER) Wrapper<MenuVo> wrapper);

    List<Menu> selectMenuByRoleId(Integer roleId);

    List<Menu> selectMenusByUserId(Integer userId);
}
