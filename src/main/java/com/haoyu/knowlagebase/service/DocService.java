package com.haoyu.knowlagebase.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.haoyu.knowlagebase.domain.Content;
import com.haoyu.knowlagebase.domain.Doc;
import com.haoyu.knowlagebase.domain.DocExample;
import com.haoyu.knowlagebase.mapper.ContentMapper;
import com.haoyu.knowlagebase.mapper.DocMapper;
import com.haoyu.knowlagebase.mapper.DocMapperCust;
import com.haoyu.knowlagebase.req.DocQueryReq;
import com.haoyu.knowlagebase.req.DocSaveReq;
import com.haoyu.knowlagebase.resp.DocQueryResp;
import com.haoyu.knowlagebase.resp.PageResp;
import com.haoyu.knowlagebase.util.CopyUtil;
import com.haoyu.knowlagebase.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/*
 * @author haoyu
 * @date 2021/8/1 17:06
 */
@Service
public class DocService {
    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    private DocMapperCust docMapperCust;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<DocQueryResp> all(Long ebookId){
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);


        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req){
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        //模糊匹配
        PageHelper.startPage(req.getPage(),req.getSize());//只对第一个遇到的sql起作用  1.页码 2.每页的条数
        List<Doc> docList = docMapper.selectByExample(docExample);
        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数:{}",pageInfo.getTotal());
        LOG.info("总页数:{}",pageInfo.getPages());
//        List<DocResp> respList = new ArrayList<>();
//        for(Doc doc : docList){
//            //DocResp docResp = new DocResp();
//            //BeanUtils.copyProperties(doc, docResp);
              //对象复制
//            DocResp docResp = CopyUtil.copy(doc, DocResp.class);
//            respList.add(docResp);
//        }


        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /*
    保存
     */
    public void save(DocSaveReq req){
        Doc doc = CopyUtil.copy(req, Doc.class);//复制的时候只会去找对应的字段复制 不对应的字段就不复制
        Content content = CopyUtil.copy(req, Content.class);
        if(ObjectUtils.isEmpty(req.getId())){
            //新增
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        }else{
            // 更新
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);//blobs代表富文本字段，是大字段
            if (count == 0) {
                contentMapper.insert(content);
            }
        }
    }

    public void delete(Long id){
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids){
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public String findContent(Long id){
        Content content = contentMapper.selectByPrimaryKey(id);
        //文档阅读数+1
        docMapperCust.increaseViewCount(id);
        if(ObjectUtils.isEmpty(content)){
            return "";
        }else{
            return content.getContent();
        }
    }

    /**
     * 点赞
     */
    public void vote(Long id) {
        docMapperCust.increaseViewCount(id);
    }
}
