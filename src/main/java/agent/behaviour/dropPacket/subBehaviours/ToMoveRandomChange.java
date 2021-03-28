package agent.behaviour.dropPacket.subBehaviours;

import agent.behaviour.BehaviourChange;

public class ToMoveRandomChange extends BehaviourChange {
    @Override
    public boolean isSatisfied() {
        System.out.println(getAgentImp().getName() + ": checking satisfied");
        return true; //No specific condition but should get the lowest priority.
    }

    @Override
    public void updateChange() {

    }
}
