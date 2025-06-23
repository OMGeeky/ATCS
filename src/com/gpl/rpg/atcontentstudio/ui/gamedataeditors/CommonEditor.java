package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import com.gpl.rpg.atcontentstudio.model.gamedata.ActorCondition;
import com.gpl.rpg.atcontentstudio.model.gamedata.Common;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;
import com.gpl.rpg.atcontentstudio.ui.OverlayIcon;

import javax.swing.*;
import java.awt.*;

public class CommonEditor {

    public static class TimedConditionsCellRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 7987880146189575234L;

        @Override
        public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (c instanceof JLabel) {
                JLabel label = ((JLabel) c);
                Common.TimedActorConditionEffect effect = (Common.TimedActorConditionEffect) value;

                if (effect.condition != null) {

                    boolean immunity = effect.isImmunity();
                    boolean clear = effect.isClear();
                    boolean forever = effect.isInfinite();

                    if (clear) {
                        label.setIcon(new ImageIcon(effect.condition.getIcon()));
                        label.setText(effect.chance + "% chances to clear actor condition " + effect.condition.getDesc());
                    } else if (immunity) {
                        label.setIcon(new OverlayIcon(effect.condition.getIcon(), DefaultIcons.getImmunityIcon()));
                        label.setText(effect.chance + "% chances to give immunity to " + effect.condition.getDesc() + (forever ? " forever" : " for " + effect.duration + " rounds"));
                    } else {
                        label.setIcon(new ImageIcon(effect.condition.getIcon()));
                        label.setText(effect.chance + "% chances to give actor condition " + effect.condition.getDesc() + " x" + effect.magnitude + (forever ? " forever" : " for " + effect.duration + " rounds"));
                    }
                } else {
                    label.setText("New, undefined actor condition effect.");
                }
            }
            return c;
        }
    }
    public static class ConditionsCellRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 7987880146189575234L;

        @Override
        public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (c instanceof JLabel) {
                JLabel label = ((JLabel) c);
                Common.ActorConditionEffect effect = (Common.ActorConditionEffect) value;

                if (effect.condition != null) {
                    if (effect.magnitude == ActorCondition.MAGNITUDE_CLEAR) {
                        label.setIcon(new OverlayIcon(effect.condition.getIcon(), DefaultIcons.getImmunityIcon()));
                        label.setText("Immune to actor condition " + effect.condition.getDesc());
                    } else {
                        label.setIcon(new ImageIcon(effect.condition.getIcon()));
                        label.setText("Give actor condition " + effect.condition.getDesc() + " x" + effect.magnitude);
                    }
                } else {
                    label.setText("New, undefined actor condition effect.");
                }
            }
            return c;
        }
    }
}
