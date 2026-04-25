package odin.server;

import java.awt.Point;

import odin.server.maps.MapleMap;
import odin.provider.IMapleData;
import tacos.server.map.TacosPortal;
import tacos.wz.TacosWzDataTool;

public class PortalFactory {

    private int nextDoorPortal = 0x80;

    public TacosPortal makePortal(MapleMap map, int type, IMapleData portal) {
        TacosPortal ret = new TacosPortal(type);
        loadPortal(map, ret, portal);
        return ret;
    }

    private void loadPortal(MapleMap map, TacosPortal myPortal, IMapleData portal) {
        myPortal.setName(TacosWzDataTool.getString(portal.getChildByPath("pn")));
        myPortal.setTarget(TacosWzDataTool.getString(portal.getChildByPath("tn")));
        myPortal.setTargetMapId(TacosWzDataTool.getInt(portal.getChildByPath("tm")));
        myPortal.setPosition(new Point(TacosWzDataTool.getInt(portal.getChildByPath("x")), TacosWzDataTool.getInt(portal.getChildByPath("y"))));
        String script = TacosWzDataTool.getStringPath("script", portal, null);
        if (script != null && script.equals("")) {
            script = null;
        }
        myPortal.setScriptName(script);

        if (myPortal.getType() == TacosPortal.DOOR_PORTAL) {
            myPortal.setId(nextDoorPortal);
            nextDoorPortal++;
        } else {
            //myPortal.setId(Integer.parseInt(portal.getName()));
            myPortal.setId(map.getPortals().size());
        }
        //portalState
        //if (myPortal.getName().equals("join00") && !myPortal.getPortalState()) { //ola ola, ox quiz, maplefitness
        //	if (myPortal.getTargetMapId() == 109030001 || myPortal.getTargetMapId() == 109030101 || myPortal.getTargetMapId() == 109050000 || myPortal.getTargetMapId() == 109040000) { //ola ola, ox quiz
        //		myPortal.setPortalState(false);
        //	}
        //}
    }
}
