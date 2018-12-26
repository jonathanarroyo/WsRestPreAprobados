package com.bancodebogota.ptdo.preaprobados.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * © Todos los derechos reservados Banco de Bogotá
 * <p>
 * Class para la configuración de la conexión SOAP
 * 
 * @author Stiven Diaz
 * 
 */
@Configuration
public class SOAPConfiguration {
	/**	
	 * Método para la configuración del marshaller del servicio SOAP actual
	 * 
	 * @return
	 */
	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		// this is the package name specified in the <generatePackage> specified in
		// pom.xml

		//marshaller.setContextPath("com.bancodebogota.accounts.product.service");
		
		/*String[] contextPaths = {
				"com.bancodebogota.accounts.product.event",
				"com.bancodebogota.accounts.product.service",
				"com.bancodebogota.creditcard.product.v1",
				"com.bancodebogota.customers.arrangement.v1",
				"com.bancodebogota.customers.condition.v1",
				"com.bancodebogota.customers.involvedparty.v1",
				"com.bancodebogota.ifx.base.v1",
				"com.bancodebogota.security.involvedparty.v1",
				"messaging.customers.entities.arrangement",
				"messaging.customers.entities.product"
				};
		
		marshaller.setContextPaths(contextPaths);*/
		marshaller.setContextPath("com.bancodebogota.accounts.product.service");
		return marshaller;
	}

	/**
	 * Método para agregar el marshaller en el servicio SOAP
	 * 
	 * @param marshaller
	 * @return
	 */
	@Bean
	public SOAPConnector soapConnector(Jaxb2Marshaller marshaller) {
		SOAPConnector client = new SOAPConnector();
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}
}
