package agent;

import agent.behaviour.Behaviour;
import agent.behaviour.BehaviourState;
import environment.CellPerception;
import environment.Coordinate;
import environment.Mail;
import environment.world.agent.Agent;
import environment.world.agent.AgentRep;
import environment.world.destination.DestinationRep;
import environment.world.packet.Packet;
import environment.world.packet.PacketRep;
import support.*;
import util.Debug;
import util.MyColor;
import util.event.Event;
import util.event.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class represents the implementation of an Agent in the MAS. It
 * interacts with the Environment for new information of the world by running
 * a separate thread. The agent implementation contains the local view on the
 * world and any received messages. It also has a behaviour.
 */
abstract public class AgentImp extends ActiveImp {

    private int additional_batt_buffer = 0;

    public final static String DESTINATION_KEY = "destination";
    public final static String SEARCH_ALL_KEY = "searchAll";
    public final static String MOVED_AWAY_KEY = "movedAway";
    public final static String LOOP_DETECTION_KEY = "loopDetected";
    public final static String HELP_QUEUE_KEY = "helpQueue";
    public final static String HELP_MESSAGE_KEY = "helpMessage";
    public final static String RANDOM_PUT_COORDINATE_KEY = "randomPut";
    public final static String AVOID_DEADLOCK = "avoidDeadlock";
    public final static String SKIP_DETECTION = "hasSkipped";

    /**
     *  Initialize a new instance of AgentImp with id <ID>. Every new AgentImp
     *  instance is initialized with an empty buffer for incoming messages
     *  and an empty buffer for outgoing mails.
     * @param ID The id of this AgentImp instance.
     * @param maxBeliefs: number of maximum beliefs this AgentImp should be able to store
     * @post new.getName()==name
     * @post new.getID()==ID
     * @post new.getMailBuffer() <> null
     */
    public AgentImp(int ID, int maxBeliefs) {
        super(ID);
        messages = new Vector<>(5);
        nbTurn = 0;
        int min = Agent.BATTERY_MAX / 10;
        additional_batt_buffer = (int)(Math.random() * 1 / 3 * Agent.BATTERY_MAX + min);
        outgoingMails = new MailBuffer();

        this.maxBeliefs = maxBeliefs;
        memory = new HashMap<>();

        EventManager.getInstance().addListener(this, AgentActionEvent.class);
    }



    /**
     * Create a mail from this AgentImp to <receiver> and with message <message> and add the resulting mail to the buffer of outgoing
     * mails.
     * @param receiver The representation of the agent to write the message to
     * @param message The message to send
     */
    public final void sendMessage(AgentRep receiver, String message) {
        this.sendMessage(receiver.getName(), message);
    }


    /**
     * Create a mail from this AgentImp to <to> and with message <message> and add the resulting mail to the buffer of outgoing
     * mails.
     * @param to The name of the agent to write the message to
     * @param message The message to send
     */
    private void sendMessage(String to, String message) {
        Debug.print(this, "agentImp" + getID() + "buffers a mail");

        Mail mail = new Mail(getName(), to, message);
        getMailBuffer().addMail(mail);
    }

    public void broadcastMessage(String message) {
        List<AgentRep> agents = getPerception().findNearbyAgents();
        for (AgentRep receiver: agents) {
            if (receiver.equals(this.getAgent().getRepresentation())) continue;
            sendMessage(receiver, message);
        }
    }


    /**
     * Do the skip action (influence)
     */
    public final void skip() {
        Debug.print(this, "agent " + getID() + " proposes to do a skip");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
            this.generateActionOutcome(new InfSkip(getEnvironment(), getID())));
    }

    /**
     * Creates an influence of the type InfStep and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfStep: try to step to a target area.
     *
     * @param nx the x coordinate of the area to step to
     * @param ny the y coordinate of the area to step to
     */
    public final void step(int nx, int ny) {
        Debug.print(this, "agent " + getID() + " proposes to do a step");

        this.concludeWithCondition(this.hasSufficientEnergy(this.hasCarry() ? Agent.BATTERY_DECAY_STEP_WITH_CARRY : Agent.BATTERY_DECAY_STEP),
            this.generateActionOutcome(new InfStep(getEnvironment(), nx, ny, getID())));
    }

    /**
     * Creates an influence of the type InfPutPacket and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPutPacket: try to put the carry down on target area.
     *
     * @param tx the x coordinate of the target area
     * @param ty the y coordinate of the target area
     */
    public final void putPacket(int tx, int ty) {
        Debug.print(this, "agent " + getID() + " proposes to put a packet");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
            this.generateActionOutcome(new InfPutPacket(getEnvironment(), tx, ty, getID())));
    }

    /**
     * Creates an influence of the type InfPickPacket and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPickPacket: try to pick up a packet from the specified area.
     *
     * @param fx the x coordinate of the area of the packet
     * @param fy the y coordinate of the area of the packet
     */
    public final void pickPacket(int fx, int fy) {
        Debug.print(this, "agent " + getID() + " proposes to pick a packet");


        var packet = getEnvironment().getPacketWorld().getItem(fx, fy);
        if (packet == null) {
            throw new RuntimeException(String.format("No packet at location (%d,%d).", fx, fy));
        }
        var color = packet.getColor();
        var agentColor = getAgent().getColor();
        if (agentColor.isPresent() && agentColor.get() != color) {
            throw new RuntimeException(String.format("Agent %d cannot pick packet with color %s (agent restricted to color %s).",
                    getID(), MyColor.getName(color), MyColor.getName(agentColor.get())));
        }

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
            this.generateActionOutcome(new InfPickPacket(getEnvironment(), fx, fy, getID(), color)));
    }

    /**
     * Creates an influence of the type InfPutPheromone and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPutPheromone: try to put a pheromone on target area.
     *
     * @param tx       the x coordinate of the target area
     * @param ty       the y coordinate of the target area
     * @param lifetime the lifetime for the pheromone
     */
    public final void putPheromone(int tx, int ty, int lifetime) {
        Debug.print(this, "agent " + getID() + " proposes to put a pheromone");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
            this.generateActionOutcome(new InfPutPheromone(getEnvironment(), tx, ty, getID())));
    }

    /**
     * Creates an influence of the type InfPutDirPheromone and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPutDirPheromone: try to put a directed pheromone on target area.
     *
     * @param tx       the x coordinate of the target area
     * @param ty       the y coordinate of the target area
     * @param lifetime the lifetime for the pheromone
     * @param target   the area the directed pheromone has to point to
     */
    public final void putDirectedPheromone(int tx, int ty, int lifetime, CellPerception target) {
        Debug.print(this, "agent " + getID() + " proposes to put a directed pheromone");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
            this.generateActionOutcome(new InfPutDirPheromone(getEnvironment(), tx, ty, getID(), lifetime, target)));
    }

    /**
     * Creates an influence of the type InfRemovePheromone and includes it
     * in an ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfRemovePheromone: try to remove a pheromone from target
     * area.
     *
     * @param tx        the x coordinate of the target area
     * @param ty        the y coordinate of the target area
     */
    public final void removePheromone(int tx, int ty) {
        Debug.print(this, "agent " + getID() + " proposes to put a pheromone");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
            this.generateActionOutcome(new InfRemovePheromone(getEnvironment(), tx, ty, getID())));
    }

    /**
     * Creates an influence of the type InfPutFlag and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPutFlag: try to put a flag with given color on target area.
     *
     * @param tx    the x coordinate of the target area
     * @param ty    the y coordinate of the target area
     * @param color the color of the new Flag
     */
    public final void putFlag(int tx, int ty, Color color) {
        Debug.print(this, "agent " + getID() + " proposes to put a flag");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
            this.generateActionOutcome(new InfPutFlag(getEnvironment(), tx, ty, getID(), color)));
    }

    /**
     * Creates an influence of the type InfPutFlag and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPutFlag: try to put a flag on target area.
     *
     * @param tx    the x coordinate of the target area
     * @param ty    the y coordinate of the target area
     */
    public final void putFlag(int tx, int ty) {
        this.putFlag(tx, ty, Color.BLACK);
    }

    /**
     * Creates an influence of the type InfPutCrumb and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPutCrumb: try to put a crumb (or say: a number of crumbs)
     * on target area.
     *
     * @param tx     the x coordinate of the target area
     * @param ty     the y coordinate of the target area
     * @param number the number of crumbs to put on target area
     */
    public final void putCrumb(int tx, int ty, int number) {
        Debug.print(this, "agent " + getID() + " proposes to put a crumb");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
            this.generateActionOutcome(new InfPutCrumb(getEnvironment(), tx, ty, getID(), number)));
    }

    /**
     * Creates an influence of the type InfPickCrumb and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPickCrumb: try to pick up a crumb (or say: a number of crumbs)
     * from target area.
     *
     * @param tx     the x coordinate of the target area
     * @param ty     the y coordinate of the target area
     * @param number the number of crumbs to get from target area
     */
    public final void pickCrumb(int tx, int ty, int number) {
        Debug.print(this, "agent " + getID() + " proposes to put a crumb");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
            this.generateActionOutcome(new InfPickCrumb(getEnvironment(), tx, ty, getID(), number)));
    }





    private ActionOutcome generateActionOutcome(Influence influence) {
        return new ActionOutcome(getID(), true, getSyncSet(), influence);
    }

    private boolean hasSufficientEnergyDefault() {
        // Default actions such as putting crumbs, skipping, placing flags, ... all require a base amount of energy
        return this.hasSufficientEnergy(Agent.BATTERY_DECAY_SKIP);
    }

    private boolean hasSufficientEnergy(int required) {
        return this.getAgent().getBatteryState() >= required;
    }


    private void concludeWithCondition(boolean condition, Outcome onSuccess) {
        var onFail = this.generateActionOutcome(getAgent().getBatteryState() > 0 ?
                new InfSkip(getEnvironment(), getID()) : new InfNOP(this.getEnvironment()));

        this.concludePhaseWith(condition ? onSuccess : onFail);
    }





    //GET EN SETTERS

    /**
     * Returns the Item this agent is carrying or null
     *
     * @return getAgent().carry()
     */
    public Packet getCarry() {
        return getAgent().carry();
    }

    /**
     * Returns whether the agent is carrying something
     *
     * @return <code>true</code> if the agent has a carry, false otherwise
     */
    public boolean hasCarry() {
        return getAgent().carry() != null;
    }

    /**
     * Returns a copy of the Agent Item which represents this AgentImp in the
     * Environment
     *
     * @return getEnvironment().getAgentWorld().getAgent(getID())
     */
    protected Agent getAgent() {
        return getEnvironment().getAgentWorld().getAgent(getID());
    }

    /**
     * Returns the X coordinate of this agent
     */
    public int getX() {
        return getAgent().getX();
    }

    /**
     * Returns the Y coordinate of this agent
     */
    public int getY() {
        return getAgent().getY();
    }

    /**
     *Return the buffer for **outgoing** mails of this AgentImp.
     */
    private MailBuffer getMailBuffer() {
        return outgoingMails;
    }

    /**
     * Returns the current Behaviour
     */
    public Behaviour getCurrentBehaviour() {
        return lnkBehaviourState.getBehaviour();
    }

    /**
     * Assigns a new BehaviourState to this Agent implementation
     */
    public void setCurrentBehaviourState(BehaviourState bs) {
        lnkBehaviourState = bs;
    }



    /**
     * Returns the name of this agent
     */
    public String getName() {
        return getAgent().getName(); //""+getID();
    }


    /**
     * Indicates the number of synchronized cycles this agent has run through
     */
    public int getNbTurns() {
        return nbTurn;
    }

    /**
     *	Gets message at index from the message queue
     * @param index the index of the required message
     */
    public Mail getMessage(int index) {
        return messages.elementAt(index);
    }

    /**
     * Number of messages in the incoming message queue
     */
    public int getNbMessages() {
        return messages.size();
    }

    /**
     * Removes the message at index from the incoming message queue
     * @param index the index of the removed message
     */
    public void removeMessage(int index) {
        messages.removeElementAt(index);
    }


    /**
     * Retrieve the all the incoming messages.
     * @return A list with the received messages from other agents.
     */
    public Collection<Mail> getMessages() {
        return messages;
    }

    /**
     * Clear the incoming message queue.
     */
    public void clearMessages() {
        messages.clear();
    }





    public int getBatteryState() {
        return this.getAgent().getBatteryState();
    }


    /**
     * Returns the previous area the agent of this AgentImp stood on.
     *
     * @return a CellPerception of the previous position
     */
    public CellPerception getLastArea() {
        int lastX = getAgent().getLastX();
        int lastY = getAgent().getLastY();
        return getPerception().getCellPerceptionOnAbsPos(lastX, lastY);
    }

    /**
     * Returns whether this agent sees a destination of the same color of his
     * carry.
     *
     * @return <code>true</code> if this agent has a destination in sight
     * of the color matching to the color of the packet he's carrying,
     * <code>false</code> otherwise.
     */
    public boolean seeDestination() {
        int vw = getPerception().getWidth();
        int vh = getPerception().getHeight();
        if (hasCarry()) {
            for (int i = 0; i < vw; i++) {
                for (int j = 0; j < vh; j++) {
                    DestinationRep destRep = getPerception().getCellAt(i, j).getRepOfType(DestinationRep.class);
                    if (destRep != null && destRep.getColor() == getCarry().getColor()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns whether this agent can see a packet.
     *
     * @return <code>true</code> if this agent has a packet in sight, <code>false</code> otherwise.
     */
    public boolean seePacket() {
        if (hasCarry()) return false;

        int vw = getPerception().getWidth();
        int vh = getPerception().getHeight();
        for (int i = 0; i < vw; i++) {
            for (int j = 0; j < vh; j++) {
                if (getPerception().getCellAt(i, j).getRepOfType(PacketRep.class) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public void catchEvent(Event e) {
        if (e instanceof AgentActionEvent) {
            AgentActionEvent aae = (AgentActionEvent) e;
            if (aae.getAgent() == getAgent()) {
                var agent = this.getAgent();

                if (aae.getAction() == AgentActionEvent.STEP) {
                    if (hasCarry()) {
                        agent.updateBatteryState(-Agent.BATTERY_DECAY_STEP_WITH_CARRY);
                    } else {
                        agent.updateBatteryState(-Agent.BATTERY_DECAY_STEP);
                    }
                } else if (aae.getAction() == AgentActionEvent.LOADENERGY) {
                    agent.updateBatteryState(aae.getValue());
                } else if (aae.getAction() == AgentActionEvent.IDLE_ENERGY) {
                    agent.updateBatteryState(-Agent.BATTERY_DECAY_SKIP);
                }
            }
        }
    }






    // Memory related methods

    /**
     * Adds a memory fragment to this agent (if memory is not full).
     *
     * @param key: the key associated with the memory fragment
     * @param data: the memory fragment itself
     * @precondition getNbBeliefs < getMaxNbBeliefs
     */
    public void addMemoryFragment(String key, String data) {
        if (getNbMemoryFragments() < getMaxNbMemoryFragments()) { //TODO die 10 moet terug weg tijdelijk nodig
            memory.put(key, data);
        }
    }

    /**
     * Removes a memory fragment with given key from this agents memory.
     * @param key: the key of the belief to remove
     */
    public void removeMemoryFragment(String key) {
        memory.remove(key);
    }

    /**
     *  Gets a memory fragment with given key from this agents memory
     *  @param key: the key of the memory fragment to retrieve
     */
    public String getMemoryFragment(String key) {
        return memory.get(key);
    }

    /**
     * Returns the current number of memory fragments in memory of this agent
     */
    public int getNbMemoryFragments() {
        return memory.size();
    }

    /**
     * Returns the maximum number of memory fragments for this agent
     */
    public int getMaxNbMemoryFragments() {
        return maxBeliefs;
    }

    public Color getAgentColor() {
        Color color = null;
        if (getAgent().getColor().isPresent()) {
            color = getAgent().getColor().get();
        }
        return color;
    }






    /**
     * Stops this agent. See superclass. Finishes also beliefBase.
     */
    public void finish() {
        super.finish();
        memory.clear();
        memory = null;
    }




    // Other

    /**
     *Deliver the current contents of this AgentImp's mailBuffer and request for another communication-phase.
     */
    public final void continueCommunication() {
        CommunicationOutcome outcome =
                new CommunicationOutcome(getID(), true, getSyncSet(), "CC",
                        (MailBuffer) getMailBuffer().clone());
        Debug.print(this, "agentImp " + getID() + " sending his communication to ");

        Mail mail;
        for (int i = 0; i < outcome.getMailBuffer().getMails().length; i++) {
            mail = outcome.getMailBuffer().getMails()[i];
            Debug.print(this, mail.getTo() + "with the following message: " + mail.getMessage() + "\n");
        }

        getMailBuffer().clear();
        concludePhaseWith(outcome);
    }

    /**
     * Deliver the current contents of this AgentImp's mailBuffer and request to enter the action-phase.
     */
    public final void closeCommunication() {
        CommunicationOutcome outcome = new CommunicationOutcome(getID(), true,
                getSyncSet(), "EOC", (MailBuffer) getMailBuffer().clone());

        Debug.print(this, "agentImp " + getID() + " sending his communication to: ");

        Mail mail;
        for (int i = 0; i < outcome.getMailBuffer().getMails().length; i++) {
            mail = outcome.getMailBuffer().getMails()[i];
            Debug.print(this, mail.getFrom() + " sends to " + mail.getTo() +
                    " the following message: " + mail.getMessage());
        }
        getMailBuffer().clear();
        concludePhaseWith(outcome);
    }

    /**
     * Returns true if agent is in communication phase
     *
     * @return true if agent is in communication phase, false otherwise
     */
    public boolean inCommPhase() {
        return talking;
    }

    /**
     * Returns true if agent is in action phase
     *
     * @return true if agent is in action phase, false otherwise
     */
    public boolean inActionPhase() {
        return doing;
    }

    /**
     * Triggers an update of the behaviour. If necessary the agent changes
     * behaviour.
     */
    public void updateBehaviour() {
        Debug.print(this, "Agent " + getName() + " testing behaviours");
        Debug.print(this, "Agent " + getName() + " from " + getCurrentBehaviour().getClass().getName());
        lnkBehaviourState.testBehaviourChanges();
        Debug.print(this, "Agent " + getName() + " to " + getCurrentBehaviour().getClass().getName());
        BehaviourChangeEvent event = new BehaviourChangeEvent(this);
        event.setAgent(getID());
        event.setBehavName(getCurrentBehaviour().getClass().getSimpleName());
        Debug.print(this, event.getBehavName());
        EventManager.getInstance().throwEvent(event);
    }



    /**
     *  Creates the graph of behaviours and changes for this
     *  agentImplementation.
     * @postconditions getCurrentBehaviour <> null
     */
    public abstract void createBehaviour();


    //INTERFACE TO SYNCHRONIZER

    protected void cleanup() {
        //finishing up
        messages.removeAllElements();
        lnkBehaviourState.finish();
        lnkBehaviourState = null;
    }

    /**
     * Adds a message to the messages queue
     */
    public void receiveMessage(Mail msg) {
        messages.addElement(msg);
    }



    // AGENT IMP INTERFACE TO RUNNING THREAD

    /**
     * The run cycle of the thread associated with this AgentImp.
     */
    public void run() {
        if (initialRun) {
            percept();
            initialRun = false;
        }
        while (running) {
            checkSuspended();
            if (running) {
                if (checkSynchronize()) {
                    synchronize();
                }
                executeCurrentPhase();
            }
        }
        cleanup();
    }

    /**
     * Implements the execution of a synchronization phase.
     */
    protected void execCurrentPhase() {
        if (perceiving) {
            Debug.print(this, "AgentImp " + getID() + " starting perception");

            perception();
        } else if (talking) {
            Debug.print(this, "AgentImp " + getID() + " starting to talk");

            communication();
        } else if (doing) {
            Debug.print(this, "AgentImp " + getID() + " starting to do");

            action();
            AgentHandledEvent event = new AgentHandledEvent(this);
            event.setAgent(this);
            EventManager.getInstance().throwEvent(event);
        }
    }

    protected boolean environmentPermissionNeededForNextPhase() {
        return true;
    }

    /**
     * Perceive and notify Environment of the conclusion of this AgentImp's perception.
     */
    private void perception() {
        percept();
        PerceptionOutcome outcome = new PerceptionOutcome(getID(), true, getSyncSet());
        concludePhaseWith(outcome);
    }

    /**
     * Implements the communication phase
     */
    protected void communication() {
        updateBehaviour();
        getCurrentBehaviour().handle(this);
    }

    /**
     * Implements the action phase
     */
    protected void action() {
        getCurrentBehaviour().handle(this);
    }

    /**
      * Returns true if coordinate c is next to the agent
     */
    public boolean isNeighbour(Coordinate c) {
        var perception = getPerception();
        List<CellPerception> neighbours = Arrays.asList(perception.getNeighboursInOrder());
        for (CellPerception neighbour : neighbours) {
            if (neighbour == null) continue; // neighbours can be null when you're at the border of the world
            if (neighbour.getX() == c.getX() && neighbour.getY() == c.getY()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return The CellPerception of the cell with the best gradient that is a neighbour of the agent and is walkable
     */
    public CellPerception getCellWithBestGradient() {

        CellPerception cellWithBestGradient = null;
        int bestGradientValue = 0;

        for (CellPerception cell: getPerception().getNeighbours()) {
            if (cell != null && cell.getGradientRepresentation().isPresent()) {
                if (cell.isWalkable() && (cellWithBestGradient == null || cell.getGradientRepresentation().get().getValue() < bestGradientValue)) {
                    cellWithBestGradient = cell;
                    bestGradientValue = cell.getGradientRepresentation().get().getValue();
                }

            }
        }
        return cellWithBestGradient;
    }

    /**
     *
     * @return true if the agent should charge, depending on his battery buffer and how far the agent is away from
     * the closest charging point
     */
    public boolean shouldCharge() {
        int buffer = Agent.BATTERY_DECAY_STEP_WITH_CARRY * 4;

        int gradientValue;
        CellPerception currentCell = getPerception().getCellPerceptionOnRelPos(0, 0);

        if (currentCell.getGradientRepresentation().isPresent())
            gradientValue = currentCell.getGradientRepresentation().get().getValue();
        else
            gradientValue = -1;

        if (hasCarry())
            buffer += Agent.BATTERY_DECAY_SKIP;

        buffer += additional_batt_buffer;

        return gradientValue != -1 && getBatteryState() < gradientValue * Agent.BATTERY_DECAY_STEP + buffer;
    }

    /**
     *
     * @return true if the agent has a critical battery state
     */
    public boolean hasCriticalBatteryState() {
        int batteryState = getBatteryState();
        return batteryState <= Agent.BATTERY_DECAY_SKIP * 2 + Agent.BATTERY_DECAY_STEP;
    }

    /**
     *
     * @return a random move for the agent to take, the move is always walkable and makes sure the agent does not
     * retrace its step, except when this is the only possibility
     */
    public Coordinate generateRandomMove() {
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        // Shuffle moves randomly
        Collections.shuffle(moves);

        var perception = this.getPerception();

        for (var move : moves) {
            int x = move.getX();
            int y = move.getY();
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                if (getLastArea() != null && getLastArea().getX() == getX() + x && getLastArea().getY() == getY() + y) continue; // Don't undo a move

                return new Coordinate(getX() + x, getY() + y);
            }
        }
        // The implementation above does not permit to return on its steps, but if no other step is possible, the agent has to return on his steps.
        if (getLastArea() != null && getLastArea().isWalkable())
           return new Coordinate(getLastArea().getX(), getLastArea().getY());

        return null;
    }

    /**
     * Removes the given list of messages from the agent's message queue
     */
    public void removeMessages(ArrayList<Mail> messages) {
        for (Mail mail: messages) {
            removeMessage(new ArrayList<>(getMessages()).indexOf(mail));
        }
    }

    //ATTRIBUTES

    /**
     * @directed
     * @supplierCardinality 1
     * @label current behaviour
     */
    private BehaviourState lnkBehaviourState;
    private final Vector<Mail> messages;
    private final MailBuffer outgoingMails;


    /**
     * The memory of an agent has the form of a key mapped to a memory fragment (represented as String)
     * e.g.  "target" -> "3, 4"
     */
    private Map<String, String> memory;
    private final int maxBeliefs;

    public static final int DEFAULT_BELIEFS = 10;


}