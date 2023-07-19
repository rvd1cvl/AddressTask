public class SecondTask {
    public static void main(String[] args) {
        String pathToAddressFile = "C:\\Users\\mzilc\\IdeaProjects\\AddressTask\\src\\main\\resources\\AS_ADDR_OBJ.XML";
        String pathToItemsFile = "C:\\Users\\mzilc\\IdeaProjects\\AddressTask\\src\\main\\resources\\AS_ADM_HIERARCHY.XML";
        AddressParser.parseAndPrintAddresses(pathToAddressFile, pathToItemsFile);
    }
}
