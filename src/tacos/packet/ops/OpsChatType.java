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
package tacos.packet.ops;

/**
 *
 * @author Riremito
 */
public enum OpsChatType implements IPacketOps {

    CHAT_TYPE_NORMAL(0),
    CHAT_TYPE_WHISPER(1),
    CHAT_TYPE_GROUPPARTY(2),
    CHAT_TYPE_GROUPFRIEND(3),
    CHAT_TYPE_GROUPGUILD(4),
    CHAT_TYPE_GROUPALLIANCE(5),
    CHAT_TYPE_COUPLE(6),
    CHAT_TYPE_GAMEDESC(7),
    CHAT_TYPE_TIP(8),
    CHAT_TYPE_NOTICE(9),
    CHAT_TYPE_NOTICE2(10),
    CHAT_TYPE_ADMINCHAT(11),
    CHAT_TYPE_SYSTEM(12),
    CHAT_TYPE_SPEAKERCHANNEL(13),
    CHAT_TYPE_SPEAKERWORLD(14),
    CHAT_TYPE_SKULLSPEAKER(15),
    CHAT_TYPE_ITEMSPEAKER(16),
    CHAT_TYPE_ITEMSPEAKER_ITEM(17),
    CHAT_TYPE_AVATARMEGAPHONE(18),
    CHAT_TYPE_GACHAPONANNOUNCE(19),
    CHAT_TYPE_CASHGACHAPONANNOUNCE(20),
    CHAT_TYPE_CASHGACHAPON_OPEN_ANNOUNCE(21),
    CHAT_TYPE_CASHGACHAPON_COPY_ANNOUNCE(22),
    CHAT_TYPE_SPEAKERBRIDGE(23),
    CHAT_TYPE_SPEAKERWORLDEX_PREVIEW(24),
    CHAT_TYPE_MOB(25),
    CHAT_TYPE_EXPEDITION(26),
    CHAT_TYPE_NO(27),
    UNKNOWN;

    private int value;

    OpsChatType(int val) {
        this.value = val;
    }

    OpsChatType() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int val) {
        this.value = val;
    }
}
