package com.esiran.greenadmin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.esiran.greenadmin.common.entity.APIException;
import com.esiran.greenadmin.common.exception.PostResourceException;
import com.esiran.greenadmin.system.entity.User;
import com.esiran.greenadmin.system.entity.dot.UserDTO;
import com.esiran.greenadmin.system.entity.dot.UserInputDto;
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
    User addUser(UserInputDto userInputDto) throws APIException;

    UserDTO selectUserById(Integer userId) throws PostResourceException;

    void updateUser(Integer userId, UserInputDTO userInputDTO) throws PostResourceException;
    void updateUserPWD(Integer integer, UserInputVo userInputVo) throws PostResourceException;
    boolean verifyTOTPPass(Integer userId, String pass);
    String getTOTPSecretKey(Integer userId);
    void resetTOTPSecretKey(Integer userId);
}
