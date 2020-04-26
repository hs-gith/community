package com.hs.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hs.config.Trimmed;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.hs.config.Trimmed.TrimmerType.ALL_WHITESPACES;


/**
 * <p>
 * 用户表
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel
public class User extends BaseEntity {

    @NotBlank(message = "用户id")
    @ApiModelProperty("用户id")
    private Integer userId;

    @NotBlank(message = "姓名不能为空")
    @ApiModelProperty("用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("性别 0男 1女")
    private String sex;

    @NotBlank(message = "手机号不能为空")
    @Trimmed(value = ALL_WHITESPACES)
    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("权限 0管理员 1版主 2普通用户")
    private Integer userPower;

    @ApiModelProperty("状态 0存在 1删除")
    private String flag;

    @ApiModelProperty("头像图片")
    private String headImg;


}
