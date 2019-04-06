package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static model.ThreeState.OPTIONAL;

/**
 * Parser used for reading configuration files for PowerUps and Weapons
 */
public class CardCreator {

    public PowerUp parsePowerUp(String filename) {
        ClassLoader classLoader = getClass().getClassLoader();
        Ammo discardAward = null;
        Moment applicability = null;
        Effect effect = null;
        String name = null;
        try (FileReader input = new FileReader(classLoader.getResource(filename).getFile());
             BufferedReader bufRead = new BufferedReader(input)
        ){
            String curLine = null;
            String[] splitLine = null;
            while ((curLine = bufRead.readLine()) != null) {
                splitLine = curLine.trim().split(":");
                switch (splitLine[0]) {
                    case "name":
                        name = splitLine[1];
                        break;
                    case "discardAward":
                        discardAward = Ammo.valueOf(splitLine[1].toUpperCase());
                    case "applicability":
                        applicability = Moment.valueOf(splitLine[1].toUpperCase());
                        break;
                    case "effects":
                        effect = parseEffects(bufRead).get(0);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            return null;
        }
        return new PowerUp.Builder().setApplicability(applicability).
                setName(name).setDiscardAward(discardAward).setEffect(effect).
                build();
    }

    public Weapon parseWeapon(String fileName){
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
        List <Move> moves = new ArrayList<>();
        List<DealDamage> damages = new ArrayList<>();
        List<ActionType> order = new ArrayList<>();
        List<Ammo> cost = new ArrayList<>();
        String name = null;
        Direction direction = null;
        String desc = null;
        int absolutePriority = 0;
        List<Integer> relativePriority = null;
        Boolean empty = Boolean.TRUE;
        while(true){
            bufRead.mark(20);
            curLine = bufRead.readLine();
            if(curLine != null)
                splitLine = curLine.trim().split(":");
            else {
                effects.add(new Effect.Builder().setName(name).
                        setDirection(direction).setDesc(desc).setAbsolutePriority(absolutePriority).
                        setRelativePriority(relativePriority).setMoves(moves).
                        setDamages(damages).setOrder(order).setCost(cost).build());
                return effects;
            }
            switch(splitLine[0]){
                case "name":
                    if(empty == Boolean.FALSE)
                        effects.add(new Effect.Builder().setName(name).
                                setDirection(direction).setDesc(desc).setAbsolutePriority(absolutePriority).
                                setRelativePriority(relativePriority).setMoves(moves).
                                setDamages(damages).setOrder(order).setCost(cost).build());
                    empty = Boolean.FALSE;
                    name = splitLine[1];
                    effects = new ArrayList<>();
                    moves = new ArrayList<>();
                    damages = new ArrayList<>();
                    order = new ArrayList<>();
                    cost = new ArrayList<>();
                    direction = null;
                    desc = null;
                    absolutePriority = 0;
                    relativePriority = null;
                    break;
                case "cost":
                    cost = parseCost(bufRead);
                    break;
                case "damages":
                    damages = parseDamages(bufRead);
                    break;
                case "moves":
                    moves = parseMoves(bufRead);
                    break;
                case "order":
                    order = parseOrder(bufRead);
                    break;
                case "absolutePriority":
                    absolutePriority = Integer.parseInt(splitLine[1]);
                    break;
                case "relativePriority":
                    relativePriority = parseRelativePriority(bufRead);
                    break;
                case "direction":
                    direction = Direction.stringToDirection(splitLine[1]);
                    break;
                default:
                    bufRead.reset();
                    effects.add(new Effect.Builder().setName(name).
                            setDirection(direction).setDesc(desc).setAbsolutePriority(absolutePriority).
                            setRelativePriority(relativePriority).setMoves(moves).
                            setDamages(damages).setOrder(order).setCost(cost).build());
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
        Boolean empty = Boolean.TRUE;
        List<DealDamage> damages = new ArrayList<>();
        int damagesAmount = 0;
        int marksAmount = 0;
        ThreeState targeting = OPTIONAL;
        Target target = new Target.Builder().build();
        while(true){
            bufRead.mark(20);
            curLine = bufRead.readLine();
            splitLine = curLine.trim().split(":");
            switch(splitLine[0]){
                case "damage":
                    if(empty == Boolean.FALSE){
                        damages.add(new DealDamage.Builder().setDamagesAmount(damagesAmount).
                                setMarksAmount(marksAmount).setTarget(target).setTargeting(targeting).
                                build());
                    }
                    empty = Boolean.FALSE;
                    targeting = OPTIONAL;
                    target = new Target.Builder().build();
                    marksAmount = 0;
                    damagesAmount = 0;
                    break;
                case "target":
                    target = parseTarget(bufRead);
                    break;
                case "damagesAmount":
                    damagesAmount = Integer.parseInt(splitLine[1]);
                    break;
                case "marksAmount":
                    marksAmount = Integer.parseInt(splitLine[1]);
                    break;
                case "targeting":
                    targeting = ThreeState.stringToThreeState(splitLine[1]);
                    break;
                default:
                    bufRead.reset();
                    damages.add(new DealDamage.Builder().setDamagesAmount(damagesAmount).
                            setMarksAmount(marksAmount).setTarget(target).setTargeting(targeting).
                            build());
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
        ThreeState visibility = OPTIONAL;
        int maxTargets = -1;
        int minDistance = 0;
        int maxDistance = -1;
        Area areaDamage = Area.SINGLE;
        ThreeState cardinal = OPTIONAL;
        ThreeState checkTargetList = OPTIONAL;
        ThreeState differentSquare = OPTIONAL;
        ThreeState samePlayerRoom = OPTIONAL;
        ThreeState throughWalls = OPTIONAL;
        PointOfView pointOfView = PointOfView.OWN;
        ThreeState checkBlackList = OPTIONAL;

        while(true){
            bufRead.mark(20);
            curLine = bufRead.readLine();
            splitLine = curLine.trim().split(":");
            switch(splitLine[0]){
                case "visibility":
                    visibility = ThreeState.stringToThreeState(splitLine[1]);
                    break;
                case "maxTargets":
                    maxTargets = Integer.parseInt(splitLine[1]);
                    break;
                case "minDistance":
                    minDistance = Integer.parseInt(splitLine[1]);
                    break;
                case "maxDistance":
                    maxDistance = Integer.parseInt(splitLine[1]);
                    break;
                case "areaDamage":
                    areaDamage = Area.stringToArea(splitLine[1]);
                    break;
                case "cardinal":
                    cardinal = ThreeState.stringToThreeState(splitLine[1]);
                    break;
                case "checkTargetList":
                    checkTargetList = ThreeState.stringToThreeState(splitLine[1]);
                    break;
                case "differentSquare":
                    differentSquare = ThreeState.stringToThreeState(splitLine[1]);
                    break;
                case "samePlayerRoom":
                    samePlayerRoom = ThreeState.stringToThreeState(splitLine[1]);
                    break;
                case "throughWalls":
                    throughWalls = ThreeState.stringToThreeState(splitLine[1]);
                    break;
                case "pointOfView":
                    pointOfView = PointOfView.stringToPointOfView(splitLine[1]);
                    break;
                case "checkBlackList":
                    checkBlackList = ThreeState.stringToThreeState(splitLine[1]);
                    break;
                default:
                    bufRead.reset();
                    return new Target.Builder().setAreaDamage(areaDamage).
                            setCardinal(cardinal).setCheckBlackList(checkBlackList).
                            setCheckTargetList(checkTargetList).setVisibility(visibility).
                            setMaxDistance(maxDistance).setMinDistance(minDistance).setMaxTargets(maxTargets).
                            setDifferentSquare(differentSquare).setSamePlayerRoom(samePlayerRoom).setThroughWalls(throughWalls).
                            setPointOfView(pointOfView).build();


            }
        }
    }

    private List<Move> parseMoves(BufferedReader bufRead) throws IOException{
        String curLine;
        String [] splitLine;
        List<Move> moves = new ArrayList<>();
        ObjectToMove objectToMove = ObjectToMove.SELF;
        Target targetDestination = new Target.Builder().build();
        ThreeState targeting = ThreeState.OPTIONAL;
        Target targetSource = new Target.Builder().build();
        Boolean empty = Boolean.TRUE;
        while(true){
            bufRead.mark(20);
            curLine = bufRead.readLine();
            splitLine = curLine.trim().split(":");
            switch(splitLine[0]){
                case "objectToMove":
                    if(empty == Boolean.FALSE) {
                        moves.add(new Move.Builder().setTargetSource(targetSource).
                                setObjectToMove(objectToMove).
                                setTargetDestination(targetDestination).
                                setTargeting(targeting).build());
                    }
                    objectToMove = ObjectToMove.stringToObjectToMove(splitLine[1]);
                    empty = Boolean.FALSE;
                    targetDestination = new Target.Builder().build();
                    targeting = ThreeState.OPTIONAL;
                    targetSource = new Target.Builder().build();
                    break;
                case "targetDestination":
                    targetDestination = parseTarget(bufRead);
                    break;
                case "targeting":
                    targeting = ThreeState.stringToThreeState(splitLine[1]);
                    break;
                case "targetSource":
                    targetSource = parseTarget(bufRead);
                    break;
                default:
                    bufRead.reset();
                    moves.add(new Move.Builder().setTargetSource(targetSource).
                            setObjectToMove(objectToMove).
                            setTargetDestination(targetDestination).
                            setTargeting(targeting).build());
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

