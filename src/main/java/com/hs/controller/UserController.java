package com.hs.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.reformer.common.base.BaseController;
import com.reformer.common.exception.BusinessException;
import com.reformer.common.wrapper.ApiWrapMapper;
import com.reformer.common.wrapper.ApiWrapper;
import com.reformer.erporder.aop.log.annotation.EnableModifyLog;
import com.reformer.erporder.aop.log.util.ModifyType;
import com.reformer.erporder.common.utils.RedisUtils;
import com.reformer.erporder.dao.system.PbUserMapper;
import com.reformer.erporder.pojo.dto.system.UserMenuDTO;
import com.reformer.erporder.pojo.entity.system.PbUser;
import com.reformer.erporder.pojo.query.system.PbUserQuery;
import com.reformer.erporder.pojo.vo.system.UserVo;
import com.reformer.erporder.service.common.UploadImageService;
import com.reformer.erporder.service.system.IPbUserMenuService;
import com.reformer.erporder.service.system.IPbUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.reformer.common.contant.ErpConstant.MenuConstant.MENU_CHANGE;
import static com.reformer.common.contant.ErpConstant.RedisConstant.*;
import static com.reformer.erporder.aop.log.util.ModifyType.UserOptType.BATCH_AUTHORIZATION;
import static com.reformer.erporder.aop.log.util.ModifyType.UserOptType.UPDATE;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wuligao
 * @since 2019-05-15
 */
@Slf4j
@RestController
@RequestMapping("/sys/pb-user")
@AllArgsConstructor
@Api(tags = "PbUserController", description = "用户管理")
public class UserController {

    private final IPbUserService userService;
    private final UploadImageService uploadImageService;
    private final IPbUserMenuService userMenuService;
    private final RedisUtils redisUtils;
    private final PbUserMapper pbUserMapper;

    @PostMapping("addUser")
    @RequiresPermissions("system:user:add")
    @ApiOperation("添加用户")
    public ApiWrapper AddUser(@RequestBody PbUser pbUser){
        userMenuService.addUser(pbUser);
        return ApiWrapMapper.success();
    }


    @GetMapping
    @ApiOperation("查询用户列表 -（用户管理分页）")
    public ApiWrapper<IPage<UserVo>> getPage(@ModelAttribute PbUserQuery userQuery) {
        IPage<UserVo> userPage = userService.getPageList(userQuery);
        return ApiWrapMapper.success(userPage);
    }

    @GetMapping("getUserByMenuId/{menuId}")
    @ApiOperation("设置菜单用户")
    public ApiWrapper getUserByMenuIds(@PathVariable("menuId") Integer menuId){
        return ApiWrapMapper.success();
    }

    @GetMapping("/format/{phone}")
    public ApiWrapper getCountByPhone(@PathVariable("phone") String phone) {
        return ApiWrapMapper.success(userService.getCountByPhone(phone));
    }

    @GetMapping("/member/{proId}")
    public ApiWrapper getKeyfreeMember(@PathVariable("proId") Integer proId) {
        try {
            userService.initUser(proId);
            return ApiWrapMapper.success();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ApiWrapMapper.error("初始化用户信息异常，请联系维护人员");
        }
    }

    @PostMapping("/update")
    @RequiresPermissions("system:user:update")
    @ApiOperation("修改用户")
    @EnableModifyLog(name = ModifyType.UPDATE, operation = UPDATE, logType = ModifyType.LogType.USER_MANAGEMENT, serviceClass = IPbUserService.class)
    public ApiWrapper update(@RequestBody PbUser user) {
        if (!ObjectUtils.isEmpty(user.getFileName())) {
            byte[] imageBase64 = Base64.decodeBase64(user.getImageUrl());
            String uploadImageUrl = uploadImageService.uploadImage(imageBase64, user.getFileName().replace(" ",""), "PbUser/");
            user.setAvatar(uploadImageUrl);
        }
        user.setUpdateTime(new Date());
        QueryWrapper<PbUser> qw=new QueryWrapper<>();
        qw.eq("phone",user.getPhone());
        PbUser pbUser = pbUserMapper.selectOne(qw);
        if (null != pbUser.getId()) {
            if (!(pbUser.getId().equals(user.getId()))){
                throw new BusinessException("修改的手机号已存在,请重新输入");
            }
        }
        if (userService.updateById(user)) {
            // 修改权限设置缓存
            redisUtils.set(PER_IS_CHANGE+user.getPhone(),user.getPhone());
            if (MENU_CHANGE.equals(user.getMenuChange())) {
                UserMenuDTO userMenuDTO = new UserMenuDTO();
                userMenuDTO.setMenuIds(user.getMenuIds());
                List<Integer> userIds = new ArrayList<>();
                userIds.add(user.getId());
                userMenuDTO.setUserIds(userIds);
                userMenuService.bindUserMenu(userMenuDTO,UPDATE);
            }
            UserVo userVo = userService.getLoginUser(user.getPhone());
            redisUtils.set(LOGIN_USER + userVo.getPhone(), userVo,DEFAULT_PERIOD_VALIDITY);
            redisUtils.del("userInfo:"+user.getId());
            return ApiWrapMapper.success();
        } else {
            return ApiWrapMapper.error("修改用户信息异常，请联系维护人");
        }
    }

    @DeleteMapping
    @RequiresPermissions("system:user:delete")
    public ApiWrapper delete(List<Integer> list) {
        if (userService.removeByIds(list)) {
            return ApiWrapMapper.success();
        } else {
            return ApiWrapMapper.error("删除用户异常，请联系维护人员");
        }
    }

    @PostMapping("/bindUserMenu")
    @ApiOperation("用户授权/批量授权")
    public ApiWrapper bindUserMenu(@RequestBody UserMenuDTO userMenuDTO) {
        if(!CollectionUtils.isEmpty(userMenuDTO.getMenuIds())){
            // 不为空执行操作
            userMenuService.bindUserMenu(userMenuDTO,BATCH_AUTHORIZATION);
        }
        return ApiWrapMapper.success();
    }


}
