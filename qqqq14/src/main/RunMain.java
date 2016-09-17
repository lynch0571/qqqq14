package main;

import java.util.List;

import pojo.NameUrl;
import service.MultiThread;
import service.UrlAnalyzer;
import utils.FileUtil;
import utils.PropertiesUtil;

public class RunMain {
//	private static Logger lg = LoggerFactory.getLogger(RunMain.class);
	private static String filePath = PropertiesUtil.getValueByKey("config/spider.properties", "filePath");

	public static void main(String[] args) {
		FileUtil.mkdir(filePath);

		List<NameUrl> list = UrlAnalyzer.getCategory();

		for (int i = 0; i < list.size(); i++) {
			String name = list.get(i).getName();
			String dirName = filePath + "/" + name;
			String indexUrl = list.get(i).getUrl();

			MultiThread t = new MultiThread(dirName, indexUrl);
			t.setName("Thread-"+name);
			t.start();
		}
	}

}
