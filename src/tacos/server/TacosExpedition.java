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
package tacos.server;

import java.util.ArrayList;
import java.util.List;
import tacos.client.TacosCharacter;
import tacos.packet.ServerPacket;

/**
 *
 * @author Riremito
 */
// EXPEDITION : 5 parties of 6 members, the master is the boss of aParty[nMasterPartyIndex].
public class TacosExpedition {

    public static final int PARTY_MAX = 5;
    public static final int MEMBER_MAX = 6;

    // last known info, kept for offline members.
    public static class Member {

        private final int id;
        private String name;
        private int job;
        private int level;

        public Member(TacosCharacter chr) {
            this.id = chr.getId();
            update(chr);
        }

        public final void update(TacosCharacter chr) {
            this.name = chr.getName();
            this.job = chr.getJob();
            this.level = chr.getLevel();
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public int getJob() {
            return this.job;
        }

        public int getLevel() {
            return this.level;
        }
    }

    private final TacosWorld world;
    private final int quest_id;
    private int master_index = 0;
    private final List<List<Member>> parties = new ArrayList<>();
    private final int[] bosses = new int[PARTY_MAX];

    public TacosExpedition(TacosWorld world, TacosCharacter master, int quest_id) {
        this.world = world;
        this.quest_id = quest_id;
        for (int i = 0; i < PARTY_MAX; i++) {
            this.parties.add(new ArrayList<>());
        }
        this.parties.get(0).add(new Member(master));
        this.bosses[0] = master.getId();
    }

    public int getQuestId() {
        return this.quest_id;
    }

    public int getMasterIndex() {
        return this.master_index;
    }

    public int getMasterId() {
        return this.bosses[this.master_index];
    }

    public int getBossId(int party_index) {
        return this.bosses[party_index];
    }

    public List<Member> getParty(int party_index) {
        return this.parties.get(party_index);
    }

    public int getMemberCount() {
        int count = 0;
        for (List<Member> party : this.parties) {
            count += party.size();
        }
        return count;
    }

    public int findPartyIndex(int character_id) {
        for (int i = 0; i < PARTY_MAX; i++) {
            for (Member member : this.parties.get(i)) {
                if (member.getId() == character_id) {
                    return i;
                }
            }
        }
        return -1;
    }

    public Member findMember(int character_id) {
        for (List<Member> party : this.parties) {
            for (Member member : party) {
                if (member.getId() == character_id) {
                    return member;
                }
            }
        }
        return null;
    }

    public boolean contains(int character_id) {
        return findMember(character_id) != null;
    }

    public TacosCharacter findOnline(Member member) {
        return this.world.findOnlinePlayerById(member.getId(), false);
    }

    // returns the party index, -1 when full.
    public int add(TacosCharacter chr) {
        return add(chr, -1);
    }

    public int add(TacosCharacter chr, int party_index) {
        for (int i = 0; i < PARTY_MAX; i++) {
            if (party_index != -1 && i != party_index) {
                continue;
            }
            List<Member> party = this.parties.get(i);
            if (party.size() < MEMBER_MAX) {
                party.add(new Member(chr));
                if (this.bosses[i] == 0) {
                    this.bosses[i] = chr.getId();
                }
                return i;
            }
        }
        return -1;
    }

    // returns the party index the member was in, -1 when not found.
    public int remove(int character_id) {
        int party_index = findPartyIndex(character_id);
        if (party_index == -1) {
            return -1;
        }
        List<Member> party = this.parties.get(party_index);
        party.removeIf(member -> member.getId() == character_id);
        if (this.bosses[party_index] == character_id) {
            this.bosses[party_index] = party.isEmpty() ? 0 : party.get(0).getId();
        }
        return party_index;
    }

    public boolean setBoss(int character_id) {
        int party_index = findPartyIndex(character_id);
        if (party_index == -1) {
            return false;
        }
        this.bosses[party_index] = character_id;
        return true;
    }

    public boolean setMaster(int character_id) {
        if (!setBoss(character_id)) {
            return false;
        }
        this.master_index = findPartyIndex(character_id);
        return true;
    }

    public void broadcast(ServerPacket packet, int exception_id) {
        for (List<Member> party : this.parties) {
            for (Member member : party) {
                if (member.getId() == exception_id) {
                    continue;
                }
                TacosCharacter chr = findOnline(member);
                if (chr != null) {
                    chr.SendPacket(packet);
                }
            }
        }
    }
}
