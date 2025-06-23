package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import com.gpl.rpg.atcontentstudio.ATContentStudio;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.Project;
import com.gpl.rpg.atcontentstudio.model.ProjectTreeNode;
import com.gpl.rpg.atcontentstudio.model.gamedata.*;
import com.gpl.rpg.atcontentstudio.model.sprites.Spritesheet;
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
import java.util.ArrayList;
import java.util.List;

public class ItemEditor extends JSONElementEditor {

    private static final long serialVersionUID = 7538154592029351986L;

    private static final String form_view_id = "Form";
    private static final String json_view_id = "JSON";

    private static final String killLabel = "Effect on every kill: ";
    private static final String useLabel = "Effect on use: ";


    private Common.ActorConditionEffect selectedEquipEffectCondition;
    private Common.TimedActorConditionEffect selectedHitEffectSourceCondition;
    private Common.TimedActorConditionEffect selectedHitEffectTargetCondition;
    private Common.TimedActorConditionEffect selectedKillEffectCondition;
    private Common.TimedActorConditionEffect selectedHitReceivedEffectSourceCondition;
    private Common.TimedActorConditionEffect selectedHitReceivedEffectTargetCondition;


    private JButton itemIcon;
    private JTextField idField;
    private JTextField nameField;
    private JTextField descriptionField;
    @SuppressWarnings("rawtypes")
    private JComboBox typeBox;
    private IntegerBasedCheckBox manualPriceBox;
    private JSpinner baseCostField;
    private MyComboBox categoryBox;
    private Integer baseManualPrice = null;

    private CollapsiblePanel equipEffectPane;
    private Item.EquipEffect equipEffect;
    private JSpinner equipDmgMin;
    private JSpinner equipDmgMax;
    private JSpinner equipBoostHP;
    private JSpinner equipBoostAP;
    private JSpinner equipBoostAC;
    private JSpinner equipBoostBC;
    private JSpinner equipBoostCS;
    private JSpinner equipSetCM;
    private JSpinner equipSetDM;
    private JSpinner equipBoostDR;
    private JSpinner equipIncMoveCost;
    private JSpinner equipIncUseCost;
    private JSpinner equipIncReequipCost;
    private JSpinner equipIncAttackCost;
    private ConditionsListModel equipConditionsModel;
    @SuppressWarnings("rawtypes")
    private JList equipConditionsList;
    private MyComboBox equipConditionBox;
    private JRadioButton equipConditionWithMagnitude;
    private JRadioButton equipConditionImmunity;
    private JSpinner equipConditionMagnitude;

    private CollapsiblePanel hitEffectPane;
    private Common.HitEffect hitEffect;
    private JSpinner hitHPMin;
    private JSpinner hitHPMax;
    private JSpinner hitAPMin;
    private JSpinner hitAPMax;
    private SourceTimedConditionsListModel hitSourceConditionsModel;
    @SuppressWarnings("rawtypes")
    private JList<Common.TimedActorConditionEffect> hitSourceConditionsList;
    private MyComboBox hitSourceConditionBox;
    private JSpinner hitSourceConditionChance;
    private JRadioButton hitSourceConditionClear;
    private JRadioButton hitSourceConditionApply;
    private JRadioButton hitSourceConditionImmunity;
    private JSpinner hitSourceConditionMagnitude;
    private JRadioButton hitSourceConditionTimed;
    private JRadioButton hitSourceConditionForever;
    private JSpinner hitSourceConditionDuration;
    private TargetTimedConditionsListModel hitTargetConditionsModel;
    @SuppressWarnings("rawtypes")
    private JList hitTargetConditionsList;
    private MyComboBox hitTargetConditionBox;
    private JSpinner hitTargetConditionChance;
    private JRadioButton hitTargetConditionClear;
    private JRadioButton hitTargetConditionApply;
    private JRadioButton hitTargetConditionImmunity;
    private JSpinner hitTargetConditionMagnitude;
    private JRadioButton hitTargetConditionTimed;
    private JRadioButton hitTargetConditionForever;
    private JSpinner hitTargetConditionDuration;

    private CollapsiblePanel killEffectPane;
    private Common.DeathEffect killEffect;
    private JSpinner killHPMin;
    private JSpinner killHPMax;
    private JSpinner killAPMin;
    private JSpinner killAPMax;
    private SourceTimedConditionsListModel killSourceConditionsModel;
    @SuppressWarnings("rawtypes")
    private JList killSourceConditionsList;
    private MyComboBox killSourceConditionBox;
    private JSpinner killSourceConditionChance;
    private JRadioButton killSourceConditionClear;
    private JRadioButton killSourceConditionApply;
    private JRadioButton killSourceConditionImmunity;
    private JSpinner killSourceConditionMagnitude;
    private JRadioButton killSourceConditionTimed;
    private JRadioButton killSourceConditionForever;
    private JSpinner killSourceConditionDuration;

    private CollapsiblePanel hitReceivedEffectPane;
    private Common.HitReceivedEffect hitReceivedEffect;
    private JSpinner hitReceivedHPMin;
    private JSpinner hitReceivedHPMax;
    private JSpinner hitReceivedAPMin;
    private JSpinner hitReceivedAPMax;
    private JSpinner hitReceivedHPMinTarget;
    private JSpinner hitReceivedHPMaxTarget;
    private JSpinner hitReceivedAPMinTarget;
    private JSpinner hitReceivedAPMaxTarget;
    private SourceTimedConditionsListModel hitReceivedSourceConditionsModel;
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
    private TargetTimedConditionsListModel hitReceivedTargetConditionsModel;
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

    public ItemEditor(Item item) {
        super(item, item.getDesc(), item.getIcon());
        addEditorTab(form_view_id, getFormView());
        addEditorTab(json_view_id, getJSONView());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void insertFormViewDataField(JPanel pane) {

        final Item item = (Item) target;

        final FieldUpdateListener listener = new ItemFieldUpdater();

        itemIcon = createButtonPane(pane, item.getProject(), item, Item.class, item.getImage(), Spritesheet.Category.item, listener);

        idField = addTextField(pane, "Internal ID: ", item.id, item.writable, listener);
        nameField = addTranslatableTextField(pane, "Display name: ", item.name, item.writable, listener);
        descriptionField = addTranslatableTextField(pane, "Description: ", item.description, item.writable, listener);
        typeBox = addEnumValueBox(pane, "Type: ", Item.DisplayType.values(), item.display_type, item.writable, listener);
        manualPriceBox = addIntegerBasedCheckBox(pane, "Has manual price", item.has_manual_price, item.writable, listener);
        baseManualPrice = item.base_market_cost;
        baseCostField = addIntegerField(pane, "Base market cost: ", (item.has_manual_price != null && item.has_manual_price == 1) ? item.base_market_cost : item.computePrice(), false, item.writable, listener);
        if (!manualPriceBox.isSelected()) {
            baseCostField.setEnabled(false);
        }
        categoryBox = addItemCategoryBox(pane, item.getProject(), "Category: ", item.category, item.writable, listener);

        equipEffectPane = new CollapsiblePanel("Effect when equipped: ");
        equipEffectPane.setLayout(new JideBoxLayout(equipEffectPane, JideBoxLayout.PAGE_AXIS));
        if (item.equip_effect == null) {
            equipEffect = new Item.EquipEffect();
        } else {
            equipEffect = item.equip_effect;
        }
        equipDmgMin = addIntegerField(equipEffectPane, "Attack Damage min: ", equipEffect.damage_boost_min, true, item.writable, listener);
        equipDmgMax = addIntegerField(equipEffectPane, "Attack Damage max: ", equipEffect.damage_boost_max, true, item.writable, listener);
        equipSetDM = addIntegerField(equipEffectPane, "Damage modifier %: ", equipEffect.damage_modifier, 100, false, item.writable, listener);
        equipBoostHP = addIntegerField(equipEffectPane, "Boost max HP: ", equipEffect.max_hp_boost, true, item.writable, listener);
        equipBoostAP = addIntegerField(equipEffectPane, "Boost max AP: ", equipEffect.max_ap_boost, true, item.writable, listener);
        equipBoostAC = addIntegerField(equipEffectPane, "Boost attack chance: ", equipEffect.increase_attack_chance, true, item.writable, listener);
        equipBoostBC = addIntegerField(equipEffectPane, "Boost block chance: ", equipEffect.increase_block_chance, true, item.writable, listener);
        equipBoostCS = addIntegerField(equipEffectPane, "Boost critical skill: ", equipEffect.increase_critical_skill, true, item.writable, listener);
        equipSetCM = addDoubleField(equipEffectPane, "Critical multiplier: ", equipEffect.critical_multiplier, item.writable, listener);
        equipBoostDR = addIntegerField(equipEffectPane, "Boost damage resistance: ", equipEffect.increase_damage_resistance, true, item.writable, listener);
        equipIncMoveCost = addIntegerField(equipEffectPane, "Increase move cost: ", equipEffect.increase_move_cost, true, item.writable, listener);
        equipIncUseCost = addIntegerField(equipEffectPane, "Increase item use cost: ", equipEffect.increase_use_item_cost, true, item.writable, listener);
        equipIncReequipCost = addIntegerField(equipEffectPane, "Increase reequip cost: ", equipEffect.increase_reequip_cost, true, item.writable, listener);
        equipIncAttackCost = addIntegerField(equipEffectPane, "Increase attack cost: ", equipEffect.increase_attack_cost, true, item.writable, listener);

        String titleEquipConditions = "Actor Conditions applied when equipped: ";
        equipConditionsModel = new ConditionsListModel(equipEffect);
        CommonEditor.ConditionsCellRenderer cellRendererEquipConditions = new CommonEditor.ConditionsCellRenderer();
        BasicLambdaWithArg<Common.ActorConditionEffect> selectedSetEquipConditions = (value)->selectedEquipEffectCondition = value;
        BasicLambdaWithReturn<Common.ActorConditionEffect> selectedGetEquipConditions = ()->selectedEquipEffectCondition ;
        BasicLambda selectedResetEquipConditions = ()->selectedEquipEffectCondition = null;
        BasicLambdaWithArg<JPanel> updatePaneEquipConditions = (editorPane) -> updateEquipConditionEditorPane(editorPane, selectedEquipEffectCondition, listener);
        var resultEquipConditions = UiUtils.getCollapsibleItemList(listener,
                equipConditionsModel,
                selectedResetEquipConditions,
                selectedSetEquipConditions,
                selectedGetEquipConditions,
                (x) -> {},
                updatePaneEquipConditions,
                item.writable,
                Common.ActorConditionEffect::new,
                cellRendererEquipConditions,
                titleEquipConditions,
                (x) -> null);
        equipConditionsList = resultEquipConditions.list;
        CollapsiblePanel equipConditionsPane = resultEquipConditions.collapsiblePanel;
        if (item.equip_effect == null || item.equip_effect.conditions == null || item.equip_effect.conditions.isEmpty()) {
            equipConditionsPane.collapse();
        }
        equipEffectPane.add(equipConditionsPane, JideBoxLayout.FIX);

        pane.add(equipEffectPane, JideBoxLayout.FIX);
        if (item.equip_effect == null) {
            equipEffectPane.collapse();
        }

        hitEffectPane = new CollapsiblePanel("Effect on every hit: ");
        hitEffectPane.setLayout(new JideBoxLayout(hitEffectPane, JideBoxLayout.PAGE_AXIS));
        if (item.hit_effect == null) {
            hitEffect = new Common.HitEffect();
        } else {
            hitEffect = item.hit_effect;
        }
        hitHPMin = addIntegerField(hitEffectPane, "HP bonus min: ", hitEffect.hp_boost_min, true, item.writable, listener);
        hitHPMax = addIntegerField(hitEffectPane, "HP bonus max: ", hitEffect.hp_boost_max, true, item.writable, listener);
        hitAPMin = addIntegerField(hitEffectPane, "AP bonus min: ", hitEffect.ap_boost_min, true, item.writable, listener);
        hitAPMax = addIntegerField(hitEffectPane, "AP bonus max: ", hitEffect.ap_boost_max, true, item.writable, listener);

        String title = "Actor Conditions applied to the source: ";
        hitSourceConditionsModel = new SourceTimedConditionsListModel(hitEffect);
        CommonEditor.TimedConditionsCellRenderer cellRenderer = new CommonEditor.TimedConditionsCellRenderer();
        BasicLambdaWithArg<Common.TimedActorConditionEffect> setSelected = (selectedItem) -> selectedHitEffectSourceCondition = selectedItem;
        BasicLambdaWithReturn<Common.TimedActorConditionEffect> getSelected = () -> hitSourceConditionsList.getSelectedValue();
        BasicLambda resetSelected = () -> selectedHitEffectSourceCondition = null;
        BasicLambdaWithArg valueChanged = (selectedReply) -> {
        };
        BasicLambdaWithArg<JPanel> updateEditorPane = (editorPane) -> updateHitSourceTimedConditionEditorPane(editorPane, selectedHitEffectSourceCondition, listener);

        var collapsibleItemList = UiUtils.getCollapsibleItemList(
                listener,
                hitSourceConditionsModel,
                resetSelected,
                setSelected,
                getSelected,
                valueChanged,
                updateEditorPane,
                item.writable,
                Common.TimedActorConditionEffect::new,
                cellRenderer,
                title,
                (x) -> null
        );
        CollapsiblePanel hitSourceConditionsPane = collapsibleItemList.collapsiblePanel;
        hitSourceConditionsList = collapsibleItemList.list;
        if (item.hit_effect == null || item.hit_effect.conditions_source == null || item.hit_effect.conditions_source.isEmpty()) {
            hitSourceConditionsPane.collapse();
        }
        hitEffectPane.add(hitSourceConditionsPane, JideBoxLayout.FIX);

        String titleHitTargetConditions = "Actor Conditions applied to the target: ";
        hitTargetConditionsModel = new TargetTimedConditionsListModel(hitEffect);
        CommonEditor.TimedConditionsCellRenderer cellRendererHitTargetConditions = new CommonEditor.TimedConditionsCellRenderer();
        BasicLambdaWithArg<Common.TimedActorConditionEffect> selectedSetHitTargetConditions = (value)->selectedHitEffectTargetCondition = value;
        BasicLambdaWithReturn<Common.TimedActorConditionEffect> selectedGetHitTargetConditions = ()->selectedHitEffectTargetCondition ;
        BasicLambda selectedResetHitTargetConditions = ()->selectedHitEffectTargetCondition = null;
        BasicLambdaWithArg<JPanel> updatePaneHitTargetConditions = (editorPane) -> updateHitTargetTimedConditionEditorPane(editorPane, selectedHitEffectTargetCondition, listener);
        var resultHitTargetConditions = UiUtils.getCollapsibleItemList(listener,
                hitTargetConditionsModel,
                selectedResetHitTargetConditions,
                selectedSetHitTargetConditions,
                selectedGetHitTargetConditions,
                (x) -> {},
                updatePaneHitTargetConditions,
                item.writable,
                Common.TimedActorConditionEffect::new,
                cellRendererHitTargetConditions,
                titleHitTargetConditions,
                (x) -> null);
        hitTargetConditionsList = resultHitTargetConditions.list;
        CollapsiblePanel hitTargetConditionsPane = resultHitTargetConditions.collapsiblePanel;
        if (item.hit_effect == null || item.hit_effect.conditions_target == null || item.hit_effect.conditions_target.isEmpty()) {
            hitTargetConditionsPane.collapse();
        }
        hitEffectPane.add(hitTargetConditionsPane, JideBoxLayout.FIX);
        if (item.hit_effect == null) {
            hitEffectPane.collapse();
        }
        pane.add(hitEffectPane, JideBoxLayout.FIX);


        killEffectPane = new CollapsiblePanel(killLabel);
        killEffectPane.setLayout(new JideBoxLayout(killEffectPane, JideBoxLayout.PAGE_AXIS));
        if (item.kill_effect == null) {
            killEffect = new Common.DeathEffect();
        } else {
            killEffect = item.kill_effect;
        }
        killHPMin = addIntegerField(killEffectPane, "HP bonus min: ", killEffect.hp_boost_min, true, item.writable, listener);
        killHPMax = addIntegerField(killEffectPane, "HP bonus max: ", killEffect.hp_boost_max, true, item.writable, listener);
        killAPMin = addIntegerField(killEffectPane, "AP bonus min: ", killEffect.ap_boost_min, true, item.writable, listener);
        killAPMax = addIntegerField(killEffectPane, "AP bonus max: ", killEffect.ap_boost_max, true, item.writable, listener);

        String titleKillSourceConditions = "Actor Conditions applied to the source: ";
        killSourceConditionsModel = new SourceTimedConditionsListModel(killEffect);
        CommonEditor.TimedConditionsCellRenderer cellRendererKillSourceConditions = new CommonEditor.TimedConditionsCellRenderer();
        BasicLambdaWithArg<Common.TimedActorConditionEffect> selectedSetKillSourceConditions = (value)->selectedKillEffectCondition = value;
        BasicLambdaWithReturn<Common.TimedActorConditionEffect> selectedGetKillSourceConditions = ()->selectedKillEffectCondition ;
        BasicLambda selectedResetKillSourceConditions = ()->selectedKillEffectCondition = null;
        BasicLambdaWithArg<JPanel> updatePaneKillSourceConditions = (editorPane) -> updateKillSourceTimedConditionEditorPane(editorPane, selectedKillEffectCondition, listener);
        var resultKillSourceConditions = UiUtils.getCollapsibleItemList(listener,
                killSourceConditionsModel,
                selectedResetKillSourceConditions,
                selectedSetKillSourceConditions,
                selectedGetKillSourceConditions,
                (x) -> {},
                updatePaneKillSourceConditions,
                item.writable,
                Common.TimedActorConditionEffect::new,
                cellRendererKillSourceConditions,
                titleKillSourceConditions,
                (x) -> null);
        killSourceConditionsList = resultKillSourceConditions.list;
        CollapsiblePanel killSourceConditionsPane = resultKillSourceConditions.collapsiblePanel;
        if (item.kill_effect == null || item.kill_effect.conditions_source == null || item.kill_effect.conditions_source.isEmpty()) {
            killSourceConditionsPane.collapse();
        }
        killEffectPane.add(killSourceConditionsPane, JideBoxLayout.FIX);

        if (item.kill_effect == null) {
            killEffectPane.collapse();
        }
        pane.add(killEffectPane, JideBoxLayout.FIX);


        hitReceivedEffectPane = new CollapsiblePanel("Effect on every hit received: ");
        hitReceivedEffectPane.setLayout(new JideBoxLayout(hitReceivedEffectPane, JideBoxLayout.PAGE_AXIS));
        if (item.hit_received_effect == null) {
            hitReceivedEffect = new Common.HitReceivedEffect();
        } else {
            hitReceivedEffect = item.hit_received_effect;
        }
        hitReceivedHPMin = addIntegerField(hitReceivedEffectPane, "Player HP bonus min: ", hitReceivedEffect.hp_boost_min, true, item.writable, listener);
        hitReceivedHPMax = addIntegerField(hitReceivedEffectPane, "Player HP bonus max: ", hitReceivedEffect.hp_boost_max, true, item.writable, listener);
        hitReceivedAPMin = addIntegerField(hitReceivedEffectPane, "Player AP bonus min: ", hitReceivedEffect.ap_boost_min, true, item.writable, listener);
        hitReceivedAPMax = addIntegerField(hitReceivedEffectPane, "Player AP bonus max: ", hitReceivedEffect.ap_boost_max, true, item.writable, listener);
        String roleHitReceivedTarget = "Attacker";
        hitReceivedHPMinTarget = addIntegerField(hitReceivedEffectPane, roleHitReceivedTarget + " HP bonus min: ", hitReceivedEffect.target.hp_boost_min, true, item.writable, listener);
        hitReceivedHPMaxTarget = addIntegerField(hitReceivedEffectPane, roleHitReceivedTarget + " HP bonus max: ", hitReceivedEffect.target.hp_boost_max, true, item.writable, listener);
        hitReceivedAPMinTarget = addIntegerField(hitReceivedEffectPane, roleHitReceivedTarget + " AP bonus min: ", hitReceivedEffect.target.ap_boost_min, true, item.writable, listener);
        hitReceivedAPMaxTarget = addIntegerField(hitReceivedEffectPane, roleHitReceivedTarget + " AP bonus max: ", hitReceivedEffect.target.ap_boost_max, true, item.writable, listener);

        String titleHitReceivedSourceConditions = "Actor Conditions applied to the player: ";
        hitReceivedSourceConditionsModel = new SourceTimedConditionsListModel(killEffect);
        CommonEditor.TimedConditionsCellRenderer cellRendererHitReceivedSourceConditions = new CommonEditor.TimedConditionsCellRenderer();
        BasicLambdaWithArg<Common.TimedActorConditionEffect> selectedSetHitReceivedSourceConditions = (value)->selectedHitReceivedEffectSourceCondition = value;
        BasicLambdaWithReturn<Common.TimedActorConditionEffect> selectedGetHitReceivedSourceConditions = ()->selectedHitReceivedEffectSourceCondition ;
        BasicLambda selectedResetHitReceivedSourceConditions = ()->selectedHitReceivedEffectSourceCondition = null;
        BasicLambdaWithArg<JPanel> updatePaneHitReceivedSourceConditions = (editorPane) -> updateHitReceivedSourceTimedConditionEditorPane(editorPane, selectedHitReceivedEffectSourceCondition, listener);
        var resultHitReceivedSourceConditions = UiUtils.getCollapsibleItemList(listener,
                hitReceivedSourceConditionsModel,
                selectedResetHitReceivedSourceConditions,
                selectedSetHitReceivedSourceConditions,
                selectedGetHitReceivedSourceConditions,
                (x) -> {},
                updatePaneHitReceivedSourceConditions,
                item.writable,
                Common.TimedActorConditionEffect::new,
                cellRendererHitReceivedSourceConditions,
                titleHitReceivedSourceConditions,
                (x) -> null);
        hitReceivedSourceConditionsList = resultHitReceivedSourceConditions.list;
        CollapsiblePanel hitReceivedSourceConditionsPane = resultHitReceivedSourceConditions.collapsiblePanel;
        if (item.hit_received_effect == null || item.hit_received_effect.conditions_source == null || item.hit_received_effect.conditions_source.isEmpty()) {
            hitReceivedSourceConditionsPane.collapse();
        }
        hitReceivedEffectPane.add(hitReceivedSourceConditionsPane, JideBoxLayout.FIX);

        String titleHitReceivedTargetConditions = "Actor Conditions applied to the attacker: ";
        hitReceivedTargetConditionsModel = new TargetTimedConditionsListModel(hitReceivedEffect);
        CommonEditor.TimedConditionsCellRenderer cellRendererHitReceivedTargetConditions = new CommonEditor.TimedConditionsCellRenderer();
        BasicLambdaWithArg<Common.TimedActorConditionEffect> selectedSetHitReceivedTargetConditions = (value)->selectedHitReceivedEffectTargetCondition = value;
        BasicLambdaWithReturn<Common.TimedActorConditionEffect> selectedGetHitReceivedTargetConditions = ()->selectedHitReceivedEffectTargetCondition ;
        BasicLambda selectedResetHitReceivedTargetConditions = ()->selectedHitReceivedEffectTargetCondition = null;
        BasicLambdaWithArg<JPanel> updatePaneHitReceivedTargetConditions = (editorPane) -> updateHitReceivedTargetTimedConditionEditorPane(editorPane, selectedHitReceivedEffectTargetCondition, listener);
        var resultHitReceivedTargetConditions = UiUtils.getCollapsibleItemList(listener,
                hitReceivedTargetConditionsModel,
                selectedResetHitReceivedTargetConditions,
                selectedSetHitReceivedTargetConditions,
                selectedGetHitReceivedTargetConditions,
                (x) -> {},
                updatePaneHitReceivedTargetConditions,
                item.writable,
                Common.TimedActorConditionEffect::new,
                cellRendererHitReceivedTargetConditions,
                titleHitReceivedTargetConditions,
                (x) -> null);
        hitReceivedTargetConditionsList = resultHitReceivedTargetConditions.list;
        CollapsiblePanel hitReceivedTargetConditionsPane = resultHitReceivedTargetConditions.collapsiblePanel;
        if (item.hit_received_effect == null || item.hit_received_effect.conditions_target == null || item.hit_received_effect.conditions_target.isEmpty()) {
            hitReceivedTargetConditionsPane.collapse();
        }
        hitReceivedEffectPane.add(hitReceivedTargetConditionsPane, JideBoxLayout.FIX);

        if (item.hit_received_effect == null) {
            hitReceivedEffectPane.collapse();
        }
        pane.add(hitReceivedEffectPane, JideBoxLayout.FIX);


        if (item.category == null || item.category.action_type == null || item.category.action_type == ItemCategory.ActionType.none) {
            equipEffectPane.setVisible(false);
            hitEffectPane.setVisible(false);
            killEffectPane.setVisible(false);
        } else if (item.category.action_type == ItemCategory.ActionType.use) {
            equipEffectPane.setVisible(false);
            hitEffectPane.setVisible(false);
            killEffectPane.setVisible(true);
            killEffectPane.setTitle(useLabel);
            killEffectPane.revalidate();
            killEffectPane.repaint();
        } else if (item.category.action_type == ItemCategory.ActionType.equip) {
            equipEffectPane.setVisible(true);
            hitEffectPane.setVisible(true);
            killEffectPane.setVisible(true);
            killEffectPane.setTitle(killLabel);
            killEffectPane.revalidate();
            killEffectPane.repaint();
        }

    }

    public void updateHitSourceTimedConditionEditorPane(JPanel pane, Common.TimedActorConditionEffect condition, final FieldUpdateListener listener) {
        pane.removeAll();
        if (hitSourceConditionBox != null) {
            removeElementListener(hitSourceConditionBox);
        }
        if (condition == null) {
            pane.revalidate();
            pane.repaint();
            return;
        }

        boolean writable = ((Item) target).writable;
        Project proj = ((Item) target).getProject();

        hitSourceConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
        hitSourceConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);

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

        updateHitSourceTimedConditionWidgets(condition);

        hitSourceConditionClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitSourceConditionClear, hitSourceConditionClear.isSelected());
            }
        });
        hitSourceConditionApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitSourceConditionApply, hitSourceConditionApply.isSelected());
            }
        });
        hitSourceConditionImmunity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitSourceConditionImmunity, hitSourceConditionImmunity.isSelected());
            }
        });

        hitSourceConditionTimed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitSourceConditionTimed, hitSourceConditionTimed.isSelected());
            }
        });
        hitSourceConditionForever.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitSourceConditionForever, hitSourceConditionForever.isSelected());
            }
        });

        pane.revalidate();
        pane.repaint();
    }

    public void updateHitSourceTimedConditionWidgets(Common.TimedActorConditionEffect condition) {

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

    public void updateHitTargetTimedConditionEditorPane(JPanel pane, Common.TimedActorConditionEffect condition, final FieldUpdateListener listener) {
        pane.removeAll();
        if (hitTargetConditionBox != null) {
            removeElementListener(hitTargetConditionBox);
        }
        if (condition == null) {
            pane.revalidate();
            pane.repaint();
            return;
        }

        boolean writable = ((Item) target).writable;
        Project proj = ((Item) target).getProject();

        hitTargetConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
        hitTargetConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);

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
                listener.valueChanged(hitTargetConditionClear, hitTargetConditionClear.isSelected());
            }
        });
        hitTargetConditionApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitTargetConditionApply, hitTargetConditionApply.isSelected());
            }
        });
        hitTargetConditionImmunity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitTargetConditionImmunity, hitTargetConditionImmunity.isSelected());
            }
        });

        hitTargetConditionTimed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitTargetConditionTimed, hitTargetConditionTimed.isSelected());
            }
        });
        hitTargetConditionForever.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitTargetConditionForever, hitTargetConditionForever.isSelected());
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

    public void updateKillSourceTimedConditionEditorPane(JPanel pane, Common.TimedActorConditionEffect condition, final FieldUpdateListener listener) {
        pane.removeAll();
        if (killSourceConditionBox != null) {
            removeElementListener(killSourceConditionBox);
        }
        if (condition == null) {
            pane.revalidate();
            pane.repaint();
            return;
        }

        boolean writable = ((Item) target).writable;
        Project proj = ((Item) target).getProject();

        killSourceConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
        killSourceConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);

        killSourceConditionClear = new JRadioButton("Clear active condition");
        pane.add(killSourceConditionClear, JideBoxLayout.FIX);
        killSourceConditionApply = new JRadioButton("Apply condition with magnitude");
        pane.add(killSourceConditionApply, JideBoxLayout.FIX);
        killSourceConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
        killSourceConditionImmunity = new JRadioButton("Give immunity to condition");
        pane.add(killSourceConditionImmunity, JideBoxLayout.FIX);

        ButtonGroup radioEffectGroup = new ButtonGroup();
        radioEffectGroup.add(killSourceConditionApply);
        radioEffectGroup.add(killSourceConditionClear);
        radioEffectGroup.add(killSourceConditionImmunity);

        killSourceConditionTimed = new JRadioButton("For a number of rounds");
        pane.add(killSourceConditionTimed, JideBoxLayout.FIX);
        killSourceConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, 1, false, writable, listener);
        killSourceConditionForever = new JRadioButton("Forever");
        pane.add(killSourceConditionForever, JideBoxLayout.FIX);

        ButtonGroup radioDurationGroup = new ButtonGroup();
        radioDurationGroup.add(killSourceConditionTimed);
        radioDurationGroup.add(killSourceConditionForever);

        updateKillSourceTimedConditionWidgets(condition);

        killSourceConditionClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(killSourceConditionClear, killSourceConditionClear.isSelected());
            }
        });
        killSourceConditionApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(killSourceConditionApply, killSourceConditionApply.isSelected());
            }
        });
        killSourceConditionImmunity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(killSourceConditionImmunity, killSourceConditionImmunity.isSelected());
            }
        });

        killSourceConditionTimed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(killSourceConditionTimed, killSourceConditionTimed.isSelected());
            }
        });
        killSourceConditionForever.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(killSourceConditionForever, killSourceConditionForever.isSelected());
            }
        });

        pane.revalidate();
        pane.repaint();
    }

    public void updateKillSourceTimedConditionWidgets(Common.TimedActorConditionEffect condition) {

        boolean immunity = condition.isImmunity();
        boolean clear = condition.isClear();
        boolean forever = condition.isInfinite();

        killSourceConditionClear.setSelected(clear);
        killSourceConditionApply.setSelected(!clear && !immunity);
        killSourceConditionMagnitude.setEnabled(!clear && !immunity);
        killSourceConditionImmunity.setSelected(immunity);

        killSourceConditionTimed.setSelected(!forever);
        killSourceConditionTimed.setEnabled(!clear);
        killSourceConditionDuration.setEnabled(!clear && !forever);
        killSourceConditionForever.setSelected(forever);
        killSourceConditionForever.setEnabled(!clear);
    }

    public void updateEquipConditionEditorPane(JPanel pane, Common.ActorConditionEffect condition, final FieldUpdateListener listener) {
        pane.removeAll();
        if (equipConditionBox != null) {
            removeElementListener(equipConditionBox);
        }
        if (condition == null) {
            pane.revalidate();
            pane.repaint();
            return;
        }

        boolean writable = ((Item) target).writable;
        Project proj = ((Item) target).getProject();

        equipConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
        equipConditionWithMagnitude = new JRadioButton("Apply condition with magnitude.");
        pane.add(equipConditionWithMagnitude, JideBoxLayout.FIX);
        equipConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude, 1, false, writable, listener);
        equipConditionImmunity = new JRadioButton("Give immunity to condition.");
        pane.add(equipConditionImmunity, JideBoxLayout.FIX);

        ButtonGroup radioEffectGroup = new ButtonGroup();
        radioEffectGroup.add(equipConditionWithMagnitude);
        radioEffectGroup.add(equipConditionImmunity);

        boolean immunity = condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR;
        equipConditionImmunity.setSelected(immunity);
        equipConditionWithMagnitude.setSelected(!immunity);
        equipConditionMagnitude.setEnabled(!immunity);

        equipConditionWithMagnitude.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(equipConditionWithMagnitude, equipConditionWithMagnitude.isSelected());
            }
        });
        equipConditionImmunity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(equipConditionImmunity, equipConditionImmunity.isSelected());
            }
        });


        pane.revalidate();
        pane.repaint();
    }

    public void updateHitReceivedSourceTimedConditionEditorPane(JPanel pane, Common.TimedActorConditionEffect condition, final FieldUpdateListener listener) {
        pane.removeAll();
        if (hitReceivedSourceConditionBox != null) {
            removeElementListener(hitReceivedSourceConditionBox);
        }
        if (condition == null) {
            pane.revalidate();
            pane.repaint();
            return;
        }

        boolean writable = ((Item) target).writable;
        Project proj = ((Item) target).getProject();

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
                listener.valueChanged(hitReceivedSourceConditionClear, hitReceivedSourceConditionClear.isSelected());
            }
        });
        hitReceivedSourceConditionApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedSourceConditionApply, hitReceivedSourceConditionApply.isSelected());
            }
        });
        hitReceivedSourceConditionImmunity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedSourceConditionImmunity, hitReceivedSourceConditionImmunity.isSelected());
            }
        });

        hitReceivedSourceConditionTimed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedSourceConditionTimed, hitReceivedSourceConditionTimed.isSelected());
            }
        });
        hitReceivedSourceConditionForever.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedSourceConditionForever, hitReceivedSourceConditionForever.isSelected());
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
        if (condition == null) {
            pane.revalidate();
            pane.repaint();
            return;
        }

        boolean writable = ((Item) target).writable;
        Project proj = ((Item) target).getProject();

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
                listener.valueChanged(hitReceivedTargetConditionClear, hitReceivedTargetConditionClear.isSelected());
            }
        });
        hitReceivedTargetConditionApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedTargetConditionApply, hitReceivedTargetConditionApply.isSelected());
            }
        });
        hitReceivedTargetConditionImmunity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedTargetConditionImmunity, hitReceivedTargetConditionImmunity.isSelected());
            }
        });

        hitReceivedTargetConditionTimed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedTargetConditionTimed, hitReceivedTargetConditionTimed.isSelected());
            }
        });
        hitReceivedTargetConditionForever.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.valueChanged(hitReceivedTargetConditionForever, hitReceivedTargetConditionForever.isSelected());
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

    public static class ConditionsListModel extends OrderedListenerListModel<Item.EquipEffect, Common.ActorConditionEffect> {
        public ConditionsListModel(Item.EquipEffect equipEffect) {
            super(equipEffect);
        }

        @Override
        protected List<Common.ActorConditionEffect> getItems() {
            return source.conditions;
        }

        @Override
        protected void setItems(List<Common.ActorConditionEffect> conditions) {
            source.conditions = conditions;
        }
    }


    public static boolean isNull(Item.EquipEffect effect) {
        if (effect.conditions != null) return false;
        if (effect.critical_multiplier != null) return false;
        if (effect.damage_modifier != null) return false;
        if (effect.damage_boost_max != null) return false;
        if (effect.damage_boost_min != null) return false;
        if (effect.increase_attack_chance != null) return false;
        if (effect.increase_attack_cost != null) return false;
        if (effect.increase_block_chance != null) return false;
        if (effect.increase_critical_skill != null) return false;
        if (effect.increase_damage_resistance != null) return false;
        if (effect.increase_move_cost != null) return false;
        if (effect.increase_reequip_cost != null) return false;
        if (effect.increase_use_item_cost != null) return false;
        if (effect.max_ap_boost != null) return false;
        if (effect.max_hp_boost != null) return false;
        return true;
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


    public static boolean isNull(Common.DeathEffect effect) {
        if (effect.ap_boost_min != null) return false;
        if (effect.ap_boost_max != null) return false;
        if (effect.hp_boost_min != null) return false;
        if (effect.hp_boost_max != null) return false;
        if (effect.conditions_source != null) return false;
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


    public class ItemFieldUpdater implements FieldUpdateListener {

        @Override
        public void valueChanged(JComponent source, Object value) {
            Item item = (Item) target;
            boolean updatePrice, updateEquip, updateHit, updateKill, updateHitReceived;
            updatePrice = updateEquip = updateHit = updateKill = updateHitReceived = false;
            if (source == idField) {
                //Events caused by cancel an ID edition. Dismiss.
                if (skipNext) {
                    skipNext = false;
                    return;
                }
                if (target.id.equals((String) value)) return;

                if (idChanging()) {
                    item.id = (String) value;
                    ItemEditor.this.name = item.getDesc();
                    item.childrenChanged(new ArrayList<ProjectTreeNode>());
                    ATContentStudio.frame.editorChanged(ItemEditor.this);
                } else {
                    cancelIdEdit(idField);
                    return;
                }
            } else if (source == nameField) {
                item.name = (String) value;
                ItemEditor.this.name = item.getDesc();
                item.childrenChanged(new ArrayList<ProjectTreeNode>());
                ATContentStudio.frame.editorChanged(ItemEditor.this);
            } else if (source == itemIcon) {
                item.icon_id = (String) value;
                item.childrenChanged(new ArrayList<ProjectTreeNode>());
                ItemEditor.this.icon = new ImageIcon(item.getProject().getIcon((String) value));
                ATContentStudio.frame.editorChanged(ItemEditor.this);
                itemIcon.setIcon(new ImageIcon(item.getProject().getImage((String) value)));
                itemIcon.revalidate();
                itemIcon.repaint();
            } else if (source == descriptionField) {
                item.description = descriptionField.getText();
            } else if (source == typeBox) {
                item.display_type = (Item.DisplayType) value;
            } else if (source == manualPriceBox) {
                item.has_manual_price = (Integer) value;
                if (!manualPriceBox.isSelected()) {
                    baseCostField.setEnabled(false);
                    updatePrice = true;
                } else {
                    baseCostField.setEnabled(true);
                    if (baseManualPrice != null) {
                        baseCostField.setValue(baseManualPrice);
                    }
                }
            } else if (source == baseCostField) {
                if (manualPriceBox.isSelected()) {
                    item.base_market_cost = (Integer) value;
                    baseManualPrice = item.base_market_cost;
                }
            } else if (source == categoryBox) {
                if (item.category != null) {
                    item.category.removeBacklink(item);
                }
                item.category = (ItemCategory) value;
                if (item.category != null) {
                    item.category_id = item.category.id;
                    item.category.addBacklink(item);
                } else {
                    item.category_id = null;
                }
                if (item.category == null || item.category.action_type == null || item.category.action_type == ItemCategory.ActionType.none) {
                    equipEffectPane.setVisible(false);
                    item.equip_effect = null;
                    hitEffectPane.setVisible(false);
                    item.hit_effect = null;
                    killEffectPane.setVisible(false);
                    item.kill_effect = null;
                    hitReceivedEffectPane.setVisible(false);
                    item.hit_received_effect = null;
                    ItemEditor.this.revalidate();
                    ItemEditor.this.repaint();
                } else if (item.category.action_type == ItemCategory.ActionType.use) {
                    equipEffectPane.setVisible(false);
                    item.equip_effect = null;
                    hitEffectPane.setVisible(false);
                    item.hit_effect = null;
                    killEffectPane.setVisible(true);
                    updateKill = true;
                    hitReceivedEffectPane.setVisible(false);
                    item.hit_received_effect = null;
                    killEffectPane.setTitle(useLabel);
                    ItemEditor.this.revalidate();
                    ItemEditor.this.repaint();
                } else if (item.category.action_type == ItemCategory.ActionType.equip) {
                    equipEffectPane.setVisible(true);
                    hitEffectPane.setVisible(true);
                    killEffectPane.setVisible(true);
                    updateKill = true;
                    hitReceivedEffectPane.setVisible(true);
                    updateEquip = true;
                    killEffectPane.setTitle(killLabel);
                    ItemEditor.this.revalidate();
                    ItemEditor.this.repaint();
                }
                updatePrice = true;
            } else if (source == equipDmgMin) {
                equipEffect.damage_boost_min = (Integer) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipDmgMax) {
                equipEffect.damage_boost_max = (Integer) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipBoostHP) {
                equipEffect.max_hp_boost = (Integer) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipBoostAP) {
                equipEffect.max_ap_boost = (Integer) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipBoostAC) {
                equipEffect.increase_attack_chance = (Integer) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipBoostBC) {
                equipEffect.increase_block_chance = (Integer) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipBoostCS) {
                equipEffect.increase_critical_skill = (Integer) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipSetCM) {
                equipEffect.critical_multiplier = (Double) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipSetDM) {
                equipEffect.damage_modifier = (Integer) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipBoostDR) {
                equipEffect.increase_damage_resistance = (Integer) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipIncMoveCost) {
                equipEffect.increase_move_cost = (Integer) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipIncUseCost) {
                equipEffect.increase_use_item_cost = (Integer) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipIncReequipCost) {
                equipEffect.increase_reequip_cost = (Integer) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipIncAttackCost) {
                equipEffect.increase_attack_cost = (Integer) value;
                updatePrice = true;
                updateEquip = true;
            } else if (source == equipConditionsList) {
                updateEquip = true;
            } else if (source == equipConditionBox) {
                updateConditionEffect((ActorCondition) value, item, selectedEquipEffectCondition, equipConditionsModel);
            } else if (source == equipConditionMagnitude) {
                selectedEquipEffectCondition.magnitude = (Integer) value;
                equipConditionsModel.itemChanged(selectedEquipEffectCondition);
            } else if (source == equipConditionImmunity && (Boolean) value) {
                selectedEquipEffectCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                equipConditionMagnitude.setEnabled(false);
                equipConditionsModel.itemChanged(selectedEquipEffectCondition);
            } else if (source == equipConditionWithMagnitude && (Boolean) value) {
                selectedEquipEffectCondition.magnitude = (Integer) equipConditionMagnitude.getValue();
                equipConditionMagnitude.setEnabled(true);
                equipConditionsModel.itemChanged(selectedEquipEffectCondition);
            } else if (source == hitHPMin) {
                hitEffect.hp_boost_min = (Integer) value;
                updatePrice = true;
                updateHit = true;
            } else if (source == hitHPMax) {
                hitEffect.hp_boost_max = (Integer) value;
                updatePrice = true;
                updateHit = true;
            } else if (source == hitAPMin) {
                hitEffect.ap_boost_min = (Integer) value;
                updatePrice = true;
                updateHit = true;
            } else if (source == hitAPMax) {
                hitEffect.ap_boost_max = (Integer) value;
                updatePrice = true;
                updateHit = true;
            } else if (source == hitSourceConditionsList) {
                updateHit = true;
            } else if (source == hitSourceConditionBox) {
                updateConditionEffect((ActorCondition)value, item, selectedHitEffectSourceCondition, hitSourceConditionsModel);
                updateHit = true;
            } else if (source == hitSourceConditionClear && (Boolean) value) {
                selectedHitEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedHitEffectSourceCondition.duration = null;
                updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
                hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitSourceConditionApply && (Boolean) value) {
                selectedHitEffectSourceCondition.magnitude = (Integer) hitSourceConditionMagnitude.getValue();
                selectedHitEffectSourceCondition.duration = hitSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitSourceConditionDuration.getValue();
                if (selectedHitEffectSourceCondition.duration == null) {
                    selectedHitEffectSourceCondition.duration = 1;
                }
                updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
                hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitSourceConditionImmunity && (Boolean) value) {
                selectedHitEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedHitEffectSourceCondition.duration = hitSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitSourceConditionDuration.getValue();
                if (selectedHitEffectSourceCondition.duration == null || selectedHitEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitEffectSourceCondition.duration = 1;
                }
                updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
                hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitSourceConditionMagnitude) {
                selectedHitEffectSourceCondition.magnitude = (Integer) value;
                hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitSourceConditionTimed && (Boolean) value) {
                selectedHitEffectSourceCondition.duration = (Integer) hitSourceConditionDuration.getValue();
                if (selectedHitEffectSourceCondition.duration == null || selectedHitEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitEffectSourceCondition.duration = 1;
                }
                updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
                hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitSourceConditionForever && (Boolean) value) {
                selectedHitEffectSourceCondition.duration = ActorCondition.DURATION_FOREVER;
                updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
                hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitSourceConditionDuration) {
                selectedHitEffectSourceCondition.duration = (Integer) value;
                hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitSourceConditionChance) {
                selectedHitEffectSourceCondition.chance = (Double) value;
                hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
                updateHit = true;
            } else if (source == hitTargetConditionsList) {
                updateHit = true;
            } else if (source == hitTargetConditionBox) {
                updateConditionEffect((ActorCondition)value, item, selectedHitEffectTargetCondition, hitTargetConditionsModel);
                updateHit = true;
            } else if (source == hitTargetConditionClear && (Boolean) value) {
                selectedHitEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedHitEffectTargetCondition.duration = null;
                updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
                hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitTargetConditionApply && (Boolean) value) {
                selectedHitEffectTargetCondition.magnitude = (Integer) hitTargetConditionMagnitude.getValue();
                selectedHitEffectTargetCondition.duration = hitTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitTargetConditionDuration.getValue();
                if (selectedHitEffectTargetCondition.duration == null || selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitEffectTargetCondition.duration = 1;
                }
                updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
                hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitTargetConditionImmunity && (Boolean) value) {
                selectedHitEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedHitEffectTargetCondition.duration = hitTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitTargetConditionDuration.getValue();
                if (selectedHitEffectTargetCondition.duration == null || selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitEffectTargetCondition.duration = 1;
                }
                updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
                hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitTargetConditionMagnitude) {
                selectedHitEffectTargetCondition.magnitude = (Integer) value;
                hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitTargetConditionTimed && (Boolean) value) {
                selectedHitEffectTargetCondition.duration = (Integer) hitTargetConditionDuration.getValue();
                if (selectedHitEffectTargetCondition.duration == null || selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitEffectTargetCondition.duration = 1;
                }
                updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
                hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitTargetConditionForever && (Boolean) value) {
                selectedHitEffectTargetCondition.duration = ActorCondition.DURATION_FOREVER;
                updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
                hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitTargetConditionDuration) {
                selectedHitEffectTargetCondition.duration = (Integer) value;
                hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == hitTargetConditionChance) {
                selectedHitEffectTargetCondition.chance = (Double) value;
                hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
                updateHit = true;
            } else if (source == killHPMin) {
                killEffect.hp_boost_min = (Integer) value;
                updatePrice = true;
                updateKill = true;
            } else if (source == killHPMax) {
                killEffect.hp_boost_max = (Integer) value;
                updatePrice = true;
                updateKill = true;
            } else if (source == killAPMin) {
                killEffect.ap_boost_min = (Integer) value;
                updatePrice = true;
                updateKill = true;
            } else if (source == killAPMax) {
                killEffect.ap_boost_max = (Integer) value;
                updatePrice = true;
                updateKill = true;
            } else if (source == killSourceConditionsList) {
                updateKill = true;
            } else if (source == killSourceConditionBox) {
                updateConditionEffect((ActorCondition) value, item, selectedKillEffectCondition, killSourceConditionsModel);
                updateKill = true;
            } else if (source == killSourceConditionClear && (Boolean) value) {
                selectedKillEffectCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedKillEffectCondition.duration = null;
                updateKillSourceTimedConditionWidgets(selectedKillEffectCondition);
                killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
                updateKill = true;
            } else if (source == killSourceConditionApply && (Boolean) value) {
                selectedKillEffectCondition.magnitude = (Integer) killSourceConditionMagnitude.getValue();
                selectedKillEffectCondition.duration = killSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) killSourceConditionDuration.getValue();
                if (selectedKillEffectCondition.duration == null || selectedKillEffectCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedKillEffectCondition.duration = 1;
                }
                updateKillSourceTimedConditionWidgets(selectedKillEffectCondition);
                killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
                updateKill = true;
            } else if (source == killSourceConditionImmunity && (Boolean) value) {
                selectedKillEffectCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedKillEffectCondition.duration = killSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) killSourceConditionDuration.getValue();
                if (selectedKillEffectCondition.duration == null || selectedKillEffectCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedKillEffectCondition.duration = 1;
                }
                updateKillSourceTimedConditionWidgets(selectedKillEffectCondition);
                killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
                updateKill = true;
            } else if (source == killSourceConditionMagnitude) {
                selectedKillEffectCondition.magnitude = (Integer) value;
                killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
                updateKill = true;
            } else if (source == killSourceConditionTimed && (Boolean) value) {
                selectedKillEffectCondition.duration = (Integer) killSourceConditionDuration.getValue();
                if (selectedKillEffectCondition.duration == null || selectedKillEffectCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedKillEffectCondition.duration = 1;
                }
                updateKillSourceTimedConditionWidgets(selectedKillEffectCondition);
                killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
                updateKill = true;
            } else if (source == killSourceConditionForever && (Boolean) value) {
                selectedKillEffectCondition.duration = ActorCondition.DURATION_FOREVER;
                updateKillSourceTimedConditionWidgets(selectedKillEffectCondition);
                killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
                updateKill = true;
            } else if (source == killSourceConditionDuration) {
                selectedKillEffectCondition.duration = (Integer) value;
                killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
                updateKill = true;
            } else if (source == killSourceConditionChance) {
                selectedKillEffectCondition.chance = (Double) value;
                killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
                updateKill = true;
            } else if (source == hitReceivedHPMin) {
                hitReceivedEffect.hp_boost_min = (Integer) value;
                updatePrice = true;
                updateHitReceived = true;
            } else if (source == hitReceivedHPMax) {
                hitReceivedEffect.hp_boost_max = (Integer) value;
                updatePrice = true;
                updateHitReceived = true;
            } else if (source == hitReceivedAPMin) {
                hitReceivedEffect.ap_boost_min = (Integer) value;
                updatePrice = true;
                updateHitReceived = true;
            } else if (source == hitReceivedAPMax) {
                hitReceivedEffect.ap_boost_max = (Integer) value;
                updatePrice = true;
                updateHitReceived = true;
            } else if (source == hitReceivedHPMinTarget) {
                hitReceivedEffect.target.hp_boost_min = (Integer) value;
                updatePrice = true;
                updateHitReceived = true;
            } else if (source == hitReceivedHPMaxTarget) {
                hitReceivedEffect.target.hp_boost_max = (Integer) value;
                updatePrice = true;
                updateHitReceived = true;
            } else if (source == hitReceivedAPMinTarget) {
                hitReceivedEffect.target.ap_boost_min = (Integer) value;
                updatePrice = true;
                updateHitReceived = true;
            } else if (source == hitReceivedAPMaxTarget) {
                hitReceivedEffect.target.ap_boost_max = (Integer) value;
                updatePrice = true;
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionsList) {
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionBox) {
                updateConditionEffect((ActorCondition) value, item, selectedHitReceivedEffectSourceCondition, hitReceivedSourceConditionsModel);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionClear && (Boolean) value) {
                selectedHitReceivedEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedHitReceivedEffectSourceCondition.duration = null;
                updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
                hitReceivedSourceConditionsModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionApply && (Boolean) value) {
                selectedHitReceivedEffectSourceCondition.magnitude = (Integer) hitReceivedSourceConditionMagnitude.getValue();
                selectedHitReceivedEffectSourceCondition.duration = hitReceivedSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitReceivedSourceConditionDuration.getValue();
                if (selectedHitReceivedEffectSourceCondition.duration == null) {
                    selectedHitReceivedEffectSourceCondition.duration = 1;
                }
                updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
                hitReceivedSourceConditionsModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionImmunity && (Boolean) value) {
                selectedHitReceivedEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedHitReceivedEffectSourceCondition.duration = hitReceivedSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitReceivedSourceConditionDuration.getValue();
                if (selectedHitReceivedEffectSourceCondition.duration == null || selectedHitReceivedEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitReceivedEffectSourceCondition.duration = 1;
                }
                updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
                hitReceivedSourceConditionsModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionMagnitude) {
                selectedHitReceivedEffectSourceCondition.magnitude = (Integer) value;
                hitReceivedSourceConditionsModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionTimed && (Boolean) value) {
                selectedHitReceivedEffectSourceCondition.duration = (Integer) hitReceivedSourceConditionDuration.getValue();
                if (selectedHitReceivedEffectSourceCondition.duration == null || selectedHitReceivedEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitReceivedEffectSourceCondition.duration = 1;
                }
                updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
                hitReceivedSourceConditionsModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionForever && (Boolean) value) {
                selectedHitReceivedEffectSourceCondition.duration = ActorCondition.DURATION_FOREVER;
                updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
                hitReceivedSourceConditionsModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionDuration) {
                selectedHitReceivedEffectSourceCondition.duration = (Integer) value;
                hitReceivedSourceConditionsModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedSourceConditionChance) {
                selectedHitReceivedEffectSourceCondition.chance = (Double) value;
                hitReceivedSourceConditionsModel.itemChanged(selectedHitReceivedEffectSourceCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionsList) {
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionBox) {
                updateConditionEffect((ActorCondition) value, item, selectedHitReceivedEffectTargetCondition, hitReceivedTargetConditionsModel);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionClear && (Boolean) value) {
                selectedHitReceivedEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedHitReceivedEffectTargetCondition.duration = null;
                updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
                hitReceivedTargetConditionsModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionApply && (Boolean) value) {
                selectedHitReceivedEffectTargetCondition.magnitude = (Integer) hitReceivedTargetConditionMagnitude.getValue();
                selectedHitReceivedEffectTargetCondition.duration = hitReceivedTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitReceivedTargetConditionDuration.getValue();
                if (selectedHitReceivedEffectTargetCondition.duration == null || selectedHitReceivedEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitReceivedEffectTargetCondition.duration = 1;
                }
                updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
                hitReceivedTargetConditionsModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionImmunity && (Boolean) value) {
                selectedHitReceivedEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
                selectedHitReceivedEffectTargetCondition.duration = hitReceivedTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitReceivedTargetConditionDuration.getValue();
                if (selectedHitReceivedEffectTargetCondition.duration == null || selectedHitReceivedEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitReceivedEffectTargetCondition.duration = 1;
                }
                updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
                hitReceivedTargetConditionsModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionMagnitude) {
                selectedHitReceivedEffectTargetCondition.magnitude = (Integer) value;
                hitReceivedTargetConditionsModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionTimed && (Boolean) value) {
                selectedHitReceivedEffectTargetCondition.duration = (Integer) hitReceivedTargetConditionDuration.getValue();
                if (selectedHitReceivedEffectTargetCondition.duration == null || selectedHitReceivedEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
                    selectedHitReceivedEffectTargetCondition.duration = 1;
                }
                updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
                hitReceivedTargetConditionsModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionForever && (Boolean) value) {
                selectedHitReceivedEffectTargetCondition.duration = ActorCondition.DURATION_FOREVER;
                updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
                hitReceivedTargetConditionsModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionDuration) {
                selectedHitReceivedEffectTargetCondition.duration = (Integer) value;
                hitReceivedTargetConditionsModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            } else if (source == hitReceivedTargetConditionChance) {
                selectedHitReceivedEffectTargetCondition.chance = (Double) value;
                hitReceivedTargetConditionsModel.itemChanged(selectedHitReceivedEffectTargetCondition);
                updateHitReceived = true;
            }

            if (updateEquip) {
                if (isNull(equipEffect)) {
                    item.equip_effect = null;
                } else {
                    item.equip_effect = equipEffect;
                }
            }
            if (updateHit) {
                if (isNull(hitEffect)) {
                    item.hit_effect = null;
                } else {
                    item.hit_effect = hitEffect;
                }
            }
            if (updateKill) {
                if (isNull(killEffect)) {
                    item.kill_effect = null;
                } else {
                    item.kill_effect = killEffect;
                }
            }
            if (updateHitReceived) {
                if (isNull(hitReceivedEffect)) {
                    item.hit_received_effect = null;
                } else {
                    item.hit_received_effect = hitReceivedEffect;
                }
            }
            if (updatePrice && !manualPriceBox.isSelected()) {
                baseCostField.setValue(item.computePrice());
            }


            if (item.state != GameDataElement.State.modified) {
                item.state = GameDataElement.State.modified;
                ItemEditor.this.name = item.getDesc();
                item.childrenChanged(new ArrayList<ProjectTreeNode>());
                ATContentStudio.frame.editorChanged(ItemEditor.this);
            }
            updateJsonViewText(item.toJsonString());

        }


    }

}
