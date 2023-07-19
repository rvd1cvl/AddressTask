import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddressParser {

    // Метод для чтения и обработки XML файла с адресами
    private static void parseAddressXML(String pathToFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(pathToFile);

            // Получаем все элементы OBJECT
            NodeList objectList = doc.getElementsByTagName("OBJECT");

            // Проходим по всем элементам и выводим информацию
            for (int i = 0; i < objectList.getLength(); i++) {
                Element object = (Element) objectList.item(i);
                String objectId = object.getAttribute("OBJECTID");
                String typeName = object.getAttribute("TYPENAME");
                String name = object.getAttribute("NAME");

                System.out.println(objectId + ": " + typeName + " " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для решения задачи #1
    public static void getAddressDescriptionOnDate(String date, String objectIds, String pathToAddressFile) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date targetDate;

        try {
            targetDate = dateFormat.parse(date);
        } catch (ParseException e) {
            System.err.println("Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        String[] ids = objectIds.split(",\\s*");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(pathToAddressFile);

            // Получаем все элементы OBJECT
            NodeList objectList = doc.getElementsByTagName("OBJECT");

            // Проходим по всем элементам и выводим информацию
            for (int i = 0; i < objectList.getLength(); i++) {
                Element object = (Element) objectList.item(i);
                String objectId = object.getAttribute("OBJECTID");
                String typeName = object.getAttribute("TYPENAME");
                String name = object.getAttribute("NAME");
                String startDateString = object.getAttribute("STARTDATE");
                String endDateString = object.getAttribute("ENDDATE");

                try {
                    Date startDate = dateFormat.parse(startDateString);
                    Date endDate = dateFormat.parse(endDateString);

                    // Проверяем, что переданная дата находится в диапазоне дат адреса
                    if (targetDate.compareTo(startDate) >= 0 && targetDate.compareTo(endDate) <= 0) {
                        for (String id : ids) {
                            if (objectId.equals(id)) {
                                System.out.println(objectId + ": " + typeName + " " + name);
                                break;
                            }
                        }
                    }
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для решения задачи #2

    public static void parseAndPrintAddresses(String addressObjectsFilePath, String admHierarchyFilePath) {
        try {
            // Разбор XML-данных из файлов
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            Document addressObjectsDoc = dbFactory.newDocumentBuilder().parse(new File(addressObjectsFilePath));
            addressObjectsDoc.getDocumentElement().normalize();

            Document admHierarchyDoc = dbFactory.newDocumentBuilder().parse(new File(admHierarchyFilePath));
            admHierarchyDoc.getDocumentElement().normalize();

            // Получение списка актуальных проездов
            NodeList addressObjectsList = addressObjectsDoc.getElementsByTagName("OBJECT");
            NodeList admHierarchyList = admHierarchyDoc.getElementsByTagName("ITEM");

            // Создание отображения parentChildMap, где ключ - OBJECTID, значение - PARENTOBJID,
            // для хранения связей родительских и дочерних адресов
            Map<String, String> parentChildMap = new HashMap<>();
            for (int i = 0; i < admHierarchyList.getLength(); i++) {
                Element admHierarchyElement = (Element) admHierarchyList.item(i);
                String objectId = admHierarchyElement.getAttribute("OBJECTID");
                String parentObjectId = admHierarchyElement.getAttribute("PARENTOBJID");
                parentChildMap.put(objectId, parentObjectId);
            }

            // Список для хранения адресов проездов
            List<String> addresses = new ArrayList<>();

            // Поиск проездов и построение полных адресов
            for (int i = 0; i < addressObjectsList.getLength(); i++) {
                Element addressObjectElement = (Element) addressObjectsList.item(i);
                String typeNameAttr = addressObjectElement.getAttribute("TYPENAME");
                String name = addressObjectElement.getAttribute("NAME");
                boolean isActual = "1".equals(addressObjectElement.getAttribute("ISACTUAL"));

                if (isActual && typeNameAttr.equals("проезд")) {
                    // Строим полный адрес проезда
                    String addressChain = buildAddressChain(addressObjectElement, parentChildMap, addressObjectsList);
                    addresses.add(addressChain);
                }
            }

            // Выводим полные адреса проездов в консоль
            for (String address : addresses) {
                System.out.println(address);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Рекурсивно строим полный адрес проезда, включая родительские адреса
    private static String buildAddressChain(Element addressObject, Map<String, String> parentChildMap, NodeList addressObjectsList) {
        String name = addressObject.getAttribute("NAME");
        String typeName = addressObject.getAttribute("TYPENAME");
        String objectId = addressObject.getAttribute("OBJECTID");

        String parentId = parentChildMap.get(objectId);
        Element parentNode = findParentNode(parentId, parentChildMap, addressObjectsList);

        if (parentNode != null) {
            // Если у объекта есть родитель, строим цепочку родительских адресов
            String parentChain = buildAddressChain(parentNode, parentChildMap, addressObjectsList);
            return parentChain + ", " + name + " " + typeName;
        } else {
            // Возвращаем полный адрес проезда без родительских элементов
            return name + " " + typeName;
        }
    }

    // Метод для поиска родительского элемента по OBJECTID в списке addressObjectsList
    private static Element findParentNode(String parentId, Map<String, String> parentChildMap, NodeList addressObjectsList) {
        for (int i = 0; i < addressObjectsList.getLength(); i++) {
            Element addressObjectElement = (Element) addressObjectsList.item(i);
            String objectId = addressObjectElement.getAttribute("OBJECTID");

            if (objectId.equals(parentId)) {
                // Найден родительский элемент с заданным OBJECTID
                return addressObjectElement;
            }
        }
        // Родительский элемент не найден
        return null;
    }
}

