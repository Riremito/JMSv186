package odin.server;

import odin.server.life.MobSkill;
import tacos.wz.WzXML;

public class MapleCarnivalFactory {

    private final static MapleCarnivalFactory instance = new MapleCarnivalFactory();

    public static MapleCarnivalFactory getInstance() {
        return instance;
    }

    public MCSkill getSkill(int id) {
        return WzXML.SKILL.getMCSkill().get(id);
    }

    public MCSkill getGuardian(int id) {
        return WzXML.SKILL.getMCGuardian().get(id);
    }

    public static class MCSkill {

        public int cpLoss;
        public int skillid;
        public int level;

        public MCSkill(int _cpLoss, int _skillid, int _level, boolean _targetsAll) {
            cpLoss = _cpLoss;
            skillid = _skillid;
            level = _level;
        }

        public MobSkill getSkill() {
            return WzXML.SKILL.getMobSkillData(skillid, 1); //level?
        }
    }
}
