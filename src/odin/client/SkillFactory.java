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
package odin.client;

import tacos.wz.data.SkillWz;
import tacos.wz.data.StringWz;
import java.util.Collection;
import java.util.List;

import odin.provider.IMapleData;
import tacos.wz.TacosWzDataTool;

public class SkillFactory {

    public static ISkill getSkill(int id) {
        return SkillWz.get().getSkill().get(id);
    }

    public static List<Integer> getSkillsByJob(int jobId) {
        return SkillWz.get().getSkillsByJob().get(jobId);
    }

    public static String getSkillName(int id) {
        ISkill skil = getSkill(id);
        if (skil != null) {
            return skil.getName();
        }
        return null;
    }

    public static String getName(int skill_id) {
        IMapleData skillroot = StringWz.get().getSkill().getChildByPath(String.format("%07d", skill_id));
        if (skillroot != null) {
            return TacosWzDataTool.getString(skillroot.getChildByPath("name"), "");
        }
        return null;
    }

    public static SummonSkillEntry getSummonData(int skillid) {
        return SkillWz.get().getSummonSkillInformation().get(skillid);
    }

    public static Collection<ISkill> getAllSkills() {
        return SkillWz.get().getSkill().values();
    }
}
