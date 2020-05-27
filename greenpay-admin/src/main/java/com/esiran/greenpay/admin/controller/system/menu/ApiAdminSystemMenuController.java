package com.esiran.greenpay.admin.controller.system.menu;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.system.entity.Menu;
import com.esiran.greenpay.system.entity.vo.MenuInputVo;
import com.esiran.greenpay.system.entity.vo.MenuTreeVo;
import com.esiran.greenpay.system.service.IMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/admin/api/v1/system/menus")
public class ApiAdminSystemMenuController {

    private final static ModelMapper modelMapper = new ModelMapper();
    private IMenuService iMenuService;

    public ApiAdminSystemMenuController(IMenuService iMenuService) {
        this.iMenuService = iMenuService;
    }

    @ApiOperation("查询所有菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码", defaultValue = "1"),
            @ApiImplicitParam(name = "size",value = "每页个数 ",defaultValue = "10")
    })
    @GetMapping
    public  ResponseEntity list() {

        List<MenuTreeVo> menuTreeVoList = iMenuService.menuList();

        for (MenuTreeVo menu : menuTreeVoList) {
            if (menu.getType() == 1) {
                menu.setTitle("|-" + menu.getTitle());
                continue;
            }
            if (menu.getType() == 2) {
                menu.setTitle("&emsp;  L" + menu.getTitle());
            }
        }
        return ResponseEntity.ok(menuTreeVoList);
    }

    @GetMapping("/roleTree")
    public ResponseEntity Rolelist(Page<Menu> page){
        IPage<Menu> menuTreeVoList = iMenuService.menuTreeList(page);

        return ResponseEntity.ok(menuTreeVoList);
    }

    @PostMapping
    public ResponseEntity add(MenuInputVo menuInputVo) throws PostResourceException {

        if (menuInputVo == null) {
            throw new PostResourceException("参数不正确");
        }
        Menu menu = modelMapper.map(menuInputVo, Menu.class);
        iMenuService.save(menu);

        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

    @PutMapping
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
    public ResponseEntity get(@PathVariable("id") Long id) {
        Menu menu = iMenuService.getById(id);
        if (menu == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(menu);
    }

    @GetMapping("/list")
    public ResponseEntity all(Page<Menu> page) {
        return iMenuService.selectAllUserMenue(page);
    }

    @GetMapping("/all")
    public ResponseEntity all(){
        return iMenuService.selectMenuAll();
    }

    @DeleteMapping("/del")
    public ResponseEntity del(@RequestParam Integer menuId) throws APIException {

        if (menuId <= 0) {
            throw new APIException("菜单ID不正确",String.valueOf(HttpStatus.NOT_FOUND) );
        }
        Menu menuDTO = iMenuService.getById(menuId);
        if (menuDTO == null) {
            throw new APIException("菜单不存在",String.valueOf(HttpStatus.NOT_FOUND) );
        }

        boolean b = iMenuService.removeMenuByid(menuId);

//        iMenuService.removeById(id);
//
//        //删除正面所有子节点
//        if (menuDTO.getParentId() == 0) {
//            QueryWrapper<Menu> menuQueryWrapper = new QueryWrapper<>();
//            menuQueryWrapper.eq("parent_id", menuDTO.getId());
//            iMenuService.remove(menuQueryWrapper);
//        }
        return ResponseEntity.status(HttpStatus.OK).body(b);
    }

}
