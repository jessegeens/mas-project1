package agent.behaviour.colored.subbehaviours.behaviourChanges;

import agent.AgentImp;
import agent.behaviour.BehaviourChange;

public class AvoidDeadlock extends BehaviourChange {

    private boolean enableAvoidDeadlock = false;

    @Override
    public boolean isSatisfied() {
        return enableAvoidDeadlock;
    }

    @Override
    public void updateChange() {
      String memFrag = getAgentImp().getMemoryFragment(AgentImp.AVOID_DEADLOCK);
       if(memFrag == null){
           enableAvoidDeadlock = false;
       } else {
           int ctr = Integer.parseInt(memFrag);
           if(ctr > 0){
               enableAvoidDeadlock = true;
               getAgentImp().addMemoryFragment(AgentImp.AVOID_DEADLOCK, String.valueOf(ctr - 1));
           }
           else{
               enableAvoidDeadlock = false;
               getAgentImp().removeMemoryFragment(AgentImp.AVOID_DEADLOCK);
           }
       }
    }
}
