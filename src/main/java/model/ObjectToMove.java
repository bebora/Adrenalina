package model;

public enum ObjectToMove {
    SELF,
    PERSPECTIVE,
    TARGETSOURCE;

    public static ObjectToMove stringToObjectToMove(String input){
        switch (input.toLowerCase()){
            case "self":
                return SELF;
            case "perspective":
                return PERSPECTIVE;
            case "TARGETSOURCE":
                return TARGETSOURCE;
            default:
                return null;
        }
    }

}
