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
		marshaller.setPackagesToScan("com.bancodebogota");
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
