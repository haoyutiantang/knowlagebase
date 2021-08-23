package com.haoyu.knowlagebase.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.haoyu.knowlagebase.domain.User;
import com.haoyu.knowlagebase.domain.UserExample;
import com.haoyu.knowlagebase.exception.BusinessException;
import com.haoyu.knowlagebase.exception.BusinessExceptionCode;
import com.haoyu.knowlagebase.mapper.UserMapper;
import com.haoyu.knowlagebase.req.UserLoginReq;
import com.haoyu.knowlagebase.req.UserQueryReq;
import com.haoyu.knowlagebase.req.UserResetPasswordReq;
import com.haoyu.knowlagebase.req.UserSaveReq;
import com.haoyu.knowlagebase.resp.UserLoginResp;
import com.haoyu.knowlagebase.resp.UserQueryResp;
import com.haoyu.knowlagebase.resp.PageResp;
import com.haoyu.knowlagebase.util.CopyUtil;
import com.haoyu.knowlagebase.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/*
 * @author haoyu
 * @date 2021/8/1 17:06
 */
@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req){
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getLoginName())){//动态sql写法
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        //模糊匹配
        PageHelper.startPage(req.getPage(),req.getSize());//只对第一个遇到的sql起作用  1.页码 2.每页的条数
        List<User> userList = userMapper.selectByExample(userExample);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("总行数:{}",pageInfo.getTotal());
        LOG.info("总页数:{}",pageInfo.getPages());
//        List<UserResp> respList = new ArrayList<>();
//        for(User user : userList){
//            //UserResp userResp = new UserResp();
//            //BeanUtils.copyProperties(user, userResp);
              //对象复制
//            UserResp userResp = CopyUtil.copy(user, UserResp.class);
//            respList.add(userResp);
//        }


        //列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);
        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 保存
     */
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            User userDB = selectByLoginName(req.getLoginName());
            if (ObjectUtils.isEmpty(userDB)) {
                // 新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            } else {
                // 用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        } else {
            // 更新
            user.setLoginName(null);//看到LoginName是空就不会更新
            user.setPassword(null);//看到Password是空就不会更新
            userMapper.updateByPrimaryKeySelective(user);//Selective表示user有值才更新没有值就不更新
        }
    }

    public void delete(Long id){
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String LoginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(LoginName);
        List<User> userList = userMapper.selectByExample(userExample);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        } else {
            return userList.get(0);
        }
    }

    /**
     * 修改密码
     */
    public void resetPassword(UserResetPasswordReq req) {
        User user = CopyUtil.copy(req, User.class);
        userMapper.updateByPrimaryKeySelective(user);//Selective表示user有值才更新没有值就不更新
    }

    /**
     * 登录
     */
    public UserLoginResp login(UserLoginReq req) {
        User userDb = selectByLoginName(req.getLoginName());
        if(ObjectUtils.isEmpty(userDb)){
            //用户名不存在
            LOG.info("用户名不存在, {}", req.getLoginName());
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        }else{
            if(userDb.getPassword().equals(req.getPassword())){
                //登录成功
                UserLoginResp userLoginResp = CopyUtil.copy(userDb, UserLoginResp.class);
                return userLoginResp;
            }else{
                //密码不对
                LOG.info("密码不对, 输入密码：{}, 数据库密码：{}", req.getPassword(), userDb.getPassword());
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }
}
