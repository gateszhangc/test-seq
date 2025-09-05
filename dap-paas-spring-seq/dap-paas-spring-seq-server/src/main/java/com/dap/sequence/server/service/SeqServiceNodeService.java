package com.dap.sequence.server.service;


import com.base.core.service.BaseService;
import com.dap.sequence.server.entity.SeqServiceNode;

/**
 * @author scalor
 * @date 2022/1/18
 */
public interface SeqServiceNodeService extends BaseService<SeqServiceNode, String> {
    SeqServiceNode getObjectByClusterName(String seqName);
}
