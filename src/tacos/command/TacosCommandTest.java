/*
 * Copyright (C) 2026 Riremito
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */
package tacos.command;

import java.util.List;
import odin.client.MapleCharacter;
import odin.server.Randomizer;
import odin.server.life.MapleMonster;
import odin.server.life.MobSkill;
import odin.server.maps.MapleMap;
import tacos.client.TacosForcedStat;
import tacos.client.TacosMapleGift;
import tacos.packet.ops.OpsFieldEffect;
import tacos.packet.ops.OpsMobSkill;
import tacos.packet.ops.OpsSecondaryStat;
import tacos.packet.ops.OpsUI;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import tacos.packet.response.ResCMobPool;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_FieldEffect;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class TacosCommandTest {

    public static boolean executeCommand(TacosCommander dcmd, MapleCharacter chr) {
        MapleMap map = chr.getMap();

        switch (dcmd.get(0)) {
            // unused packet test.
            case "/koc164" -> {
                chr.SendPacket(ResCWvsContext.KOC_UI_Open());
                return true;
            }
            case "/maplegift" -> {
                TacosMapleGift maple_gift = chr.getMapleGift();
                maple_gift.clear();
                TacosMapleGift.MapleGiftData maple_gift_data = new TacosMapleGift.MapleGiftData();
                maple_gift_data.unk1 = 1;
                maple_gift_data.item_id = 1452045;
                maple_gift_data.name = "TACOS";
                maple_gift_data.id = 777;
                maple_gift.add(maple_gift_data);

                chr.SendPacket(ResCWvsContext.MapleGift(maple_gift_data));
                return true;
            }
            case "/pollquestion" -> {
                String questions[] = {
                    "Question1",
                    "Question2"
                };
                String answers[][] = {
                    {"123", "aiueo", "asdf"},
                    {"456", "qwert"}
                };

                chr.SendPacket(ResCUserLocal.PollQuestion(questions, answers));
                return true;
            }
            case "/miro" -> {
                chr.SendPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Screen, PB_FieldEffect.builder().wz_path("miro/frame").build()));
                chr.SendPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Screen, PB_FieldEffect.builder().wz_path("miro/RR1/" + Randomizer.nextInt(4)).build()));
                chr.SendPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Screen, PB_FieldEffect.builder().wz_path("miro/RR2/" + Randomizer.nextInt(4)).build()));
                chr.SendPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Screen, PB_FieldEffect.builder().wz_path("miro/RR3/" + Randomizer.nextInt(5)).build()));
                chr.SendPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Sound, PB_FieldEffect.builder().wz_path("quest2288/" + Randomizer.nextInt(9)).build())); // test bgm
                return true;
            }
            // packet test.
            case "/msgtest" -> {
                chr.DebugMsg("BLUE.");
                chr.DebugMsg2("PINK.");
                chr.DebugMsg3("YELLOW.");
                return true;
            }
            case "/repairui" -> {
                chr.SendPacket(ResCUserLocal.UserOpenUIWithOption(OpsUI.UI_REPAIRDURABILITY, 1012003));
                return true;
            }
            case "/omok" -> {
                chr.SendPacket(ResCMiniRoomBaseDlg.EnterResultStaticOmokTest(chr));
                return true;
            }
            case "/fstest" -> {
                TacosForcedStat fs = chr.getForcedStat();
                int index = 1;
                fs.setSTR(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setDEX(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setINT(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setLUK(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setPAD(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setACC(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setEVA(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setSpeed(dcmd.check(index) ? dcmd.getInt(index++) : 0);
                fs.setJump(dcmd.check(index) ? dcmd.getInt(index++) : 0);

                chr.SendPacket(ResCWvsContext.ForcedStatSet(chr));
                chr.DebugMsg("fstest : STR DEX INT LUK PAD ACC EVA Speed Jump");
                return true;
            }
            case "/mobtest" -> {
                List<MapleMonster> monsters = map.getAllMonsters();
                MapleMonster monster = null;
                if (map.getAllMonsters().isEmpty()) {
                    monster = WzXML.MOB.findMonster(130101);
                    map.spawnMonsterOnGroundBelow(monster, chr.getPosition());
                } else {
                    monster = monsters.get(0);
                }

                int index = dcmd.check(1) ? dcmd.getInt(1) : 1;
                chr.DebugMsg("mobtest : " + index);
                mobTest(chr, monster, index);
                return true;
            }
            case "/mobskilltest" -> {
                if (!dcmd.check(1)) {
                    return true;
                }

                int mob_skill_id = dcmd.getInt(1);
                int mob_skill_level = 1;
                OpsMobSkill oms = OpsMobSkill.find(mob_skill_id);
                if (oms == OpsMobSkill.UNKNOWN) {
                    chr.DebugMsg("mobskilltest : invalid or not supported id.");
                    return true;
                }
                MobSkill ms = WzXML.SKILL.getMobSkillData(mob_skill_id, mob_skill_level);
                if (ms == null) {
                    chr.DebugMsg("mobskilltest : not found.");
                    return true;
                }

                int cts = oms.getDisease().get();
                int buff_id = mob_skill_id | (mob_skill_level << 16);
                int buff_effect = Math.max(ms.getX(), 1);
                int buff_time = dcmd.check(2) ? dcmd.getInt(2) : 5000;
                if (chr.getBuff().updateTest(cts, buff_id, buff_effect, buff_time)) {
                    chr.SendPacket(ResCWvsContext.TemporaryStatSet(chr, buff_id));
                }

                chr.DebugMsg("mobskilltest : " + mob_skill_id);
                return true;
            }
            case "/bufftest" -> {
                if (!dcmd.check(1)) {
                    return true;
                }
                int cts = dcmd.getInt(1);
                int buff_id = -4000000;
                int buff_effect = dcmd.check(2) ? dcmd.getInt(2) : 1;
                int buff_time = dcmd.check(3) ? dcmd.getInt(3) : 5000;

                if (chr.getBuff().updateTest(dcmd.getInt(1), buff_id, buff_effect, buff_time)) {
                    chr.SendPacket(ResCWvsContext.TemporaryStatSet(chr, buff_id));
                }
                chr.DebugMsg("bufftest : " + OpsSecondaryStat.find(cts) + "(" + cts + "), buff_effect = " + buff_effect + ", buff_time =" + buff_time);
                return true;
            }
            case "/buff2test" -> {
                if (!dcmd.check(3)) {
                    return true;
                }

                int cts = dcmd.getInt(1);
                if (!OpsSecondaryStat.find(cts).isTwoState()) {
                    chr.DebugMsg("buff2test : not a two state buff.");
                    return true;
                }

                int buff_id = -4000000;
                int buff_effect = dcmd.getInt(2);
                int buff_effect_2 = dcmd.getInt(3);
                int buff_time = dcmd.check(4) ? dcmd.getInt(4) : 5000;

                if (chr.getBuff().updateTest(dcmd.getInt(1), buff_id, buff_effect, buff_time, buff_effect_2)) {
                    chr.SendPacket(ResCWvsContext.TemporaryStatSet(chr, buff_id));
                }
                chr.DebugMsg("buff2 : " + OpsSecondaryStat.find(cts) + "(" + cts + "), buff_effect = " + buff_effect + ", " + buff_effect_2);
                return true;
            }
            case "/ridingtest" -> {
                int buff_id = -4000000;
                int buff_effect = 1902000;
                int buff_effect_2 = 1004;
                int buff_time = dcmd.check(2) ? dcmd.getInt(2) : 5000;

                if (chr.getBuff().updateTest(OpsSecondaryStat.CTS_RideVehicle.get(), buff_id, buff_effect, buff_time, buff_effect_2)) {
                    chr.SendPacket(ResCWvsContext.TemporaryStatSet(chr, buff_id));
                }
                return true;
            }
            default -> {
            }
        }

        return false;
    }

    public static void mobTest(MapleCharacter chr, MapleMonster monster, int index) {
        switch (index) {
            case 1 -> {
                chr.SendPacket(ResCMobPool.MobAttackedByMob(monster, 1, 7777));
            }
            case 2 -> {
                chr.SendPacket(ResCMobPool.MobNextAttack(monster, 1));
            }
            case 3 -> {
                chr.SendPacket(ResCMobPool.MobEscortReturnBefore(monster));
            }
            case 4 -> {
                chr.SendPacket(ResCMobPool.MobEscortStopSay(monster, 5120035, "MobEscortStopSay"));
            }
            case 5 -> {
                chr.SendPacket(ResCMobPool.MobRequestResultEscortInfo(monster, chr.getMap()));
            }
            case 6 -> {
                chr.SendPacket(ResCMobPool.MobEscortStopEndPermmision(monster));
            }
            case 7 -> {
                chr.SendPacket(ResCMobPool.MobSkillDelay(monster));
            }
            case 8 -> {
                chr.SendPacket(ResCMobPool.MobChargeCount(monster, 1, 1));
            }
            case 9 -> {
                chr.SendPacket(ResCMobPool.MobSpeaking(monster, 0, 0));
            }
            case 10 -> {
                chr.SendPacket(ResCMobPool.MobEffectByItem(monster, 2270004, true));
            }
            case 11 -> {
                chr.SendPacket(ResCMobPool.MobCatchEffect(monster, true));
            }
            case 12 -> {
                chr.SendPacket(ResCMobPool.MobCrcKeyChanged(monster, 0xBEEF));
            }
            case 13 -> {
                chr.SendPacket(ResCMobPool.MobSpecialEffectBySkill(monster, chr, 3110001, 1000));
            }
            case 14 -> {
                chr.SendPacket(ResCMobPool.MobAffected(monster, 4341003, 1000));
            }
            case 15 -> {
                chr.SendPacket(ResCMobPool.MobSuspendReset(monster));
            }
            case 16 -> {
                chr.SendPacket(ResCMobPool.MobStatReset(monster));
            }
            case 17 -> {
                chr.SendPacket(ResCMobPool.MobStatSet(monster));
            }
            default -> {
            }
        }
    }
}
