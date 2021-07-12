// ビジター マンホールくん
// test

// 整形
var face_list_male = Array(
	20000,
	20001,
	20002,
	20003,
	20004,
	20005,
	20006,
	20007,
	20008,
	20009,
	20010,
	20011,
	20012,
	20013,
	20014,
	20016,
	20017,
	20018,
	20019,
	20020,
	20021,
	20022,
	20024,
	20025,
	20027,
	20028,
	20029,
	20030,
	20031,
	20032,
	20033,
	20035,
	20038,
	20040
);

var face_list_female = Array(
	21000,
	21001,
	21002,
	21003,
	21004,
	21005,
	21006,
	21007,
	21008,
	21009,
	21010,
	21011,
	21012,
	21013,
	21014,
	21016,
	21017,
	21018,
	21019,
	21020,
	21021,
	21023,
	21024,
	21026,
	21027,
	21028,
	21029,
	21030,
	21031,
	21033,
	21036,
	21038
);

function Surgery_male() {
	var my_face = cm.getPlayerStat("FACE");
	var my_face_gender = (my_face >= 21000) ? true : false;
	var my_face_id = my_face % 100;
	var my_face_color = (my_face - (my_face_gender ? 21000 : 20000) - my_face_id);

	for (i = 0; i < face_list_male.length; i++) {
		var face_id = face_list_male[i] % 100;
		var face_color = (face_list_male[i] - 20000 - face_id);
		face_list_male[i] -= face_color;
	}

	for (i = 0; i < face_list_male.length; i++) {
		face_list_male[i] += my_face_color;
	}

	cm.sendStyle("整形（男）", face_list_male);
}

function Surgery_female() {
	var my_face = cm.getPlayerStat("FACE");
	var my_face_gender = (my_face >= 21000) ? true : false;
	var my_face_id = my_face % 100;
	var my_face_color = (my_face - (my_face_gender ? 21000 : 20000) - my_face_id);

	for (i = 0; i < face_list_female.length; i++) {
		var face_id = face_list_female[i] % 100;
		var face_color = (face_list_female[i] - 21000 - face_id);
		face_list_female[i] -= face_color;
	}

	// 目の色白 + 女だけバグあり
	if (my_face_color != 800) {
		for (i = 0; i < face_list_female.length; i++) {
			face_list_female[i] += my_face_color;
		}
	}
	cm.sendStyle("整形（女）", face_list_female);
}

// 調髪
var hair_list_male = Array(
	// 男
	30000,
	//30010, // 特殊
	30020,
	30030,
	30040,
	30050,
	30060,
	//30070, // 特殊
	//30080, // 特殊
	//30090, // 特殊
	30100,
	30110,
	30120,
	30130,
	30140,
	30150,
	30160,
	30170,
	30180,
	30190,
	30200,
	30210,
	30220,
	30230,
	30240,
	30250,
	30260,
	30270,
	30280,
	30290,
	30300,
	30310,
	30320,
	30330,
	30340,
	30350,
	30360,
	30370,
	30400,
	30410,
	30420,
	30430,
	30440,
	30450,
	30460,
	30470,
	30480,
	30490,
	30510,
	30520,
	30530,
	30540,
	30550,
	30560,
	30570,
	30580,
	30590,
	30600,
	30610,
	30620,
	30630,
	30640,
	30650,
	30660,
	30670,
	30680,
	30690,
	30700,
	30710,
	30720,
	30730,
	30740,
	30750,
	30760,
	30770,
	30780,
	30790,
	30800,
	30810,
	30820,
	30830,
	30840,
	30850,
	30860,
	30870,
	30880,
	30890,
	30900,
	30910,
	30920,
	30930,
	30940,
	30950,
	30960,
	30970,
	30980,
	30990
);

var hair_list_female = Array(
	31000,
	31010,
	31020,
	31030,
	31040,
	31050,
	31060,
	31070,
	31080,
	31090,
	31100,
	31110,
	31120,
	31130,
	31140,
	31150,
	31160,
	31170,
	31180,
	31190,
	31200,
	31210,
	31220,
	31230,
	31240,
	31250,
	31260,
	31270,
	31280,
	31290,
	31300,
	31310,
	31320,
	31330,
	31340,
	31350,
	31400,
	31410,
	31420,
	31430,
	31440,
	31450,
	31460,
	31470,
	31480,
	31490,
	31510,
	31520,
	31530,
	31540,
	31550,
	31560,
	31570,
	31580,
	31590,
	31600,
	31610,
	31620,
	31630,
	31640,
	31650,
	31660,
	31670,
	31680,
	31690,
	31700,
	31710,
	31720,
	31730,
	31740,
	31750,
	31760,
	31770,
	31780,
	31790,
	31800,
	31810,
	31820,
	31830,
	31840,
	31850,
	31860,
	31870,
	31880,
	31890,
	31900,
	31910,
	31920,
	31930,
	31940,
	31950,
	31960,
	31970,
	31980,
	31990
);

var hair_list_female2 = Array(
	32000,
	32010,
	32020,
	32030,
	32040,
	33010,
	33020,
	33030,
	33050,
	33060,
	33070,
	33080,
	33090,
	33110,
	33120,
	33130,
	33140,
	33150,
	33160,
	33170,
	33240,
	33260,
	34000,
	34010,
	34020,
	34030,
	34040,
	34060,
	34070,
	34080,
	34090,
	34100,
	34110,
	34120,
	34130,
	34140,
	34150,
	34160,
	34170,
	34240,
	34250,
	34260
);

function ChangeHair_male() {
	var color = cm.getPlayerStat("HAIR") % 10;
	for (i = 0; i < hair_list_male.length; i++) {
		hair_list_male[i] -= hair_list_male[i] % 10;
		hair_list_male[i] += color;
	}
	cm.sendStyle("調髪（男）", hair_list_male);
}

function ChangeHair_female() {
	var color = cm.getPlayerStat("HAIR") % 10;
	for (i = 0; i < hair_list_female.length; i++) {
		hair_list_female[i] -= hair_list_female[i] % 10;
		hair_list_female[i] += color;
	}
	cm.sendStyle("調髪（女）", hair_list_female);
}

function ChangeHair_female2() {
	var color = cm.getPlayerStat("HAIR") % 10;
	for (i = 0; i < hair_list_female2.length; i++) {
		hair_list_female2[i] -= hair_list_female2[i] % 10;
		hair_list_female2[i] += color;
	}
	cm.sendStyle("調髪（女）", hair_list_female2);
}

// 染毛
var hair_color_list = new Array();
function HairDyeing() {
	var my_hair_color = Math.floor((cm.getPlayerStat("HAIR") / 10)) * 10;
	hair_color_list = new Array();

	for (var i = 0; i < 8; i++) {
		hair_color_list[i] = my_hair_color + i;
	}
	cm.sendStyle("染毛", hair_color_list);
}

// 目の色
var face_color_list = new Array();
function FaceColor() {
	var my_face_gender = (cm.getPlayerStat("FACE") >= 21000) ? true : false;
	var my_face = cm.getPlayerStat("FACE") % 100;

	my_face += 20000;
	// female
	if (my_face_gender) {
		my_face += 1000;
	}

	face_color_list = new Array();

	for (var i = 0; i < 9; i++) {
		face_color_list[i] = my_face + (i * 100);
	}
	cm.sendStyle("目の色", face_color_list);
}

// スキンケア
var skin_list = Array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
function SkinCare() {
	cm.sendStyle("スキンケア", skin_list);
}

var status = -1;
var old_selection = -1;
function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	switch (status) {
		case 0:
			{
				//cm.sendOk("test hair color" + (cm.getPlayerStat("HAIR") % 10));
				//break;
				var text = "デバッグキャラクタークリエイト\r\n";
				text += "パワーエリクサーが必要です\r\n";
				text += "目の色白 + 女の顔の一部, 男の髪型の一部 + 染毛でバグあり\r\n";
				text += "#L" + 1 + "##r整形（男）#k#l\r\n";
				text += "#L" + 2 + "##r調髪（男）#k#l\r\n";
				text += "#L" + 3 + "##r染毛#k#l\r\n";
				text += "#L" + 4 + "##rスキンケア#k#l\r\n";
				text += "#L" + 5 + "##r目の色#k#l\r\n";
				text += "#L" + 6 + "##r性転換#k#l\r\n";
				text += "#L" + 7 + "##r調髪（女）#k#l\r\n";
				text += "#L" + 8 + "##r調髪（女）#k#l\r\n";
				text += "#L" + 9 + "##r整形（女）#k#l\r\n";
				if (!cm.haveItem(2000005)) {
					cm.gainItem(2000005, 1);
				}
				return cm.sendSimple(text);
			}
		case 1:
			{
				old_selection = selection;
				switch (selection) {
					case 1:
						{
							return Surgery_male();
						}
					case 2:
						{
							return ChangeHair_male();
						}
					case 3:
						{
							return HairDyeing();
						}
					case 4:
						{
							return SkinCare();
						}
					case 5:
						{
							return FaceColor();
						}
					case 6:
						{
							cm.sendSimple("作成中....");
							break;
						}
					// 髪型が0x7Fでオーバーフローしてそれ以降の選択肢がキャンセル扱い
					case 7:
						{
							return ChangeHair_female();
						}
					case 8:
						{
							return ChangeHair_female2();
						}
					case 9:
						{
							return Surgery_female();
						}
					default:
						break;
				}
				break;
			}
		case 2: {
			switch (old_selection) {
				case 1:
					{
						cm.setAvatar(2000005, face_list_male[selection]);
						break;
					}
				case 2:
					{

						cm.setAvatar(2000005, hair_list_male[selection]);
						break;
					}
				case 3:
					{
						cm.setAvatar(2000005, hair_color_list[selection]);
						break;
					}
				case 4:
					{
						cm.setAvatar(2000005, skin_list[selection]);
						break;
					}
				case 5:
					{
						cm.setAvatar(2000005, face_color_list[selection]);
						break;
					}
				case 6:
					{
						// test
						break;
					}
				case 7:
					{
						cm.setAvatar(2000005, hair_list_female[selection]);
						break;
					}
				case 8:
					{
						cm.setAvatar(2000005, hair_list_female2[selection]);
						break;
					}
				case 9:
					{
						cm.setAvatar(2000005, face_list_female[selection]);
						break;
					}
				default:
					break;
			}
			break;
		}
		default:
			break;
	}

	cm.dispose();
}