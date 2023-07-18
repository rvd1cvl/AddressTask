public class FirstTask {
    public static void main(String[] args) {
        String targetDate = "2010-01-01";
        String objectIds = "1422396, 1450759, 1449192, 1451562";
        String pathToAddressFile = "C:\\Users\\mzilc\\IdeaProjects\\AddressTask\\src\\main\\resources\\AS_ADDR_OBJ.XML";

        AddressParser.getAddressDescriptionOnDate(targetDate, objectIds, pathToAddressFile);
    }
}
