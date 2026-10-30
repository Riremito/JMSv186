/*
 * Copyright (C) 2025 Riremito
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
import java.util.HashMap;
import lombok.Getter;
import odin.client.inventory.Equip;
import tacos.config.Content;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class CharacterWz extends WzXML {

    public CharacterWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Character" : "Character.wz");
    }

    public MapleData getItemData(int id) {
        int item_type = id / 1000000;
        if (2 <= item_type) {
            return null;
        }

        String target_img_name = String.format("%08d.img", id);
        for (MapleDataDirectoryEntry mdde : rootDirectory.getSubDirectories()) {
            for (MapleDataEntity mdfe : mdde.getFiles()) {
                if (mdfe.getName().equals(target_img_name)) {
                    MapleData md_equip = getData(mdde.getName() + "/" + mdfe.getName());
                    if (md_equip == null) {
                        DebugLogger.XmlLog("getItemData : invalid equip data, " + id);
                        return null;
                    }
                    return md_equip;
                }
            }
        }

        DebugLogger.XmlLog("getItemData : not found, " + id);
        return null;
    }

    @Getter
    public class EquipInfo {

        private int tuc;
        private int incSTR;
        private int incDEX;
        private int incINT;
        private int incLUK;
        private int incMHP;
        private int incMMP;
        private int incPAD;
        private int incMAD;
        private int incPDD;
        private int incMDD;
        private int incACC;
        private int incEVA;
        private int incCraft;
        private int incSpeed;
        private int incJump;
        private int durability;
        private int incMHPr;
        private int incMMPr;
        private int cash;
        private int only;
        private int tradeAvailable;
        private int tradeBlock;
        private int price;
        private int reqJob;
        private int reqLevel;
        private int reqSTR;
        private int reqDEX;
        private int reqINT;
        private int reqLUK;
        private int reqPOP;
        private int charmEXP;
        private int slotMax;
        private int quest;
        private int accountSharable;
        private int pickUpBlock;
        private int notSale;
        private int equipTradeBlock;
        private int setItemID;
        private boolean canLevel;
        private int elemDefault;
        private int incRMAS;
        private int incRMAF;
        private int incRMAL;
        private int incRMAI;
        // islot
        // vslot
    }

    private final HashMap<Integer, EquipInfo> equip_infos = new HashMap<>();

    public EquipInfo getEquipInfo(int equip_id) {
        EquipInfo ret = this.equip_infos.get(equip_id);
        if (ret == null) {
            MapleData equip_img = getItemData(equip_id);
            if (equip_img != null) {
                MapleData info = equip_img.getChildByPath("info");
                if (info != null) {
                    ret = new EquipInfo();
                    ret.tuc = info.getInt("tuc");
                    ret.incSTR = info.getInt("incSTR");
                    ret.incDEX = info.getInt("incDEX");
                    ret.incINT = info.getInt("incINT");
                    ret.incLUK = info.getInt("incLUK");
                    ret.incMHP = info.getInt("incMHP");
                    ret.incMMP = info.getInt("incMMP");
                    ret.incPAD = info.getInt("incPAD");
                    ret.incMAD = info.getInt("incMAD");
                    ret.incPDD = info.getInt("incPDD");
                    ret.incMDD = info.getInt("incMDD");
                    ret.incACC = info.getInt("incACC");
                    ret.incEVA = info.getInt("incEVA");
                    ret.incCraft = info.getInt("incCraft");
                    ret.incSpeed = info.getInt("incSpeed");
                    ret.incJump = info.getInt("incJump");
                    ret.durability = info.getInt("durability", -1);
                    ret.incMHPr = info.getInt("incMHPr");
                    ret.incMMPr = info.getInt("incMMPr");
                    ret.cash = info.getInt("cash");
                    ret.reqJob = info.getInt("reqJob");
                    ret.reqLevel = info.getInt("reqLevel");
                    ret.reqSTR = info.getInt("reqSTR");
                    ret.reqDEX = info.getInt("reqDEX");
                    ret.reqINT = info.getInt("reqINT");
                    ret.reqLUK = info.getInt("reqLUK");
                    ret.reqPOP = info.getInt("reqPOP");
                    ret.slotMax = info.getInt("slotMax", 1);
                    ret.price = info.getInt("price", -1);
                    ret.only = info.getInt("only");
                    ret.quest = info.getInt("quest");
                    ret.tradeBlock = info.getInt("tradeBlock");
                    ret.accountSharable = info.getInt("accountSharable");
                    ret.pickUpBlock = info.getInt("pickUpBlock");
                    ret.notSale = info.getInt("notSale");
                    ret.equipTradeBlock = info.getInt("equipTradeBlock");
                    ret.setItemID = info.getInt("setItemID");
                    ret.canLevel = info.getChildByPath("level") != null;
                    // magic weapon
                    ret.elemDefault = info.getInt("elemDefault", 100);
                    ret.incRMAS = info.getInt("incRMAS", 100); // Poison
                    ret.incRMAF = info.getInt("incRMAF", 100); // Fire
                    ret.incRMAL = info.getInt("incRMAL", 100); // Lightning
                    ret.incRMAI = info.getInt("incRMAI", 100); // Ice
                }
                this.equip_infos.put(equip_id, ret);
            }
        }
        return ret;
    }

    public Equip getEquipById(int equip_id) {
        EquipInfo ed = getEquipInfo(equip_id);
        Equip ret = new Equip(equip_id, (byte) 0, -1, (byte) 0);

        ret.setQuantity((short) 1);

        if (ed == null) {
            DebugLogger.ErrorLog("getEquipById : EquipData is null.");
            return ret;
        }

        ret.setRUC(ed.getTuc());
        ret.setCUC(0);
        ret.setSTR(ed.incSTR);
        ret.setDEX(ed.incDEX);
        ret.setINT(ed.incINT);
        ret.setLUK(ed.incLUK);
        ret.setMaxHP(ed.incMHP);
        ret.setMaxMP(ed.incMMP);
        ret.setPAD(ed.incPAD);
        ret.setMAD(ed.incMAD);
        ret.setPDD(ed.incPDD);
        ret.setMDD(ed.incMDD);
        ret.setACC(ed.incACC);
        ret.setEVA(ed.incEVA);
        ret.setCraft(ed.incCraft);
        ret.setSpeed(ed.incSpeed);
        ret.setJump(ed.incJump);
        ret.setDurability(ed.durability);
        ret.setMaxHPr(ed.incMHPr);
        ret.setMaxMPr(ed.incMMPr);
        if (ed.canLevel) {
            // 成長装備はLv1から
            ret.setLevelLevel(1);
        }
        return ret;
    }

    @Getter
    public class EquipInfoLevel {

        private int exp; // no idea.
        private int incSTRMax;
        private int incSTRMin;
        private int incDEXMax;
        private int incDEXMin;
        private int incINTMax;
        private int incINTMin;
        private int incLUKMax;
        private int incLUKMin;
        private int incMHPMax;
        private int incMHPMin;
        private int incMMPMax;
        private int incMMPMin;
        private int incPADMax;
        private int incPADMin;
        private int incMADMax;
        private int incMADMin;
        private int incPDDMax;
        private int incPDDMin;
        private int incMDDMax;
        private int incMDDMin;
        private int incACCMax;
        private int incACCMin;
        private int incEVAMax;
        private int incEVAMin;
        private int incSpeedMax;
        private int incSpeedMin;
        private int incJumpMax;
        private int incJumpMin;
    }

    private final HashMap<Integer, ArrayList<EquipInfoLevel>> equip_info_levels = new HashMap<>();

    public ArrayList<EquipInfoLevel> getEquipLevelInfo(int item_id) {
        ArrayList<EquipInfoLevel> growth = this.equip_info_levels.get(item_id);
        if (growth != null) {
            return growth;
        }
        growth = new ArrayList<>();
        MapleData equip_img = getItemData(item_id);
        if (equip_img != null) {
            MapleData info_level = equip_img.getChildByPath("info/level/info");
            if (info_level != null) {
                for (MapleData level_node : info_level.getChildren()) {
                    EquipInfoLevel eil = new EquipInfoLevel();
                    eil.exp = level_node.getInt("exp");
                    eil.incSTRMax = level_node.getInt("incSTRMax");
                    eil.incSTRMin = level_node.getInt("incSTRMin");
                    eil.incDEXMax = level_node.getInt("incDEXMax");
                    eil.incDEXMin = level_node.getInt("incDEXMin");
                    eil.incINTMax = level_node.getInt("incINTMax");
                    eil.incINTMin = level_node.getInt("incINTMin");
                    eil.incLUKMax = level_node.getInt("incLUKMax");
                    eil.incLUKMin = level_node.getInt("incLUKMin");
                    eil.incMHPMax = level_node.getInt("incMHPMax");
                    eil.incMHPMin = level_node.getInt("incMHPMin");
                    eil.incMMPMax = level_node.getInt("incMMPMax");
                    eil.incMMPMin = level_node.getInt("incMMPMin");
                    eil.incPADMax = level_node.getInt("incPADMax");
                    eil.incPADMin = level_node.getInt("incPADMin");
                    eil.incMADMax = level_node.getInt("incMADMax");
                    eil.incMADMin = level_node.getInt("incMADMin");
                    eil.incPDDMax = level_node.getInt("incPDDMax");
                    eil.incPDDMin = level_node.getInt("incPDDMin");
                    eil.incMDDMax = level_node.getInt("incMDDMax");
                    eil.incMDDMin = level_node.getInt("incMDDMin");
                    eil.incACCMax = level_node.getInt("incACCMax");
                    eil.incACCMin = level_node.getInt("incACCMin");
                    eil.incEVAMax = level_node.getInt("incEVAMax");
                    eil.incEVAMin = level_node.getInt("incEVAMin");
                    eil.incSpeedMax = level_node.getInt("incSpeedMax");
                    eil.incSpeedMin = level_node.getInt("incSpeedMin");
                    eil.incJumpMax = level_node.getInt("incJumpMax");
                    eil.incJumpMin = level_node.getInt("incJumpMin");
                    growth.add(eil);
                }
            }
        }
        this.equip_info_levels.put(item_id, growth);
        return growth;
    }
}
