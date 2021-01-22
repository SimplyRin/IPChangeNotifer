package net.simplyrin.ipchangenotifer;

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
public class Key {

	protected static class RequestURL {
		private static String BASE = "URL.";

		protected static String IP_Check = BASE + "IP-Check";
		protected static String Discord = BASE + "Discord";
	}

	protected static class Message {
		private static String BASE = "Message.";

		protected static class System {
			private static String BASE = Message.BASE + "System.";

			protected static String IP_Changed = BASE + "IP-Changed";
			protected static String Discord = BASE + "Discord";
		}

		protected static class Discord {
			private static String BASE = Message.BASE + "Discord.";

			protected static String IP_Changed = BASE + "IP-Changed";
			protected static String NewIP = BASE + "New-IP";
		}
	}

	protected static class Cloudflare {
		private static String BASE = "Cloudflare.";

		protected static class API {
			private static String BASE = Cloudflare.BASE + "API.";

			protected static String Zones = BASE + "Zones";
			protected static String DNS_Records = BASE + "DNS-Records";
			protected static String Authorization = BASE + "Authorization";
		}

		protected static class Data {
			private static String BASE = Cloudflare.BASE + "Data.";

			protected static String Type = BASE + "Type";
			protected static String Name = BASE + "Name";
			protected static String Content = BASE + "Content";
			protected static String TTL = BASE + "TTL";
			protected static String Proxied = BASE + "Proxied";

		}
	}

}
