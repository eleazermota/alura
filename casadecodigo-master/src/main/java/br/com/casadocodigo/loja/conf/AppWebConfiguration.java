package br.com.casadocodigo.loja.conf;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.h2.tools.Server;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.google.common.cache.CacheBuilder;

@EnableWebMvc
@EnableCaching
@ComponentScan( basePackages= {"br.com.casadocodigo.loja" })
public class AppWebConfiguration extends WebMvcConfigurerAdapter {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LocaleChangeInterceptor());
	}
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/css/**").addResourceLocations("/resources/css/").setCachePeriod(31556926);
        registry.addResourceHandler("/resources/images/**").addResourceLocations("/resources/imagens/").setCachePeriod(31556926);
        registry.addResourceHandler("/resources/js/**").addResourceLocations("/resources/js/").setCachePeriod(31556926);
    }
	
	@Bean
	public LocaleResolver localeResolver(){
	    return new CookieLocaleResolver();
	}

	@Bean
	public ViewResolver contentNegotiationViewResolver(ContentNegotiationManager manager){
	    List<ViewResolver> viewResolvers = new ArrayList<>();
	    viewResolvers.add(internalResourceViewResolver());
	    viewResolvers.add(new JsonViewResolver());

	    ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
	    resolver.setViewResolvers(viewResolvers);
	    resolver.setContentNegotiationManager(manager);
	    return resolver;
	}
	
	@Bean
	public CacheManager cacheManager(){
	  CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(5, TimeUnit.MINUTES);
	  GuavaCacheManager manager = new GuavaCacheManager();
	  manager.setCacheBuilder(builder);
	  return manager;
	}
	
	@Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

	@Bean
	public MultipartResolver multipartResolver() {
	    return new StandardServletMultipartResolver();
	}
	
	@Bean
	public FormattingConversionService mvcConversionService(){
	    DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
	    DateFormatterRegistrar formatterRegistrar = new DateFormatterRegistrar();
	    formatterRegistrar.setFormatter(new DateFormatter("dd/MM/yyyy"));
	    formatterRegistrar.registerFormatters(conversionService);

	    return conversionService;
	}
	
	@Bean
	public MessageSource messageSource(){
	    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	    messageSource.setBasename("/WEB-INF/messages");
	    messageSource.setDefaultEncoding("UTF-8");
	    messageSource.setCacheSeconds(1);
	    return messageSource;
	}
	
	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server h2WebConsonleServer() throws SQLException {
		return Server.createWebServer("-web", "-webAllowOthers", "-webDaemon", "-webPort", "8082");
	}

	@Bean
	public InternalResourceViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		resolver.setExposedContextBeanNames("carrinhoCompras");
		return resolver;
	}
}
