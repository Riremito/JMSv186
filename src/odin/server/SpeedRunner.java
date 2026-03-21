package odin.server;

import tacos.database.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import odin.server.maps.SpeedRunType;
import tacos.odin.OdinPair;

public class SpeedRunner {

    private static SpeedRunner instance = new SpeedRunner();
    private final Map<SpeedRunType, OdinPair<String, Map<Integer, String>>> speedRunData;

    private SpeedRunner() {
        speedRunData = new EnumMap<>(SpeedRunType.class);
    }

    public static final SpeedRunner getInstance() {
        return instance;
    }

    public final OdinPair<String, Map<Integer, String>> getSpeedRunData(SpeedRunType type) {
        return speedRunData.get(type);
    }

    public final void addSpeedRunData(SpeedRunType type, OdinPair<StringBuilder, Map<Integer, String>> mib) {
        speedRunData.put(type, new OdinPair<>(mib.getLeft().toString(), mib.getRight()));
    }

    public final void removeSpeedRunData(SpeedRunType type) {
        speedRunData.remove(type);
    }

    public final void loadSpeedRuns() throws SQLException {
        if (speedRunData.size() > 0) {
            return;
        }
        for (SpeedRunType type : SpeedRunType.values()) {
            loadSpeedRunData(type);
        }
    }

    public final void loadSpeedRunData(SpeedRunType type) throws SQLException {
        StringBuilder ret;
        Map<Integer, String> rett;
        int rank;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM speedruns WHERE type = ? ORDER BY time LIMIT 25") //or should we do less
        ) {
            ps.setString(1, type.name());
            ret = new StringBuilder("#rThese are the speedrun times for " + type.name() + ".#k\r\n\r\n");
            rett = new LinkedHashMap<>();
            try (ResultSet rs = ps.executeQuery()) {
                rank = 1;
                while (rs.next()) {
                    addSpeedRunData(ret, rett, rs.getString("members"), rs.getString("leader"), rank, rs.getString("timestring"));
                    rank++;
                }
            }
        }
        if (rank != 1) {
            speedRunData.put(type, new OdinPair<>(ret.toString(), rett));
        }
    }

    public final OdinPair<StringBuilder, Map<Integer, String>> addSpeedRunData(StringBuilder ret, Map<Integer, String> rett, String members, String leader, int rank, String timestring) {
        StringBuilder rettt = new StringBuilder();

        String[] membrz = members.split(",");
        rettt.append("#bThese are the squad members of " + leader + "'s squad at rank " + rank + ".#k\r\n\r\n");
        for (int i = 0; i < membrz.length; i++) {
            rettt.append("#r#e");
            rettt.append(i + 1);
            rettt.append(".#n ");
            rettt.append(membrz[i]);
            rettt.append("#k\r\n");
        }
        rett.put(rank, rettt.toString());
        ret.append("#b");
        if (membrz.length > 1) {
            ret.append("#L");
            ret.append(rank);
            ret.append("#");
        }
        ret.append("Rank #e");
        ret.append(rank);
        ret.append("#n#k : ");
        ret.append(leader);
        ret.append(", in ");
        ret.append(timestring);
        if (membrz.length > 1) {
            ret.append("#l");
        }
        ret.append("\r\n");
        return new OdinPair<>(ret, rett);
    }
}
