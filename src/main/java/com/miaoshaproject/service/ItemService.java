package com.miaoshaproject.service;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.service.model.ItemModel;

import java.util.List;

/**
 * @author aixscode.github.io
 * @date 2019/1/8 15:25
 */
public interface ItemService {

    //创建商品
    ItemModel  createItem(ItemModel itemModel) ;



    //商品列表浏览
    List<ItemModel> listItem();

    //商品详情浏览
    ItemModel getItemById(Integer id);

    //商品减库存

    boolean decreaseStock(Integer itemId,Integer amount) throws  BussinessException;

    //商品销量增加

    void increaseSales(Integer itemId,Integer amount) throws  BussinessException;
}
