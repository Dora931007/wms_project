package com.wms.entity;

import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Record对象", description="")
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    @Alias("序号")
    private Integer id;

    @ApiModelProperty(value = "物品")
    private Integer goods;

    @ApiModelProperty(value = "取货人/补货人")
    private Integer userId;

    @ApiModelProperty(value = "操作人id")
    private Integer adminId;

    @ApiModelProperty(value = "数量")
    @Alias("物品数量")
    private Integer count;

    @ApiModelProperty(value = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Alias("操作时间")
    private LocalDateTime createtime;

    @ApiModelProperty(value = "备注")
    @Alias("备注")
    private String remark;

    @ApiModelProperty(value = "行为")
    private String action;

    @ApiModelProperty(value = "申请人")
    @TableField(exist = false)
    @Alias("申请人")
    private String username;

    @ApiModelProperty(value = "操作人")
    @TableField(exist = false)
    @Alias("操作人")
    private String adminname;

    @ApiModelProperty(value = "物品名称")
    @TableField(exist = false)
    @Alias("物品名称")
    private String goodsname;

    @ApiModelProperty(value = "所属仓库")
    @TableField(exist = false)
    @Alias("所属仓库")
    private String storagename;

    @ApiModelProperty(value = "所属分类")
    @TableField(exist = false)
    @Alias("所属分类")
    private String goodstype;

}
