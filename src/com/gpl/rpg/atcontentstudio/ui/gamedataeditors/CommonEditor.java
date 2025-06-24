package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.Project;
import com.gpl.rpg.atcontentstudio.model.gamedata.ActorCondition;
import com.gpl.rpg.atcontentstudio.model.gamedata.Common;
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
import java.util.function.Supplier;

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

    public static class HitRecievedEffectPane<EFFECT extends Common.HitReceivedEffect, LIST_MODEL_SOURCE, ELEMENT extends Common.TimedActorConditionEffect, MODEL extends OrderedListenerListModel<LIST_MODEL_SOURCE, ELEMENT>> extends HitEffectPane<EFFECT, LIST_MODEL_SOURCE, ELEMENT, MODEL> {
        /// this should just be a convenience field, to access it, without casting. DO NOT SET WITHOUT ALSO SETTING THE FIELD IN THE SUPER-CLASS!
        EFFECT effect;
        private JSpinner hitReceivedEffectHPMinTarget;
        private JSpinner hitReceivedEffectHPMaxTarget;
        private JSpinner hitReceivedEffectAPMinTarget;
        private JSpinner hitReceivedEffectAPMaxTarget;

        public HitRecievedEffectPane(String title, Supplier<ELEMENT> sourceNewSupplier, Editor editor, String applyToHint, String applyToTargetHint) {
            super(title, sourceNewSupplier, editor, applyToHint, applyToTargetHint);
        }

        void createHitReceivedEffectPaneContent(FieldUpdateListener listener, boolean writable, EFFECT e, MODEL sourceConditionsModelInput, MODEL targetConditionsModelInput) {
            effect = e;
            createHitEffectPaneContent(listener, writable, e, sourceConditionsModelInput, targetConditionsModelInput);
        }
        @Override
        protected void addFields(FieldUpdateListener listener, boolean writable) {
            super.addFields(listener, writable);
            hitReceivedEffectHPMinTarget = addIntegerField(effectPane, "HP bonus min%s: ".formatted(applyToTargetHint), effect.target.hp_boost_min, true, writable, listener);
            hitReceivedEffectHPMaxTarget = addIntegerField(effectPane, "HP bonus max%s: ".formatted(applyToTargetHint), effect.target.hp_boost_max, true, writable, listener);
            hitReceivedEffectAPMinTarget = addIntegerField(effectPane, "AP bonus min%s: ".formatted(applyToTargetHint), effect.target.ap_boost_min, true, writable, listener);
            hitReceivedEffectAPMaxTarget = addIntegerField(effectPane, "AP bonus max%s: ".formatted(applyToTargetHint), effect.target.ap_boost_max, true, writable, listener);
        }


        @Override
        public boolean valueChanged(JComponent source, Object value, GameDataElement backlink) {
            boolean updateHitReceived = super.valueChanged(source, value, backlink);
            if (!updateHitReceived) {
                if (source == hitReceivedEffectHPMinTarget) {
                    effect.target.hp_boost_min = (Integer) value;
                    updateHitReceived = true;
                } else if (source == hitReceivedEffectHPMaxTarget) {
                    effect.target.hp_boost_max = (Integer) value;
                    updateHitReceived = true;
                } else if (source == hitReceivedEffectAPMinTarget) {
                    effect.target.ap_boost_min = (Integer) value;
                    updateHitReceived = true;
                } else if (source == hitReceivedEffectAPMaxTarget) {
                    effect.target.ap_boost_max = (Integer) value;
                    updateHitReceived = true;
                }
            }

            return updateHitReceived;
        }
    }

    public static class HitEffectPane<EFFECT extends Common.HitEffect, LIST_MODEL_SOURCE, ELEMENT extends Common.TimedActorConditionEffect, MODEL extends OrderedListenerListModel<LIST_MODEL_SOURCE, ELEMENT>> extends DeathEffectPane<EFFECT, LIST_MODEL_SOURCE, ELEMENT, MODEL> {
        /// this should just be a convenience field, to access it, without casting. DO NOT SET WITHOUT ALSO SETTING THE FIELD IN THE SUPER-CLASS!
        EFFECT effect;

        String applyToTargetHint;

        MODEL hitTargetConditionsListModel;
        JList hitTargetConditionsList;
        ELEMENT selectedHitEffectTargetCondition;

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
        public HitEffectPane(String title, Supplier<ELEMENT> sourceNewSupplier, Editor editor, String applyToHint, String applyToTargetHint) {
            super(title, sourceNewSupplier, editor, applyToHint);
            this.selectedHitEffectTargetCondition = selectedHitEffectTargetCondition;

            if (applyToTargetHint == null || applyToTargetHint == "") {
                applyToTargetHint = "";
            } else {
                applyToTargetHint = " (%s)".formatted(applyToTargetHint);
            }
            this.applyToTargetHint = applyToTargetHint;
        }

        void createHitEffectPaneContent(FieldUpdateListener listener, boolean writable, EFFECT e, MODEL sourceConditionsModelInput, MODEL hitTargetConditionsListModel1) {
            effect = e;
            hitTargetConditionsListModel = hitTargetConditionsListModel1;
            createDeathEffectPaneContent(listener, writable, e, sourceConditionsModelInput);
        }

        @Override
        protected void addLists(FieldUpdateListener listener, boolean writable) {
            super.addLists(listener, writable);

            String titleTarget = "Actor Conditions applied to the target%s: ".formatted(applyToTargetHint);
            CommonEditor.TimedConditionsCellRenderer cellRendererTarget = new CommonEditor.TimedConditionsCellRenderer();
            BasicLambdaWithArg<ELEMENT> selectedSetTarget = (value) -> selectedHitEffectTargetCondition = value;
            BasicLambdaWithReturn<ELEMENT> selectedGetTarget = () -> selectedHitEffectTargetCondition;
            BasicLambda selectedResetTarget = () -> selectedHitEffectTargetCondition = null;
            BasicLambdaWithArg<JPanel> updatePaneTarget = (editorPane) -> updateHitTargetTimedConditionEditorPane(editorPane, selectedHitEffectTargetCondition, listener, this.editor);

            var resultTarget = UiUtils.getCollapsibleItemList(listener,
                    hitTargetConditionsListModel,
                    selectedResetTarget,
                    selectedSetTarget,
                    selectedGetTarget,
                    (x) -> {
                    },
                    updatePaneTarget,
                    writable,
                    this.conditionSupplier,
                    cellRendererTarget,
                    titleTarget,
                    (x) -> null);
            hitTargetConditionsList = resultTarget.list;
            CollapsiblePanel hitTargetConditionsPane = resultTarget.collapsiblePanel;
            if (effect == null || effect.conditions_target == null || effect.conditions_target.isEmpty()) {
                hitTargetConditionsPane.collapse();
            }
            effectPane.add(hitTargetConditionsPane, JideBoxLayout.FIX);
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

        @Override
        public boolean valueChanged(JComponent source, Object value, GameDataElement backlink) {
            boolean updateHit = super.valueChanged(source, value, backlink);
            if (!updateHit) {
                if (source == hitTargetConditionsList) {
                    updateHit = true;
                } else if (source == hitTargetConditionBox) {
                    editor.updateConditionEffect((ActorCondition) value, backlink, selectedHitEffectTargetCondition, hitTargetConditionsListModel);
                } else if (source == hitTargetConditionClear && (Boolean) value) {
                    selectedHitEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                    selectedHitEffectTargetCondition.duration = null;
                    updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
                    hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
                    updateHit = true;
                } else if (source == hitTargetConditionApply && (Boolean) value) {
                    selectedHitEffectTargetCondition.magnitude = (Integer) hitTargetConditionMagnitude.getValue();
                    selectedHitEffectTargetCondition.duration = hitTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitTargetConditionDuration.getValue();
                    if (selectedHitEffectTargetCondition.duration == null || selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                        selectedHitEffectTargetCondition.duration = 1;
                    }
                    updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
                    hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
                    updateHit = true;
                } else if (source == hitTargetConditionImmunity && (Boolean) value) {
                    selectedHitEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                    selectedHitEffectTargetCondition.duration = hitTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitTargetConditionDuration.getValue();
                    if (selectedHitEffectTargetCondition.duration == null || selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                        selectedHitEffectTargetCondition.duration = 1;
                    }
                    updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
                    hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
                    updateHit = true;
                } else if (source == hitTargetConditionMagnitude) {
                    selectedHitEffectTargetCondition.magnitude = (Integer) value;
                    hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
                    updateHit = true;
                } else if (source == hitTargetConditionTimed && (Boolean) value) {
                    selectedHitEffectTargetCondition.duration = (Integer) hitTargetConditionDuration.getValue();
                    if (selectedHitEffectTargetCondition.duration == null || selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                        selectedHitEffectTargetCondition.duration = 1;
                    }
                    updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
                    hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
                    updateHit = true;
                } else if (source == hitTargetConditionForever && (Boolean) value) {
                    selectedHitEffectTargetCondition.duration = ActorCondition.DURATION_FOREVER;
                    updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
                    hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
                    updateHit = true;
                } else if (source == hitTargetConditionDuration) {
                    selectedHitEffectTargetCondition.duration = (Integer) value;
                    hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
                    updateHit = true;
                } else if (source == hitTargetConditionChance) {
                    selectedHitEffectTargetCondition.chance = (Double) value;
                    hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
                }
            }

            return updateHit;
        }
    }

    public static class DeathEffectPane<EFFECT extends Common.DeathEffect, LIST_MODEL_SOURCE, ELEMENT extends Common.TimedActorConditionEffect, MODEL extends OrderedListenerListModel<LIST_MODEL_SOURCE, ELEMENT>> {
        protected Supplier<ELEMENT> conditionSupplier;
        protected String title;
        protected Editor editor;
        protected String applyToHint;


        EFFECT effect;
        CollapsiblePanel effectPane;
        JSpinner effectHPMin;
        JSpinner effectHPMax;
        JSpinner effectAPMin;
        JSpinner effectAPMax;
        MODEL sourceConditionsModel;
        JList<ELEMENT> sourceConditionsList;
        ELEMENT selectedEffectSourceCondition;

        Editor.MyComboBox sourceConditionBox;
        JSpinner sourceConditionChance;
        JRadioButton sourceConditionClear;
        JRadioButton sourceConditionApply;
        JRadioButton sourceConditionImmunity;
        JSpinner sourceConditionMagnitude;
        JRadioButton sourceConditionTimed;
        JRadioButton sourceConditionForever;
        JSpinner sourceConditionDuration;

        /*
         * create a new DeatchEffectPane with the selections (probably passed in from last time)
         */
        public DeathEffectPane(String title, Supplier<ELEMENT> conditionSupplier, Editor editor, String applyToHint) {
            this.title = title;
            this.conditionSupplier = conditionSupplier;
            this.editor = editor;
            this.applyToHint = applyToHint;
        }

        void createDeathEffectPaneContent(FieldUpdateListener listener, boolean writable, EFFECT e, MODEL sourceConditionsModel) {
            effect = e;
            this.sourceConditionsModel = sourceConditionsModel;

            if (applyToHint == null || applyToHint == "") {
                applyToHint = "";
            } else {
                applyToHint = " (%s)".formatted(applyToHint);
            }
            effectPane = new CollapsiblePanel(title);
            effectPane.setLayout(new JideBoxLayout(effectPane, JideBoxLayout.PAGE_AXIS));


            addFields(listener, writable);
            addLists(listener, writable);
        }

        protected void addFields(FieldUpdateListener listener, boolean writable) {
            effectHPMin = addIntegerField(effectPane, "HP bonus min%s: ".formatted(applyToHint), effect.hp_boost_min, true, writable, listener);
            effectHPMax = addIntegerField(effectPane, "HP bonus max%s: ".formatted(applyToHint), effect.hp_boost_max, true, writable, listener);
            effectAPMin = addIntegerField(effectPane, "AP bonus min%s: ".formatted(applyToHint), effect.ap_boost_min, true, writable, listener);
            effectAPMax = addIntegerField(effectPane, "AP bonus max%s: ".formatted(applyToHint), effect.ap_boost_max, true, writable, listener);
        }

        protected void addLists(FieldUpdateListener listener, boolean writable) {
            String titleSource = "Actor Conditions applied to the source%s: ".formatted(applyToHint);
            TimedConditionsCellRenderer cellRendererSource = new TimedConditionsCellRenderer();
            BasicLambdaWithArg<ELEMENT> selectedSetSource = (value) -> selectedEffectSourceCondition = value;
            BasicLambdaWithReturn<ELEMENT> selectedGetSource = () -> selectedEffectSourceCondition;
            BasicLambda selectedResetSource = () -> selectedEffectSourceCondition = null;
            BasicLambdaWithArg<JPanel> updatePaneSource = (editorPane) -> updateEffectSourceTimedConditionEditorPane(editorPane, selectedEffectSourceCondition, listener, editor);

            var resultSource = UiUtils.getCollapsibleItemList(listener,
                    this.sourceConditionsModel,
                    selectedResetSource,
                    selectedSetSource,
                    selectedGetSource,
                    (x) -> {
                    },
                    updatePaneSource,
                    writable,
                    conditionSupplier,
                    cellRendererSource,
                    titleSource,
                    (x) -> null);
            sourceConditionsList = resultSource.list;
            CollapsiblePanel sourceConditionsPane = resultSource.collapsiblePanel;
            if (effect == null || effect.conditions_source == null || effect.conditions_source.isEmpty()) {
                sourceConditionsPane.collapse();
            }
            effectPane.add(sourceConditionsPane, JideBoxLayout.FIX);
        }

        public void updateEffectSourceTimedConditionEditorPane(JPanel pane, ELEMENT condition, final FieldUpdateListener listener, Editor e) {
            pane.removeAll();
            if (sourceConditionBox != null) {
                e.removeElementListener(sourceConditionBox);
            }

            boolean writable = e.target.writable;
            Project proj = e.target.getProject();

            sourceConditionBox = e.addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
            sourceConditionChance = e.addDoubleField(pane, "Chance: ", condition.chance, writable, listener);

            sourceConditionClear = new JRadioButton("Clear active condition");
            pane.add(sourceConditionClear, JideBoxLayout.FIX);
            sourceConditionApply = new JRadioButton("Apply condition with magnitude");
            pane.add(sourceConditionApply, JideBoxLayout.FIX);
            sourceConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
            sourceConditionImmunity = new JRadioButton("Give immunity to condition");
            pane.add(sourceConditionImmunity, JideBoxLayout.FIX);

            ButtonGroup radioEffectGroup = new ButtonGroup();
            radioEffectGroup.add(sourceConditionApply);
            radioEffectGroup.add(sourceConditionClear);
            radioEffectGroup.add(sourceConditionImmunity);

            sourceConditionTimed = new JRadioButton("For a number of rounds");
            pane.add(sourceConditionTimed, JideBoxLayout.FIX);
            sourceConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, 1, false, writable, listener);
            sourceConditionForever = new JRadioButton("Forever");
            pane.add(sourceConditionForever, JideBoxLayout.FIX);

            ButtonGroup radioDurationGroup = new ButtonGroup();
            radioDurationGroup.add(sourceConditionTimed);
            radioDurationGroup.add(sourceConditionForever);

            updateEffectSourceTimedConditionWidgets(condition);

            sourceConditionClear.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(sourceConditionClear, new Boolean(sourceConditionClear.isSelected()));
                }
            });
            sourceConditionApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(sourceConditionApply, new Boolean(sourceConditionApply.isSelected()));
                }
            });
            sourceConditionImmunity.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(sourceConditionImmunity, new Boolean(sourceConditionImmunity.isSelected()));
                }
            });

            sourceConditionTimed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(sourceConditionTimed, new Boolean(sourceConditionTimed.isSelected()));
                }
            });
            sourceConditionForever.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.valueChanged(sourceConditionForever, new Boolean(sourceConditionForever.isSelected()));
                }
            });
            pane.revalidate();
            pane.repaint();
        }


        public void updateEffectSourceTimedConditionWidgets(ELEMENT condition) {
            boolean immunity = condition.isImmunity();
            boolean clear = condition.isClear();
            boolean forever = condition.isInfinite();

            sourceConditionClear.setSelected(clear);
            sourceConditionApply.setSelected(!clear && !immunity);
            sourceConditionMagnitude.setEnabled(!clear && !immunity);
            sourceConditionImmunity.setSelected(immunity);

            sourceConditionTimed.setSelected(!forever);
            sourceConditionTimed.setEnabled(!clear);
            sourceConditionDuration.setEnabled(!clear && !forever);
            sourceConditionForever.setSelected(forever);
            sourceConditionForever.setEnabled(!clear);
        }

        public boolean valueChanged(JComponent source, Object value, GameDataElement backlink) {
            boolean updateHit = false;
            if (source == effectHPMin) {
                effect.hp_boost_min = (Integer) value;
                updateHit = true;
            } else if (source == effectHPMax) {
                effect.hp_boost_max = (Integer) value;
                updateHit = true;
            } else if (source == effectAPMin) {
                effect.ap_boost_min = (Integer) value;
                updateHit = true;
            } else if (source == effectAPMax) {
                effect.ap_boost_max = (Integer) value;
                updateHit = true;
            } else if (source == sourceConditionsList) {
                updateHit = true;
            } else if (source == sourceConditionBox) {
                if (selectedEffectSourceCondition.condition != null) {
                    selectedEffectSourceCondition.condition.removeBacklink(backlink);
                }
                selectedEffectSourceCondition.condition = (ActorCondition) value;
                if (selectedEffectSourceCondition.condition != null) {
                    selectedEffectSourceCondition.condition.addBacklink(backlink);
                    selectedEffectSourceCondition.condition_id = selectedEffectSourceCondition.condition.id;
                } else {
                    selectedEffectSourceCondition.condition_id = null;
                }
                sourceConditionsModel.itemChanged(selectedEffectSourceCondition);
            } else if (source == sourceConditionClear && (Boolean) value) {
                selectedEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedEffectSourceCondition.duration = null;
                updateEffectSourceTimedConditionWidgets(selectedEffectSourceCondition);
                sourceConditionsModel.itemChanged(selectedEffectSourceCondition);
                updateHit = true;
            } else if (source == sourceConditionApply && (Boolean) value) {
                selectedEffectSourceCondition.magnitude = (Integer) sourceConditionMagnitude.getValue();
                selectedEffectSourceCondition.duration = sourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) sourceConditionDuration.getValue();
                if (selectedEffectSourceCondition.duration == null || selectedEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedEffectSourceCondition.duration = 1;
                }
                updateEffectSourceTimedConditionWidgets(selectedEffectSourceCondition);
                sourceConditionsModel.itemChanged(selectedEffectSourceCondition);
                updateHit = true;
            } else if (source == sourceConditionImmunity && (Boolean) value) {
                selectedEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedEffectSourceCondition.duration = sourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) sourceConditionDuration.getValue();
                if (selectedEffectSourceCondition.duration == null || selectedEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedEffectSourceCondition.duration = 1;
                }
                updateEffectSourceTimedConditionWidgets(selectedEffectSourceCondition);
                sourceConditionsModel.itemChanged(selectedEffectSourceCondition);
                updateHit = true;
            } else if (source == sourceConditionMagnitude) {
                selectedEffectSourceCondition.magnitude = (Integer) value;
                sourceConditionsModel.itemChanged(selectedEffectSourceCondition);
                updateHit = true;
            } else if (source == sourceConditionTimed && (Boolean) value) {
                selectedEffectSourceCondition.duration = (Integer) sourceConditionDuration.getValue();
                if (selectedEffectSourceCondition.duration == null || selectedEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedEffectSourceCondition.duration = 1;
                }
                updateEffectSourceTimedConditionWidgets(selectedEffectSourceCondition);
                sourceConditionsModel.itemChanged(selectedEffectSourceCondition);
                updateHit = true;
            } else if (source == sourceConditionForever && (Boolean) value) {
                selectedEffectSourceCondition.duration = ActorCondition.DURATION_FOREVER;
                updateEffectSourceTimedConditionWidgets(selectedEffectSourceCondition);
                sourceConditionsModel.itemChanged(selectedEffectSourceCondition);
                updateHit = true;
            } else if (source == sourceConditionDuration) {
                selectedEffectSourceCondition.duration = (Integer) value;
                sourceConditionsModel.itemChanged(selectedEffectSourceCondition);
                updateHit = true;
            } else if (source == sourceConditionChance) {
                selectedEffectSourceCondition.chance = (Double) value;
                sourceConditionsModel.itemChanged(selectedEffectSourceCondition);
            }
            return updateHit;
        }
    }
}
