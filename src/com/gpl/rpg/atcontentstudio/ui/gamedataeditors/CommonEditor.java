package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import com.gpl.rpg.atcontentstudio.model.Project;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public static class HitEffectPane extends DeathEffectPane {
        Common.HitEffect hitEffect;
        CollapsiblePanel hitEffectPane;
        JSpinner hitEffectHPMin;
        JSpinner hitEffectHPMax;
        JSpinner hitEffectAPMin;
        JSpinner hitEffectAPMax;
        NPCEditor.TargetTimedConditionsListModel hitTargetConditionsListModel;
        JList hitSourceConditionsList;
        JList hitTargetConditionsList;
        Common.TimedActorConditionEffect selectedHitEffectTargetCondition;

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
            super(selectedHitEffectSourceCondition);
            this.selectedHitEffectTargetCondition = selectedHitEffectTargetCondition;
        }

        void createHitEffectPaneContent(NPC npc, FieldUpdateListener listener, Editor editor) {
            createDeathEffectPaneContent(npc, listener, editor);

            String titleTarget = "Actor Conditions applied to the target: ";
            hitTargetConditionsListModel = new NPCEditor.TargetTimedConditionsListModel(hitEffect);
            CommonEditor.TimedConditionsCellRenderer cellRendererTarget = new CommonEditor.TimedConditionsCellRenderer();
            BasicLambdaWithArg<Common.TimedActorConditionEffect> selectedSetTarget = (value)-> selectedHitEffectTargetCondition = value;
            BasicLambdaWithReturn<Common.TimedActorConditionEffect> selectedGetTarget = ()-> selectedHitEffectTargetCondition ;
            BasicLambda selectedResetTarget = ()-> selectedHitEffectTargetCondition = null;
            BasicLambdaWithArg<JPanel> updatePaneTarget = (editorPane) -> updateHitTargetTimedConditionEditorPane(editorPane, selectedHitEffectTargetCondition, listener, editor);

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

        public void updateHitTargetTimedConditionEditorPane(JPanel pane, Common.TimedActorConditionEffect condition, final FieldUpdateListener listener, Editor e) {
            pane.removeAll();
            if (hitTargetConditionBox != null) {
                e.removeElementListener(hitTargetConditionBox);
            }

            boolean writable = e.target.writable;
            Project proj = e.target.getProject();

            hitTargetConditionBox = e.addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
            hitTargetConditionChance = e.addDoubleField(pane, "Chance: ", condition.chance, writable, listener);
            hitTargetConditionClear = new JRadioButton("Clear active condition");
            pane.add(hitTargetConditionClear, JideBoxLayout.FIX);
            hitTargetConditionApply = new JRadioButton("Apply condition with magnitude");
            pane.add(hitTargetConditionApply, JideBoxLayout.FIX);
            hitTargetConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
            hitTargetConditionImmunity = new JRadioButton("Give immunity to condition");
            pane.add(hitTargetConditionImmunity, JideBoxLayout.FIX);

            ButtonGroup radioEffectGroup = new ButtonGroup();
            radioEffectGroup.add(hitTargetConditionApply);
            radioEffectGroup.add(hitTargetConditionClear);
            radioEffectGroup.add(hitTargetConditionImmunity);

            hitTargetConditionTimed = new JRadioButton("For a number of rounds");
            pane.add(hitTargetConditionTimed, JideBoxLayout.FIX);
            hitTargetConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, 1, false, writable, listener);
            hitTargetConditionForever = new JRadioButton("Forever");
            pane.add(hitTargetConditionForever, JideBoxLayout.FIX);

            ButtonGroup radioDurationGroup = new ButtonGroup();
            radioDurationGroup.add(hitTargetConditionTimed);
            radioDurationGroup.add(hitTargetConditionForever);

            updateHitTargetTimedConditionWidgets(condition);

            hitTargetConditionClear.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(hitTargetConditionClear, new Boolean(hitTargetConditionClear.isSelected()));
                }
            });
            hitTargetConditionApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(hitTargetConditionApply, new Boolean(hitTargetConditionApply.isSelected()));
                }
            });
            hitTargetConditionImmunity.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(hitTargetConditionImmunity, new Boolean(hitTargetConditionImmunity.isSelected()));
                }
            });

            hitTargetConditionTimed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(hitTargetConditionTimed, new Boolean(hitTargetConditionTimed.isSelected()));
                }
            });
            hitTargetConditionForever.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(hitTargetConditionForever, new Boolean(hitTargetConditionForever.isSelected()));
                }
            });
            pane.revalidate();
            pane.repaint();
        }

        public void updateHitTargetTimedConditionWidgets(Common.TimedActorConditionEffect condition) {
            boolean immunity = condition.isImmunity();
            boolean clear = condition.isClear();
            boolean forever = condition.isInfinite();

            hitTargetConditionClear.setSelected(clear);
            hitTargetConditionApply.setSelected(!clear && !immunity);
            hitTargetConditionMagnitude.setEnabled(!clear && !immunity);
            hitTargetConditionImmunity.setSelected(immunity);
            hitTargetConditionTimed.setSelected(!forever);
            hitTargetConditionTimed.setEnabled(!clear);
            hitTargetConditionDuration.setEnabled(!clear && !forever);
            hitTargetConditionForever.setSelected(forever);
            hitTargetConditionForever.setEnabled(!clear);

        }
    }

    public static class DeathEffectPane {
        Common.DeathEffect hitEffect;
        CollapsiblePanel hitEffectPane;
        JSpinner hitEffectHPMin;
        JSpinner hitEffectHPMax;
        JSpinner hitEffectAPMin;
        JSpinner hitEffectAPMax;
        NPCEditor.SourceTimedConditionsListModel hitSourceConditionsModel;
        JList hitSourceConditionsList;
        Common.TimedActorConditionEffect selectedHitEffectSourceCondition;

        Editor.MyComboBox hitSourceConditionBox;
        JSpinner hitSourceConditionChance;
        JRadioButton hitSourceConditionClear;
        JRadioButton hitSourceConditionApply;
        JRadioButton hitSourceConditionImmunity;
        JSpinner hitSourceConditionMagnitude;
        JRadioButton hitSourceConditionTimed;
        JRadioButton hitSourceConditionForever;
        JSpinner hitSourceConditionDuration;

        /*
         * create a new DeatchEffectPane with the selections (probably passed in from last time)
         */
        public DeathEffectPane(Common.TimedActorConditionEffect selectedHitEffectSourceCondition) {
            this.selectedHitEffectSourceCondition = selectedHitEffectSourceCondition;
        }

        void createDeathEffectPaneContent(NPC npc, FieldUpdateListener listener, Editor editor) {
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
            BasicLambdaWithArg<JPanel> updatePaneSource =(editorPane) -> updateDeathEffectSourceTimedConditionEditorPane(editorPane, selectedHitEffectSourceCondition, listener, editor);

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
        }

        public void updateDeathEffectSourceTimedConditionEditorPane(JPanel pane, Common.TimedActorConditionEffect condition, final FieldUpdateListener listener, Editor e) {
            pane.removeAll();
            if (hitSourceConditionBox != null) {
                e.removeElementListener(hitSourceConditionBox);
            }

            boolean writable = e.target.writable;
            Project proj = e.target.getProject();

            hitSourceConditionBox = e.addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
            hitSourceConditionChance = e.addDoubleField(pane, "Chance: ", condition.chance, writable, listener);

            hitSourceConditionClear = new JRadioButton("Clear active condition");
            pane.add(hitSourceConditionClear, JideBoxLayout.FIX);
            hitSourceConditionApply = new JRadioButton("Apply condition with magnitude");
            pane.add(hitSourceConditionApply, JideBoxLayout.FIX);
            hitSourceConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
            hitSourceConditionImmunity = new JRadioButton("Give immunity to condition");
            pane.add(hitSourceConditionImmunity, JideBoxLayout.FIX);

            ButtonGroup radioEffectGroup = new ButtonGroup();
            radioEffectGroup.add(hitSourceConditionApply);
            radioEffectGroup.add(hitSourceConditionClear);
            radioEffectGroup.add(hitSourceConditionImmunity);

            hitSourceConditionTimed = new JRadioButton("For a number of rounds");
            pane.add(hitSourceConditionTimed, JideBoxLayout.FIX);
            hitSourceConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, 1, false, writable, listener);
            hitSourceConditionForever = new JRadioButton("Forever");
            pane.add(hitSourceConditionForever, JideBoxLayout.FIX);

            ButtonGroup radioDurationGroup = new ButtonGroup();
            radioDurationGroup.add(hitSourceConditionTimed);
            radioDurationGroup.add(hitSourceConditionForever);

            updateDeathEffectSourceTimedConditionWidgets(condition);

            hitSourceConditionClear.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(hitSourceConditionClear, new Boolean(hitSourceConditionClear.isSelected()));
                }
            });
            hitSourceConditionApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(hitSourceConditionApply, new Boolean(hitSourceConditionApply.isSelected()));
                }
            });
            hitSourceConditionImmunity.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(hitSourceConditionImmunity, new Boolean(hitSourceConditionImmunity.isSelected()));
                }
            });

            hitSourceConditionTimed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(hitSourceConditionTimed, new Boolean(hitSourceConditionTimed.isSelected()));
                }
            });
            hitSourceConditionForever.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(hitSourceConditionForever, new Boolean(hitSourceConditionForever.isSelected()));
                }
            });
            pane.revalidate();
            pane.repaint();
        }


        public void updateDeathEffectSourceTimedConditionWidgets(Common.TimedActorConditionEffect condition) {
            boolean immunity = condition.isImmunity();
            boolean clear = condition.isClear();
            boolean forever = condition.isInfinite();

            hitSourceConditionClear.setSelected(clear);
            hitSourceConditionApply.setSelected(!clear && !immunity);
            hitSourceConditionMagnitude.setEnabled(!clear && !immunity);
            hitSourceConditionImmunity.setSelected(immunity);

            hitSourceConditionTimed.setSelected(!forever);
            hitSourceConditionTimed.setEnabled(!clear);
            hitSourceConditionDuration.setEnabled(!clear && !forever);
            hitSourceConditionForever.setSelected(forever);
            hitSourceConditionForever.setEnabled(!clear);
        }
    }
}
