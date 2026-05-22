package odin.client.messages.commands;

import odin.client.MapleCharacter;
import odin.constants.ServerConstants.PlayerGMRank;
import odin.client.MapleClient;
import odin.client.MapleDisease;
import odin.client.messages.CommandProcessorUtil;
import tacos.wz.data.SkillWz;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;

/**
 *
 * @author Emilyx3
 */
public class AdminCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.ADMIN;
    }

    public static class Disease extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "!disease <type> [charname] <level> where type = SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE");
                return 0;
            }
            int type = 0;
            MapleDisease dis = null;
            if (splitted[1].equalsIgnoreCase("SEAL")) {
                type = 120;
            } else if (splitted[1].equalsIgnoreCase("DARKNESS")) {
                type = 121;
            } else if (splitted[1].equalsIgnoreCase("WEAKEN")) {
                type = 122;
            } else if (splitted[1].equalsIgnoreCase("STUN")) {
                type = 123;
            } else if (splitted[1].equalsIgnoreCase("CURSE")) {
                type = 124;
            } else if (splitted[1].equalsIgnoreCase("POISON")) {
                type = 125;
            } else if (splitted[1].equalsIgnoreCase("SLOW")) {
                type = 126;
            } else if (splitted[1].equalsIgnoreCase("SEDUCE")) {
                type = 128;
            } else if (splitted[1].equalsIgnoreCase("REVERSE")) {
                type = 132;
            } else if (splitted[1].equalsIgnoreCase("ZOMBIFY")) {
                type = 133;
            } else if (splitted[1].equalsIgnoreCase("POTION")) {
                type = 134;
            } else if (splitted[1].equalsIgnoreCase("SHADOW")) {
                type = 135;
            } else if (splitted[1].equalsIgnoreCase("BLIND")) {
                type = 136;
            } else if (splitted[1].equalsIgnoreCase("FREEZE")) {
                type = 137;
            } else {
                c.getPlayer().dropMessage(6, "!disease <type> [charname] <level> where type = SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE");
                return 0;
            }
            dis = MapleDisease.getBySkill(type);
            if (dis == null) {
                return 0;
            }
            if (splitted.length == 4) {
                MapleCharacter victim = c.getChannelServer().getOnlinePlayers().findByName(splitted[2]);
                if (victim == null) {
                    c.getPlayer().dropMessage(5, "Not found.");
                    return 0;
                }
                victim.setChair(0);
                victim.getClient().getSession().write(ResCUserLocal.UserSitResult(-1));
                victim.getMap().broadcastMessage(victim, ResCUserRemote.UserSetActivePortableChair(c.getPlayer().getId(), 0), false);
                victim.giveDebuff(dis, SkillWz.get().getMobSkillData(type, CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1)));
            } else {
                for (MapleCharacter victim : c.getPlayer().getMap().getCharacters()) {
                    victim.setChair(0);
                    victim.getClient().getSession().write(ResCUserLocal.UserSitResult(-1));
                    victim.getMap().broadcastMessage(victim, ResCUserRemote.UserSetActivePortableChair(c.getPlayer().getId(), 0), false);
                    victim.giveDebuff(dis, SkillWz.get().getMobSkillData(type, CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1)));
                }
            }
            return 1;
        }
    }
}
