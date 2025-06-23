package com.gpl.rpg.atcontentstudio.model.gamedata;

import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.Project;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Common {

    //region link common stuff
    public static void linkConditions(List<? extends ActorConditionEffect> conditions, Project proj, GameDataElement backlink) {
        if (conditions != null) {
            for (ActorConditionEffect ce : conditions) {
                if (ce.condition_id != null) ce.condition = proj.getActorCondition(ce.condition_id);
                if (ce.condition != null) ce.condition.addBacklink(backlink);
            }
        }
    }
    public static  void linkEffects(HitEffect effect, Project proj, GameDataElement backlink){
        linkEffects((DeathEffect) effect, proj, backlink);
        if (effect != null)
        {
            linkConditions(effect.conditions_target, proj, backlink);
        }
    }
    public static  void linkEffects(DeathEffect effect, Project proj, GameDataElement backlink){
        if (effect != null)
        {
            linkConditions(effect.conditions_source, proj, backlink);
        }
    }
    public static void linkIcon(Project proj, String iconId, GameDataElement backlink) {
        if (iconId != null) {
            String spritesheetId = iconId.split(":")[0];
            proj.getSpritesheet(spritesheetId).addBacklink(backlink);
        }
    }
    //endregion

    //region write common stuff

    public static void writeHitReceivedEffectToMap(Map parent, HitReceivedEffect effect) {
        if(effect != null){
            writeHitEffectToMap(parent, effect);
            writeBasicEffectObjectToMap(effect.target, parent, "increaseAttackerCurrentHP", "increaseAttackerCurrentAP");
        }
    }
    public static void writeHitReceivedEffectToMap(Map parent, HitReceivedEffect effect, String key) {
        if (effect != null) {
            Map effectJson = new LinkedHashMap();
            parent.put(key, effectJson);
            writeHitReceivedEffectToMap(effectJson, effect);
        }
    }
    public static void writeHitEffectToMap(Map parent, HitEffect effect) {
        if(effect != null){
            writeDeathEffectToMap(parent, effect);
            writeTimedActorConditionEffectObjectToMap(effect.conditions_target, parent, "conditionsTarget");
        }
    }
    public static void writeHitEffectToMap(Map parent, HitEffect effect, String key) {
        if (effect != null) {
            Map effectJson = new LinkedHashMap();
            parent.put(key, effectJson);
            writeHitEffectToMap(effectJson, effect);
        }
    }
    public static void writeDeathEffectToMap(Map parent, DeathEffect effect) {
        writeBasicEffectObjectToMap(effect, parent, "increaseCurrentHP", "increaseCurrentAP");
        writeTimedActorConditionEffectObjectToMap(effect.conditions_source, parent, "conditionsSource");
    }
    public static void writeDeathEffectToMap(Map parent, DeathEffect effect, String key) {
        if (effect != null) {
            Map effectJson = new LinkedHashMap();
            parent.put(key, effectJson);
            writeDeathEffectToMap(effectJson, effect);
        }
    }

    public static void writeBasicEffectObjectToMap(BasicEffect effect, Map parent, String keyHP, String keyAP) {
        if (effect.hp_boost_min != null || effect.hp_boost_max != null) {
            Map hpJson = new LinkedHashMap();
            parent.put(keyHP, hpJson);
            if (effect.hp_boost_min != null)
                hpJson.put("min", effect.hp_boost_min);
            else hpJson.put("min", 0);
            if (effect.hp_boost_max != null)
                hpJson.put("max", effect.hp_boost_max);
            else hpJson.put("max", 0);
        }

        if (effect.ap_boost_min != null || effect.ap_boost_max != null) {
            Map apJson = new LinkedHashMap();
            parent.put(keyAP, apJson);
            if (effect.ap_boost_min != null)
                apJson.put("min", effect.ap_boost_min);
            else apJson.put("min", 0);
            if (effect.ap_boost_max != null)
                apJson.put("max", effect.ap_boost_max);
            else apJson.put("max", 0);
        }
    }

    public static void writeTimedActorConditionEffectObjectToMap(List<TimedActorConditionEffect> list, Map parent, String key) {
        if (list != null) {
            List conditionsSourceJson = new ArrayList();
            parent.put(key, conditionsSourceJson);
            for (TimedActorConditionEffect condition : list) {
                Map conditionJson = new LinkedHashMap();
                conditionsSourceJson.add(conditionJson);
                writeTimedConditionEffectToMap(condition, conditionJson);
            }
        }

    }

    public static void writeConditionEffectToMap(ActorConditionEffect condition, Map parent) {
        if (condition.condition != null) {
            parent.put("condition", condition.condition.id);
        } else if (condition.condition_id != null) {
            parent.put("condition", condition.condition_id);
        }
        if (condition.magnitude != null) {
            parent.put("magnitude", condition.magnitude);
        }
    }

    public static void writeTimedConditionEffectToMap(TimedActorConditionEffect condition, Map parent) {
        writeConditionEffectToMap(condition, parent);
        if (condition.duration != null) {
            parent.put("duration", condition.duration);
        }
        if (condition.chance != null) {
            parent.put("chance", JSONElement.printJsonChance(condition.chance));
        }
    }
    //endregion
    public static class TimedActorConditionEffect extends ActorConditionEffect {
        //Available from parsed state
        public Integer duration = null;
        public Double chance = null;

        public TimedActorConditionEffect createClone() {
            TimedActorConditionEffect cclone = new TimedActorConditionEffect();
            cclone.magnitude = this.magnitude;
            cclone.condition_id = this.condition_id;
            cclone.condition = this.condition;
            cclone.chance = this.chance;
            cclone.duration = this.duration;
            return cclone;
        }
        public boolean isInfinite(){
            return duration != null && duration == ActorCondition.DURATION_FOREVER;
        }
        public boolean isImmunity(){
            return (magnitude == null || magnitude == ActorCondition.MAGNITUDE_CLEAR) && (duration != null && duration > ActorCondition.DURATION_NONE);
        }
        public boolean isClear(){
            return (magnitude == null || magnitude == ActorCondition.MAGNITUDE_CLEAR) && (duration == null || duration == ActorCondition.DURATION_NONE);
        }
    }

    public static class ActorConditionEffect {
        //Available from parsed state
        public Integer magnitude = null;
        public String condition_id = null;

        //Available from linked state
        public ActorCondition condition = null;
    }

    @SuppressWarnings("rawtypes")
    public static ArrayList<TimedActorConditionEffect> parseTimedConditionEffects(List conditionsSourceJson) {
        ArrayList<TimedActorConditionEffect> conditions_source;
        if (conditionsSourceJson != null && !conditionsSourceJson.isEmpty()) {
            conditions_source = new ArrayList<>();
            for (Object conditionJsonObj : conditionsSourceJson) {
                Map conditionJson = (Map) conditionJsonObj;
                TimedActorConditionEffect condition = new TimedActorConditionEffect();
                readConditionEffect(condition, conditionJson);
                condition.duration = JSONElement.getInteger((Number) conditionJson.get("duration"));
                if (conditionJson.get("chance") != null)
                    condition.chance = JSONElement.parseChance(conditionJson.get("chance").toString());
                conditions_source.add(condition);
            }
        } else {
            conditions_source = null;
        }
        return conditions_source;
    }

    @SuppressWarnings("rawtypes")
    private static void readConditionEffect(ActorConditionEffect condition, Map conditionJson) {
        condition.condition_id = (String) conditionJson.get("condition");
        condition.magnitude = JSONElement.getInteger((Number) conditionJson.get("magnitude"));
    }

    @SuppressWarnings("rawtypes")
    public static Common.DeathEffect parseDeathEffect(Map killEffect) {
        Common.DeathEffect kill_effect = new Common.DeathEffect();
        readDeathEffect(killEffect, kill_effect);
        return kill_effect;
    }

    @SuppressWarnings("rawtypes")
    public static HitEffect parseHitEffect(Map hitEffect) {
        Common.HitEffect hit_effect = new Common.HitEffect();
        readHitEffect(hitEffect, hit_effect);
        return hit_effect;
    }

    @SuppressWarnings("rawtypes")
    public static HitReceivedEffect parseHitReceivedEffect(Map hitReceivedEffect) {
        HitReceivedEffect hit_received_effect = new Common.HitReceivedEffect();
        readHitEffect(hitReceivedEffect, hit_received_effect);
        if (hitReceivedEffect.get("increaseAttackerCurrentHP") != null) {
            hit_received_effect.target.hp_boost_max = JSONElement.getInteger((Number) (((Map) hitReceivedEffect.get("increaseAttackerCurrentHP")).get("max")));
            hit_received_effect.target.hp_boost_min = JSONElement.getInteger((Number) (((Map) hitReceivedEffect.get("increaseAttackerCurrentHP")).get("min")));
        }
        if (hitReceivedEffect.get("increaseAttackerCurrentAP") != null) {
            hit_received_effect.target.ap_boost_max = JSONElement.getInteger((Number) (((Map) hitReceivedEffect.get("increaseAttackerCurrentAP")).get("max")));
            hit_received_effect.target.ap_boost_min = JSONElement.getInteger((Number) (((Map) hitReceivedEffect.get("increaseAttackerCurrentAP")).get("min")));
        }
        return hit_received_effect;
    }

    @SuppressWarnings("rawtypes")
    private static void readDeathEffect(Map killEffect, DeathEffect kill_effect) {
        if (killEffect.get("increaseCurrentHP") != null) {
            kill_effect.hp_boost_min = JSONElement.getInteger((Number) (((Map) killEffect.get("increaseCurrentHP")).get("min")));
            kill_effect.hp_boost_max = JSONElement.getInteger((Number) (((Map) killEffect.get("increaseCurrentHP")).get("max")));
        }
        if (killEffect.get("increaseCurrentAP") != null) {
            kill_effect.ap_boost_min = JSONElement.getInteger((Number) (((Map) killEffect.get("increaseCurrentAP")).get("min")));
            kill_effect.ap_boost_max = JSONElement.getInteger((Number) (((Map) killEffect.get("increaseCurrentAP")).get("max")));
        }
        List conditionsSourceJson = (List) killEffect.get("conditionsSource");
        kill_effect.conditions_source = parseTimedConditionEffects(conditionsSourceJson);
    }

    @SuppressWarnings("rawtypes")
    private static void readHitEffect(Map hitEffect, HitEffect hit_effect) {
        readDeathEffect(hitEffect, hit_effect);
        List conditionsTargetJson = (List) hitEffect.get("conditionsTarget");
        hit_effect.conditions_target = parseTimedConditionEffects(conditionsTargetJson);
    }

    public static class BasicEffect {
        public Integer hp_boost_min = null;
        public Integer hp_boost_max = null;
        public Integer ap_boost_min = null;
        public Integer ap_boost_max = null;
    }

    public static class DeathEffect extends BasicEffect {
        //Available from parsed state
        public List<TimedActorConditionEffect> conditions_source = null;
    }

    public static class HitEffect extends DeathEffect {
        //Available from parsed state
        public List<TimedActorConditionEffect> conditions_target = null;
    }

    public static class HitReceivedEffect extends Common.HitEffect {
        //Available from parsed state
        public BasicEffect target = new BasicEffect();
    }


    public static void copyDeathEffectValues(Common.DeathEffect target, Common.DeathEffect source, GameDataElement backlink) {
        copyEffectValues(target, source);
        if (source.conditions_source != null) {
            target.conditions_source = new ArrayList<>();
            for (TimedActorConditionEffect c : source.conditions_source) {
                TimedActorConditionEffect cclone = c.createClone();
                if (cclone.condition != null) {
                    cclone.condition.addBacklink(backlink);
                }
                target.conditions_source.add(cclone);
            }
        }
    }

    private static void copyEffectValues(BasicEffect target, BasicEffect source) {
        target.ap_boost_max = source.ap_boost_max;
        target.ap_boost_min = source.ap_boost_min;
        target.hp_boost_max = source.hp_boost_max;
        target.hp_boost_min = source.hp_boost_min;
    }

    public static void copyHitEffectValues(Common.HitEffect target, Common.HitEffect source, GameDataElement backlink) {
        copyDeathEffectValues(target, source, backlink);
        if (source.conditions_target != null) {
            target.conditions_target = new ArrayList<>();
            for (TimedActorConditionEffect c : source.conditions_target) {
                TimedActorConditionEffect cclone = c.createClone();
                if (cclone.condition != null) {
                    cclone.condition.addBacklink(backlink);
                }
                target.conditions_target.add(cclone);
            }
        }
    }

    public static void copyHitReceivedEffectValues(Common.HitReceivedEffect target, Common.HitReceivedEffect source, GameDataElement backlink) {
        copyHitEffectValues(target, source, backlink);
        copyEffectValues(target.target, source.target);
    }

}
