package com.bancodebogota.ptdo.preaprobados.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bancodebogota.accounts.product.event.CampPotentialSaleInqRqType;
import com.bancodebogota.accounts.product.service.GetCampPotentialSaleRequest;
import com.bancodebogota.accounts.product.service.GetCampPotentialSaleResponse;
import com.bancodebogota.customers.arrangement.v1.CampaignRuleType;
import com.bancodebogota.ifx.base.v1.AcctDomainListType;
import com.bancodebogota.ifx.base.v1.CustIdType;
import com.bancodebogota.ifx.base.v1.NetworkTrnInfoType;
import com.bancodebogota.ptdo.preaprobados.configuration.SOAPConnector;
import com.bancodebogota.ptdo.preaprobados.service.ConsumoServiceREST;
import com.bancodebogota.ptdo.preaprobados.service.ConsumoServiceSOAP;

import messaging.customers.entities.arrangement.CampPotentialSaleInqType;
import messaging.customers.entities.product.ProductPotentialSaleType;

/**
 * © Todos los derechos reservados Banco de Bogotá
 * <p>
 * Class para el consumo del servicio SOAP
 * implementa {ConsumoServiceSOAP}
 * 
 * @author Stiven Diaz
 * 
 */
@Service("consumoServiceSOAP")
public class ConsumoServiceSOAPImpl implements ConsumoServiceSOAP {
	
	@Autowired
	private SOAPConnector soapConnector;
	
	@Autowired
	@Qualifier("ConsumoServiceREST")
	private ConsumoServiceREST consumoServiceREST;
	
	@Value("${com.bancodebogota.ptdo.parametro.endpointBUSPreAprobados}")
	private String pEndpointBUSPreAprobados;
	
	@Value("${com.bancodebogota.ptdo.parametro.canal}")
	private String pCanal;
	
	@Value("${com.bancodebogota.ptdo.parametro.terminalId}")
	private String pTerminalId;
	
	@Value("${com.bancodebogota.ptdo.parametro.bankId}")
	private String pBankId;
	
	private HashMap<String, String> tipoProducto;
	
	private void poblarHasMap() {
		tipoProducto = new HashMap<String, String>();
		tipoProducto.put("ACC","ACCIONES");
		tipoProducto.put("AFC","CUENTAS AFC");
		tipoProducto.put("CAH","CTA AHORROS");
		tipoProducto.put("CAR_ME","CARTERA MONEDA EXTRANJERA");
		tipoProducto.put("CAR_ML","CARTERA MONEDA LEGAL");
		tipoProducto.put("CARDIF","SEGURO CARDIF");
		tipoProducto.put("CCT","CTA CORRIENTE");
		//tipoProducto.put("CDT","CDT`S");
		tipoProducto.put("FID","FIDUCIARIA");
		tipoProducto.put("SEG","SEGURO");
		tipoProducto.put("TCR","TARJETA CREDITO");
		tipoProducto.put("TDB","TARJETA DEBITO");
	}

	/* (non-Javadoc)
	 * @see com.bancodebogota.grupo.app.service.ConsumoWSService#custBasicInfoRequest(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public GetCampPotentialSaleResponse getCampPotentialSale(String tipoDocumento,
			 String numeroDocumento,
			 String usuario) 
	{
		
		String endpoint = consumoServiceREST.getParametro(pEndpointBUSPreAprobados);
		// http://10.85.88.126:15090/customers/ProductCampPotentialSaleInquiry externo desarrollo
		// http://10.87.52.23:10088/customers/ProductCampPotentialSaleInquiry interno desarrollo
			
		if(endpoint == null || "".equals(endpoint.trim()))
			throw new RuntimeException("El endpoint no se encuentra parametrizado");
		
		System.out.println(endpoint);
		
		String canal = consumoServiceREST.getParametro(pCanal);
		String bankId = consumoServiceREST.getParametro(pBankId);

		this.poblarHasMap();
		
		CustIdType custId = new CustIdType();
		custId.setCustType(tipoDocumento);
		custId.setCustPermId(numeroDocumento);

		NetworkTrnInfoType network = new NetworkTrnInfoType();	
		network.setNetworkOwner(canal);
		network.setBankId(bankId);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String clientDt = sdf.format(new Date());
		
		CampaignRuleType campaignRule = new CampaignRuleType();
		campaignRule.setRefType("");
		campaignRule.setRefId("");	
		campaignRule.setAcctDomainList(new AcctDomainListType());

		CampPotentialSaleInqRqType campPotentialSale = new CampPotentialSaleInqRqType();
		campPotentialSale.setRqUID(UUID.randomUUID().toString());
		campPotentialSale.setCustId(custId);
		campPotentialSale.setNetworkTrnInfo(network);
		campPotentialSale.setClientDt(clientDt);
		campPotentialSale.setCampaignRule(campaignRule);

		GetCampPotentialSaleRequest request = new GetCampPotentialSaleRequest();
		request.setCampPotentialSaleInqRq(campPotentialSale);
				
		GetCampPotentialSaleResponse response = (GetCampPotentialSaleResponse) soapConnector.callWebService(endpoint, request);
		
		for(CampPotentialSaleInqType potential : response.getCampPotentialSaleInqRs().getCampPotentialSaleInq()) {
			for(ProductPotentialSaleType product : potential.getProductPotentialSale()) {
				if(tipoProducto.containsKey(product.getAcctDomain()))
					product.setAcctDomain(tipoProducto.get(product.getAcctDomain()));
			}
		}
		
		return response;
		
	}
	
}
