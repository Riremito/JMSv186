// プパ
// ココナッツジュース販売

var npc_talk_status = -1;

function CustomShop() {
	var shop = cm.CreateCustomShop(cm.getNpc());
	cm.CustomShopAdd(shop, 2022258, 105);
	cm.OpenCustomShop(shop);
}

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				CustomShop();
			}
		default:
			break;
	}

	return cm.dispose();
}