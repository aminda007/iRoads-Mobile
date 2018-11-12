package codemo.iroads_mobile.Entity;

public class Journey {

    private static String name;
    private static String id;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Journey.name = name;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Journey.id = id;
    }

    public static void clear(){
        Journey.name = null;
        Journey.id = null;
    }
}
