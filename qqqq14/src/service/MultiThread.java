package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.FileUtil;

public class MultiThread extends Thread {

	private static Logger lg = LoggerFactory.getLogger(MultiThread.class);

	private String dirName;
	private String indexUrl;

	public MultiThread(String dirName, String indexUrl) {
		super();
		this.dirName = dirName;
		this.indexUrl = indexUrl;
	}

	@Override
	public void run() {
		FileUtil.mkdir(dirName);
		lg.info("{} start to grab resources. indexUrl:{}", this.getName(), indexUrl);
		UrlAnalyzer.startGrab(indexUrl, dirName);
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public String getIndexUrl() {
		return indexUrl;
	}

	public void setIndexUrl(String indexUrl) {
		this.indexUrl = indexUrl;
	}

}
