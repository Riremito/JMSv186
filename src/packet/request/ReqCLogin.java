/*
 * Copyright (C) 2024 Riremito
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
package packet.request;

import client.MapleClient;
import config.DebugConfig;
import config.ServerConfig;
import debug.Debug;
import debug.DebugAutoLogin;
import handling.channel.handler.InterServerHandler;
import packet.ClientPacket;
import packet.response.ResCLogin;
import packet.response.addon.AddonResponse;

/**
 *
 * @author Riremito
 */
public class ReqCLogin {

    public static boolean OnPacket(ClientPacket.Header header, ClientPacket cp, MapleClient c) {
        switch (header) {
            // GameGuard
            case CP_T_UpdateGameGuard: {
                c.SendPacket(ResCLogin.CheckGameGuardUpdate());
                break;
            }
            // ログイン画面
            case CP_CreateSecurityHandle: {
                c.SendPacket(ResCLogin.LoginAUTH(cp, c));
                return true;
            }
            // ログイン
            case CP_CheckPassword: {
                if (LoginRequest.login(cp, c)) {
                    InterServerHandler.SetLogin(false);
                    Debug.InfoLog("[LOGIN MAPLEID] \"" + c.getAccountName() + "\"");

                    if (ServerConfig.login_server_antihack && (ServerConfig.IsJMS() && ServerConfig.GetVersion() == 186)) {
                        c.getSession().write(AddonResponse.Hash());
                        c.getSession().write(AddonResponse.Scan(0x008625B5, (short) 3)); // damage hack check
                        // test
                        byte mem[] = {(byte) 0x90, (byte) 0x90, (byte) 0x90};
                        c.getSession().write(AddonResponse.Patch(0x00BCCA45, mem));
                    }

                    c.SendPacket(SocketPacket.AuthenMessage());
                }
                return true;
            }
            case CP_Check2ndPassword: {
                if (ServerConfig.IsJMS() && ServerConfig.IsPostBB()) {
                    LoginRequest.ServerListRequest(c);
                }
                return true;
            }
            // サーバー一覧
            case CP_WorldInfoRequest: {
                // +p
                LoginRequest.ServerListRequest(c);
                return true;
            }
            // サーバーの状態
            case CP_CheckUserLimit: {
                // +p
                LoginRequest.ServerStatusRequest(c);
                return true;
            }
            // キャラクター一覧
            case CP_SelectWorld: {
                LoginRequest.CharlistRequest(cp, c);
                return true;
            }
            // キャラクター作成時の名前重複確認
            case CP_CheckDuplicatedID: {
                // p
                LoginRequest.CheckCharName(cp, c);
                return true;
            }
            // キャラクター作成
            case CP_CreateNewCharacter: {
                LoginRequest.CreateChar(cp, c);
                return true;
            }
            // キャラクター削除
            case CP_DeleteCharacter: {
                LoginRequest.DeleteChar(cp, c);
                return true;
            }
            // クラッシュデータ
            case CP_ExceptionLog: {
                // @000F EncodeBuffer(CrashDumpLog)
                // 起動時に何らかの条件で前回のクラッシュの詳細のテキストが送信される
                // 文字列で送信されているがnullで終わっていないので注意
                return true;
            }
            // キャラクター選択
            case CP_SelectCharacter:
            case CP_CheckPinCode: {
                if (LoginRequest.Character_WithSecondPassword(cp, c)) {
                    InterServerHandler.SetLogin(false);
                }
                return true;
            }
            // ログイン画面に到達
            case REACHED_LOGIN_SCREEN: {
                // @0018
                // ログイン画面に到達した場合に送信される

                if (DebugConfig.auto_login) {
                    DebugAutoLogin.AutoLogin(c);
                }
                return true;
            }
            case CP_ViewAllChar: {
                if (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 194) {
                    c.SendPacket(ResCLogin.ViewAllCharResult_Alloc(c));
                    c.SendPacket(ResCLogin.ViewAllCharResult(c));
                } else {
                    c.SendPacket(ResCLogin.ViewAllCharResult_v201(c));
                }
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }
}
