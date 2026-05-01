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

import tacos.packet.ops.OpsMovePathAttr;

/**
 *
 * @author Riremito
 */
public class TacosDragon extends TacosObject {

    private int job_code;

    public TacosDragon(TacosCharacter chr) {
        this.job_code = chr.getJob();
        super(chr.getId(), chr.getPosition().x, chr.getPosition().y, OpsMovePathAttr.MPA_NORMAL.get(), chr.getFH());
    }

    public int getJobCode() {
        return this.job_code;
    }

    public void setJobCode(TacosCharacter chr) {
        this.job_code = chr.getJob();
    }
}
