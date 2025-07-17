package server;

import client.MapleDisease;
import config.property.Property_Java;
import debug.Debug;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import server.life.MobSkillFactory;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleData;
import provider.MapleDataTool;
import server.life.MobSkill;

public class MapleCarnivalFactory {

    private final static MapleCarnivalFactory instance = new MapleCarnivalFactory();
    private final Map<Integer, MCSkill> skills = new HashMap<Integer, MCSkill>();
    private final Map<Integer, MCSkill> guardians = new HashMap<Integer, MCSkill>();
    private final MapleDataProvider dataRoot = MapleDataProviderFactory.getDataProvider(new File(Property_Java.getDir_WzXml() + "/Skill.wz"));

    public MapleCarnivalFactory() {
        //whoosh
    }

    public static final MapleCarnivalFactory getInstance() {
        return instance;
    }

    public void initialize() {
        if (skills.size() != 0) {
            return;
        }

        for (MapleData z : dataRoot.getData("MCSkill.img")) {

            // THMS meme
            int mobSkillID = 0;
            try {
                mobSkillID = MapleDataTool.getInt("mobSkillID", z, 0);
            } catch (NumberFormatException e) {
                // MCSkill.img/4/mobSkillID
                Debug.ErrorLog("MCSkill.img/" + z.getName() + "/mobSkillID");
                continue;
            }

            skills.put(Integer.parseInt(z.getName()), new MCSkill(MapleDataTool.getInt("spendCP", z, 0), mobSkillID, MapleDataTool.getInt("level", z, 0), MapleDataTool.getInt("target", z, 1) > 1));
        }
        for (MapleData z : dataRoot.getData("MCGuardian.img")) {
            guardians.put(Integer.parseInt(z.getName()), new MCSkill(MapleDataTool.getInt("spendCP", z, 0), MapleDataTool.getInt("mobSkillID", z, 0), MapleDataTool.getInt("level", z, 0), true));
        }
    }

    public MCSkill getSkill(final int id) {
        return skills.get(id);
    }

    public MCSkill getGuardian(final int id) {
        return guardians.get(id);
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
            return MobSkillFactory.getMobSkill(skillid, 1); //level?
        }

        public MapleDisease getDisease() {
            if (skillid <= 0) {
                return MapleDisease.getRandom();
            }
            return MapleDisease.getBySkill(skillid);
        }
    }
}
