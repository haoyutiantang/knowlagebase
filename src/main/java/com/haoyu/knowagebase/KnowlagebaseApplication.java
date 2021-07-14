package com.haoyu.knowagebase;
/*
 * @author haoyu
 * @date 2021/7/14 7:23
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class KnowlagebaseApplication {

    private static final Logger LOG = LoggerFactory.getLogger(KnowlagebaseApplication.class);
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(KnowlagebaseApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！");
        LOG.info("地址：\thttp://127.0.0.1:{}", env.getProperty("server.port"));
    }

}
