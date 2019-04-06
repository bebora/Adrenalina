package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static model.ThreeState.OPTIONAL;

/**
 * Parser used for reading configuration files for PowerUps and Weapons
 */
public class CardCreator {
    /**
     * Returns a PowerUp object after parsing its configuration from a btl file.
     * The file must be in the resource folder.
     * The param filename must include the file extension.
     * @param filename name of the file to read the configuration from
     * @return a PowerUp built according to the given configuration
     * @see PowerUp
     */
    public static PowerUp parsePowerUp(String filename) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
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
                        break;
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

    /**
     *Returns a Weapon object after parsing a weapon configuration from a btl file.
     * The configuration file must be in the resource folder, preferably in the weapon subfolder.
     * The parameter filename must include the file extension.
     * @param fileName name of the file to read the configuration from
     * @return a Weapon built according to the given configuration
     * @see Weapon
     */
    public Weapon parseWeapon(String fileName){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
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

    /**
     * Returns an ArrayList of Effect to be used in a Weapon or PowerUp.
     * The Effects are parsed from a btl file and bufRead must be a BufferedReader at
     * the position where the first effect configuration starts.
     * @param bufRead a BufferedReader buffering input from a btl file
     * @return an ArrayList of Effect
     * @throws IOException If an I/O error occurs
     * @see Effect
     */
    private static List<Effect> parseEffects(BufferedReader bufRead) throws IOException{
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
                    direction = Direction.valueOf(splitLine[1].toUpperCase());
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

    /**
     * Returns an ArrayList of Integers representing the relative priority of an effect.
     * bufRead must be a BufferedReader buffering a btl file at the position where the
     * priority configuration starts.
     * @param bufRead a BufferedReader buffering an input stream from a btl file
     * @return an ArrayList of Integer
     * @throws IOException If an I/O error occurs
     */
    private static List<Integer> parseRelativePriority (BufferedReader bufRead) throws IOException{
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
    private static ArrayList<Ammo> parseCost(BufferedReader bufRead) throws IOException{
        ArrayList<Ammo> temp = new ArrayList<>();
        String curLine;
        curLine = bufRead.readLine().trim();
        while(!curLine.contains(":")){
            temp.add(Ammo.valueOf(curLine.toUpperCase()));
            bufRead.mark(20);
            curLine = bufRead.readLine().trim();
        }
        bufRead.reset();
        return temp;
    }

    /**
     * Returns an ArrayList of DealDamage to be used in a Weapon or PowerUp.
     * bufRead is a BufferedReader at the position where the first DealDamage config start.
     * @param bufRead a BufferedReader buffering input stream from a btl file
     * @return an ArrayList of DealDamage
     * @throws IOException If an I/O error occurs
     * @see DealDamage
     */
    private static List<DealDamage> parseDamages(BufferedReader bufRead) throws IOException{
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
                    targeting = ThreeState.valueOf(splitLine[1].toUpperCase());
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
     * @param bufRead a BufferedReader buffering an input stream from a btl file
     * @return a target object initialized with the specified btl config
     * @see Target
     * @throws IOException If an I/O error occurs
     */
    private static Target parseTarget(BufferedReader bufRead) throws IOException{
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
                    visibility = ThreeState.valueOf(splitLine[1].toUpperCase());
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
                    areaDamage = Area.valueOf(splitLine[1].toUpperCase());
                    break;
                case "cardinal":
                    cardinal = ThreeState.valueOf(splitLine[1].toUpperCase());
                    break;
                case "checkTargetList":
                    checkTargetList = ThreeState.valueOf(splitLine[1].toUpperCase());
                    break;
                case "differentSquare":
                    differentSquare = ThreeState.valueOf(splitLine[1].toUpperCase());
                    break;
                case "samePlayerRoom":
                    samePlayerRoom = ThreeState.valueOf(splitLine[1].toUpperCase());
                    break;
                case "throughWalls":
                    throughWalls = ThreeState.valueOf(splitLine[1].toUpperCase());
                    break;
                case "pointOfView":
                    pointOfView = PointOfView.valueOf(splitLine[1].toUpperCase());
                    break;
                case "checkBlackList":
                    checkBlackList = ThreeState.valueOf(splitLine[1].toUpperCase());
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

    /**
     * Returns an ArrayList of Moves to be used in a Weapon or PowerUp.
     * bufRead must be a BufferedReader at the position where the first Move config starts.
     * @param bufRead a BufferedReader buffering an input stream from a btl file
     * @return an ArrayList of Move
     * @throws IOException If an I/O error occurs
     * @see Move
     */
    private static List<Move> parseMoves(BufferedReader bufRead) throws IOException{
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
                    objectToMove = ObjectToMove.valueOf(splitLine[1].toUpperCase());
                    empty = Boolean.FALSE;
                    targetDestination = new Target.Builder().build();
                    targeting = ThreeState.OPTIONAL;
                    targetSource = new Target.Builder().build();
                    break;
                case "targetDestination":
                    targetDestination = parseTarget(bufRead);
                    break;
                case "targeting":
                    targeting = ThreeState.valueOf(splitLine[1].toUpperCase());
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

    /**
     * Returns an ArrayList of ActionType representing the absolute order of
     * effects in a Weapon.
     * bufRead must be a BufferedReader at the position where the first ActionType config starts.
     * @param bufRead a BufferedReader buffering an input stream from a btl file
     * @return an ArrayList of ActionType
     * @throws IOException If an I/O error occurs
     * @see ActionType
     */
    private static List<ActionType> parseOrder(BufferedReader bufRead) throws IOException{
        List<ActionType> order = new ArrayList<>();

        String curLine = bufRead.readLine().trim();
        while(!curLine.contains(":")) {
            order.add(ActionType.valueOf(curLine.toUpperCase()));
            bufRead.mark(20);
        }
        bufRead.reset();
        return order;
    }

}

