package packet.response;

import client.MapleBeans;
import client.MapleCharacter;
import handling.MaplePacket;
import handling.channel.handler.BeanGame;
import java.util.List;
import packet.ServerPacket;
import tools.data.output.MaplePacketLittleEndianWriter;

// CField_Pachinko
public class Res_JMS_CField_Pachinko {

    // CMS v72から流用
    public static MaplePacket BeansGameMessage(int cid, int x, String laba) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_JMS_PachinkoMessage.get());
        mplew.writeInt(cid);
        // JMS v186.1 fix
        mplew.write(x);
        mplew.writeMapleAsciiString(laba);
        return mplew.getPacket();
    }

    public static MaplePacket openBeans(MapleCharacter c, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_JMS_PachinkoOpen.get());
        mplew.writeInt(c.getTama());
        mplew.write(type);
        return mplew.getPacket();
    }

    public static MaplePacket BeansZJgeiddB(int a) {
        //豆豆进洞后奖励的
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_JMS_PachinkoPlay.get());
        mplew.write(BeanGame.BeansType.奖励豆豆效果B.getType()); //类型 05   08  都是加豆豆···
        mplew.writeInt(a); //奖励豆豆的数量
        mplew.write(0); //未知效果
        return mplew.getPacket();
    }

    public static MaplePacket BeansHJG(byte type) {
        //黄金狗
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_JMS_PachinkoPlay.get());
        mplew.write(BeanGame.BeansType.黄金狗.getType()); //类型
        mplew.write(type); //改变模式
        return mplew.getPacket();
    }

    public static MaplePacket BeansJDCS(int a, int 加速旋转, int 蓝, int 绿, int 红) {
        //进洞次数 最多有7个
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_JMS_PachinkoPlay.get());
        mplew.write(BeanGame.BeansType.颜色求进洞.getType());
        mplew.write(a); //
        mplew.write(加速旋转); //快速转动
        mplew.write(蓝); // 蓝？
        mplew.write(绿); // 绿？
        mplew.write(红); // 红？
        return mplew.getPacket();
    }

    public static MaplePacket BeansJDXZ(int a, int 第一排, int 第三排, int 第二排, int 启动打怪效果, int 中奖率, int 加速旋转, boolean 关闭打击效果A, boolean 关闭打击效果B) {
        //进洞后开始旋转图片
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_JMS_PachinkoPlay.get());
        mplew.write(BeanGame.BeansType.进洞旋转.getType()); //类型
        mplew.write(a);
        mplew.write(第一排); //第一排
        mplew.write(第三排); //第三排
        mplew.write(第二排); //第二排
        mplew.write(启动打怪效果); //开启情况下出现怪物打框
        if (启动打怪效果 > 0) {
            mplew.write(中奖率); //中奖率？？%
            mplew.writeInt(0); //未知
        }
        mplew.write(加速旋转); //加速旋转
        mplew.writeBoolean(关闭打击效果A); //boolean
        mplew.writeBoolean(关闭打击效果B); //boolean
        return mplew.getPacket();
    }

    public static MaplePacket BeansZJgeidd(boolean type, int a) {
        //豆豆进洞后奖励的
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_JMS_PachinkoPlay.get());
        mplew.write(type ? BeanGame.BeansType.奖励豆豆效果.getType() : BeanGame.BeansType.奖励豆豆效果B.getType()); //类型 05   08  都是加豆豆···
        mplew.writeInt(a); //奖励豆豆的数量
        mplew.write(5);
        return mplew.getPacket();
    }

    public static MaplePacket Beans_why() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_JMS_PachinkoPlay.get());
        mplew.write(BeanGame.BeansType.未知效果.getType()); //类型
        return mplew.getPacket();
    }

    public static MaplePacket BeansUP(int ITEM) {
        //%s。请拿到凯瑟琳处确认。
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_JMS_PachinkoPlay.get());
        mplew.write(BeanGame.BeansType.领奖NPC.getType()); //类型
        mplew.writeInt(ITEM);
        return mplew.getPacket();
    }

    public static MaplePacket showBeans(List<MapleBeans> beansInfo) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_JMS_PachinkoPlay.get());
        mplew.write(BeanGame.BeansType.开始打豆豆.getType());
        mplew.write(beansInfo.size());
        for (MapleBeans bean : beansInfo) {
            mplew.writeShort(bean.getPos());
            mplew.write(bean.getType());
            mplew.writeInt(bean.getNumber());
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateBeans(int beansCount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_JMS_PachinkoUpdate.get());
        mplew.writeInt(beansCount);
        return mplew.getPacket();
    }

    public static MaplePacket 能量储存器(int beansCount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_JMS_PachinkoUpdate.get());
        mplew.writeInt(beansCount);
        return mplew.getPacket();
    }

}
