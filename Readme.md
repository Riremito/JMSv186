# JMS エミュ鯖
## 概要
### 動作確認済み
|バージョン|時期|動作状況|
|---|---|---|
|JMS v131|属性杖|〇|
|JMS v164|シグナス騎士団実装直前|◎|
|JMS v165|シグナス騎士団|△|
|JMS v180|モンスターレイド2|〇|
|JMS v186|BB直前, 潜在能力|◎|
|JMS v187|BB|×|
|JMS v188|BB, メカニック|◎|
|JMS v194|ルネサンス直前|◎|
|JMS v201|ルネサンス||
|JMS v302|戦国, カンナ|△|
|KMS v2.65|シグナス騎士団実装直前|△|
|KMS v2.71|JMS 164|△|
|KMS v2.84|JMS 180, GMS 83|△|
|KMS v2.92|JMS 183||
|KMS v2.95|JMS 184|〇|
|KMS v2.114|BB, メカニック有|〇|
|TWMS v94|シグナス騎士団実装直前|△|
|TWMS v122|BB直前|×|
|TWMS v124|BB|〇|
|TWMS v125|BB, 内部的にメカニック有|〇|
|CMS v85|BB直前|△|
|CMS v88|BB, メカニック||
|EMS v76|BB, メカニック有|〇|
|BMS v24|最終バージョン|×|
|MSEA v102|BB直前||
|MSEA v105|BB||
|THMS v87|BB直前|×|
|GMS v83|JMS 180||
|GMS v84|JMS 183||
|GMS v95|BB, メカニック||

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