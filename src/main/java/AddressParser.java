import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    // Метод для решения задачи № 1
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
}

