package com.haoyu.knowlagebase.controller;

import com.haoyu.knowlagebase.req.DocQueryReq;
import com.haoyu.knowlagebase.req.DocSaveReq;
import com.haoyu.knowlagebase.resp.DocQueryResp;
import com.haoyu.knowlagebase.resp.CommonResp;
import com.haoyu.knowlagebase.resp.PageResp;
import com.haoyu.knowlagebase.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/*
 * @author haoyu
 * @date 2021/7/14 23:39
 */
@RestController
@RequestMapping("/doc")
public class DocController {
    @Resource
    private DocService docService;

    @GetMapping("/all")
    public CommonResp all(){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all();
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq req){
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req){
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        docService.delete(id);
        return resp;
    }

}
