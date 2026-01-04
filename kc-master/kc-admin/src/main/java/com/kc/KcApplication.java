package com.kc;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author kc
 */

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class KcApplication implements InitializingBean {

    public static void main(String[] args)
    {
        SpringApplication.run(KcApplication.class, args);
        System.out.println("启动成功");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("////////////////////////////////////////////////////////////////////\n" +
                "//                           快仓-pmo                             //\n" +
                "////////////////////////////////////////////////////////////////////");
    }
}
