package com.haoyu.knowlagebase.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.haoyu.knowlagebase.domain.Ebook;
import com.haoyu.knowlagebase.domain.EbookExample;
import com.haoyu.knowlagebase.mapper.EbookMapper;
import com.haoyu.knowlagebase.req.EbookReq;
import com.haoyu.knowlagebase.resp.EbookResp;
import com.haoyu.knowlagebase.resp.PageResp;
import com.haoyu.knowlagebase.util.CopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/*
 * @author haoyu
 * @date 2021/8/1 17:06
 */
@Service
public class EbookService {
    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Resource
    private EbookMapper ebookMapper;

    public PageResp<EbookResp> list(EbookReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())){//动态sql写法
            criteria.andNameLike("%"+req.getName()+"%");
        }
        //模糊匹配
        PageHelper.startPage(req.getPage(),req.getSize());//只对第一个遇到的sql起作用  1.页码 2.每页的条数
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数:{}",pageInfo.getTotal());
        LOG.info("总页数:{}",pageInfo.getPages());
//        List<EbookResp> respList = new ArrayList<>();
//        for(Ebook ebook : ebookList){
//            //EbookResp ebookResp = new EbookResp();
//            //BeanUtils.copyProperties(ebook, ebookResp);
              //对象复制
//            EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);
//            respList.add(ebookResp);
//        }


        //列表复制
        List<EbookResp> list = CopyUtil.copyList(ebookList, EbookResp.class);
        PageResp<EbookResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }
}
