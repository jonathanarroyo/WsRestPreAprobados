package com.bancodebogota.ptdo.preaprobados.model;

/**
 * © Todos los derechos reservados Banco de Bogotá
 * <p>
 * Class para el manejo del parametro requerido
 * 
 * @author Stiven Diaz
 * 
 */
public class Parametro {

	private String nombre;
	private String valor;

	public Parametro() {

	}

	public Parametro(String nombre, String valor) {
		this.nombre = nombre;
		this.valor = valor;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

}
