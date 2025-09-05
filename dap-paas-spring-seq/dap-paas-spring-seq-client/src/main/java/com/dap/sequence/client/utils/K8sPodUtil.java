package com.dap.sequence.client.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * k8s-容器信息
 *
 * @author lyf
 * @date 2024/1/12
 */
public class K8sPodUtil {

    private K8sPodUtil() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(K8sPodUtil.class);

    /** Environment param keys */
    /**
     * k8s容器内置环境变量
     */
    public static final String ENV_KEY_HOSTNAME = "HOSTNAME";
    /**
     * k8s容器内置的环境变量，可以判断是否为k8s pod
     */
    public static final String ENV_KEY_KUBERNETES_SERVICE_HOST = "KUBERNETES_SERVICE_HOST";
    /**
     * 判断当前POD属于哪个应用集群
     */
    public static final String ENV_KEY_CLUSTER_NAME = "CLUSTER_NAME";

    /**
     * pod属于哪个namespace
     */
    public static final String ENV_KEY_POD_NAMESPACE = "POD_NAMESPACE";

    /**
     * 判断当前POD属于哪个应用集群
     */
    public static final String ENV_KEY_POD_NAME = "POD_NAME";

    /**
     * 判断当前POD属于哪个应用集群
     */
    public static final String COLON_STR = ":";

    /**
     * 获取pod内置的HOSTNAME环境变量
     */
    private static String POD_INSTANCE_NAME = "";

    /**
     * 判断是不是应用运行在K8S POD中
     */
    private static boolean IS_K8S_POD = false;

    static {
        retrieveFromEnv();
    }

    /**
     * 获取运行在pod中当前应用的HOSTNAME
     *
     * @return empty string if not a docker
     */
    public static String getPodInstanceNameName() {
        return POD_INSTANCE_NAME;
    }

    /**
     * 判断是不是应用运行在K8S POD中
     *
     * @return true：运行在K8S pod中， false: 不是
     */
    public static boolean isIsK8sPod() {
        return IS_K8S_POD;
    }

    /**
     * 从环境变量中读取HOSTNAME， CLUSTER_NAME和判断是不是容器
     */
    private static void retrieveFromEnv() {
        //取内置到pod中的环境变量：CLUSTER_NAME、POD_NAMESPACE、POD_NAME，当都存在时，才使用
        if (StringUtils.isNotBlank(System.getenv(ENV_KEY_CLUSTER_NAME)) && StringUtils.isNotBlank(
                System.getenv(ENV_KEY_POD_NAMESPACE)) && StringUtils.isNotBlank(System.getenv(ENV_KEY_POD_NAME))) {
            POD_INSTANCE_NAME = System.getenv(ENV_KEY_CLUSTER_NAME) + COLON_STR +
                    System.getenv(ENV_KEY_POD_NAMESPACE) + COLON_STR + System.getenv(ENV_KEY_POD_NAME);
        } else {
            //当未配置环境变量：CLUSTER_NAME、POD_NAMESPACE、POD_NAME，取pod的hostname作为instanceName
            POD_INSTANCE_NAME = System.getenv(ENV_KEY_HOSTNAME);
        }

        LOGGER.info("getting from system env-hostname：{}", POD_INSTANCE_NAME);
        //k8s pod的会内置环境变量：KUBERNETES_SERVICE_HOST,可通过此环境变量判断是否为容器部署
        String k8sServiceHost = System.getenv(ENV_KEY_KUBERNETES_SERVICE_HOST);
        if (StringUtils.isNotBlank(k8sServiceHost)){
            IS_K8S_POD = true;
        }
    }

}
