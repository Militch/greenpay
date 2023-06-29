package com.esiran.greenpay.common.util;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

@Slf4j
public class Map2Xml {
    /**
     * XML格式字符串转换为Map
     *
     * @param xml XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String xml) {
        try {
            Map<String, String> data = new HashMap<>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(xml.getBytes("GBK"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            stream.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 将Map转换为XML格式的字符串
     *
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, String> data) throws Exception {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document document = documentBuilder.newDocument();
            org.w3c.dom.Element root = document.createElement("Result");
            document.appendChild(root);
            for (String key: data.keySet()) {
                String value = data.get(key);
                if (value == null) {
                    value = "";
                }
                value = value.trim();
                org.w3c.dom.Element filed = document.createElement(key);
                filed.appendChild(document.createTextNode(value));
                root.appendChild(filed);
            }
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(document);
            transformer.setOutputProperty(OutputKeys.ENCODING, "GBK");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
            writer.close();
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * (多层)xml格式字符串转换为map
     *
     * @param xml xml字符串
     * @return 第一个为Root节点，Root节点之后为Root的元素，如果为多层，可以通过key获取下一层Map
     */
    public static Map<String, Object> multilayerXmlToMap(String xml) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
//            logger.error("xml字符串解析，失败 --> {}", e);
        }
        Map<String, Object> map = new HashMap<>();
        if (null == doc) {
            return map;
        }
        // 获取根元素
        Element rootElement = doc.getRootElement();
        recursionXmlToMap(rootElement,map);
        return map;
    }

    /**
     * multilayerXmlToMap核心方法，递归调用
     *
     * @param element 节点元素
     * @param outmap 用于存储xml数据的map
     */
    @SuppressWarnings("unchecked")
    private static void recursionXmlToMap(Element element, Map<String, Object> outmap) {
        // 得到根元素下的子元素列表
        List<Element> list = element.elements();
        int size = list.size();
        if (size == 0) {
            // 如果没有子元素,则将其存储进map中
            outmap.put(element.getName(), element.getTextTrim());
        } else {
            // innermap用于存储子元素的属性名和属性值
            Map<String, Object> innermap = new HashMap<>();
            // 遍历子元素
            list.forEach(childElement -> recursionXmlToMap(childElement, innermap));
            outmap.put(element.getName(), innermap);
        }
    }
    /**
     * (多层)xml格式字符串转换为map
     *
     * @param xml xml字符串
     * @return 第一个为Root节点，Root节点之后为Root的元素，如果为多层，可以通过key获取下一层Map
     */
//    public static Map<String, Object> multilayerXmlToMap(String xml) {
//        Document doc = null;
//        try {
//            doc = DocumentHelper.parseText(xml);
//        } catch (DocumentException e) {
////            logger.error("xml字符串解析，失败 --> {}", e);
//        }
//        Map<String, Object> map = new HashMap<>();
//        if (null == doc) {
//            return map;
//        }
//        // 获取根元素
//        Element rootElement = doc.getRootElement();
//        recursionXmlToMap(rootElement,map);
//        return map;
//    }

    /**
     * multilayerXmlToMap核心方法，递归调用
     *
     * @param element 节点元素
     * @param outmap 用于存储xml数据的map
     */
//    @SuppressWarnings("unchecked")
//    private static void recursionXmlToMap(Element element, Map<String, Object> outmap) {
//        // 得到根元素下的子元素列表
//        List<Element> list = element.elements();
//        int size = list.size();
//        if (size == 0) {
//            // 如果没有子元素,则将其存储进map中
//            outmap.put(element.getName(), element.getTextTrim());
//        } else {
//            // innermap用于存储子元素的属性名和属性值
//            Map<String, Object> innermap = new HashMap<>();
//            // 遍历子元素
//            list.forEach(childElement -> recursionXmlToMap(childElement, innermap));
//            outmap.put(element.getName(), innermap);
//        }
//    }
    /**
     * (多层)map转换为xml格式字符串
     *
     * @param map 需要转换为xml的map
     * @param isCDATA 是否加入CDATA标识符 true:加入 false:不加入
     * @return xml字符串
     */
    public static String multilayerMapToXml(Map<String, Object> map, boolean isCDATA){
        String parentName = "xml";
        Document doc = DocumentHelper.createDocument();
        doc.addElement(parentName);
        String xml = recursionMapToXml(doc.getRootElement(), parentName, map, isCDATA);
        return formatXML(xml);
    }

    /**
     * multilayerMapToXml核心方法，递归调用
     *
     * @param element 节点元素
     * @param parentName 根元素属性名
     * @param map 需要转换为xml的map
     * @param isCDATA 是否加入CDATA标识符 true:加入 false:不加入
     * @return xml字符串
     */
    @SuppressWarnings("unchecked")
    private static String recursionMapToXml(Element element, String parentName, Map<String, Object> map, boolean isCDATA) {
        Element xmlElement = element.addElement(parentName);
        map.keySet().forEach(key -> {
            Object obj = map.get(key);
            if (obj instanceof Map) {
                recursionMapToXml(xmlElement, key, (Map<String, Object>)obj, isCDATA);
            } else {
                String value = obj == null ? "" : obj.toString();
                if (isCDATA) {
                    xmlElement.addElement(key).addCDATA(value);
                } else {
                    xmlElement.addElement(key).addText(value);
                }
            }
        });
        return xmlElement.asXML();
    }
    /**
     * 格式化xml,显示为容易看的XML格式
     *
     * @param xml 需要格式化的xml字符串
     * @return
     */
    public static String formatXML(String xml) {
        String requestXML = null;
        try {
            // 拿取解析器
            SAXReader reader = new SAXReader();
            Document document = reader.read(new StringReader(xml));
            if (null != document) {
                StringWriter stringWriter = new StringWriter();
                // 格式化,每一级前的空格
                OutputFormat format = new OutputFormat("    ", true);
                // xml声明与内容是否添加空行
                format.setNewLineAfterDeclaration(false);
                // 是否设置xml声明头部
                format.setSuppressDeclaration(false);
                // 是否分行
                format.setNewlines(true);
                XMLWriter writer = new XMLWriter(stringWriter, format);
                writer.write(document);
                writer.flush();
                writer.close();
                requestXML = stringWriter.getBuffer().toString();
            }
            return requestXML;
        } catch (Exception e) {
//            logger.error("格式化xml，失败 --> {}", e);
            return null;
        }
    }
//    public static String formatXML(String xml) {
//        String requestXML = null;
//        try {
//            // 拿取解析器
//            SAXReader reader = new SAXReader();
//            Document document = reader.read(new StringReader(xml));
//            if (null != document) {
//                StringWriter stringWriter = new StringWriter();
//                // 格式化,每一级前的空格
//                OutputFormat format = new OutputFormat("    ", true);
//                // xml声明与内容是否添加空行
//                format.setNewLineAfterDeclaration(false);
//                // 是否设置xml声明头部
//                format.setSuppressDeclaration(false);
//                // 是否分行
//                format.setNewlines(true);
//                XMLWriter writer = new XMLWriter(stringWriter, format);
//                writer.write(document);
//                writer.flush();
//                writer.close();
//                requestXML = stringWriter.getBuffer().toString();
//            }
//            return requestXML;
//        } catch (Exception e) {
//            System.out.println("格式化xml，失败 --> {}"+e);
//            return null;
//        }
//    }
    public static String  parseMap(Map<?, ?> map, StringBuffer sb) {
        Set<?> set = map.keySet();
        for (Iterator<?> it = set.iterator(); it.hasNext();) {
            String key = (String) it.next();
            Object value = map.get(key);
            if (value instanceof HashMap) {
                sb.append("<" + key + ">");
                parseMap((HashMap<?, ?>) value, sb);
                sb.append("</" + key + ">");
            } else if (value instanceof ArrayList) {
                List<?> list = (ArrayList<?>) map.get(key);
                for (int i = 0; i < list.size(); i++) {
                    sb.append("<" + key + ">");
                    Map<?, ?> hm = (HashMap<?, ?>) list.get(i);
                    parseMap(hm, sb);
                    sb.append("</" + key + ">");
                }
            } else {
                sb.append("<" + key + ">" + value + "</" + key + ">");
            }
        }
        return sb.toString();
    }
//    /**
//     * w3c Element 转成xml串
//     * @param element
//     * @return
//     * @throws Exception
//     */
//    public static String w3cElementTransferXmlStr(org.w3c.dom.Element element) throws Exception{
//        TransformerFactory tFactory = TransformerFactory.newInstance();
//        Transformer transformer = tFactory.newTransformer();
//        Source source = new DOMSource(element);
//        StringWriter out = new StringWriter();
//        Result output = new StreamResult(out);
//        transformer.transform(source, output);
//        out.flush();
//        String ss = out.toString();
//        return formatXML(ss);
//    }
//    /**
//     *  xml串 转成w3c Element
//     * @param xmlString
//     * @return
//     * @throws Exception
//     */
//    public static org.w3c.dom.Element getElementByString(String xmlString) throws Exception {
//        if (xmlString == null){
//            xmlString = "<returnInfo><returnCode>-999</returnCode><description>error</description></returnInfo>";
//        }
//        org.w3c.dom.Document doc = null;
//        Reader strreader = new StringReader(xmlString);
//        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        doc = builder.parse(new InputSource(strreader));
//        return doc.getDocumentElement();
//    }
}
