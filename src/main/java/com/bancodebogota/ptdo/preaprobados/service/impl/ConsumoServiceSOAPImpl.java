package com.bancodebogota.ptdo.preaprobados.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bancodebogota.accounts.product.event.CampPotentialSaleInqRqType;
import com.bancodebogota.accounts.product.service.GetCampPotentialSaleRequest;
import com.bancodebogota.accounts.product.service.GetCampPotentialSaleResponse;
import com.bancodebogota.customers.arrangement.v1.CampaignRuleType;
import com.bancodebogota.ifx.base.v1.AcctDomainListType;
import com.bancodebogota.ifx.base.v1.CustIdType;
import com.bancodebogota.ifx.base.v1.NetworkTrnInfoType;
import com.bancodebogota.ptdo.preaprobados.configuration.SOAPConnector;
import com.bancodebogota.ptdo.preaprobados.service.ConsumoServiceSOAP;

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
	
	/*@Autowired
	@Qualifier("ConsumoServiceREST")
	private ConsumoServiceREST consumoServiceREST;
	
	@Value("${com.bancodebogota.ptdo.parametro.endpointBUSInfoBasica}")
	private String pEndpointBUSInfoBasica;
	
	@Value("${com.bancodebogota.ptdo.parametro.canal}")
	private String pCanal;
	
	@Value("${com.bancodebogota.ptdo.parametro.terminalId}")
	private String pTerminalId;
	
	@Value("${com.bancodebogota.ptdo.parametro.bankId}")
	private String pBankId;*/

	/* (non-Javadoc)
	 * @see com.bancodebogota.grupo.app.service.ConsumoWSService#custBasicInfoRequest(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public GetCampPotentialSaleResponse getCampPotentialSale(String tipoDocumento,
			 String numeroDocumento,
			 String usuario) 
	{
		
		String endpoint = "http://10.85.88.126:15090/customers/ProductCampPotentialSaleInquiry";//consumoServiceREST.getParametro(pEndpointBUSInfoBasica);
		//endpoint = "http://10.87.52.23:10088/customers/ProductCampPotentialSaleInquiry";
		
		System.out.println("Endpoint: " + endpoint);
			
		if(endpoint == null || "".equals(endpoint.trim()))
			throw new RuntimeException("El endpoint no se encuentra parametrizado");
		
		String canal = "PTDO";//consumoServiceREST.getParametro(pCanal);
		String bankId = "001";//consumoServiceREST.getParametro(pBankId);
		
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
				
		return  (GetCampPotentialSaleResponse) soapConnector.callWebService(endpoint, request);
		
	}
	
}
