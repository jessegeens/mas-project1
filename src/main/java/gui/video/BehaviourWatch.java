/* Generated by Together */

package gui.video;

import agent.AgentImplementations;
import gui.setup.Setup;
import util.event.BehaviourChangeEvent;
import util.event.Event;
import util.event.EventManager;
import util.event.Listener;

import javax.swing.*;

public class BehaviourWatch extends JFrame implements Listener {
    final AgentImplementations ais;
    final int[] ids;

    public BehaviourWatch() {
        EventManager.getInstance().addListener(this, BehaviourChangeEvent.class);
        ais = Setup.getInstance().getAgentImplementations();
        Object[][] data = new Object[ais.getNbAgents()][2];
        ids = ais.getAllAgentID();
        for (int i = 0; i < ais.getNbAgents(); i++) {
            data[i][0] = "" + ids[i];
        }
        Object[] columns = {
            "AgentID", "Behaviour"};
        jTable1 = new JTable(data, columns);
        jScrollPane1 = new JScrollPane(jTable1);
    }

    public void initialize() {
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(20);
        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);
        setSize(new java.awt.Dimension(300, 75 + jTable1.getRowHeight() * jTable1.getRowCount()));

        setTitle("Current Behaviours");
        jScrollPane1.getViewport().add(jTable1);
    }

    public void catchEvent(Event e) {
        if (e instanceof BehaviourChangeEvent) {
            BehaviourChangeEvent bce = (BehaviourChangeEvent) e;
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == bce.getAgent()) {
                    jTable1.getModel().setValueAt(bce.getBehavName(), i, 1);
                }
            }
        }
    }

    private final JTable jTable1;
    private final JScrollPane jScrollPane1;
}
