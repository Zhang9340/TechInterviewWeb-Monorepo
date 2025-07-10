package com.zzy.techInterviewWeb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzy.techInterviewWeb.model.dto.user.UserQueryRequest;
import com.zzy.techInterviewWeb.model.vo.LoginUserVO;
import com.zzy.techInterviewWeb.model.vo.UserVO;
import com.zzy.techInterviewWeb.model.entity.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
/**
 * User Service
 */
public interface UserService extends IService<User> {

    /**
     * User Registration
     *
     * @param userAccount   User account
     * @param userPassword  User password
     * @param checkPassword Password confirmation
     * @return New user ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * User Login
     *
     * @param userAccount  User account
     * @param userPassword User password
     * @param request      HTTP request
     * @return Desensitized user information
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * User Login (WeChat Open Platform)
     *
     * @param wxOAuth2UserInfo User information retrieved from WeChat
     * @param request          HTTP request
     * @return Desensitized user information
     */
    LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request);

    /**
     * Get the currently logged-in user
     *
     * @param request HTTP request
     * @return The logged-in user
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * Get the currently logged-in user (allows not being logged in)
     *
     * @param request HTTP request
     * @return The logged-in user or null if not logged in
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * Check if the user is an administrator
     *
     * @param request HTTP request
     * @return True if the user is an administrator, false otherwise
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * Check if the user is an administrator
     *
     * @param user The user object
     * @return True if the user is an administrator, false otherwise
     */
    boolean isAdmin(User user);

    /**
     * User Logout
     *
     * @param request HTTP request
     * @return True if logout was successful, false otherwise
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * Get desensitized information of the logged-in user
     *
     * @param user The user object
     * @return Desensitized user information
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * Get desensitized user information
     *
     * @param user The user object
     * @return Desensitized user information
     */
    UserVO getUserVO(User user);

    /**
     * Get desensitized user information for a list of users
     *
     * @param userList The list of user objects
     * @return A list of desensitized user information
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * Get query conditions
     *
     * @param userQueryRequest User query request object
     * @return Query conditions
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     *add user daily check in
     *
     *
     */
    boolean addUserSignIn(long userId);

    /**
     *get user daily check in record
     *
     *
     */
    List<Integer> getUserSignInRecord(long userId, Integer year);

}
