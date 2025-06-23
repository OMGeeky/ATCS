package com.gpl.rpg.atcontentstudio.model.gamedata;

import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Common {

    public static void linkConditions(List<? extends ActorConditionEffect> conditions, Project proj, GameDataElement backlink) {
        if (conditions != null) {
            for (ActorConditionEffect ce : conditions) {
                if (ce.condition_id != null) ce.condition = proj.getActorCondition(ce.condition_id);
                if (ce.condition != null) ce.condition.addBacklink(backlink);
            }
        }
    }

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
