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
    private Common.TimedActorConditionEffect selectedDeathEffectSourceCondition;

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

    private Common.DeathEffect deathEffect;
    private CollapsiblePanel deathEffectPane;
    private JSpinner deathEffectHPMin;
    private JSpinner deathEffectHPMax;
    private JSpinner deathEffectAPMin;
    private JSpinner deathEffectAPMax;

    private SourceTimedConditionsListModel deathSourceConditionsListModel;
    @SuppressWarnings("rawtypes")
    private JList deathSourceConditionsList;
    private MyComboBox deathSourceConditionBox;
    private JSpinner deathSourceConditionChance;
    private JRadioButton deathSourceConditionClear;
    private JRadioButton deathSourceConditionApply;
    private JRadioButton deathSourceConditionImmunity;
    private JSpinner deathSourceConditionMagnitude;
    private JRadioButton deathSourceConditionTimed;
    private JRadioButton deathSourceConditionForever;
    private JSpinner deathSourceConditionDuration;

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

        hitEffectPane = new CommonEditor.HitEffectPane(this.hitEffectPane.selectedHitEffectSourceCondition, this.hitEffectPane.selectedHitEffectTargetCondition);
        hitEffectPane.createHitEffectPaneContent(npc, listener, this);
        combatTraitPane.add(hitEffectPane.hitEffectPane, JideBoxLayout.FIX);

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

        deathEffectPane = new CollapsiblePanel("Effect when killed: ");
        deathEffectPane.setLayout(new JideBoxLayout(deathEffectPane, JideBoxLayout.PAGE_AXIS));
        if (npc.death_effect == null) {
            deathEffect = new Common.DeathEffect();
        } else {
            deathEffect = npc.death_effect;
        }
        deathEffectHPMin = addIntegerField(deathEffectPane, "Killer HP bonus min: ", deathEffect.hp_boost_min, true, npc.writable, listener);
        deathEffectHPMax = addIntegerField(deathEffectPane, "Killer HP bonus max: ", deathEffect.hp_boost_max, true, npc.writable, listener);
        deathEffectAPMin = addIntegerField(deathEffectPane, "Killer AP bonus min: ", deathEffect.ap_boost_min, true, npc.writable, listener);
        deathEffectAPMax = addIntegerField(deathEffectPane, "Killer AP bonus max: ", deathEffect.ap_boost_max, true, npc.writable, listener);

        String titleDeathSource = "Actor Conditions applied to the killer: ";
        deathSourceConditionsListModel = new SourceTimedConditionsListModel(deathEffect);
        CommonEditor.TimedConditionsCellRenderer cellRendererDeathSource = new CommonEditor.TimedConditionsCellRenderer();
        BasicLambdaWithArg<Common.TimedActorConditionEffect> selectedSetDeathSource = (value)->selectedDeathEffectSourceCondition = value;
        BasicLambdaWithReturn<Common.TimedActorConditionEffect> selectedGetDeathSource = ()->selectedDeathEffectSourceCondition ;
        BasicLambda selectedResetDeathSource = ()->selectedDeathEffectSourceCondition = null;
        BasicLambdaWithArg<JPanel> updatePaneDeathSource = (editorPane) -> updateDeathSourceTimedConditionEditorPane(editorPane, selectedDeathEffectSourceCondition, listener);
        var resultDeathSource = UiUtils.getCollapsibleItemList(listener,
                deathSourceConditionsListModel,
                selectedResetDeathSource,
                selectedSetDeathSource,
                selectedGetDeathSource,
                (x) -> {},
                updatePaneDeathSource,
                npc.writable,
                Common.TimedActorConditionEffect::new,
                cellRendererDeathSource,
                titleDeathSource,
                (x) -> null);
        deathSourceConditionsList = resultDeathSource.list;
        CollapsiblePanel deathSourceConditionsPane = resultDeathSource.collapsiblePanel;
        if (npc.death_effect == null || npc.death_effect.conditions_source == null || npc.death_effect.conditions_source.isEmpty()) {
            deathSourceConditionsPane.collapse();
        }
        deathEffectPane.add(deathSourceConditionsPane, JideBoxLayout.FIX);

        combatTraitPane.add(deathEffectPane, JideBoxLayout.FIX);


        pane.add(combatTraitPane, JideBoxLayout.FIX);
    }

    

    public void updateHitSourceTimedConditionEditorPane(JPanel pane, Common.TimedActorConditionEffect condition, final FieldUpdateListener listener) {
        pane.removeAll();
        if (hitEffectPane.hitSourceConditionBox != null) {
            removeElementListener(hitEffectPane.hitSourceConditionBox);
        }

        boolean writable = ((NPC) target).writable;
        Project proj = ((NPC) target).getProject();

        hitEffectPane.hitSourceConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
        hitEffectPane.hitSourceConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);

        hitEffectPane.hitSourceConditionClear = new JRadioButton("Clear active condition");
        pane.add(hitEffectPane.hitSourceConditionClear, JideBoxLayout.FIX);
        hitEffectPane.hitSourceConditionApply = new JRadioButton("Apply condition with magnitude");
        pane.add(hitEffectPane.hitSourceConditionApply, JideBoxLayout.FIX);
        hitEffectPane.hitSourceConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
        hitEffectPane.hitSourceConditionImmunity = new JRadioButton("Give immunity to condition");
        pane.add(hitEffectPane.hitSourceConditionImmunity, JideBoxLayout.FIX);

        ButtonGroup radioEffectGroup = new ButtonGroup();
        radioEffectGroup.add(hitEffectPane.hitSourceConditionApply);
        radioEffectGroup.add(hitEffectPane.hitSourceConditionClear);
        radioEffectGroup.add(hitEffectPane.hitSourceConditionImmunity);

        hitEffectPane.hitSourceConditionTimed = new JRadioButton("For a number of rounds");
        pane.add(hitEffectPane.hitSourceConditionTimed, JideBoxLayout.FIX);
        hitEffectPane.hitSourceConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, 1, false, writable, listener);
        hitEffectPane.hitSourceConditionForever = new JRadioButton("Forever");
        pane.add(hitEffectPane.hitSourceConditionForever, JideBoxLayout.FIX);

        ButtonGroup radioDurationGroup = new ButtonGroup();
        radioDurationGroup.add(hitEffectPane.hitSourceConditionTimed);
        radioDurationGroup.add(hitEffectPane.hitSourceConditionForever);

        updateHitSourceTimedConditionWidgets(condition);

        hitEffectPane.hitSourceConditionClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitEffectPane.hitSourceConditionClear, new Boolean(hitEffectPane.hitSourceConditionClear.isSelected()));
            }
        });
        hitEffectPane.hitSourceConditionApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitEffectPane.hitSourceConditionApply, new Boolean(hitEffectPane.hitSourceConditionApply.isSelected()));
            }
        });
        hitEffectPane.hitSourceConditionImmunity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitEffectPane.hitSourceConditionImmunity, new Boolean(hitEffectPane.hitSourceConditionImmunity.isSelected()));
            }
        });

        hitEffectPane.hitSourceConditionTimed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitEffectPane.hitSourceConditionTimed, new Boolean(hitEffectPane.hitSourceConditionTimed.isSelected()));
            }
        });
        hitEffectPane.hitSourceConditionForever.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitEffectPane.hitSourceConditionForever, new Boolean(hitEffectPane.hitSourceConditionForever.isSelected()));
            }
        });
        pane.revalidate();
        pane.repaint();
    }

    public void updateHitSourceTimedConditionWidgets(Common.TimedActorConditionEffect condition) {
        hitEffectPane.updateDeathEffectSourceTimedConditionWidgets(condition);
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

    public void updateDeathSourceTimedConditionEditorPane(JPanel pane, Common.TimedActorConditionEffect condition, final FieldUpdateListener listener) {
        pane.removeAll();
        if (deathSourceConditionBox != null) {
            removeElementListener(deathSourceConditionBox);
        }

        boolean writable = ((NPC) target).writable;
        Project proj = ((NPC) target).getProject();

        deathSourceConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
        deathSourceConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);

        deathSourceConditionClear = new JRadioButton("Clear active condition");
        pane.add(deathSourceConditionClear, JideBoxLayout.FIX);
        deathSourceConditionApply = new JRadioButton("Apply condition with magnitude");
        pane.add(deathSourceConditionApply, JideBoxLayout.FIX);
        deathSourceConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
        deathSourceConditionImmunity = new JRadioButton("Give immunity to condition");
        pane.add(deathSourceConditionImmunity, JideBoxLayout.FIX);

        ButtonGroup radioEffectGroup = new ButtonGroup();
        radioEffectGroup.add(deathSourceConditionApply);
        radioEffectGroup.add(deathSourceConditionClear);
        radioEffectGroup.add(deathSourceConditionImmunity);

        deathSourceConditionTimed = new JRadioButton("For a number of rounds");
        pane.add(deathSourceConditionTimed, JideBoxLayout.FIX);
        deathSourceConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, 1, false, writable, listener);
        deathSourceConditionForever = new JRadioButton("Forever");
        pane.add(deathSourceConditionForever, JideBoxLayout.FIX);

        ButtonGroup radioDurationGroup = new ButtonGroup();
        radioDurationGroup.add(deathSourceConditionTimed);
        radioDurationGroup.add(deathSourceConditionForever);

        updateDeathSourceTimedConditionWidgets(condition);

        deathSourceConditionClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(deathSourceConditionClear, new Boolean(deathSourceConditionClear.isSelected()));
            }
        });
        deathSourceConditionApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(deathSourceConditionApply, new Boolean(deathSourceConditionApply.isSelected()));
            }
        });
        deathSourceConditionImmunity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(deathSourceConditionImmunity, new Boolean(deathSourceConditionImmunity.isSelected()));
            }
        });

        deathSourceConditionTimed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(deathSourceConditionTimed, new Boolean(deathSourceConditionTimed.isSelected()));
            }
        });
        deathSourceConditionForever.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(deathSourceConditionForever, new Boolean(deathSourceConditionForever.isSelected()));
            }
        });
        pane.revalidate();
        pane.repaint();
    }

    public void updateDeathSourceTimedConditionWidgets(Common.TimedActorConditionEffect condition) {

        boolean immunity = condition.isImmunity();
        boolean clear = condition.isClear();
        boolean forever = condition.isInfinite();

        deathSourceConditionClear.setSelected(clear);
        deathSourceConditionApply.setSelected(!clear && !immunity);
        deathSourceConditionMagnitude.setEnabled(!clear && !immunity);
        deathSourceConditionImmunity.setSelected(immunity);

        deathSourceConditionTimed.setSelected(!forever);
        deathSourceConditionTimed.setEnabled(!clear);
        deathSourceConditionDuration.setEnabled(!clear && !forever);
        deathSourceConditionForever.setSelected(forever);
        deathSourceConditionForever.setEnabled(!clear);
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
            } else if (source == hitEffectPane.hitEffectHPMin) {
                hitEffectPane.hitEffect.hp_boost_min = (Integer) value;
                updateHit = true;
            } else if (source == hitEffectPane.hitEffectHPMax) {
                hitEffectPane.hitEffect.hp_boost_max = (Integer) value;
                updateHit = true;
            } else if (source == hitEffectPane.hitEffectAPMin) {
                hitEffectPane.hitEffect.ap_boost_min = (Integer) value;
                updateHit = true;
            } else if (source == hitEffectPane.hitEffectAPMax) {
                hitEffectPane.hitEffect.ap_boost_max = (Integer) value;
                updateHit = true;
            } else if (source == hitEffectPane.hitSourceConditionsList) {
                updateHit = true;
            } else if (source == hitEffectPane.hitSourceConditionBox) {
                if (hitEffectPane.selectedHitEffectSourceCondition.condition != null) {
                    hitEffectPane.selectedHitEffectSourceCondition.condition.removeBacklink(npc);
                }
                hitEffectPane.selectedHitEffectSourceCondition.condition = (ActorCondition) value;
                if (hitEffectPane.selectedHitEffectSourceCondition.condition != null) {
                    hitEffectPane.selectedHitEffectSourceCondition.condition.addBacklink(npc);
                    hitEffectPane.selectedHitEffectSourceCondition.condition_id = hitEffectPane.selectedHitEffectSourceCondition.condition.id;
                } else {
                    hitEffectPane.selectedHitEffectSourceCondition.condition_id = null;
                }
                hitEffectPane.hitSourceConditionsModel.itemChanged(hitEffectPane.selectedHitEffectSourceCondition);
            } else if (source == hitEffectPane.hitSourceConditionClear && (Boolean) value) {
                hitEffectPane.selectedHitEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                hitEffectPane.selectedHitEffectSourceCondition.duration = null;
                updateHitSourceTimedConditionWidgets(hitEffectPane.selectedHitEffectSourceCondition);
                hitEffectPane.hitSourceConditionsModel.itemChanged(hitEffectPane.selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitSourceConditionApply && (Boolean) value) {
                hitEffectPane.selectedHitEffectSourceCondition.magnitude = (Integer) hitEffectPane.hitSourceConditionMagnitude.getValue();
                hitEffectPane.selectedHitEffectSourceCondition.duration = hitEffectPane.hitSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitEffectPane.hitSourceConditionDuration.getValue();
                if (hitEffectPane.selectedHitEffectSourceCondition.duration == null || hitEffectPane.selectedHitEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    hitEffectPane.selectedHitEffectSourceCondition.duration = 1;
                }
                updateHitSourceTimedConditionWidgets(hitEffectPane.selectedHitEffectSourceCondition);
                hitEffectPane.hitSourceConditionsModel.itemChanged(hitEffectPane.selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitSourceConditionImmunity && (Boolean) value) {
                hitEffectPane.selectedHitEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                hitEffectPane.selectedHitEffectSourceCondition.duration = hitEffectPane.hitSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitEffectPane.hitSourceConditionDuration.getValue();
                if (hitEffectPane.selectedHitEffectSourceCondition.duration == null || hitEffectPane.selectedHitEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    hitEffectPane.selectedHitEffectSourceCondition.duration = 1;
                }
                updateHitSourceTimedConditionWidgets(hitEffectPane.selectedHitEffectSourceCondition);
                hitEffectPane.hitSourceConditionsModel.itemChanged(hitEffectPane.selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitSourceConditionMagnitude) {
                hitEffectPane.selectedHitEffectSourceCondition.magnitude = (Integer) value;
                hitEffectPane.hitSourceConditionsModel.itemChanged(hitEffectPane.selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitSourceConditionTimed && (Boolean) value) {
                hitEffectPane.selectedHitEffectSourceCondition.duration = (Integer) hitEffectPane.hitSourceConditionDuration.getValue();
                if (hitEffectPane.selectedHitEffectSourceCondition.duration == null || hitEffectPane.selectedHitEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    hitEffectPane.selectedHitEffectSourceCondition.duration = 1;
                }
                updateHitSourceTimedConditionWidgets(hitEffectPane.selectedHitEffectSourceCondition);
                hitEffectPane.hitSourceConditionsModel.itemChanged(hitEffectPane.selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitSourceConditionForever && (Boolean) value) {
                hitEffectPane.selectedHitEffectSourceCondition.duration = ActorCondition.DURATION_FOREVER;
                updateHitSourceTimedConditionWidgets(hitEffectPane.selectedHitEffectSourceCondition);
                hitEffectPane.hitSourceConditionsModel.itemChanged(hitEffectPane.selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitSourceConditionDuration) {
                hitEffectPane.selectedHitEffectSourceCondition.duration = (Integer) value;
                hitEffectPane.hitSourceConditionsModel.itemChanged(hitEffectPane.selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitSourceConditionChance) {
                hitEffectPane.selectedHitEffectSourceCondition.chance = (Double) value;
                hitEffectPane.hitSourceConditionsModel.itemChanged(hitEffectPane.selectedHitEffectSourceCondition);
            } else if (source == hitEffectPane.hitTargetConditionsList) {
                updateHit = true;
            } else if (source == hitEffectPane.hitTargetConditionBox) {
                updateConditionEffect((ActorCondition) value, npc, hitEffectPane.selectedHitEffectTargetCondition, hitEffectPane.hitTargetConditionsListModel);
            } else if (source == hitEffectPane.hitTargetConditionClear && (Boolean) value) {
                hitEffectPane.selectedHitEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                hitEffectPane.selectedHitEffectTargetCondition.duration = null;
                updateHitTargetTimedConditionWidgets(hitEffectPane.selectedHitEffectTargetCondition);
                hitEffectPane.hitTargetConditionsListModel.itemChanged(hitEffectPane.selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitTargetConditionApply && (Boolean) value) {
                hitEffectPane.selectedHitEffectTargetCondition.magnitude = (Integer) hitEffectPane.hitTargetConditionMagnitude.getValue();
                hitEffectPane.selectedHitEffectTargetCondition.duration = hitEffectPane.hitTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitEffectPane.hitTargetConditionDuration.getValue();
                if (hitEffectPane.selectedHitEffectTargetCondition.duration == null || hitEffectPane.selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                    hitEffectPane.selectedHitEffectTargetCondition.duration = 1;
                }
                updateHitTargetTimedConditionWidgets(hitEffectPane.selectedHitEffectTargetCondition);
                hitEffectPane.hitTargetConditionsListModel.itemChanged(hitEffectPane.selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitTargetConditionImmunity && (Boolean) value) {
                hitEffectPane.selectedHitEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                hitEffectPane.selectedHitEffectTargetCondition.duration = hitEffectPane.hitTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitEffectPane.hitTargetConditionDuration.getValue();
                if (hitEffectPane.selectedHitEffectTargetCondition.duration == null || hitEffectPane.selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                    hitEffectPane.selectedHitEffectTargetCondition.duration = 1;
                }
                updateHitTargetTimedConditionWidgets(hitEffectPane.selectedHitEffectTargetCondition);
                hitEffectPane.hitTargetConditionsListModel.itemChanged(hitEffectPane.selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitTargetConditionMagnitude) {
                hitEffectPane.selectedHitEffectTargetCondition.magnitude = (Integer) value;
                hitEffectPane.hitTargetConditionsListModel.itemChanged(hitEffectPane.selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitTargetConditionTimed && (Boolean) value) {
                hitEffectPane.selectedHitEffectTargetCondition.duration = (Integer) hitEffectPane.hitTargetConditionDuration.getValue();
                if (hitEffectPane.selectedHitEffectTargetCondition.duration == null || hitEffectPane.selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                    hitEffectPane.selectedHitEffectTargetCondition.duration = 1;
                }
                updateHitTargetTimedConditionWidgets(hitEffectPane.selectedHitEffectTargetCondition);
                hitEffectPane.hitTargetConditionsListModel.itemChanged(hitEffectPane.selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitTargetConditionForever && (Boolean) value) {
                hitEffectPane.selectedHitEffectTargetCondition.duration = ActorCondition.DURATION_FOREVER;
                updateHitTargetTimedConditionWidgets(hitEffectPane.selectedHitEffectTargetCondition);
                hitEffectPane.hitTargetConditionsListModel.itemChanged(hitEffectPane.selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitTargetConditionDuration) {
                hitEffectPane.selectedHitEffectTargetCondition.duration = (Integer) value;
                hitEffectPane.hitTargetConditionsListModel.itemChanged(hitEffectPane.selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitEffectPane.hitTargetConditionChance) {
                hitEffectPane.selectedHitEffectTargetCondition.chance = (Double) value;
                hitEffectPane.hitTargetConditionsListModel.itemChanged(hitEffectPane.selectedHitEffectTargetCondition);
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
            } else if (source == deathEffectHPMin) {
                deathEffect.hp_boost_min = (Integer) value;
                updateDeath = true;
            } else if (source == deathEffectHPMax) {
                deathEffect.hp_boost_max = (Integer) value;
                updateDeath = true;
            } else if (source == deathEffectAPMin) {
                deathEffect.ap_boost_min = (Integer) value;
                updateDeath = true;
            } else if (source == deathEffectAPMax) {
                deathEffect.ap_boost_max = (Integer) value;
                updateDeath = true;
            } else if (source == deathSourceConditionsList) {
                updateDeath = true;
            } else if (source == deathSourceConditionBox) {
                if (selectedDeathEffectSourceCondition.condition != null) {
                    selectedDeathEffectSourceCondition.condition.removeBacklink(npc);
                }
                selectedDeathEffectSourceCondition.condition = (ActorCondition) value;
                if (selectedDeathEffectSourceCondition.condition != null) {
                    selectedDeathEffectSourceCondition.condition.addBacklink(npc);
                    selectedDeathEffectSourceCondition.condition_id = selectedDeathEffectSourceCondition.condition.id;
                } else {
                    selectedDeathEffectSourceCondition.condition_id = null;
                }
                deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
            } else if (source == deathSourceConditionClear && (Boolean) value) {
                selectedDeathEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedDeathEffectSourceCondition.duration = null;
                updateDeathSourceTimedConditionWidgets(selectedDeathEffectSourceCondition);
                deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
                updateDeath = true;
            } else if (source == deathSourceConditionApply && (Boolean) value) {
                selectedDeathEffectSourceCondition.magnitude = (Integer) deathSourceConditionMagnitude.getValue();
                selectedDeathEffectSourceCondition.duration = deathSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) deathSourceConditionDuration.getValue();
                if (selectedDeathEffectSourceCondition.duration == null || selectedDeathEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedDeathEffectSourceCondition.duration = 1;
                }
                updateDeathSourceTimedConditionWidgets(selectedDeathEffectSourceCondition);
                deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
                updateDeath = true;
            } else if (source == deathSourceConditionImmunity && (Boolean) value) {
                selectedDeathEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedDeathEffectSourceCondition.duration = deathSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) deathSourceConditionDuration.getValue();
                if (selectedDeathEffectSourceCondition.duration == null || selectedDeathEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedDeathEffectSourceCondition.duration = 1;
                }
                updateDeathSourceTimedConditionWidgets(selectedDeathEffectSourceCondition);
                deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
                updateDeath = true;
            } else if (source == deathSourceConditionMagnitude) {
                selectedDeathEffectSourceCondition.magnitude = (Integer) value;
                deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
                updateDeath = true;
            } else if (source == deathSourceConditionTimed && (Boolean) value) {
                selectedDeathEffectSourceCondition.duration = (Integer) deathSourceConditionDuration.getValue();
                if (selectedDeathEffectSourceCondition.duration == null || selectedDeathEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedDeathEffectSourceCondition.duration = 1;
                }
                updateDeathSourceTimedConditionWidgets(selectedDeathEffectSourceCondition);
                deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
                updateDeath = true;
            } else if (source == deathSourceConditionForever && (Boolean) value) {
                selectedDeathEffectSourceCondition.duration = ActorCondition.DURATION_FOREVER;
                updateDeathSourceTimedConditionWidgets(selectedDeathEffectSourceCondition);
                deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
                updateDeath = true;
            } else if (source == deathSourceConditionDuration) {
                selectedDeathEffectSourceCondition.duration = (Integer) value;
                deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
                updateDeath = true;
            } else if (source == deathSourceConditionChance) {
                selectedDeathEffectSourceCondition.chance = (Double) value;
                deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
            }

            if (updateHit) {
                if (isNull(hitEffectPane.hitEffect)) {
                    npc.hit_effect = null;
                } else {
                    npc.hit_effect = hitEffectPane.hitEffect;
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
                if (isNull(deathEffect)) {
                    npc.death_effect = null;
                } else {
                    npc.death_effect = deathEffect;
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
