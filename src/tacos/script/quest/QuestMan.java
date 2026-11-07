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
package tacos.script.quest;

import odin.server.quest.MapleQuest;
import tacos.client.TacosCharacter;

/**
 *
 * @author Riremito
 */
public class QuestMan {

    // quest_start : true = startscript (開始時), false = endscript (完了時)
    // check_complete : endscript の実行前に完了条件を確認する
    public static boolean start(TacosCharacter chr, int npc_id, int quest_id, boolean quest_start, boolean check_complete) {
        QuestScriptMan quest_script = create(quest_id);
        if (quest_script == null) {
            return false;
        }
        if (!quest_start && check_complete && !MapleQuest.getInstance(quest_id).canComplete(chr, null)) {
            // 完了条件を満たしていないため何もしない
            return true;
        }
        quest_script.setNpcId(npc_id);
        quest_script.setQuest(quest_id, quest_start);
        chr.DebugMsg("QuestMan : " + quest_id + (quest_start ? " (start)" : " (end)"));
        return quest_script.start(chr);
    }

    private static QuestScriptMan create(int quest_id) {
        switch (quest_id) {
            case 4615, 4616, 4617, 4618, 9682, 9683, 9684, 9685 -> {
                // 4615 - 4618, カサンドラのスペシャル占い
                // 9682 - 9685, 毎日毎日イベント (3月 5日, 12日, 19日, 26日)
                return new Quest_Event_ScrollChange();
            }
            case 8185, 8189 -> {
                // 8185, ペットの進化　2
                // 8189, ペットの再進化
                return new Quest_Common_PetEvolution();
            }
            case 9680, 9810, 9811, 9812, 9813 -> {
                // 9680, 驚愕の経験値ガッツリイベント
                // 9810 - 9813, 毎日毎日イベント (4月 27日, 4月 29日, 5月 4日, 5月 6日)
                return new Quest_Event_SelectedMob();
            }
            case 9800 -> {
                // 9800, 4周年記念のレベルアップイベント
                return new Quest_Event_LevelUpReward();
            }
            case 9809 -> {
                // 9809, カサンドラの占い
                return new Quest_Event_CassandraFortune();
            }
            case 1021 -> {
                // 1021, ローザーとリンゴ
                return new Quest_MapleIsland_RogersApple();
            }
            case 20015 -> {
                // 20015, 幼い女王の挨拶
                return new Quest_Cygnus_EncounterWithTheYoungQueen();
            }
            case 20016 -> {
                // 20016, 暗黒の魔法使い
                return new Quest_Cygnus_DoYouKnowTheBlackMagician();
            }
            case 20104, 20105 -> {
                // 20104, ナイトウォーカーの道
                // 20105, ストライカーの道
                return new Quest_Cygnus_FirstJobAdvancement();
            }
            case 20200 -> {
                // 20200, 見習い騎士の終わり
                return new Quest_Cygnus_SecondJobAdvancement();
            }
            case 20201, 20202, 20203, 20204, 20205 -> {
                // 20201, ソウルマスターの騎士クラス試験
                // 20202, フレイムウィザードの騎士クラス試験
                // 20203, ウィンドシューターの騎士クラス試験
                // 20204, ナイトウォーカーの騎士クラス試験
                // 20205, ストライカーの騎士クラス試験
                return new Quest_Cygnus_KnightClassTest();
            }
            case 20311, 20312, 20313, 20314, 20315 -> {
                // 20311, 神獣の涙(ソウルマスター)
                // 20312, 神獣の涙(フレイムウィザード)
                // 20313, 神獣の涙(ウィンドシューター)
                // 20314, 神獣の涙(ナイトウォーカー)
                // 20315, 神獣の涙(ストライカー)
                return new Quest_Cygnus_TearOfTheDivineBird();
            }
            case 20400, 20401, 20405, 20406 -> {
                // 20400, 騎士の軌跡を追って
                // 20401, ゾンビ倒し
                // 20405, 暗黒の魔女の洞窟
                // 20406, 消えた騎士
                return new Quest_Cygnus_KnightsTrail();
            }
            case 20408 -> {
                // 20408, 女王の騎士団長
                return new Quest_Cygnus_CaptainKnight();
            }
            case 20520 -> {
                // 20520, 騎士の品位
                return new Quest_Cygnus_KnightsDignity();
            }
            case 20600, 20610 -> {
                // 20600, 終わらない修行
                // 20610, まだ終わらない修行
                return new Quest_Cygnus_EndlessTraining();
            }
            case 20700 -> {
                // 20700, このまま出られる？
                return new Quest_Cygnus_AreYouSureYouCanLeave();
            }
            case 3529, 3539 -> {
                // 3529, 失われた思い出見付け
                // 3539, 失った思い出を探して
                return new Quest_Common_LostMemory();
            }
            case 29503 -> {
                // 29503, 寄付王
                return new Quest_Medal_KingOfDonation();
            }
            case 29900, 29901, 29902, 29903 -> {
                // 29900, 初心者冒険家
                // 29901, ジュニア冒険家
                // 29902, ベテラン冒険家
                // 29903, マスター冒険家
                return new Quest_Medal_Explorer();
            }
            case 29904, 29905, 29906, 29907, 29908, 29909 -> {
                // 29904, ノーブレス
                // 29905, ノーブレス
                // 29906, 見習い騎士
                // 29907, 正騎士
                // 29908, 上級騎士
                // 29909, 騎士団長
                return new Quest_Medal_Cygnus();
            }
            case 29910, 29911, 29912, 29913, 29914 -> {
                // 29910, 覇王
                // 29911, 魔王
                // 29912, 賢王
                // 29913, 冥王
                // 29914, 海王
                return new Quest_Medal_Level200();
            }
            case 29924, 29925, 29926, 29927, 29928 -> {
                // 29924, 目覚めたアラン
                // 29925, 記憶の中のアラン
                // 29926, 試練の中のアラン
                // 29927, 希望の中のアラン
                // 29928, 英雄アラン
                return new Quest_Medal_Aran();
            }
            case 29934, 29935, 29936, 29937, 29938, 29939, 29940 -> {
                // 29934, いいひと
                // 29935, ぺリオン警備兵
                // 29936, カニングシティー名誉市民
                // 29937, 秘密組織秘密メンバー
                // 29938, ドラゴンマスター
                // 29939, ドラゴンマスター
                // 29940, 英雄の後継者
                return new Quest_Medal_Evan();
            }
            case 2230 -> {
                // 2230, 謎の小さな卵
                return new Quest_Ellinia_MysteriousSmallEgg();
            }
            case 2300, 2301, 2302, 2303, 2304, 2305, 2306, 2307, 2308, 2309, 2310 -> {
                // 2300, キノコ王国の事件！
                // 2301, キノコ王国の事件！
                // 2302, キノコ王国の事件！
                // 2303, キノコ王国の事件！
                // 2304, キノコ王国の事件！
                // 2305, キノコ王国の事件！
                // 2306, キノコ王国の事件！
                // 2307, キノコ王国の事件！
                // 2308, キノコ王国の事件！
                // 2309, キノコ王国の事件！
                // 2310, キノコ王国の事件！
                return new Quest_MushroomCastle_KingdomInDanger();
            }
            case 3360 -> {
                // 3360, 暗証番号の認証
                return new Quest_Magatia_VerifyingThePassword();
            }
            case 3514 -> {
                // 3514, 感情を売り出す魔法製錬術師
                return new Quest_Magatia_AlchemistWhoSellsEmotions();
            }
            case 4681 -> {
                // 4681, 未来への扉
                return new Quest_Zipangu_DoorToTheFuture();
            }
            case 4686, 4689 -> {
                // 4686, 決戦！移動要塞ベルガモット
                // 4689, 強襲！機械化小隊マーヴェリック
                return new Quest_Zipangu_FutureBoss();
            }
            case 21000, 21001 -> {
                // 21000, 子供を助けてください1
                // 21001, 子供を助けてください2
                return new Quest_Aran_RescueTheChild();
            }
            case 21010 -> {
                // 21010, 英雄の帰還
                return new Quest_Aran_ReturnOfTheHero();
            }
            case 21011 -> {
                // 21011, 消えた武器
                return new Quest_Aran_MissingWeapon();
            }
            case 21012 -> {
                // 21012, 消えた能力
                return new Quest_Aran_MissingAbility();
            }
            case 21013 -> {
                // 21013, 英雄のためのプレゼント
                return new Quest_Aran_GiftForTheHero();
            }
            case 21015, 21016, 21017, 21018 -> {
                // 21015, 基礎体力鍛錬1
                // 21016, 基礎体力鍛錬2
                // 21017, 基礎体力鍛錬3
                // 21018, 基礎体力テスト
                return new Quest_Aran_BasicTraining();
            }
            case 21100 -> {
                // 21100, 5人の英雄
                return new Quest_Aran_FiveHeroes();
            }
            case 21101 -> {
                // 21101, 鉾の使い手たる英雄
                return new Quest_Aran_HeroOfThePolearm();
            }
            case 21200, 21201 -> {
                // 21200, 主を待つ武器
                // 21201, 念願の鏡
                return new Quest_Aran_WeaponAwaitingItsMaster();
            }
            case 21202 -> {
                // 21202, 最高の武器を探して
                return new Quest_Aran_SearchingForTheBestWeapon();
            }
            case 21600, 21604, 21613, 21618 -> {
                // 21600, プニの頼み
                // 21604, ヴォルフライディング
                // 21613, オオカミの試験
                // 21618, ヴォルフの進化
                return new Quest_Aran_WolfRiding();
            }
            case 21700 -> {
                // 21700, 新たな始まり
                return new Quest_Aran_NewBeginning();
            }
            case 21703 -> {
                // 21703, 修行だけが唯一の道！3
                return new Quest_Aran_TrainingIsTheOnlyWay3();
            }
            case 21704 -> {
                // 21704, とても小さな一歩
                return new Quest_Aran_VerySmallStep();
            }
            case 21712, 21716 -> {
                // 21712, 1番目の情報収集の完了
                // 21716, 2番目の情報収集 完了
                return new Quest_Aran_InformationGathering();
            }
            case 21719 -> {
                // 21719, 人形使いの招待
                return new Quest_Aran_PuppeteersInvitation();
            }
            case 21720 -> {
                // 21720, 人形使いの警告
                return new Quest_Aran_PuppeteersWarning();
            }
            case 22000 -> {
                // 22000, 不思議な夢
                return new Quest_Evan_StrangeDream();
            }
            case 22001 -> {
                // 22001, エゾオオカミに餌をあげる
                return new Quest_Evan_FeedTheBulldog();
            }
            case 22002 -> {
                // 22002, ランチはサンドイッチ
                return new Quest_Evan_SandwichForLunch();
            }
            case 22003 -> {
                // 22003, お弁当配達
                return new Quest_Evan_LunchBoxDelivery();
            }
            case 22004 -> {
                // 22004, 垣根の修理
                return new Quest_Evan_FixingTheFence();
            }
            case 22007 -> {
                // 22007, 卵を集める
                return new Quest_Evan_CollectingEggs();
            }
            case 22008 -> {
                // 22008, 裏庭の狐の追放
                return new Quest_Evan_ChaseAwayTheFoxes();
            }
            case 22500 -> {
                // 22500, ちびドラゴン目覚める
                return new Quest_Evan_BabyDragonAwakens();
            }
            case 22501, 22502, 22503, 22504 -> {
                // 22501, お腹が空いたちびドラゴン
                // 22502, 乾草を一口
                // 22503, オオカミ肉を一口
                // 22504, 美味しい牛乳1
                return new Quest_Evan_HungryBabyDragon();
            }
            case 22507 -> {
                // 22507, ドラゴンマスターとは？
                return new Quest_Evan_WhatIsADragonMaster();
            }
            case 22100, 22101, 22102, 22103, 22104, 22105, 22106, 22107, 22108, 22109, 22401, 22403, 22406, 22411, 22510, 22512, 22514, 22518, 22536, 22541, 22546, 22560, 22564, 22565, 22567, 22575, 22578, 22581, 22582, 22585, 22591, 22593, 22594, 22595, 22602, 22603 -> {
                // 22100, ドラゴンマスター 1次転職
                // 22101, ドラゴンマスター 2次転職
                // 22102, ドラゴンマスター 3次転職
                // 22103, ドラゴンマスター 4次転職
                // 22104, ドラゴンマスター 5次転職
                // 22105, ドラゴンマスター 6次転職
                // 22106, ドラゴンマスター 7次転職
                // 22107, ドラゴンマスター 8次転職
                // 22108, ドラゴンマスター 9次転職
                // 22109, ドラゴンマスター 10次転職
                // 22401, ドラゴンライディングってできるのかなぁ？
                // 22403, ステキな鞍を求めて
                // 22406, 不便な鞍
                // 22411, またもや不便になった鞍
                // 22510, 手紙配達
                // 22512, ドラゴンマスターのお仕事
                // 22514, 修練あるのみ
                // 22518, 絶えないヘンケルの妄想
                // 22536, カニングシティーでの捜査 : ネーラ
                // 22541, 本の行方は？1
                // 22546, ドラゴンの種類と特徴(下)
                // 22560, 秘密組織への加入条件1
                // 22564, オニックスドラゴンに関する知識3
                // 22565, ミルの考え
                // 22567, 秘密組織　初回の任務
                // 22575, 秘密組織　二回目の任務
                // 22578, 秘密組織に関する疑問
                // 22581, 秘密組織　三回目の任務の前に
                // 22582, 秘密組織　三回目の任務
                // 22585, 秘密組織に関する疑惑
                // 22591, 過去、オニックスドラゴン、暗黒の魔法使い
                // 22593, 初回任務の結果
                // 22594, 二回目任務の結果
                // 22595, 三番目任務の結果
                // 22602, 脱皮直後１
                // 22603, 脱皮直後2
                return new Quest_Evan_Simplified();
            }
            case 50003 -> {
                // 50003, 何度でも止めてみせる！
                return new Quest_Zipangu_StopHimAgain();
            }
            case 50004, 50005 -> {
                // 50004, コアブレイズ始動
                // 50005, コアブレイズ暴走
                return new Quest_Zipangu_CoreBlaze();
            }
            case 50012 -> {
                // 50012, 魔術と科学と宇宙エネルギーと
                return new Quest_Zipangu_MagicScienceAndSpaceEnergy();
            }
            case 50016 -> {
                // 50016, ブレイズブレイク！
                return new Quest_Zipangu_BlazeBreak();
            }
            case 50042 -> {
                // 50042, パチンコデビュー
                return new Quest_Common_PachinkoDebut();
            }
            case 50217, 50218, 50233, 50234 -> {
                // 50217, レッドパンダの友達
                // 50218, オチャマルの友達
                // 50233, レッドパンダと親しくなろう！
                // 50234, オチャマルと親しくなろう！
                return new Quest_Common_PetFriends();
            }
            case 1028 -> {
                // 1028, リス港口へ！
                return new Quest_MapleIsland_ToLithHarbor();
            }
            case 3452 -> {
                // 3452, ブロックパスは外界生物？
                return new Quest_OmegaSector_IsBloctopusAnAlien();
            }
            case 3941 -> {
                // 3941, 王妃の絹を奪う
                return new Quest_Ariant_StealingTheQueensSilk();
            }
            case 3933 -> {
                // 3933, アディンの砂絵団
                return new Quest_Ariant_AdinsSandBandits();
            }
            case 2127 -> {
                // 2127, 砂漠へ…
                return new Quest_Ariant_ToTheDesert();
            }
            case 2124, 2126 -> {
                // 2124, 砂絵団の補給品
                // 2126, 砂絵団の補給品！
                return new Quest_Ariant_SandBanditsSupplies();
            }
            case 3833 -> {
                // 3833, 足りない薬材探し
                return new Quest_MuLung_MissingHerbs();
            }
            case 3314 -> {
                // 3314, 生命錬金術と、失踪した錬金術師
                return new Quest_Magatia_LifeAlchemyAndTheMissingAlchemist();
            }
            case 3305, 3306 -> {
                // 3305, ジェニミストのマントを再び受け取る。
                // 3306, アルカドノのマントを再び受け取る。
                return new Quest_Magatia_ReceivingTheCapeAgain();
            }
            case 3320 -> {
                // 3320, ファウェンが知っていること
                return new Quest_Magatia_WhatParwenKnows();
            }
            case 3321 -> {
                // 3321, 失踪した錬金術師ドラン
                return new Quest_Magatia_TheMissingAlchemistDrang();
            }
            case 3301, 3303 -> {
                // 3301, ジェニミスト協会長のテスト
                // 3303, アルカドノ協会長のテスト
                return new Quest_Magatia_AssociationPresidentsTest();
            }
            case 6700 -> {
                // 6700, 궁수의 길
                return new Quest_Victoria_ArrowSupply();
            }
            case 3353 -> {
                // 3353, ドランの願い
                return new Quest_Magatia_DrangsWish();
            }
            case 3354 -> {
                // 3354, ドランの薬
                return new Quest_Magatia_DrangsMedicine();
            }
            case 2148, 2149, 2150, 2151, 2152 -> {
                // 2148, 噂の真相-豚と一緒に踊りを
                // 2149, 噂の真相-マンジ
                // 2150, 噂の真相-イヤン
                // 2151, 噂の真相-コブシを開いて立て
                // 2152, 噂の真相-Dr.ベティ
                return new Quest_Perion_TheTruthBehindTheRumor();
            }
            case 3108 -> {
                // 3108, スノウマンの怒り-手がかり発見
                return new Quest_ElNath_SnowmansAnger();
            }
            case 2156 -> {
                // 2156, 願いを聞き入れる無知改色デンデン皮!？
                return new Quest_Victoria_RainbowSnailShell();
            }
            default -> {
            }
        }
        return null;
    }
}
