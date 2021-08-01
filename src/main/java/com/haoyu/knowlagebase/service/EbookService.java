package com.haoyu.knowlagebase.service;

import com.haoyu.knowlagebase.domain.Ebook;
import com.haoyu.knowlagebase.domain.EbookExample;
import com.haoyu.knowlagebase.mapper.EbookMapper;
import com.haoyu.knowlagebase.req.EbookReq;
import com.haoyu.knowlagebase.resp.EbookResp;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/*
 * @author haoyu
 * @date 2021/8/1 17:06
 */
@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        //模糊匹配
        criteria.andNameLike("%"+req.getName()+"%");
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        List<EbookResp> respList = new ArrayList<>();
        for(Ebook ebook : ebookList){
            EbookResp ebookResp = new EbookResp();
            BeanUtils.copyProperties(ebook, ebookResp);
            respList.add(ebookResp);
        }
        return respList;
    }
}
