/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.server.maps;

import java.awt.Point;
import odin.client.MapleCharacter;

import tacos.client.TacosClient;
import odin.client.SkillFactory;
import tacos.packet.ops.OpsFieldEffect;
import tacos.packet.ops.arg.ArgFieldEffect;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCMobPool;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.wrapper.ResWrapper;
import odin.server.Randomizer;
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleMonster;
import tacos.debug.DebugLogger;
import tacos.script.TacosScriptNPC;
import tacos.script.TacosScriptQuest;

public class MapScriptMethods {

    private static final Point witchTowerPos = new Point(-60, 184);
    private static final String[] mulungEffects = {
        //"If you want to step on the path to failure, by all means to do so!"
        "意外と利口だな！　戦略的撤退と無謀に突っ込むのじゃ、ワケが違うからな！",
        "武陵道場に挑むなんて…無謀なのか、それだけの自信があるってことか…。",
        "武陵道場に挑むなんてその勇気に感心するな！",
        "待ってたぞ！勇気が有り余ってるなら入ってこい！"
    };

    private static enum onFirstUserEnter {

        dojang_Eff,
        PinkBeen_before,
        onRewordMap,
        StageMsg_together,
        StageMsg_davy,
        party6weatherMsg,
        StageMsg_juliet,
        StageMsg_romio,
        moonrabbit_mapEnter,
        astaroth_summon,
        boss_Ravana,
        killing_BonusSetting,
        killing_MapSetting,
        metro_firstSetting,
        balog_bonusSetting,
        balog_summon,
        easy_balog_summon,
        Sky_TrapFEnter,
        shammos_Fenter,
        PRaid_D_Fenter,
        PRaid_B_Fenter,
        NULL;

        private static onFirstUserEnter fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    };

    private static enum onUserEnter {

        babyPigMap,
        crash_Dragon,
        evanleaveD,
        getDragonEgg,
        meetWithDragon,
        go1010100,
        go1010200,
        go1010300,
        go1010400,
        evanPromotion,
        PromiseDragon,
        evanTogether,
        incubation_dragon,
        TD_MC_Openning,
        TD_MC_gasi,
        TD_MC_title,
        cygnusJobTutorial,
        cygnusTest,
        startEreb,
        dojang_Msg,
        dojang_1st,
        reundodraco,
        undomorphdarco,
        explorationPoint,
        goAdventure,
        go10000,
        go20000,
        go30000,
        go40000,
        go50000,
        go1000000,
        go1010000,
        go1020000,
        go2000000,
        goArcher,
        goPirate,
        goRogue,
        goMagician,
        goSwordman,
        goLith,
        iceCave,
        mirrorCave,
        aranDirection,
        rienArrow,
        rien,
        check_count,
        Massacre_first,
        Massacre_result,
        aranTutorAlone,
        evanAlone,
        dojang_QcheckSet,
        Sky_StageEnter,
        outCase,
        balog_buff,
        balog_dateSet,
        Sky_BossEnter,
        Sky_GateMapEnter,
        shammos_Enter,
        shammos_Result,
        shammos_Base,
        dollCave00,
        dollCave01,
        Sky_Quest,
        enterBlackfrog,
        onSDI,
        blackSDI,
        summonIceWall,
        metro_firstSetting,
        start_itemTake,
        PRaid_D_Enter,
        PRaid_B_Enter,
        PRaid_Revive,
        PRaid_W_Enter,
        PRaid_WinEnter,
        PRaid_FailEnter,
        NULL;

        private static onUserEnter fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    };

    public static void startScript_FirstUser(TacosClient client, String scriptName) {
        if (client.getPlayer() == null) {
            return;
        } //o_O
        switch (onFirstUserEnter.fromString(scriptName)) {
            case dojang_Eff: {
                int temp = (client.getPlayer().getMapId() - 925000000) / 100;
                int stage = (int) (temp - ((temp / 100) * 100));

                sendDojoClock(client, getTiming(stage) * 60);
                sendDojoStart(client, stage - getDojoStageDec(stage));
                break;
            }
            case PinkBeen_before: {
                handlePinkBeanStart(client);
                break;
            }
            case onRewordMap: {
                reloadWitchTower(client);
                break;
            }
            //5120018 = ludi(none), 5120019 = orbis(start_itemTake - onUser)
            case moonrabbit_mapEnter: {
                client.getPlayer().getMap().startMapEffect("Gather the Primrose Seeds around the moon and protect the Moon Bunny!", 5120016);
                break;
            }
            case StageMsg_together: {
                switch (client.getPlayer().getMapId()) {
                    case 103000800:
                        client.getPlayer().getMap().startMapEffect("Solve the question and gather the amount of passes!", 5120017);
                        break;
                    case 103000801:
                        client.getPlayer().getMap().startMapEffect("Get on the ropes and unveil the correct combination!", 5120017);
                        break;
                    case 103000802:
                        client.getPlayer().getMap().startMapEffect("Get on the platforms and unveil the correct combination!", 5120017);
                        break;
                    case 103000803:
                        client.getPlayer().getMap().startMapEffect("Get on the barrels and unveil the correct combination!", 5120017);
                        break;
                    case 103000804:
                        client.getPlayer().getMap().startMapEffect("Defeat King Slime and his minions!", 5120017);
                        break;
                }
                break;
            }
            case StageMsg_romio: {
                switch (client.getPlayer().getMapId()) {
                    case 926100000:
                        client.getPlayer().getMap().startMapEffect("Please find the hidden door by investigating the Lab!", 5120021);
                        break;
                    case 926100001:
                        client.getPlayer().getMap().startMapEffect("Find  your way through this darkness!", 5120021);
                        break;
                    case 926100100:
                        client.getPlayer().getMap().startMapEffect("Fill the beakers to power the energy!", 5120021);
                        break;
                    case 926100200:
                        client.getPlayer().getMap().startMapEffect("Get the files for the experiment through each door!", 5120021);
                        break;
                    case 926100203:
                        client.getPlayer().getMap().startMapEffect("Please defeat all the monsters!", 5120021);
                        break;
                    case 926100300:
                        client.getPlayer().getMap().startMapEffect("Find your way through the Lab!", 5120021);
                        break;
                    case 926100401:
                        client.getPlayer().getMap().startMapEffect("Please, protect my love!", 5120021);

                        break;
                }
                break;
            }
            case StageMsg_juliet: {
                switch (client.getPlayer().getMapId()) {
                    case 926110000:
                        client.getPlayer().getMap().startMapEffect("Please find the hidden door by investigating the Lab!", 5120022);
                        break;
                    case 926110001:
                        client.getPlayer().getMap().startMapEffect("Find  your way through this darkness!", 5120022);
                        break;
                    case 926110100:
                        client.getPlayer().getMap().startMapEffect("Fill the beakers to power the energy!", 5120022);
                        break;
                    case 926110200:
                        client.getPlayer().getMap().startMapEffect("Get the files for the experiment through each door!", 5120022);
                        break;
                    case 926110203:
                        client.getPlayer().getMap().startMapEffect("Please defeat all the monsters!", 5120022);
                        break;
                    case 926110300:
                        client.getPlayer().getMap().startMapEffect("Find your way through the Lab!", 5120022);
                        break;
                    case 926110401:
                        client.getPlayer().getMap().startMapEffect("Please, protect my love!", 5120022);
                        break;
                }
                break;
            }
            case party6weatherMsg: {
                switch (client.getPlayer().getMapId()) {
                    case 930000000:
                        client.getPlayer().getMap().startMapEffect("Step in the portal to be transformed.", 5120023);
                        break;
                    case 930000100:
                        client.getPlayer().getMap().startMapEffect("Defeat the poisoned monsters!", 5120023);
                        break;
                    case 930000200:
                        client.getPlayer().getMap().startMapEffect("Eliminate the spore that blocks the way by purifying the poison!", 5120023);
                        break;
                    case 930000300:
                        client.getPlayer().getMap().startMapEffect("Uh oh! The forest is too confusing! Find me, quick!", 5120023);
                        break;
                    case 930000400:
                        client.getPlayer().getMap().startMapEffect("Purify the monsters by getting Purification Marbles from me!", 5120023);
                        break;
                    case 930000500:
                        client.getPlayer().getMap().startMapEffect("Find the Purple Magic Stone!", 5120023);
                        break;
                    case 930000600:
                        client.getPlayer().getMap().startMapEffect("Place the Magic Stone on the altar!", 5120023);
                        break;
                }
                break;
            }
            case StageMsg_davy: {
                switch (client.getPlayer().getMapId()) {
                    case 925100000:
                        client.getPlayer().getMap().startMapEffect("Defeat the monsters outside of the ship to advance!", 5120020);
                        break;
                    case 925100100:
                        client.getPlayer().getMap().startMapEffect("We must prove ourselves! Get me Pirate Medals!", 5120020);
                        break;
                    case 925100200:
                        client.getPlayer().getMap().startMapEffect("Defeat the guards here to pass!", 5120020);
                        break;
                    case 925100300:
                        client.getPlayer().getMap().startMapEffect("Eliminate the guards here to pass!", 5120020);
                        break;
                    case 925100400:
                        client.getPlayer().getMap().startMapEffect("Lock the doors! Seal the root of the Ship's power!", 5120020);
                        break;
                    case 925100500:
                        client.getPlayer().getMap().startMapEffect("Destroy the Lord Pirate!", 5120020);
                        break;
                }
                break;
            }
            case astaroth_summon: {
                client.getPlayer().getMap().resetFully();
                client.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9400633), new Point(600, -26)); //rough estimate
                break;
            }
            case boss_Ravana: { //event handles this so nothing for now until i find out something to do with it
                client.getPlayer().getMap().broadcastMessage(ResWrapper.BroadCastMsgEvent("Ravana has appeared!"));
                break;
            }
            case killing_BonusSetting: { //spawns monsters according to mapid
                //910320010-910320029 = Train 999 bubblings.
                //926010010-926010029 = 30 Yetis
                //926010030-926010049 = 35 Yetis
                //926010050-926010069 = 40 Yetis
                //926010070-926010089 - 50 Yetis (specialized? immortality)
                //TODO also find positions to spawn these at
                client.getPlayer().getMap().resetFully();
                client.getSession().write(ResWrapper.showEffect("killing/bonus/bonus"));
                client.getSession().write(ResWrapper.showEffect("killing/bonus/stage"));
                Point pos1 = null, pos2 = null, pos3 = null;
                int spawnPer = 0;
                int mobId = 0;
                //9700019, 9700029
                //9700021 = one thats invincible
                if (client.getPlayer().getMapId() >= 910320010 && client.getPlayer().getMapId() <= 910320029) {
                    pos1 = new Point(121, 218);
                    pos2 = new Point(396, 43);
                    pos3 = new Point(-63, 43);
                    mobId = 9700020;
                    spawnPer = 10;
                } else if (client.getPlayer().getMapId() >= 926010010 && client.getPlayer().getMapId() <= 926010029) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 10;
                } else if (client.getPlayer().getMapId() >= 926010030 && client.getPlayer().getMapId() <= 926010049) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 15;
                } else if (client.getPlayer().getMapId() >= 926010050 && client.getPlayer().getMapId() <= 926010069) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 20;
                } else if (client.getPlayer().getMapId() >= 926010070 && client.getPlayer().getMapId() <= 926010089) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700029;
                    spawnPer = 20;
                } else {
                    break;
                }
                for (int i = 0; i < spawnPer; i++) {
                    client.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos1));
                    client.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos2));
                    client.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos3));
                }
                client.getPlayer().startMapTimeLimitTask(120, client.getPlayer().getMap().getReturnMap());
                break;
            }
            case shammos_Fenter: {
                if (client.getPlayer().getMapId() >= 921120100 && client.getPlayer().getMapId() < 921120500) {
                    final MapleMonster shammos = MapleLifeFactory.getMonster(9300275);
                    client.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(client.getPlayer().getMap().getPortal(0).getPosition()), 12);
                    shammos.switchController(client.getPlayer(), false);
                    client.getSession().write(ResCMobPool.MobRequestResultEscortInfo(shammos, client.getPlayer().getMap()));

                }
                break;
            }
            case PRaid_D_Fenter: {
                switch (client.getPlayer().getMapId() % 10) {
                    case 0:
                        client.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120033);
                        break;
                    case 1:
                        client.getPlayer().getMap().startMapEffect("Break the boxes and eliminate the monsters!", 5120033);
                        break;
                    case 2:
                        client.getPlayer().getMap().startMapEffect("Eliminate the Officer!", 5120033);
                        break;
                    case 3:
                        client.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120033);
                        break;
                    case 4:
                        client.getPlayer().getMap().startMapEffect("Find the way to the other side!", 5120033);
                        break;
                }
                break;
            }
            case PRaid_B_Fenter: {
                client.getPlayer().getMap().startMapEffect("Defeat the Ghost Ship Captain!", 5120033);
                break;
            }
            case balog_summon:
            case easy_balog_summon: { //we dont want to reset
                break;
            }
            case metro_firstSetting:
            case killing_MapSetting:
            case Sky_TrapFEnter:
            case balog_bonusSetting: { //not needed
                client.getPlayer().getMap().resetFully();
                break;
            }
            default: {
                DebugLogger.ErrorLog("onFirstUserEnter : " + scriptName);
                break;
            }
        }
    }

    public static void startScript_User(TacosClient client, String scriptName) {
        MapleCharacter chr = client.getPlayer();
        if (chr == null) {
            return;
        } //o_O
        String data = "";
        switch (onUserEnter.fromString(scriptName)) {
            case cygnusTest:
            case cygnusJobTutorial: {
                showIntro(client, "Effect/Direction.img/cygnusJobTutorial/Scene" + (client.getPlayer().getMapId() - 913040100));
                break;
            }
            case shammos_Enter: { //nothing to go on inside the map
                client.getSession().write(ResWrapper.sendPyramidEnergy("shammos_LastStage", String.valueOf((client.getPlayer().getMapId() % 1000) / 100)));
                if (client.getPlayer().getMapId() == 921120500) {
                    TacosScriptNPC.getInstance().dispose(client);
                    TacosScriptQuest.getInstance().dispose(client);
                    TacosScriptNPC.getInstance().start(client, 2022006);
                }
                break;
            }
            case start_itemTake: {
                break;
            }
            case PRaid_W_Enter: {
                client.getSession().write(ResWrapper.sendPyramidEnergy("PRaid_expPenalty", "0"));
                client.getSession().write(ResWrapper.sendPyramidEnergy("PRaid_ElapssedTimeAtField", "0"));
                client.getSession().write(ResWrapper.sendPyramidEnergy("PRaid_Point", "-1"));
                client.getSession().write(ResWrapper.sendPyramidEnergy("PRaid_Bonus", "-1"));
                client.getSession().write(ResWrapper.sendPyramidEnergy("PRaid_Total", "-1"));
                client.getSession().write(ResWrapper.sendPyramidEnergy("PRaid_Team", ""));
                client.getSession().write(ResWrapper.sendPyramidEnergy("PRaid_IsRevive", "0"));
                client.getPlayer().writePoint("PRaid_Point", "-1");
                client.getPlayer().writeStatus("Red_Stage", "1");
                client.getPlayer().writeStatus("Blue_Stage", "1");
                client.getPlayer().writeStatus("redTeamDamage", "0");
                client.getPlayer().writeStatus("blueTeamDamage", "0");
                break;
            }
            case PRaid_D_Enter:
            case PRaid_B_Enter:
            case PRaid_WinEnter: //handled by event
            case PRaid_FailEnter: //also
            case PRaid_Revive: //likely to subtract points or remove a life, but idc rly
            case metro_firstSetting:
            case blackSDI:
            case summonIceWall:
            case onSDI:
            case enterBlackfrog:
            case Sky_Quest: //forest that disappeared 240030102
            case dollCave00:
            case dollCave01:
            case shammos_Base:
            case shammos_Result:
            case Sky_BossEnter:
            case Sky_GateMapEnter:
            case balog_dateSet:
            case balog_buff:
            case outCase:
            case Sky_StageEnter:
            case dojang_QcheckSet:
            case evanTogether:
            case aranTutorAlone:
            case evanAlone: { //no idea
                chr.updateStat();
                break;
            }
            case startEreb:
            case mirrorCave:
            case babyPigMap:
            case evanleaveD: {
                client.getSession().write(ResCUserLocal.SetStandAloneMode(false));
                client.getSession().write(ResCUserLocal.SetDirectionMode(false));
                chr.updateStat();
                break;
            }
            case dojang_Msg: {
                client.getPlayer().getMap().startMapEffect(mulungEffects[Randomizer.nextInt(mulungEffects.length)], 5120024);
                break;
            }
            case dojang_1st: {
                client.getPlayer().writeMulungEnergy();
                break;
            }
            case undomorphdarco:
            case reundodraco: {
                break;
            }
            case goAdventure: {
                // BUG in MSEA v.91, so let's skip this part.
                showIntro(client, "Effect/Direction3.img/goAdventure/Scene" + (client.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case crash_Dragon:
                showIntro(client, "Effect/Direction4.img/crash/Scene" + (client.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case getDragonEgg:
                showIntro(client, "Effect/Direction4.img/getDragonEgg/Scene" + (client.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case meetWithDragon:
                showIntro(client, "Effect/Direction4.img/meetWithDragon/Scene" + (client.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case PromiseDragon:
                showIntro(client, "Effect/Direction4.img/PromiseDragon/Scene" + (client.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case evanPromotion:
                switch (client.getPlayer().getMapId()) {
                    case 900090000:
                        data = "Effect/Direction4.img/promotion/Scene0" + (client.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 900090001:
                        data = "Effect/Direction4.img/promotion/Scene1";
                        break;
                    case 900090002:
                        data = "Effect/Direction4.img/promotion/Scene2" + (client.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 900090003:
                        data = "Effect/Direction4.img/promotion/Scene3";
                        break;
                    case 900090004:
                        client.getSession().write(ResCUserLocal.SetStandAloneMode(false));
                        client.getSession().write(ResCUserLocal.SetDirectionMode(false));
                        chr.updateStat();
                        final MapleMap mapto = chr.findMap(900010000);
                        client.getPlayer().changeMap(mapto, mapto.getPortal(0));
                        return;
                }
                showIntro(client, data);
                break;
            case TD_MC_title: {
                client.getSession().write(ResCUserLocal.SetStandAloneMode(false));
                client.getSession().write(ResCUserLocal.SetDirectionMode(false));
                chr.updateStat();
                client.getSession().write(ResCField.FieldEffect(new ArgFieldEffect(OpsFieldEffect.FieldEffect_Screen, "temaD/enter/mushCatle")));
                break;
            }
            case explorationPoint: {
                break;
            }
            case go10000:
            case go1020000:
                client.getSession().write(ResCUserLocal.SetStandAloneMode(false));
                client.getSession().write(ResCUserLocal.SetDirectionMode(false));
                chr.updateStat();
            case go20000:
            case go30000:
            case go40000:
            case go50000:
            case go1000000:
            case go2000000:
            case go1010000:
            case go1010100:
            case go1010200:
            case go1010300:
            case go1010400: {
                client.getSession().write(ResWrapper.MapNameDisplay(client.getPlayer().getMapId()));
                break;
            }
            case goArcher: {
                showIntro(client, "Effect/Direction3.img/archer/Scene" + (client.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goPirate: {
                showIntro(client, "Effect/Direction3.img/pirate/Scene" + (client.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goRogue: {
                showIntro(client, "Effect/Direction3.img/rogue/Scene" + (client.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goMagician: {
                showIntro(client, "Effect/Direction3.img/magician/Scene" + (client.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goSwordman: {
                showIntro(client, "Effect/Direction3.img/swordman/Scene" + (client.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goLith: {
                showIntro(client, "Effect/Direction3.img/goLith/Scene" + (client.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case TD_MC_Openning: {
                showIntro(client, "Effect/Direction2.img/open");
                break;
            }
            case TD_MC_gasi: {
                showIntro(client, "Effect/Direction2.img/gasi");
                break;
            }
            case aranDirection: {
                switch (client.getPlayer().getMapId()) {
                    case 914090010:
                        data = "Effect/Direction1.img/aranTutorial/Scene0";
                        break;
                    case 914090011:
                        data = "Effect/Direction1.img/aranTutorial/Scene1" + (client.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914090012:
                        data = "Effect/Direction1.img/aranTutorial/Scene2" + (client.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914090013:
                        data = "Effect/Direction1.img/aranTutorial/Scene3";
                        break;
                    case 914090100:
                        data = "Effect/Direction1.img/aranTutorial/HandedPoleArm" + (client.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914090200:
                        data = "Effect/Direction1.img/aranTutorial/Maha";
                        break;
                }
                showIntro(client, data);
                break;
            }
            case iceCave: {
                client.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000014), (byte) -1, (byte) 0);
                client.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000015), (byte) -1, (byte) 0);
                client.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000016), (byte) -1, (byte) 0);
                client.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000017), (byte) -1, (byte) 0);
                client.getPlayer().changeSkillLevel(SkillFactory.getSkill(20000018), (byte) -1, (byte) 0);
                client.getSession().write(ResCUserLocal.ShowWZEffect("Effect/Direction1.img/aranTutorial/ClickLirin"));
                client.getSession().write(ResCUserLocal.SetStandAloneMode(false));
                client.getSession().write(ResCUserLocal.SetDirectionMode(false));
                chr.updateStat();
                break;
            }
            case rienArrow: {
                if (client.getPlayer().getInfoQuest(21019).equals("miss=o;helper=clear")) {
                    client.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;helper=clear");
                    client.getSession().write(ResCUserLocal.AranTutInstructionalBalloon("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3"));
                }
                break;
            }
            case rien: {
                if (client.getPlayer().getQuestStatus(21101) == 2 && client.getPlayer().getInfoQuest(21019).equals("miss=o;arr=o;helper=clear")) {
                    client.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;ck=1;helper=clear");
                }
                client.getSession().write(ResCUserLocal.SetStandAloneMode(false));
                client.getSession().write(ResCUserLocal.SetDirectionMode(false));
                break;
            }
            case check_count: {
                if (client.getPlayer().getMapId() == 950101010 && (!client.getPlayer().haveItem(4001433, 20) || client.getPlayer().getLevel() < 50)) { //ravana Map
                    final MapleMap mapp = chr.findMap(950101100); //exit Map
                    client.getPlayer().changeMap(mapp, mapp.getPortal(0));
                }
                break;
            }
            case Massacre_first: { //sends a whole bunch of shit.
                if (client.getPlayer().getPyramidSubway() == null) {
                    client.getPlayer().setPyramidSubway(new Event_PyramidSubway(client.getPlayer()));
                }
                break;
            }
            case Massacre_result: { //clear, give exp, etc.
                //if (c.getPlayer().getPyramidSubway() == null) {
                client.getSession().write(ResWrapper.showEffect("killing/fail"));
                //} else {
                //	c.getSession().write(MaplePacketCreator.showEffect("killing/clear"));
                //}
                //left blank because pyramidsubway handles this.
                break;
            }
            default: {
                DebugLogger.ErrorLog("onUserEnter : " + scriptName);
                break;
            }
        }
    }

    private static final int getTiming(int ids) {
        if (ids <= 5) {
            return 5;
        } else if (ids >= 7 && ids <= 11) {
            return 6;
        } else if (ids >= 13 && ids <= 17) {
            return 7;
        } else if (ids >= 19 && ids <= 23) {
            return 8;
        } else if (ids >= 25 && ids <= 29) {
            return 9;
        } else if (ids >= 31 && ids <= 35) {
            return 10;
        } else if (ids >= 37 && ids <= 38) {
            return 15;
        }
        return 0;
    }

    private static final int getDojoStageDec(int ids) {
        if (ids <= 5) {
            return 0;
        } else if (ids >= 7 && ids <= 11) {
            return 1;
        } else if (ids >= 13 && ids <= 17) {
            return 2;
        } else if (ids >= 19 && ids <= 23) {
            return 3;
        } else if (ids >= 25 && ids <= 29) {
            return 4;
        } else if (ids >= 31 && ids <= 35) {
            return 5;
        } else if (ids >= 37 && ids <= 38) {
            return 6;
        }
        return 0;
    }

    private static void showIntro(final TacosClient c, final String data) {
        c.getSession().write(ResCUserLocal.SetStandAloneMode(true));
        c.getSession().write(ResCUserLocal.SetDirectionMode(true));
        c.getSession().write(ResCUserLocal.ShowWZEffect(data));
    }

    private static void sendDojoClock(TacosClient c, int time) {
        c.getSession().write(ResCField.Clock(time));
    }

    private static void sendDojoStart(TacosClient c, int stage) {
        c.getSession().write(ResCField.FieldEffect(new ArgFieldEffect(OpsFieldEffect.FieldEffect_Sound, "Dojang/start")));
        c.getSession().write(ResCField.FieldEffect(new ArgFieldEffect(OpsFieldEffect.FieldEffect_Screen, "dojang/start/stage")));
        c.getSession().write(ResCField.FieldEffect(new ArgFieldEffect(OpsFieldEffect.FieldEffect_Screen, "dojang/start/number/" + stage)));
        c.SendPacket(ResCField.FieldEffect(new ArgFieldEffect(OpsFieldEffect.FieldEffect_Tremble, 0, 1)));
    }

    private static void handlePinkBeanStart(TacosClient c) {
        final MapleMap map = c.getPlayer().getMap();
        map.resetFully();

        if (!map.containsNPC(2141000)) {
            map.spawnNpc(2141000, new Point(-190, -42));
        }
    }

    private static void reloadWitchTower(TacosClient c) {
        final MapleMap map = c.getPlayer().getMap();
        map.killAllMonsters(false);

        final int level = c.getPlayer().getLevel();
        int mob;
        if (level <= 10) {
            mob = 9300367;
        } else if (level <= 20) {
            mob = 9300368;
        } else if (level <= 30) {
            mob = 9300369;
        } else if (level <= 40) {
            mob = 9300370;
        } else if (level <= 50) {
            mob = 9300371;
        } else if (level <= 60) {
            mob = 9300372;
        } else if (level <= 70) {
            mob = 9300373;
        } else if (level <= 80) {
            mob = 9300374;
        } else if (level <= 90) {
            mob = 9300375;
        } else if (level <= 100) {
            mob = 9300376;
        } else {
            mob = 9300377;
        }
        map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mob), witchTowerPos);
    }
}
