package lu.nowina.nexu.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(basePackages = { "lu.nowina.nexu.server.business" })
@PropertySource("classpath:" + ServiceConfig.PROPERTIES_FILE + ".properties")
public class ServiceConfig {

	public static final String PROPERTIES_FILE = "nexu-proxy";

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}