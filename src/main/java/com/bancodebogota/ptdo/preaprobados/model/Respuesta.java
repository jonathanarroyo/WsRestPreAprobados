package com.bancodebogota.ptdo.preaprobados.model;

import com.bancodebogota.accounts.product.service.GetCampPotentialSaleResponse;

/**
 * © Todos los derechos reservados Banco de Bogotá
 * <p>
 * Class para el response del servicio REST
 * estado: si es diferente a 0 es un error
 * descripcion: detalla el estado
 * 
 * @author Stiven Diaz
 * 
 */
public class Respuesta {

	private Integer estado;
	private String descripcion;
	private GetCampPotentialSaleResponse getCampPotentialSaleResponse;
	
	public Respuesta() {
		
	}

	public Respuesta(Integer estado, String descripcion) {
		super();
		this.estado = estado;
		this.descripcion = descripcion;
	}

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public GetCampPotentialSaleResponse getGetCampPotentialSaleResponse() {
		return getCampPotentialSaleResponse;
	}

	public void setGetCampPotentialSaleResponse(GetCampPotentialSaleResponse getCampPotentialSaleResponse) {
		this.getCampPotentialSaleResponse = getCampPotentialSaleResponse;
	}

}
