package com.dap.sequence.server.shutdown;

import com.base.api.exception.DaoException;
import com.base.api.util.SnowflakeIdWorker;
import com.dap.sequence.api.constant.SequenceConstant;
import com.dap.sequence.api.expection.SequenceException;
import com.dap.sequence.server.entity.SeqDesignPo;
import com.dap.sequence.server.entity.SeqRecycleInfo;
import com.dap.sequence.server.holder.SeqHolder;
import com.dap.sequence.server.service.SeqDesignService;
import com.dap.sequence.server.service.SeqRecycleInfoService;
import com.dap.sequence.server.utils.EnvUtils;
import com.dap.sequence.server.utils.NetUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static com.dap.sequence.api.constant.SequenceConstant.DAY_SWITCH_SPLIT;
import static com.dap.sequence.api.constant.SequenceConstant.TenantPostfix;

@Component
public class ShutdownApplication  implements InitializingBean {
    @Autowired
    private SeqDesignService seqDesignService;
    @Autowired
    private SeqRecycleInfoService seqRecycleInfoService;
    @Override
    public void afterPropertiesSet() throws Exception {

        /**
         * 回收对应序列号吗
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    ConcurrentHashMap<String, BlockingQueue<Object>> map = SeqHolder.getMap();
                    Iterator<String> it = map.keySet().iterator();
                    while (it.hasNext()) {
                        String seqCode = it.next();

                        BlockingQueue<Object> sequenceQueue = map.get(seqCode);

                        if (sequenceQueue != null && sequenceQueue != null && sequenceQueue.size() > 0) {
                            List<SeqRecycleInfo> seqRecycleInfos = new ArrayList<>();
                            List<Object> list = new ArrayList<>();
                            sequenceQueue.drainTo(list);
                            String sequenceCode = StringUtils.substringBefore(seqCode, SequenceConstant.DAY_SWITCH_SPLIT);
                            String tenantId = StringUtils.substringAfterLast(seqCode , TenantPostfix);
                            String rqDay = StringUtils.substringBetween(seqCode, SequenceConstant.DAY_SWITCH_SPLIT , TenantPostfix);
                            Map designMap = new HashMap();
                            designMap.put("tenantId",tenantId);
                            designMap.put("seqCode",sequenceCode);
                            SeqDesignPo seqDesignPo = seqDesignService.getObjectByMap(designMap);

                            for (Object seq : list) {
                                //存储数据库刷库
                                System.out.println(seqCode+"=="+seq);
                                SeqRecycleInfo seqRecycleInfo = new SeqRecycleInfo();
                                seqRecycleInfo.setTenantId(tenantId);
                                seqRecycleInfo.setRqDay(rqDay);
                                seqRecycleInfo.setSeqDesignId(seqDesignPo.getId());
                                seqRecycleInfo.setRecycleCode(seq.toString());
                                seqRecycleInfo.setCreateDate(new Date());
                                seqRecycleInfo.setId(SnowflakeIdWorker.getID());
                                seqRecycleInfo.setIp(NetUtils.ip());
                                seqRecycleInfo.setSeqCode(sequenceCode);
                                seqRecycleInfos.add(seqRecycleInfo);
                            }
                            seqRecycleInfoService.insertBatch(seqRecycleInfos);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
