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
package tacos.wz;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import odin.provider.IMapleDataEntity;
import tacos.config.Content;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class UIWz extends WzXML {

    public UIWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/UI" : "UI.wz");
    }

    private ArrayList<String> map_login = null;
    private Random rand = new Random();

    private void loadMapLogin() {
        this.map_login = new ArrayList<>();
        Pattern pattern = Pattern.compile("(MapLogin\\d*)\\.img");
        for (IMapleDataEntity dir : getRootDirectory().getFiles()) {
            Matcher matcher = pattern.matcher(dir.getName());
            if (matcher.matches()) {
                this.map_login.add(matcher.group(1));
            }
        }

        DebugLogger.XmlLog("loadMapLogin : " + this.map_login.size());
    }

    public String getRandomMapLogin() {
        if (this.map_login == null) {
            loadMapLogin();
        }

        int index = rand.nextInt(this.map_login.size());
        DebugLogger.XmlLog("getRandomMapLogin : " + index);
        return this.map_login.get(index);
    }
}
