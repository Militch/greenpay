package com.esiran.greenadmin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.common.exception.PostResourceException;
import com.esiran.greenadmin.system.entity.User;
import com.esiran.greenadmin.system.entity.dot.UserDTO;
import com.esiran.greenadmin.system.entity.dot.UserInputDto;
import com.esiran.greenadmin.system.entity.dot.UserUpdateDto;
import com.esiran.greenadmin.system.entity.vo.UserInputDTO;
import com.esiran.greenadmin.system.entity.vo.UserInputVo;

/**
 * <p>
 * 系统用户 服务类
 * </p>
 *
 * @author Militch
 * @since 2020-04-13
 */
public interface IUserService extends IService<User> {
    UserDTO addUser(UserInputDto userInputDto) throws APIException;
    UserDTO updateUserById(UserUpdateDto userUpdateDto) throws APIException, PostResourceException;
    User getUserByUsernameOrEmail(String username,String emails);
    UserDTO selectUserById(Integer userId) throws PostResourceException;
    void removeUserById(Integer id) throws PostResourceException;
    void updateUserById(Integer userId, UserInputDTO userInputDTO) throws PostResourceException;
    void updateUserPWD(Integer integer, UserInputVo userInputVo) throws PostResourceException;
    boolean verifyTOTPPass(Integer userId, String pass);
    String getTOTPSecretKey(Integer userId);
    void resetTOTPSecretKey(Integer userId);
}
