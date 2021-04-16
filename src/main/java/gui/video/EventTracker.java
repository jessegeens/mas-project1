package gui.video;

import environment.Coordinate;
import environment.Environment;
import environment.world.agent.Agent;
import gui.setup.Setup;
import org.json.JSONArray;
import org.json.JSONObject;
import util.event.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EventTracker {
    public EventTracker(Consumer<ActionUpdate> consumer) {
        this.callback = consumer;

        this.historyPackets = new ArrayList<>();
        this.historyMoves = new ArrayList<>();
        this.historyEnergy = new ArrayList<>();

        this.energySpent = 0;
        this.totalPackets = getEnvironment().getPacketWorld().getNbPackets();

        EventManager.getInstance().addListener(this::addAgentAction, AgentActionEvent.class);
        EventManager.getInstance().addListener(this::addEnergyEvent, EnergyUpdateEvent.class);
    }

    /**
     * Add the given agent action to the history of actions (if actions should be logged).
     *
     * @param e The AgentAction event.
     */
    private void addAgentAction(Event e) {
        AgentActionEvent event = (AgentActionEvent) e;
        int time = this.getEnvironment().getTime();


        var packet = event.getPacket();
        var agentId = event.getAgent() == null ? -1 : event.getAgent().getID();

        switch (event.getAction()) {
            case AgentActionEvent.PICK: {
                var action = new PacketAction(packet.getX(), packet.getY(),
                        PacketAction.Mode.Pickup, agentId, time);
                this.historyPackets.add(action);
                this.callback.accept(action);
                break;
            }
            case AgentActionEvent.PUT: {
                var action = new PacketAction(event.getToX(), event.getToY(),
                        PacketAction.Mode.Drop, agentId, time);
                this.historyPackets.add(action);
                this.callback.accept(action);
                break;
            }
            case AgentActionEvent.DELIVER: {
                var action = new PacketAction(event.getToX(), event.getToY(),
                        PacketAction.Mode.Delivery, agentId, time);
                this.historyPackets.add(action);
                this.callback.accept(action);
                break;
            }
            case AgentActionEvent.STEP: {
                var action = new AgentMove(event.getFromX(), event.getFromY(),
                        event.getToX(), event.getToY(), agentId, time);
                this.historyMoves.add(action);
                this.callback.accept(action);

                // Add additional energy consumption when stepping (also depending on carry)
                this.energySpent +=
                        (event.getAgent().hasCarry() ? Agent.BATTERY_DECAY_STEP_WITH_CARRY : Agent.BATTERY_DECAY_STEP)
                                - Agent.BATTERY_DECAY_SKIP;
                break;
            }
            case AgentActionEvent.LOADENERGY: {
                // Special case -> no energy cost when charging
                this.energySpent -= Agent.BATTERY_DECAY_SKIP;
            }
        }

        // Agent action base energy cost
        this.energySpent += Agent.BATTERY_DECAY_SKIP;
    }

    /**
     * Add the given energy update to the history of energy updates.
     *
     * @param e The event containing the energy update.
     */
    private void addEnergyEvent(Event e) {
        EnergyUpdateEvent event = (EnergyUpdateEvent) e;

        int time = this.getEnvironment().getTime();

        var agentId = event.getAgent().getID();
        var action = new EnergyUpdate(event.getEnergyPercentage(), event.isIncreased(), agentId, time);
        this.historyEnergy.add(action);
        this.callback.accept(action);
    }


    public JSONObject getHistoryJSON() {
        JSONObject head = new JSONObject();

        // Meta information about the run
        JSONObject meta = new JSONObject();
        meta.put("TotalCycles", getEnvironment().getTime());
        meta.put("TotalPackets", this.totalPackets);
        meta.put("PacketsDelivered", this.historyPackets.stream()
                .filter(p -> p.mode == PacketAction.Mode.Delivery)
                .count());
        meta.put("EnergyConsumed", this.energySpent);

        JSONObject moves = new JSONObject();
        moves.put("Key", new JSONArray(new String[] {"Cycle", "AgentID", "FromX", "FromY", "ToX", "ToY"}));
        moves.put("Data", new JSONArray(historyMoves.stream()
                .map(AgentMove::toJSONArray)
                .collect(Collectors.toList())));


        JSONObject packetPickup = new JSONObject();
        packetPickup.put("Key", new JSONArray(new String[] {"Cycle", "AgentID", "PacketX", "PacketY"}));
        packetPickup.put("Data", new JSONArray(historyPackets.stream()
                .filter(p -> p.mode == PacketAction.Mode.Pickup)
                .map(PacketAction::toJSONArray)
                .collect(Collectors.toList())));


        JSONObject packetDelivery = new JSONObject();
        packetDelivery.put("Key", new JSONArray(new String[] {"Cycle", "AgentID", "DestinationX", "DestinationY"}));
        packetDelivery.put("Data", new JSONArray(historyPackets.stream()
                .filter(p -> p.mode == PacketAction.Mode.Delivery)
                .map(PacketAction::toJSONArray)
                .collect(Collectors.toList())));


        JSONObject packetDrop = new JSONObject();
        packetDrop.put("Key", new JSONArray(new String[] {"Cycle", "AgentID", "DropX", "DropY"}));
        packetDrop.put("Data", new JSONArray(historyPackets.stream()
                .filter(p -> p.mode == PacketAction.Mode.Drop)
                .map(PacketAction::toJSONArray)
                .collect(Collectors.toList())));


        JSONObject energyUpdate = new JSONObject();
        energyUpdate.put("Key", new JSONArray(new String[] {"Cycle", "AgentID", "Operator", "Percentage"}));
        energyUpdate.put("Data", new JSONArray(historyEnergy.stream()
                .map(EnergyUpdate::toJSONArray)
                .collect(Collectors.toList())));


        head.put("Meta", meta);
        head.put("Moves", moves);
        head.put("PacketPickups", packetPickup);
        head.put("PacketDeliveries", packetDelivery);
        head.put("PacketDrops", packetDrop);
        head.put("EnergyUpdates", energyUpdate);

        return head;
    }


    private Environment getEnvironment() {
        return Setup.getInstance().getEnvironment();
    }

    public int getEnergySpent() {
        return this.energySpent;
    }

    public boolean isRunFinished() {
    	return this.totalPackets == this.historyPackets.stream()
    		.filter(p -> p.mode == PacketAction.Mode.Delivery)
    		.count();
    }


    public void reset() {
        this.historyMoves.clear();
        this.historyPackets.clear();
        this.historyEnergy.clear();
        this.totalPackets = getEnvironment().getPacketWorld().getNbPackets();
        this.energySpent = 0;
    }


    private final Consumer<ActionUpdate> callback;

    private int totalPackets;
    private int energySpent;

    // Cycle -> packet pickup, put or delivery
    private final List<PacketAction> historyPackets;
    // Cycle -> agent move
    private final List<AgentMove> historyMoves;
    // Cycle -> energy update
    private final List<EnergyUpdate> historyEnergy;







    public abstract static class ActionUpdate {
        int agentId;
        int cycle;

        public abstract String toString();

        public int getAgentId() {
            return this.agentId;
        }

        public int getCycle() {
            return this.cycle;
        }
    }

    private static class AgentMove extends ActionUpdate {
        Coordinate moveFrom;
        Coordinate moveTo;

        AgentMove(int fromX, int fromY, int toX, int toY, int agentId, int cycle) {
            this.agentId = agentId;
            this.cycle = cycle;
            this.moveFrom = new Coordinate(fromX, fromY);
            this.moveTo = new Coordinate(toX, toY);
        }

        @Override
        public String toString() {
            return "Moved from " + this.moveFrom.toString() + " to " + this.moveTo.toString();
        }

        public JSONArray toJSONArray() {
            return new JSONArray(new Integer[] {this.cycle, this.agentId, this.moveFrom.getX(), this.moveFrom.getY(),
                    this.moveTo.getX(), this.moveTo.getY()});
        }
    }

    private static class PacketAction extends ActionUpdate {
        Coordinate packetLocation;
        PacketAction.Mode mode;

        PacketAction(int x, int y, PacketAction.Mode mode, int agentId, int cycle) {
            this.agentId = agentId;
            this.cycle = cycle;
            this.packetLocation = new Coordinate(x, y);
            this.mode = mode;
        }

        @Override
        public String toString() {
            return this.mode.toString() + " of packet at location " + this.packetLocation.toString();
        }

        public JSONArray toJSONArray() {
            return new JSONArray(new Integer[] {this.cycle, this.agentId, this.packetLocation.getX(), this.packetLocation.getY()});
        }

        enum Mode {
            Pickup,
            Drop,
            Delivery
        }
    }

    private static class EnergyUpdate extends ActionUpdate {
        int percentage;
        boolean isIncreased;

        EnergyUpdate(int percentage, boolean isIncreased, int agentId, int cycle) {
            this.agentId = agentId;
            this.cycle = cycle;
            this.percentage = percentage;
            this.isIncreased = isIncreased;
        }

        @Override
        public String toString() {
            return String.format("Energy %s %d%%", this.isIncreased ? "increased above (or equal to)" : "dropped below", this.percentage);
        }

        public JSONArray toJSONArray() {
            return new JSONArray(new Object[] {this.cycle, this.agentId, this.isIncreased ? ">=" : "<", this.percentage});
        }
    }
}
