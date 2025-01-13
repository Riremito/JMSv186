package client.messages.commands;

import client.MapleCharacter;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleStat;
import constants.GameConstants;
import java.util.List;
import packet.response.wrapper.ResWrapper;
import scripting.NPCScriptManager;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;

import server.life.MapleMonsterInformationProvider;
import server.life.MonsterDropEntry;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;

/**
 *
 * @author Emilyx3
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class STR extends DistributeStatCommands {

        public STR() {
            stat = MapleStat.STR;
        }
    }

    public static class DEX extends DistributeStatCommands {

        public DEX() {
            stat = MapleStat.DEX;
        }
    }

    public static class INT extends DistributeStatCommands {

        public INT() {
            stat = MapleStat.INT;
        }
    }

    public static class LUK extends DistributeStatCommands {

        public LUK() {
            stat = MapleStat.LUK;
        }
    }

    public abstract static class DistributeStatCommands extends CommandExecute {

        protected MapleStat stat = null;
        private static int statLim = 999;

        private void setStat(MapleCharacter player, int amount) {
            switch (stat) {
                case STR:
                    player.getStat().setStr((short) amount);
                    player.updateSingleStat(MapleStat.STR, player.getStat().getStr());
                    break;
                case DEX:
                    player.getStat().setDex((short) amount);
                    player.updateSingleStat(MapleStat.DEX, player.getStat().getDex());
                    break;
                case INT:
                    player.getStat().setInt((short) amount);
                    player.updateSingleStat(MapleStat.INT, player.getStat().getInt());
                    break;
                case LUK:
                    player.getStat().setLuk((short) amount);
                    player.updateSingleStat(MapleStat.LUK, player.getStat().getLuk());
                    break;
            }
        }

        private int getStat(MapleCharacter player) {
            switch (stat) {
                case STR:
                    return player.getStat().getStr();
                case DEX:
                    return player.getStat().getDex();
                case INT:
                    return player.getStat().getInt();
                case LUK:
                    return player.getStat().getLuk();
                default:
                    throw new RuntimeException(); //Will never happen.
            }
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "Invalid number entered.");
                return 0;
            }
            int change = 0;
            try {
                change = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(5, "Invalid number entered.");
                return 0;
            }
            if (change <= 0) {
                c.getPlayer().dropMessage(5, "You must enter a number greater than 0.");
                return 0;
            }
            if (c.getPlayer().getRemainingAp() < change) {
                c.getPlayer().dropMessage(5, "You don't have enough AP for that.");
                return 0;
            }
            if (getStat(c.getPlayer()) + change > statLim) {
                c.getPlayer().dropMessage(5, "The stat limit is " + statLim + ".");
                return 0;
            }
            setStat(c.getPlayer(), getStat(c.getPlayer()) + change);
            c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - change));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());

            return 1;
        }
    }

    public abstract static class OpenNPCCommand extends CommandExecute {

        protected int npc = -1;
        private static int[] npcs = { //Ish yur job to make sure these are in order and correct ;(
            9100205,
            //            9270035,
            9010017,
            9000000,};

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (npc != 1 && c.getPlayer().getMapId() != 910000000) { //drpcash can use anywhere
                for (int i : GameConstants.blockedMaps) {
                    if (c.getPlayer().getMapId() == i) {
                        c.getPlayer().dropMessage(5, "You may not use this command here.");
                        return 0;
                    }
                }
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "You must be over level 10 to use this command.");
                    return 0;
                }
                if (c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000/* || FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())*/) {
                    c.getPlayer().dropMessage(5, "You may not use this command here.");
                    return 0;
                }
                if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                    c.getPlayer().dropMessage(5, "You may not use this command here.");
                    return 0;
                }
            }
            NPCScriptManager.getInstance().start(c, npcs[npc]);
            return 1;
        }
    }

    public static class Joyce extends OpenNPCCommand {

        public Joyce() {
            npc = 0;
        }
    }

    public static class EA extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(ResWrapper.enableActions());
            return 1;
        }
    }

    public static class CheckDrop extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 4) { //start end
                c.getPlayer().dropMessage(5, "Use @checkdrop [monsterID] [start number] [end number] where start and end are the number of the drop");
                c.getPlayer().dropMessage(5, "You can get the monsterID through @mobdebug command.");
            } else {
                int start = 1, end = 10;
                try {
                    start = Integer.parseInt(splitted[2]);
                    end = Integer.parseInt(splitted[3]);
                } catch (NumberFormatException e) {
                    c.getPlayer().dropMessage(5, "You didn't specify start and end number correctly, the default values of 1 and 10 will be used.");
                }
                if (end < start || end - start > 10) {
                    c.getPlayer().dropMessage(5, "End number must be greater, and end number must be within a range of 10 from the start number.");
                } else {
                    final MapleMonster job = MapleLifeFactory.getMonster(Integer.parseInt(splitted[1]));
                    if (job == null) {
                        c.getPlayer().dropMessage(5, "Please use @mobdebug to check monsterID properly.");
                    } else {
                        final List<MonsterDropEntry> ranks = MapleMonsterInformationProvider.getInstance().retrieveDrop(job.getId());
                        if (ranks == null || ranks.size() <= 0) {
                            c.getPlayer().dropMessage(5, "No drops was returned.");
                        } else {
                            final int originalEnd = end;
                            int num = 0;
                            MonsterDropEntry de;
                            String name;
                            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                            for (int i = 0; i < ranks.size(); i++) {
                                if (i >= (start - 1) && i < end) {
                                    de = ranks.get(i);
                                    name = ii.getName(de.itemId);
                                    if (de.chance > 0 && name != null && name.length() > 0 && (de.questid <= 0 || (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0))) {
                                        if (num == 0) {
                                            c.getPlayer().dropMessage(6, "Drops for " + job.getStats().getName() + " - from " + start + " to " + originalEnd);
                                            c.getPlayer().dropMessage(6, "--------------------------------------");
                                        }
                                        c.getPlayer().dropMessage(6, ii.getName(de.itemId) + " (" + de.itemId + "), anywhere from " + de.Minimum + " to " + de.Maximum + " quantity. " + (Integer.valueOf(de.chance == 999999 ? 1000000 : de.chance).doubleValue() / 10000.0) + "% chance. " + (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0 ? ("Requires quest " + MapleQuest.getInstance(de.questid).getName() + " to be started.") : ""));
                                        num++;
                                    } else {
                                        end++; //go more. 10 drops plz
                                    }
                                }
                            }
                            if (num == 0) {
                                c.getPlayer().dropMessage(5, "No drops was returned.");
                            }
                        }
                    }
                }
            }
            return 1;
        }
    }

    public static class Help extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "List of commands :");
            c.getPlayer().dropMessage(5, "@str, @dex, @int, @luk <amount to add>");
            c.getPlayer().dropMessage(5, "@mobdebug < Debug information on the closest monster >");
            c.getPlayer().dropMessage(5, "@check < Displays amount of points, voting points, A-Cash >");
            c.getPlayer().dropMessage(5, "@mumu < Opens the traveling merchant >");
            c.getPlayer().dropMessage(5, "@fm < Warp to FM >");
            /*c.getPlayer().dropMessage(5, "@changesecondpass - Change second password, @changesecondpass <current Password> <new password> <Confirm new password> ");*/
            c.getPlayer().dropMessage(5, "@joyce < Universal Town Warp / Event NPC>");
            c.getPlayer().dropMessage(5, "@dropcash < Universal Cash Item Dropper >");
            c.getPlayer().dropMessage(5, "@tsmega < Toggle super megaphone on/off >");
            c.getPlayer().dropMessage(5, "@ea < If you are unable to attack or talk to NPC >");
            c.getPlayer().dropMessage(5, "@clearslot < Cleanup that trash in your inventory >");
            c.getPlayer().dropMessage(5, "@ranking < Use @ranking for more details >");
            c.getPlayer().dropMessage(5, "@checkdrop < Use @checkdrop for more details >");
            c.getPlayer().dropMessage(5, "If you want to buy A-Cash, talk to the NPC in FM!");
            return 1;
        }
    }
}
