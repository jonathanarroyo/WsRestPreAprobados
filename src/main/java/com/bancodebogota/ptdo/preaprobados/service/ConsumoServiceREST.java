package com.bancodebogota.ptdo.preaprobados.service;

/**
 * © Todos los derechos reservados Banco de Bogotá
 * <p>
 * Interface para el consumo del servicio REST
 * 
 * @author Stiven Diaz
 * 
 */
public interface ConsumoServiceREST {

	/**
	 * Método que consume el servicio REST {/parametro}, el
	 * método {/} y retorna String {valor} trae el parametro en la tabla Parametros de mongodb 
	 * 
	 * @param nombre
	 * @return
	 */
	String getParametro(String nombre);
	
}
