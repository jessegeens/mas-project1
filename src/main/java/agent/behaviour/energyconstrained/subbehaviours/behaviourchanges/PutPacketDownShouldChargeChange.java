package agent.behaviour.energyconstrained.subbehaviours.behaviourchanges;

import agent.behaviour.BehaviourChange;

/**
 * This behaviour change takes into effect when an agent has put his packet down and is ready to go charge.
 * It is always true because this change has the lowest possible priority in the configuration.
 */
public class PutPacketDownShouldChargeChange extends BehaviourChange {
    @Override
    public boolean isSatisfied() {
        return true;
    }

    @Override
    public void updateChange() {

    }
}
