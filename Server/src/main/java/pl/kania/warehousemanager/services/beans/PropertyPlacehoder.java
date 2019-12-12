package pl.kania.warehousemanager.services.beans;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.properties")
public class PropertyPlacehoder extends PropertySourcesPlaceholderConfigurer {
}
