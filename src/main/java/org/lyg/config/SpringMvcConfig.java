package org.lyg.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author :lyg
 * @time :2018/7/28 0028
 */
/*<!-- 上传文件 -->
    <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
        <property name="defaultEncoding" value="utf-8"/>
        <!-- 最大内存大小 -->
        <property name="maxInMemorySize" value="10240"/>
        <!-- 最大文件大小，-1为不限制大小 -->
        <property name="maxUploadSize" value="-1"/>
    </bean>*/
@Configuration
@ImportResource("classpath:bean.xml")
public class SpringMvcConfig{

}
