package agent.behaviour.colored.subbehaviours.behaviourChanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;

public class PublishHelpMessage extends BehaviourChange {

    private Boolean hasHelpMessage = false;

    @Override
    public boolean isSatisfied() {
        return hasHelpMessage;
    }

    @Override
    public void updateChange() {
        hasHelpMessage = getAgentImp().getMemoryFragment(AgentImp.HELP_MESSAGE_KEY) != null;
    }
}
