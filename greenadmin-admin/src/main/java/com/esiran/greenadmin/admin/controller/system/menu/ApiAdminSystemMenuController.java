package com.esiran.greenadmin.admin.controller.system.menu;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.common.exception.PostResourceException;
import com.esiran.greenadmin.system.entity.Menu;
import com.esiran.greenadmin.system.entity.MenuTreeNode;
import com.esiran.greenadmin.system.entity.User;
import com.esiran.greenadmin.system.entity.vo.MenuInputVo;
import com.esiran.greenadmin.system.entity.vo.MenuTreeVo;
import com.esiran.greenadmin.system.service.IMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author han
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/api/v1/system/menus")
public class ApiAdminSystemMenuController {

    private final static ModelMapper modelMapper = new ModelMapper();
    private final IMenuService iMenuService;

    public ApiAdminSystemMenuController(IMenuService iMenuService) {
        this.iMenuService = iMenuService;
    }


    @GetMapping("/tree")
    public ResponseEntity<List<MenuTreeNode>> treeList(){
        User u = (User) SecurityUtils.getSubject().getPrincipal();
        return ResponseEntity.ok(iMenuService.selectMenuTreeByUserId(u.getId()));
    }

    @ApiOperation("查询所有菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码", defaultValue = "1"),
            @ApiImplicitParam(name = "size",value = "每页个数 ",defaultValue = "10")
    })
    @GetMapping
    @RequiresPermissions("system_menu_view")
    public  ResponseEntity<List<MenuTreeVo>> list() {
        List<MenuTreeVo> menuTreeVoList = iMenuService.menuList();
        for (MenuTreeVo menu : menuTreeVoList) {
            if (menu.getType() == 1) {
                menu.setTitle("|-" + menu.getTitle());
                continue;
            }
            if (menu.getType() == 2) {
                menu.setTitle("&emsp;  -" + menu.getTitle());
            }
        }
        return ResponseEntity.ok(menuTreeVoList);
    }



    @GetMapping("/roleTree")
    @RequiresPermissions("system_menu_view")
    public ResponseEntity<IPage<Menu>> Rolelist(Page<Menu> page){
        page.setSize(100);
        IPage<Menu> menuTreeVoList = iMenuService.menuTreeList(page);

        return ResponseEntity.ok(menuTreeVoList);
    }

    @PostMapping
    @RequiresPermissions("system_menu_add")
    public ResponseEntity<String> add(MenuInputVo menuInputVo) throws PostResourceException {

        if (menuInputVo == null) {
            throw new PostResourceException("参数不正确");
        }
        Menu menu = modelMapper.map(menuInputVo, Menu.class);
        iMenuService.save(menu);

        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

    @PutMapping
    @RequiresPermissions("system_menu_update")
    public ResponseEntity put( MenuInputVo menu) throws PostResourceException {
        if (menu.getId() <= 0) {
            throw new PostResourceException("权限ID不正确");
        }
        //查找到
        Menu menu1 = iMenuService.getById(menu.getId());
        if (menu1 == null) {
            throw new PostResourceException("未找到权限");
        }
        //更新
        Menu newMenu = modelMapper.map(menu, Menu.class);
        iMenuService.updateById(newMenu);
        //返回
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

    @GetMapping("/{id}")
    @RequiresPermissions("system_menu_update")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Menu menu = iMenuService.getById(id);
        if (menu == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(menu);
    }

    @GetMapping("/list")
    @RequiresPermissions("system_menu_view")
    public ResponseEntity all(Page<Menu> page) {
        return iMenuService.selectAllUserMenue(page);
    }

    @GetMapping("/all")
    @RequiresPermissions("system_menu_view")
    public ResponseEntity all(){
        return iMenuService.selectMenuAll();
    }

    @DeleteMapping("/del")
    @RequiresPermissions("system_menu_del")
    public void del(@RequestParam Integer menuId) throws APIException, PostResourceException {
        iMenuService.removeMenuById(menuId);
    }

}
