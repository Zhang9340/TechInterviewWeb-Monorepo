package com.zzy.techInterviewWeb.model.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * User Table
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Account
     */
    private String userAccount;

    /**
     * Password
     */
    private String userPassword;

    /**
     * WeChat Open Platform ID
     */
    private String unionId;

    /**
     * Public Account OpenID
     */
    private String mpOpenId;

    /**
     * User Nickname
     */
    private String userName;

    /**
     * User Avatar
     */
    private String userAvatar;

    /**
     * User Profile
     */
    private String userProfile;

    /**
     * User Role: user/admin/ban
     */
    private String userRole;

    /**
     * Edit Time
     */
    private Date editTime;

    /**
     * Creation Time
     */
    private Date createTime;

    /**
     * Update Time
     */
    private Date updateTime;

    /**
     * Deleted Flag
     */
    @TableLogic
    private Integer isDelete;


    /**
     * phone number
     */
    private String phoneNumber;

    /**
     * email
     */
    private String email;

    /**
     *  graduation time
     */
    private String grade;

    /**
     * work experience
     */
    private String workExperience;

    /**
     * expertise
     */
    private String expertiseDirection;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}