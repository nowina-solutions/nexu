package lu.nowina.nexu.server.config;

import lu.nowina.nexu.server.api.ws.FeedbackEndpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.ws.config.annotation.EnableWs;

@Configuration
@ComponentScan(basePackages = { "lu.nowina.nexu.server" })
@EnableWs
@ImportResource({ "classpath:META-INF/cxf/cxf.xml", "classpath:META-INF/cxf/cxf-servlet.xml" })
public class WebConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private ApplicationContext applicationContext;
	
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		String[] resources= {"classpath:messages"};
		messageSource.setBasenames(resources);
		messageSource.setFallbackToSystemLocale(false);
		return messageSource;
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public EndpointImpl endpoint1() {
		final Bus bus = (Bus) this.applicationContext.getBean(Bus.DEFAULT_BUS_ID);
		final Object implementor = this.applicationContext.getBean(FeedbackEndpoint.class);
		final EndpointImpl endpoint = new EndpointImpl(bus, implementor);
		endpoint.publish("/feedback");
		endpoint.getServer().getEndpoint().getInInterceptors().add(new LoggingInInterceptor());
		endpoint.getServer().getEndpoint().getOutInterceptors().add(new LoggingOutInterceptor());
		return endpoint;
	}

}