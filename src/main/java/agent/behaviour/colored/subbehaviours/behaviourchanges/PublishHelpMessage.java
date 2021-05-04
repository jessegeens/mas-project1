package agent.behaviour.colored.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;

/**
 * The behaviour change when the agent needs help and should publish this message.
 */
public class PublishHelpMessage extends BehaviourChange {

    /**
     * A boolean whether the agent has a help message.
     */
    private Boolean hasHelpMessage = false;

    /**
     *
     * @return Returns true if the agent has a help message.
     */
    @Override
    public boolean isSatisfied() {
        return hasHelpMessage;
    }

    /**
     * Checks whether the agent has a help message in his memory.
     */
    @Override
    public void updateChange() {
        hasHelpMessage = getAgentImp().getMemoryFragment(AgentImp.HELP_MESSAGE_KEY) != null;
    }
}
