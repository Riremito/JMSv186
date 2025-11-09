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
package provider.WzXML;

import debug.DebugLogger;
import server.server.ServerOdinGame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataProvider;

public class XMLWZFile implements MapleDataProvider {

    private File root;
    private WZDirectoryEntry rootForNavigation;

    public XMLWZFile(File fileIn) {
        root = fileIn;
        rootForNavigation = new WZDirectoryEntry(fileIn.getName(), 0, 0, null);
        fillMapleDataEntitys(root, rootForNavigation);
    }

    private void fillMapleDataEntitys(File lroot, WZDirectoryEntry wzdir) {
        for (File file : lroot.listFiles()) {
            String fileName = file.getName();

            if (file.isDirectory() && !fileName.endsWith(".img")) {
                WZDirectoryEntry newDir = new WZDirectoryEntry(fileName, 0, 0, wzdir);
                wzdir.addDirectory(newDir);
                fillMapleDataEntitys(file, newDir);

            } else if (fileName.endsWith(".xml")) { // get the real size here?
                wzdir.addFile(new WZFileEntry(fileName.substring(0, fileName.length() - 4), 0, 0, wzdir));
            }
        }
    }

    @Override
    public MapleData getData(String path) {
        File dataFile = new File(root, path + ".xml");
        if (!dataFile.exists()) {
            DebugLogger.ErrorLog("Not Found : " + path + ".xml");
            return null;
        }
        File imageDataDir = new File(root, path);
        /*		if (!dataFile.exists()) {
        throw new RuntimeException("Datafile " + path + " does not exist in " + root.getAbsolutePath());
        }*/
        FileInputStream fis;
        try {
            fis = new FileInputStream(dataFile);
        } catch (FileNotFoundException e) {
            // Wz改変済みの場合は追加マップを開放
            if (ServerOdinGame.IsCustom()) {
                try {
                    int dir_left = path.indexOf("/");
                    int dir_right = path.indexOf("/", dir_left + 1);
                    String path_dir = path.substring(dir_left, dir_right + 1);
                    String pathCustom = path;
                    pathCustom = pathCustom.replace(path_dir, "/Custom/");

                    System.out.println("custom wz: " + pathCustom + ".xml");

                    File dataFileCustom = new File(root, pathCustom + ".xml");
                    fis = new FileInputStream(dataFileCustom);
                } catch (FileNotFoundException e2) {
                    throw new RuntimeException("Datafile " + path + " does not exist in " + root.getAbsolutePath());
                }
            } else {
                throw new RuntimeException("Datafile " + path + " does not exist in " + root.getAbsolutePath());
            }
        }
        final XMLDomMapleData domMapleData;

        try {
            domMapleData = new XMLDomMapleData(fis, imageDataDir.getParentFile());
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return domMapleData;
    }

    @Override
    public MapleDataDirectoryEntry getRoot() {
        return rootForNavigation;
    }
}
