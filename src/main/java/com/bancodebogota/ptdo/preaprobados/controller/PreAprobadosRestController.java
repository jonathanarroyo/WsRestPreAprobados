package com.bancodebogota.ptdo.preaprobados.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.bancodebogota.ptdo.preaprobados.model.Respuesta;
import com.bancodebogota.ptdo.preaprobados.service.ConsumoServiceSOAP;


/**
 * © Todos los derechos reservados Banco de Bogotá
 * <p>
 * Class {RestController} para la consulta de la información básica
 * 
 * @author Stiven Diaz
 * 
 */
@RestController
@RequestMapping("/preaprobados")
public class PreAprobadosRestController {
	
	@Autowired
	@Qualifier("consumoServiceSOAP")
	private ConsumoServiceSOAP consumoServiceSOAP;

	/**
	 * Método que retorna el response del servicio web SOAP de la información basica del cliente en CRM
	 * 
	 * @param usuario
	 * @param tipoDocumento
	 * @param numeroDocumento
	 * @return
	 */
	@CrossOrigin
	@GetMapping("/cliente/{tipoDocumento}/{numeroDocumento}")
	public ResponseEntity<Respuesta> getDatosCliente(@RequestHeader(value ="usuarioBBta", defaultValue ="usuario", required = false) String usuario,
			@RequestHeader(value ="endpoint", defaultValue ="", required = false) String endpoint,
			@PathVariable("tipoDocumento") String tipoDocumento,
			@PathVariable("numeroDocumento") String numeroDocumento) {

		Respuesta respuesta = new Respuesta();
		
		if ("".equals(tipoDocumento.trim()) || tipoDocumento.length() != 1) {
			respuesta.setEstado(1);
			respuesta.setDescripcion("Ingrese un tipo de documento valido.");
			return new ResponseEntity<Respuesta>(respuesta, HttpStatus.BAD_REQUEST);
		}

		if ("".equals(numeroDocumento.trim()) || numeroDocumento.length() == 0) {
			respuesta.setEstado(1);
			respuesta.setDescripcion("Ingrese un número de documento valido.");
			return new ResponseEntity<Respuesta>(respuesta, HttpStatus.BAD_REQUEST);
		}

		try {
			
			respuesta.setEstado(0);
			respuesta.setDescripcion("Transacción exitosa");
			respuesta.setGetCampPotentialSaleResponse(consumoServiceSOAP.getCampPotentialSale(tipoDocumento, numeroDocumento, usuario, endpoint));

			return new ResponseEntity<Respuesta>(respuesta, HttpStatus.OK);

		} catch (SoapFaultClientException e) {
			respuesta.setEstado(1);
			respuesta.setDescripcion(e.getMessage());
			return new ResponseEntity<Respuesta>(respuesta, HttpStatus.SERVICE_UNAVAILABLE);
		} catch (Exception e) {
			respuesta.setEstado(1);
			respuesta.setDescripcion(e.getMessage());
			return new ResponseEntity<Respuesta>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
}
