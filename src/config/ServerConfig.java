package config;

import debug.Debug;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Properties;
import packet.ClientPacket;
import packet.ServerPacket;

public class ServerConfig {

    public static long expiration_date = (Timestamp.valueOf("2027-07-07 07:00:00").getTime() + Timestamp.valueOf("2339-01-01 18:00:00").getTime()) * 10000;
    private static boolean packet_encryption = true;
    private static boolean packet_custom_encryption = false;
    private static boolean packet_old_iv = false;

    // 初期スロット数
    public static final byte DEFAULT_INV_SLOT_EQUIP = 72;
    public static final byte DEFAULT_INV_SLOT_USE = 72;
    public static final byte DEFAULT_INV_SLOT_ETC = 24;
    public static final byte DEFAULT_INV_SLOT_SETUP = 24;
    public static final byte DEFAULT_INV_SLOT_CASH = 96;
    private static final byte DEFAULT_INV_SLOT_STORAGE = 4;

    public static boolean PacketEncryptionEnabled() {
        return packet_encryption;
    }

    public static boolean CustomEncryptionEnabled() {
        return packet_custom_encryption;
    }

    public static boolean IsOldIV() {
        return packet_old_iv;
    }

    public enum Region {
        KMS,
        KMST,
        JMS,
        JMST,
        CMS,
        TWMS,
        THMS,
        GMS,
        EMS,
        BMS,
        MSEA,
        VMS,
        IMS,
        unk,
    }

    public static boolean IsKMS() {
        return GetRegion() == Region.KMS || GetRegion() == Region.KMST;
    }

    public static boolean IsKMST() {
        return GetRegion() == Region.KMST;
    }

    public static boolean IsJMS() {
        return GetRegion() == Region.JMS || GetRegion() == Region.JMST;
    }

    public static boolean IsJMST() {
        return GetRegion() == Region.JMST;
    }

    public static boolean IsCMS() {
        return GetRegion() == Region.CMS;
    }

    public static boolean IsTWMS() {
        return GetRegion() == Region.TWMS;
    }

    public static boolean IsTHMS() {
        return GetRegion() == Region.THMS;
    }

    public static boolean IsGMS() {
        return GetRegion() == Region.GMS;
    }

    public static boolean IsEMS() {
        return GetRegion() == Region.EMS;
    }

    public static boolean IsBMS() {
        return GetRegion() == Region.BMS;
    }

    public static boolean IsMSEA() {
        return GetRegion() == Region.MSEA;
    }

    public static boolean IsVMS() {
        return GetRegion() == Region.VMS;
    }

    public static boolean IsIMS() {
        return GetRegion() == Region.IMS;
    }

    private static int character_name_size = 13;
    private static boolean job_pirate = true;
    private static boolean job_KOC = true;
    private static boolean job_Aran = true;
    private static boolean job_Evan = true;
    private static boolean job_DB = true;
    private static boolean job_Resistance = true;
    private static boolean is_postBB = false;

    public static int GetCharacterNameSize() {
        return character_name_size;
    }

    public static boolean IsPostBB() {
        return is_postBB;
    }

    public static boolean IsPreBB() {
        return !IsPostBB();
    }

    public static boolean JMS146or147() {

        switch (GetRegion()) {
            case JMS: {
                if (146 <= GetVersion() && GetVersion() <= 147) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean JMS146orLater() {
        if (IsPostBB()) {
            return true;
        }

        switch (GetRegion()) {
            case JMS: {
                if (146 <= GetVersion()) {
                    return true;
                }
                return false;
            }

            case KMS: {
                if (47 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (62 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (73 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (0 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (61 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (0 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (0 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case BMS: {
                if (24 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case VMS: {
                if (35 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean JMS147orLater() {
        if (IsPostBB()) {
            return true;
        }

        switch (GetRegion()) {
            case JMS: {
                if (147 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (48 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (63 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (74 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (0 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (62 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (0 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (0 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // GMS62 (Pirate) + 3
    public static boolean GMS65orLater() {
        if (!IsGMS()) {
            return false;
        }

        if (65 <= GetVersion()) {
            return true;
        }

        return false;
    }

    // leak version
    public static boolean GMS95orLater() {
        if (!IsGMS()) {
            return false;
        }

        if (95 <= GetVersion()) {
            return true;
        }

        return false;
    }

    // only 5 jobs
    // シグナス実装前まではほぼ変わらないはずなのでバージョンの誤差は多少あっても問題ない
    public static boolean JMS164orLater() {
        if (IsPostBB()) {
            return true;
        }

        switch (GetRegion()) {
            case JMS: {
                if (164 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                // v2.66
                if (65 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (73 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (94 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (87 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (72 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (54 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (100 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case VMS: {
                return false;
            }
        }
        return false;
    }

    // Knights of Cygnus update
    public static boolean JMS165orLater() {
        if (IsPostBB()) {
            return true;
        }

        switch (GetRegion()) {
            case JMS: {
                if (165 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (67 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (74 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (96 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (87 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (73 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (55 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (100 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case VMS: {
                return false;
            }
        }
        return false;
    }

    public static boolean TWMS74orLater() {
        if (!IsTWMS()) {
            return false;
        }
        if (74 <= GetVersion()) {
            return true;
        }
        return false;
    }

    public static boolean TWMS94orLater() {
        if (!IsTWMS()) {
            return false;
        }
        if (94 <= GetVersion()) {
            return true;
        }
        return false;
    }

    public static boolean GMS72orLater() {
        if (!IsGMS()) {
            return false;
        }
        if (72 <= GetVersion()) {
            return true;
        }
        return false;
    }

    public static boolean GMS73orLater() {
        if (!IsGMS()) {
            return false;
        }
        if (73 <= GetVersion()) {
            return true;
        }
        return false;
    }

    // under JMS v180
    public static boolean KMS84orEarlier() {
        if (IsPostBB()) {
            return false;
        }

        switch (GetRegion()) {
            case KMS: {
                if (GetVersion() <= 84) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS84orLater() {
        switch (GetRegion()) {
            case KMS: {
                if (84 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // stable pre bb
    public static boolean JMS180orLater() {
        if (IsPostBB()) {
            return true;
        }

        switch (GetRegion()) {
            case JMS: {
                if (180 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (92 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (85 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (121 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (87 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (91 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (70 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (100 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS95orEarlier() {
        if (IsPostBB()) {
            return false;
        }

        switch (GetRegion()) {
            case KMS: {
                if (GetVersion() <= 95) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS92orLater() {
        switch (GetRegion()) {
            case KMS: {
                if (92 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS95orLater() {
        if (IsPostBB()) {
            return true;
        }

        switch (GetRegion()) {
            case KMS: {
                if (95 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean GMS83orLater() {
        switch (GetRegion()) {
            case GMS: {
                if (83 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean GMS91orLater() {
        switch (GetRegion()) {
            case GMS: {
                if (91 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // pre bb with potential
    public static boolean JMS186orLater() {
        if (IsPostBB()) {
            return true;
        }

        switch (GetRegion()) {
            case JMS: {
                if (186 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (100 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (85 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (121 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (87 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (91 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (70 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (100 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS114orLater() {
        // ?
        if (IsPostBB()) {
            return true;
        }

        switch (GetRegion()) {
            case KMS: {
                if (114 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean TWMS121orLater() {
        switch (GetRegion()) {
            case TWMS: {
                if (121 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // near Chaos update
    public static boolean JMS194orLater() {
        if (!IsPostBB()) {
            return false;
        }

        switch (GetRegion()) {
            case JMS: {
                if (194 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case JMST: {
                if (110 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (114 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case KMST: {
                if (391 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (76 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // JMS194 <= JMST110 <= JMS200
    public static boolean JMST110() {
        if (!IsPostBB()) {
            return false;
        }

        switch (GetRegion()) {
            case JMST: {
                if (110 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS118orLater() {
        switch (GetRegion()) {
            case KMS: {
                if (118 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case KMST: {
                if (391 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS119orLater() {
        switch (GetRegion()) {
            case KMS: {
                if (119 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case KMST: {
                if (391 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS121orLater() {
        switch (GetRegion()) {
            case KMS: {
                if (121 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case KMST: {
                if (391 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS127orLater() {
        switch (GetRegion()) {
            case KMS: {
                if (127 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case KMST: {
                if (391 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS118() {
        switch (GetRegion()) {
            case KMS: {
                if (118 == GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMST391() {
        switch (GetRegion()) {
            case KMST: {
                if (391 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS138orLater() {
        switch (GetRegion()) {
            case KMS: {
                if (138 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            case KMST: {
                if (391 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS148orLater() {
        switch (GetRegion()) {
            case KMS: {
                if (148 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // Phantom version
    public static boolean KMS149orLater() {
        switch (GetRegion()) {
            case KMS: {
                if (149 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS160orLater() {
        switch (GetRegion()) {
            case KMS: {
                if (160 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // Sengoku update
    public static boolean JMS302orLater() {
        switch (GetRegion()) {
            case JMS: {
                if (302 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // Angelic Buster version
    public static boolean JMS308orLater() {
        switch (GetRegion()) {
            case JMS: {
                if (308 <= GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // test version of potential system
    public static boolean IsPrePotentialVersion() {
        switch (GetRegion()) {
            case JMS: {
                if (184 <= GetVersion() && GetVersion() <= 185) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (95 == GetVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // around Pirate update
    public static boolean JMS131orEarlier() {
        if (IsPostBB()) {
            return false;
        }

        switch (GetRegion()) {
            case JMS: {
                // not checked v132 to v163
                if (GetVersion() <= 131) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // Knights of Cygnus update
    // todo : replace to orlater func
    public static boolean JMS165orEarlier() {
        if (IsPostBB()) {
            return false;
        }

        switch (GetRegion()) {
            case JMS: {
                if (GetVersion() <= 165) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (GetVersion() <= 94) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (GetVersion() <= 73) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (GetVersion() <= 55) {
                    return true;
                }
                return false;
            }
            case BMS: {
                if (GetVersion() <= 24) {
                    return true;
                }
                return false;
            }
            case VMS: {
                if (GetVersion() <= 35) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean SetContentFlag() {
        switch (GetRegion()) {
            case KMS: {
                if (101 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case KMST: {
                if (317 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case JMS: {
                if (GetVersion() <= 141) {
                    packet_old_iv = true;
                }
                if (187 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case JMST: {
                // v110
                is_postBB = true;
                return true;
            }
            case CMS: {
                if (87 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case TWMS: {
                if (94 <= GetVersion()) {
                    character_name_size = 15;
                }
                if (123 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case THMS: {
                if (90 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case GMS: {
                if (93 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case EMS: {
                if (73 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case MSEA: {
                if (105 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case BMS:
            case VMS: {
                is_postBB = false;
                return true;
            }
            case IMS: {
                is_postBB = true;
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // codepage
    public static boolean utf8 = false;
    public static Charset codepage_ascii;
    public static Charset codepage_utf8;

    // Version
    private static Region region_type = Region.JMS;
    private static byte region_number = 3; // JMS
    public static int version = 186;
    private static byte version_sub = 1;

    public static byte GetRegionNumber() {
        return region_number;
    }

    public static Region GetRegion() {
        return region_type;
    }

    public static int GetVersion() {
        return version;
    }

    public static int GetSubVersion() {
        return version_sub;
    }

    public static String GetRegionName() {
        return "" + region_type;
    }

    public static boolean SetRegion(String region_name) {
        switch (region_name) {
            case "KMS": {
                region_type = Region.KMS;
                region_number = 1;
                character_name_size = 13;
                return true;
            }
            case "KMST": {
                region_type = Region.KMST;
                region_number = 2;
                character_name_size = 13;
                return true;
            }
            case "JMS": {
                region_type = Region.JMS;
                region_number = 3;
                character_name_size = 13;
                return true;
            }
            case "JMST": {
                region_type = Region.JMST;
                region_number = 3;
                character_name_size = 13;
                return true;
            }
            case "CMS": {
                region_type = Region.CMS;
                region_number = 4;
                packet_custom_encryption = true;
                character_name_size = 13;
                return true;
            }
            case "TWMS": {
                region_type = Region.TWMS;
                region_number = 6;
                character_name_size = 13;
                return true;
            }
            case "MSEA": {
                region_type = Region.MSEA;
                region_number = 7;
                packet_custom_encryption = true;
                character_name_size = 13;
                return true;
            }
            case "GMS": {
                region_type = Region.GMS;
                region_number = 8;
                packet_custom_encryption = true;
                character_name_size = 13;
                return true;
            }
            case "EMS": {
                region_type = Region.EMS;
                region_number = 9;
                packet_custom_encryption = true;
                character_name_size = 13;
                return true;
            }
            case "BMS": {
                region_type = Region.BMS;
                region_number = 9;
                packet_custom_encryption = true;
                character_name_size = 13;
                return true;
            }
            case "THMS": {
                region_type = Region.THMS;
                region_number = 7;
                packet_custom_encryption = true;
                return true;
            }
            case "VMS": {
                region_type = Region.VMS;
                region_number = 7;
                packet_custom_encryption = true;
                character_name_size = 16;
                return true;
            }
            case "IMS": {
                region_type = Region.IMS;
                region_number = 1;
                //packet_custom_encryption = true;
                character_name_size = 13;
                return true;
            }
            default: {
                break;
            }
        }

        region_type = Region.unk;
        return false;
    }

    public static void SetVersion(int ver1, int ver2) {
        version = ver1;
        version_sub = (byte) ver2;
    }
    public static String wz_path, script_path;

    // コマンドライン引数からファイルパスを取得
    public static void SetDataPath() {
        wz_path = System.getProperty("riresaba.path.wz");
        script_path = System.getProperty("riresaba.path.script");
    }

    // Database
    public static String database_url, database_user, database_password;
    // Login Server
    public static int login_server_port, login_server_userlimit;
    public static boolean login_server_antihack;
    // Game Server
    public static int game_server_channels, game_server_DEFAULT_PORT, game_server_flags;
    public static int game_server_expRate, game_server_mesoRate, game_server_dropRate;
    public static String game_server_serverName, game_server_serverMessage, game_server_event, game_server_events;
    public static boolean game_server_adminOnly;
    public static boolean game_server_enable_hammer, game_server_enable_EE, game_server_enable_potential, game_server_enable_mphp;
    public static boolean game_server_custom, game_server_god_equip, game_server_disable_scroll_boom, game_server_disable_scroll_failure, game_server_disable_star_consuming, game_server_disable_stone_consuming, game_server_disable_boss_timer;
    // Cash Shop
    public static int cash_shop_server_port;
    // Maple Trade Space Server
    public static int maple_trade_space_server_port;
    // Test Game Server
    public static int test_game_server_channels, test_game_server_DEFAULT_PORT, test_game_server_flags;
    public static String test_game_server_serverName, test_game_server_serverMessage, test_game_server_event;
    // Game Server - Pachinko
    public static String 豆豆装备[], 豆豆坐骑[], 消耗品[], 黄金狗几率[], 小白怪[], 大白怪[], 紫色怪[], 粉色怪[], 飞侠[], 海盗[], 法师[], 战士[], 弓箭手[], 女皇[], 白怪奖励[], 色怪奖励[], 五职业奖励[], 女皇奖励[];
    public static int 海洋帽子几率, 力度搞假, 豆豆奖励范围;

    // キャラクター作成後の最初のMapID
    public static int first_mapid = 910000000; // フリーマーケット入口
    // 存在しないMapIDへ飛んでしまった場合に強制的にワープさせる場所
    public static int error_mapid = 800000000; // キノコ神社

    // propertiesファイルの読み込み
    public static void SetProperty() {
        Properties DataBase = ReadPropertyFile("properties/database.properties");
        {
            // jdbc:mysql://127.0.0.1:3306/jms_v186?autoReconnect=true&characterEncoding=utf8
            database_url = DataBase.getProperty("database.url");
            if (database_url.isEmpty()) {
                String database_host = DataBase.getProperty("database.host");
                String database_port = DataBase.getProperty("database.port");
                database_url = "jdbc:mysql://" + database_host + ":" + database_port + "/" + GetRegionName() + "_v" + version + "?autoReconnect=true&characterEncoding=utf8";
            }

            database_user = DataBase.getProperty("database.user");
            database_password = DataBase.getProperty("database.password");
        }

        // test
        Properties TestConfig = ReadPropertyFile("properties/test.properties");
        {
            // codepage
            utf8 = Boolean.parseBoolean(TestConfig.getProperty("codepage.use_utf8"));
            codepage_ascii = Charset.forName(TestConfig.getProperty("codepage.ascii"));
            codepage_utf8 = Charset.forName(TestConfig.getProperty("codepage.utf8"));
            // map
            first_mapid = Integer.parseInt(TestConfig.getProperty("config.first_mapid"));
            error_mapid = Integer.parseInt(TestConfig.getProperty("config.error_mapid"));
            // debug
            DebugConfig.log_packet = Boolean.parseBoolean(TestConfig.getProperty("debug.show_packet"));
            DebugConfig.log_debug = Boolean.parseBoolean(TestConfig.getProperty("debug.show_debug_log"));
            DebugConfig.log_admin = Boolean.parseBoolean(TestConfig.getProperty("debug.show_admin_log"));
            DebugConfig.starter_set = Boolean.parseBoolean(TestConfig.getProperty("debug.starter_set"));
            DebugConfig.GM = Boolean.parseBoolean(TestConfig.getProperty("debug.gm_mode"));
            DebugConfig.open_debug_ui = Boolean.parseBoolean(TestConfig.getProperty("debug.admin_ui"));
        }

        Properties LoginServer = ReadPropertyFile("properties/login.properties");
        {

            login_server_port = Integer.parseInt(LoginServer.getProperty("server.port"));
            login_server_userlimit = Integer.parseInt(LoginServer.getProperty("server.userlimit"));
            login_server_antihack = Boolean.parseBoolean(LoginServer.getProperty("server.antihack"));
        }

        Properties GameServer = ReadPropertyFile("properties/kaede.properties");
        {
            game_server_channels = Integer.parseInt(GameServer.getProperty("server.channels"));
            game_server_DEFAULT_PORT = Short.parseShort(GameServer.getProperty("server.port"));
            game_server_expRate = Integer.parseInt(GameServer.getProperty("server.rate.exp"));
            game_server_mesoRate = Integer.parseInt(GameServer.getProperty("server.rate.meso"));
            game_server_dropRate = Integer.parseInt(GameServer.getProperty("server.rate.drop"));
            game_server_serverMessage = GameServer.getProperty("server.message");
            game_server_serverName = GameServer.getProperty("server.name");
            game_server_flags = Integer.parseInt(GameServer.getProperty("server.flags"));
            game_server_adminOnly = Boolean.parseBoolean(GameServer.getProperty("server.admin", "false"));
            game_server_events = GameServer.getProperty("server.events");
            game_server_event = GameServer.getProperty("server.event");

            game_server_enable_hammer = Boolean.parseBoolean(GameServer.getProperty("server.enable.hammer"));
            game_server_enable_EE = Boolean.parseBoolean(GameServer.getProperty("server.enable.ee"));
            game_server_enable_potential = Boolean.parseBoolean(GameServer.getProperty("server.enable.potential"));
            game_server_enable_mphp = Boolean.parseBoolean(GameServer.getProperty("server.enable.mphp"));

            game_server_custom = Boolean.parseBoolean(GameServer.getProperty("server.custom"));
            game_server_god_equip = Boolean.parseBoolean(GameServer.getProperty("server.custom.god_equip"));
            game_server_disable_scroll_boom = Boolean.parseBoolean(GameServer.getProperty("server.custom.disable.scroll_boom"));
            game_server_disable_scroll_failure = Boolean.parseBoolean(GameServer.getProperty("server.custom.disable.scroll_failure"));
            game_server_disable_star_consuming = Boolean.parseBoolean(GameServer.getProperty("server.custom.disable.star_consuming"));
            game_server_disable_stone_consuming = Boolean.parseBoolean(GameServer.getProperty("server.custom.disable.stone_consuming"));
            game_server_disable_boss_timer = Boolean.parseBoolean(GameServer.getProperty("server.custom.disable.boss_timer"));

        }

        Properties CashShopServer = ReadPropertyFile("properties/shop.properties");
        {
            cash_shop_server_port = Integer.parseInt(CashShopServer.getProperty("server.port"));
            // Port共有
            maple_trade_space_server_port = cash_shop_server_port;
        }

        Properties TestGameServer = ReadPropertyFile("properties/momiji.properties");
        {
            test_game_server_channels = Integer.parseInt(TestGameServer.getProperty("server.channels"));
            test_game_server_DEFAULT_PORT = Short.parseShort(TestGameServer.getProperty("server.port"));
            test_game_server_serverMessage = TestGameServer.getProperty("server.message");
            test_game_server_serverName = TestGameServer.getProperty("server.name");
            test_game_server_flags = Integer.parseInt(TestGameServer.getProperty("server.flags"));
            test_game_server_event = TestGameServer.getProperty("server.event");
        }

        Properties Pachinko = ReadPropertyFile("properties/beans.properties");
        {
            豆豆装备 = Pachinko.getProperty("ddzb").split(",");
            豆豆坐骑 = Pachinko.getProperty("ddzq").split(",");
            消耗品 = Pachinko.getProperty("xhp").split(",");
            海洋帽子几率 = Integer.parseInt(Pachinko.getProperty("hymzjl"));
            黄金狗几率 = Pachinko.getProperty("hjgjl").split(",");
            大白怪 = Pachinko.getProperty("dbg").split(",");
            小白怪 = Pachinko.getProperty("xbg").split(",");
            紫色怪 = Pachinko.getProperty("zsg").split(",");
            粉色怪 = Pachinko.getProperty("fsg").split(",");
            飞侠 = Pachinko.getProperty("fx").split(",");
            海盗 = Pachinko.getProperty("hd").split(",");
            法师 = Pachinko.getProperty("fs").split(",");
            战士 = Pachinko.getProperty("zs").split(",");
            弓箭手 = Pachinko.getProperty("gjs").split(",");
            女皇 = Pachinko.getProperty("nh").split(",");
            白怪奖励 = Pachinko.getProperty("bgjl").split(",");
            色怪奖励 = Pachinko.getProperty("sgjl").split(",");
            五职业奖励 = Pachinko.getProperty("wzyjl").split(",");
            女皇奖励 = Pachinko.getProperty("nhjl").split(",");
            力度搞假 = Integer.parseInt(Pachinko.getProperty("ldgj"));
            豆豆奖励范围 = Integer.parseInt(Pachinko.getProperty("ddjlfw"));
        }

        Properties ServerPacketHeader = ReadPropertyFile("properties/packet/" + GetRegionName() + "_v" + GetVersion() + "_ServerPacket.properties");
        if (ServerPacketHeader != null) {
            ServerPacket.init(ServerPacketHeader);

            Debug.DebugLog("[SP]");
            for (ServerPacket.Header header : ServerPacket.Header.values()) {
                int val = header.get();
                if (val != -1) {
                    Debug.DebugLog(String.format("@%04X", val) + " : " + header.name());
                }
            }
        }

        Properties ClientPacketHeader = ReadPropertyFile("properties/packet/" + GetRegionName() + "_v" + GetVersion() + "_ClientPacket.properties");
        if (ClientPacketHeader != null) {
            ClientPacket.Load(ClientPacketHeader);

            Debug.DebugLog("[CP]");
            for (ClientPacket.Header header : ClientPacket.Header.values()) {
                int val = header.Get();
                if (val != -1) {
                    Debug.DebugLog(String.format("@%04X", val) + " : " + header.name());
                }
            }
        }
    }

    public static void ReloadHeader() {
        Properties ServerPacketHeader = ReadPropertyFile("properties/packet/" + GetRegionName() + "_v" + GetVersion() + "_ServerPacket.properties");
        if (ServerPacketHeader != null) {
            ServerPacket.reset();
            ServerPacket.init(ServerPacketHeader);
            Debug.InfoLog("ServerPacket is reloaded!");
        }
        Properties ClientPacketHeader = ReadPropertyFile("properties/packet/" + GetRegionName() + "_v" + GetVersion() + "_ClientPacket.properties");
        if (ClientPacketHeader != null) {
            ClientPacket.Reset();
            ClientPacket.Load(ClientPacketHeader);
            Debug.InfoLog("ClientPacket is reloaded!");
        }
    }

    public static boolean IsGMTestMode() {
        return DebugConfig.GM;
    }

    public static Properties ReadPropertyFile(final String path) {
        final Properties p = new Properties();

        FileReader fr;
        try {
            fr = new FileReader(path);
            p.load(fr);
            fr.close();
        } catch (IOException e) {
            Debug.ErrorLog("設定ファイルが見つかりません (" + path + ")");
            return null;
        }

        return p;
    }

}
