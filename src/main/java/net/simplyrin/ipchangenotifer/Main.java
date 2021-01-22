package net.simplyrin.ipchangenotifer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.md_5.bungee.config.Configuration;
import net.simplyrin.config.Config;
import net.simplyrin.httpclient.HttpClient;
import net.simplyrin.ipchangenotifer.Key.Cloudflare.API;
import net.simplyrin.ipchangenotifer.Key.Cloudflare.Data;
import net.simplyrin.ipchangenotifer.Key.Message;
import net.simplyrin.ipchangenotifer.Key.Message.Discord;
import net.simplyrin.ipchangenotifer.Key.RequestURL;
import net.simplyrin.rinstream.RinStream;

/**
 * Created by SimplyRin on 2021/01/21.
 *
 * Copyright (c) 2021 SimplyRin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class Main {

	public static void main(String[] args) {
		RinStream rs = new RinStream();
		rs.enableError();

		new Main().run();
	}

	private Configuration config;
	private String currentIp;

	public void run() {
		File file = new File("config.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Configuration config = new Configuration();
			config.set(RequestURL.IP_Check, "https://api.v2.simplyrin.net/checkMyIp.php");
			config.set(RequestURL.Discord, "https://discordapp.com/api/webhooks/000000000000000000/XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

			config.set(Message.System.IP_Changed, "IP が変更されました。");
			config.set(Message.System.Discord, "Discord WebHooks にメッセージを送信しました！");

			config.set(Discord.IP_Changed, "IP アドレスが変更されました。");
			config.set(Discord.NewIP, "新しい IP アドレス:");

			config.set(API.Zones, "ZONES");
			// config.set(API.DNS_Records, "DNSRECORDS");
			config.set(API.Authorization, "Bearer Authorization");

			config.set(Data.Type, "A");
			config.set(Data.Name, "www.example.com");
			// config.set(Data.TTL, 1);
			// config.set(Data.Proxied, true);

			Config.saveConfig(config, file);

			System.out.println("config.yml ファイルを作成しました。ファイルを開いて設定を変更してください。");
			System.exit(0);
		}
		this.config = Config.getConfig(file);

		String zones = this.config.getString(API.Zones);
		String authorization = this.config.getString(API.Authorization);

		if (!authorization.equalsIgnoreCase("Bearer Authorization")
				&& !this.config.getString(Data.Name).equalsIgnoreCase("www.example.com") && !zones.equals("ZONES")) {
			System.out.println("DNS レコード ID を探しています...");

			// curl -X GET "https://api.cloudflare.com/client/v4/zones/4d8b56689b468f3373dec8801b7a1427/dns_records?type=A&name=test.moqs.net&proxied=true&page=1&per_page=20&order=type&direction=desc&match=all"
			try {
				String type = this.config.getString(Data.Type);
				String name = this.config.getString(Data.Name);

				String url = "https://api.cloudflare.com/client/v4/zones/" + zones + "/dns_records?type=" + type
						+ "&name=" + name + "&page=1&per_page=20&order=type&direction=desc&match=all";
				HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();

				connection.setRequestMethod("GET");
				connection.addRequestProperty("Authorization", this.config.getString(API.Authorization, null));
				connection.addRequestProperty("Content-Type", "application/json");
				connection.connect();

				String result = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
				JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();

				JsonArray jsonArray = jsonObject.get("result").getAsJsonArray();
				if (jsonArray.size() > 0) {
					JsonObject item = jsonArray.get(0).getAsJsonObject();

					String itemName = item.get("name").getAsString();
					if (itemName.equalsIgnoreCase(name)) {
						this.config.set(API.DNS_Records, item.get("id").getAsString());
						this.config.set(Data.TTL, item.get("ttl").getAsInt());
						this.config.set(Data.Proxied, item.get("proxied").getAsBoolean());

						Config.saveConfig(config, file);

						System.out.println("DNS レコード ID が見つかりました。");
					}
				} else {
					System.err.println("DNS レコード ID が見つかりませんでした。config.yml ファイルを確認してください。");
					System.exit(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		while (true) {
			try {
				String ip = HttpClient.fetch(this.config.getString(RequestURL.IP_Check));

				if (this.currentIp != null && !this.currentIp.equals(ip) && ip != null) {
					System.out.println(this.config.getString(Message.System.IP_Changed));
					this.currentIp = ip;
					this.sendDiscordMessage();
					this.sendCloudflare();
				}

				TimeUnit.MINUTES.sleep(1);
			} catch (Exception e) {
			}
		}
	}

	public void sendDiscordMessage() {
		HttpClient httpClient = new HttpClient(this.config.getString(RequestURL.Discord));

		String ipChanged = this.config.getString(Discord.IP_Changed);
		String newIp = this.config.getString(Discord.NewIP);

		httpClient.setData("{\"tts\":false,\"embeds\":[{\"color\":65280,\"description\":\""
				+ ipChanged + "\",\"fields\":[{\"inline\":true,\"name\":\""
				+ newIp + "\",\"value\":\""
				+ this.currentIp + "\"}]}]}");
		httpClient.connect();

		System.out.println(this.config.getString(Message.System.Discord));
	}

	public void sendCloudflare() {
		try {
			String zones = this.config.getString(API.Zones);
			String records = this.config.getString(API.DNS_Records);

			HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.cloudflare.com/client/v4/zones/"
					+ zones + "/dns_records/" + records).openConnection();

			connection.setRequestMethod("PUT");
			connection.addRequestProperty("Authorization", this.config.getString(API.Authorization, null));
			connection.addRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", this.config.getString(Data.Type));
			jsonObject.addProperty("name", this.config.getString(Data.Name));
			jsonObject.addProperty("content", this.currentIp);
			jsonObject.addProperty("ttl", this.config.getInt(Data.TTL));
			jsonObject.addProperty("proxied", this.config.getBoolean(Data.Proxied));

			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
			outputStreamWriter.write(jsonObject.toString());
			outputStreamWriter.close();

			connection.connect();

			String result = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
