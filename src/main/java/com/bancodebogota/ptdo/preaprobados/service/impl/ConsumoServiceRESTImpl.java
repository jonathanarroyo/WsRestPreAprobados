package com.bancodebogota.ptdo.preaprobados.service.impl;

import java.io.IOException;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bancodebogota.ptdo.preaprobados.model.Parametro;
import com.bancodebogota.ptdo.preaprobados.service.ConsumoServiceREST;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * © Todos los derechos reservados Banco de Bogotá
 * <p>
 * Class para el consumo del servicio REST
 * implementa {ConsumoServiceREST}
 * 
 * @author Stiven Diaz
 * 
 */
@Service("ConsumoServiceREST")
public class ConsumoServiceRESTImpl implements ConsumoServiceREST {

	@Value("${com.bancodebogota.ptdo.rest.endpoint.wsparametro}")
	private String wsparametro;
	
	/* (non-Javadoc)
	 * @see com.bancodebogota.ptdo.productos.service.ConsumoServiceREST#getParametro(java.lang.String)
	 */
	@Override
	public String getParametro(String nombre) {
		
		Parametro parametro = new Parametro();
		Response response = null;
		String strResponse = null;
		ObjectMapper mapper = null;
		String urlWSRestDevices = null;

		urlWSRestDevices = String.format(wsparametro, nombre);

		System.out.println("Inicio consumo parametria: " + urlWSRestDevices);
		
		response = ClientBuilder.newBuilder()
				.register(JacksonJsonProvider.class)
				.build()
				.target(urlWSRestDevices)
				.request(MediaType.APPLICATION_JSON)
				.get();
		
		System.out.println("Realizo el consumo parametria");
		System.out.println(response.getStatus());
		
		if (response.getStatus() != 200) {
			throw new RuntimeException(
					String.format("Failed : HTTP error code %s - %s", response.getStatus(), response.getStatusInfo()));

		}
		
		strResponse = response.readEntity(String.class);
		mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		try {
			parametro = mapper.readValue(strResponse, Parametro.class);
			System.out.println(parametro);
			return parametro.getValor();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Fin consumo parametria");
		return parametro.getValor();
	}
}
