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
package tacos.packet.response.data;

import odin.client.MapleCharacter;
import odin.server.maps.MapleSummon;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import tacos.packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class DataCSummoned {

    // CSummoned::Init
    public static byte[] Init(MapleSummon summon, boolean animated) {
        ServerPacket data = new ServerPacket();

        boolean is_avater_look = false;
        MapleCharacter chr = summon.getOwner();
        int m_nSkillID = summon.getSkill();

        // ダミーエフェクト
        if (m_nSkillID == 4341006) {
            if (chr != null) {
                is_avater_look = true;
            }
        }

        data.Encode2(summon.getPosition().x); // m_ptPos.x
        data.Encode2(summon.getPosition().y); // m_ptPos.y
        data.Encode1(summon.getSkill() == 32111006 ? 5 : 4); // m_nMoveAction MA_PRONE, MA_ALERT, MA_TESLA_COIL_TRIANGLE
        data.Encode2(summon.getFh()); // m_dwSN (CStaticFoothold)
        data.Encode1(summon.getMovementType().getValue()); // m_nMoveAbility
        data.Encode1(summon.getSummonType()); // m_nAssistType
        data.Encode1(animated ? 0 : 1); //nEnterType ENTER_TYPE_DEFAULT, ENTER_TYPE_CREATE_SUMMONED

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            data.Encode1(0);
        }

        if (ServerConfig.JMS186orLater()) {
            data.Encode1(is_avater_look ? 1 : 0);
            if (is_avater_look) {
                data.EncodeBuffer(DataAvatarLook.Encode(chr));
            }
        }

        if (Version.PostBB()) {
            // アクセラレーター<EX-7>
            if (m_nSkillID == 35111002) {
                int m_nTeslaCoilState = 0;
                data.Encode1(m_nTeslaCoilState); // m_nTeslaCoilState
                if (m_nTeslaCoilState == 1) { // TESLACOIL_LEADER
                    for (int i = 0; i < 3; i++) {
                        data.Encode2(0); // x
                        data.Encode2(0); // y
                    }
                }
            }
            // 鬼神召喚
            if (Version.GreaterOrEqual(Region.JMS, 302)) {
                if (m_nSkillID == 42111003) {
                    data.Encode2(summon.getPosition().x + 250);
                    data.Encode2(summon.getPosition().y);
                    data.Encode2(summon.getPosition().x - 250);
                    data.Encode2(summon.getPosition().y);
                }
            }
        }

        return data.get().getBytes();
    }
}
