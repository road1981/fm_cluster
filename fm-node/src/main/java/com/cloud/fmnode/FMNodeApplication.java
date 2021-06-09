package com.cloud.fmnode;

import com.cloud.fmnode.config.FlyMoreClusterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableDiscoveryClient
@SpringBootApplication
public class FMNodeApplication {
    public static void main(String[] args) {

        FlyMoreClusterConfig.initClusterOpt(args);

        SpringApplication.run(FMNodeApplication.class, args);
    }
}
