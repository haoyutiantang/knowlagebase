package com.haoyu.knowlagebase.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.haoyu.knowlagebase.domain.Category;
import com.haoyu.knowlagebase.domain.CategoryExample;
import com.haoyu.knowlagebase.mapper.CategoryMapper;
import com.haoyu.knowlagebase.req.CategoryQueryReq;
import com.haoyu.knowlagebase.req.CategorySaveReq;
import com.haoyu.knowlagebase.resp.CategoryQueryResp;
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
public class CategoryService {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<CategoryQueryResp> all(){
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);


        //列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        return list;
    }

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req){
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        //模糊匹配
        PageHelper.startPage(req.getPage(),req.getSize());//只对第一个遇到的sql起作用  1.页码 2.每页的条数
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        LOG.info("总行数:{}",pageInfo.getTotal());
        LOG.info("总页数:{}",pageInfo.getPages());
//        List<CategoryResp> respList = new ArrayList<>();
//        for(Category category : categoryList){
//            //CategoryResp categoryResp = new CategoryResp();
//            //BeanUtils.copyProperties(category, categoryResp);
              //对象复制
//            CategoryResp categoryResp = CopyUtil.copy(category, CategoryResp.class);
//            respList.add(categoryResp);
//        }


        //列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);
        PageResp<CategoryQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /*
    保存
     */
    public void save(CategorySaveReq req){
        Category category = CopyUtil.copy(req, Category.class);
        if(ObjectUtils.isEmpty(req.getId())){
            //新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }else{
            //更新
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    public void delete(Long id){
        categoryMapper.deleteByPrimaryKey(id);
    }
}
