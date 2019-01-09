package com.bancodebogota.ptdo.preaprobados.component;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xml.messaging.saaj.soap.impl.TextImpl;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Component("objectUtils")
public class ObjectUtils
{
    /**
     * Realiza la conversión de un Objeto de petición o respuesta de un servicio en una Trama SOAP Válida.
     * @param obj Objeto sobre el cual se va a realizar la conversión a trama SOAP.
     * @return String que contiene la trama SOAP generada a partir del objeto enviado.
     * @throws JAXBException
     */
    public String obtenerTramaSoapDesdeObjeto(Object obj) throws JAXBException, UnsupportedEncodingException
    {
        String soapEnvelope = null;
        Marshaller marshaller = null;
        ByteArrayOutputStream outputStream = null;
        String payload = null;
        JAXBElement root = null;
        Class clazz = null;
        String namespaceURI = null;
        String localPart = null;
        Annotation annotation = null;

        clazz = obj.getClass();

        annotation = clazz.getPackage().getAnnotation(XmlSchema.class);
        namespaceURI = (annotation != null ? ((XmlSchema) annotation).namespace() : clazz.getPackage().getName());

        annotation = clazz.getAnnotation(XmlType.class);
        localPart = (annotation != null ? ((XmlType) annotation).name() : clazz.getSimpleName());

        if ("".equals(localPart.trim()))
        {
            annotation = clazz.getAnnotation(XmlRootElement.class);
            localPart = (annotation != null ? ((XmlRootElement) annotation).name() : clazz.getSimpleName());
        }

        soapEnvelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" + "<soapenv:Header /><soapenv:Body>%s</soapenv:Body></soapenv:Envelope>";
        marshaller = JAXBContext.newInstance(clazz).createMarshaller();
        marshaller.setProperty("jaxb.fragment", Boolean.TRUE); // required to stop <?xml ... being added ?>
        outputStream = new ByteArrayOutputStream();
        root = new JAXBElement(new QName(namespaceURI, localPart), clazz, obj);

        marshaller.marshal(root, outputStream);
        payload = outputStream.toString("UTF-8");        
        return prettyFormat(String.format(soapEnvelope, payload), 3);
    }

    /**
     * Realiza el formateo de una trama SOAP.
     * @param input String con la trama a organizar.
     * @param indent Número de espacios por tabulación de objeto.
     * @return Trama SOAP Formateada.
     */
    private String prettyFormat(String input, int indent)
    {
        StreamResult xmlOutput = null;
        TransformerFactory transformerFactory = null;
        Transformer transformer = null;
        try
        {
            xmlOutput = new StreamResult(new StringWriter());

            transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);

            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new StreamSource(new StringReader(input)), xmlOutput);
            return xmlOutput.getWriter().toString();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e); // simple exception handling, please review it
        }
    }

    public String obtenerValorPorExpresion(Node node, String expression) throws Exception
    {
        return (String) obtenerObjectoPorExpresion(node, expression, true);
    }

    /**
     * @param node
     * @param expression
     * @param valor
     * @return
     * @throws Exception
     */
    private Object obtenerObjectoPorExpresion(Node node, String expression, boolean valor) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document newDocument = builder.newDocument();
        Node importedNode = newDocument.importNode(node, true);
        newDocument.appendChild(importedNode);
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate(valor ? (expression + "/text()") : expression, newDocument.getDocumentElement(), XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); ++i)
        {
            if (valor)
            {
                TextImpl e = (TextImpl) nodes.item(i);
                return e.getData();
            }
            else
            {
                return nodes.item(i);
            }
        }
        return "";
    }

    /**
     * Permite obtener los campos de un objeto y sus valores
     * @param object Objeto del cual se obtendran sus campos
     */
    public String obtenerValoresDesdeObjeto(Object object)
    {
        XStream xStream;
        xStream = new XStream(new DomDriver());
        return new StringBuilder(xStream.toXML(object)).toString().trim();
    }

    /**
     *
     * @param port
     * @return
     */
    public String obtenerNombreServicio(Object port)
    {
        String nombreServicio = "<no service>";
        try
        {
            W3CEndpointReference endpoint = (W3CEndpointReference) port.getClass().getMethod("getEndpointReference").invoke(port);
            Field f0 = endpoint.getClass().getDeclaredField("address");
            f0.setAccessible(true);
            Object address = f0.get(endpoint);
            Field f1 = address.getClass().getDeclaredField("uri");
            f1.setAccessible(true);
            String uri = (String) f1.get(address);
            nombreServicio = uri.substring(uri.lastIndexOf("/") + 1);
        }
        catch (Throwable ite)
        {
            // ite.printStackTrace();
        }
        return nombreServicio;
    }
    
    public String obtenerTramaSoapDesdeObjetoSMS(Object obj) throws ParserConfigurationException, TransformerConfigurationException, TransformerException
    {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        
        transformer.transform(new DOMSource((Node) obj), new StreamResult(sw));
        
        return sw.toString();
    }


}
