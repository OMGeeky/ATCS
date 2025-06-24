package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import com.gpl.rpg.atcontentstudio.ATContentStudio;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.ProjectTreeNode;
import com.gpl.rpg.atcontentstudio.model.gamedata.*;
import com.gpl.rpg.atcontentstudio.model.sprites.Spritesheet;
import com.gpl.rpg.atcontentstudio.ui.*;
import com.gpl.rpg.atcontentstudio.ui.gamedataeditors.dialoguetree.DialogueGraphView;
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
    private CommonEditor.HitRecievedEffectPane hitReceivedEffectPane;
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
            hitEffectPane = new CommonEditor.HitEffectPane("Effect on every hit: ", Common.TimedActorConditionEffect::new, this, null, null);
        hitEffectPane.createHitEffectPaneContent(listener, npc.writable, hitEffect, new SourceTimedConditionsListModel(hitEffect), new TargetTimedConditionsListModel(hitEffect));
        combatTraitPane.add(hitEffectPane.effectPane, JideBoxLayout.FIX);

        Common.HitReceivedEffect hitReceivedEffect;
        if (npc.hit_received_effect == null) {
            hitReceivedEffect = new Common.HitReceivedEffect();
        } else {
            hitReceivedEffect = npc.hit_received_effect;
        }
        if (hitReceivedEffectPane == null)
            hitReceivedEffectPane = new CommonEditor.HitRecievedEffectPane("Effect on every hit received: ", Common.TimedActorConditionEffect::new, this, "NPC", "Attacker");
        hitReceivedEffectPane.createHitReceivedEffectPaneContent(listener, npc.writable, hitReceivedEffect, new SourceTimedConditionsListModel(hitReceivedEffect), new TargetTimedConditionsListModel(hitReceivedEffect));
        combatTraitPane.add(hitReceivedEffectPane.effectPane, JideBoxLayout.FIX);

        Common.DeathEffect deathEffect;
        if (npc.death_effect == null) {
            deathEffect = new Common.DeathEffect();
        } else {
            deathEffect = npc.death_effect;
        }
        if (deathEffectPane == null)
            deathEffectPane = new CommonEditor.DeathEffectPane("Effect when killed: ", Common.TimedActorConditionEffect::new, this, "Killer");
        deathEffectPane.createDeathEffectPaneContent(listener, npc.writable, deathEffect, new SourceTimedConditionsListModel(deathEffect)
        );
        combatTraitPane.add(deathEffectPane.effectPane, JideBoxLayout.FIX);

        pane.add(combatTraitPane, JideBoxLayout.FIX);
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
            } else if (hitReceivedEffectPane != null && hitReceivedEffectPane.valueChanged(source, value, npc)) {
                updateHitReceived = true;
            } else if (deathEffectPane != null && deathEffectPane.valueChanged(source, value, npc)) {
                updateDeath = true;
            }

            if (updateHit) {
                if (hitEffectPane.effect.isNull()) {
                    npc.hit_effect = null;
                } else {
                    npc.hit_effect = hitEffectPane.effect;
                }
            }
            if (updateHitReceived) {
                if (hitEffectPane.effect.isNull()) {
                    npc.hit_received_effect = null;
                } else {
                    npc.hit_received_effect = hitReceivedEffectPane.effect;
                }
            }
            if (updateDeath) {
                if (deathEffectPane.effect.isNull()) {
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
