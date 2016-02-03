package lu.nowina.nexu.server.config;

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
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.ws.config.annotation.EnableWs;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import lu.nowina.nexu.server.api.ws.FeedbackEndpoint;

@Configuration
@ComponentScan(basePackages = { "lu.nowina.nexu.server" })
@EnableWebMvc
@EnableWs
@ImportResource({ "classpath:META-INF/cxf/cxf.xml", "classpath:META-INF/cxf/cxf-servlet.xml" })
public class WebConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private ApplicationContext applicationContext;
	
	private boolean debug = false;
	
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		String[] resources= {"classpath:messages"};
		messageSource.setBasenames(resources);
		messageSource.setFallbackToSystemLocale(false);
		return messageSource;
	}

	@Bean
	public ServletContextTemplateResolver defaultTemplateResolver() {
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
		templateResolver.setPrefix("/WEB-INF/html/");
		templateResolver.setSuffix(".html");
		templateResolver.setCacheable(false);
		return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(defaultTemplateResolver());
		return templateEngine;
	}

	@Bean
	public ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		return viewResolver;
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/public/**").addResourceLocations("/webjars/");
		registry.addResourceHandler("/script/**").addResourceLocations("/script/");
		registry.addResourceHandler("/style/**").addResourceLocations("/style/");
		registry.addResourceHandler("/controllers/**").addResourceLocations("/controllers/");
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