package com.dap.sequence.client.snow;

import com.dap.sequence.client.utils.OSUtils;
import com.dap.sequence.client.utils.WorkIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Snowflake生成器
 *
 * @author zpj
 */
public class SnowflakeGenerator  {

    private static final Logger logger = LoggerFactory.getLogger(SnowflakeGenerator.class);

    private static Snowflake instance = null;

    private static Integer workId = null;

    private SnowflakeGenerator() {
    }

    /**
     * 获取Snowflake
     *
     * @return Snowflake实例
     */
    public static synchronized Snowflake getInstance() {
        if (instance == null) {
            synchronized (SnowflakeGenerator.class){
                //从application配置文件获取dap.sequence.workerId
                workId = WorkIdUtil.wordIdFromConfig();
                //从序列服务端获取
                if(workId == null){
                    workId = WorkIdUtil.wordIdFromSeqServer();
                }
                //兜底方案 默认构造的终端ID和数据中心ID都为0，不适用于分布式环境。
                if (workId == null) {
                    workId = OSUtils.getWorkId();
                    //提示信息
                    logger.warn("Because current application can not get workId from application config key " +
                            "[dap.sequence.workerId] or sequence server, so use default workId:{}!!!",workId);
                }
                long dataCenterId = (SnowflakeFile.readerClock() + 1) & Snowflake.maxDataCenterId;
                SnowflakeFile.writerClock(dataCenterId);
                instance = new Snowflake(workId, dataCenterId);
            }
        }
        return instance;
    }

    /**
     * 获取雪花算法Id
     *
     * @return ID
     */
    public static Long nextId() {
        return getInstance().nextId();
    }

    /**
     * 获取雪花算法Id
     *
     * @return ID
     */
    public static String nextIdStr() {
        return getInstance().nextIdStr();
    }

    /**
     * 获取当前使用雪花算法的workId
     *
     * @return ID
     */
    public static Integer getWorkId() {
        return workId;
    }

}