/**
 * © Nowina Solutions, 2015-2015
 *
 * Concédée sous licence EUPL, version 1.1 ou – dès leur approbation par la Commission européenne - versions ultérieures de l’EUPL (la «Licence»).
 * Vous ne pouvez utiliser la présente œuvre que conformément à la Licence.
 * Vous pouvez obtenir une copie de la Licence à l’adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation légale ou contractuelle écrite, le logiciel distribué sous la Licence est distribué «en l’état»,
 * SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques spécifiques relevant de la Licence.
 */
package lu.nowina.nexu.server;

import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lu.nowina.nexu.server.api.ws.FeedbackEndpoint;

/**
 * Launcher for NexU Server. The Server is not involved in normal signature operation but can collect feedback from NexU install base.
 * 
 * @author David Naramski
 *
 */
@SpringBootApplication
@ComponentScan
@EnableTransactionManagement
@EnableScheduling
@ImportResource({ "classpath:META-INF/cxf/cxf.xml" })
public class NexuServerApp extends SpringBootServletInitializer {

	private static final Logger logger = LoggerFactory.getLogger(NexuServerApp.class.getName());

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(NexuServerApp.class);
		app.run();
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean(final ApplicationContext context) {
		return new ServletRegistrationBean(new CXFServlet(), "/api/v1/*");
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
