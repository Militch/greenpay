package com.esiran.greenpay.system.service;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenpay.common.entity.APIException;
import com.esiran.greenpay.common.exception.PostResourceException;
import com.esiran.greenpay.system.entity.User;
import com.esiran.greenpay.system.entity.dot.UserDTO;
import com.esiran.greenpay.system.entity.dot.UserInputDto;
import com.esiran.greenpay.system.entity.vo.UserInputDTO;
import com.esiran.greenpay.system.entity.vo.UserInputVo;

/**
 * <p>
 * 系统用户 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IUserService extends IService<User> {
    User addUser(UserInputDto userInputDto) throws APIException;

    UserDTO selectUserById(Integer userId) throws PostResourceException;

    void updateUser(Integer userId, UserInputDTO userInputDTO) throws PostResourceException;
    void updateUserPWD(Integer integer, UserInputVo userInputVo) throws PostResourceException;
    boolean verifyTOTPPass(Integer userId, String pass);
    String getTOTPSecretKey(Integer userId);
    void resetTOTPSecretKey(Integer userId);
}
