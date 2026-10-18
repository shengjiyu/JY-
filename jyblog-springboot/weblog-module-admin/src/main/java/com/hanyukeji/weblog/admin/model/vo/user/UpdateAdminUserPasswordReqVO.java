package com.hanyukeji.weblog.admin.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "修改用户密码 VO")
public class UpdateAdminUserPasswordReqVO {

    /**
     * 用户名（仅作展示，后端以当前登录用户为准）
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 64, message = "密码长度需在 6 ~ 64 位之间")
    @ApiModelProperty(value = "密码")
    private String password;
}
