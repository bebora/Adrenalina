package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CardCreator {
    public Weapon parseWeapon(String fileName) throws IOException{
        ClassLoader classLoader = getClass().getClassLoader();
        String tempName = null;
        List<Ammo> tempCost = null;
        List<Effect> tempEffects = null;
        try (FileReader input = new FileReader(classLoader.getResource(fileName).getFile());
             BufferedReader bufRead = new BufferedReader(input)
        ){
            String curLine = null;
            String[] splitLine = null;
            while ((curLine = bufRead.readLine()) != null) {
                splitLine = curLine.trim().split(":");
                switch (splitLine[0]) {
                    case "name":
                        tempName = splitLine[1];
                        break;
                    case "cost":
                        tempCost = parseCost(bufRead);
                        break;
                    case "effects":
                        tempEffects = parseEffects(bufRead);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            return null;
        }
        return new Weapon.Builder(tempEffects).
                setCost(tempCost).
                setName(tempName).
                build();
    }

    private List<Effect> parseEffects(BufferedReader bufRead) throws IOException{
        String curLine;
        String[] splitLine;

        List<Effect> effects = new ArrayList<>();
        Effect tempEffect = null;

        while(true){
            bufRead.mark(20);
            curLine = bufRead.readLine();
            if(curLine != null)
                splitLine = curLine.trim().split(":");
            else {
                effects.add(tempEffect);
                return effects;
            }
            switch(splitLine[0]){
                case "name":
                    if(tempEffect != null)
                        effects.add(tempEffect);
                    tempEffect = new Effect();
                    tempEffect.setName(splitLine[1]);
                    break;
                case "cost":
                    tempEffect.setCost(parseCost(bufRead));
                    break;
                case "damages":
                    tempEffect.setDamages(parseDamages(bufRead));
                    break;
                case "moves":
                    tempEffect.setMoves(parseMoves(bufRead));
                    break;
                case "order":
                    tempEffect.setOrder(parseOrder(bufRead));
                    break;
                case "absolutePriority":
                    tempEffect.setAbsolutePriority((Integer.parseInt(splitLine[1])));
                    break;
                case "relativePriority":
                    tempEffect.setRelativePriority(parseRelativePriority(bufRead));
                    break;
                case "direction":
                    tempEffect.setDirection(Direction.stringToDirection(splitLine[1]));
                    break;
                default:
                    bufRead.reset();
                    effects.add(tempEffect);
                    return effects;
            }
        }
    }

    private List<Integer> parseRelativePriority (BufferedReader bufRead) throws IOException{
        List<Integer> temp= new ArrayList<>();
        String curLine = bufRead.readLine().trim();
        while (!curLine.contains(":")) {
            temp.add(Integer.parseInt(curLine));
            bufRead.mark(20);
            curLine = bufRead.readLine().trim();
        }
        bufRead.reset();
        return temp;


    }
    private ArrayList<Ammo> parseCost(BufferedReader bufRead) throws IOException{
        ArrayList<Ammo> temp = new ArrayList<>();
        String curLine;
        curLine = bufRead.readLine().trim();
        while(!curLine.contains(":")){
            temp.add(Ammo.stringToAmmo(curLine));
            bufRead.mark(20);
            curLine = bufRead.readLine().trim();
        }
        bufRead.reset();
        return temp;
    }

    private List<DealDamage> parseDamages(BufferedReader bufRead) throws IOException{
        String curLine;
        String[] splitLine;

        DealDamage tempDamage = null;
        ArrayList<DealDamage> damages = new ArrayList<>();
        while(true){
            bufRead.mark(20);
            curLine = bufRead.readLine();
            splitLine = curLine.trim().split(":");
            switch(splitLine[0]){
                case "damage":
                    if(tempDamage != null){
                        damages.add(tempDamage);
                    }
                    tempDamage = new DealDamage();
                    break;
                case "target":
                    tempDamage.setTarget(parseTarget(bufRead));
                    break;
                case "damagesAmount":
                    tempDamage.setDamagesAmount(Integer.parseInt(splitLine[1]));
                    break;
                case "marksAmount":
                    tempDamage.setMarksAmount(Integer.parseInt(splitLine[1]));
                    break;
                case "targeting":
                    tempDamage.setTargeting(ThreeState.stringToThreeState(splitLine[1]));
                    break;
                default:
                    bufRead.reset();
                    damages.add(tempDamage);
                    return damages;
            }
        }
    }

    /**
     * Return a Target object after parsing it from a btl configuration file.
     * bufRead must be a BufferedReader at the position where the target configuration starts.
     * The final position of bufRead is the beginning end of the last line of the Target
     * configuration.
     * @param bufRead a BufferedReader buffering input from a btl file
     * @return a target object initialized with the specified btl config
     * @see Target
     * @throws IOException
     */
    private Target parseTarget(BufferedReader bufRead) throws IOException{
        String curLine;
        String[] splitLine;
        Target temp = new Target();
        while(true){
            bufRead.mark(20);
            curLine = bufRead.readLine();
            splitLine = curLine.trim().split(":");
            switch(splitLine[0]){
                case "visibility":
                    temp.setVisibility(ThreeState.stringToThreeState(splitLine[1]));
                    break;
                case "maxTargets":
                    temp.setMaxTargets(Integer.parseInt(splitLine[1]));
                    break;
                case "minDistance":
                    temp.setMinDistance(Integer.parseInt(splitLine[1]));
                    break;
                case "maxDistance":
                    temp.setMaxDistance(Integer.parseInt(splitLine[1]));
                    break;
                case "areaDamage":
                    temp.setAreaDamage(Area.stringToArea(splitLine[1]));
                    break;
                case "cardinal":
                    temp.setCardinal(ThreeState.stringToThreeState(splitLine[1]));
                    break;
                case "checkTargetList":
                    temp.setCheckTargetList(ThreeState.stringToThreeState(splitLine[1]));
                    break;
                case "differentSquare":
                    temp.setDifferentSquare(ThreeState.stringToThreeState(splitLine[1]));
                    break;
                case "samePlayerRoom":
                    temp.setSamePlayerRoom(ThreeState.stringToThreeState(splitLine[1]));
                    break;
                case "throughWalls":
                    temp.setThroughWalls(ThreeState.stringToThreeState(splitLine[1]));
                    break;
                case "pointOfView":
                    temp.setPointOfView(PointOfView.stringToPointOfView(splitLine[1]));
                    break;
                case "checkBlackList":
                    temp.setCheckBlackList(ThreeState.stringToThreeState(splitLine[1]));
                    break;
                default:
                    bufRead.reset();
                    return temp;
            }
        }
    }

    private List<Move> parseMoves(BufferedReader bufRead) throws IOException{
        String curLine;
        String [] splitLine;

        List<Move> moves = new ArrayList<>();
        Move tempMove = null;

        while(true){
            bufRead.mark(20);
            curLine = bufRead.readLine();
            splitLine = curLine.trim().split(":");
            switch(splitLine[0]){
                case "move":
                    if(tempMove != null)
                        moves.add(tempMove);
                    tempMove = new Move();
                    break;
                case "objectToMove":
                    tempMove.setObjectToMove(ObjectToMove.stringToObjectToMove(splitLine[1]));
                    break;
                case "targetDestination":
                    tempMove.setTargetDestination(parseTarget(bufRead));
                    break;
                case "targeting":
                    tempMove.setTargeting(ThreeState.stringToThreeState(splitLine[1]));
                    break;
                case "targetSource":
                    tempMove.setTargetSource(parseTarget(bufRead));
                    break;
                default:
                    moves.add(tempMove);
                    return moves;
            }
        }
    }

    private List<ActionType> parseOrder(BufferedReader bufRead) throws IOException{
        List<ActionType> order = new ArrayList<>();

        String curLine = bufRead.readLine().trim();
        while(!curLine.contains(":")) {
            order.add(ActionType.stringToActionType(curLine));
            bufRead.mark(20);
        }
        bufRead.reset();
        return order;
    }

}

