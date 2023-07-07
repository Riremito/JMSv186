/*
 * Copyright (C) 2023 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package packet;

/**
 *
 * @author Riremito
 */
public class v194_0_CP {

    public static void Set() {
        ClientPacket.Header.CP_CheckPassword.Set(0x0001);
        ClientPacket.Header.CP_WorldInfoRequest.Set(0x0003);
        ClientPacket.Header.CP_SelectWorld.Set(0x0004);
        ClientPacket.Header.CP_CheckUserLimit.Set(0x0005);
        ClientPacket.Header.CP_SelectCharacter.Set(0x0006);
        ClientPacket.Header.CP_MigrateIn.Set(0x0007);
        ClientPacket.Header.CP_CheckDuplicatedID.Set(0x0008);
        ClientPacket.Header.CP_ViewAllChar.Set(0x000A);
        ClientPacket.Header.CP_CreateNewCharacter.Set(0x000B);
        ClientPacket.Header.CP_DeleteCharacter.Set(0x000D);
        ClientPacket.Header.CP_AliveAck.Set(0x000F);
        ClientPacket.Header.CP_ExceptionLog.Set(0x0010);
        ClientPacket.Header.CP_CreateSecurityHandle.Set(0x0018); // MapLogin, name wrong?

        ClientPacket.Header.CP_UserTransferFieldRequest.Set(0x001B); // Map移動
        ClientPacket.Header.CP_UserTransferChannelRequest.Set(0x001C); // CH変更
        ClientPacket.Header.CP_UserMigrateToCashShopRequest.Set(0x001D); // ポイントショップへ移動
        ClientPacket.Header.CP_UserMove.Set(0x001E); // 座標移動
        ClientPacket.Header.CP_UserSitRequest.Set(0x0020); // イス
        ClientPacket.Header.CP_UserMeleeAttack.Set(0x0023); // 攻撃
        ClientPacket.Header.CP_UserShootAttack.Set(0x0024); // 遠距離攻撃
        ClientPacket.Header.CP_UserMagicAttack.Set(0x0025); // 魔法攻撃
        ClientPacket.Header.CP_UserBodyAttack.Set(0x0026);
        ClientPacket.Header.CP_UserHit.Set(0x0028); // 被ダメージ
        ClientPacket.Header.CP_UserChat.Set(0x002A); // チャット
        ClientPacket.Header.CP_UserSelectNpc.Set(0x0036); // NPC会話
        ClientPacket.Header.CP_UserScriptMessageAnswer.Set(0x003A); // NPC会話継続
        ClientPacket.Header.CP_UserShopRequest.Set(0x003B);
        ClientPacket.Header.CP_UserTrunkRequest.Set(0x003C);
        ClientPacket.Header.CP_UserChangeSlotPositionRequest.Set(0x0046); // アイテム移動, ドロップ
        ClientPacket.Header.CP_UserStatChangeItemUseRequest.Set(0x0048); // 回復薬
        ClientPacket.Header.CP_UserConsumeCashItemUseRequest.Set(0x004F); // ポイントアイテム
        ClientPacket.Header.CP_UserPortalScrollUseRequest.Set(0x0057); // 帰還の書
        ClientPacket.Header.CP_UserUpgradeItemUseRequest.Set(0x0058); // 書
        ClientPacket.Header.CP_UserHyperUpgradeItemUseRequest.Set(0x0059); // 装備強化の書
        ClientPacket.Header.CP_UserItemOptionUpgradeItemUseRequest.Set(0x005A); // 潜在能力覚醒の書
        ClientPacket.Header.CP_UserItemReleaseRequest.Set(0x005D); // 鑑定の虫眼鏡
        ClientPacket.Header.CP_UserAbilityUpRequest.Set(0x005E); // AP
        ClientPacket.Header.CP_UserAbilityMassUpRequest.Set(0x005F); // AP自動配分
        ClientPacket.Header.CP_UserChangeStatRequest.Set(0x0060); // 時間経過による自動回復
        ClientPacket.Header.CP_UserSkillUpRequest.Set(0x0062); // SP
        ClientPacket.Header.CP_UserSkillUseRequest.Set(0x0063); // スキル使用
        ClientPacket.Header.CP_UserSkillCancelRequest.Set(0x0064); // バフキャンセル
        ClientPacket.Header.CP_UserSkillPrepareRequest.Set(0x0065);
        ClientPacket.Header.CP_UserDropMoneyRequest.Set(0x0066); // メルを捨てる
        ClientPacket.Header.CP_UserCharacterInfoRequest.Set(0x0069); // キャラクター情報
        ClientPacket.Header.CP_UserPortalScriptRequest.Set(0x006E); // Map移動スクリプト
        ClientPacket.Header.CP_UserPortalTeleportRequest.Set(0x0070); // Map内移動スクリプト
        ClientPacket.Header.CP_UserMapTransferRequest.Set(0x0071); // テレポストーン
        ClientPacket.Header.CP_UserQuestRequest.Set(0x0076);
        ClientPacket.Header.CP_Admin.Set(0x009C); // GMコマンド
        ClientPacket.Header.CP_Log.Set(0x009D); // GMコマンド文字列
        ClientPacket.Header.CP_FuncKeyMappedModified.Set(0x00A4); // キー設定
        ClientPacket.Header.CP_UserMigrateToITCRequest.Set(0x00C1); // MTSへ移動
        ClientPacket.Header.CP_UpdateScreenSetting.Set(0x00D8); // CWvsApp::Run
        //ClientPacket.Header.CP_QuickslotKeyMappedModified.Set(0x00D6);
        ClientPacket.Header.CP_MobMove.Set(0x00E2); // Mob移動
        ClientPacket.Header.CP_MobApplyCtrl.Set(0x00E3);
        ClientPacket.Header.CP_DropPickUpRequest.Set(0x00F5); // 拾う
        ClientPacket.Header.CP_ReactorHit.Set(0x00F8); // 設置物攻撃
    }
}
