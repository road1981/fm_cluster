package com.cloud.fmnode.startup;



import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({Bios.class})
public class EnvConfig {


}
