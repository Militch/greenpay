package com.esiran.greenpay.agentpay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.esiran.greenpay.common.entity.BaseMapperEntity;
import com.esiran.greenpay.common.util.NumberUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.modelmapper.ModelMapper;

import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Militch
 * @since 2020-06-05
 */
@Data
public class PassageRiskDTO extends BaseMapperEntity {
    private static final ModelMapper modelMapper = new ModelMapper();
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final long serialVersionUID = 1L;

    /**
     * 代付通道id
     */
    private Integer passageId;

    /**
     * 代付通道名称
     */
    private String passageName;

    /**
     * 单笔代付最低金额
     */
    private Integer amountMin;
    private String amountMinDisplay;

    /**
     * 单笔代付最大金额
     */
    private Integer amountMax;
    private String amountMaxDisplay;

    /**
     * 0 关闭 1 开启
     */
    private Integer status;


    public static PassageRiskDTO convertOrderEntity(PassageRisk passageRisk){
        if (passageRisk == null) return null;
        PassageRiskDTO dto = modelMapper.map(passageRisk, PassageRiskDTO.class);
        dto.setAmountMinDisplay(NumberUtil.amountFen2Yuan(passageRisk.getAmountMin()));
        dto.setAmountMaxDisplay(NumberUtil.amountFen2Yuan(passageRisk.getAmountMax()));
        return dto;
    }

}
