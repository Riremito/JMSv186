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
package tacos.client;

import java.util.TreeMap;
import tacos.config.DeveloperMode;
import tacos.config.Region;
import tacos.config.Version;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsSkill;
import tacos.packet.response.ResCUserLocal;

/**
 *
 * @author Riremito
 */
public class TacosCoolTime {

    private TreeMap<OpsSkill, TacosCoolTimeData> map_ctd = new TreeMap<>();
    private TacosCharacter chr;

    public TacosCoolTime(TacosCharacter chr) {
        this.chr = chr;
    }

    public boolean add(OpsSkill skill, int skill_ct_sec) {
        if (check(skill)) {
            return false;
        }

        TacosCoolTimeData ctd = new TacosCoolTimeData();
        int ct_sec = skill_ct_sec;
        if (DeveloperMode.DM_SKILL_COOL_TIME.getInt() != 0) {
            ct_sec = Math.min(skill_ct_sec, DeveloperMode.DM_SKILL_COOL_TIME.getInt());
        }

        ctd.skill = skill;
        ctd.time_start = System.currentTimeMillis();
        ctd.time_end = ctd.time_start + (ct_sec * 1000);
        this.map_ctd.put(skill, ctd);

        this.chr.SendPacket(ResCUserLocal.SkillCooltimeSet(skill.get(), ct_sec));
        return true;
    }

    public boolean remove(OpsSkill skill) {
        if (!check(skill)) {
            return false;
        }

        this.map_ctd.remove(skill);
        this.chr.SendPacket(ResCUserLocal.SkillCooltimeSet(skill.get(), 0));
        return true;
    }

    public boolean check(OpsSkill skill) {
        return this.map_ctd.containsKey(skill);
    }

    public void timeLeap() {
        for (TacosCoolTimeData ctd : this.map_ctd.values()) {
            if (ctd.skill != OpsSkill.VIPER_TIME_LEAP) {
                remove(ctd.skill);
            }
        }
    }

    public void update(long time) {
        for (TacosCoolTimeData ctd : this.map_ctd.values()) {
            if (ctd.time_end <= time) {
                remove(ctd.skill);
            }
        }
    }

    public byte[] getBufferForLogin(long time) {
        ServerPacket data = new ServerPacket();

        data.Encode2(this.map_ctd.size());
        for (TacosCoolTimeData ctd : this.map_ctd.values()) {
            data.Encode4(ctd.skill.get());

            long ct_sec = Math.max(0, ctd.time_end - time) / 1000;
            if (Version.GreaterOrEqual(Region.JMS, 302) | Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
                data.Encode4((int) ct_sec);
            } else {
                data.Encode2((short) ct_sec);
            }
        }

        return data.getBytes();
    }

    public static class TacosCoolTimeData {

        public OpsSkill skill;
        public long time_start;
        public long time_end;
    }
}
