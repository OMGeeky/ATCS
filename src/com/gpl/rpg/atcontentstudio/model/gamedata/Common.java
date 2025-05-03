package com.gpl.rpg.atcontentstudio.model.gamedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Common {

    public static class TimedConditionEffect extends ConditionEffect {
        //Available from parsed state
        public Integer duration = null;
        public Double chance = null;
    }

    public static class ConditionEffect {
        //Available from parsed state
        public Integer magnitude = null;
        public String condition_id = null;

        //Available from linked state
        public ActorCondition condition = null;
    }

    @SuppressWarnings("rawtypes")
    public static ArrayList<TimedConditionEffect> parseTimedConditionEffects(List conditionsSourceJson) {
        ArrayList<TimedConditionEffect> conditions_source;
        if (conditionsSourceJson != null && !conditionsSourceJson.isEmpty()) {
            conditions_source = new ArrayList<TimedConditionEffect>();
            for (Object conditionJsonObj : conditionsSourceJson) {
                Map conditionJson = (Map) conditionJsonObj;
                TimedConditionEffect condition = new TimedConditionEffect();
                condition.condition_id = (String) conditionJson.get("condition");
                condition.magnitude = JSONElement.getInteger((Number) conditionJson.get("magnitude"));
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
    public static HitReceivedEffect parseHitReceivedEffect(Map hitReceivedEffect) {
        if (hitReceivedEffect == null) {
            return null;
        }

        HitReceivedEffect hit_received_effect = new Common.HitReceivedEffect();
        if (hitReceivedEffect.get("increaseCurrentHP") != null) {
            hit_received_effect.hp_boost_max = JSONElement.getInteger((Number) (((Map) hitReceivedEffect.get("increaseCurrentHP")).get("max")));
            hit_received_effect.hp_boost_min = JSONElement.getInteger((Number) (((Map) hitReceivedEffect.get("increaseCurrentHP")).get("min")));
        }
        if (hitReceivedEffect.get("increaseCurrentAP") != null) {
            hit_received_effect.ap_boost_max = JSONElement.getInteger((Number) (((Map) hitReceivedEffect.get("increaseCurrentAP")).get("max")));
            hit_received_effect.ap_boost_min = JSONElement.getInteger((Number) (((Map) hitReceivedEffect.get("increaseCurrentAP")).get("min")));
        }
        if (hitReceivedEffect.get("increaseAttackerCurrentHP") != null) {
            hit_received_effect.hp_boost_max_target = JSONElement.getInteger((Number) (((Map) hitReceivedEffect.get("increaseAttackerCurrentHP")).get("max")));
            hit_received_effect.hp_boost_min_target = JSONElement.getInteger((Number) (((Map) hitReceivedEffect.get("increaseAttackerCurrentHP")).get("min")));
        }
        if (hitReceivedEffect.get("increaseAttackerCurrentAP") != null) {
            hit_received_effect.ap_boost_max_target = JSONElement.getInteger((Number) (((Map) hitReceivedEffect.get("increaseAttackerCurrentAP")).get("max")));
            hit_received_effect.ap_boost_min_target = JSONElement.getInteger((Number) (((Map) hitReceivedEffect.get("increaseAttackerCurrentAP")).get("min")));
        }
        List conditionsSourceJson = (List) hitReceivedEffect.get("conditionsSource");
        hit_received_effect.conditions_source = parseTimedConditionEffects(conditionsSourceJson);
        List conditionsTargetJson = (List) hitReceivedEffect.get("conditionsTarget");
        hit_received_effect.conditions_target = parseTimedConditionEffects(conditionsTargetJson);
        return hit_received_effect;
    }




    public static class DeathEffect {
        //Available from parsed state
        public Integer hp_boost_min = null;
        public Integer hp_boost_max = null;
        public Integer ap_boost_min = null;
        public Integer ap_boost_max = null;
        public List<Common.TimedConditionEffect> conditions_source = null;
    }

    public static class HitEffect extends DeathEffect {
        //Available from parsed state
        public List<Common.TimedConditionEffect> conditions_target = null;
    }

    public static class HitReceivedEffect extends Common.HitEffect {
        //Available from parsed state
        public Integer hp_boost_min_target = null;
        public Integer hp_boost_max_target = null;
        public Integer ap_boost_min_target = null;
        public Integer ap_boost_max_target = null;
    }

}
