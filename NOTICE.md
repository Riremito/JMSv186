# NOTICE

## About this project
This repository is an unofficial server emulator for JMS (the Japanese version of MapleStory).
It consists of code developed by Riremito and code inherited from the earlier open-source projects OdinMS and ZeroFusion.

This project is based on the JMS v186 source released on the RaGEZONE forum:
+ https://forum.ragezone.com/threads/jms186-source-87-working-like-gms90-sea1-02.1111150/

The JMS v186 source is in turn based on the SEA v1.02 source released on the RaGEZONE forum:
+ https://forum.ragezone.com/threads/1-02-sea-source-fully-working.809012/

## Authors
| Part | Authors | Years | Main location |
| --- | --- | --- | --- |
| This project's own code | Riremito | 2023 - 2026 | `src/tacos/` |
| Code derived from OdinMS | Patrick Huy &lt;patrick.huy@frz.cc&gt;<br>Matthias Butz &lt;matze@odinms.de&gt;<br>Jan Christian Meyer &lt;vimes@odinms.de&gt; | 2008 - 2010 | `src/odin/`, and some files in `src/tacos/` (`tacos/odin`, `tacos/database`, `tacos/network`, `tacos/wz`) |
| Code derived from ZeroFusion | The OdinMS authors above (ZeroFusion organized by "RMZero213" &lt;RMZero213@hotmail.com&gt;) | 2008 | Some files in `src/odin/` |

For other contributors, see the git history.
Each source file starts with a notice naming its authors and license.

## License
| Part | License |
| --- | --- |
| This project's own code (files with `Copyright (C) <year> Riremito`) | [GNU General Public License version 3](https://www.gnu.org/licenses/gpl-3.0.html) or later (GPL-3.0-or-later) |
| Code derived from OdinMS / ZeroFusion (files with `This file is part of the OdinMS ...` / `ZeroFusion ...`) | [GNU Affero General Public License version 3](https://www.gnu.org/licenses/agpl-3.0.html) only (AGPL-3.0-only) |

Some files under `src/odin/` have no license notice; treat them as OdinMS-derived code as well.

## Notes for users
+ The server as a whole contains AGPLv3 code. Section 13 of the GPLv3 allows GPLv3 code to be combined with AGPLv3 code, but when you distribute or publish the combined server you must also follow the terms of the AGPLv3.
+ Under section 13 of the AGPLv3, if you modify the server and let other people use it over a network, you must offer those users the corresponding source code.
+ Do not remove or alter the copyright and license notices in the source files.
+ The `odin` package is planned to be retired and replaced. As long as OdinMS-derived code remains, the terms above apply.
+ This document is not legal advice. Refer to the full text of each license for the exact terms.

## Bundled libraries (`lib/`)
Each library is copyrighted by its authors and provided under its own license. See each library's distribution for details.

| Library | Files | Authors | License |
| --- | --- | --- | --- |
| ASM | `asm-9.7.jar`, `asm-commons-9.7.jar`, `asm-tree-9.7.jar`, `asm-util-9.7.jar` | OW2 Consortium | BSD-3-Clause |
| Bouncy Castle | `bcprov-jdk16-145.jar` | The Legion of the Bouncy Castle | Bouncy Castle License (MIT style) |
| exp4j | `exp4j-0.4.8.jar` | Frank Asseg | Apache License 2.0 |
| Project Lombok | `lombok-1.18.46.jar` | The Project Lombok Authors | MIT License |
| Apache MINA | `mina-core.jar` | The Apache Software Foundation | Apache License 2.0 |
| MySQL Connector/J | `mysql-connector-j-9.6.0.jar` | Oracle and/or its affiliates | GPLv2 with the Universal FOSS Exception |
| OpenJDK Nashorn | `nashorn-core-15.4.jar` | Oracle and/or its affiliates | GPLv2 with the Classpath Exception |
| SLF4J | `slf4j-api.jar`, `slf4j-jdk14.jar` | QOS.ch | MIT License |

## Game data
+ MapleStory is a trademark of NEXON. This project is unofficial and is not affiliated with NEXON or its affiliates.
+ The game data (`wz_xml/`) and scripts (`scripts/`) are not included in this repository and are obtained separately. Rights to the game data belong to their respective owners.

## Original license notices
The original notices as they appear in the source files.

OdinMS (2008 - 2010)
```
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
```

OdinMS (2008)
```
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
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
```

ZeroFusion (2008)
```
This file is part of the ZeroFusion MapleStory Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>
ZeroFusion organized by "RMZero213" <RMZero213@hotmail.com>

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
```

This project's own code (`<year>` is 2023 - 2026)
```
Copyright (C) <year> Riremito

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```
