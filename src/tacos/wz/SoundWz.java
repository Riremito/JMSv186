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
import odin.provider.IMapleData;
import odin.provider.IMapleDataEntity;
import tacos.config.Content;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class SoundWz extends WzXML {

    public SoundWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Sound" : "Sound.wz");
    }

    private ArrayList<String> bgm_path = null;
    private Random rand = new Random();

    private void loadBGM() {
        this.bgm_path = new ArrayList<>();
        Pattern pattern = Pattern.compile("(Bgm.*)\\.img");
        for (IMapleDataEntity dir : getRootDirectory().getFiles()) {
            Matcher matcher = pattern.matcher(dir.getName());
            if (matcher.matches()) {
                String base = matcher.group(1);
                for (IMapleData md : getData(dir.getName()).getChildren()) {
                    this.bgm_path.add(base + "/" + md.getName());
                }
            }
        }

        DebugLogger.XmlLog("loadBGM : " + this.bgm_path.size());
    }

    public String getRandomBGM() {
        if (this.bgm_path == null) {
            loadBGM();
        }

        int index = rand.nextInt(this.bgm_path.size());
        DebugLogger.XmlLog("getRandomBGM : " + index);
        return this.bgm_path.get(index);
    }
}
