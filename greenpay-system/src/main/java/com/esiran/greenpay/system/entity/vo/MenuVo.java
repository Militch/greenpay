package com.esiran.greenpay.system.entity.vo;

import com.esiran.greenpay.system.entity.Menu;
import lombok.Data;

import java.util.List;

/**
 * @author han
 */
@Data
public class MenuVo {

    private Integer id;

    private String username;

    private String email;

    private List<Menu> menus;

    private String titles;
}
