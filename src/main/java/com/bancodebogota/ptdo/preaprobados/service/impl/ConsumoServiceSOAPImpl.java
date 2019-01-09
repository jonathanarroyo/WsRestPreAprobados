package com.bancodebogota.ptdo.preaprobados.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import javax.xml.ws.soap.SOAPFaultException;

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
import com.bancodebogota.ptdo.preaprobados.component.ObjectUtils;
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
	
	private final static Logger log = Logger.getLogger(ConsumoServiceSOAPImpl.class.getName());
		
	@Autowired
	private SOAPConnector soapConnector;
	
	@Autowired
	@Qualifier("ConsumoServiceREST")
	private ConsumoServiceREST consumoServiceREST;
	
	@Autowired
	@Qualifier("objectUtils")
	private ObjectUtils objectUtils;
	
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
		tipoProducto.put("CDT","CDT`S");
		tipoProducto.put("FID","FIDUCIARIA");
		tipoProducto.put("SEG","SEGURO");
		tipoProducto.put("TCR","TARJETA CREDITO");
		tipoProducto.put("TDB","TARJETA DEBITO");
	}

	
	/* (non-Javadoc)
	 * @see com.bancodebogota.ptdo.preaprobados.service.ConsumoServiceSOAP#getCampPotentialSale(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public GetCampPotentialSaleResponse getCampPotentialSale(String tipoDocumento,
			 String numeroDocumento,
			 String usuario,
			 String endpoint) 
	{
		if("".equals(endpoint))
			endpoint = consumoServiceREST.getParametro(pEndpointBUSPreAprobados);
		//String endpoint =  "http://10.85.88.126:15090/customers/ProductCampPotentialSaleInquiry";// externo desarrollo
		//String endpoint =  "http://10.87.52.23:10088/customers/ProductCampPotentialSaleInquiry";// interno desarrollo
			
		if(endpoint == null || "".equals(endpoint.trim()))
			throw new RuntimeException("El endpoint no se encuentra parametrizado");
		
		//String canal = consumoServiceREST.getParametro(pCanal);
		//String bankId = consumoServiceREST.getParametro(pBankId);
		
		log.warning(endpoint);

		String canal = "PTDO";
		String bankId = "001";
		
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
		
		log.warning(campPotentialSale.getCustId().getCustPermId());

		GetCampPotentialSaleRequest request = new GetCampPotentialSaleRequest();
		request.setCampPotentialSaleInqRq(campPotentialSale);
				
		GetCampPotentialSaleResponse response = new GetCampPotentialSaleResponse();
		
		try {
			
			log.warning(objectUtils.obtenerTramaSoapDesdeObjeto(request));
			
			response = (GetCampPotentialSaleResponse) soapConnector.callWebService(endpoint, request);
			
			log.warning(objectUtils.obtenerTramaSoapDesdeObjeto(response));
			
		} catch (Exception excepcion) {
	        String statusCode = null;
	        String statusDesc = null;
	        String faultCode = null;
	        String faultString = null;
	        Object faultObject = null;
            if (excepcion instanceof SOAPFaultException)
            {
                SOAPFaultException spe = (SOAPFaultException) excepcion;
                faultCode = spe.getFault().getFaultCode();
                faultString = spe.getFault().getFaultString();
                try
                {
                    statusCode = objectUtils.obtenerValorPorExpresion(spe.getFault(), "//*/*[local-name()='GeneralException']/*[local-name()='Status']/*[local-name()='StatusCode']");
                    if (statusCode.length() == 0 || statusDesc.length() == 0)
                    {
                        throw spe;
                    }
                    faultObject = objectUtils.obtenerValorPorExpresion(spe.getFault(), "//*/*[local-name()='GeneralException']");
                }
                catch (Exception egppe)
                {
                    statusCode = spe.getFault().getFaultCode();
                    statusDesc = spe.getFault().getFaultString();
                    faultObject = spe.getFault();
                }
            }
            else
            {
                faultCode = excepcion.getClass().getName();
                faultString = excepcion.getMessage();
                faultObject = excepcion.getStackTrace();
                statusCode = "VSC001";
                statusDesc = faultString;
            }
            log.warning(objectUtils.obtenerValoresDesdeObjeto(faultObject))
		}
				
		
		for(CampPotentialSaleInqType potential : response.getCampPotentialSaleInqRs().getCampPotentialSaleInq()) {
			for(ProductPotentialSaleType product : potential.getProductPotentialSale()) {
				if(tipoProducto.containsKey(product.getAcctDomain()))
					product.setAcctDomain(tipoProducto.get(product.getAcctDomain()));
			}
		}
		
		return response;
		
	}
	
}
