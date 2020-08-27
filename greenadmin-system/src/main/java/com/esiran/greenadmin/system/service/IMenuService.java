package com.esiran.greenadmin.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.system.entity.Menu;
import com.esiran.greenadmin.system.entity.dot.MenuDTO;
import com.esiran.greenadmin.system.entity.vo.MenuTreeVo;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * <p>
 * 系统菜单 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IMenuService extends IService<Menu> {

    List<MenuTreeVo> menuList();
    public IPage<Menu> menuTreeList(Page<Menu> menuPage);

    public List<MenuTreeVo> getMenuTreeByType(int type);

    public List<MenuTreeVo> getMenuTreeByType(Integer usserId, int type);

    public List<MenuTreeVo> getMenuTreeByRoleId(Integer roleId);

    public List<MenuTreeVo> getMenuTreeByUserId(Integer userId);

    public List<Menu> getMenuListByRoleId(Integer roleId);

    public List<Menu> getMenuListByUserId(Integer userId);

    public boolean removeMenuByid(Integer menudId) throws APIException;

    List<Menu> findMenusByParentId(Integer parentId);
    List<MenuDTO> fa(List<Menu> menus, int deep);
    List<MenuDTO> all();

    void addMenu(MenuDTO menuDTO) throws APIException;

    MenuDTO selectMenuById(Long userId) throws APIException;

    ResponseEntity selectAllUserMenue(Page<Menu> iPage);

    ResponseEntity selectMenu(Integer id);

     ResponseEntity selectMenuAll();
}
