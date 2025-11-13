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

import odin.provider.MapleData;
import odin.provider.MapleDataTool;
import odin.tools.StringUtil;

public class SkillFactory {

    public static final ISkill getSkill(final int id) {
        return SkillWz.getSkill().get(Integer.valueOf(id));
    }

    public static final List<Integer> getSkillsByJob(final int jobId) {
        return SkillWz.getSkillsByJob().get(jobId);
    }

    public static final String getSkillName(final int id) {
        ISkill skil = getSkill(id);
        if (skil != null) {
            return skil.getName();
        }
        return null;
    }

    public static final String getName(final int id) {
        String strId = Integer.toString(id);
        strId = StringUtil.getLeftPaddedStr(strId, '0', 7);
        MapleData skillroot = StringWz.getSkill().getChildByPath(strId);
        if (skillroot != null) {
            return MapleDataTool.getString(skillroot.getChildByPath("name"), "");
        }
        return null;
    }

    public static final SummonSkillEntry getSummonData(final int skillid) {
        return SkillWz.getSummonSkillInformation().get(skillid);
    }

    public static final Collection<ISkill> getAllSkills() {
        return SkillWz.getSkill().values();
    }
}
