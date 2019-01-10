package com.bancodebogota.ptdo.preaprobados.configuration;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

/**
 * © Todos los derechos reservados Banco de Bogotá
 * <p>
 * Class que realiza la conexion SOAP
 * 
 * @author Stiven Diaz
 * 
 */
public class SOAPConnector extends WebServiceGatewaySupport {
 
	/**
     * Método para el consumo del servicio SOAP, por medio del endpoint {url}
     * y el {resquest}.
     * 
     * @param url
     * @param request
     * @return
     */
    public Object callWebService(String url, Object request){
        return getWebServiceTemplate().marshalSendAndReceive(url, request);
    }
    
}