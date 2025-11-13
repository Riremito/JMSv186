package odin.server;

import odin.client.MapleDisease;
import tacos.wz.data.SkillWz;
import odin.server.life.MobSkill;

public class MapleCarnivalFactory {

    private final static MapleCarnivalFactory instance = new MapleCarnivalFactory();

    public static final MapleCarnivalFactory getInstance() {
        return instance;
    }

    public MCSkill getSkill(final int id) {
        return SkillWz.getMCSkill().get(id);
    }

    public MCSkill getGuardian(final int id) {
        return SkillWz.getMCGuardian().get(id);
    }

    public static class MCSkill {

        public int cpLoss, skillid, level;
        public boolean targetsAll;

        public MCSkill(int _cpLoss, int _skillid, int _level, boolean _targetsAll) {
            cpLoss = _cpLoss;
            skillid = _skillid;
            level = _level;
            targetsAll = _targetsAll;
        }

        public MobSkill getSkill() {
            return SkillWz.getMobSkillData(skillid, 1); //level?
        }

        public MapleDisease getDisease() {
            if (skillid <= 0) {
                return MapleDisease.getRandom();
            }
            return MapleDisease.getBySkill(skillid);
        }
    }
}
