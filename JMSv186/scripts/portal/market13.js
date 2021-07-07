// 退場位置おかしい マガティア
function enter(pi) {
	pi.playPortalSE();
	pi.saveLocation("FREE_MARKET");
	pi.warp(910000000, "out00");
}