# IPChangeNotifer
動かしている環境の IP アドレスが変更されたら Discord に通知するもの (Cloudflare レコード更新機能つき)

Discord は WebHooks URL を `URL.Discord` に設定してください。

# ダウンロード
・[Releases](https://github.com/SimplyRin/IPChangeNotifer/releases/latest)

・自分でビルドしたければ `build.bat` か `mvn clean package` でビルドできます

# 設定ファイル
Cloudflare レコード更新機能を使うには `Cloudflare.API.Zones` と `Cloudflare.API.Authorization` の変更が必要です。

Authorization キーの取得方法については下の方に適当に書いておきました。

```Yaml
URL:
  IP-Check: https://api.v2.simplyrin.net/checkMyIp.php
  Discord: https://discordapp.com/api/webhooks/000000000000000000/XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
Message:
  System:
    IP-Changed: IP が変更されました。
    Discord: Discord WebHooks にメッセージを送信しました！
  Discord:
    IP-Changed: IP アドレスが変更されました。
    New-IP: '新しい IP アドレス:'
Cloudflare:
  API:
    Zones: ZONES
    Authorization: Bearer Authorization
  Data:
    Type: A
    Name: www.example.com
```

# Cloudflare 系の設定方法
・ [cloudflare.com/profile/api-tokens](https://dash.cloudflare.com/profile/api-tokens) に移動

・`トークンを作成する` をクリック

・`ゾーン DNS を編集する` の右側にある `テンプレートを使用する` をクリック

・`ゾーン リソース` のところで変更したいドメインを選択

・`概要に進む` をクリック

・`この API トークンは、それぞれの権限とともに、以下のアカウントとゾーンに影響します` というのが表示されるので間違ってないか確認する

・`トークンを作成する` をクリック

・上の方に出てきたトークンを以下のような形で config.yml ファイルに入力する

![o.png](https://raw.githubusercontent.com/SimplyRin/IPChangeNotifer/master/images/o.png)

```Yaml
Cloudflare:
  API:
    Authorization: Bearer 1EMhSr...........
```

・おわり
