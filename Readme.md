# JMS エミュ鯖
## 現状
+ JMS v131
    + 一番古いバージョン
    + そこそこ動く
+ JMS v164
    + 海賊までのバージョン
    + まぁまぁ動く
+ JMS v165
    + シグナス騎士団実装時のバージョン
    + あまり動かない
+ JMS v180
    + 海外版モンスターレイドバージョン
    + ほぼ動かない
+ JMS v186
    + BIGBANG前最終バージョン
    + とりあえずある程度動作するので現在はコードを修正中
+ JMS v187
    + BIGBANGバージョン (壊れています)
    + まぁまぁ動く
+ JMS v188
    + このバージョンからVS2008に移行
    + そこそこ動く
+ JMS v194
    + JMS v186の次くらいに良く動くはず
    + とりあえずある程度動作するので現在はコードを修正中
+ JMS v201
    + ルネサンスバージョン (Chaos Update)
    + ほぼ動かない
+ JMS v302
    + 戦国時代バージョン (Sengoku Update)
    + ほぼ動かない
+ KMS v65
    + JMS v164とほぼ同等のバージョン
    + 開発予定
+ KMS v95 (本当はv100が良いがクライアントが存在しない)
    + JMS v186より少し古いバージョン
    + まぁまぁ動く
+ KMS v114
    + JMS v194とほぼ同等のバージョン
    + ほぼ動かない
+ CMS v85 (本当はv86が良いがクライアントが解析に適していない)
    + JMS v186と同等のバージョン
    + ほぼ動かない
+ TWMS v122
    + JMS v186とほぼ同等のバージョン、VS2008に移行済み
    + ほぼ動かない
+ MSEA v102
    + JMS v186とほぼ同等のバージョン、VS2008に移行済み
    + 開発予定
+ EMS v70
    + JMS v186と同等のバージョン (本当はv72が良いがクライアントが存在しない)
    + 開発予定
+ GMS v92
    + JMS v186とほぼ同等のバージョン
    + 開発予定
+ GMS v95
    + JMS v194とほぼ同等のバージョン
    + 開発予定

## 開発環境構築
### Java
+ jre-8u111-windows-x64
+ jdk-8u111-nb-8_2-windows-x64
+ jce_policy-8
    + AESが動作しないので以下を参考にJDK/JREのファイルを上書きする
        + [参考](https://qiita.com/mizuki_takahashi/items/cc26a7fd51aa04396e92)
        + アカウント作成しないとダウンロード出来ないので注意
+ NetBeans IDE 8.2
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
    + `/JMSv186/jms_scripts/`
        + このように配置する
### ビルド手順
+ このリポジトリをクローン (developブランチ推奨)
+ NetBeansを実行して以下の手順でビルド
    + ファイル -> 新規プロジェクト -> カテゴリ = Java, プロジェクト = 既存のソースを使用するJavaプロジェクト
    + プロジェクト名 = v186 (なんでもOK)
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
    + JMS v186 を利用したい場合は `v186` という名前に変更する
        + 例
            + JMS v194の場合は `v194` という名前に変更する
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
+ https://archive.org/download/mp_setups/JMS/
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
        + v164前後のバージョンのクライアントはWindows 10に対応しておらずこれを利用しないと実行出来ないので注意
+ JMS v186 専用 バグ修正済みファイル
    + https://hostr.co/KlwxasOTT6k9
        + 画面崩壊し辛くなります