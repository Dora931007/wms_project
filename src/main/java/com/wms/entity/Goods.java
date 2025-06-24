package com.wms.entity;

import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Goods对象", description = "")
public class Goods implements Serializable {

    //Java序列化的版本号，通过显式声明来控制序列化的兼容性
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    @Alias("序号")
    private Integer id;

    @ApiModelProperty(value = "物品名称")
    @Alias("物品名称")
    private String name;

    @ApiModelProperty(value = "所属仓库")
    private Integer storage;

    @ApiModelProperty(value = "所属分类")
    @TableField("goodsType")
    private Integer goodstype;

    @ApiModelProperty(value = "物品数量")
    @Alias("物品数量")
    private Integer count;

    @ApiModelProperty(value = "备注")
    @Alias("备注")
    private String remark;

    @ApiModelProperty(value = "所属仓库名称")
    @TableField(exist = false) // 不是数据库字段
    @Alias("所属仓库")
    private String storageName;

    @ApiModelProperty(value = "所属分类名称")
    @TableField(exist = false)
    @Alias("所属分类")
    private String goodsTypeName;

}
