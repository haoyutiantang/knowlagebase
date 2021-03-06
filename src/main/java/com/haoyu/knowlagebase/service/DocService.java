package com.haoyu.knowlagebase.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.haoyu.knowlagebase.domain.Content;
import com.haoyu.knowlagebase.domain.Doc;
import com.haoyu.knowlagebase.domain.DocExample;
import com.haoyu.knowlagebase.exception.BusinessException;
import com.haoyu.knowlagebase.exception.BusinessExceptionCode;
import com.haoyu.knowlagebase.mapper.ContentMapper;
import com.haoyu.knowlagebase.mapper.DocMapper;
import com.haoyu.knowlagebase.mapper.DocMapperCust;
import com.haoyu.knowlagebase.req.DocQueryReq;
import com.haoyu.knowlagebase.req.DocSaveReq;
import com.haoyu.knowlagebase.resp.DocQueryResp;
import com.haoyu.knowlagebase.resp.PageResp;
import com.haoyu.knowlagebase.util.CopyUtil;
import com.haoyu.knowlagebase.util.RedisUtil;
import com.haoyu.knowlagebase.util.RequestContext;
import com.haoyu.knowlagebase.util.SnowFlake;
import com.haoyu.knowlagebase.websocket.WebSocketServer;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Resource
    public RedisUtil redisUtil;

    @Resource
    public WsService wsService;

//    @Resource
//    private RocketMQTemplate rocketMQTemplate;

    public List<DocQueryResp> all(Long ebookId){
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);


        //????????????
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req){
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        //????????????
        PageHelper.startPage(req.getPage(),req.getSize());//????????????????????????sql?????????  1.?????? 2.???????????????
        List<Doc> docList = docMapper.selectByExample(docExample);
        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("?????????:{}",pageInfo.getTotal());
        LOG.info("?????????:{}",pageInfo.getPages());
//        List<DocResp> respList = new ArrayList<>();
//        for(Doc doc : docList){
//            //DocResp docResp = new DocResp();
//            //BeanUtils.copyProperties(doc, docResp);
              //????????????
//            DocResp docResp = CopyUtil.copy(doc, DocResp.class);
//            respList.add(docResp);
//        }


        //????????????
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /*
    ??????
     */
    @Transactional //???????????????????????????????????????, Transactional????????????????????????????????????????????????
    public void save(DocSaveReq req){
        Doc doc = CopyUtil.copy(req, Doc.class);//???????????????????????????????????????????????? ??????????????????????????????
        Content content = CopyUtil.copy(req, Content.class);
        if(ObjectUtils.isEmpty(req.getId())){
            //??????
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        }else{
            // ??????
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);//blobs????????????????????????????????????
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
        //???????????????+1
        docMapperCust.increaseViewCount(id);
        if(ObjectUtils.isEmpty(content)){
            return "";
        }else{
            return content.getContent();
        }
    }

    /**
     * ??????
     */
    public void vote(Long id) {
        // docMapperCust.increaseVoteCount(id);
        // ??????IP+doc.id??????key???24?????????????????????
        String ip = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 1)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
        // ????????????
        Doc docDb = docMapper.selectByPrimaryKey(id);
        String logId = MDC.get("LOG_ID");
        wsService.sendInfo("???" + docDb.getName() + "???????????????", logId);
        //rocketMQTemplate.convertAndSend("VOTE_TOPIC","???" + docDb.getName() + "???????????????");
    }

    public void updateEbookInfo(){
        docMapperCust.updateEbookInfo();
    }
}
