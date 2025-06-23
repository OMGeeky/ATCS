package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import com.gpl.rpg.atcontentstudio.model.gamedata.ActorCondition;
import com.gpl.rpg.atcontentstudio.model.gamedata.Common;
import com.gpl.rpg.atcontentstudio.model.gamedata.NPC;
import com.gpl.rpg.atcontentstudio.ui.*;
import com.gpl.rpg.atcontentstudio.utils.BasicLambda;
import com.gpl.rpg.atcontentstudio.utils.BasicLambdaWithArg;
import com.gpl.rpg.atcontentstudio.utils.BasicLambdaWithReturn;
import com.gpl.rpg.atcontentstudio.utils.UiUtils;
import com.jidesoft.swing.JideBoxLayout;

import javax.swing.*;
import java.awt.*;

import static com.gpl.rpg.atcontentstudio.ui.Editor.addIntegerField;

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

    public static class HitEffectPane {
        Common.HitEffect hitEffect;
        CollapsiblePanel hitEffectPane;
        JSpinner hitEffectHPMin;
        JSpinner hitEffectHPMax;
        JSpinner hitEffectAPMin;
        JSpinner hitEffectAPMax;
        NPCEditor.SourceTimedConditionsListModel hitSourceConditionsModel;
        NPCEditor.TargetTimedConditionsListModel hitTargetConditionsListModel;
        JList hitSourceConditionsList;
        JList hitTargetConditionsList;
        Common.TimedActorConditionEffect selectedHitEffectSourceCondition;
        Common.TimedActorConditionEffect selectedHitEffectTargetCondition;


        Editor.MyComboBox hitSourceConditionBox;
        JSpinner hitSourceConditionChance;
        JRadioButton hitSourceConditionClear;
        JRadioButton hitSourceConditionApply;
        JRadioButton hitSourceConditionImmunity;
        JSpinner hitSourceConditionMagnitude;
        JRadioButton hitSourceConditionTimed;
        JRadioButton hitSourceConditionForever;
        JSpinner hitSourceConditionDuration;

        Editor.MyComboBox hitTargetConditionBox;
        JSpinner hitTargetConditionChance;
        JRadioButton hitTargetConditionClear;
        JRadioButton hitTargetConditionApply;
        JRadioButton hitTargetConditionImmunity;
        JSpinner hitTargetConditionMagnitude;
        JRadioButton hitTargetConditionTimed;
        JRadioButton hitTargetConditionForever;
        JSpinner hitTargetConditionDuration;

        /*
         * create a new HitEffectPane with the selections (probably passed in from last time)
         */
        public HitEffectPane(Common.TimedActorConditionEffect selectedHitEffectSourceCondition, Common.TimedActorConditionEffect selectedHitEffectTargetCondition) {
            this.selectedHitEffectTargetCondition = selectedHitEffectTargetCondition;
            this.selectedHitEffectSourceCondition = selectedHitEffectSourceCondition;
        }

        void createHitEffectPaneContent(NPC npc, FieldUpdateListener listener, BasicLambdaWithArg<JPanel> updatePaneSource, BasicLambdaWithArg<JPanel> updatePaneTarget) {
            hitEffectPane = new CollapsiblePanel("Effect on every hit: ");
            hitEffectPane.setLayout(new JideBoxLayout(hitEffectPane, JideBoxLayout.PAGE_AXIS));
            if (npc.hit_effect == null) {
                hitEffect = new Common.HitEffect();
            } else {
                hitEffect = npc.hit_effect;
            }
            hitEffectHPMin = addIntegerField(hitEffectPane, "HP bonus min: ", hitEffect.hp_boost_min, true, npc.writable, listener);
            hitEffectHPMax = addIntegerField(hitEffectPane, "HP bonus max: ", hitEffect.hp_boost_max, true, npc.writable, listener);
            hitEffectAPMin = addIntegerField(hitEffectPane, "AP bonus min: ", hitEffect.ap_boost_min, true, npc.writable, listener);
            hitEffectAPMax = addIntegerField(hitEffectPane, "AP bonus max: ", hitEffect.ap_boost_max, true, npc.writable, listener);

            String titleSource = "Actor Conditions applied to the source: ";
            hitSourceConditionsModel = new NPCEditor.SourceTimedConditionsListModel(hitEffect);
            CommonEditor.TimedConditionsCellRenderer cellRendererSource = new CommonEditor.TimedConditionsCellRenderer();
            BasicLambdaWithArg<Common.TimedActorConditionEffect> selectedSetSource = (value)-> selectedHitEffectSourceCondition = value;
            BasicLambdaWithReturn<Common.TimedActorConditionEffect> selectedGetSource = ()-> selectedHitEffectSourceCondition ;
            BasicLambda selectedResetSource = ()-> selectedHitEffectSourceCondition = null;

            var resultSource = UiUtils.getCollapsibleItemList(listener,
                    hitSourceConditionsModel,
                    selectedResetSource,
                    selectedSetSource,
                    selectedGetSource,
                    (x) -> {},
                    updatePaneSource,
                    npc.writable,
                    Common.TimedActorConditionEffect::new,
                    cellRendererSource,
                    titleSource,
                    (x) -> null);
            hitSourceConditionsList = resultSource.list;
            CollapsiblePanel hitSourceConditionsPane = resultSource.collapsiblePanel;
            if (npc.hit_effect == null || npc.hit_effect.conditions_source == null || npc.hit_effect.conditions_source.isEmpty()) {
                hitSourceConditionsPane.collapse();
            }
            hitEffectPane.add(hitSourceConditionsPane, JideBoxLayout.FIX);

            String titleTarget = "Actor Conditions applied to the target: ";
            hitTargetConditionsListModel = new NPCEditor.TargetTimedConditionsListModel(hitEffect);
            CommonEditor.TimedConditionsCellRenderer cellRendererTarget = new CommonEditor.TimedConditionsCellRenderer();
            BasicLambdaWithArg<Common.TimedActorConditionEffect> selectedSetTarget = (value)-> selectedHitEffectTargetCondition = value;
            BasicLambdaWithReturn<Common.TimedActorConditionEffect> selectedGetTarget = ()-> selectedHitEffectTargetCondition ;
            BasicLambda selectedResetTarget = ()-> selectedHitEffectTargetCondition = null;
            var resultTarget = UiUtils.getCollapsibleItemList(listener,
                    hitTargetConditionsListModel,
                    selectedResetTarget,
                    selectedSetTarget,
                    selectedGetTarget,
                    (x) -> {},
                    updatePaneTarget,
                    npc.writable,
                    Common.TimedActorConditionEffect::new,
                    cellRendererTarget,
                    titleTarget,
                    (x) -> null);
            hitTargetConditionsList = resultTarget.list;
            CollapsiblePanel hitTargetConditionsPane = resultTarget.collapsiblePanel;
            if (npc.hit_effect == null || npc.hit_effect.conditions_target == null || npc.hit_effect.conditions_target.isEmpty()) {
                hitTargetConditionsPane.collapse();
            }
            hitEffectPane.add(hitTargetConditionsPane, JideBoxLayout.FIX);
        }

    }
}
