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
package odin.provider.WzXML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odin.provider.IMapleDataEntity;
import odin.provider.IMapleDataEntry;
import odin.provider.IMapleDataDirectoryEntry;

public class WZDirectoryEntry extends WZEntry implements IMapleDataDirectoryEntry {

    private List<IMapleDataDirectoryEntry> subdirs = new ArrayList<>();
    private List<IMapleDataEntry> files = new ArrayList<>();
    private Map<String, IMapleDataEntry> entries = new HashMap<>();

    public WZDirectoryEntry(String name, int size, int checksum, IMapleDataEntity parent) {
        super(name, size, checksum, parent);
    }

    public WZDirectoryEntry() {
        super(null, 0, 0, null);
    }

    public void addDirectory(IMapleDataDirectoryEntry dir) {
        subdirs.add(dir);
        entries.put(dir.getName(), dir);
    }

    public void addFile(IMapleDataEntry fileEntry) {
        files.add(fileEntry);
        entries.put(fileEntry.getName(), fileEntry);
    }

    @Override
    public List<IMapleDataDirectoryEntry> getSubDirectories() {
        return Collections.unmodifiableList(subdirs);
    }

    @Override
    public List<IMapleDataEntry> getFiles() {
        return Collections.unmodifiableList(files);
    }

    @Override
    public IMapleDataEntry getEntry(String name) {
        return entries.get(name);
    }
}
