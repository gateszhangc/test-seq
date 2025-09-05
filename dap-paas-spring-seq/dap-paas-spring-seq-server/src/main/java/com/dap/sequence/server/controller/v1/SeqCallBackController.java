package com.dap.sequence.server.controller.v1;

import com.base.api.exception.ServiceException;
import com.dap.sequence.api.commons.Result;
import com.dap.sequence.api.commons.ResultEnum;
import com.dap.sequence.api.commons.ResultUtils;
import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.core.AbstractSeqFlowEngine;
import com.dap.sequence.api.core.SeqProducer;
import com.dap.sequence.api.dto.SeqCallbackDto;
import com.dap.sequence.server.entity.SeqDesignPo;
import com.dap.sequence.server.holder.SeqHolder;
import com.dap.sequence.server.service.SeqDesignService;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/seq/callback")
public class SeqCallBackController {
    @Resource
    private SeqDesignService seqDesignService;
    /**
     * 保存回收的序列
     * @param request
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/shutdownCallbackSeq")
    public Result shutdownCallbackSeq(HttpEntity<SeqCallbackDto> request, HttpServletRequest httpServletRequest) {
        SeqCallbackDto seqCallbackDto = request.getBody();
        String tenantId=httpServletRequest.getHeader("tenantId");
        String seqCode = StringUtils.substringBefore(seqCallbackDto.getSequenceCode(), SequenceConstant.DAY_SWITCH_SPLIT);
        //seqCallbackDto.setSequenceCode(seqCode);
        if (!checkSeqCode(seqCode ,tenantId)) {
            if (SeqHolder.getMap().get(seqCallbackDto.getSequenceCode()+SequenceConstant.TenantPostfix+tenantId) != null) {
                AbstractSeqFlowEngine seqFlowEngine = SeqHolder.getEngineMap().get(seqCode + SequenceConstant.TenantPostfix+tenantId);
                SeqProducer seqProducer = seqFlowEngine.getSeqProducer().get();
                seqProducer.loadSeq(seqCallbackDto.getSequenceCode(),seqCallbackDto.getCallbackNumQueue(),tenantId);
            } else {
                return ResultUtils.error(500, "没有找到对应的序列！、无法回收对应序号");
            }
        } else {
            return ResultUtils.error(500, "没有找到对应的序列！、无法回收对应序号");
        }
        return ResultUtils.success(ResultEnum.SUCCESS);
    }

    /**
     * 检查序列是否存在，进行校验
     *
     * @param seqCode
     */
    private boolean checkSeqCode(String seqCode,String tenantId) {
        SeqDesignPo seqDesignPo = new SeqDesignPo();
        String code = StringUtils.substringBefore(seqCode, SequenceConstant.DAY_SWITCH_SPLIT);
        seqDesignPo.setSequenceCode(code);
        seqDesignPo.setTenantId(tenantId);
        List<SeqDesignPo> designPos = new ArrayList<>();
        try {
            designPos = seqDesignService.queryList(seqDesignPo);
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        return CollectionUtils.isEmpty(designPos);
    }
}
