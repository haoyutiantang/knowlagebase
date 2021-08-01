package com.haoyu.knowlagebase.controller;

import com.haoyu.knowlagebase.domain.Ebook;
import com.haoyu.knowlagebase.req.EbookReq;
import com.haoyu.knowlagebase.resp.CommonResp;
import com.haoyu.knowlagebase.resp.EbookResp;
import com.haoyu.knowlagebase.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/*
 * @author haoyu
 * @date 2021/7/14 23:39
 */
@RestController
@RequestMapping("/ebook")
public class EbookController {
    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(EbookReq req){
        CommonResp<List<EbookResp>> resp = new CommonResp<>();
        List<EbookResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
}
