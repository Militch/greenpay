package com.esiran.greenadmin.admin.controller.system.menu;

import com.esiran.greenadmin.admin.controller.CURDBaseController;
import com.esiran.greenadmin.common.entity.APIError;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.system.entity.Menu;
import com.esiran.greenadmin.system.entity.dot.MenuDTO;
import com.esiran.greenadmin.system.service.IMenuService;
import com.google.gson.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/system/menu")
public class AdminSystemMenuController extends CURDBaseController {

    private final IMenuService iMenuService;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,new LocalDateAdapter())
            .disableHtmlEscaping().create();
    private static final ModelMapper MODEL_MAPPER = new ModelMapper();
    public static class LocalDateAdapter implements JsonSerializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }
    public AdminSystemMenuController(IMenuService iMenuService) {
        this.iMenuService = iMenuService;
    }

    @GetMapping("/list")
    public String list(ModelMap modelMap){
        List<MenuDTO> md = iMenuService.all();
        modelMap.addAttribute("listJsonString", gson.toJson(md));
        return render("system/menu/list");
    }

    @GetMapping("/list/{menuId}/edit")
    public String edit(HttpSession httpSession, ModelMap modelMap, @PathVariable Long menuId) throws APIException {
        List<APIError> apiErrors = (List<APIError>) httpSession.getAttribute("errors");
        modelMap.addAttribute("errors", apiErrors);
        httpSession.removeAttribute("errors");
        MenuDTO menuDTO = iMenuService.selectMenuById(menuId);
        modelMap.addAttribute("menu", menuDTO);
        return render("system/menu/edit");
    }


    @PostMapping("/list/{menuId}/edit")
    public String edit(@PathVariable Integer menuId, MenuDTO menuDTO) throws APIException {

        if (menuDTO.getParentId()<=0){
            throw new APIException("上级ID不正确",String.valueOf(HttpStatus.NOT_FOUND));
        }
        Menu menu = iMenuService.getById(menuId);
        menu.setTitle(menuDTO.getTitle());
        menu.setMark(menuDTO.getMark());
        menu.setType(menuDTO.getType());
        menu.setPath(menuDTO.getPath());
        menu.setSorts(menuDTO.getSorts());
        menu.setParentId(menuDTO.getParentId());
        iMenuService.updateById(menu);
        return redirect("/system/menu/list");
    }



    @GetMapping("/add")
    public String add(HttpSession httpSession, ModelMap modelMap) {
        List<APIError> apiErrors = (List<APIError>) httpSession.getAttribute("errors");
        modelMap.addAttribute("errors", apiErrors);
        httpSession.removeAttribute("errors");
        return render("system/menu/add");
    }


    @PostMapping("/add")
    public String add(@Valid MenuDTO menuDTO) throws Exception{
        iMenuService.addMenu(menuDTO);
        return redirect("/system/menu/list");
    }
}
