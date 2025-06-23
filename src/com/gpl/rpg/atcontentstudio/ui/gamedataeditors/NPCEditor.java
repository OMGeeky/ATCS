package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import com.gpl.rpg.atcontentstudio.ATContentStudio;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.Project;
import com.gpl.rpg.atcontentstudio.model.ProjectTreeNode;
import com.gpl.rpg.atcontentstudio.model.gamedata.*;
import com.gpl.rpg.atcontentstudio.model.sprites.Spritesheet;
import com.gpl.rpg.atcontentstudio.ui.*;
import com.gpl.rpg.atcontentstudio.ui.gamedataeditors.dialoguetree.DialogueGraphView;
import com.gpl.rpg.atcontentstudio.utils.BasicLambda;
import com.gpl.rpg.atcontentstudio.utils.BasicLambdaWithArg;
import com.gpl.rpg.atcontentstudio.utils.BasicLambdaWithReturn;
import com.gpl.rpg.atcontentstudio.utils.UiUtils;
import com.jidesoft.swing.JideBoxLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class NPCEditor extends JSONElementEditor {

    private static final long serialVersionUID = 4001483665523721800L;

    private static final String form_view_id = "Form";
    private static final String json_view_id = "JSON";
    private static final String dialogue_tree_id = "Dialogue Tree";

    private Common.TimedActorConditionEffect selectedHitReceivedEffectSourceCondition;
    private Common.TimedActorConditionEffect selectedHitReceivedEffectTargetCondition;

    private JButton npcIcon;
    private JTextField idField;
    private JTextField nameField;
    private JTextField spawnGroupField;
    private JTextField factionField;
    private JSpinner experienceField;
    private MyComboBox dialogueBox;
    private MyComboBox droplistBox;
    @SuppressWarnings("rawtypes")
    private JComboBox monsterClassBox;
    private IntegerBasedCheckBox uniqueBox;
    @SuppressWarnings("rawtypes")
    private JComboBox moveTypeBox;

    private CollapsiblePanel combatTraitPane;
    private JSpinner maxHP;
    private JSpinner maxAP;
    private JSpinner moveCost;
    private JSpinner atkDmgMin;
    private JSpinner atkDmgMax;
    private JSpinner atkCost;
    private JSpinner atkChance;
    private JSpinner critSkill;
    private JSpinner critMult;
    private JSpinner blockChance;
    private JSpinner dmgRes;

    private CommonEditor.HitEffectPane hitEffectPane;


    private Common.HitReceivedEffect hitReceivedEffect;
    private CollapsiblePanel hitReceivedEffectPane;
    private JSpinner hitReceivedEffectHPMin;
    private JSpinner hitReceivedEffectHPMax;
    private JSpinner hitReceivedEffectAPMin;
    private JSpinner hitReceivedEffectAPMax;
    private JSpinner hitReceivedEffectHPMinTarget;
    private JSpinner hitReceivedEffectHPMaxTarget;
    private JSpinner hitReceivedEffectAPMinTarget;
    private JSpinner hitReceivedEffectAPMaxTarget;

    private SourceTimedConditionsListModel hitReceivedSourceConditionsListModel;
    @SuppressWarnings("rawtypes")
    private JList hitReceivedSourceConditionsList;
    private MyComboBox hitReceivedSourceConditionBox;
    private JSpinner hitReceivedSourceConditionChance;
    private JRadioButton hitReceivedSourceConditionClear;
    private JRadioButton hitReceivedSourceConditionApply;
    private JRadioButton hitReceivedSourceConditionImmunity;
    private JSpinner hitReceivedSourceConditionMagnitude;
    private JRadioButton hitReceivedSourceConditionTimed;
    private JRadioButton hitReceivedSourceConditionForever;
    private JSpinner hitReceivedSourceConditionDuration;

    private TargetTimedConditionsListModel hitReceivedTargetConditionsListModel;
    @SuppressWarnings("rawtypes")
    private JList hitReceivedTargetConditionsList;
    private MyComboBox hitReceivedTargetConditionBox;
    private JSpinner hitReceivedTargetConditionChance;
    private JRadioButton hitReceivedTargetConditionClear;
    private JRadioButton hitReceivedTargetConditionApply;
    private JRadioButton hitReceivedTargetConditionImmunity;
    private JSpinner hitReceivedTargetConditionMagnitude;
    private JRadioButton hitReceivedTargetConditionTimed;
    private JRadioButton hitReceivedTargetConditionForever;
    private JSpinner hitReceivedTargetConditionDuration;

    private CommonEditor.DeathEffectPane deathEffectPane;

    private JPanel dialogueGraphPane;
    private DialogueGraphView dialogueGraphView;

    public NPCEditor(NPC npc) {
        super(npc, npc.getDesc(), npc.getIcon());
        addEditorTab(form_view_id, getFormView());
        addEditorTab(json_view_id, getJSONView());
        if (npc.dialogue != null) {
            createDialogueGraphView(npc);
            addEditorTab(dialogue_tree_id, dialogueGraphPane);
        }
    }

    public JPanel createDialogueGraphView(final NPC npc) {
        dialogueGraphPane = new JPanel();
        dialogueGraphPane.setLayout(new BorderLayout());

        dialogueGraphView = new DialogueGraphView(npc.dialogue, npc);
        dialogueGraphPane.add(dialogueGraphView, BorderLayout.CENTER);

        JPanel buttonPane = UiUtils.createRefreshButtonPane(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadGraphView(npc);
            }
        });
        dialogueGraphPane.add(buttonPane, BorderLayout.NORTH);


        return dialogueGraphPane;
    }

    public void reloadGraphView(NPC npc) {
        if (npc.dialogue != null) {
            if (dialogueGraphPane != null) {
                dialogueGraphPane.remove(dialogueGraphView);
                dialogueGraphView = new DialogueGraphView(npc.dialogue, npc);
                dialogueGraphPane.add(dialogueGraphView, BorderLayout.CENTER);
                dialogueGraphPane.revalidate();
                dialogueGraphPane.repaint();
            } else {
                createDialogueGraphView(npc);
                addEditorTab(dialogue_tree_id, dialogueGraphPane);
            }
        } else {
            if (dialogueGraphPane != null) {
                removeEditorTab(dialogue_tree_id);
                dialogueGraphPane = null;
                dialogueGraphView = null;
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void insertFormViewDataField(JPanel pane) {
        final NPC npc = (NPC) target;

        final FieldUpdateListener listener = new NPCFieldUpdater();

        npcIcon = createButtonPane(pane, npc.getProject(), npc, NPC.class, npc.getImage(), Spritesheet.Category.monster, listener);

        idField = addTextField(pane, "Internal ID: ", npc.id, npc.writable, listener);
        nameField = addTranslatableTextField(pane, "Display name: ", npc.name, npc.writable, listener);
        spawnGroupField = addTextField(pane, "Spawn group ID: ", npc.spawngroup_id, npc.writable, listener);
        factionField = addTextField(pane, "Faction ID: ", npc.faction_id, npc.writable, listener);
        experienceField = addIntegerField(pane, "Experience reward: ", npc.getMonsterExperience(), false, false, listener);
        dialogueBox = addDialogueBox(pane, npc.getProject(), "Initial phrase: ", npc.dialogue, npc.writable, listener);
        droplistBox = addDroplistBox(pane, npc.getProject(), "Droplist / Shop inventory: ", npc.droplist, npc.writable, listener);
        monsterClassBox = addEnumValueBox(pane, "Monster class: ", NPC.MonsterClass.values(), npc.monster_class, npc.writable, listener);
        uniqueBox = addIntegerBasedCheckBox(pane, "Unique", npc.unique, npc.writable, listener);
        moveTypeBox = addEnumValueBox(pane, "Movement type: ", NPC.MovementType.values(), npc.movement_type, npc.writable, listener);
        combatTraitPane = new CollapsiblePanel("Combat traits: ");
        combatTraitPane.setLayout(new JideBoxLayout(combatTraitPane, JideBoxLayout.PAGE_AXIS, 6));
        maxHP = addIntegerField(combatTraitPane, "Max HP: ", npc.max_hp, 1, false, npc.writable, listener);
        maxAP = addIntegerField(combatTraitPane, "Max AP: ", npc.max_ap, 10, false, npc.writable, listener);
        moveCost = addIntegerField(combatTraitPane, "Move cost: ", npc.move_cost, 10, false, npc.writable, listener);
        atkDmgMin = addIntegerField(combatTraitPane, "Attack Damage min: ", npc.attack_damage_min, false, npc.writable, listener);
        atkDmgMax = addIntegerField(combatTraitPane, "Attack Damage max: ", npc.attack_damage_max, false, npc.writable, listener);
        atkCost = addIntegerField(combatTraitPane, "Attack cost: ", npc.attack_cost, 10, false, npc.writable, listener);
        atkChance = addIntegerField(combatTraitPane, "Attack chance: ", npc.attack_chance, false, npc.writable, listener);
        critSkill = addIntegerField(combatTraitPane, "Critical skill: ", npc.critical_skill, false, npc.writable, listener);
        critMult = addDoubleField(combatTraitPane, "Critical multiplier: ", npc.critical_multiplier, npc.writable, listener);
        blockChance = addIntegerField(combatTraitPane, "Block chance: ", npc.block_chance, false, npc.writable, listener);
        dmgRes = addIntegerField(combatTraitPane, "Damage resistance: ", npc.damage_resistance, false, npc.writable, listener);

        Common.HitEffect hitEffect;
        if (npc.hit_effect == null) {
            hitEffect = new Common.HitEffect();
        } else {
            hitEffect = npc.hit_effect;
        }
        if (hitEffectPane == null)
            hitEffectPane = new CommonEditor.HitEffectPane("Effect on every hit: ", Common.TimedActorConditionEffect::new, this, "test");
        hitEffectPane.createHitEffectPaneContent(listener,
                npc.writable,
                hitEffect,
                new SourceTimedConditionsListModel(hitEffect));
        combatTraitPane.add(hitEffectPane.effectPane, JideBoxLayout.FIX);

        hitReceivedEffectPane = new CollapsiblePanel("Effect on every hit received: ");
        hitReceivedEffectPane.setLayout(new JideBoxLayout(hitReceivedEffectPane, JideBoxLayout.PAGE_AXIS));
        if (npc.hit_received_effect == null) {
            hitReceivedEffect = new Common.HitReceivedEffect();
        } else {
            hitReceivedEffect = npc.hit_received_effect;
        }
        hitReceivedEffectHPMin = addIntegerField(hitReceivedEffectPane, "NPC HP bonus min: ", hitReceivedEffect.hp_boost_min, true, npc.writable, listener);
        hitReceivedEffectHPMax = addIntegerField(hitReceivedEffectPane, "NPC HP bonus max: ", hitReceivedEffect.hp_boost_max, true, npc.writable, listener);
        hitReceivedEffectAPMin = addIntegerField(hitReceivedEffectPane, "NPC AP bonus min: ", hitReceivedEffect.ap_boost_min, true, npc.writable, listener);
        hitReceivedEffectAPMax = addIntegerField(hitReceivedEffectPane, "NPC AP bonus max: ", hitReceivedEffect.ap_boost_max, true, npc.writable, listener);
        hitReceivedEffectHPMinTarget = addIntegerField(hitReceivedEffectPane, "Attacker HP bonus min: ", hitReceivedEffect.target.hp_boost_min, true, npc.writable, listener);
        hitReceivedEffectHPMaxTarget = addIntegerField(hitReceivedEffectPane, "Attacker HP bonus max: ", hitReceivedEffect.target.hp_boost_max, true, npc.writable, listener);
        hitReceivedEffectAPMinTarget = addIntegerField(hitReceivedEffectPane, "Attacker AP bonus min: ", hitReceivedEffect.target.ap_boost_min, true, npc.writable, listener);
        hitReceivedEffectAPMaxTarget = addIntegerField(hitReceivedEffectPane, "Attacker AP bonus max: ", hitReceivedEffect.target.ap_boost_max, true, npc.writable, listener);

        String titleReceivedSource = "Actor Conditions applied to this NPC: ";
        hitReceivedSourceConditionsListModel = new SourceTimedConditionsListModel(hitReceivedEffect);
        CommonEditor.TimedConditionsCellRenderer cellRendererReceivedSource = new CommonEditor.TimedConditionsCellRenderer();
        BasicLambdaWithArg<Common.TimedActorConditionEffect> selectedSetReceivedSource = (value)->selectedHitReceivedEffectSourceCondition = value;
        BasicLambdaWithReturn<Common.TimedActorConditionEffect> selectedGetReceivedSource = ()->selectedHitReceivedEffectSourceCondition ;
        BasicLambda selectedResetReceivedSource = ()->selectedHitReceivedEffectSourceCondition = null;
        BasicLambdaWithArg<JPanel> updatePaneReceivedSource = (editorPane) -> updateHitReceivedSourceTimedConditionEditorPane(editorPane, selectedHitReceivedEffectSourceCondition, listener);
        var resultReceivedSource = UiUtils.getCollapsibleItemList(listener,
                hitReceivedSourceConditionsListModel,
                selectedResetReceivedSource,
                selectedSetReceivedSource,
                selectedGetReceivedSource,
                (x) -> {},
                updatePaneReceivedSource,
                npc.writable,
                Common.TimedActorConditionEffect::new,
                cellRendererReceivedSource,
                titleReceivedSource,
                (x) -> null);
        hitReceivedSourceConditionsList = resultReceivedSource.list;
        CollapsiblePanel hitReceivedSourceConditionsPane = resultReceivedSource.collapsiblePanel;
        if (npc.hit_received_effect == null || npc.hit_received_effect.conditions_source == null || npc.hit_received_effect.conditions_source.isEmpty()) {
            hitReceivedSourceConditionsPane.collapse();
        }
        hitReceivedEffectPane.add(hitReceivedSourceConditionsPane, JideBoxLayout.FIX);

        String titleReceivedTarget = "Actor Conditions applied to the attacker: ";
        hitReceivedTargetConditionsListModel = new TargetTimedConditionsListModel(hitReceivedEffect);
        CommonEditor.TimedConditionsCellRenderer cellRendererReceivedTarget = new CommonEditor.TimedConditionsCellRenderer();
        BasicLambdaWithArg<Common.TimedActorConditionEffect> selectedSetReceivedTarget = (value)->selectedHitReceivedEffectTargetCondition = value;
        BasicLambdaWithReturn<Common.TimedActorConditionEffect> selectedGetReceivedTarget = ()->selectedHitReceivedEffectTargetCondition ;
        BasicLambda selectedResetReceivedTarget = ()->selectedHitReceivedEffectTargetCondition = null;
        BasicLambdaWithArg<JPanel> updatePaneReceivedTarget = (editorPane) -> updateHitReceivedTargetTimedConditionEditorPane(editorPane, selectedHitReceivedEffectTargetCondition, listener);
        var resultReceivedTarget = UiUtils.getCollapsibleItemList(listener,
                hitReceivedTargetConditionsListModel,
                selectedResetReceivedTarget,
                selectedSetReceivedTarget,
                selectedGetReceivedTarget,
                (x) -> {},
                updatePaneReceivedTarget,
                npc.writable,
                Common.TimedActorConditionEffect::new,
                cellRendererReceivedTarget,
                titleReceivedTarget,
                (x) -> null);
        hitReceivedTargetConditionsList = resultReceivedTarget.list;
        CollapsiblePanel hitReceivedTargetConditionsPane = resultReceivedTarget.collapsiblePanel;
        if (npc.hit_received_effect == null || npc.hit_received_effect.conditions_target == null || npc.hit_received_effect.conditions_target.isEmpty()) {
            hitReceivedTargetConditionsPane.collapse();
        }
        combatTraitPane.add(hitReceivedEffectPane, JideBoxLayout.FIX);

        Common.DeathEffect deathEffect;
        if (npc.death_effect == null) {
            deathEffect = new Common.DeathEffect();
        } else {
            deathEffect = npc.death_effect;
        }
        if (deathEffectPane == null)
            deathEffectPane = new CommonEditor.DeathEffectPane("Effect when killed: ", Common.TimedActorConditionEffect::new, this, "Killer");
        deathEffectPane.createDeathEffectPaneContent(listener,
                npc.writable,
                deathEffect,
                new SourceTimedConditionsListModel(deathEffect)
        );
        combatTraitPane.add(deathEffectPane.effectPane, JideBoxLayout.FIX);

        pane.add(combatTraitPane, JideBoxLayout.FIX);
    }


    public void updateHitSourceTimedConditionWidgets(Common.TimedActorConditionEffect condition) {
        hitEffectPane.updateEffectSourceTimedConditionWidgets(condition);
    }


    public void updateHitTargetTimedConditionWidgets(Common.TimedActorConditionEffect condition) {
        hitEffectPane.updateHitTargetTimedConditionWidgets(condition);
    }


    public void updateHitReceivedSourceTimedConditionEditorPane(JPanel pane, Common.TimedActorConditionEffect condition, final FieldUpdateListener listener) {
        pane.removeAll();
        if (hitReceivedSourceConditionBox != null) {
            removeElementListener(hitReceivedSourceConditionBox);
        }

        boolean writable = ((NPC) target).writable;
        Project proj = ((NPC) target).getProject();

        hitReceivedSourceConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
        hitReceivedSourceConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);

        hitReceivedSourceConditionClear = new JRadioButton("Clear active condition");
        pane.add(hitReceivedSourceConditionClear, JideBoxLayout.FIX);
        hitReceivedSourceConditionApply = new JRadioButton("Apply condition with magnitude");
        pane.add(hitReceivedSourceConditionApply, JideBoxLayout.FIX);
        hitReceivedSourceConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
        hitReceivedSourceConditionImmunity = new JRadioButton("Give immunity to condition");
        pane.add(hitReceivedSourceConditionImmunity, JideBoxLayout.FIX);

        ButtonGroup radioEffectGroup = new ButtonGroup();
        radioEffectGroup.add(hitReceivedSourceConditionApply);
        radioEffectGroup.add(hitReceivedSourceConditionClear);
        radioEffectGroup.add(hitReceivedSourceConditionImmunity);

        hitReceivedSourceConditionTimed = new JRadioButton("For a number of rounds");
        pane.add(hitReceivedSourceConditionTimed, JideBoxLayout.FIX);
        hitReceivedSourceConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, 1, false, writable, listener);
        hitReceivedSourceConditionForever = new JRadioButton("Forever");
        pane.add(hitReceivedSourceConditionForever, JideBoxLayout.FIX);

        ButtonGroup radioDurationGroup = new ButtonGroup();
        radioDurationGroup.add(hitReceivedSourceConditionTimed);
        radioDurationGroup.add(hitReceivedSourceConditionForever);

        updateHitReceivedSourceTimedConditionWidgets(condition);

        hitReceivedSourceConditionClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedSourceConditionClear, new Boolean(hitReceivedSourceConditionClear.isSelected()));
            }
        });
        hitReceivedSourceConditionApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedSourceConditionApply, new Boolean(hitReceivedSourceConditionApply.isSelected()));
            }
        });
        hitReceivedSourceConditionImmunity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedSourceConditionImmunity, new Boolean(hitReceivedSourceConditionImmunity.isSelected()));
            }
        });

        hitReceivedSourceConditionTimed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedSourceConditionTimed, new Boolean(hitReceivedSourceConditionTimed.isSelected()));
            }
        });
        hitReceivedSourceConditionForever.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedSourceConditionForever, new Boolean(hitReceivedSourceConditionForever.isSelected()));
            }
        });
        pane.revalidate();
        pane.repaint();
    }

    public void updateHitReceivedSourceTimedConditionWidgets(Common.TimedActorConditionEffect condition) {
        boolean immunity = condition.isImmunity();
        boolean clear = condition.isClear();
        boolean forever = condition.isInfinite();

        hitReceivedSourceConditionClear.setSelected(clear);
        hitReceivedSourceConditionApply.setSelected(!clear && !immunity);
        hitReceivedSourceConditionMagnitude.setEnabled(!clear && !immunity);
        hitReceivedSourceConditionImmunity.setSelected(immunity);

        hitReceivedSourceConditionTimed.setSelected(!forever);
        hitReceivedSourceConditionTimed.setEnabled(!clear);
        hitReceivedSourceConditionDuration.setEnabled(!clear && !forever);
        hitReceivedSourceConditionForever.setSelected(forever);
        hitReceivedSourceConditionForever.setEnabled(!clear);
    }


    public void updateHitReceivedTargetTimedConditionEditorPane(JPanel pane, Common.TimedActorConditionEffect condition, final FieldUpdateListener listener) {
        pane.removeAll();
        if (hitReceivedTargetConditionBox != null) {
            removeElementListener(hitReceivedTargetConditionBox);
        }

        boolean writable = ((NPC) target).writable;
        Project proj = ((NPC) target).getProject();

        hitReceivedTargetConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
        hitReceivedTargetConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);
        hitReceivedTargetConditionClear = new JRadioButton("Clear active condition");
        pane.add(hitReceivedTargetConditionClear, JideBoxLayout.FIX);
        hitReceivedTargetConditionApply = new JRadioButton("Apply condition with magnitude");
        pane.add(hitReceivedTargetConditionApply, JideBoxLayout.FIX);
        hitReceivedTargetConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
        hitReceivedTargetConditionImmunity = new JRadioButton("Give immunity to condition");
        pane.add(hitReceivedTargetConditionImmunity, JideBoxLayout.FIX);

        ButtonGroup radioEffectGroup = new ButtonGroup();
        radioEffectGroup.add(hitReceivedTargetConditionApply);
        radioEffectGroup.add(hitReceivedTargetConditionClear);
        radioEffectGroup.add(hitReceivedTargetConditionImmunity);

        hitReceivedTargetConditionTimed = new JRadioButton("For a number of rounds");
        pane.add(hitReceivedTargetConditionTimed, JideBoxLayout.FIX);
        hitReceivedTargetConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, 1, false, writable, listener);
        hitReceivedTargetConditionForever = new JRadioButton("Forever");
        pane.add(hitReceivedTargetConditionForever, JideBoxLayout.FIX);

        ButtonGroup radioDurationGroup = new ButtonGroup();
        radioDurationGroup.add(hitReceivedTargetConditionTimed);
        radioDurationGroup.add(hitReceivedTargetConditionForever);

        updateHitReceivedTargetTimedConditionWidgets(condition);

        hitReceivedTargetConditionClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedTargetConditionClear, new Boolean(hitReceivedTargetConditionClear.isSelected()));
            }
        });
        hitReceivedTargetConditionApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedTargetConditionApply, new Boolean(hitReceivedTargetConditionApply.isSelected()));
            }
        });
        hitReceivedTargetConditionImmunity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedTargetConditionImmunity, new Boolean(hitReceivedTargetConditionImmunity.isSelected()));
            }
        });

        hitReceivedTargetConditionTimed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedTargetConditionTimed, new Boolean(hitReceivedTargetConditionTimed.isSelected()));
            }
        });
        hitReceivedTargetConditionForever.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedTargetConditionForever, new Boolean(hitReceivedTargetConditionForever.isSelected()));
            }
        });
        pane.revalidate();
        pane.repaint();
    }

    public void updateHitReceivedTargetTimedConditionWidgets(Common.TimedActorConditionEffect condition) {

        boolean immunity = condition.isImmunity();
        boolean clear = condition.isClear();
        boolean forever = condition.isInfinite();

        hitReceivedTargetConditionClear.setSelected(clear);
        hitReceivedTargetConditionApply.setSelected(!clear && !immunity);
        hitReceivedTargetConditionMagnitude.setEnabled(!clear && !immunity);
        hitReceivedTargetConditionImmunity.setSelected(immunity);

        hitReceivedTargetConditionTimed.setSelected(!forever);
        hitReceivedTargetConditionTimed.setEnabled(!clear);
        hitReceivedTargetConditionDuration.setEnabled(!clear && !forever);
        hitReceivedTargetConditionForever.setSelected(forever);
        hitReceivedTargetConditionForever.setEnabled(!clear);
    }

    public void updateDeathSourceTimedConditionWidgets(Common.TimedActorConditionEffect condition) {
        deathEffectPane.updateEffectSourceTimedConditionWidgets(condition);
    }

    public static class TargetTimedConditionsListModel extends OrderedListenerListModel<Common.HitEffect, Common.TimedActorConditionEffect> {
        public TargetTimedConditionsListModel(Common.HitEffect effect) {
            super(effect);
        }

        @Override
        protected List<Common.TimedActorConditionEffect> getItems() {
            return source.conditions_target;
        }

        @Override
        protected void setItems(List<Common.TimedActorConditionEffect> items) {
            source.conditions_target = items;
        }
    }

    public static class SourceTimedConditionsListModel extends OrderedListenerListModel<Common.DeathEffect, Common.TimedActorConditionEffect> {
        public SourceTimedConditionsListModel(Common.DeathEffect effect) {
            super(effect);
        }

        @Override
        protected List<Common.TimedActorConditionEffect> getItems() {
            return source.conditions_source;
        }

        @Override
        protected void setItems(List<Common.TimedActorConditionEffect> items) {
            source.conditions_source = items;
        }
    }

    public static boolean isNull(Common.HitEffect effect) {
        if (effect.ap_boost_min != null) return false;
        if (effect.ap_boost_max != null) return false;
        if (effect.hp_boost_min != null) return false;
        if (effect.hp_boost_max != null) return false;
        if (effect.conditions_source != null) return false;
        if (effect.conditions_target != null) return false;
        return true;
    }

    public static boolean isNull(Common.HitReceivedEffect effect) {
        if (effect.ap_boost_min != null) return false;
        if (effect.ap_boost_max != null) return false;
        if (effect.hp_boost_min != null) return false;
        if (effect.hp_boost_max != null) return false;
        if (effect.target.ap_boost_min != null) return false;
        if (effect.target.ap_boost_max != null) return false;
        if (effect.target.hp_boost_min != null) return false;
        if (effect.target.hp_boost_max != null) return false;
        if (effect.conditions_source != null) return false;
        if (effect.conditions_target != null) return false;
        return true;
    }

    public static boolean isNull(Common.DeathEffect effect) {
        if (effect.ap_boost_min != null) return false;
        if (effect.ap_boost_max != null) return false;
        if (effect.hp_boost_min != null) return false;
        if (effect.hp_boost_max != null) return false;
        if (effect.conditions_source != null) return false;
        return true;
    }

    public class NPCFieldUpdater implements FieldUpdateListener {

        @Override
        public void valueChanged(JComponent source, Object value) {
            NPC npc = (NPC) target;
            boolean updateHit, updateHitReceived, updateDeath;
            updateHit = updateHitReceived = updateDeath = false;
            if (source == idField) {
                //Events caused by cancel an ID edition. Dismiss.
                if (skipNext) {
                    skipNext = false;
                    return;
                }
                if (target.id.equals((String) value)) return;

                if (idChanging()) {
                    npc.id = (String) value;
                    NPCEditor.this.name = npc.getDesc();
                    npc.childrenChanged(new ArrayList<ProjectTreeNode>());
                    ATContentStudio.frame.editorChanged(NPCEditor.this);
                } else {
                    cancelIdEdit(idField);
                    return;
                }
            } else if (source == nameField) {
                npc.name = (String) value;
                NPCEditor.this.name = npc.getDesc();
                npc.childrenChanged(new ArrayList<ProjectTreeNode>());
                ATContentStudio.frame.editorChanged(NPCEditor.this);
            } else if (source == npcIcon) {
                npc.icon_id = (String) value;
                npc.childrenChanged(new ArrayList<ProjectTreeNode>());
                NPCEditor.this.icon = new ImageIcon(npc.getProject().getIcon((String) value));
                ATContentStudio.frame.editorChanged(NPCEditor.this);
                npcIcon.setIcon(new ImageIcon(npc.getProject().getImage((String) value)));
                npcIcon.revalidate();
                npcIcon.repaint();
            } else if (source == spawnGroupField) {
                npc.spawngroup_id = (String) value;
            } else if (source == factionField) {
                npc.faction_id = (String) value;
            } else if (source == dialogueBox) {
                if (npc.dialogue != null) {
                    npc.dialogue.removeBacklink(npc);
                }
                npc.dialogue = (Dialogue) value;
                if (npc.dialogue != null) {
                    npc.dialogue_id = npc.dialogue.id;
                    npc.dialogue.addBacklink(npc);
                } else {
                    npc.dialogue_id = null;
                }
                reloadGraphView(npc);
            } else if (source == droplistBox) {
                if (npc.droplist != null) {
                    npc.droplist.removeBacklink(npc);
                }
                npc.droplist = (Droplist) value;
                if (npc.droplist != null) {
                    npc.droplist_id = npc.droplist.id;
                    npc.droplist.addBacklink(npc);
                } else {
                    npc.droplist_id = null;
                }
            } else if (source == monsterClassBox) {
                npc.monster_class = (NPC.MonsterClass) value;
            } else if (source == uniqueBox) {
                npc.unique = (Integer) value;
            } else if (source == moveTypeBox) {
                npc.movement_type = (NPC.MovementType) value;
            } else if (source == maxHP) {
                npc.max_hp = (Integer) value;
            } else if (source == maxAP) {
                npc.max_ap = (Integer) value;
            } else if (source == moveCost) {
                npc.move_cost = (Integer) value;
            } else if (source == atkDmgMin) {
                npc.attack_damage_min = (Integer) value;
            } else if (source == atkDmgMax) {
                npc.attack_damage_max = (Integer) value;
            } else if (source == atkCost) {
                npc.attack_cost = (Integer) value;
            } else if (source == atkChance) {
                npc.attack_chance = (Integer) value;
            } else if (source == critSkill) {
                npc.critical_skill = (Integer) value;
            } else if (source == critMult) {
                npc.critical_multiplier = (Double) value;
            } else if (source == blockChance) {
                npc.block_chance = (Integer) value;
            } else if (source == dmgRes) {
                npc.damage_resistance = (Integer) value;
            } else if(hitEffectPane != null &&hitEffectPane.valueChanged(source, value, npc)) {
                updateHit = true;
            } else if (source == hitReceivedEffectHPMin) {
                hitReceivedEffect.hp_boost_min = (Integer) value;
                updateHitReceived = true;
            } else if (source == hitReceivedEffectHPMax) {
                hitReceivedEffect.hp_boost_max = (Integer) value;
                updateHitReceived = true;
            } else if (source == hitReceivedEffectAPMin) {
                hitReceivedEffect.ap_boost_min = (Integer) value;
                updateHitReceived = true;
            } else if (source == hitReceivedEffectAPMax) {
                hitReceivedEffect.ap_boost_max = (Integer) value;
                updateHitReceived = true;
            } else if (source == hitReceivedEffectHPMinTarget) {
                hitReceivedEffect.target.hp_boost_min = (Integer) value;
                updateHitReceived = true;
            } else if (source == hitReceivedEffectHPMaxTarget) {
                hitReceivedEffect.target.hp_boost_max = (Integer) value;
                updateHitReceived = true;
            } else if (source == hitReceivedEffectAPMinTarget) {
                hitReceivedEffect.target.ap_boost_min = (Integer) value;
                updateHitReceived = true;
            } else if (source == hitReceivedEffectAPMaxTarget) {
                hitReceivedEffect.target.ap_boost_max = (Integer) value;
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionsList) {
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionBox) {
                if (selectedHitReceivedEffectSourceCondition.condition != null) {
                    selectedHitReceivedEffectSourceCondition.condition.removeBacklink(npc);
                }
                selectedHitReceivedEffectSourceCondition.condition = (ActorCondition) value;
                if (selectedHitReceivedEffectSourceCondition.condition != null) {
                    selectedHitReceivedEffectSourceCondition.condition.addBacklink(npc);
                    selectedHitReceivedEffectSourceCondition.condition_id = selectedHitReceivedEffectSourceCondition.condition.id;
                } else {
                    selectedHitReceivedEffectSourceCondition.condition_id = null;
                }
                hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
            } else if (source == hitReceivedSourceConditionClear && (Boolean) value) {
                selectedHitReceivedEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedHitReceivedEffectSourceCondition.duration = null;
                updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
                hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionApply && (Boolean) value) {
                selectedHitReceivedEffectSourceCondition.magnitude = (Integer) hitReceivedSourceConditionMagnitude.getValue();
                selectedHitReceivedEffectSourceCondition.duration = hitReceivedSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitReceivedSourceConditionDuration.getValue();
                if (selectedHitReceivedEffectSourceCondition.duration == null || selectedHitReceivedEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitReceivedEffectSourceCondition.duration = 1;
                }
                updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
                hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionImmunity && (Boolean) value) {
                selectedHitReceivedEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedHitReceivedEffectSourceCondition.duration = hitReceivedSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitReceivedSourceConditionDuration.getValue();
                if (selectedHitReceivedEffectSourceCondition.duration == null || selectedHitReceivedEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitReceivedEffectSourceCondition.duration = 1;
                }
                updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
                hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionMagnitude) {
                selectedHitReceivedEffectSourceCondition.magnitude = (Integer) value;
                hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionTimed && (Boolean) value) {
                selectedHitReceivedEffectSourceCondition.duration = (Integer) hitReceivedSourceConditionDuration.getValue();
                if (selectedHitReceivedEffectSourceCondition.duration == null || selectedHitReceivedEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitReceivedEffectSourceCondition.duration = 1;
                }
                updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
                hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionForever && (Boolean) value) {
                selectedHitReceivedEffectSourceCondition.duration = ActorCondition.DURATION_FOREVER;
                updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
                hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionDuration) {
                selectedHitReceivedEffectSourceCondition.duration = (Integer) value;
                hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionChance) {
                selectedHitReceivedEffectSourceCondition.chance = (Double) value;
                hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
            } else if (source == hitReceivedTargetConditionsList) {
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionBox) {
                updateConditionEffect((ActorCondition) value, npc, selectedHitReceivedEffectTargetCondition, hitReceivedTargetConditionsListModel);
            } else if (source == hitReceivedTargetConditionClear && (Boolean) value) {
                selectedHitReceivedEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedHitReceivedEffectTargetCondition.duration = null;
                updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
                hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionApply && (Boolean) value) {
                selectedHitReceivedEffectTargetCondition.magnitude = (Integer) hitReceivedTargetConditionMagnitude.getValue();
                selectedHitReceivedEffectTargetCondition.duration = hitReceivedTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitReceivedTargetConditionDuration.getValue();
                if (selectedHitReceivedEffectTargetCondition.duration == null || selectedHitReceivedEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitReceivedEffectTargetCondition.duration = 1;
                }
                updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
                hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionImmunity && (Boolean) value) {
                selectedHitReceivedEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedHitReceivedEffectTargetCondition.duration = hitReceivedTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitReceivedTargetConditionDuration.getValue();
                if (selectedHitReceivedEffectTargetCondition.duration == null || selectedHitReceivedEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitReceivedEffectTargetCondition.duration = 1;
                }
                updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
                hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionMagnitude) {
                selectedHitReceivedEffectTargetCondition.magnitude = (Integer) value;
                hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionTimed && (Boolean) value) {
                selectedHitReceivedEffectTargetCondition.duration = (Integer) hitReceivedTargetConditionDuration.getValue();
                if (selectedHitReceivedEffectTargetCondition.duration == null || selectedHitReceivedEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitReceivedEffectTargetCondition.duration = 1;
                }
                updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
                hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionForever && (Boolean) value) {
                selectedHitReceivedEffectTargetCondition.duration = ActorCondition.DURATION_FOREVER;
                updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
                hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionDuration) {
                selectedHitReceivedEffectTargetCondition.duration = (Integer) value;
                hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionChance) {
                selectedHitReceivedEffectTargetCondition.chance = (Double) value;
                hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
            } else if (deathEffectPane.valueChanged(source, value, npc)) {
                updateDeath = true;
            }

            if (updateHit) {
                if (isNull(hitEffectPane.effect)) {
                    npc.hit_effect = null;
                } else {
                    npc.hit_effect = hitEffectPane.effect;
                }
            }
            if (updateHitReceived) {
                if (isNull(hitReceivedEffect)) {
                    npc.hit_received_effect = null;
                } else {
                    npc.hit_received_effect = hitReceivedEffect;
                }
            }
            if (updateDeath) {
                if (isNull(deathEffectPane. effect)) {
                    npc.death_effect = null;
                } else {
                    npc.death_effect = deathEffectPane. effect;
                }
            }

            experienceField.setValue(npc.getMonsterExperience());

            if (npc.state != GameDataElement.State.modified) {
                npc.state = GameDataElement.State.modified;
                NPCEditor.this.name = npc.getDesc();
                npc.childrenChanged(new ArrayList<ProjectTreeNode>());
                ATContentStudio.frame.editorChanged(NPCEditor.this);
            }
            updateJsonViewText(npc.toJsonString());

        }

    }


}
