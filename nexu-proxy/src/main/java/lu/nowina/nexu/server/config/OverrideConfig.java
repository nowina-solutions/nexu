package lu.nowina.nexu.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Petit hack qui s'assure que "xxx.properties" est bien chargé avant "xxx-custom.properties"
 * 
 * @author david.naramski
 *
 */
@Configuration
@PropertySource(value = "classpath:" + ServiceConfig.PROPERTIES_FILE + "-custom.properties", ignoreResourceNotFound = true)
public class OverrideConfig {

	@Configuration
	@Import(ServiceConfig.class)
	static class InnerConfiguration {

	}

}
