# JMS エミュ鯖
## 現状
+ JMS147, 164, 186, 188, 194, 302, 308で動くように修正中
    + JMS147から164の間のバージョンはほぼ同じ処理で動くはず
    + JMS165から186の間のバージョンも同様
    + JMS187から194の間のバージョンも同様
+ クライアントの状態的な問題 (Virtualized)
    + Virtualizer的な問題を考えるとJMS308が一番高いバージョンとなる
    + GMS111/CMS104/TWMS148
        + JMS308より古いバージョンの選択肢
    + JMS308/EMS89
        + JMS308付近の選択肢
    + KMS18X
        + JMS308より新しいバージョンの選択肢
        + KMS190は良くなかったのでKMS183~KMS189のどれかが良い

## 開発環境構築
### Java
+ jre-8u111-windows-x64
+ jdk-8u111-nb-8_2-windows-x64
+ jce_policy-8
    + AESが動作しないので以下を参考にJDK/JREのファイルを上書きする
        + [参考](https://qiita.com/mizuki_takahashi/items/cc26a7fd51aa04396e92)
        + アカウント作成しないとダウンロード出来ないので注意
+ NetBeans IDE 8.2
+ jdk-17.0.11_windows-x64_bin.exe
    + 実行時に必要 (ビルド時は不要)
### DB
+ Wampserver2.4-x64.exe
    + MySQL 5.6.12 が入っています
    + phpMyAdmin 4.0.4 が入っています
### wz_xml
+ https://github.com/Riremito/wz_xml
+ 利用するバージョンのXMLを解凍する
    + `/JMSv186/wz_xml/xml_JMS_v186/`
        + このように配置する
### jms_scripts
+ https://github.com/Riremito/jms_scripts
    + 他のサーバー用のscriptも放り込めば大体動くはず
    + `/JMSv186/scripts/scripts_jms/`
        + このように配置する
### ビルド手順
+ このリポジトリをクローン (developブランチ推奨)
+ NetBeansを実行して以下の手順でビルド
    + ファイル -> 新規プロジェクト -> カテゴリ = Java, プロジェクト = 既存のソースを使用するJavaプロジェクト
    + プロジェクト名 = JMSv186 (なんでもOK)
    + ソース・パッケージフォルダ = src
    + 一旦プロジェクトが読み込まれるので続ける
    + 右クリックからプロパティ
    + ライブラリを選択 -> JAR/フォルダの追加 = libディレクトリに.jarファイルがあるので全て選択して追加
    + 追加したプロジェクトを右クリックしてビルド
### DB設定
+ Wampを実行
+ タスクアイコンクリックするとメニューが出るので以下をする
    + Start all Services
    + phpMyAdminを開く
+ ユーザー名 `root`, パスワード `空欄` で管理画面へ入る
+ `sql` フォルダ内のファイルを以下の手順でインポートする
+ DBを作成する
    + JMS v186 を利用したい場合は `jms_v186` という名前に変更する
        + JMS以外の場合は `kms_v95` のようにバージョンの前にKMSなどを付与する
     `v186_empty.sql` をインポートする
    + 対象のバージョンのDBへ `init_data_set.sql`をインポートする、しなくても良い

## サーバーの実行
### 実行方法
+ `run_JMS_v186.1.bat` 等実行したいバージョンの `bat` ファイルを実行する
### サーバーの仕様
+ ログインサーバー
    + 自動登録
    + ログインする時に `MapleID` と`MapleID_` は同じ扱いとなります
        + `MapleID` だと男キャラクターの作成が可能で、 `MapleID_` だと女キャラクターの作成が可能です
        + `GMMapleID` の場合はGM状態となります
    + 2次パスワード
        + 内部的にパスワードは `777777`で固定となっていますが、無効化しているので不要です

## クライアントの準備
### フルクライアント
+ Web Archiveなどから探す
### Local Host Client
+ 面倒くさい人用
    + https://forum.ragezone.com/threads/localhost-workshop.1202021/
    + https://forum.ragezone.com/threads/some-localhost-clients-jms-cms-twms.1225637/
+ Local Hostを自分で作成したい人用
    + https://github.com/Riremito/EmuClient
    + https://github.com/Riremito/LocalHost
        + IP変更可能可能なので、公開したい場合はこれを利用すると楽だと思います
    + https://github.com/Riremito/FixThemida
        + 古いクライアントをWindows 10で実行可能にするためのツール
        + JMS164前後のバージョンのクライアントはWindows 10に対応しておらずこれを利用しないと実行出来ないので注意
### バグ修正 & 起動高速化
+ https://github.com/Riremito/iGPUplz
    + JMS187以下で画面が壊れる場合は必須
    + JMS188以上で起動速度を高速化したい場合も必要

## Version
### Pre-BB & VS2006 (JMS186 or before)
|Date|Version|Updates|memo|
|---|---|---|---|
|2007/08|JMS131|||
|2008/06|JMS146|Before Pirate||
||GMS61||near JMS146|
|2008/07|JMS147|Pirate|client is extremely changed from JMS146|
|2008/07|TWMS77|Pirate & Magatia|near JMS147|
|2009/01|GMS65|Pirate & Magatia|near JMS147|
||BMS24|Final Version|near JMS147-JMS164|
||VMS35|Final Version|near JMS147-JMS164|
|2008/12|KMS65||near JMS164|
|2009/05|TWMS94||near JMS164|
|2009/06|GMS72||near JMS164|
|2009/07|JMS164|Before Knights of Cygnus||
|2009/07|JMS165|Knights of Cygnus||
|2009/08|EMS55|Knights of Cygnus|near JMS165|
||KMS71||near JMS165|
|2009/09|KMS84||near JMS180|
|2010/02|GMS83||near JMS180|
|2010/04|JMS180|||
|2010/02|KMS92|Dual Blade||
|2010/03|KMS95||near JMS185 or before|
|2010/09|JMS186|Before BIGBANG||
|2010/09|CMS85||near JMS186|
|2010/09|TWMS121||near JMS186|
|2010/10|GMS91||near JMS186|
|2010/11|THMS87||near JMS186|
|2011/01|MSEA100||near JMS186|
||EMS70||near JMS186|

### Pre-BB & VS2008 (not in JMS & KMS)
|Date|Versions|
|---|---|
||CMS86|
||TWMS122|
||THMS88|
||GMS92|
||MSEA101 to MSEA102|
||EMS71 to EMS72|

### Post-BB & VS2006 (JMS187, KMS112 or before)
|Date|Version|
|---|---|
||KMST330||
||KMS101 to KMS112|
|2010/11|JMS187|

### Post-BB & VS2008 (JMS188 to JMS194, KMS113 or later)
|Date|Version|Updates|memo|
|---|---|---|---|
|2010/10|KMS114||near JMS188|
|2010/12|JMS188|Mechanic||
|2010/12|TWMS125||near JMS188|
|2011/01|GMS95||near JMS188|
|2011/07|THMS96||near JMS194|
|2011/10|EMS76||near JMS188|
|2014/05|IMS1||near JMS188|
|2011/06|JMS194|Before Professions||
||KMS118|||

### Renaissance to Phantom (JMS200 to JMS214)
|Date|Version|Updates|memo|
|---|---|---|---|
|2011/07|JMST110|||
||JMS200|||
||KMS119|||
|2011/01|KMS121|||
|2011/02|KMS127|||
|2011/07|KMST391|||
||KMS138|||
||KMS148|||
|2011/12|KMS149|||
|2012/08|CMS104|||
|2012/08|TWMS148|||
|2012/05|KMS160|||

### Sengoku (JMS300) to Angelic Buster (JMS308)
|Date|Version|Updates|memo|
|---|---|---|---|
|2012/06|JMS302|Sengoku & Kanna||
|2012/08|KMS169|Angelic Buster||
|2013/01|JMS308|Angelic Buster||
|2013/03|EMS89|Angelic Buster||

### More (JMS309 or later)
|Date|Version|Updates|memo|
|---|---|---|---|
||KMS174|||
||KMS183|||
|2013/07|KMS197|||
|2013/08|KMS200|||
|2014/02|KMS211|||
|2014/06|JMS327|||
|2014/11|JMS334|||
|2016/10|JMS354|||