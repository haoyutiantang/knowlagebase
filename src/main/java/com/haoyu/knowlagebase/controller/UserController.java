package com.haoyu.knowlagebase.controller;

import com.haoyu.knowlagebase.req.UserQueryReq;
import com.haoyu.knowlagebase.req.UserSaveReq;
import com.haoyu.knowlagebase.resp.CommonResp;
import com.haoyu.knowlagebase.resp.UserQueryResp;
import com.haoyu.knowlagebase.resp.PageResp;
import com.haoyu.knowlagebase.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/*
 * @author haoyu
 * @date 2021/7/14 23:39
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req){
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
         userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

}
