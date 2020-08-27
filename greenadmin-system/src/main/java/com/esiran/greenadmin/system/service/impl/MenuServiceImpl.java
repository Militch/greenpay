package com.esiran.greenadmin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.system.entity.Menu;
import com.esiran.greenadmin.system.entity.dot.MenuDTO;
import com.esiran.greenadmin.system.entity.vo.MenuTreeVo;
import com.esiran.greenadmin.system.entity.vo.MenuVo;
import com.esiran.greenadmin.system.mapper.MenuMapper;
import com.esiran.greenadmin.system.service.IMenuService;
import com.esiran.greenadmin.system.utils.TreeUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

/**
 * <p>
 * 系统菜单 服务实现类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
    private static  final ModelMapper modelMap = new ModelMapper();

    @Override
    public ResponseEntity selectAllUserMenue(Page<Menu> iPage) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);
        IPage<MenuVo> menuIPage = this.baseMapper.selectMenu(iPage, queryWrapper );
        return ResponseEntity.status(HttpStatus.OK).body(menuIPage.getRecords());
    }

    @Override
    public ResponseEntity selectMenu(Integer id) {
        QueryWrapper<MenuVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("system_user.id", id);
        queryWrapper.eq("parent_id", 0);
        MenuVo menuVo = this.baseMapper.selectMenu(queryWrapper);
        menuVo.setTitles(buildMenuRoles(menuVo.getMenus()));

        return ResponseEntity.status(HttpStatus.OK).body(menuVo);
    }

    @Override
    public ResponseEntity selectMenuAll() {
        QueryWrapper<MenuVo> queryWrapper = new QueryWrapper<>();
        MenuVo menuVo = this.baseMapper.selectMenu(queryWrapper);
        menuVo.setTitles(buildMenuRoles(menuVo.getMenus()));

        return ResponseEntity.status(HttpStatus.OK).body(menuVo);
    }

    private String buildMenuRoles(List<Menu> menus) {
        List<String> strings = menus.stream().map(item -> item.getTitle()).collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            stringBuilder.append(strings.get(i)).append(i < strings.size()-1 ? "," : "");
        }
        return stringBuilder.toString();
    }

    @Override
    public List<MenuTreeVo> menuList() {

        List<Menu> m = this.list(Wrappers.<Menu>query().orderByAsc("sorts"));

        List<MenuTreeVo> menuTreeVoList =m.stream()
                .map(item -> modelMap.map(item, MenuTreeVo.class))
                .collect(Collectors.toList());

        TreeUtil.buildByLoop(menuTreeVoList, 0);
//
//        Map<Integer, List<MenuTreeVo>> listMap = new HashMap<>();
//
//        menuTreeVoList.forEach(item-> {
//            if (!CollectionUtils.isEmpty(item.getChildrens())) {
//                listMap.put(item.getSorts(), item.getChildrens());
//            }
//        });
//        sortByValue(listMap);
//        List<MenuTreeVo> entries = new ArrayList(listMap.entrySet());

        return menuTreeVoList;
    }

    // 按照值排序
    static void sortByValue(Map map) {
        List<Map.Entry<Integer, Object>> list = new ArrayList<Map.Entry<Integer, Object>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Object>>() {
            @Override
            public int compare(Map.Entry<Integer, Object> o1, Map.Entry<Integer, Object> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });


    }

    @Override
    public IPage<Menu> menuTreeList(Page<Menu> menuPage) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        Page<Menu> menuPage1 = this.baseMapper.selectPage(menuPage, queryWrapper);
        return menuPage1;
    }


    @Override
    public List<MenuTreeVo> getMenuTreeByType(int type) {
        List<Menu> menus = this.list();
        List<MenuTreeVo> mt = menus.stream()
                .map(item -> modelMap.map(item, MenuTreeVo.class))
                .filter(item -> item.getType().equals(type))
                .collect(Collectors.toList());
        return TreeUtil.buildByLoop(mt, 0);
    }

    @Override
    public List<MenuTreeVo> getMenuTreeByType(Integer usserId, int type) {
        List<Menu> menus = this.baseMapper.selectMenusByUserId(new QueryWrapper<>(),usserId);
        List<MenuTreeVo> menuTreeVos = menus.stream()
                .map(item -> modelMap.map(item, MenuTreeVo.class))
                .filter(item -> item.getType().equals(type))
                .collect(Collectors.toList());
        return TreeUtil.buildByLoop(menuTreeVos,0);
    }

    @Override
    public List<MenuTreeVo> getMenuTreeByRoleId(Integer roleId) {
        List<Menu> byRoleId = getMenuListByRoleId(roleId);
        return convertMenuToTree(byRoleId);
    }

    @Override
    public List<MenuTreeVo> getMenuTreeByUserId(Integer userId) {
        List<Menu> menus = getMenuListByUserId(userId);
        return convertMenuToTree(menus);
    }

    @Override
    public List<Menu> getMenuListByRoleId(Integer roleId) {
        return baseMapper.selectMenuByRoleId(new QueryWrapper<>(),roleId);
    }

    @Override
    public List<Menu> getMenuListByUserId(Integer userId) {
        return baseMapper.selectMenusByUserId(new QueryWrapper<>(),userId);
    }

    @Override
    public boolean removeMenuByid(Integer menudId) throws APIException {
        Menu menu = getById(menudId);
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Menu::getParentId, menu.getId());
        List<Menu> menus = this.list(lambdaQueryWrapper);
        if (!CollectionUtils.isEmpty(menus)) {
            throw new APIException("当前菜单下还有子节点", String.valueOf(HttpStatus.BAD_REQUEST));
        }
        removeById(menudId);
        return true;
    }

    @Override
    public List<Menu> findMenusByParentId(Integer parentId) {
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(Menu::getParentId,parentId)
                .orderByDesc(Menu::getSorts);
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<MenuDTO> fa(List<Menu> menus, int deep) {
        modelMap.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<MenuDTO> m = new ArrayList<>();
        for (Menu menu : menus){
            MenuDTO menuDTO = modelMap.map(menu,MenuDTO.class);
            String text = String.format("%s%s",deep>0?"--&nbsp;":"",menu.getTitle());
            String[] s = new String[deep>1?(deep-1)*3:0];
            Arrays.fill(s,"&nbsp;");
            String a = String.join("",s);
            menuDTO.setTitleDisplay(a.concat(text));
            m.add(menuDTO);
            List<Menu> c = findMenusByParentId(menu.getId());
            if (c.size() != 0){
                int de = deep+1;
                m.addAll(fa(c, de));
            }
        }
        return m;
    }

    @Override
    public List<MenuDTO> all() {
        List<Menu> rootMenus = findMenusByParentId(0);
        return fa(rootMenus, 0);
    }


    private List<MenuTreeVo> convertMenuToTree(List<Menu> menus){

        ModelMapper modelMapper = new ModelMapper();
        List<MenuTreeVo> mt = menus.stream()
                .map(item -> modelMapper.map(item, MenuTreeVo.class))
                .collect(Collectors.toList());
        return TreeUtil.buildByLoop(mt,0);
    }

    @Override
    public MenuDTO selectMenuById(Long userId) throws APIException {
        Menu user = getById(userId);
        if (user == null) {
            throw new APIException("菜单不存在",String.valueOf(HttpStatus.NOT_FOUND) );
        }
        return modelMap.map(user, MenuDTO.class);
    }



    @Override
    public void addMenu(MenuDTO menuDTO) throws APIException {
        Menu menu = modelMap.map(menuDTO, Menu.class);
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Menu::getTitle, menu.getTitle());
        Menu oldMenu = getOne(lambdaQueryWrapper);
        if (oldMenu != null) {
            throw new APIException("菜单已经存在",String.valueOf(HttpStatus.BAD_REQUEST));
        }
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(menu.getCreatedAt());
        save(menu);
    }


}
