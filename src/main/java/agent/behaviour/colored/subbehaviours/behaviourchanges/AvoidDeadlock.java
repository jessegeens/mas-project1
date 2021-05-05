package agent.behaviour.colored.subbehaviours.behaviourchanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;

public class AvoidDeadlock extends BehaviourChange {

    private boolean enableAvoidDeadlock = false;

    @Override
    public boolean isSatisfied() {
        return enableAvoidDeadlock;
    }

    /**
     * The avoid deadlock behaviour makes an agent move randomly for a random number of steps. This is to make sure that
     * two agents that are together in deadlock dont come back to the same position. This counter of how many random steps
     * need to be taken is stored in the agent's memory.
     */
    @Override
    public void updateChange() {
      String memFrag = getAgentImp().getMemoryFragment(AgentImp.AVOID_DEADLOCK);
       if (memFrag == null) {
           enableAvoidDeadlock = false;
       } else {
           int ctr = Integer.parseInt(memFrag);
           if (ctr > 0) {
               enableAvoidDeadlock = true;
               getAgentImp().addMemoryFragment(AgentImp.AVOID_DEADLOCK, String.valueOf(ctr - 1));
           } else {
               enableAvoidDeadlock = false;
               getAgentImp().removeMemoryFragment(AgentImp.AVOID_DEADLOCK);
           }
       }
    }
}
