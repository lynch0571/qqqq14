package service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import main.RunMain;
import pojo.NameUrl;
import utils.PropertiesUtil;

public class UrlAnalyzer {

	private final static Logger lg = LoggerFactory.getLogger(RunMain.class);
	public final static String baseUrl = PropertiesUtil.getValueByKey("config/spider.properties", "website");

	public static List<NameUrl> getCategory() {
		List<NameUrl> list = new ArrayList<>();
		Document doc = null;
		try {
			doc = Jsoup.connect(baseUrl).get();
		} catch (IOException e) {
			lg.error("Failed to get doc.Exception:{}, StackTrace:{}", e.toString(), e.getStackTrace());
		}
		if (doc == null) {
			lg.warn("Get document null from {}", baseUrl);
		} else {
			Elements es = doc.select("#header .menu ul");
			for (int i = es.size() - 1; i > 0; --i) {
				es.remove(i);
			}
			es = es.select("li a");
			es.remove(0);
			for (Element e : es) {
				NameUrl cu = new NameUrl();
				cu.setName(e.text());
				cu.setUrl(baseUrl + e.attr("href"));
				list.add(cu);
			}
			lg.info("Obtain category success. size:{}, list:{}", list.size(), list);
		}
		return list;
	}

	public static String[] getAllImageUrl(String urlStr) {
		String content = HtmlParser.getURLSource(urlStr);
		Document doc = Jsoup.parse(content);

		Elements elements = doc.select("img[src~=(?i).(png|jpe?g)]");

		String[] strs = elements.outerHtml().split("<img src=\"");
		for (int i = 0; i < strs.length; i++) {
			strs[i] = strs[i].replaceAll("\" border=\"0\">", "");
		}
		return strs;
	}

	public static NameUrl getFirstUrl(String urlStr) {
		NameUrl nu = new NameUrl();
		Document doc = null;
		try {
			doc = Jsoup.connect(urlStr).get();
		} catch (IOException e) {
			lg.error("Failed to get doc.Exception:{}, StackTrace:{}", e.toString(), e.getStackTrace());
		}
		if (doc == null) {
			lg.warn("Get document null from {}", baseUrl);
		} else {
			Elements es = doc.select(".typelist a");
			nu.setName(es.get(0).text());
			nu.setUrl(baseUrl + es.get(0).attr("href"));
			if (StringUtil.isBlank(nu.getName()) || StringUtil.isBlank(nu.getUrl())) {
				lg.info("Obtain first url failure. NameUrl:{}", nu);
			} else {
				lg.info("Obtain first url success. NameUrl:{}", nu);
			}
		}
		return nu;
	}

	public static String getNextUrl(String urlStr) {
		String content = HtmlParser.getURLSource(urlStr);
		Document doc = Jsoup.parse(content);
		String partUrl = doc.select(".next a").attr("href");
		String url = baseUrl + partUrl;

		if (StringUtil.isBlank(url)) {
			lg.info("Obtain next url failure. Url:{}", url);
			return "";
		} else {
			lg.info("Obtain next url success. Url:{}", url);
			return url;
		}
	}

	public static String getPreviousUrl(String urlStr) {
		String content = HtmlParser.getURLSource(urlStr);
		Document doc = Jsoup.parse(content);

		String elements = doc.select(".last a").attr("href");

		String str = baseUrl + elements;
		System.out.println(str);
		return str;
	}

	public static void startGrab(String indexUrl, String dirName) {
		NameUrl nu = UrlAnalyzer.getFirstUrl(indexUrl);
		if (nu == null) {
			lg.warn("FirstUrl is null");
			return;
		}
		int num = 0;
		Document doc = null;
		String urlStr = nu.getUrl();
		while (!StringUtil.isBlank(urlStr) && UrlAnalyzer.isUrl(urlStr)) {
			Long begin = System.currentTimeMillis();
			try {
				doc = Jsoup.connect(urlStr).get();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			if (doc != null) {
				String name = doc.select("h3 font").html();
				String fileName = dirName + "/" + String.format("%04d", num) + "_" + name;
				Map<String, Object> map = new HashMap<String, Object>();

				map.put("date", new Date());
				map.put("num", String.format("%04d", num));
				map.put("url", urlStr);
				map.put("file", fileName);
				File file = new File(fileName);
				file.mkdir();
				String[] strs = UrlAnalyzer.getAllImageUrl(urlStr);
				map.put("imgCount", strs.length);
				map.put("imgUrls", strs);
				if (strs.length > 0) {
					for (int i = 0; i < strs.length; i++) {
						if (StringUtil.isBlank(strs[i])) {
							continue;
						}
						Long start = System.currentTimeMillis();
						ImageDownloader.downloadImage(fileName + "/" + name + i + ".jpg", strs[i]);
						Long time = System.currentTimeMillis() - start;
						lg.info("{}ms to download the image：{}/{}{}.jpg", time, fileName, name,
								String.format("%03d", i));
					}
				}
				Long end = System.currentTimeMillis();
				map.put("time", (end - begin) / 1000.0 + "s");
				Gson g = new Gson();
				String jsonStr = g.toJson(map);
				lg.info("Grab info:{}", jsonStr);
				num++;
				urlStr = UrlAnalyzer.getNextUrl(urlStr);
			}
		}
	}

	public static boolean isUrl(String urlStr) {
		URL url;
		try {
			url = new URL(urlStr);
			InputStream in = url.openStream();
			in.close();
		} catch (Exception e1) {
			System.out.println("url非法：" + urlStr);
			url = null;
			return false;
		}
		return true;
	}

}
