package com.bancodebogota.ptdo.preaprobados.service;

import com.bancodebogota.accounts.product.service.GetCampPotentialSaleResponse;

/**
 * © Todos los derechos reservados Banco de Bogotá
 * <p>
 * Interface para el consumo del servicio SOAP
 * 
 * @author Stiven Diaz
 * 
 */
public interface ConsumoServiceSOAP {
	
	/**
	 * Método que consume el servicio SOAP {CustomerInformationInquiry}, el método {GetCustBasicInfoRequest} 
	 * y retorna el response {GetCustBasicInfoResponse}
	 * 
	 * @param tipoDocumento
	 * @param numeroDocumento
	 * @param usuario
	 * @return
	 */
	GetCampPotentialSaleResponse getCampPotentialSale(String tipoDocumento, String numeroDocumento, String usuario, String endpoint);

}
