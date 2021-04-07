package agent.behaviour.droppacket.subbehaviours;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.world.gradient.GradientWorld;

public class InitialiseGradients extends LTDBehaviour {

    @Override
    public void act(AgentImp agent) {
        var gradientWorld = agent.getEnvironment()
                .getWorld(GradientWorld.class);
        // Invoke methods on GradientWorld
        // For example: own, newly added, method
        //gradientWorld.safelyInvokeNuclearKillSwitch();

    }

    @Override
    public void communicate(AgentImp agent) {

    }
}
